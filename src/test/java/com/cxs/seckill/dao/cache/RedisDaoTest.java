package com.cxs.seckill.dao.cache;

import com.cxs.seckill.dao.SeckillDao;
import com.cxs.seckill.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by cxs on 2017/7/10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
// 告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest{

  private long id = 1001;
  @Autowired
  private SeckillDao seckillDao;

  @Autowired
  private RedisDao redisDao;

  @Test
  public void testSeckill() throws Exception {
    Seckill seckill = redisDao.getSeckill(id);
    if (seckill == null){
      seckill = seckillDao.queryById(id);
      if (seckill != null){
        String result = redisDao.putSeckill(seckill);
        System.out.println(result);
        seckill = redisDao.getSeckill(id);
        System.out.println(seckill);
      }

    }

  }

  @Test
  public void putSeckill() throws Exception {

  }

}