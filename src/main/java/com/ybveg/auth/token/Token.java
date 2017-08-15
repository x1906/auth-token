package com.ybveg.auth.token;

public interface Token {

  String JWT_TOKEN = "jwt_token";

  String TOKEN_NAME = "X-Token";

  String getToken();

  <T> T getData(Class<T> clazz);

  boolean isRefresh();
}
