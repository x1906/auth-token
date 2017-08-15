package com.ybveg.auth.token;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import io.jsonwebtoken.Claims;

/**
 * 数据访问令牌
 */
public final class AccessToken implements Token {

  private final String rawToken;

  @JSONField(serialize = false)
  private Claims claims;

  @JSONField(serialize = false)
  private boolean isRefresh = false;

  protected AccessToken(final String token, Claims claims) {
    this.rawToken = token;
    this.claims = claims;
  }

  public String getToken() {
    return this.rawToken;
  }

  public Claims getClaims() {
    return claims;
  }

  public <T> T getData(Class<T> clazz) {
    String subject = this.claims.getSubject();
    return JSON.parseObject(subject, clazz);
  }

  @Override
  public boolean isRefresh() {
    return isRefresh;
  }

  public void setRefresh(boolean refresh) {
    isRefresh = refresh;
  }
}
