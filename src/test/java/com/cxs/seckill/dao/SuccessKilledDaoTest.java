package com.cxs.seckill.dao;

import com.cxs.seckill.entity.SuccessKilled;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by cxsta on 2017/7/9.
 */
@RunWith(SpringJUnit4ClassRunner.class)
// 告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

  @Resource
  private SuccessKilledDao successKilledDao;

  @Test
  public void insertSuccessKilledTest() throws Exception {
    long id = 1000L;
    long phone = 11111111111L;
    int result = successKilledDao.insertSuccessKilled(id, phone);
    System.out.println(result);
  }

  /**
   SuccessKilled{
   seckillId=1000,
   userPhone='11111111111',
   state=-1,
   createTime=Sun Jul 09 14:20:50 CST 2017,
   seckill=Seckill{
   seckillId=0, number=100,
   name='1000元秒杀iphone6',
   startTIme=Fri Jan 01 00:00:00 CST 2016,
   endTime=Sat Jan 02 00:00:00 CST 2016,
   createTime=Fri Jul 07 20:44:16 CST 2017}}
   * @throws Exception
   */

  @Test
  public void queryByIdWithSeckillTest() throws Exception {
    long id = 1000L;
    long phone = 11111111111L;
    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id, phone);
    System.out.println(successKilled);
  }

}
