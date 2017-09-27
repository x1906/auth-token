package com.ybveg.auth;

import com.ybveg.auth.exception.AuthScanException;
import com.ybveg.auth.exception.TokenExpiredException;
import com.ybveg.auth.exception.TokenInvalidException;
import com.ybveg.auth.model.ModuleModel;
import com.ybveg.auth.token.AccessToken;
import com.ybveg.auth.token.RefreshToken;
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
   * @param userId 用户ID或者用户名
   * @param data 用户其他数据不要存敏感数据
   */
  <T> AccessToken createAccessToken(String userId, T data);

  /**
   * 创建刷新令牌
   *
   * @param userId ID
   */
  RefreshToken createRefreshToken(String userId);

  /**
   * 验证数据令牌
   *
   * @param rawToken token
   */
  AccessToken parseAccess(String rawToken) throws TokenInvalidException, TokenExpiredException;

  /**
   * 验证刷新令牌
   *
   * @param rawToken token
   */
  RefreshToken parseRefresh(String rawToken)
      throws TokenExpiredException, TokenInvalidException;

  /**
   * 验证权限 请继承AuthAbstractManager 并实现getAuths
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
  Collection<ModuleModel> scan() throws AuthScanException;

  /**
   * 作废数据令牌
   */
  void evictAccess(String id);

  /**
   * 作废刷新令牌
   */
  void evictRefresh(String id);
}
