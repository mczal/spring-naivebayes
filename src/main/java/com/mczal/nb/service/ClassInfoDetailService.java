package com.mczal.nb.service;

import com.mczal.nb.model.ClassInfo;
import com.mczal.nb.model.ClassInfoDetail;

/**
 * Created by mczal on 17/03/17.
 */
public interface ClassInfoDetailService {

  ClassInfoDetail findByClassInfoAndValue(ClassInfo classInfo,String value);

}
