package com.cxs.seckill.service;

import static org.junit.Assert.*;

import com.cxs.seckill.dto.Exposer;
import com.cxs.seckill.dto.SeckillExecution;
import com.cxs.seckill.entity.Seckill;
import com.cxs.seckill.exception.RepeatKillException;
import com.cxs.seckill.exception.SeckilException;
import com.cxs.seckill.exception.SeckillCloseException;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by cxsta on 2017/7/9.
 */
@RunWith(SpringJUnit4ClassRunner.class)
// 告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-*.xml"})
public class SeckillServiceTest {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private SeckillService seckillService;

  @Test
  public void getSeckillListTest() throws Exception {
    //Closing non transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@7de62196]
    List<Seckill> list = seckillService.getSeckillList();
    logger.info("list={}", list);
  }

  @Test
  public void getByIdTest() throws Exception {
    Seckill seckill = seckillService.getById(1000L);
    logger.info("seckill={}", seckill);
  }

  /*
    测试代码完整逻辑，注意可重复执行
   */
  @Test
  public void exportSeckillUrlTest() throws Exception {
    long id = 1001;
    Exposer exposer = seckillService.exportSeckillUrl(id);
      logger.info("exposer:{}", exposer);
    if (exposer.isExposed()) {
      long phone = 12222222122L;
      String md5 = "c89565d7b0990dc45a889c9c632210c3";
      SeckillExecution seckillExecution = null;
      try {
        seckillExecution = seckillService.executeSeckill(id, phone, md5);
      } catch (RepeatKillException exp) {
        logger.info("seckillExecution:={}", seckillExecution);
      } catch (SeckillCloseException exp) {
        logger.info("seckillExecution:={}", seckillExecution);
      }
    } else {
      logger.error("exposer:{}", exposer);
    }
    /*
    Exposer{exposed=true,
    md5='c89565d7b0990dc45a889c9c632210c3',
    seckillId=1000,
     now=0,
     start=0
     , end=0}
     */
  }

  @Test
  public void executeSeckillTest() throws Exception {
//    long id = 1000;
//    long phone = 12222222122L;
//    String md5 = "c89565d7b0990dc45a889c9c632210c3";
//    SeckillExecution seckillExecution = null;
//    try {
//      seckillExecution = seckillService.executeSeckill(id, phone, md5);
//    } catch (RepeatKillException exp) {
//      logger.info("seckillExecution:={}", seckillExecution);
//    } catch (SeckillCloseException exp) {
//      logger.info("seckillExecution:={}", seckillExecution);
//    }


  }


  @Test
  public void executeSeckillProcedure() throws Exception {
    long seckilId = 1001;
    long phone = 12221222422L;
    Exposer exposer = seckillService.exportSeckillUrl(seckilId);
    if (exposer.isExposed()){
      String md5 =exposer.getMd5();
      SeckillExecution seckillExecution = seckillService.executeSeckillProcedure(seckilId, phone, md5);
      logger.info(seckillExecution.getStateInfo());
    }

  }

}
