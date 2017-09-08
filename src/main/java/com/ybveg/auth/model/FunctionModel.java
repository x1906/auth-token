package com.ybveg.auth.model;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * @auther zbb
 * @create 2017/8/14
 */
@Getter
@Setter
public class FunctionModel implements Serializable {

  private static final long serialVersionUID = -7823154137975351934L;

  private String code;  //功能编码
  private String name;  //功能名称
  private String clazz; // class

  @Override
  public String toString() {
    return this.clazz;
  }

  @Override
  public int hashCode() {
    return this.clazz.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof FunctionModel) {
      FunctionModel model = (FunctionModel) obj;
      return this.clazz.equals(model.getClazz());
    } else {
      return obj.equals(this.clazz);
    }
  }
}
