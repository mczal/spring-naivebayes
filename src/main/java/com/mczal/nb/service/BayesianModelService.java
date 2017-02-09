package com.mczal.nb.service;

import com.mczal.nb.model.BayesianModel;

import java.util.List;

/**
 * Created by Gl552 on 1/21/2017.
 */
public interface BayesianModelService extends CRUDService<BayesianModel> {

  void insertNewModel(List<String> models);

}
