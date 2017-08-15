package com.ybveg.auth;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 功能注解作用于方法上<br/>
 *
 * 系统定义 模块大于功能
 *
 * @auther zbb
 * @create 2017/8/11
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Function {

  /**
   * @return 这个方法上可以执行那些功能
   */
  Class<? extends FunctionType>[] value();

  Relation[] relation() default {};
}
