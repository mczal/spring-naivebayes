package com.mczal.nb.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Gl552 on 2/11/2017.
 */
@Controller
public class IndexController {

  @Value("${hadoop.help.guides.pdf}")
  private String helpGuidesPdf;

  @RequestMapping(value = "/guides.pdf",
      method = RequestMethod.GET)
  public ResponseEntity<byte[]> getPDF1() throws Exception {

    HttpHeaders headers = new HttpHeaders();

    headers.setContentType(MediaType.parseMediaType("application/pdf"));
    String filename = "help.pdf";

    byte[] bFile = Files.readAllBytes(Paths.get(helpGuidesPdf));

    headers.add("content-disposition", "inline;filename=" + filename);

    headers.setContentDispositionFormData(filename, filename);
    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
    ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(bFile, headers, HttpStatus.OK);
    return response;
  }

  @RequestMapping({"", "/"})
  public String index() {
    return "redirect:/admin/home";
  }

}
