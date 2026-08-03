package com.studying.backendservice.configurations;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.studying.backendservice.repositories.publicrepos",
    entityManagerFactoryRef = "publicEntityManagerFactory",
    transactionManagerRef = "publicTransactionManager"
)
public class PublicDbConfig {

  @Bean
  @ConfigurationProperties("spring.datasource.public")
  public DataSourceProperties publicDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @ConfigurationProperties("spring.datasource.public.hikari")
  public HikariDataSource publicDataSource() {
    return publicDataSourceProperties().initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean publicEntityManagerFactory(
      EntityManagerFactoryBuilder builder) {
    Map<String, Object> properties = new HashMap<>();
    properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
    properties.put("hibernate.hbm2ddl.auto", "none");
    properties.put("hibernate.show_sql", true);
    properties.put("hibernate.format_sql", true);
    return builder
        .dataSource(publicDataSource())
        .packages("com.studying.backendservice.entities.publicentities",
            "com.studying.backendservice.entities.userentity")
        .persistenceUnit("publicPU")
        .properties(properties)
        .build();
  }

  @Bean
  public PlatformTransactionManager publicTransactionManager(
      @Qualifier("publicEntityManagerFactory")
      EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }

}
