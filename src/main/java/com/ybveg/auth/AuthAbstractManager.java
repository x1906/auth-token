package com.ybveg.auth;

import com.ybveg.auth.exception.AuthScanException;
import com.ybveg.auth.exception.TokenExpiredException;
import com.ybveg.auth.exception.TokenInvalidException;
import com.ybveg.auth.model.FunctionModel;
import com.ybveg.auth.model.ModuleModel;
import com.ybveg.auth.token.AccessToken;
import com.ybveg.auth.token.TokenFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

  private String MESSAGE = "";


  @Override
  public <T> AccessToken createAccessToken(String id, T data) {
    return tokenFactory.createAccessToken(id, data);
  }

  @Override
  public AccessToken parseToken(String rawToken)
      throws TokenExpiredException, TokenInvalidException {
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
  public boolean valid(final Module module, final Function function, final String key) {
    if (module == null) {  // 如果模块注解为null 返回true
      return true;
    }
    Optional<List<ModuleModel>> list = Optional.of(this.getAuths(key));
    if (list.isPresent()) {
      final Map<String, Set<String>> map = scanner.resolveToMap(module, function);
      for (ModuleModel m : list.get()) {
        if (map.containsKey(m.getClazz())) {
          Set<String> mapSet = map.get(m.getClazz());
          if (function == null) {
            log.info("权限验证通过 拥有访问模块:{}", m.getName());
            return true;
          } else if (m.getFunctions() != null) {
            List<FunctionModel> functions = new ArrayList<>(m.getFunctions());
            functions.retainAll(mapSet); // 求交集
            if (functions.size() > 0) {
              log.info(
                  "权限验证通过 拥有访问模块:{} 功能:{}", m.getName(), functions.iterator().next().getName());
              return true;
            }
          }
        }
      }
      final List<String> m_key = new ArrayList<>();
      final List f_value = new ArrayList();

      if (log.isDebugEnabled()) {
        map.forEach((k, v) -> {
          m_key.add(k);
          m_key.add(StringUtils.join(Arrays.asList(v), ","));
//        log.info("模块验证失败 " + k);
//        log.info("模块功能验证失败 " + StringUtils.join(Arrays.asList(v), ","));
        });

        log.debug("模块验证失败 " + StringUtils.join(Arrays.asList(m_key), ","));
        if (!f_value.isEmpty()) {
          log.debug("模块功能验证失败 " + StringUtils.join(Arrays.asList(f_value), ","));
        }
      }
      return false;
    } else {
      log.info("权限验证失败 无法获取到用户权限信息:" + key);
      return false;
    }
  }


  public Collection<ModuleModel> scan() throws AuthScanException {
    return scanner.scan();
  }

}
