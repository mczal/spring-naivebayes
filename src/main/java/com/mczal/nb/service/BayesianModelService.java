package com.mczal.nb.service;

import com.mczal.nb.model.BayesianModel;
import com.mczal.nb.model.util.Type;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

  Page<BayesianModel> findByType(Type type, Pageable pageRequest) throws Exception;

  List<BayesianModel> findByType(Type type);

  void insertNewModel(BufferedReader br) throws IOException;

}
