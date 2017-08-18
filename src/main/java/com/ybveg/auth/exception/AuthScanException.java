package com.ybveg.auth.exception;

import java.text.MessageFormat;

/**
 * 参数配置异常
 *
 * @auther zbb
 * @create 2017/8/15
 */
public class AuthScanException extends Exception {


  public AuthScanException(String message) {
    super(message);
  }


  public AuthScanException(String message, Object... args) {
    this(MessageFormat.format(message, args));
  }
}
