package com.ybveg.auth;

/**
 * 每个模块都有一个默认功能
 *
 * @auther zbb
 * @create 2017/8/11
 */
public class DefaultFunction implements FunctionType {

  @Override
  public String getCode() {
    return "auth.function.default";
  }

  @Override
  public String getName() {
    return "默认";
  }
}
