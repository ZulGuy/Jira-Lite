package com.studying.backendservice.configurations;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PublicDataSourceConfig {

//  @Bean
//  public DataSource publicDataSource() {
//    return DataSourceBuilder.create()
//        .url()
//        .driverClassName()
//        .username().password()
//        .build();
//  }
//
//  @Bean
//  public EntityManagerFactory publicEntityManagerFactory() {
//    EntityManagerFactoryBuilder
//  }

}
