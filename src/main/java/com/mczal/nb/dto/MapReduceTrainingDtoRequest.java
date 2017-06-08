package com.mczal.nb.dto;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by mczal on 07/06/17.
 */
public class MapReduceTrainingDtoRequest implements Serializable {

  private static final long serialVersionUID = -498529077291609146L;

  @NotEmpty
  @NotNull
  private String model;

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }
}
