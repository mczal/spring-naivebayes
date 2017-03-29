package com.mczal.nb.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Created by Gl552 on 1/22/2017.
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "className")})
public class ClassInfo implements Serializable {

  private static final long serialVersionUID = 7690173919728795609L;

  @OneToMany(cascade = CascadeType.ALL,
      fetch = FetchType.EAGER,
      mappedBy = "classInfo")
  private Set<ClassInfoDetail> classInfoDetails = new HashSet<>();

  @Column(name = "className")
  private String className;

  @OneToOne(mappedBy = "classInfo")
  private ConfusionMatrixLast confusionMatrixLast;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "accuracy")
  private ErrorRate accuracy;

  public ErrorRate getAccuracy() {
    return accuracy;
  }

  public void setAccuracy(ErrorRate accuracy) {
    this.accuracy = accuracy;
  }

  public Set<ClassInfoDetail> getClassInfoDetails() {
    return classInfoDetails;
  }

  public void setClassInfoDetails(Set<ClassInfoDetail> classInfoDetails) {
    this.classInfoDetails = classInfoDetails;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public ConfusionMatrixLast getConfusionMatrixLast() {
    return confusionMatrixLast;
  }

  public void setConfusionMatrixLast(ConfusionMatrixLast confusionMatrixLast) {
    this.confusionMatrixLast = confusionMatrixLast;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }
}
