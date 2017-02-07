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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
  public BayesianModel findById(String id) throws Exception {
    return null;
  }

  @Override
  @Transactional(readOnly = false)
  public void insertNewModel(List<String> models) {
    List<String> predictors = new ArrayList<>();
    List<String> classes = new ArrayList<>();
    models.stream().forEach(s -> {
      String[] splitter = s.split("\\|");
      //      logger.info("\nMCZAL : "+splitter[splitter.length-1].trim());
      switch (splitter[splitter.length - 1].trim()) {
        case "DISCRETE": {
          String[] splitterInner = s.split("\\|")[0].split(",");
          BayesianModel bayesianModel = new BayesianModel();
          bayesianModel.setPredictorName(splitterInner[0]);
          bayesianModel.setPredVal(splitterInner[1]);
          bayesianModel.setClassName(splitterInner[2]);
          bayesianModel.setClassVal(splitterInner[3]);
          bayesianModel.setCount((int) Double.parseDouble(splitterInner[4]));
          bayesianModel.setType(Type.DISCRETE);
          bayesianModelDao.save(bayesianModel);
          /**
           * ------ WORK SEPARATOR ------
           * */
          PredictorInfo predictorInfo = predictorInfoDao.findByPredictorName(splitterInner[0]);
          if (predictorInfo == null) {
            predictorInfo = new PredictorInfo();
            predictorInfo.setPredictorName(splitterInner[0]);
            predictorInfo.setType(Type.DISCRETE);
          }
          AtomicBoolean checker = new AtomicBoolean(false);
          predictorInfo.getPredictorInfoDetails().stream().filter(
              predictorInfoDetail -> predictorInfoDetail.getValue().equals(splitterInner[1]))
              .forEach(predictorInfoDetail -> {
                checker.set(true);
                predictorInfoDetail.setCount(
                    predictorInfoDetail.getCount() + (int) Double.parseDouble(splitterInner[4]));
              });
          if (!checker.get()) {
            PredictorInfoDetail predictorInfoDetail = new PredictorInfoDetail();
            predictorInfoDetail.setCount((int) Double.parseDouble(splitterInner[4]));
            predictorInfoDetail.setValue(splitterInner[1]);
            predictorInfoDetail.setPredictorInfo(predictorInfo);
            predictorInfo.getPredictorInfoDetails().add(predictorInfoDetail);
          }
          predictorInfoDao.save(predictorInfo);
          break;
        }
        case "CLASS": {
          String[] splitterInner = s.split("\\|")[0].split(",");
          ClassInfo classInfo = classInfoDao.findByClassName(splitterInner[0]);
          if (classInfo == null) {
            classInfo = new ClassInfo();
            classInfo.setClassName(splitterInner[0]);
          }
          ClassInfoDetail classInfoDetail = new ClassInfoDetail();
          classInfoDetail.setValue(splitterInner[1]);
          classInfoDetail.setCount((int) Double.parseDouble(splitterInner[2]));
          classInfoDetail.setClassInfo(classInfo);
          classInfo.getClassInfoDetails().add(classInfoDetail);
          classInfoDao.save(classInfo);
          break;
        }
        case "NUMERIC": {
          String[] splitterInner = s.split(";");
          String[] splitterMeta = splitterInner[0].split(",");
          BayesianModel bayesianModel = new BayesianModel();
          bayesianModel.setPredictorName(splitterMeta[0]);
          bayesianModel.setClassName(splitterMeta[1]);
          bayesianModel.setClassVal(splitterMeta[2]);
          String[] splitterInfo = splitterInner[1].split("\\|");
//          logger.info("\nMCZAL: splitterInfo[0]=>" + splitterInfo[0]);
//          logger.info("\nMCZAL: splitterInfo[1]=>" + splitterInfo[1]);
          bayesianModel.setMean(new BigDecimal(Double.parseDouble(splitterInfo[0])));
          bayesianModel.setSigma(new BigDecimal(Double.parseDouble(splitterInfo[1])));
          bayesianModel.setType(Type.NUMERIC);
          bayesianModelDao.save(bayesianModel);
          /**
           * ------ WORK SEPARATOR ------
           * */
          PredictorInfo predictorInfo = predictorInfoDao.findByPredictorName(splitterMeta[0]);
          if (predictorInfo == null) {
            predictorInfo = new PredictorInfo();
            predictorInfo.setPredictorName(splitterMeta[0]);
            predictorInfo.setType(Type.NUMERIC);
          }
          PredictorInfoDetail predictorInfoDetail = new PredictorInfoDetail();
          predictorInfoDetail.setClassPriorName(splitterMeta[1]);
          predictorInfoDetail.setClassPriorValue(splitterMeta[2]);
          predictorInfoDetail.setMean(new BigDecimal(Double.parseDouble(splitterInfo[0])));
          predictorInfoDetail.setSigma(new BigDecimal(Double.parseDouble(splitterInfo[1])));
          predictorInfoDetail.setPredictorInfo(predictorInfo);
          predictorInfo.getPredictorInfoDetails().add(predictorInfoDetail);
          predictorInfoDao.save(predictorInfo);
          break;
        }
        default:
          break;
      }
    });
  }

  @Override
  public List<BayesianModel> listAll() {
    return null;
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
