package com.ybveg.auth.exception;

/**
 * Token 过期
 *
 * @auther zbb
 * @create 2017/8/15
 */
public class TokenExpiredException extends RuntimeException {


  public TokenExpiredException(String message) {
    super(message);
  }

  public TokenExpiredException(String message, Throwable cause) {
    super(message, cause);
  }
}
