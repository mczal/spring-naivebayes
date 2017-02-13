package com.mczal.nb.model;

import com.mczal.nb.model.util.ErrorType;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Gl552 on 2/12/2017.
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "type")})
public class ErrorRate implements Serializable {
  private static final long serialVersionUID = -774762515628401586L;

  @Column
  private Integer falseNegative = 0;

  @Column
  private Integer falsePositive = 0;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column
  private Integer trueNegative = 0;

  @Column
  private Integer truePositive = 0;

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private ErrorType type;

  public Integer getFalseNegative() {
    return falseNegative;
  }

  public void setFalseNegative(Integer falseNegative) {
    this.falseNegative = falseNegative;
  }

  public Integer getFalsePositive() {
    return falsePositive;
  }

  public void setFalsePositive(Integer falsePositive) {
    this.falsePositive = falsePositive;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getTrueNegative() {
    return trueNegative;
  }

  public void setTrueNegative(Integer trueNegative) {
    this.trueNegative = trueNegative;
  }

  public Integer getTruePositive() {
    return truePositive;
  }

  public void setTruePositive(Integer truePositive) {
    this.truePositive = truePositive;
  }

  public ErrorType getType() {
    return type;
  }

  public void setType(ErrorType type) {
    this.type = type;
  }
}
