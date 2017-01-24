package com.mczal.nb.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Gl552 on 1/22/2017.
 */
@Entity
public class PredictorInfoDetail implements Serializable{

  private static final long serialVersionUID = -9028287663776722770L;

  @Column
  private Integer count;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "predictorInfoId")
  private PredictorInfo predictorInfo;

  @Column
  private String value;

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

  public PredictorInfo getPredictorInfo() {
    return predictorInfo;
  }

  public void setPredictorInfo(PredictorInfo predictorInfo) {
    this.predictorInfo = predictorInfo;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
