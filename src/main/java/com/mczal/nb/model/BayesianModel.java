package com.mczal.nb.model;

import com.mczal.nb.model.util.Type;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Gl552 on 1/21/2017.
 */
@Entity
public class BayesianModel implements Serializable {

  private static final long serialVersionUID = 918981889757624891L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column
  private String className;

  @Column
  private String classVal;

  @Column
  private Integer count;

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

  public String getPredictor() {
    return predictorName;
  }

  public void setPredictor(String predictor) {
    this.predictorName = predictor;
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
