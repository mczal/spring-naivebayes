package com.mczal.nb.controller;

import com.mczal.nb.controller.utils.ConfusionMatrix;
import com.mczal.nb.dto.SingletonQuery;
import com.mczal.nb.dto.TrainFile;
import com.mczal.nb.model.ClassInfo;
import com.mczal.nb.model.ClassInfoDetail;
import com.mczal.nb.model.ErrorRate;
import com.mczal.nb.model.PredictorInfo;
import com.mczal.nb.model.util.ErrorType;
import com.mczal.nb.service.BayesianModelService;
import com.mczal.nb.service.ClassInfoService;
import com.mczal.nb.service.ErrorRateService;
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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Gl552 on 2/11/2017.
 */
@Controller
@RequestMapping(TrainingController.ABSOLTE_PATH)
public class TrainingController {

  public static final String ABSOLTE_PATH = "/admin/testing";

  private static final String LAYOUTS_ADMIN = "layouts/admin";

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private BayesianModelService bayesianModelService;

  @Autowired
  private ClassInfoService classInfoService;

  @Autowired
  private ErrorRateService errorRateService;

  @Autowired
  private PredictorInfoService predictorInfoService;

  @Autowired
  private TrainUtils trainUtils;

  @RequestMapping("")
  public String index(Model model) {
    model.addAttribute("view", "testing");

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
     * <ClassName,ConfusionMatric>
     * */
    HashMap<String, ConfusionMatrix> confusionEachClass = new HashMap<>();
    /**
     * Format :
     * HashMap<ClassName,[Class]ResultValue>
     * */
    HashMap<String, String> resultPerClass = new HashMap<>();
    classInfos.stream().forEach(classInfo -> {
      ConfusionMatrix confusionMatrix = new ConfusionMatrix(classInfo.getClassInfoDetails().size());
      confusionEachClass.put(classInfo.getClassName(), confusionMatrix);
      /**
       * Accumulative Count
       * */
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

      AtomicInteger atomicIntegerConfMatrix = new AtomicInteger(0);
      classInfo.getClassInfoDetails().stream().forEach(classInfoDetail -> {
        confusionMatrix.getInfo()
            .put(classInfoDetail.getValue(), atomicIntegerConfMatrix.getAndIncrement());

        //        logger.info("\n\nMCZAL : FIRST=>");
        double currPredRes = 1.0;
        /**
         * List<String> predictorInfos
         *      Format :
         * Type|PredictorName|PredictorValue
         *
         * */
        int flag = 0;
        outer:
        for (String s : singletonQuery.getPredictorInfos()) {
          s = s.trim();
          switch (s.split("\\|")[0].trim()) {
            case "DISCRETE":
              Pair<Double, Double> pairRes = trainUtils.calcDiscrete(classInfo, classInfoDetail, s);
              if (pairRes == null) {
                flag = 1;
                break outer;
              }
              double dividend = pairRes.getFirst();
              double divisor = pairRes.getSecond();
              currPredRes *= (dividend) / (divisor);
              //              logger.info("\nMCZAL: currPredRes => " + currPredRes + "\n");
              break;
            case "NUMERIC":
              double res = trainUtils.calcNormDistEachClass(classInfo, classInfoDetail, s);
              //              logger.info("\n\nMCZAL : double res =>" + res + "\n-----\n");
              currPredRes *= res;
              //              logger.info("\n\nMCZAL : currPredRes After doubled =>" + currPredRes + "\n-----\n");
              break;
          }
        }
        if (flag == 1) {
          logger.info(
              "Zero-Frequency Problem Occured. Ignore Class Value For: " + classInfo.getClassName()
                  + " -> " + classInfoDetail.getValue());
        } else {
          currPredRes *= (classInfoDetail.getCount() * 1.0) / (accFinal * 1.0);
          //        * List<ClassName,ClassVal|Result>
          allPredRes
              .add(classInfo.getClassName() + "," + classInfoDetail.getValue() + "|" + currPredRes);
        }
      });
      /**
       * Format :
       * maxS  => ClassName,ClassVal|Result
       * */
      String maxS = "";
      Double checker = 0.0;
      double divisorNorm = 0.0;
      //      logger.info("\nMCZAL: allPredRes.size(): " + allPredRes.size());
      //      logger.info(
      //          "\n--------------\nMCZAL: allPredRes.size(): " + allPredRes + "\n--------------\n\n");
      for (String s : allPredRes) {
        //        logger.info("\nMCZAL: s from allPredRes => " + s);
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
      //      logger.info("\nMCZAL: maxS => " + maxS);
      //      logger.info("\nMCZAL: maxSNorm => " + maxSNorm);
      //    * HashMap<"ClassName","ClassVal|[Class]ResultValue">
      resultPerClass.put(maxSNorm.split(",")[0], maxSNorm.split(",")[1]);
    });
    logger.info("\nMCZAL: resultPerClass => " + resultPerClass.toString());
    logger.info("\nMCZAL: singletonQuery => " + singletonQuery.toString());

    /**
     * Apply result to error rate
     * */
    //    logger.info("\n\n" + singletonQuery.toString() + "\n\n");
    logger.info("\nMCZAL: confusionEachClass => \n" + confusionEachClass.toString());
    resultPerClass.entrySet().stream().forEach(resClass -> {
      //    *resClass=> HashMap<"ClassName","ClassVal|[Class]ResultValue">

      ConfusionMatrix confusionMatrix = confusionEachClass.get(resClass.getKey());
      if (confusionMatrix == null) {
        throw new IllegalArgumentException(
            "Confusion Matrix is Null for: resClass.getKey()=" + resClass.getKey() + " "
                + confusionMatrix.toString());
      }

      ErrorRate errorRateAcc = errorRateService.findByErrorType(ErrorType.ACCUMULATIVE);
      ErrorRate errorRateLast = errorRateService.findByErrorType(ErrorType.LAST);
      singletonQuery.getClassInfos().stream()
          .filter(s -> s.split("\\|")[0].trim().equalsIgnoreCase(resClass.getKey()))
          .forEach(classActual -> {

            String actual = classActual.split("\\|")[1].trim();
            String predicted = resClass.getValue().split("\\|")[0].trim();
            if (actual.equalsIgnoreCase(predicted)) {
              logger.info("\nEQUAL Actual => " + actual + " ; Predicted => " + predicted + "\n");
              int index = confusionMatrix.getInfo().get(actual);
              confusionMatrix.getMatrix()[index][index]++;
            } else {
              logger
                  .info("\nNOT EQUAL Actual => " + actual + " ; Predicted => " + predicted + "\n");
              int predIndex = confusionMatrix.getInfo().get(predicted);
              int actIndex = confusionMatrix.getInfo().get(actual);
              confusionMatrix.getMatrix()[actIndex][predIndex]++;
            }
          });
      logger.info("\nConfusionMatrix: \n" + confusionMatrix.stringPrintedMatrix() + "\n");
    });

    redirectAttributes.addFlashAttribute("success", "Success training NB-C");
    return "redirect:" + ErrorRateController.ABSOLUTE_PATH;
  }

}
