package com.mczal.nb.controller;

import com.mczal.nb.dto.SingletonQuery;
import com.mczal.nb.dto.TrainFile;
import com.mczal.nb.model.ClassInfo;
import com.mczal.nb.model.ClassInfoDetail;
import com.mczal.nb.model.PredictorInfo;
import com.mczal.nb.service.BayesianModelService;
import com.mczal.nb.service.ClassInfoService;
import com.mczal.nb.service.PredictorInfoService;
import com.mczal.nb.utils.TrainUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Gl552 on 2/11/2017.
 */
@Controller
@RequestMapping(TrainingController.ABSOLTE_PATH)
public class TrainingController {

  public static final String ABSOLTE_PATH = "/admin/train";

  private static final String LAYOUTS_ADMIN = "layouts/admin";

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private BayesianModelService bayesianModelService;

  @Autowired
  private ClassInfoService classInfoService;

  @Autowired
  private PredictorInfoService predictorInfoService;

  @Autowired
  private TrainUtils trainUtils;

  @RequestMapping("")
  public String index(Model model) {
    model.addAttribute("view", "trainings");

    //    singletonQuery.setClassInfos(classInfoService.listAll());
    //    singletonQuery.setPredictorInfo(predictorInfoService.listAll());

    List<ClassInfo> classInfos = classInfoService.listAll();
    List<PredictorInfo> predictorInfos = predictorInfoService.listAll();
    model.addAttribute("classes", classInfos);
    model.addAttribute("predictors", predictorInfos);

    model.addAttribute("singleton", new SingletonQuery());
    model.addAttribute("trainFile", new TrainFile());

    return LAYOUTS_ADMIN;
  }

  @RequestMapping(value = "/files",
      method = RequestMethod.POST)
  public String trainFiles(TrainFile trainFile, RedirectAttributes redirectAttributes) {


    redirectAttributes.addFlashAttribute("success", "Success training NB-C");
    return "redirect:" + ABSOLTE_PATH;
  }

  @RequestMapping(value = "/singleton",
      method = RequestMethod.POST)
  public String trainSingleton(SingletonQuery singletonQuery,
      RedirectAttributes redirectAttributes) {

    List<ClassInfo> classInfos = classInfoService.listAll();

    /**
     * Format :
     * HashMap<ClassName,[Class]ResultValue>
     * */
    HashMap<String, String> resultPerClass = new HashMap<>();
    classInfos.stream().forEach(classInfo -> {
      int acc = 0;
      for (ClassInfoDetail cd : classInfo.getClassInfoDetails()) {
        acc += cd.getCount();
      }
      final int accFinal = acc;

      /**
       * Format
       * K => ClassName,ClassVal
       * V => Result
       * List<ClassName,ClassVal|Result>
       * */
      List<String> allPredRes = new ArrayList<>();
      classInfo.getClassInfoDetails().stream().forEach(classInfoDetail -> {
        logger.info("\n\nMCZAL : FIRST=>");
        double currPredRes = 1.0;
        /**
         * List<String> predictorInfos
         *      Format :
         * Type|PredictorName|PredictorValue
         *
         * */

        for (String s : singletonQuery.getPredictorInfos()) {
          s = s.trim();
          switch (s.split("\\|")[0].trim()) {
            case "DISCRETE":
              Pair<Double, Double> pairRes = trainUtils.calcDiscrete(classInfo, classInfoDetail, s);
//              if (pairRes == null) {
//                continue;
//              }
              double dividend = pairRes.getFirst();
              double divisor = pairRes.getSecond();
              currPredRes *= (dividend) / (divisor);
              logger.info("\nMCZAL: currPredRes => " + currPredRes + "\n");
              break;
            case "NUMERIC":
              double res = trainUtils.calcNormDistEachClass(classInfo, classInfoDetail, s);
              logger.info("\n\nMCZAL : double res =>" + res + "\n-----\n");
              currPredRes *= res;
              logger.info("\n\nMCZAL : currPredRes After doubled =>" + currPredRes + "\n-----\n");
              break;
          }
        }
        currPredRes *= (classInfoDetail.getCount() * 1.0) / (accFinal * 1.0);
        //        * List<ClassName,ClassVal|Result>
        allPredRes
            .add(classInfo.getClassName() + "," + classInfoDetail.getValue() + "|" + currPredRes);
      });
      /**
       * Format :
       * maxS  => ClassName,ClassVal|Result
       * */
      String maxS = "";
      Double checker = 0.0;
      double divisorNorm = 0.0;
      logger.info("\nMCZAL: allPredRes.size(): " + allPredRes.size());
      for (String s : allPredRes) {
        logger.info("\nMCZAL: s from allPredRes => " + s);

        divisorNorm += Double.parseDouble(s.split("\\|")[1]);

        if (Double.parseDouble(s.split("\\|")[1]) > checker) {
          checker = Double.parseDouble(s.split("\\|")[1]);
          maxS = s;
        }
      }
      String maxSNorm = maxS.split(",")[0] + "," + maxS.split(",")[1].split("\\|")[0];
      double resNorm = Double.parseDouble(maxS.split("\\|")[1]);
      resNorm = (resNorm / divisorNorm) * 100.0;
      DecimalFormat df = new DecimalFormat("#.00");
      df.setRoundingMode(RoundingMode.HALF_UP);
      maxSNorm += "|" + df.format(resNorm) + "%";
      logger.info("\nMCZAL: maxS => " + maxS);
      logger.info("\nMCZAL: maxSNorm => " + maxSNorm);
      //    * HashMap<"ClassName","ClassVal|[Class]ResultValue">
      resultPerClass.put(maxSNorm.split(",")[0], maxSNorm.split(",")[1]);
    });
    logger.info("\nMCZAL: resultPerClass => " + resultPerClass.toString());

    redirectAttributes.addFlashAttribute("success", "Success training NB-C");
    return "redirect:" + ErrorRateController.ABSOLUTE_PATH;
  }

}
