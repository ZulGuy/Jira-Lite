package com.studying.backendservice.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SchemaServiceImpl implements SchemaService{

  private final JdbcTemplate jdbcTemplate;
  private final Path SOURCE = Path.of("src/main/resources/schemas.tmp");
  private final Path SOURCE_TXT = Path.of("src/main/resources/schemas.txt");

  @Autowired
  public SchemaServiceImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<String> getSchemas() {
    List<String> schemas;
    try {
      Files.readAllLines(SOURCE_TXT);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void saveSchemas() {
    String sql = "SELECT schema_name FROM information_schema.schemata";
    List<String> schemas = jdbcTemplate.queryForList(sql, String.class);
    try {
      Files.write(SOURCE, schemas);
      log.info("Schemas were written to a .tmp file");
      Files.move(SOURCE, SOURCE_TXT, StandardCopyOption.ATOMIC_MOVE);
      log.info("Schemas were moved from the .tmp file to a .txt file");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
