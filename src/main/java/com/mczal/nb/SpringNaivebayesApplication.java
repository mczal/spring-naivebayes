package com.mczal.nb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@PropertySource("file:${NAIVE_CONF_DIR}/application.properties")
//@PropertySource(value = "classpath:${NAIVE_CONF_DIR}/application.properties")
//@PropertySource(value = "classpath*:application.properties")
public class SpringNaivebayesApplication
    //    extends SpringBootServletInitializer
{

  public static void main(String[] args) {
    if (args.length == 0) {
      TextAreaOutputStreamWrapper.mainRunner(args);
    }
    SpringApplication.run(SpringNaivebayesApplication.class, args);
  }

  //  @Override
  //  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
  //    return application.sources(SpringNaivebayesApplication.class);
  //  }
}
