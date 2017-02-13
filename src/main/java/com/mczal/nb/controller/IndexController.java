package com.mczal.nb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Gl552 on 2/11/2017.
 */
@Controller
public class IndexController {

  @RequestMapping({"","/"})
  public String index(){
    return "redirect:/admin/home";
  }

}
