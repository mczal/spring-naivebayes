package com.mczal.nb.dao;

import com.mczal.nb.model.ClassInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Gl552 on 2/8/2017.
 */
public interface ClassInfoDao extends JpaRepository<ClassInfo, String> {

  ClassInfo findByClassName(String className);

}
