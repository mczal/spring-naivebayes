package com.mczal.nb.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gl552 on 2/11/2017.
 */
public class ClassInfoDto implements Serializable {
  private static final long serialVersionUID = -4400669069419965389L;

  List<String> classesDto = new ArrayList<>();

  public List<String> getClassesDto() {
    return classesDto;
  }

  public void setClassesDto(List<String> classesDto) {
    this.classesDto = classesDto;
  }
}
