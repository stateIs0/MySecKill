package com.cxs.seckill.dao;

import com.cxs.seckill.entity.SuccessKilled;
import org.apache.ibatis.annotations.Param;

/**
 * Created by cxsta on 2017/7/9.
 */
public interface SuccessKilledDao {

  /**
   * 插入购买明细,可过滤重复
   *
   * @return 表示插入的行数
   */
  int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

  /**
   * 根据id程序SuccessKilled并携带秒杀产品对象实体
   */
  SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId,
      @Param("userPhone") long userPhone);
}
