package com.cxs.seckill.dao;

import com.cxs.seckill.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by cxsta on 2017/7/9.
 */
@RunWith(SpringJUnit4ClassRunner.class)
// 告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

  // 注入dao实现依赖
  @Resource
  private SeckillDao seckillDao;

  @Test
  public void reduceNumberTest() throws Exception {
    Date killTime = new Date();
    int udpateCount = seckillDao.reduceNumber(1000L, killTime);
    System.out.println(udpateCount);
  }

  @Test
  public void queryByIdTest() throws Exception {
    long id = 1000;
    Seckill seckill = seckillDao.queryById(id);
    System.out.printf(seckill.getName());
    System.out.printf(seckill.toString());
  }

  @Test
  public void queryAllTest() throws Exception {
    List<Seckill> list = seckillDao.queryAll(0, 100);
    for (Seckill seckill : list) {
      System.out.printf(seckill.toString());
    }
  }

}

