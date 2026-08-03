package com.studying.backendservice.configurations;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.studying.backendservice.repositories.tenantrepos",
    entityManagerFactoryRef = "tenantEntityManagerFactory",
    transactionManagerRef = "tenantTransactionManager"
)
public class TenantDbConfig {

  @Primary
  @Bean
  @ConfigurationProperties("spring.datasource.tenant")
  public DataSourceProperties tenantDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Primary
  @Bean
  @ConfigurationProperties("spring.datasource.tenant.hikari")
  public HikariDataSource tenantDataSource() {
    return tenantDataSourceProperties().initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  @Primary
  @Bean
  public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(
      EntityManagerFactoryBuilder builder,
      SchemaMultiTenantConnectionProvider schemaMultiTenantConnectionProvider,
      SchemaTenantIdentifierResolver schemaTenantIdentifierResolver) {
    Map<String, Object> properties = new HashMap<>();
    properties.put("hibernate.multiTenancy", "SCHEMA");
    properties.put("hibernate.multi_tenant_connection_provider", schemaMultiTenantConnectionProvider);
    properties.put("hibernate.tenant_identifier_resolver", schemaTenantIdentifierResolver);
    properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
    properties.put("hibernate.hbm2ddl.auto", "none");
    properties.put("hibernate.show_sql", true);
    properties.put("hibernate.format_sql", true);
    return builder
        .dataSource(tenantDataSource())
        .packages("com.studying.backendservice.entities")
        .persistenceUnit("tenantPU")
        .properties(properties)
        .build();
  }

  @Primary
  @Bean
  public PlatformTransactionManager tenantTransactionManager(
      @Qualifier("tenantEntityManagerFactory")
      EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }

}
