package com.mczal.nb.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by Gl552 on 1/21/2017.
 */
@Configuration
@EnableJpaRepositories("com.mczal.nb.dao")
@EnableAutoConfiguration
public class CommonBeanConfig {

}
