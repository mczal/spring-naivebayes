package com.mczal.nb.service.impl;

import com.mczal.nb.dao.BayesianModelDao;
import com.mczal.nb.dao.ClassInfoDao;
import com.mczal.nb.dao.PredictorInfoDao;
import com.mczal.nb.model.*;
import com.mczal.nb.model.util.Type;
import com.mczal.nb.service.BayesianModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

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
            bayesianModel.setCount((int) Double.parseDouble(splitterInner[4].trim()));
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
              predictorInfoDetail.setCount(predictorInfoDetail.getCount() + (int) Double
                  .parseDouble(splitterInner[4].trim()));
            });
            if (!checker.get()) {
              PredictorInfoDetail predictorInfoDetail = new PredictorInfoDetail();
              predictorInfoDetail.setCount((int) Double.parseDouble(splitterInner[4].trim()));
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
            classInfoDetail.setValue(splitterInner[1].trim());
            classInfoDetail.setCount((int) Double.parseDouble(splitterInner[2].trim()));
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
