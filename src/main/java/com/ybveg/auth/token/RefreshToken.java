package com.ybveg.auth.token;

import io.jsonwebtoken.Claims;

/**
 * 数据访问令牌
 */
public final class RefreshToken extends AbstractToken {


  public RefreshToken(final String token, final Claims claims) {
    super(token, claims);
  }

  @Override
  public <T> T getData(Class<T> clazz) {
    return (T) super.getClaims().getSubject();
  }

}
