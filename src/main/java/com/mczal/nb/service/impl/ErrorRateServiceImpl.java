package com.mczal.nb.service.impl;

import com.mczal.nb.dao.ErrorRateDao;
import com.mczal.nb.model.ErrorRate;
import com.mczal.nb.model.util.ErrorType;
import com.mczal.nb.service.ErrorRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Gl552 on 2/12/2017.
 */
@Service
@Transactional(readOnly = true)
public class ErrorRateServiceImpl implements ErrorRateService {

  @Autowired
  private ErrorRateDao errorRateDao;

  @Override
  public void delete(String id) throws Exception {

  }

  @Override
  @Transactional(readOnly = false)
  public void delete(Integer id) throws Exception {
    errorRateDao.delete(id);
  }

  @Override
  public ErrorRate findByErrorType(ErrorType type) {
    return errorRateDao.findByType(type);
  }

  @Override
  public ErrorRate findById(String id) throws Exception {
    return null;
  }

  @Override
  public List<ErrorRate> listAll() {
    return errorRateDao.findAll();
  }

  @Override
  @Transactional(readOnly = false)
  public void resetAll() {
    errorRateDao.findAll().forEach(errorRate -> {
      errorRateDao.delete(errorRate);
    });
  }

  @Override
  @Transactional(readOnly = false)
  public ErrorRate save(ErrorRate domainObject) throws Exception {
    return errorRateDao.save(domainObject);
  }

  @Override
  @Transactional(readOnly = false)
  public ErrorRate update(ErrorRate domainObject) throws Exception {
    return errorRateDao.save(domainObject);
  }
}
