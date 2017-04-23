package com.mczal.nb.controller;

import com.mczal.nb.controller.utils.ConfusionMatrix;
import com.mczal.nb.controller.utils.WrapperTestingResponse;
import com.mczal.nb.dto.RenewModelHdfsFormRequest;
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
import com.mczal.nb.service.ClassInfoDetailService;
import com.mczal.nb.service.ClassInfoService;
import com.mczal.nb.service.ConfusionMatrixLastService;
import com.mczal.nb.service.ErrorRateService;
import com.mczal.nb.service.PredictorInfoService;
import com.mczal.nb.service.hdfs.HdfsService;
import com.mczal.nb.utils.TrainUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//import com.mczal.nb.service.ConfusionMatrixService;

/**
 * Created by Gl552 on 2/11/2017.
 */
@Controller
@RequestMapping(TestingController.ABSOLUTE_PATH)
public class TestingController {

  public static final String ABSOLUTE_PATH = "/admin/testing";

  private static final String LAYOUTS_ADMIN = "layouts/admin";

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private BayesianModelService bayesianModelService;

  @Autowired
  private ClassInfoService classInfoService;

  @Autowired
  private ErrorRateService errorRateService;

  @Autowired
  private ClassInfoDetailService classInfoDetailService;

  @Autowired
  private ConfusionMatrixLastService confusionMatrixLastService;

  @Autowired
  private PredictorInfoService predictorInfoService;

  @Autowired
  private TrainUtils trainUtils;

  @Autowired
  private HdfsService hdfsService;

  private void calculateErrorRate(HashMap<String, ConfusionMatrix> confusionEachClass) {
    confusionEachClass.forEach((className, confusionMatrix) -> {
      ClassInfo classInfo = classInfoService.findByClassName(className);
      if (classInfo == null) {
        throw new RuntimeException("Class Info Is Null On Line 169");
      }

      /**
       * Calculate Accuracy for current class
       * */
      int dividend = 0;
      int divisor = 0;
      for (int i = 0; i < confusionMatrix.getMatrix().length; i++) {
        for (int j = 0; j < confusionMatrix.getMatrix().length; j++) {
          if (i == j) {
            dividend += confusionMatrix.getMatrix()[i][j];
          }
          divisor += confusionMatrix.getMatrix()[i][j];
        }
      }
      String accuracyOperator = dividend + " \\over " + divisor;
      double accuracyResult = (dividend * 1.0) / (divisor * 1.0);
      ErrorRate accuracy = new ErrorRate();
      accuracy.setOperation(accuracyOperator);
      accuracy.setType(ErrorType.ACCURACY);
      accuracy.setResult(accuracyResult);
      classInfo.setAccuracy(accuracy);
      try {
        classInfoService.save(classInfo);
      } catch (Exception e) {
        e.printStackTrace();
      }

      /**
       * Calculate Precision, Recall, Specificity
       * */
      confusionMatrix.getInfo().forEach((classValue, index) -> {
        ClassInfoDetail classInfoDetail = classInfoDetailService
            .findByClassInfoAndValue(classInfo, classValue);
        if (classInfoDetail == null) {
          throw new RuntimeException(
              "Class Info Detail Is Null On Line 105:\n" + classInfo.getClassName() + " - "
                  + classValue);
        }
        int currTP = confusionMatrix.getMatrix()[index][index];
        int currFP = 0;
        int currFN = 0;
        for (int i = 0; i < confusionMatrix.getMatrix().length; i++) {
          if (i != index) {
            currFP += confusionMatrix.getMatrix()[i][index];
            currFN += confusionMatrix.getMatrix()[index][i];
          }
        }
        /**
         * Precision
         * */
        ErrorRate precision = new ErrorRate();
        String precisionOperation = currTP + " \\over " + currTP + " + " + currFP;
        double precisionResult = currTP * 1.0 / (currTP + currFP) * 1.0;
        precision.setType(ErrorType.PRECISION);
        precision.setOperation(precisionOperation);
        precision.setResult(precisionResult);
        precision.setClassInfoDetail(classInfoDetail);
        /**
         * Recall
         * */
        ErrorRate recall = new ErrorRate();
        String recallOperation = currTP + " \\over " + currTP + " + " + currFN;
        double recallResult = currTP * 1.0 / (currTP + currFN) * 1.0;
        recall.setType(ErrorType.RECALL);
        recall.setOperation(recallOperation);
        recall.setResult(recallResult);
        recall.setClassInfoDetail(classInfoDetail);

        /**
         * F Measure
         * */
        ErrorRate fMeasure = new ErrorRate();
        double alpha = (2.0 * precisionResult * recallResult) / (precisionResult + recallResult);
        String fMeasureOperation =
            "{1 \\over { " + String.format("%.2f", alpha) + " {1 \\over P}+(1- " + String
                .format("%.2f", alpha) + " ) {1 \\over R} }}";
        double fMeasureResult = 1 /
            ((alpha * (1 / precisionResult)) + ((1 - alpha) * (1 / recallResult)));
        fMeasure.setType(ErrorType.F_MEASURE);
        fMeasure.setOperation(fMeasureOperation);
        fMeasure.setResult(fMeasureResult);
        fMeasure.setClassInfoDetail(classInfoDetail);

        try {
          errorRateService.save(precision);
          errorRateService.save(recall);
          errorRateService.save(fMeasure);
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    });
  }

  @RequestMapping("")
  public String index(Model model) throws Exception {
    model.addAttribute("view", "testing");
    model.addAttribute("testPathHdfs", new RenewModelHdfsFormRequest());
    model.addAttribute("trainFile", new TrainFile());
    model.addAttribute("availableDirs", hdfsService.listInputDirOnPath());

    return LAYOUTS_ADMIN;
  }

  @RequestMapping(value = "/predict-new-case", method = RequestMethod.GET)
  public String predictNewCase(Model model) {
    model.addAttribute("view", "predict-new-case");
    List<ClassInfo> classInfos = classInfoService.listAll();
    List<PredictorInfo> predictorInfos = predictorInfoService.listAll();
    model.addAttribute("classes", classInfos);
    model.addAttribute("predictors", predictorInfos);

    model.addAttribute("singleton", new SingletonQuery());
    return LAYOUTS_ADMIN;
  }

  @RequestMapping(value = "/files-from-hdfs",
      method = RequestMethod.POST)
  public String trainFileFromHdfs(RenewModelHdfsFormRequest modelHdfs,
      RedirectAttributes redirectAttributes) throws Exception {
    if (modelHdfs == null || modelHdfs.getModelHdfs().equals("null")) {
      redirectAttributes.addFlashAttribute("danger", "Failed to retrieve file from HDFS.");
      return "redirect:" + ABSOLUTE_PATH;
    }
    Pair<List<BufferedReader>, BufferedReader> pair =
        hdfsService.getListOfOutputModelBufferedReaderFromModelHdfs(
            modelHdfs.getModelHdfs());
    if (pair.getFirst() == null || pair.getFirst().size() <= 0) {
      redirectAttributes.addFlashAttribute("danger", "Failed retrive file from HDFS");
      return "redirect:" + ABSOLUTE_PATH;
    }
    return this.trainFilesEncapsuled(pair.getFirst(), pair.getSecond(), redirectAttributes);
  }

  @RequestMapping(value = "/files",
      method = RequestMethod.POST)
  public String trainFiles(TrainFile trainFile, RedirectAttributes redirectAttributes)
      throws Exception {
    BufferedReader br1 = new BufferedReader(
        new InputStreamReader(trainFile.getFileTesting()[0].getInputStream()));
    BufferedReader br2 = new BufferedReader(
        new InputStreamReader(trainFile.getFileInfo()[0].getInputStream()));
    List<BufferedReader> br1s = new ArrayList<>();
    br1s.add(br1);
    return this.trainFilesEncapsuled(br1s, br2, redirectAttributes);
  }

  /**
   * @param br2 Info File
   * @param br1s Array of File Test
   */
  public String trainFilesEncapsuled(List<BufferedReader> br1s, BufferedReader br2,
      RedirectAttributes redirectAttributes) {
    errorRateService.resetAll();
    confusionMatrixLastService.deleteAll();

    HashMap<String, ConfusionMatrix> confusionEachClassz = new HashMap<String, ConfusionMatrix>();
    ArrayList<HashMap<String, String>> resultPerClasses = new ArrayList<HashMap<String, String>>();

//    BufferedReader br2 = new BufferedReader(
//        new InputStreamReader(trainFile.getFiles()[1].getInputStream()));
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
        } else if (s.split(":")[0].contains("class")) {
          /**
           * If Class
           * */
          attributeInfos.put(Integer.parseInt(info[1]), info[0]);
        }
      });
    });
//    logger.info(attributeInfos.toString());
//    BufferedReader br1 =
//        new BufferedReader(new InputStreamReader(trainFile.getFiles()[0].getInputStream()));
    br1s.forEach(br1 -> {
      br1.lines().forEach(s -> {
        SingletonQuery singletonQuery = new SingletonQuery();
        List<String> classInfos = new ArrayList<String>();
        List<String> predictorInfos = new ArrayList<String>();
        String[] in = s.split(",");
        for (int i = 0; i < in.length; i++) {
          String attrInfo = attributeInfos.get(i);
//        logger.info("\nattrInfo:\n" + attrInfo + " : " + attrInfo.split("\\|").length + "\n\n");
          if (attrInfo != null) {
//          throw new IllegalArgumentException("Null for attributeInfos key=" + i);
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

        }
        singletonQuery.setClassInfos(classInfos);
        singletonQuery.setPredictorInfos(predictorInfos);
        this.trainSingleton(null, singletonQuery, redirectAttributes, confusionEachClassz,
            resultPerClasses);
//      logger.info("\nsingletonQuery.toString(): \n" + singletonQuery.toString() + "\n\n");
      });
    });

//    logger.info("\n\nMCZAL:\n" + confusionEachClassz.toString() + "\n\n");
    calculateErrorRate(confusionEachClassz);

    redirectAttributes.addFlashAttribute("success", "Complete test NB-Classifier");
    return "redirect:" + ErrorRateController.ABSOLUTE_PATH;
  }

  @RequestMapping(value = "/singleton",
      method = RequestMethod.POST)
  public String trainSingleton(Model model, SingletonQuery singletonQuery,
      RedirectAttributes redirectAttributes, HashMap<String, ConfusionMatrix> confusionEachClassz,
      ArrayList<HashMap<String, String>> resultPerClasses) {
//    logger.info("MASUK BRO");

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
//          logger.info("\ns = " + s);
          String type = s.split("\\|")[0].trim();
//          logger.info("type=" + type);
          if (type.equalsIgnoreCase("DISCRETE")) {
//            logger.info("Discrete Bro");
            Pair<Double, Double> pairRes = trainUtils.calcDiscrete(classInfo, classInfoDetail, s);
            if (pairRes == null) {
              flag = 1;
              break outer;
            }
            double dividend = pairRes.getFirst();
            double divisor = pairRes.getSecond();
//            logger.info("\n240 dividend:" + dividend + " / divisor:" + divisor
//                + " = currPredRes:" + (dividend / divisor) + " "
//                + "=> currPredResBef " + currPredRes + " "
//                + "=> accCurrPredRes " + currPredRes * (dividend / divisor));
            currPredRes *= (dividend / divisor);
            //              logger.info("\nMCZAL: currPredRes => " + currPredRes + "\n");
          } else if (type.equalsIgnoreCase("NUMERIC") || type.equalsIgnoreCase("NUMERICAL")) {
//            logger.info("Numeric Bro");
            double res = trainUtils.calcNormDistEachClass(classInfo, classInfoDetail, s);
            if (res == -1.0) {
              throw new IllegalArgumentException("RES = " + res);
            }
            //              logger.info("\n\nMCZAL : double res =>" + res + "\n-----\n");
//            logger.info(
//                "\n251 res =>" + res + "currPredRes => " +
//                    currPredRes + "** currPredRes * res = "
//                    + currPredRes * res
//                    + "\n-----\n");
            currPredRes *= res;
          } else {
            throw new IllegalArgumentException("NO SUCH TYPE = " + type);
          }

        }
        if (flag == 1) {
          throw new RuntimeException(
              "261 Zero-Frequency Problem Occured. Laplacian smoothing didn't work for: \n"
                  + classInfo
                  .getClassName()
                  + " -> " + classInfoDetail.getValue());
        } else {
          currPredRes *= (classInfoDetail.getCount() * 1.0) / (accFinal * 1.0);
          //        * List<ClassName,ClassVal|Result>
//          logger.info("\n\n267 MCZAL : currPredRes After finalPred =>" + currPredRes + "\n-----\n");
          allPredRes
              .add(classInfo.getClassName() + "," + classInfoDetail.getValue() + "|" + currPredRes);
//          logger.info("\n\n270 MCZAL : allPredRes just added =>" + allPredRes + "\n-----\n");

        }
      });
      /**
       * Format :
       * maxS  => ClassName,ClassVal|Result
       * */
      String maxS = "";
      double checker = Double.MIN_VALUE;
      double divisorNorm = 0.0;
//      logger.info("\nAllPredRes.size(): " + allPredRes.size());
      for (String s : allPredRes) {
//        logger.info("\nString s  from allPredRes : " + s);
        divisorNorm += Double.parseDouble(s.split("\\|")[1]);
//        logger.info("\n" + Double.parseDouble(s.split("\\|")[1]) + " > " + checker);
        if (Double.parseDouble(s.split("\\|")[1]) > checker) {
          checker = Double.parseDouble(s.split("\\|")[1]);
          maxS = s;
        }
      }
//      logger.info("\nmaxS: " + maxS);
      String maxSNorm = maxS.split(",")[0] + "," + maxS.split(",")[1].split("\\|")[0];
//      logger.info("maxSNorm: " + maxSNorm);
//      logger.info("checker " + checker);
      double resNorm = checker;
      resNorm = (resNorm / divisorNorm) * 100.0;
//      logger.info(
//          "296 (resNorm:" + checker + " / divisorNorm:" + divisorNorm + ") * 100 = resNorm:"
//              + resNorm);
      DecimalFormat df = new DecimalFormat("#.00");
      df.setRoundingMode(RoundingMode.CEILING);
      maxSNorm += "|" + df.format(resNorm) + "%";
//      logger.info("\nmaxSNorm: " + maxSNorm + " \n resNorm: " + resNorm + " \n checker: " + checker
//          + " \n divisorNorm: " + divisorNorm);
      //      logger.info("\nMCZAL: maxS => " + maxS);
      //      logger.info("\nMCZAL: maxSNorm => " + maxSNorm);
      //    * HashMap<"ClassName","ClassVal|[Class]ResultValue">
      resultPerClass.put(maxSNorm.split(",")[0], maxSNorm.split(",")[1]);
    });
//    logger.info("\nMCZAL: resultPerClass => " + resultPerClass.toString());
//    logger.info("\nMCZAL: singletonQuery => " + singletonQuery.toString());

    /**
     * Apply result to error rate
     * */
    //    logger.info("\n\n" + singletonQuery.toString() + "\n\n");
//    logger.info("\n313 MCZAL: confusionEachClass => \n" + confusionEachClass.toString());
    List<WrapperTestingResponse> wrapperTestingResponses = new ArrayList<>();
    resultPerClass.entrySet().stream().forEach(resClass -> {
      WrapperTestingResponse wrapperTestingResponse = new WrapperTestingResponse();
      wrapperTestingResponse.setClassName(resClass.getKey());
      wrapperTestingResponse.setPredicted(resClass.getValue().split("\\|")[0]);
//      logger.info("resClass.getValue(): " + resClass.getValue());
//      logger.info("resClass.getValue(): " + resClass.getValue());
      wrapperTestingResponse.setPercentage(resClass.getValue().split("\\|")[1]);
      //    *resClass=> HashMap<"ClassName","ClassVal|[Class]ResultValue">

      ConfusionMatrix confusionMatrix = confusionEachClass.get(resClass.getKey());
      if (confusionMatrix == null) {
        throw new IllegalArgumentException(
            "Confusion Matrix is Null for: resClass.getKey()=" + resClass.getKey() + " "
                + confusionMatrix.toString());
      }

      singletonQuery.getClassInfos().stream()
          .filter(s -> s.split("\\|")[0].trim().equalsIgnoreCase(resClass.getKey()))
          .forEach(classActual -> {

            String actual = classActual.split("\\|")[1].trim();
            String predicted = resClass.getValue().split("\\|")[0].trim();
            wrapperTestingResponse.setActual(actual);

            /**
             * TODO: ERROR FUCKING RATE
             * */
//            ErrorRate errorRate = errorRateService.findByErrorType(ErrorType.LAST);
            if (actual.equalsIgnoreCase(predicted)) {
//              logger
//                  .info("\n339 EQUAL Actual => " + actual + " ; Predicted => " + predicted + "\n");
              int index = confusionMatrix.getInfo().get(actual);
              confusionMatrix.getMatrix()[index][index]++;
//              errorRate.incrementPositive();
            } else {
//              logger
//                  .info("\nNOT EQUAL Actual => " + actual + " ; Predicted => " + predicted + "\n");
              int predIndex = confusionMatrix.getInfo().get(predicted);
              int actIndex = confusionMatrix.getInfo().get(actual);
              confusionMatrix.getMatrix()[actIndex][predIndex]++;
//              errorRate.incrementNegative();
            }
            try {
//              errorRateService.save(errorRate);
            } catch (Exception e) {
              e.printStackTrace();
            }
          });
      wrapperTestingResponse.setConfusionMatrix(confusionMatrix);
      wrapperTestingResponses.add(wrapperTestingResponse);

      ConfusionMatrixLast confusionMatrixLast = confusionMatrixLastService
          .findByClassName(wrapperTestingResponse.getClassName());
      if (confusionMatrixLast == null) {
        confusionMatrixLast = new ConfusionMatrixLast();
        confusionMatrixLast.setClassName(wrapperTestingResponse.getClassName());
        ClassInfo classInfo = classInfoService
            .findByClassName(wrapperTestingResponse.getClassName());
        if (classInfo == null) {
          throw new RuntimeException("Class Info Null On 461");
        }
        confusionMatrixLast.setClassInfo(classInfo);
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

//      logger.info("\nConfusionMatrix: \n" + confusionMatrix.stringPrintedMatrix() + "\n");

      try {
        confusionMatrixLastService.save(confusionMatrixLast);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    redirectAttributes.addFlashAttribute("success", "Complete test NB-Classifier");
//    return "redirect:" + ErrorRateController.ABSOLUTE_PATH;
    if (model != null) {
//      redirectAttributes.addAttribute("view", "predict-new-case")
//      model.addAttribute("view", "predict-new-case");
      redirectAttributes.addFlashAttribute("results", resultPerClass);
//      model.addAttribute("results", resultPerClasses);
    }
//    logger.info("\n\nsingletonQuery.toString() -> \n"
//        + "" + singletonQuery.toString() + "\n");
//    logger.info("\n\nresultPerClass.toString() -> \n"
//        + "" + resultPerClass.toString() + "\n");
//    return LAYOUTS_ADMIN;
    return "redirect:" + ABSOLUTE_PATH + "/predict-new-case";
  }

}
