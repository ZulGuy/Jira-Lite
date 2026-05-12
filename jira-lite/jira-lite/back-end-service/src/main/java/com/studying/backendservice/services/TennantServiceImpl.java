package com.studying.backendservice.services;

import com.studying.backendservice.configurations.TenantContext;
import com.studying.backendservice.dto.TennantDTO;
import com.studying.backendservice.dto.UserDTO;
import com.studying.backendservice.models.Tennant;
import com.studying.backendservice.models.User;
import com.studying.backendservice.repositories.TennantRepository;
import com.studying.backendservice.repositories.UserRepository;
import com.studying.backendservice.utils.Role;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class TennantServiceImpl implements TennantService {

  private JdbcTemplate jdbcTemplate;
  private TennantRepository tennantRepository;
  private UserServiceImpl userService;
  private UserRepository userRepository;
  private static final Pattern VALID_NAME = Pattern.compile("^[a-z][a-z0-9_]{2,29}$");

  @Autowired
  public TennantServiceImpl(JdbcTemplate jdbcTemplate, TennantRepository tennantRepository,
      UserServiceImpl userService, UserRepository userRepository) {
    this.jdbcTemplate = jdbcTemplate;
    this.tennantRepository = tennantRepository;
    this.userService = userService;
    this.userRepository = userRepository;
  }

  @Override
  public List<TennantDTO> getAllTennants() {
    return tennantRepository.findAll().stream()
        .map(this::toDto)
        .toList();
  }

  @Override
  public TennantDTO getTennantByName(String name) {
    return toDto(tennantRepository.findByName(name));
  }

  @Override
  @Transactional
  public TennantDTO createTennant(String name, int adminId) throws AccessDeniedException {
    if (userService.getCurrentUser().getTennant() != "public" && userService.getCurrentUser().getTennant() != null) {
      throw new AccessDeniedException("You are not allowed to create tennant");
    }
    validateName(name);

    List<String> statements = loadDdl(name);
    try {
      for (String sql : statements) {
        jdbcTemplate.execute(sql);
      }
    } catch (Exception e) {
      jdbcTemplate.execute("DROP SCHEMA IF EXISTS " + name + " CASCADE");
      throw new RuntimeException("Failed to create tenant schema: " + name, e);
    }

    Tennant tennant = new Tennant();
    tennant.setName(name);
    tennant.setAdminId(adminId);
    TennantDTO tennantDTO = toDto(tennantRepository.save(tennant));
    UserDTO admin = userService.getUserById(adminId);
    admin.setTennant(name);
    userService.save(admin);
    TenantContext.setTenantId(name);
    admin.setRole(Role.ROLE_ADMIN);
    userService.save(admin);
    TenantContext.setTenantId("public");
    return tennantDTO;
  }

  private void validateName(String name) {
    if (name == null || !VALID_NAME.matcher(name).matches()) {
      throw new IllegalArgumentException("Invalid tenant name: '" + name + "'. Use 3-30 lowercase letters, digits, or underscores.");
    }

    if (name.equals("public") || name.startsWith("pg_")) {
      throw new IllegalArgumentException("Tenant name '" + name + "' is reserved.");
    }
  }

  private List<String> loadDdl(String schema) {
    try {
      ClassPathResource resource = new ClassPathResource("db/tenant-template.sql");
      String sql = resource.getContentAsString(StandardCharsets.UTF_8)
          .replace("{schema}", schema);
      return Arrays.stream(sql.split(";"))
          .map(String::trim)
          .filter(s -> s.isBlank())
          .toList();
    } catch (Exception e) {
      throw new RuntimeException("Cannot load tenant DDL template", e);
    }
  }

  @Override
  public void deleteTennant(int id) {
    tennantRepository.deleteById(id);
  }

  @Override
  public TennantDTO toDto(Tennant tennant) {
    TennantDTO dto = new TennantDTO();
    dto.setId(tennant.getId());
    dto.setName(tennant.getName());
    dto.setAdminId(tennant.getAdminId());
    dto.setStatus(tennant.isEnabled());
    return dto;
  }

  @Override
  public Tennant toEntity(TennantDTO dto) {
    Tennant tennant = new Tennant();
    tennant.setId(dto.getId());
    tennant.setName(dto.getName());
    tennant.setEnabled(dto.isStatus());
    tennant.setAdminId(dto.getAdminId());
    return tennant;
  }
}
