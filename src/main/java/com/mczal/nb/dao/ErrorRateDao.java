package com.mczal.nb.dao;

import com.mczal.nb.model.ErrorRate;
import com.mczal.nb.model.util.ErrorType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Gl552 on 2/12/2017.
 */
public interface ErrorRateDao extends JpaRepository<ErrorRate, Integer> {

  ErrorRate findByType(ErrorType type);

}
