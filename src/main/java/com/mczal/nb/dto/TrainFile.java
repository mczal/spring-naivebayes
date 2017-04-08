package com.mczal.nb.dto;

import java.io.Serializable;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Gl552 on 2/11/2017.
 */
public class TrainFile implements Serializable {

  private static final long serialVersionUID = 6380381859860762987L;

  @NotEmpty
  private MultipartFile[] fileTesting;

  @NotEmpty
  private MultipartFile[] fileInfo;

  public MultipartFile[] getFileInfo() {
    return fileInfo;
  }

  public void setFileInfo(MultipartFile[] fileInfo) {
    this.fileInfo = fileInfo;
  }

  public MultipartFile[] getFileTesting() {
    return fileTesting;
  }

  public void setFileTesting(MultipartFile[] fileTesting) {
    this.fileTesting = fileTesting;
  }
}
