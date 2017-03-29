package com.mczal.nb.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by mczal on 08/03/17.
 */
public class InputSetDtoRequest implements Serializable {

  private static final long serialVersionUID = -2619459006974040099L;
  @NotEmpty
  private MultipartFile[] filesInfo;

  @NotEmpty
  private MultipartFile[] filesInput;

  private String modelDir;

  private String modelDirSelect;

  private Integer percentage;

  private List<String> types = new ArrayList<String>();

  private List<String> clazz = new ArrayList<String>();

  public List<String> getClazz() {
    return clazz;
  }

  public void setClazz(List<String> clazz) {
    this.clazz = clazz;
  }

  public MultipartFile[] getFilesInfo() {
    return filesInfo;
  }

  public void setFilesInfo(MultipartFile[] filesInfo) {
    this.filesInfo = filesInfo;
  }

  public MultipartFile[] getFilesInput() {
    return filesInput;
  }

  public void setFilesInput(MultipartFile[] filesInput) {
    this.filesInput = filesInput;
  }

  public String getModelDir() {
    return modelDir;
  }

  public void setModelDir(String modelDir) {
    this.modelDir = modelDir;
  }

  public String getModelDirSelect() {
    return modelDirSelect;
  }

  public void setModelDirSelect(String modelDirSelect) {
    this.modelDirSelect = modelDirSelect;
  }

  public Integer getPercentage() {
    return percentage;
  }

  public void setPercentage(Integer percentage) {
    this.percentage = percentage;
  }

  public List<String> getTypes() {
    return types;
  }

  public void setTypes(List<String> types) {
    this.types = types;
  }
}
