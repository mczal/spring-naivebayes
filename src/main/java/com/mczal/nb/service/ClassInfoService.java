package com.mczal.nb.service;

import com.mczal.nb.model.ClassInfo;
import java.util.List;

/**
 * Created by Gl552 on 2/11/2017.
 */
public interface ClassInfoService extends CRUDService<ClassInfo> {

  ClassInfo findByClassName(String className);

  List<ClassInfo> getAllWithErrorDetails();

}
