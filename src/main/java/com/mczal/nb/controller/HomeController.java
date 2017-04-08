package com.mczal.nb.controller;

import com.mczal.nb.dto.RenewModelHdfsFormRequest;
import com.mczal.nb.dto.RenewModelLocalFormRequest;
import com.mczal.nb.model.BayesianModel;
import com.mczal.nb.model.util.Type;
import com.mczal.nb.service.BayesianModelService;
import com.mczal.nb.service.ClassInfoService;
import com.mczal.nb.service.ConfusionMatrixLastService;
import com.mczal.nb.service.ErrorRateService;
import com.mczal.nb.service.PredictorInfoService;
import com.mczal.nb.service.hdfs.HdfsService;
import com.mczal.nb.utils.McnBasePageWrapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created by Gl552 on 2/4/2017.
 */
@Controller
@RequestMapping("/admin")
public class HomeController {

  private final static String LAYOUTS_ADMIN = "layouts/admin";
  private static final String DEFAULT_PAGE_SIZE = "10";
  private static final String DEFAULT_PAGE_NUMBER = "0";
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private BayesianModelService bayesianModelService;

  @Autowired
  private ClassInfoService classInfoService;

  @Autowired
  private PredictorInfoService predictorInfoService;

  @Autowired
  private ConfusionMatrixLastService confusionMatrixLastService;

  @Autowired
  private ErrorRateService errorRateService;

  @Autowired
  private HdfsService hdfsService;

  private Boolean cleanAll() {
    AtomicBoolean result = new AtomicBoolean(true);
    bayesianModelService.listAll().stream().forEach(bayesianModel -> {
      try {
        bayesianModelService.delete(bayesianModel.getId());
      } catch (Exception e) {
        e.printStackTrace();
//        redirectAttributes.addFlashAttribute("warning", "Failed renew NB-C model");
        result.set(false);
      }
    });
    classInfoService.listAll().stream().forEach(classInfo -> {
      try {
        classInfoService.delete(classInfo.getId());
      } catch (Exception e) {
        e.printStackTrace();
//        redirectAttributes.addFlashAttribute("warning", "Failed renew NB-C model");
        result.set(false);
      }
    });
    predictorInfoService.listAll().stream().forEach(predictorInfo -> {
      try {
        predictorInfoService.delete(predictorInfo.getId());
      } catch (Exception e) {
        e.printStackTrace();
//        redirectAttributes.addFlashAttribute("warning", "Failed renew NB-C model");
        result.set(false);
      }
    });
    confusionMatrixLastService.listAll().stream().forEach(confusionMatrixLast -> {
      try {
        confusionMatrixLastService.delete(confusionMatrixLast.getId());
      } catch (Exception e) {
        e.printStackTrace();
//        redirectAttributes.addFlashAttribute("warning", "Failed renew NB-C model");
        result.set(false);
      }
    });
    errorRateService.listAll().stream().forEach(errorRate -> {
      try {
        errorRateService.delete(errorRate.getId());
      } catch (Exception e) {
        e.printStackTrace();
//        redirectAttributes.addFlashAttribute("warning", "Failed renew NB-C model");
        result.set(false);
      }
    });
    return result.get();
  }

  @RequestMapping("/home")
  public String index(Model model,
      @RequestParam(required = false, defaultValue = DEFAULT_PAGE_NUMBER) Integer page,
      @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer size)
      throws Exception {
    model.addAttribute("view", "dashboard");
    model.addAttribute("classes", classInfoService.listAll());
    model.addAttribute("predictors", predictorInfoService.listAll());
//    List<BayesianModel> bayesianModels = bayesianModelService.listAll();

    Page<BayesianModel> discreteModels = bayesianModelService
        .findByType(Type.DISCRETE, new PageRequest(page, size));
    List<BayesianModel> numericModels = bayesianModelService.findByType(Type.NUMERIC);

    McnBasePageWrapper<BayesianModel> pageWrapper = new McnBasePageWrapper<>(discreteModels,
        "/admin/home");
    model.addAttribute("page", pageWrapper);

    //    bayesianModels.stream().forEach(bayesianModel -> {
//      switch (bayesianModel.getType()) {
//        case DISCRETE:
//          discreteModels.add(bayesianModel);
//          break;
//        case NUMERIC:
//          numericModels.add(bayesianModel);
//          break;
//        default:
//          break;
//      }
//    });
//    model.addAttribute("discreteModels", discreteModels);
    model.addAttribute("numericModels", numericModels);

    return LAYOUTS_ADMIN;
  }

  @RequestMapping("/renew-model")
  public String renewModel(Model model) throws Exception {
    model.addAttribute("view", "renew-modelz");
    if (!model.containsAttribute("modelLocal")) {
      model.addAttribute("modelLocal", new RenewModelLocalFormRequest());
    }
    if (!model.containsAttribute("modelHdfs")) {
      model.addAttribute("modelHdfs", new RenewModelHdfsFormRequest());
    }
    model.addAttribute("availableDirs", hdfsService.listInputDirOnPath());

    return LAYOUTS_ADMIN;
  }

  @RequestMapping(value = "/renew-model-hdfs", method = RequestMethod.POST)
  public String renewModelHdfsPost(RenewModelHdfsFormRequest modelHdfs,
      RedirectAttributes redirectAttributes) throws Exception {
    if (modelHdfs.getModelHdfs() == null || modelHdfs.getModelHdfs().equals("NULL")) {
      redirectAttributes.addFlashAttribute("danger",
          "FAILED TO RENEW MODEL FROM HDFS. VALIDATION CONSTRAINT FAILURE OCCURED");
      return "redirect:/admin/home/renew-model";
    }
    if (!cleanAll()) {
      redirectAttributes.addFlashAttribute("warning", "Failed renew NB-C model");
      return "redirect:/admin/home/renew-model";
    }

//    logger.info("\n\nmodelHdfs.getModelHdfs: " + modelHdfs.getModelHdfs() + "\n");
    BufferedReader br = hdfsService
        .getOutputModelBufferedReaderFromModelHdfs(modelHdfs.getModelHdfs());
    if (br == null) {
//      logger.info("%%HIT THIS%%");
      redirectAttributes.addFlashAttribute("danger", "Failed renew NB-C model");
      return "redirect:/admin/home/renew-model";
    }
    bayesianModelService.insertNewModel(br);

    redirectAttributes
        .addFlashAttribute("success", "Success upload new model of classifier from hdfs");
    return "redirect:/admin/home";
  }

  @RequestMapping(value = "renew-model-local",
      method = RequestMethod.POST)
  public String renewModelLocalPost(Model model, @Valid RenewModelLocalFormRequest modelLocal,
      BindingResult bindingResult, RedirectAttributes redirectAttributes) throws Exception {
    if (bindingResult.hasErrors()) {
      model.addAttribute("modelLocal", modelLocal);
      model.addAttribute("org.springframework.validation.BindingResult.modelLocal", bindingResult);
      return this.renewModel(model);
    }
    if (!cleanAll()) {
      redirectAttributes.addFlashAttribute("warning", "Failed renew NB-C model");
      return "redirec:/admin/home/renew-model";
    }

    BufferedReader br =
        new BufferedReader(new InputStreamReader(modelLocal.getFiles()[0].getInputStream()));

    bayesianModelService.insertNewModel(br);
    redirectAttributes
        .addFlashAttribute("success", "Success upload new model of classifier from local");
    return "redirect:/admin/home";
  }
}
