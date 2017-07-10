package com.cxs.seckill.dao;

import com.cxs.seckill.entity.Seckill;
import java.util.Map;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by cxsta on 2017/7/9.
 */
public interface SeckillDao {

  /**
   * 减库存
   *
   * @param seckillId the seckill id
   * @param killTime the kill time
   * @return 如果影响行数等于 >1，表示更新的记录行数
   */
  int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

  /**
   * 根据id查询秒杀对象
   *
   * @param seckillId the seckill id
   * @return seckill
   */
  Seckill queryById(long seckillId);

  /**
   * 根据偏移量程序秒杀商品列表
   * java 语言是不能记录形参的，所以我们使用@Param（）这个注解来记住参数的名称。
   * 但是，在java8中，java是可以记住形参的，但是要看我们的orm框架是否使用了这个特性。
   *
   * @param offset the offset
   * @param limit the limit
   * @return list
   */
  List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);

  void killByProcedure(Map<String, Object> map);

}
