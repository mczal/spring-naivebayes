package com.mczal.nb.controller.utils;

import java.io.Serializable;

/**
 * Created by mczal on 06/03/17.
 */
public class WrapperTestingResponse implements Serializable {

  private static final long serialVersionUID = 5630599963439781510L;

  private String className;

  private String predicted;

  private String percentage;

  private ConfusionMatrix confusionMatrix;

  private String actual;

  private String printedConfusionMatrix;

  public String getActual() {
    return actual;
  }

  public void setActual(String actual) {
    this.actual = actual;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public ConfusionMatrix getConfusionMatrix() {
    return confusionMatrix;
  }

  public void setConfusionMatrix(ConfusionMatrix confusionMatrix) {
    this.confusionMatrix = confusionMatrix;
//    str = str.replaceAll("(\r\n|\n)", "<br />");
//    temp = temp.replace(" ", "&nbsp;");
//    this.printedConfusionMatrix = confusionMatrix.stringPrintedMatrix()
//        .replaceAll("(\r\n|\n)", "<br/>").replace(" ", "&nbsp;");
    this.printedConfusionMatrix = confusionMatrix.stringPrintedMatrix();
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

  public String getPrintedConfusionMatrix() {
    return printedConfusionMatrix;
  }
}
