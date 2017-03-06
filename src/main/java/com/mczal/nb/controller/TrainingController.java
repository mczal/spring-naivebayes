package com.mczal.nb.controller;

import com.mczal.nb.controller.utils.ConfusionMatrix;
import com.mczal.nb.controller.utils.WrapperTestingResponse;
import com.mczal.nb.dto.SingletonQuery;
import com.mczal.nb.dto.TrainFile;
import com.mczal.nb.model.ClassInfo;
import com.mczal.nb.model.ClassInfoDetail;
import com.mczal.nb.model.ConfusionMatrixDetail;
import com.mczal.nb.model.ConfusionMatrixLast;
import com.mczal.nb.model.ErrorRate;
import com.mczal.nb.model.PredictorInfo;
import com.mczal.nb.model.util.ErrorType;
import com.mczal.nb.service.BayesianModelService;
import com.mczal.nb.service.ClassInfoService;
import com.mczal.nb.service.ConfusionMatrixLastService;
//import com.mczal.nb.service.ConfusionMatrixService;
import com.mczal.nb.service.ErrorRateService;
import com.mczal.nb.service.PredictorInfoService;
import com.mczal.nb.utils.TrainUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
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

//  @Autowired
//  private ConfusionMatrixService confusionMatrixService;

  @Autowired
  private ConfusionMatrixLastService confusionMatrixLastService;

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
  public String trainFiles(TrainFile trainFile, RedirectAttributes redirectAttributes)
      throws Exception {

    confusionMatrixLastService.deleteAll();

    HashMap<String, ConfusionMatrix> confusionEachClassz = new HashMap<String, ConfusionMatrix>();
    ArrayList<HashMap<String, String>> resultPerClasses = new ArrayList<HashMap<String, String>>();

    BufferedReader br2 = new BufferedReader(
        new InputStreamReader(trainFile.getFiles()[1].getInputStream()));
    /**
     * attributeInfo = [Index,ClassName|Type]
     * */
    HashMap<Integer, String> attributeInfos = new HashMap<Integer, String>();
    br2.lines().forEach(s -> {
      Arrays.stream(s.split(":")[1].split(";")).forEach(s1 -> {
        String[] info = s1.split(",");
        if (s.split(":")[0].contains("attribute")) {
          /**
           * If Predictor
           * */
          attributeInfos.put(Integer.parseInt(info[1]), info[2] + "|" + info[0]);
        } else {
          /**
           * If Class
           * */
          attributeInfos.put(Integer.parseInt(info[1]), info[0]);
        }
      });
    });
    logger.info(attributeInfos.toString());
    BufferedReader br1 =
        new BufferedReader(new InputStreamReader(trainFile.getFiles()[0].getInputStream()));
    br1.lines().forEach(s -> {
      SingletonQuery singletonQuery = new SingletonQuery();
      List<String> classInfos = new ArrayList<String>();
      List<String> predictorInfos = new ArrayList<String>();
      String[] in = s.split(",");
      for (int i = 0; i < in.length; i++) {
        String attrInfo = attributeInfos.get(i);
        logger.info("\nattrInfo:\n" + attrInfo + " : " + attrInfo.split("\\|").length + "\n\n");
        if (attrInfo == null) {
          throw new IllegalArgumentException("Null for attributeInfos key=" + i);
        }
        if (attrInfo.split("\\|").length == 1) {
          /**
           * If Class
           * */
          classInfos.add(attrInfo + "|" + in[i]);
        } else {
          /**
           * If Predictor
           * */
          predictorInfos.add(attrInfo + "|" + in[i]);
        }
      }
      singletonQuery.setClassInfos(classInfos);
      singletonQuery.setPredictorInfos(predictorInfos);
      this.trainSingleton(singletonQuery, redirectAttributes, confusionEachClassz,
          resultPerClasses);
      //      logger.info("\n\n" + singletonQuery.toString() + "\n\n");
    });

    redirectAttributes.addFlashAttribute("success", "Success training NB-C");
    return "redirect:" + ABSOLTE_PATH;
  }

  @RequestMapping(value = "/singleton",
      method = RequestMethod.POST)
  public String trainSingleton(SingletonQuery singletonQuery,
      RedirectAttributes redirectAttributes, HashMap<String, ConfusionMatrix> confusionEachClassz,
      ArrayList<HashMap<String, String>> resultPerClasses) {

    List<ClassInfo> classInfos = classInfoService.listAll();

    /**
     * <ClassName,ConfusionMatric>
     * */
    final HashMap<String, ConfusionMatrix> confusionEachClass;
    if (confusionEachClassz == null) {
      confusionMatrixLastService.deleteAll();
      confusionEachClass = new HashMap<String, ConfusionMatrix>();
    } else {
      confusionEachClass = confusionEachClassz;
    }
//    HashMap<String, ConfusionMatrix> confusionEachClass = new HashMap<>();

    if (resultPerClasses == null) {
      resultPerClasses = new ArrayList<HashMap<String, String>>();
    }

    /**
     * Format :
     * HashMap<ClassName,[Class]ResultValue>
     * */
    HashMap<String, String> resultPerClass = new HashMap<>();
    resultPerClasses.add(resultPerClass);
    classInfos.stream().forEach(classInfo -> {

      ConfusionMatrix confusionMatrix = new ConfusionMatrix(classInfo.getClassInfoDetails().size());
      confusionEachClass.putIfAbsent(classInfo.getClassName(), confusionMatrix);
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
            .putIfAbsent(classInfoDetail.getValue(), atomicIntegerConfMatrix.getAndIncrement());

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
    List<WrapperTestingResponse> wrapperTestingResponses = new ArrayList<>();
    resultPerClass.entrySet().stream().forEach(resClass -> {
      WrapperTestingResponse wrapperTestingResponse = new WrapperTestingResponse();
      wrapperTestingResponse.setClassName(resClass.getKey());
      wrapperTestingResponse.setPredicted(resClass.getValue().split("\\|")[0]);
      wrapperTestingResponse.setPercentage(resClass.getValue().split("\\|")[1]);
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
            wrapperTestingResponse.setActual(actual);
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
      wrapperTestingResponse.setConfusionMatrix(confusionMatrix);
      wrapperTestingResponses.add(wrapperTestingResponse);

      ConfusionMatrixLast confusionMatrixLast = confusionMatrixLastService
          .findByClassName(wrapperTestingResponse.getClassName());
      if (confusionMatrixLast == null) {
        confusionMatrixLast = new ConfusionMatrixLast();
        confusionMatrixLast.setClassName(wrapperTestingResponse.getClassName());
      }
//      ConfusionMatrixLast confusionMatrixLast = new ConfusionMatrixLast();
      ConfusionMatrixDetail confusionMatrixDetail = new ConfusionMatrixDetail();
      confusionMatrixDetail.setPercentage(wrapperTestingResponse.getPercentage());
      confusionMatrixDetail.setActual(wrapperTestingResponse.getActual());
      confusionMatrixDetail.setPredicted(wrapperTestingResponse.getPredicted());
      confusionMatrixDetail.setConfusionMatrixLast(confusionMatrixLast);
      confusionMatrixLast
          .setPrintedConfusionMatrix(wrapperTestingResponse.getPrintedConfusionMatrix());
      confusionMatrixLast.getConfusionMatrixDetails().add(confusionMatrixDetail);
//      com.mczal.nb.model.ConfusionMatrix confusionMatrixModel = confusionMatrixService
//          .findByClassName(wrapperTestingResponse.getClassName());
//      if (confusionMatrixModel == null) {
//        confusionMatrixModel = new com.mczal.nb.model.ConfusionMatrix();
//      }
//      confusionMatrixModel.setPercentage(wrapperTestingResponse.getPercentage());
//      confusionMatrixModel.setActual(wrapperTestingResponse.getActual());
//      confusionMatrixModel.setClassName(wrapperTestingResponse.getClassName());
//      confusionMatrixModel.setPredicted(wrapperTestingResponse.getPredicted());
//      confusionMatrixModel
//          .setPrintedConfusionMatrix(wrapperTestingResponse.getPrintedConfusionMatrix());

      logger.info("\nConfusionMatrix: \n" + confusionMatrix.stringPrintedMatrix() + "\n");

      try {
        confusionMatrixLastService.save(confusionMatrixLast);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    redirectAttributes.addFlashAttribute("success", "Complete test NB-Classifier");
    return "redirect:" + ErrorRateController.ABSOLUTE_PATH;
  }

}
