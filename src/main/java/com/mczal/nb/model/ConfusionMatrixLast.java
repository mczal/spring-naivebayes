package com.mczal.nb.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Created by mczal on 06/03/17.
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "className")})
@EntityListeners(value = {AuditingEntityListener.class})
public class ConfusionMatrixLast implements Serializable {

  private static final long serialVersionUID = -6423414871356377433L;

  /**
   * TODO: RECONS
   */
  @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
  @JoinColumn(name = "classInfoId")
  private ClassInfo classInfo;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @OneToMany(cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      mappedBy = "confusionMatrixLast")
  private List<ConfusionMatrixDetail> confusionMatrixDetails = new ArrayList<ConfusionMatrixDetail>();

  @CreatedDate
  @Temporal(value = TemporalType.TIMESTAMP)
  @Column(name = "createdDate",
      nullable = false)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(value = TemporalType.TIMESTAMP)
  @Column(name = "updatedDate")
  private Date updatedDate;

  @Column(name = "className")
  private String className;

  @Lob
  @Column(name = "printedConfusionMatrix", length = 10000)
  private String printedConfusionMatrix;

  public ClassInfo getClassInfo() {
    return classInfo;
  }

  public void setClassInfo(ClassInfo classInfo) {
    this.classInfo = classInfo;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public List<ConfusionMatrixDetail> getConfusionMatrixDetails() {
    return confusionMatrixDetails;
  }

  public void setConfusionMatrixDetails(
      List<ConfusionMatrixDetail> confusionMatrixDetails) {
    this.confusionMatrixDetails = confusionMatrixDetails;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getPrintedConfusionMatrix() {
    return printedConfusionMatrix;
  }

  public void setPrintedConfusionMatrix(String printedConfusionMatrix) {
    this.printedConfusionMatrix = printedConfusionMatrix;
  }

  public Date getUpdatedDate() {
    return updatedDate;
  }

  public void setUpdatedDate(Date updatedDate) {
    this.updatedDate = updatedDate;
  }

  @Override
  public String toString() {
    return "ConfusionMatrixLast{" +
        "id=" + id +
        ", confusionMatrixDetails=" + confusionMatrixDetails.size() +
        ", createdDate=" + createdDate +
        ", updatedDate=" + updatedDate +
        ", className='" + className + '\'' +
        ", printedConfusionMatrix='" + printedConfusionMatrix + '\'' +
        '}';
  }
}
