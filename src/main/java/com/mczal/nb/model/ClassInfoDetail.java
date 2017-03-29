package com.mczal.nb.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

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

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "classInfoDetail")
  private List<ErrorRate> errorRates = new ArrayList<ErrorRate>();

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

  public List<ErrorRate> getErrorRates() {
    return errorRates;
  }

  public void setErrorRates(List<ErrorRate> errorRates) {
    this.errorRates = errorRates;
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
