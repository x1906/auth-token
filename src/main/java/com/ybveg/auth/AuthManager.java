package com.ybveg.auth;

import com.ybveg.auth.model.ModuleModel;
import com.ybveg.auth.token.AccessToken;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * 权限控制接口
 *
 * @auther zbb
 * @create 2017/8/15
 */
public interface AuthManager {

  /**
   * 创建数据访问Token
   *
   * @param id 用户ID或者用户名
   * @param data 用户其他数据不要存敏感数据
   */
  <T> AccessToken createAccessToken(String id, T data);

  /**
   * 验证或者刷新token 如果未刷新token  返回null
   *
   * @param rawToken token
   * @return 新的token
   */
  AccessToken parseToken(String rawToken);

  /**
   * 验证权限 请继承AuthAbstractManager 并实现getCurrentAuth
   *
   * @param module 类似模块注解
   * @param function 方法上注解
   * @param key 获取用户权限参数
   */
  boolean valid(Module module, Function function, String key);

  /**
   * 获取用户权限
   *
   * @param key 获取权限参数
   */
  List<ModuleModel> getAuths(String key);

  /**
   * 扫描模块功能, 配置参数 auth.module.scan
   */
  Collection<ModuleModel> scan() throws IOException;
}
