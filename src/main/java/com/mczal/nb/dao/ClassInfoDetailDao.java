package com.mczal.nb.dao;

import com.mczal.nb.model.ClassInfo;
import com.mczal.nb.model.ClassInfoDetail;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by mczal on 17/03/17.
 */
public interface ClassInfoDetailDao extends JpaRepository<ClassInfoDetail,Integer> {

  ClassInfoDetail findByClassInfoAndValue(ClassInfo classInfo,String value);

}
