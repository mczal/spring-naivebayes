package com.mczal.nb.dao;

import com.mczal.nb.model.PredictorInfo;
import com.mczal.nb.model.util.Type;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Gl552 on 2/8/2017.
 */

public interface PredictorInfoDao extends JpaRepository<PredictorInfo, Integer> {

  PredictorInfo findByPredictorName(String predictorName);

  PredictorInfo findByPredictorNameAndType(String predictorName, Type type);

}
