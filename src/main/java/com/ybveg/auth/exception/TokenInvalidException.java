package com.ybveg.auth.exception;

/**
 * Token 验证错误<br/>
 *
 * UnsupportedJwtException | MalformedJwtException | SignatureException |
 * IllegalArgumentException<br/>
 *
 * 不支持的JWT  修改畸形的jwt  签名错误的jwt  参数错误的jwt
 *
 * @auther zbb
 * @create 2017/8/15
 */
public class TokenInvalidException extends RuntimeException {


  public TokenInvalidException(String message) {
    super(message);
  }

  public TokenInvalidException(String message, Throwable cause) {
    super(message, cause);
  }
}
