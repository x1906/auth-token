package com.ybveg.auth.token;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import java.util.Date;

/**
 * 数据访问令牌
 */
public abstract class AbstractToken implements Token {

  private final String rawToken;

  //  @JSONField(serialize = false)
  private final Claims claims;


  protected AbstractToken(final String token, final Claims claims) {
    this.rawToken = token;
    this.claims = claims;
  }

  protected Claims getClaims() {
    return this.claims;
  }

  @Override
  public String getToken() {
    return this.rawToken;
  }

  @Override
  public <T> T getData(Class<T> clazz) {
    String subject = this.claims.getSubject();
    return JSON.parseObject(subject, clazz);
  }

  @Override
  public String getUserId() {
    return this.claims.get(Token.USER).toString();
  }

  @Override
  public String getId() {
    return this.claims.getId();
  }


  /**
   * 获取令牌到期时间
   */
  @Override
  public Date getExpiration() {
    return this.claims.getExpiration();
  }

  /**
   * 获取令牌签发时间
   */
  @Override
  public Date getIssuedAt() {
    return this.claims.getIssuedAt();
  }

}
