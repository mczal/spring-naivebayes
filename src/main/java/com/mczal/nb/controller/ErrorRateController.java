package com.mczal.nb.controller;


import com.mczal.nb.model.ClassInfo;
import com.mczal.nb.model.ConfusionMatrixDetail;
import com.mczal.nb.model.ConfusionMatrixLast;
import com.mczal.nb.service.ClassInfoService;
import com.mczal.nb.service.ConfusionMatrixDetailService;
import com.mczal.nb.service.ConfusionMatrixLastService;
import com.mczal.nb.service.ErrorRateService;
import com.mczal.nb.utils.McnBasePageWrapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created by Gl552 on 2/13/2017.
 */
@Controller
@RequestMapping(ErrorRateController.ABSOLUTE_PATH)
public class ErrorRateController {

  public static final String ABSOLUTE_PATH = "/admin/error-rate";

  private static final String LAYOUTS_ADMIN = "layouts/admin";

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private ErrorRateService errorRateService;

  @Autowired
  private ClassInfoService classInfoService;

  @Autowired
  private ConfusionMatrixLastService confusionMatrixLastService;

  @Autowired
  private ConfusionMatrixDetailService confusionMatrixDetailService;

  @RequestMapping("/detail/{id}")
  public String detail(@PathVariable Integer id, Model model,
      @RequestParam(required = false, defaultValue = "0") Integer page,
      @RequestParam(required = false, defaultValue = "15") Integer size) {
    model.addAttribute("view", "error-rates-detail");

    ConfusionMatrixLast confusionMatrixLast = confusionMatrixLastService.findById(id);
    if (confusionMatrixLast == null) {
      throw new RuntimeException("ConfusionMatrix not available for id=" + id);
    }
    Page<ConfusionMatrixDetail> confusionMatrixDetails = confusionMatrixDetailService
        .findByConfusionMatrixLastPageable(confusionMatrixLast, new PageRequest(page, size));
    model.addAttribute("id", id);
    McnBasePageWrapper<ConfusionMatrixDetail> pageWrapper =
        new McnBasePageWrapper<ConfusionMatrixDetail>(confusionMatrixDetails,
            ABSOLUTE_PATH + "/detail/" + id);

    model.addAttribute("page", pageWrapper);
    return LAYOUTS_ADMIN;
  }

  @RequestMapping({"", "/"})
  public String index(Model model) {
    model.addAttribute("view", "error-rates");

    List<ClassInfo> classInfos = classInfoService.getAllWithErrorDetails();
    model.addAttribute("classInfos", classInfos);

    List<ConfusionMatrixLast> confusionMatrices = confusionMatrixLastService.listAll();
    model.addAttribute("confusionMatrices", confusionMatrices);
//    logger.info(confusionMatrices.toString());
//    logger.info("confusionMatrices.size(): " + confusionMatrices.size());

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
