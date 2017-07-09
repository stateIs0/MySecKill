package com.cxs.seckill.exception;

/**
 * 秒杀关闭异常
 * Created by cxsta on 2017/7/9.
 */
public class SeckillCloseException extends SeckilException {

  public SeckillCloseException(String message) {
    super(message);
  }

  public SeckillCloseException(String message, Throwable cause) {
    super(message, cause);
  }
}
