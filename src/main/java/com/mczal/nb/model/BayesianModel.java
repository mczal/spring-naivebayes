package com.mczal.nb.model;

import com.mczal.nb.model.util.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Gl552 on 1/21/2017.
 */
@Entity
public class BayesianModel implements Serializable {

  private static final long serialVersionUID = 918981889757624891L;

  //  @OneToMany(cascade = CascadeType.ALL,
  //      fetch = FetchType.EAGER,
  //      mappedBy = "bayesianModel")
  //  private Set<ClassInfoDetail> classInfoDetail = new HashSet<ClassInfoDetail>();

  @Column
  private String className;

  //  @OneToMany(cascade = CascadeType.ALL,
  //      fetch = FetchType.EAGER,
  //      mappedBy = "bayesianModel")
  //  private Set<PredictorInfoDetail> predictorInfoDetail = new HashSet<PredictorInfoDetail>();

  @Column
  private String classVal;

  @Column
  private Integer count;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column
  private BigDecimal mean;

  @Column
  private String predVal;

  @Column
  private String predictorName;

  @Column
  private BigDecimal sigma;

  @Column
  @Enumerated(value = EnumType.STRING)
  private Type type;

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getClassVal() {
    return classVal;
  }

  public void setClassVal(String classVal) {
    this.classVal = classVal;
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

  public BigDecimal getMean() {
    return mean;
  }

  public void setMean(BigDecimal mean) {
    this.mean = mean;
  }

  public String getPredVal() {
    return predVal;
  }

  public void setPredVal(String predVal) {
    this.predVal = predVal;
  }

  public String getPredictorName() {
    return predictorName;
  }

  public void setPredictorName(String predictorName) {
    this.predictorName = predictorName;
  }

  public BigDecimal getSigma() {
    return sigma;
  }

  public void setSigma(BigDecimal sigma) {
    this.sigma = sigma;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }
}
