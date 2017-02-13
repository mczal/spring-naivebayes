package com.mczal.nb.service;

import com.mczal.nb.model.PredictorInfo;

/**
 * Created by Gl552 on 2/11/2017.
 */
public interface PredictorInfoService extends CRUDService<PredictorInfo> {

  PredictorInfo findByPredictorName(String predictorName);

}
