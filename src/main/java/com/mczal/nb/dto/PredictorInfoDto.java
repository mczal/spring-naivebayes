package com.mczal.nb.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gl552 on 2/11/2017.
 */
public class PredictorInfoDto implements Serializable {
  private static final long serialVersionUID = -3844897317335063778L;

  List<String> predictorsDto = new ArrayList<>();

  public List<String> getPredictorsDto() {
    return predictorsDto;
  }

  public void setPredictorsDto(List<String> predictorsDto) {
    this.predictorsDto = predictorsDto;
  }
}
