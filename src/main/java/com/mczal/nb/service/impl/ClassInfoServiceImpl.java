package com.mczal.nb.service.impl;

import com.mczal.nb.dao.ClassInfoDao;
import com.mczal.nb.model.ClassInfo;
import com.mczal.nb.service.ClassInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Gl552 on 2/11/2017.
 */
@Service
@Transactional(readOnly = true)
public class ClassInfoServiceImpl implements ClassInfoService {

  @Autowired
  private ClassInfoDao classInfoDao;

  @Override
  public void delete(String id) throws Exception {

  }

  @Override
  public void delete(Integer id) throws Exception {
    classInfoDao.delete(id);
  }

  @Override
  public ClassInfo findByClassName(String className) {
    return classInfoDao.findByClassName(className);
  }

  @Override
  public ClassInfo findById(String id) throws Exception {
    return null;
  }

  @Override
  public List<ClassInfo> listAll() {
    return classInfoDao.findAll();
  }

  @Override
  public ClassInfo save(ClassInfo domainObject) throws Exception {
    return null;
  }

  @Override
  public ClassInfo update(ClassInfo domainObject) throws Exception {
    return null;
  }
}
