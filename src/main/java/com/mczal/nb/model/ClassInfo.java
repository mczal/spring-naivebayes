package com.mczal.nb.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

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

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

}
