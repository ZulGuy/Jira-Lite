package com.studying.backendservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class BackEndServiceApplicationTests {


  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:16")
          .withDatabaseName("jiralite")
          .withUsername("test")
          .withPassword("test");

  @DynamicPropertySource
  static void cofigureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.public.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.public.username", postgres::getUsername);
    registry.add("spring.datasource.public.password", postgres::getPassword);

    registry.add("spring.datasource.tenant.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.tenant.username", postgres::getUsername);
    registry.add("spring.datasource.tenant.password", postgres::getPassword);
  }

  @Test
  void contextLoads() {
  }

}
