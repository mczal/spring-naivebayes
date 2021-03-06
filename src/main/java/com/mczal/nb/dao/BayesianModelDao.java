package com.mczal.nb.dao;

import com.mczal.nb.model.BayesianModel;
import com.mczal.nb.model.util.Type;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Gl552 on 1/21/2017.
 */
public interface BayesianModelDao extends JpaRepository<BayesianModel, Integer> {

  //  Set<BayesianModel> findByClassName(String className);
  //
  //  Set<BayesianModel> findByClassNameAndPredictorName(String className, String predictorName);
  //
  //  Set<BayesianModel> findByPredictorName(String predictorName);

  Set<BayesianModel> findByPredictorNameAndClassNameAndClassVal(String predictorName,
      String className, String classVal);

  BayesianModel findByPredictorNameAndClassNameAndClassValAndType(String predictorName,
      String className, String classVal, Type type);

  BayesianModel findByPredictorNameAndPredValAndClassNameAndClassVal(String predictorName,
      String predVal, String className, String classVal);

  Page<BayesianModel> findByType(Type type, Pageable pageRequest);

  List<BayesianModel> findByType(Type type);

}
