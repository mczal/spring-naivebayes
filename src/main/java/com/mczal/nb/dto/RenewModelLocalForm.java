package com.mczal.nb.dto;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * Created by Gl552 on 2/7/2017.
 */
public class RenewModelLocalForm implements Serializable {
  private static final long serialVersionUID = -5175977158367731520L;

  @NotEmpty
  private MultipartFile[] files;

  public MultipartFile[] getFiles() {
    return files;
  }

  public void setFiles(MultipartFile[] files) {
    this.files = files;
  }
}
