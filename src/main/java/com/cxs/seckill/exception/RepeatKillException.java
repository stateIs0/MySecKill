package com.cxs.seckill.exception;

/**
 * 重复秒杀异常(运行期异常）
 * Created by cxsta on 2017/7/9.
 */
public class RepeatKillException extends SeckilException {

  public RepeatKillException(String message) {
    super(message);
  }

  public RepeatKillException(String message, Throwable cause) {
    super(message, cause);
  }
}
