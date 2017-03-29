package com.mczal.nb.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by mczal on 07/03/17.
 */
@Entity
public class ConfusionMatrixDetail implements Serializable {

  private static final long serialVersionUID = -7643004541516515067L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column
  private String predicted;

  @ManyToOne
  @JoinColumn(name = "confusionMatrixLastId")
  private ConfusionMatrixLast confusionMatrixLast;

  @Column
  private String percentage;

  @Column
  private String actual;

  public String getActual() {
    return actual;
  }

  public void setActual(String actual) {
    this.actual = actual;
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

  public String getPercentage() {
    return percentage;
  }

  public void setPercentage(String percentage) {
    this.percentage = percentage;
  }

  public String getPredicted() {
    return predicted;
  }

  public void setPredicted(String predicted) {
    this.predicted = predicted;
  }

  @Override
  public String toString() {
    return "ConfusionMatrixDetail{" +
        "id=" + id +
        ", predicted='" + predicted + '\'' +
        ", confusionMatrixLast=" + confusionMatrixLast +
        ", percentage='" + percentage + '\'' +
        ", actual='" + actual + '\'' +
        '}';
  }

}
