package com.mczal.nb.service;

import com.mczal.nb.model.ConfusionMatrixLast;
import java.util.List;

/**
 * Created by mczal on 06/03/17.
 */
public interface ConfusionMatrixLastService extends CRUDService<ConfusionMatrixLast> {

  void deleteAll();

  List<ConfusionMatrixLast> findAllDetails();

  ConfusionMatrixLast findByClassName(String className);

  ConfusionMatrixLast findById(Integer id);

}
