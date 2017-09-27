package com.ybveg.auth.token;

import io.jsonwebtoken.Claims;
import java.util.Date;

/**
 * 数据访问令牌
 */
public final class AccessToken extends AbstractToken {


  protected AccessToken(final String token, final Claims claims) {
    super(token, claims);
  }

  @Override
  public Date getExpiration() {
    Date date = super.getExpiration();
    return new Date(date.getTime() - 5 * 60 * 1000); // 提前5分钟到期
  }
}
