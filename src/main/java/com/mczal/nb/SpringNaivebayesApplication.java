package com.mczal.nb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("file:${CONF_DIR}/spring-naivebayes/application.properties")
//@PropertySource(value = "classpath:${NAIVE_CONF_DIR}/application.properties")
//@PropertySource(value = "classpath*:application.properties")
public class SpringNaivebayesApplication extends SpringBootServletInitializer {
  public static void main(String[] args) {
    SpringApplication.run(SpringNaivebayesApplication.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(SpringNaivebayesApplication.class);
  }
}
