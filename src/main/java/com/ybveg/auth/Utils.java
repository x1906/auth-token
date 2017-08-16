package com.ybveg.auth;

import com.ybveg.auth.model.FunctionModel;
import com.ybveg.auth.model.ModuleModel;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;

/**
 * @auther zbb
 * @create 2017/8/15
 */
@Slf4j
public class Utils {


  public static ModuleModel classToModuleModel(Class<? extends ModuleType> clazz) {
    try {
      ModuleModel module = new ModuleModel();
      ModuleType instance = clazz.newInstance();
      module.setClazz(clazz.getName());
      module.setCode(instance.getCode());
      module.setName(instance.getName());
      module.setFunctions(new ArrayList<>());
      return module;
    } catch (InstantiationException | IllegalAccessException e) {
      log.error("classToModuleModel error {}", clazz.getName(), e);
      return null;
    }
  }

  public static FunctionModel classToFunctionModel(Class<? extends FunctionType> clazz) {
    try {
      FunctionModel function = new FunctionModel();
      FunctionType instance = clazz.newInstance();
      function.setClazz(clazz.getName());
      function.setCode(instance.getCode());
      function.setName(instance.getName());
      return function;
    } catch (InstantiationException | IllegalAccessException e) {
      log.error("classToModuleModel error {}", clazz.getName(), e);
      return null;
    }
  }
}
