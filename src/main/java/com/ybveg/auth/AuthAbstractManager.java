package com.ybveg.auth;

import com.ybveg.auth.exception.AuthScanException;
import com.ybveg.auth.model.FunctionModel;
import com.ybveg.auth.model.ModuleModel;
import com.ybveg.auth.token.AccessToken;
import com.ybveg.auth.token.TokenFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 权限控制抽象类
 *
 * @auther zbb
 * @create 2017/8/14
 */
@Slf4j
public abstract class AuthAbstractManager implements AuthManager {

  @Autowired
  private TokenFactory tokenFactory;

  @Autowired
  private AuthScanner scanner;


  @Override
  public <T> AccessToken createAccessToken(String id, T data) {
    return tokenFactory.createAccessToken(id, data);
  }

  @Override
  public AccessToken parseToken(String rawToken) {
    return tokenFactory.parseToken(rawToken);
  }

  /**
   * 验证权限
   *
   * @param module 模块注解
   * @param function 功能注解
   * @param key 获取权限参数
   */
  @Override
  public boolean valid(final Module module, final Function function, String key) {
    Optional<List<ModuleModel>> list = Optional.of(this.getAuths(key));
    if (list.isPresent()) {
      final Map<String, Set<String>> map = scanner.resolveToMap(module, function);
      Optional<ModuleModel> result = list.map(moduleModels -> {
        for (ModuleModel m : moduleModels) {
          if (map.containsKey(m.getClazz())) {
            if (function != null) {
              Set<FunctionModel> functions = Optional.of(m.getFunctions())
                  .orElse(Collections.emptySet());
              functions.retainAll(map.get(m.getClazz()));
              if (functions.size() > 0) {
                return m;
              }
            } else {
              return m;
            }
          }
        }
        return null;
      });
      return result.isPresent();
    } else {
      log.info("权限验证失败 无法获取到用户权限信息:" + key);
      return false;
    }
  }

  public Collection<ModuleModel> scan() throws AuthScanException {
    return scanner.scan();
  }

}
