package com.mczal.nb.utils;

import com.mczal.nb.model.BayesianModel;
import com.mczal.nb.model.ClassInfo;
import com.mczal.nb.model.ClassInfoDetail;
import com.mczal.nb.model.util.Type;
import com.mczal.nb.service.BayesianModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Created by Gl552 on 2/13/2017.
 */
@Component
public class TrainUtils {

  //  @Autowired
  //  private PredictorInfoService predictorInfoService;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private BayesianModelService bayesianModelService;

  public Pair<Double, Double> calcDiscrete(ClassInfo classInfo, ClassInfoDetail classInfoDetail,
      String s) {
    logger.info("\nMCZAL: singletonQuey => " + s);
    logger.info("\nMczal: method => " + "findByPredictorNameAndPredValAndClassNameAndClassVal(" + s
        .split("\\|")[1] + ", " + s.split("\\|")[2] + ", " + classInfo.getClassName() + ", "
        + classInfoDetail.getValue() + ")" + "\n");
    BayesianModel bayesianModelDividend = bayesianModelService
        .findByPredictorNameAndPredValAndClassNameAndClassVal(s.split("\\|")[1], s.split("\\|")[2],
            classInfo.getClassName(), classInfoDetail.getValue());
    if (bayesianModelDividend == null) {
      logger.info("\n\n GAGILS => Mczal: method => "
          + "findByPredictorNameAndPredValAndClassNameAndClassVal(" + s.split("\\|")[1] + ", " + s
          .split("\\|")[2] + ", " + classInfo.getClassName() + ", " + classInfoDetail.getValue()
          + ")" + "\n\n\n");
      return null;
    }
    Set<BayesianModel> bayesianModelsDivisor = bayesianModelService
        .findByPredictorNameAndClassNameAndClassVal(s.split("\\|")[1].trim(),
            classInfo.getClassName(), classInfoDetail.getValue());
    int divisor = 0;
    int dividend = bayesianModelDividend.getCount();
    for (BayesianModel bm : bayesianModelsDivisor) {
      divisor += bm.getCount();
    }
    logger.info("\nMCZAL : \n divisor: " + divisor + "\ndividend: " + dividend + "\n");
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
    logger.info(
        "\n-----TRAIN UTILS---\n" + "findByPredictorNameAndClassNameAndClassValAndType(" + in
            .split("\\|")[1].trim() + ", " + classInfo.getClassName().trim() + ", "
            + classInfoDetail.getValue().trim() + ", " + Type.NUMERIC + ")"
            + "\n----TRAIN UTILS-----\n");
    if (bayesianModel == null)
      return -1.0;
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

  private double calcNormalDist(double mean, double sigma, double x) {
    double divisor = Math.sqrt(2 * Math.PI * sigma);
    double pemangkat = -1.0 * ((Math.pow(x - mean, 2)) / (2 * Math.pow(sigma, 2)));
    return (1 / divisor) * (Math.pow(Math.E, pemangkat));
  }

  @Autowired
  public void setBayesianModelService(BayesianModelService bayesianModelService) {
    this.bayesianModelService = bayesianModelService;
  }

}
