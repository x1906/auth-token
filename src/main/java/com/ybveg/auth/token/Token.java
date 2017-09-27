package com.ybveg.auth.token;

import java.util.Date;

public interface Token {

  String USER = "user_id";

  String ACCESS_TOKEN = "access_token";
  String REFRESH_TOKEN = "refresh_token";

  String ACCESS_EXP = "access_exp";
  String REFRESH_EXP = "refresh_exp";

  String getToken();

  <T> T getData(Class<T> clazz);

  /**
   * 用户ID
   */
  String getUserId();

  /**
   * 令牌ID
   */
  String getId();


  /**
   * 获取令牌到期时间
   */
  public Date getExpiration();

  /**
   * 获取令牌签发时间
   */
  public Date getIssuedAt();

}
