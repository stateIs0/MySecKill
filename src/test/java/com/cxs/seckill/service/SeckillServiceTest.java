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
    long id = 1000;
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


    /*
    Logging initialized using 'class org.apache.ibatis.logging.stdout.StdOutImpl' adapter.
    Creating a new SqlSession
    Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2415fc55]
    JDBC Connection [com.mysql.jdbc.JDBC4Connection@48b67364] will be managed by Spring
    ==>  Preparing: UPDATE seckill SET number = number - 1 WHERE seckill_id = ? AND start_time <= ? AND end_time >= ? AND number > 0
    ==> Parameters: 1000(Long), 2017-07-09 16:38:31.158(Timestamp), 2017-07-09 16:38:31.158(Timestamp)
    <==    Updates: 1
    Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2415fc55]
    Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2415fc55] from current transaction
    ==>  Preparing: INSERT ignore INTO success_killed(seckill_id, user_phone) VALUES (?, ?)
    ==> Parameters: 1000(Long), 12222222122(Long)
    <==    Updates: 1
    Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2415fc55]
    Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2415fc55] from current transaction
    ==>  Preparing: select sk.seckill_id, sk.user_phone, sk.state, sk.create_time, s.seckill_id "seckil.seckill_id", s.name "seckill.name", s.number "seckill.number", s.start_time "seckill.startTime", s.end_time "seckill.end_time", s.create_time "seckill.create_time" from success_killed sk join seckill s on sk.seckill_id = s.seckill_id where sk.seckill_id = ? AND sk.user_phone = ?
    ==> Parameters: 1000(Long), 12222222122(Long)
    <==    Columns: seckill_id, user_phone, state, create_time, seckil.seckill_id, seckill.name, seckill.number, seckill.startTime, seckill.end_time, seckill.create_time
    <==        Row: 1000, 12222222122, -1, 2017-07-09 16:38:31.0, 1000, 1000元秒杀iphone6, 95, 2017-07-09 16:38:31.0, 2018-01-02 00:00:00.0, 2017-07-07 20:44:16.0
    <==      Total: 1
    Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2415fc55]
    Transaction synchronization committing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2415fc55]
    Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2415fc55]
    Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2415fc55]
     */
  }

}
