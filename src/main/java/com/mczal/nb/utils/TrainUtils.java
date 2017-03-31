package com.mczal.nb.utils;

import com.mczal.nb.model.BayesianModel;
import com.mczal.nb.model.ClassInfo;
import com.mczal.nb.model.ClassInfoDetail;
import com.mczal.nb.model.util.Type;
import com.mczal.nb.service.BayesianModelService;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

/**
 * Created by Gl552 on 2/13/2017.
 */
@Component
public class TrainUtils {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  //  @Autowired
  //  private PredictorInfoService predictorInfoService;
  @Value("${k.count}")
  private Integer laplacianSmoothingAdder;

  private BayesianModelService bayesianModelService;

  private double calcNormalDist(double mean, double sigma, double x) {
//    double divisor = Math.sqrt(2 * Math.PI * sigma);
//    double pemangkat = ((Math.pow(x - mean, 2)) * -1.0 / (2.0 * Math.pow(sigma, 2)));
//    return (1 / divisor) * (Math.pow(Math.E, pemangkat));

    double divisor = Math.sqrt(2.0 * Math.PI * sigma);
    double powerDividend = Math.pow((x - mean), 2) * -1;
    double powerDivisor = 2.0 * Math.pow(sigma, 2);
    double resPower = powerDividend / powerDivisor;
    double currRes = (1 / divisor) * (Math.pow(Math.E, resPower));
    return currRes;
  }

  public Pair<Double, Double> calcDiscrete(ClassInfo classInfo, ClassInfoDetail classInfoDetail,
      String s) {
    //    logger.info("\nMCZAL: singletonQuey => " + s);
    //    logger.info("\nMczal: method => " + "findByPredictorNameAndPredValAndClassNameAndClassVal(" + s
    //        .split("\\|")[1].trim() + ", " + s.split("\\|")[2].trim() + ", " + classInfo.getClassName()
    //        + ", " + classInfoDetail.getValue() + ")" + "\n");
    String predictorName = s.split("\\|")[1].trim();
    String predictorValue = s.split("\\|")[2].trim();
    BayesianModel bayesianModelDividend = bayesianModelService
        .findByPredictorNameAndPredValAndClassNameAndClassVal(predictorName,
            predictorValue, classInfo.getClassName(), classInfoDetail.getValue());

    int divisor = 0;
    int dividend;
    if (bayesianModelDividend == null) {
      //      logger.info("\n\n GAGILS => Mczal: method => "
      //          + "findByPredictorNameAndPredValAndClassNameAndClassVal(" + s.split("\\|")[1].trim()
      //          + ", " + s.split("\\|")[2].trim() + ", " + classInfo.getClassName() + ", "
      //          + classInfoDetail.getValue() + ")" + "\n\n\n");

      /**
       * Debug per March 31st 2017
       * */
      throw new RuntimeException("\nZero Frequency problem occured for -> \n"
          + "PredictorName: " + predictorName + "\n"
          + "PredictorValue: " + predictorValue + "\n"
          + "ClassName: " + classInfo.getClassName() + "\n"
          + "ClassValue: " + classInfoDetail.getValue() + "\n"
      );
//      dividend = laplacianSmoothingAdder;
      //      return null;
    } else {
      dividend = bayesianModelDividend.getCount();
    }
    Set<BayesianModel> bayesianModelsDivisor = bayesianModelService
        .findByPredictorNameAndClassNameAndClassVal(s.split("\\|")[1].trim(),
            classInfo.getClassName(), classInfoDetail.getValue());
    for (BayesianModel bm : bayesianModelsDivisor) {
      divisor += bm.getCount();
    }
//    logger.info("\nMCZAL : \n divisor: " + divisor + "\ndividend: " + dividend + "\n");
    Pair<Double, Double> result = Pair.of(dividend * 1.0, divisor * 1.0);
    return result;
  }

  /**
   * Format
   *
   * @param in : NUMERIC|<PredictorName>|<Value>
   * @return : String<"ClassName|ClassVal|Value">
   */
  public double calcNormDistEachClass(ClassInfo classInfo, ClassInfoDetail classInfoDetail,
      String in) {
    double x = Double.parseDouble(in.split("\\|")[2].trim());

    BayesianModel bayesianModel = bayesianModelService
        .findByPredictorNameAndClassNameAndClassValAndType(in.split("\\|")[1].trim(),
            classInfo.getClassName().trim(), classInfoDetail.getValue().trim(), Type.NUMERIC);
    //    logger.info(
    //        "\n-----TRAIN UTILS---\n" + "findByPredictorNameAndClassNameAndClassValAndType(" + in
    //            .split("\\|")[1].trim() + ", " + classInfo.getClassName().trim() + ", "
    //            + classInfoDetail.getValue().trim() + ", " + Type.NUMERIC + ")"
    //            + "\n----TRAIN UTILS-----\n");
    if (bayesianModel == null) {
      return -1.0;
    }
    double currSigma = bayesianModel.getSigma().doubleValue();
    double currMean = bayesianModel.getMean().doubleValue();
    double res = calcNormalDist(currMean, currSigma, x);
    return res;
    //    DecimalFormat df = new DecimalFormat("#.00");
    //    df.setRoundingMode(RoundingMode.HALF_UP);
    //    return classInfo.getClassName() + "|" + classInfoDetail.getValue() + "|" + df.format(res);

    //    PredictorInfo predictorInfo =
    //        predictorInfoService.findByPredictorName(in.split("\\|")[1].trim());
    //    if (predictorInfo == null) {
    //      return new ArrayList<String>();
    //    }
    //    List<String> result = new ArrayList<>();
    //    predictorInfo.getPredictorInfoDetails().stream().forEach(predictorInfoDetail -> {
    //      double currSigma = predictorInfoDetail.getSigma().doubleValue();
    //      double currMean = predictorInfoDetail.getMean().doubleValue();
    //      double currRes = calcNormalDist(currMean, currSigma, x);
    //      result.add(
    //          predictorInfoDetail.getClassPriorName() + "|" + predictorInfoDetail.getClassPriorValue()
    //              + "|" + currRes);
    //    });

  }

  @Autowired
  public void setBayesianModelService(BayesianModelService bayesianModelService) {
    this.bayesianModelService = bayesianModelService;
  }

}
