package com.mczal.nb.service;

import com.mczal.nb.model.ConfusionMatrixDetail;
import com.mczal.nb.model.ConfusionMatrixLast;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by mczal on 17/03/17.
 */
public interface ConfusionMatrixDetailService {

  Page<ConfusionMatrixDetail> findByConfusionMatrixLastPageable(
      ConfusionMatrixLast confusionMatrixLast, Pageable pageRequest);

}