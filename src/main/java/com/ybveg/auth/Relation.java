package com.ybveg.auth;

/**
 * 描述功能和模块的关系
 *
 * 当一个类上含有多个模块的时候 可能需要指定那个功能是那个模块的
 *
 * 如果不描述 默认为该功能属于所有模块
 *
 * @auther zbb
 * @create 2017/8/11
 */
public @interface Relation {

  Class<? extends ModuleType> module();

  Class<? extends FunctionType> func();
}
