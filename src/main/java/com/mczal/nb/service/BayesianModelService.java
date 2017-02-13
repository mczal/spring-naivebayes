package com.mczal.nb.service;

import com.mczal.nb.model.BayesianModel;
import com.mczal.nb.model.util.Type;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Set;

/**
 * Created by Gl552 on 1/21/2017.
 */
public interface BayesianModelService extends CRUDService<BayesianModel> {

  Set<BayesianModel> findByPredictorNameAndClassNameAndClassVal(String predictorName,
      String className, String classVal);

  BayesianModel findByPredictorNameAndClassNameAndClassValAndType(String predictorName,
      String className, String classVal, Type type);

  BayesianModel findByPredictorNameAndPredValAndClassNameAndClassVal(String predictorName,
      String predVal, String className, String classVal);

  void insertNewModel(BufferedReader br) throws IOException;

}
