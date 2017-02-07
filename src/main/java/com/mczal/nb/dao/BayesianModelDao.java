package com.mczal.nb.dao;

import com.mczal.nb.model.BayesianModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

/**
 * Created by Gl552 on 1/21/2017.
 */
public interface BayesianModelDao extends JpaRepository<BayesianModel, String> {

//  Set<BayesianModel> findByClassName(String className);
//
//  Set<BayesianModel> findByClassNameAndPredictorName(String className, String predictorName);
//
//  Set<BayesianModel> findByPredictorName(String predictorName);

}
