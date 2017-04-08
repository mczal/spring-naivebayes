package com.mczal.nb.dto;

import java.io.Serializable;

/**
 * Created by mczal on 08/04/17.
 */
public class RenewModelHdfsFormRequest implements Serializable {

  private static final long serialVersionUID = 5244828371638121944L;

  private String modelHdfs;

  public String getModelHdfs() {
    return modelHdfs;
  }

  public void setModelHdfs(String modelHdfs) {
    this.modelHdfs = modelHdfs;
  }
}
