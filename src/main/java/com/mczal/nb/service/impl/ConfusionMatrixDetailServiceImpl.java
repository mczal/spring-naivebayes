package com.mczal.nb.service.impl;

import com.mczal.nb.dao.ConfusionMatrixDetailDao;
import com.mczal.nb.model.ConfusionMatrixDetail;
import com.mczal.nb.model.ConfusionMatrixLast;
import com.mczal.nb.service.ConfusionMatrixDetailService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by mczal on 17/03/17.
 */
@Service
@Transactional(readOnly = true)
public class ConfusionMatrixDetailServiceImpl implements ConfusionMatrixDetailService {

  @Autowired
  private ConfusionMatrixDetailDao confusionMatrixDetailDao;

  @Override
  public Page<ConfusionMatrixDetail> findByConfusionMatrixLastPageable(
      ConfusionMatrixLast confusionMatrixLast, Pageable pageRequest) {
    return confusionMatrixDetailDao.findByConfusionMatrixLast(confusionMatrixLast, pageRequest);
  }
}
