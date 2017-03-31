package com.mczal.nb.service.impl;

import com.mczal.nb.dao.BayesianModelDao;
import com.mczal.nb.dao.ClassInfoDao;
import com.mczal.nb.dao.PredictorInfoDao;
import com.mczal.nb.model.BayesianModel;
import com.mczal.nb.model.ClassInfo;
import com.mczal.nb.model.ClassInfoDetail;
import com.mczal.nb.model.PredictorInfo;
import com.mczal.nb.model.PredictorInfoDetail;
import com.mczal.nb.model.util.Type;
import com.mczal.nb.service.BayesianModelService;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Gl552 on 1/21/2017.
 */
@Service
@Transactional(readOnly = true)
public class BayesianModelServiceImpl implements BayesianModelService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private BayesianModelDao bayesianModelDao;

  @Autowired
  private ClassInfoDao classInfoDao;

  @Autowired
  private PredictorInfoDao predictorInfoDao;

  @Value("${k.count}")
  private Integer laplacianSmoothingAdder;

  //  @Transactional(readOnly = false)
  private void laplacianSmoothing() {
    List<ClassInfo> classInfos = classInfoDao.findAll();
    if (classInfos.size() <= 0) {
      throw new RuntimeException("Size of class is equal to zero");
    }
    classInfos.forEach(classInfo -> {
      /**
       * FOR EACH CLASS
       * */
      Set<ClassInfoDetail> classInfoDetails = classInfo.getClassInfoDetails();
      classInfoDetails.forEach(classInfoDetail -> {
        /**
         * FOR EACH CLASS VALUE
         * */
        AtomicInteger totalAdditionForCurrClassDetail = new AtomicInteger(0);

        List<PredictorInfo> predictorInfos = predictorInfoDao.findAll();
        if (predictorInfos.size() <= 0) {
          throw new RuntimeException("Size of predictor is equal to zero");
        }
        predictorInfos.forEach(predictorInfo -> {
          /**
           * FOR EACH PREDICTOR
           * */
          if (predictorInfo.getType() == Type.DISCRETE) {

            Set<PredictorInfoDetail> predictorInfoDetails = predictorInfo.getPredictorInfoDetails();
            predictorInfoDetails.forEach(predictorInfoDetail -> {
              /**
               * FOR EACH PREDICTOR VALUE
               * */
              BayesianModel bayesianModel = bayesianModelDao
                  .findByPredictorNameAndPredValAndClassNameAndClassVal(
                      predictorInfo.getPredictorName(), predictorInfoDetail.getValue(),
                      classInfo.getClassName(), classInfoDetail.getValue());
              if (bayesianModel == null) {
                /**
                 * ZERO FREQ OCCURED
                 * */
                logger.info("Zero frequency occured.");

                /**
                 * HANDLING FOR BAYESIAN MODEL
                 * */
                bayesianModel = new BayesianModel();
                bayesianModel.setClassName(classInfo.getClassName());
                bayesianModel.setClassVal(classInfoDetail.getValue());
                bayesianModel.setCount(laplacianSmoothingAdder);
                bayesianModel.setPredictorName(predictorInfo.getPredictorName());
                bayesianModel.setPredVal(predictorInfoDetail.getValue());
                bayesianModel.setType(Type.DISCRETE);
              } else {
                bayesianModel.setCount(bayesianModel.getCount() + laplacianSmoothingAdder);
              }
              bayesianModelDao.save(bayesianModel);
              /**
               * ------------------_WORK SEPARATOR --------------------
               * HANDLING FOR PREDICTOR INFO DETAIL
               * */
              predictorInfoDetail
                  .setCount(predictorInfoDetail.getCount() + laplacianSmoothingAdder);

              for (int i = 0; i < laplacianSmoothingAdder; i++) {
                totalAdditionForCurrClassDetail.incrementAndGet();
              }
            });
          }
          predictorInfoDao.save(predictorInfo);
        });

        classInfoDetail
            .setCount(classInfoDetail.getCount() + totalAdditionForCurrClassDetail.get());

      });
      classInfoDao.save(classInfo);
    });
  }

  @Override
  @Transactional(readOnly = false)
  public void delete(String id) throws Exception {

  }

  @Override
  @Transactional(readOnly = false)
  public void delete(Integer id) throws Exception {
    bayesianModelDao.delete(id);
  }

  @Override
  public BayesianModel findById(String id) throws Exception {
    return null;
  }

  @Override
  public Set<BayesianModel> findByPredictorNameAndClassNameAndClassVal(String predictorName,
      String className, String classVal) {
    return bayesianModelDao
        .findByPredictorNameAndClassNameAndClassVal(predictorName, className, classVal);
  }

  @Override
  public BayesianModel findByPredictorNameAndClassNameAndClassValAndType(String predictorName,
      String className, String classVal, Type type) {
    Set<BayesianModel> resz =
        this.findByPredictorNameAndClassNameAndClassVal(predictorName, className, classVal);
    //        .findByPredictorNameAndClassNameAndClassValAndType(predictorName, className, classVal,
    //            type);
    //    logger.info("\n\n----TRAIN SERVICE---\n" + resz + "\n---------\n");
    BayesianModel res = null;
    for (Iterator<BayesianModel> itr = resz.iterator(); itr.hasNext(); ) {
      res = itr.next();
    }
    return res;
  }

  @Override
  public BayesianModel findByPredictorNameAndPredValAndClassNameAndClassVal(String predictorName,
      String predVal, String className, String classVal) {
    BayesianModel bayesianModel = bayesianModelDao
        .findByPredictorNameAndPredValAndClassNameAndClassVal(predictorName, predVal, className,
            classVal);
    return bayesianModel;
  }

  @Override
  @Transactional(readOnly = false)
  public void insertNewModel(BufferedReader br) throws IOException {
    //    List<String> predictors = new ArrayList<>();
    //    List<String> classes = new ArrayList<>();
    if (br.ready()) {
      br.lines().forEach(s -> {
        String[] splitter = s.split("\\|");
        //      logger.info("\nMCZAL : "+splitter[splitter.length-1].trim());
        switch (splitter[splitter.length - 1].trim()) {
          case "DISCRETE": {
            String[] splitterInner = s.split("\\|")[0].trim().split(",");
            BayesianModel bayesianModel = new BayesianModel();
            bayesianModel.setPredictorName(splitterInner[0].trim());
            bayesianModel.setPredVal(splitterInner[1].trim());
            bayesianModel.setClassName(splitterInner[2].trim());
            bayesianModel.setClassVal(splitterInner[3].trim());
            int bayesianCount =
                ((int) Double.parseDouble(splitterInner[4].trim()))
//                    + laplacianSmoothingAdder
                ;
            bayesianModel
                .setCount(bayesianCount);
            bayesianModel.setType(Type.DISCRETE);
            bayesianModelDao.save(bayesianModel);
            /**
             * ------ WORK SEPARATOR ------
             * */
            PredictorInfo predictorInfo =
                predictorInfoDao.findByPredictorName(splitterInner[0].trim());
            if (predictorInfo == null) {
              predictorInfo = new PredictorInfo();
              predictorInfo.setPredictorName(splitterInner[0].trim());
              predictorInfo.setType(Type.DISCRETE);
            }
            AtomicBoolean checker = new AtomicBoolean(false);
            predictorInfo.getPredictorInfoDetails().stream().filter(
                predictorInfoDetail -> predictorInfoDetail.getValue()
                    .equals(splitterInner[1].trim())).forEach(predictorInfoDetail -> {
              checker.set(true);
              predictorInfoDetail.setCount(predictorInfoDetail.getCount() + bayesianCount);
            });
            if (!checker.get()) {
              PredictorInfoDetail predictorInfoDetail = new PredictorInfoDetail();
              predictorInfoDetail.setCount(bayesianCount);
              predictorInfoDetail.setValue(splitterInner[1].trim());
              predictorInfoDetail.setPredictorInfo(predictorInfo);
              predictorInfo.getPredictorInfoDetails().add(predictorInfoDetail);
            }
            predictorInfoDao.save(predictorInfo);
            break;
            /**
             * Rev 0.1 match the numeric
             * */
            //            PredictorInfo predictorInfo =
            //                predictorInfoDao.findByPredictorName(splitterInner[0].trim());
            //            if (predictorInfo == null) {
            //              predictorInfo = new PredictorInfo();
            //              predictorInfo.setPredictorName(splitterInner[0].trim());
            //              predictorInfo.setType(Type.DISCRETE);
            //            }
            //            PredictorInfoDetail predictorInfoDetail = new PredictorInfoDetail();
            //            predictorInfoDetail.setClassPriorName(splitterInner[2].trim());
            //            predictorInfoDetail.setClassPriorValue(splitterInner[3].trim());
            //            //            predictorInfoDetail.setMean(new BigDecimal(Double.parseDouble(splitterInfo[0])));
            //            //            predictorInfoDetail.setSigma(new BigDecimal(Double.parseDouble(splitterInfo[1])));
            //            predictorInfoDetail.setValue(splitterInner[1].trim());
            //            predictorInfoDetail.setCount((int) Double.parseDouble(splitterInner[4].trim()));
            //            predictorInfoDetail.setPredictorInfo(predictorInfo);
            //            predictorInfo.getPredictorInfoDetails().add(predictorInfoDetail);
            //            predictorInfoDao.save(predictorInfo);
            //            break;
          }
          case "CLASS": {
            //          logger.info("\nMC : s => "+s);
            String[] splitterInner = s.split("\\|")[0].split(",");
            //          logger.info("\nMC : className => "+splitterInner[0]);
            ClassInfo classInfo = classInfoDao.findByClassName(splitterInner[0].trim());
            if (classInfo == null) {
              classInfo = new ClassInfo();
              classInfo.setClassName(splitterInner[0].trim());
            }
            ClassInfoDetail classInfoDetail = new ClassInfoDetail();
            int bayesianClassCount = (int) Double.parseDouble(splitterInner[2].trim());
            classInfoDetail.setValue(splitterInner[1].trim());
            classInfoDetail.setCount(bayesianClassCount);
            classInfoDetail.setClassInfo(classInfo);
            classInfo.getClassInfoDetails().add(classInfoDetail);
            classInfoDao.save(classInfo);
            break;
          }
          case "NUMERIC": {
            String[] splitterInner = s.split(";");
            String[] splitterMeta = splitterInner[0].split(",");
            BayesianModel bayesianModel = new BayesianModel();
            bayesianModel.setPredictorName(splitterMeta[0].trim());
            bayesianModel.setClassName(splitterMeta[1].trim());
            bayesianModel.setClassVal(splitterMeta[2].trim());
            String[] splitterInfo = splitterInner[1].trim().split("\\|");
            //          logger.info("\nMCZAL: splitterInfo[0]=>" + splitterInfo[0]);
            //          logger.info("\nMCZAL: splitterInfo[1]=>" + splitterInfo[1]);
            bayesianModel.setMean(new BigDecimal(Double.parseDouble(splitterInfo[0].trim())));
            bayesianModel.setSigma(new BigDecimal(Double.parseDouble(splitterInfo[1].trim())));
            bayesianModel.setType(Type.NUMERIC);
            bayesianModelDao.save(bayesianModel);
            /**
             * ------ WORK SEPARATOR ------
             * */
            PredictorInfo predictorInfo = predictorInfoDao.findByPredictorName(splitterMeta[0]);
            if (predictorInfo == null) {
              predictorInfo = new PredictorInfo();
              predictorInfo.setPredictorName(splitterMeta[0].trim());
              predictorInfo.setType(Type.NUMERIC);
            }
            PredictorInfoDetail predictorInfoDetail = new PredictorInfoDetail();
            predictorInfoDetail.setClassPriorName(splitterMeta[1].trim());
            predictorInfoDetail.setClassPriorValue(splitterMeta[2].trim());
            predictorInfoDetail.setMean(new BigDecimal(Double.parseDouble(splitterInfo[0])));
            predictorInfoDetail.setSigma(new BigDecimal(Double.parseDouble(splitterInfo[1])));
            predictorInfoDetail.setPredictorInfo(predictorInfo);
            predictorInfo.getPredictorInfoDetails().add(predictorInfoDetail);
            predictorInfoDao.save(predictorInfo);
            break;
          }
          default:
            throw new IllegalArgumentException("<mczal>UNDEFINED CASE</mczal>");
//            break;
        }
      });
    }
    this.laplacianSmoothing();
  }

  @Override
  public List<BayesianModel> listAll() {
    return bayesianModelDao.findAll();
  }

  @Override
  @Transactional(readOnly = false)
  public BayesianModel save(BayesianModel domainObject) throws Exception {
    return null;
  }

  @Override
  @Transactional(readOnly = false)
  public BayesianModel update(BayesianModel domainObject) throws Exception {
    return null;
  }
}
