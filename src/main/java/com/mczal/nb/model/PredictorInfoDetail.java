package com.mczal.nb.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Gl552 on 1/22/2017.
 */
@Entity
public class PredictorInfoDetail implements Serializable {

  private static final long serialVersionUID = -9028287663776722770L;

  //  @ManyToOne
  //  @JoinColumn(name = "bayesianModelId")
  //  private BayesianModel bayesianModel;

  @Column
  private String classPriorName;

  @Column
  private String classPriorValue;

  @Column
  private Integer count = 0;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column
  private BigDecimal mean;

  @ManyToOne
  @JoinColumn(name = "predictorInfoId")
  private PredictorInfo predictorInfo;

  @Column
  private BigDecimal sigma;

  @Column
  private String value;

  public String getClassPriorName() {
    return classPriorName;
  }

  public void setClassPriorName(String classPriorName) {
    this.classPriorName = classPriorName;
  }

  public String getClassPriorValue() {
    return classPriorValue;
  }

  public void setClassPriorValue(String classPriorValue) {
    this.classPriorValue = classPriorValue;
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

  public PredictorInfo getPredictorInfo() {
    return predictorInfo;
  }

  public void setPredictorInfo(PredictorInfo predictorInfo) {
    this.predictorInfo = predictorInfo;
  }

  public BigDecimal getSigma() {
    return sigma;
  }

  public void setSigma(BigDecimal sigma) {
    this.sigma = sigma;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
