package com.ybveg.auth;

/**
 * 功能基类 所有功能都要继承此接口
 *
 * @auther zbb
 * @create 2017/8/11
 */
public interface FunctionType {

  /**
   * 功能编码 <br/>
   *
   * 编码规则 func.功能名称 例如: func.scan 扫描功能
   */
  String getCode();

  /**
   * 功能名称
   */
  String getName();

}
