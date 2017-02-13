package com.mczal.nb.dao;

import com.mczal.nb.model.BayesianModel;
import com.mczal.nb.model.util.Type;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Created by Gl552 on 2/13/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class})
@Transactional(readOnly = false)
public class TestBayesianModelDao {

  @Autowired
  private BayesianModelDao dao;

  @Before
  public void setup() {
    BayesianModel bayesianModel = new BayesianModel();
    bayesianModel.setClassName("Play");
    bayesianModel.setClassVal("No");
    bayesianModel.setMean(new BigDecimal(6.08));
    bayesianModel.setPredictorName("Rand");
    bayesianModel.setSigma(new BigDecimal(4.10));
    bayesianModel.setType(Type.NUMERIC);
    dao.save(bayesianModel);
  }

  @Test
  public void testFindByPredictorNameAndClassNameAndClassValAndType() {
    //    BayesianModel findByPredictorNameAndClassNameAndClassValAndType(String predictorName,
    //        String className, String classVal, Type type);
    BayesianModel bayesianModel =
        dao.findByPredictorNameAndClassNameAndClassValAndType("Randx", "Play", "No", Type.NUMERIC);
    assert bayesianModel != null;
    Assert.assertEquals("Play", bayesianModel.getClassName());
  }

}
