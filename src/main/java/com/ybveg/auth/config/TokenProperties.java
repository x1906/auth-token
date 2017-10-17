package com.ybveg.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @auther zbb
 * @create 2017/8/9
 */
@ConfigurationProperties("auth.token")
public class TokenProperties {

  /**
   * 数据令牌有效时间
   */
  private int expire;
  /**
   * 签发者
   */
  private String issuer;
  /**
   * 秘钥
   */
  private String secert;

  /**
   * 刷新令牌过期时间
   */
  private int refreshExpire;


  public int getExpire() {
    return expire;
  }

  public void setExpire(int expire) {
    this.expire = expire;
  }

  public String getIssuer() {
    return issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  public String getSecert() {
    return secert;
  }

  public void setSecert(String secert) {
    this.secert = secert;
  }

  public int getRefreshExpire() {
    return refreshExpire;
  }

  public void setRefreshExpire(int refreshExpire) {
    this.refreshExpire = refreshExpire;
  }
}
