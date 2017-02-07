package com.mczal.nb.controller;

import com.mczal.nb.dto.RenewModelLocalForm;
import com.mczal.nb.service.BayesianModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Gl552 on 2/4/2017.
 */
@Controller
@RequestMapping("/admin")
public class HomeController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final static String LAYOUTS_ADMIN = "layouts/admin";

  @Autowired
  private BayesianModelService bayesianModelService;

  @RequestMapping("/home")
  public String index(Model model) {
    model.addAttribute("view", "dashboard");
    return LAYOUTS_ADMIN;
  }

  @RequestMapping("/renew-model")
  public String renewModel(Model model) {
    model.addAttribute("view", "renew-model");
    if (!model.containsAttribute("modelLocal"))
      model.addAttribute("modelLocal", new RenewModelLocalForm());

    return LAYOUTS_ADMIN;
  }

  @RequestMapping(value = "renew-model-local",
      method = RequestMethod.POST)
  public String renewModelLocalPost(Model model, @Valid RenewModelLocalForm modelLocal,
      BindingResult bindingResult, RedirectAttributes redirectAttributes) throws Exception{
    if (bindingResult.hasErrors()) {
      model.addAttribute("modelLocal", modelLocal);
      model.addAttribute("org.springframework.validation.BindingResult.modelLocal", bindingResult);
      return this.renewModel(model);
    }

    File file = new File("D:/Projects/Spring/spring-naivebayes/src/main/resources/"
        +modelLocal.getFiles()[0].getOriginalFilename()+".input");
    /**
     * Reconsider deleting original file
     * */
    if(file.exists()){
      file.delete();
    }

    modelLocal.getFiles()[0].transferTo(file);
    Scanner sc = new Scanner(file);
    List<String> models = new ArrayList<>();
    while(sc.hasNext()){
//      logger.info("MCZAL: FILE => "+sc.nextLine()+"\n\n");
      models.add(sc.nextLine().trim());
    }
    bayesianModelService.insertNewModel(models);
    redirectAttributes.addFlashAttribute("success","Success upload new model of classifier");
    return "redirect:/admin/home";
  }

}
