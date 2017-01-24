package com.mczal.nb.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by Gl552 on 1/21/2017.
 */
@Configuration
@EnableJpaRepositories("com.mczal.nb.dao")
public class CommonBeanConfig {

}
