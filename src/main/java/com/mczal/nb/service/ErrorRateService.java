package com.mczal.nb.service;

import com.mczal.nb.model.ErrorRate;
import com.mczal.nb.model.util.ErrorType;

/**
 * Created by Gl552 on 2/12/2017.
 */
public interface ErrorRateService extends CRUDService<ErrorRate> {

  ErrorRate findByErrorType(ErrorType type);

  void resetAll();

}
