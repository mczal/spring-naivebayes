package com.mczal.nb.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gl552 on 2/13/2017.
 */
@Configuration
@EnableJpaRepositories("com.mczal.nb.dao")
@EnableJpaAuditing
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.mczal.nb.model"})
public class TestConfig {

  @Bean
  public DataSource dataSource() {
    return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
  }

  @Bean
  LocalContainerEntityManagerFactoryBean entityManagerFactory() throws Exception {
    LocalContainerEntityManagerFactoryBean entityManagerFactory =
        new LocalContainerEntityManagerFactoryBean();
    entityManagerFactory.setDataSource(this.dataSource());
    entityManagerFactory.setJpaVendorAdapter(this.jpaVendorAdapter());
    entityManagerFactory.setPackagesToScan(new String[] {"com.mczal.nb.model"});
    Map<String, String> map = new HashMap<String, String>();
    map.put("hibernate.id.new_generator_mappings", "false");
    entityManagerFactory.setJpaPropertyMap(map);
    return entityManagerFactory;
  }

  @Bean
  HibernateJpaDialect jpaDialect() {
    HibernateJpaDialect jpaDialect = new HibernateJpaDialect();
    return jpaDialect;
  }

  @Bean
  HibernateJpaVendorAdapter jpaVendorAdapter() {
    HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
    jpaVendorAdapter.setDatabase(Database.H2);
    jpaVendorAdapter.setGenerateDdl(true);
    jpaVendorAdapter.setShowSql(true);
    return jpaVendorAdapter;
  }

  @Bean
  JpaTransactionManager transactionManager() throws Exception {
    JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
    jpaTransactionManager.setDataSource(this.dataSource());
    jpaTransactionManager.setJpaDialect(this.jpaDialect());
    return jpaTransactionManager;
  }
}

