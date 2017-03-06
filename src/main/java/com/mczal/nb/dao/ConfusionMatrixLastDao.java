package com.mczal.nb.dao;

import com.mczal.nb.model.ConfusionMatrixLast;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by mczal on 06/03/17.
 */
public interface ConfusionMatrixLastDao extends JpaRepository<ConfusionMatrixLast, Integer> {

  ConfusionMatrixLast findByClassName(String className);
}
