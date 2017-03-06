package com.mczal.nb.service.impl;

import com.mczal.nb.dao.ConfusionMatrixLastDao;
import com.mczal.nb.model.ConfusionMatrixLast;
import com.mczal.nb.service.ConfusionMatrixLastService;
import java.util.List;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by mczal on 06/03/17.
 */
@Service
@Transactional(readOnly = true)
public class ConfusionMatrixLastServiceImpl implements ConfusionMatrixLastService {

  @Autowired
  private ConfusionMatrixLastDao confusionMatrixLastDao;

  @Override
  public void delete(String id) throws Exception {

  }

  @Override
  @Transactional(readOnly = false)
  public void delete(Integer id) throws Exception {
    confusionMatrixLastDao.delete(id);
  }

  @Override
  @Transactional(readOnly = false)
  public void deleteAll() {
    confusionMatrixLastDao.findAll().forEach(confusionMatrixLast -> {
      try {
        this.delete(confusionMatrixLast.getId());
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  @Override
  public List<ConfusionMatrixLast> findAllDetails() {
    List<ConfusionMatrixLast> confusionMatrixLasts = confusionMatrixLastDao.findAll();
    confusionMatrixLasts.forEach(confusionMatrixLast -> {
      Hibernate.initialize(confusionMatrixLast.getConfusionMatrixDetails());
    });
    return confusionMatrixLasts;
  }

  @Override
  public ConfusionMatrixLast findByClassName(String className) {
    return confusionMatrixLastDao.findByClassName(className);
  }

  @Override
  public ConfusionMatrixLast findById(String id) throws Exception {
    return null;
  }

  @Override
  public List<ConfusionMatrixLast> listAll() {
    return confusionMatrixLastDao.findAll();
  }

  @Override
  @Transactional(readOnly = false)
  public ConfusionMatrixLast save(ConfusionMatrixLast domainObject) throws Exception {
    return confusionMatrixLastDao.save(domainObject);
  }

  @Override
  @Transactional(readOnly = false)
  public ConfusionMatrixLast update(ConfusionMatrixLast domainObject) throws Exception {
    return this.save(domainObject);
  }
}
