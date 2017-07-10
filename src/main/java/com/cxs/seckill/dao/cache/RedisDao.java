package com.cxs.seckill.dao.cache;

import com.cxs.seckill.entity.Seckill;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by cxs on 2017/7/10.
 */
public class RedisDao {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private final JedisPool jedisPool;

  private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

  public RedisDao(String ip, int port) {
    jedisPool = new JedisPool(ip, port);
  }

  public Seckill getSeckill(long seckillId) {
    // redis 操作逻辑
    try {
      Jedis jedis = jedisPool.getResource();
      try {
        String key = "seckill:" + seckillId;
        // redis 并没有实现序列换，memcache 实现了序列化
        // get byte[] 反序列化 object（seckill）
        // 通常实现jdk的序列化接口
        // 我们自定义序列化
        // protostuff : 必须是pojo，不能是string之类的类型
        // 缓存获取到
        byte[] bytes = jedis.get(key.getBytes());
        if (bytes != null) {
          // 空对象
          Seckill seckill = schema.newMessage();
          ProtobufIOUtil.mergeFrom(bytes, seckill, schema);
          // 可以压缩到jdk序列化的10分之一到5分之1，压缩速度是2个数量级。
          return seckill;
        }
      } finally {
        jedis.close();
      }

    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }

    return null;
  }

  public String putSeckill(Seckill seckill) {
    // set object (seckill) 序列化 byte[]
    try {
      Jedis jedis = jedisPool.getResource();
      try {
        String key = "seckill:" + seckill.getSeckillId();
        byte[] bytes = ProtobufIOUtil
            .toByteArray(seckill, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
        // 超时缓存
        int timeout = 60 * 60;
        // 如果正确，返回ok
        String result = jedis.setex(key.getBytes(), timeout, bytes);
        return result;
      } finally {
        jedisPool.close();
      }

    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
    return null;
  }

}
