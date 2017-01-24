package com.mczal.nb.service.impl;

import com.mczal.nb.dao.BayesianModelDao;
import com.mczal.nb.model.BayesianModel;
import com.mczal.nb.service.BayesianModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Gl552 on 1/21/2017.
 */
@Service
@Transactional(readOnly = true)
public class BayesianModelServiceImpl implements BayesianModelService {

  @Autowired
  private BayesianModelDao bayesianModelDao;

  @Override
  @Transactional(readOnly = false)
  public void delete(String id) throws Exception {

  }

  @Override
  public BayesianModel findById(String id) throws Exception {
    return null;
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
