package com.cxs.seckill.exception;

/**
 * 秒杀业务异常
 * Created by cxsta on 2017/7/9.
 */
public class SeckilException extends RuntimeException {

  public SeckilException(String message) {
    super(message);
  }

  public SeckilException(String message, Throwable cause) {
    super(message, cause);
  }
}
