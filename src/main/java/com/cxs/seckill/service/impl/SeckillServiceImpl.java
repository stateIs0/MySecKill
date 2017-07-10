package com.cxs.seckill.service.impl;

import static org.apache.commons.collections.MapUtils.getInteger;

import com.cxs.seckill.dao.SeckillDao;
import com.cxs.seckill.dao.SuccessKilledDao;
import com.cxs.seckill.dao.cache.RedisDao;
import com.cxs.seckill.dto.Exposer;
import com.cxs.seckill.dto.SeckillExecution;
import com.cxs.seckill.entity.Seckill;
import com.cxs.seckill.entity.SuccessKilled;
import com.cxs.seckill.enums.SeckillStatEnum;
import com.cxs.seckill.exception.RepeatKillException;
import com.cxs.seckill.exception.SeckilException;
import com.cxs.seckill.exception.SeckillCloseException;
import com.cxs.seckill.service.SeckillService;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.impl.WeakHashtable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

/**
 * Created by cxsta on 2017/7/9.
 */
@Service
public class SeckillServiceImpl implements SeckillService {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private SeckillDao seckillDao;

  @Autowired
  private SuccessKilledDao successKilledDao;

  @Autowired
  private RedisDao redisDao;

  // md5盐值字符串
  private final String slat = "yuefazayueha";

  public List<Seckill> getSeckillList() {
    return seckillDao.queryAll(0, 4);
  }

  public Seckill getById(long seckillId) {
    return seckillDao.queryById(seckillId);
  }

  public Exposer exportSeckillUrl(long seckillId) {
    // 使用redis缓存，降低数据库压力： 超时的基础上维护一致性，一个秒杀对象是不会改的。
    // 1:访问redis
    Seckill seckill = redisDao.getSeckill(seckillId);
    if (seckill == null){
      // 访问数据库
      seckill = seckillDao.queryById(seckillId);
      if (seckill != null) {
        // 放入redis
        redisDao.putSeckill(seckill);
      }
      return new Exposer(false, seckillId);
    }
    Date startTime = seckill.getStartTime();
    Date endTime = seckill.getEndTime();
    Date nowTime = new Date();
    if (nowTime.getTime() < startTime.getTime()
        || nowTime.getTime() > endTime.getTime()) {
      return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(),
          endTime.getTime());
    }
    //转换特定字符串的过程，最大特点不可逆
    String md5 = getMd5(seckillId);
    return new Exposer(true, md5, seckillId);
  }

  private String getMd5(long seckillId) {
    String base = seckillId + "/" + slat;
    String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
    return md5;
  }
  /**
   * 使用注解控制事务方法的优点：
   * 1，开发团队达成一致约定，明确标注事务方法的编程风格。
   * 2,保证事务方法的执行时间尽量短，不要穿插其他网络操作（RPC/HTTP 请求），或者剥离到事务方法外部。
   * 3，不是所有的方法都需要事务，如只有一条修改操作，只读操作不需要事务控制
   */
  @Transactional
  public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
      throws SeckilException, RepeatKillException, SeckillCloseException {
    if (md5 == null || !md5.equals(getMd5(seckillId))) {
      throw new SeckilException("seckill data rewrite");
    }
    // 执行秒杀逻辑：减库存 + 记录购买行为
    Date nowTime = new Date();
    try {
      // 记录购买行为 如果主键冲突不会报错
      int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
      if (insertCount <= 0) {
        // 重复秒杀
        throw new RepeatKillException("seckill repeated");
      } else {
        // 减库存 ，热点商品竞争，
        int udpateCount = seckillDao.reduceNumber(seckillId, nowTime);
        if (udpateCount <= 0) {
          // 没有更新操作 秒杀结束 rollback
          throw new SeckillCloseException("seckill is closed");
        } else {
          // 秒杀成功 commit
          SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
          return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
        }
      }
    } catch (SeckillCloseException e) {
      throw e;
    } catch (RepeatKillException e) {
      throw e;
    } catch (Exception exp) {
      logger.error(exp.getMessage(), exp);
      // 所有的编译器异常转换为运行期异常
      throw new SeckilException("seckill inner error:" + exp.getMessage());
    }
  }

  /**
   * 调用存储过程
   * @param seckillId
   * @param userPhone
   * @param md5
   * @return
   * @throws SeckilException
   * @throws RepeatKillException
   * @throws SeckillCloseException
   */
  public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
    if (md5 == null || !md5.equals(getMd5(seckillId))){
      return new SeckillExecution(seckillId,SeckillStatEnum.DATA_REWRITE);
    }
    Date killTime = new Date();
    Map<String,Object> map = new HashMap<String, Object>();
    map.put("seckillId",seckillId);
    map.put("phone",userPhone);
    map.put("killTime",killTime);
    map.put("result",null);
    try {
      // 存储过程执行后会给result赋值
      seckillDao.killByProcedure(map);
      int result = MapUtils.getInteger(map,"result",-2);
      if (result == 1){
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
        return new SeckillExecution(seckillId,SeckillStatEnum.SUCCESS, successKilled);
      } else {
        return new SeckillExecution(seckillId,SeckillStatEnum.stateOf(result));
      }
    }catch (Exception e){
      logger.error(e.getMessage(),e);
      return new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR);
    }

  }
}
