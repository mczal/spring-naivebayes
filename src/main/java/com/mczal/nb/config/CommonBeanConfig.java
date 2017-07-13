package com.mczal.nb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by Gl552 on 1/21/2017.
 */
@Configuration
@EnableJpaRepositories("com.mczal.nb.dao")
@EnableJpaAuditing
public class CommonBeanConfig {

  //  FileSystem hdfsFileSystem = FileSystem.get(new URI(HDFS_AUTHORITY), conf);

  private static final String HDFS_AUTHORITY = "HDFS_AUTHORITY";

  @Bean
  public org.apache.hadoop.conf.Configuration createConfig() {
    return new org.apache.hadoop.conf.Configuration();
  }

  //  @Bean
  //  public TomcatEmbeddedServletContainerFactory embeddedServletContainerFactoryTomcat() {
  //    TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory(8080);
  //    return factory;
  //  }

  //  @Bean
  //  public UndertowEmbeddedServletContainerFactory embeddedServletContainerFactory() {
  //    UndertowEmbeddedServletContainerFactory factory =
  //        new UndertowEmbeddedServletContainerFactory();
  //
  //    factory.addBuilderCustomizers(new UndertowBuilderCustomizer() {
  //      @Override
  //      public void customize(io.undertow.Undertow.Builder builder) {
  //        builder.addHttpListener(8080, "127.0.0.1");
  //      }
  //    });
  //
  //    return factory;
  //  }

}
