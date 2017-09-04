package com.ybveg.auth;

import com.ybveg.auth.exception.AuthScanException;
import com.ybveg.auth.model.FunctionModel;
import com.ybveg.auth.model.ModuleModel;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

  private static final String POSTFIX = "/**/*.class";
  private static final String PREFIX = "classpath*:";


  private List<String> scans;

  private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

  public AuthScanner(String scan) {
    if (StringUtils.isNotEmpty(scan)) {
      String[] sc = scan.replaceAll("\\.", "/").split(",");
      scans = new ArrayList<>();
      for (String s : sc) {
        scans.add(PREFIX + s + POSTFIX);
      }
    }
  }

  public Collection<ModuleModel> scan() throws AuthScanException {
    final Map<String, ModuleModel> resultMap = new HashMap<>();  // 全局模块集合
    final Map<String, String> codeOfModule = new HashMap<>();  // key 为code 判断Module 编码是否重复
    final Map<String, String> codeOfFunction = new HashMap<>();  // key 为code 判断Module 编码是否重复
    List<Resource> resources = getResource();

    if (!resources.isEmpty()) {
      MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(
          this.resourcePatternResolver);
      for (Resource resource : resources) {
        if (resource.isReadable()) {
          try {
            MetadataReader reader = readerFactory.getMetadataReader(resource);
            String className = reader.getClassMetadata().getClassName();
            Class<?> clazz = Class.forName(className);
            Module module = clazz.getAnnotation(Module.class);
            if (module == null) {
              continue;
            }
            final Map<String, Set<FunctionModel>> toSingle = new HashMap<>();// 指定了模块的功能集合
            final Set<FunctionModel> toAll = new HashSet<>();   // 没有指定模块的功能集合

            Method[] methods = clazz.getDeclaredMethods();  // 取不继承方法
            for (Method m : methods) {
              Function function = m.getAnnotation(Function.class);
              if (function != null) {
                // 指定模块
                Map<String, Set<FunctionModel>> moduleFunctionModel = moduleRelationFunctionModel(
                    function.relation());

                moduleFunctionModel.forEach((key, value) -> { //  JDK8 特性 forEach
                  toSingle.merge(key, value, (oldValue, newValue) -> {  // JDK8 特性 Map 合并值
                    if (oldValue == null) {   // 如果旧值不存在 说明 toSingle 内没有 此key 直接返回newValue
                      oldValue = newValue;
                    } else {
                      oldValue.addAll(newValue);  // 如果旧值存在 在oldValue中加入newValue
                    }
                    return oldValue;
                  });
                });

                Map<String, Set<String>> functionModule = functionRelationModule(
                    function.relation());

                Class<? extends FunctionType>[] functionClasses = function.value();
                for (Class<? extends FunctionType> functionClass : functionClasses) {
                  FunctionModel functionModel = Utils.classToFunctionModel(functionClass);

                  String functionClassName = codeOfFunction.get(functionModel.getCode());
                  if (StringUtils.isNotEmpty(functionClassName)) { // 判断 Function 是否重复
                    if (!functionClassName.equals(functionClass.getName())) {
                      throw new AuthScanException("功能编码{0},不能对应多个Class,{1}和{2}",
                          functionModel.getCode(),
                          functionClassName, functionClass.getName());
                    }
                  } else {  //  如果为空 插入
                    codeOfFunction.put(functionModel.getCode(), functionClass.getName());
                  }
                  if (!functionModule.containsKey(functionModel)) {  //如果指定了模块
                    toAll.add(functionModel);
                  }
                }
              }
            }

            final Map<String, ModuleModel> classHasModule = new HashMap<>(); // 当前类含有的模块

            Class<? extends ModuleType>[] moduleClasses = module.value();
            for (Class<? extends ModuleType> moduleClass : moduleClasses) {
              ModuleModel moduleModel = null;
              if (!resultMap.containsKey(moduleClass.getName())) {   // 判断 全局模块集合 内是否有此模块
                moduleModel = Utils.classToModuleModel(moduleClass);   // 如果没有 生成此模块对象
                resultMap.put(moduleClass.getName(), moduleModel);   // 添加到 全局模块集合
                classHasModule.put(moduleClass.getName(), moduleModel);  // 添加到 当前类含有的模块中
              } else {                        // 如果有 从 全局模块集合 取出 放入到  当前类含有的模块中
                moduleModel = resultMap.get(moduleClass.getName());
                classHasModule.put(moduleClass.getName(), moduleModel);
              }
              String moduleClassName = codeOfModule.get(moduleModel.getCode());
              if (StringUtils.isNotEmpty(moduleClassName)) {        // 判断模块code 和 模块class 是否一一对应
                if (!moduleModel.getClazz().equals(moduleClassName)) {
                  throw new AuthScanException("模块编码{0},不能对应多个Class,{1}和{2}",
                      moduleModel.getCode(),
                      moduleClassName, moduleModel.getClazz());
                }
              } else {
                codeOfModule.put(moduleModel.getCode(), moduleModel.getClazz());
              }

              moduleModel.addFunctions(toAll);   // 将 没有指定模块的功能集合 添加到此模块中
            }

            toSingle.forEach((key, value) -> {    // 将  指定了模块的功能集合 合并到 classHasModule
              ModuleModel moduleModel = classHasModule.get(key);
              if (moduleModel != null) {    // 如果 当前类有此模块  将此模块有的功能加入 否则不操作
                moduleModel.addFunctions(value);
              }
            });


          } catch (ClassNotFoundException | IOException e) {
            log.error("AuthScanner {} not found", resource.getFilename(), e);
          }
        }
      }
    }
    return resultMap.values();
  }


  private List<Resource> getResource() throws AuthScanException {
    List<Resource> list = new ArrayList<>();
    if (scans == null) {
      throw new AuthScanException("未配置模块扫描包 auth.module.scan");
    }
    for (String s : scans) {
      try {
        list.addAll(Arrays.asList(resourcePatternResolver.getResources(s)));
      } catch (IOException e) {
        log.error("AuthScanner getResource() error {}", s, e);
      }
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
    Map<String, Set<String>> relationMap = moduleRelationFunction(function.relation());

    for (Class<? extends FunctionType> functionClazz : functionClasses) {
      if (!relationMap.containsKey(functionClazz.getName())) { // 如果指定关系 表明所有
        toAll.add(functionClazz.getName());
      }
    }

    for (Class<? extends ModuleType> moduleClazz : moduleClasses) {
      Set<String> set = new HashSet<>();
      set.addAll(toAll);
      set.addAll(relationMap.get(moduleClazz.getName()));    //指定模块
      map.put(moduleClazz.getName(), set);
    }

    return map;
  }

  /**
   * 返回模块与功能 关系描述Map <br/> key为Module class name <br />value为Function功能 class name set集合
   */
  private Map<String, Set<String>> moduleRelationFunction(Relation[] relations) {
    Map<String, Set<String>> relationMap = new HashMap<>();
    if (relations != null) {
      for (Relation relation : relations) {
        if (relationMap.containsKey(relation.module().getName())) {
          relationMap.get(relation.module().getName()).add(relation.func().getName());
        } else {
          Set<String> set = new HashSet<>();
          set.add(relation.func().getName());
          relationMap.put(relation.module().getName(), set);
        }
      }
    }
    return relationMap;
  }

  /**
   * 返回模块与功能 关系描述Map <br/> key为Module class name <br />value为FunctionModel set集合
   */
  private Map<String, Set<FunctionModel>> moduleRelationFunctionModel(Relation[] relations) {
    Map<String, Set<FunctionModel>> relationMap = new HashMap<>();
    if (relations != null) {
      for (Relation relation : relations) {
        if (relationMap.containsKey(relation.module().getName())) {
          relationMap.get(relation.module().getName())
              .add(Utils.classToFunctionModel(relation.func()));
        } else {
          Set<FunctionModel> set = new HashSet<>();
          set.add(Utils.classToFunctionModel(relation.func()));
          relationMap.put(relation.module().getName(), set);
        }
      }
    }
    return relationMap;
  }

  /**
   * 返回功能与模块 关系描述Map <br/> key为Fucntion class name <br />value为Module功能 class set集合
   */
  private static Map<String, Set<String>> functionRelationModule(
      Relation[] relations) {
    Map<String, Set<String>> relationMap = new HashMap<>();
    if (relations != null) {
      for (Relation relation : relations) {
        if (relationMap.containsKey(relation.func().getName())) {
          relationMap.get(relation.func().getName()).add(relation.module().getName());
        } else {
          Set<String> set = new HashSet<>();
          set.add(relation.module().getName());
          relationMap.put(relation.func().getName(), set);
        }
      }
    }
    return relationMap;
  }
}
