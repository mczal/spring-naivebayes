package com.mczal.nb.dao;

import com.mczal.nb.model.ConfusionMatrixDetail;
import com.mczal.nb.model.ConfusionMatrixLast;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by mczal on 17/03/17.
 */
public interface ConfusionMatrixDetailDao extends JpaRepository<ConfusionMatrixDetail, Integer> {

  Page<ConfusionMatrixDetail> findByConfusionMatrixLast(
      ConfusionMatrixLast confusionMatrixLast, Pageable pageRequest);
}
