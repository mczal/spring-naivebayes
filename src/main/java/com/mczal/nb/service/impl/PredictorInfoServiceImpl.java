package com.mczal.nb.service.impl;

import com.mczal.nb.dao.PredictorInfoDao;
import com.mczal.nb.model.PredictorInfo;
import com.mczal.nb.service.PredictorInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Gl552 on 2/11/2017.
 */
@Service
@Transactional(readOnly = true)
public class PredictorInfoServiceImpl implements PredictorInfoService {

  @Autowired
  private PredictorInfoDao predictorInfoDao;

  @Override
  public void delete(String id) throws Exception {

  }

  @Override
  public void delete(Integer id) throws Exception {
    predictorInfoDao.delete(id);
  }

  @Override
  public PredictorInfo findById(String id) throws Exception {
    return null;
  }

  @Override
  public PredictorInfo findByPredictorName(String predictorName) {
    return predictorInfoDao.findByPredictorName(predictorName);
  }

  @Override
  public List<PredictorInfo> listAll() {
    return predictorInfoDao.findAll();
  }

  @Override
  public PredictorInfo save(PredictorInfo domainObject) throws Exception {
    return null;
  }

  @Override
  public PredictorInfo update(PredictorInfo domainObject) throws Exception {
    return null;
  }
}
