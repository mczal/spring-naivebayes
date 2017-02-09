package com.mczal.nb.model;

import com.mczal.nb.model.util.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Gl552 on 1/22/2017.
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "predictorName")})
public class PredictorInfo implements Serializable {
  private static final long serialVersionUID = 3941627791928304053L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @OneToMany(cascade = CascadeType.ALL,
      fetch = FetchType.EAGER,
      mappedBy = "predictorInfo")
  private Set<PredictorInfoDetail> predictorInfoDetails = new HashSet<PredictorInfoDetail>();

  @Column(name = "predictorName")
  private String predictorName;

  @Column
  @Enumerated(EnumType.STRING)
  private Type type;

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

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }
}
