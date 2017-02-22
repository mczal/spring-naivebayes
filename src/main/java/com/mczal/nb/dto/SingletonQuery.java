package com.mczal.nb.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gl552 on 2/11/2017.
 */
public class SingletonQuery implements Serializable {

  private static final long serialVersionUID = -8199181218321325761L;

  private List<String> classInfos = new ArrayList<>();

  /**
   * Format :
   * Type|PredictorName|PredictorValue
   */
  private List<String> predictorInfos = new ArrayList<>();

  public List<String> getClassInfos() {
    return classInfos;
  }

  public void setClassInfos(List<String> classInfos) {
    this.classInfos = classInfos;
  }

  public List<String> getPredictorInfos() {
    return predictorInfos;
  }

  public void setPredictorInfos(List<String> predictorInfos) {
    this.predictorInfos = predictorInfos;
  }

  @Override
  public String toString() {
    return "SingletonQuery{" + "classInfos=" + classInfos + ", predictorInfos=" + predictorInfos
        + '}';
  }
}
