package com.mczal.nb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
//@PropertySource("file:${NAIVE_CONF_DIR}/application.properties")
//@PropertySource(value = "classpath:${NAIVE_CONF_DIR}/application.properties")
//@PropertySource(value = "classpath*:application.properties")
public class SpringNaivebayesApplication
    extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(SpringNaivebayesApplication.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(SpringNaivebayesApplication.class);
  }
}
