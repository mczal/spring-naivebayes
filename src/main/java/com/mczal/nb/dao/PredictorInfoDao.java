package com.mczal.nb.dao;

import com.mczal.nb.model.PredictorInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Gl552 on 2/8/2017.
 */

public interface PredictorInfoDao extends JpaRepository<PredictorInfo, String> {

  PredictorInfo findByPredictorName(String predictorName);

}
