package com.mczal.nb.config;

import java.io.IOException;
import java.net.URI;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

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

}
