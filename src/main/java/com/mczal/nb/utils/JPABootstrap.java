package com.mczal.nb.utils;

import com.mczal.nb.service.ErrorRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Created by Gl552 on 2/14/2017.
 */
@Component
public class JPABootstrap implements ApplicationListener<ContextRefreshedEvent> {

  @Autowired
  private ErrorRateService errorRateService;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

//    if (errorRateService.listAll().size() < 2) {
//      errorRateService.resetAll();
//      try {
//        ErrorRate last = new ErrorRate();
//        ErrorRate current = new ErrorRate();
//        current.setType(ErrorType.ACCUMULATIVE);
//        last.setType(ErrorType.LAST);
//        errorRateService.save(current);
//        errorRateService.save(last);
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//    }

  }
}
