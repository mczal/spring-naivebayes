package com.mczal.nb.dto;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * Created by Gl552 on 2/11/2017.
 */
public class TrainFile implements Serializable {
  private static final long serialVersionUID = 6380381859860762987L;

  @NotEmpty
  private MultipartFile[] files;

  public MultipartFile[] getFiles() {
    return files;
  }

  public void setFiles(MultipartFile[] files) {
    this.files = files;
  }
}
