package com.mczal.nb.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Gl552 on 1/22/2017.
 */
@Entity
public class PredictorInfo implements Serializable {
  private static final long serialVersionUID = 3941627791928304053L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @OneToMany(cascade = CascadeType.ALL,
      fetch = FetchType.EAGER,
      mappedBy = "predictorInfo")
  private Set<PredictorInfoDetail> predictorInfoDetails = new HashSet<>();

  @Column
  private String predictorName;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Set<PredictorInfoDetail> getPredictorInfoDetails() {
    return predictorInfoDetails;
  }

  public void setPredictorInfoDetails(Set<PredictorInfoDetail> predictorInfoDetails) {
    this.predictorInfoDetails = predictorInfoDetails;
  }

  public String getPredictorName() {
    return predictorName;
  }

  public void setPredictorName(String predictorName) {
    this.predictorName = predictorName;
  }
}
