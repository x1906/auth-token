package com.ybveg.auth;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 模块注解  > 系统定义 模块大于功能
 *
 * @auther zbb
 * @create 2017/8/11
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Module {

  /**
   * 模块列表
   *
   * @return 这个Controller含有那些模块
   */
  Class<? extends ModuleType>[] value();

}
