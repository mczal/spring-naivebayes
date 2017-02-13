package com.mczal.nb.controller;

import com.mczal.nb.service.ErrorRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created by Gl552 on 2/13/2017.
 */
@Controller
@RequestMapping(ErrorRateController.ABSOLUTE_PATH)
public class ErrorRateController {

  public static final String ABSOLUTE_PATH = "/admin/error-rate";

  private static final String LAYOUTS_ADMIN = "layouts/admin";

  @Autowired
  private ErrorRateService errorRateService;

  @RequestMapping({"", "/"})
  public String index(Model model) {
    model.addAttribute("view", "error-rates");
    model.addAttribute("errorRates", errorRateService.listAll());
    return LAYOUTS_ADMIN;
  }

  @RequestMapping(value = "/reset",
      method = RequestMethod.POST)
  public String reset(Model model, RedirectAttributes redirectAttributes) {
    errorRateService.resetAll();
    redirectAttributes.addFlashAttribute("success", "Success reset error rate model");
    return LAYOUTS_ADMIN;
  }

}
