package com.ybveg.auth.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * @auther zbb
 * @create 2017/8/14
 */
@Getter
@Setter
public class ModuleModel implements Serializable {

  private static final long serialVersionUID = 7068396545285702003L;

  private String code;  //模块编码
  private String name;  //模块名称
  private String clazz; // class

  private String group; // 模块组

  Set<FunctionModel> functions;

  @Override
  public String toString() {    //重写toString 便于权限验证后台对比
    return this.clazz;
  }

  @Override
  public int hashCode() {
    return this.clazz.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ModuleModel) {
      ModuleModel model = (ModuleModel) obj;
      return this.clazz.equals(model.getClazz());
    } else {
      return obj.equals(this.clazz);
    }
  }

  public void addFunction(FunctionModel function) {
    if (this.functions == null) {
      this.functions = new HashSet<>();
    }
    this.functions.add(function);
  }

  public void addFunctions(Collection<FunctionModel> functions) {
    if (this.functions == null) {
      this.functions = new HashSet<>();
    }
    this.functions.addAll(functions);
  }
}
