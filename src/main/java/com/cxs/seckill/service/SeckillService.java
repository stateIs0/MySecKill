package com.cxs.seckill.service;

import com.cxs.seckill.dto.Exposer;
import com.cxs.seckill.dto.SeckillExecution;
import com.cxs.seckill.entity.Seckill;
import com.cxs.seckill.exception.RepeatKillException;
import com.cxs.seckill.exception.SeckilException;
import com.cxs.seckill.exception.SeckillCloseException;
import java.util.List;

/**
 * 业务接口：站在“使用者”角度设计接口
 * 3个方面：方法定义粒度，参数，返回类型（return 类型）
 *
 * Created by cxsta on 2017/7/9.
 */
public interface SeckillService {

  /**
   * 查询所有秒杀记录
   */
  List<Seckill> getSeckillList();

  /**
   * 查询单个秒杀记录
   */
  Seckill getById(long seckillId);

  /**
   * 秒杀开启时输出秒杀接口秒杀接口地址
   * 否则输出系统时间和秒杀时间
   */
  Exposer exportSeckillUrl(long seckillId);

  /**
   * 执行秒杀操作
   */
  SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
      throws SeckilException, RepeatKillException, SeckillCloseException;


  /**
   * 执行秒杀操作 存储过程
   */
  SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5);
}
