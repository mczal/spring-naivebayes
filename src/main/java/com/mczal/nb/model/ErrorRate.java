package com.mczal.nb.model;

import com.mczal.nb.model.util.ErrorType;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by Gl552 on 2/12/2017.
 */
@Entity
public class ErrorRate implements Serializable {

  private static final long serialVersionUID = -774762515628401586L;

//  @Column
//  private Integer positive = 0;
//
//  @Column
//  private Integer negative = 0;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private ErrorType type;

  @ManyToOne(cascade = CascadeType.DETACH)
  @JoinColumn(name = "classInfoDetailId")
  private ClassInfoDetail classInfoDetail;

  @Column
  private String operation;

  @Column
  private Double result;

  public ClassInfoDetail getClassInfoDetail() {
    return classInfoDetail;
  }

  public void setClassInfoDetail(ClassInfoDetail classInfoDetail) {
    this.classInfoDetail = classInfoDetail;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public Double getResult() {
    return result;
  }

  public void setResult(Double result) {
    this.result = result;
  }

  public ErrorType getType() {
    return type;
  }

  public void setType(ErrorType type) {
    this.type = type;
  }

//  public Integer getNegative() {
//    return negative;
//  }
//
//  public void setNegative(Integer negative) {
//    this.negative = negative;
//  }
//
//  public Integer getPositive() {
//    return positive;
//  }
//
//  public void setPositive(Integer positive) {
//    this.positive = positive;
//  }

//  public void incrementNegative() {
//    this.negative++;
//  }
//
//  public void incrementPositive() {
//    this.positive++;
//  }


}
