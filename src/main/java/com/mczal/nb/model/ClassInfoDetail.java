package com.mczal.nb.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Gl552 on 1/22/2017.
 */
@Entity
public class ClassInfoDetail implements Serializable {

  private static final long serialVersionUID = 6334593415054392713L;

  //  @ManyToOne
  //  @JoinColumn(name = "bayesianModelId")
  //  private BayesianModel bayesianModel;

  @ManyToOne
  @JoinColumn(name = "classInfoId")
  private ClassInfo classInfo;

  @Column
  private Integer count = 0;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column
  private String value;

  public ClassInfo getClassInfo() {
    return classInfo;
  }

  public void setClassInfo(ClassInfo classInfo) {
    this.classInfo = classInfo;
  }

  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
