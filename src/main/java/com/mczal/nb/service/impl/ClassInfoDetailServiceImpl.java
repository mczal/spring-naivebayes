package com.mczal.nb.service.impl;

import com.mczal.nb.dao.ClassInfoDetailDao;
import com.mczal.nb.model.ClassInfo;
import com.mczal.nb.model.ClassInfoDetail;
import com.mczal.nb.service.ClassInfoDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by mczal on 17/03/17.
 */
@Service
@Transactional(readOnly = true)
public class ClassInfoDetailServiceImpl implements ClassInfoDetailService {

  @Autowired
  private ClassInfoDetailDao classInfoDetailDao;

  @Override
  public ClassInfoDetail findByClassInfoAndValue(ClassInfo classInfo, String value) {
    return classInfoDetailDao.findByClassInfoAndValue(classInfo, value);
  }
}
