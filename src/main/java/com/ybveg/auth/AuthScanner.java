package com.ybveg.auth;

import com.ybveg.auth.exception.AuthParameterException;
import com.ybveg.auth.model.FunctionModel;
import com.ybveg.auth.model.ModuleModel;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

/**
 * @auther zbb
 * @create 2017/8/14
 */
@Slf4j
public class AuthScanner {


  private String scan;

  private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

  public AuthScanner(String scan) {
    this.scan = scan;
  }

  public Set<ModuleModel> scan() throws IOException {

    Map<ModuleModel, List<FunctionModel>> result = new HashMap<>();
    List<Resource> resources = getResource();

    if (!resources.isEmpty()) {
      MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(
          this.resourcePatternResolver);
      for (Resource resource : resources) {
        if (resource.isReadable()) {
          MetadataReader reader = readerFactory.getMetadataReader(resource);
          String className = reader.getClassMetadata().getClassName();
          try {
            Class<?> clazz = Class.forName(className);
            Module module = clazz.getAnnotation(Module.class);
            if (module == null) {
              continue;
            }


            Class<? extends ModuleType>[] moduleClasses = module.value();
            for (Class<? extends ModuleType> moduleClass : moduleClasses) {
              if (!result.containsKey(moduleClass.getName())) {
                ModuleModel moduleModel = Utils.classToModuleModel(moduleClass);
                result.put(moduleModel, moduleModel.getFunctions());
              }
            }

            Method[] methods = clazz.getMethods();
            for (Method m : methods) {
              Function function = m.getAnnotation(Function.class);
              if (function != null) {
              }
            }

          } catch (ClassNotFoundException e) {
            log.error("AuthScanner {} not found", resource.getFilename(), e);
          }
        }
      }
    }
    return result.keySet();
  }


  private List<Resource> getResource() throws AuthParameterException, IOException {
    if (StringUtils.isEmpty(scan)) {
      throw new AuthParameterException("未配置模块扫描包 auth.module.scan");
    }
    List<Resource> list = new ArrayList<>();
    String[] scans = scan.split(",");
    for (String s : scans) {
      list.addAll(Arrays.asList(resourcePatternResolver.getResources(s)));
    }
    return list;
  }


  public void resolve(Module module, List<Function> functions,
      Map<ModuleModel, List<FunctionModel>> map) {

    Map<Class<? extends ModuleType>, Class<? extends FunctionType>> moduleMap = new HashMap<>();
    Class<? extends ModuleType>[] classes = module.value();
  }


  /**
   * 解析单个方法 map <br/>
   *
   * key 为 模块的class<br/>
   *
   * value 为 功能的class<br/>
   *
   * @param module 模块注解
   * @param function 功能注解
   */
  public Map<String, Set<String>> resolveToMap(Module module, Function function) {
    Map<String, Set<String>> map = new HashMap<>();

    Class<? extends ModuleType>[] moduleClasses = module.value();
    Class<? extends FunctionType>[] functionClasses = function.value();

    Set<String> toAll = new HashSet<>();
    Map<String, Set<String>> relationMap = resolveRelationString(function.relation());

    for (Class<? extends FunctionType> functionClazz : functionClasses) {
      if (!relationMap.containsKey(functionClazz.getName())) { // 如果指定关系 表明所有
        toAll.add(functionClazz.getName());
      }
    }

    for (Class<? extends ModuleType> moduleClazz : moduleClasses) {
      Set<String> temp = new HashSet<>();
      temp.addAll(toAll);
      temp.addAll(relationMap.get(moduleClazz.getName()));    //指定模块
      map.put(moduleClazz.getName(), temp);
    }

    return map;
  }

  /**
   * 返回功能模块关系描述Map <br/> key为模块class name <br />value为模块功能 class name set集合
   */
  private Map<String, Set<String>> resolveRelationString(
      Relation[] relations) {
    Map<String, Set<String>> relationMap = new HashMap<>();
    if (relations != null) {
      for (Relation relation : relations) {
        if (relationMap.get(relation.module().getName()) != null) {
          relationMap.get(relation.module().getName()).add(relation.func().getName());
        } else {
          Set<String> temp = new HashSet<>();
          temp.add(relation.func().getName());
          relationMap.put(relation.module().getName(), temp);
        }
      }
    }
    return relationMap;
  }

  /**
   * 返回功能模块关系描述Map <br/> key为模块class name <br />value为模块功能 class set集合
   */
  private static Map<String, Set<Class<? extends FunctionType>>> resolveRelationClass(
      Relation[] relations) {
    Map<String, Set<Class<? extends FunctionType>>> relationMap = new HashMap<>();
    if (relations != null) {
      for (Relation relation : relations) {
        if (relationMap.get(relation.module().getName()) != null) {
          relationMap.get(relation.module().getName()).add(relation.func());
        } else {
          Set<Class<? extends FunctionType>> temp = new HashSet<>();
          temp.add(relation.func());
          relationMap.put(relation.module().getName(), temp);
        }
      }
    }
    return relationMap;
  }
}
