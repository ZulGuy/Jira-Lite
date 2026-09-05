package com.studying.backendservice.services;

import com.studying.backendservice.configurations.TenantContext;
import com.studying.backendservice.dto.TennantDTO;
import com.studying.backendservice.dto.UserDTO;
import com.studying.backendservice.entities.publicentities.Tennant;
import com.studying.backendservice.repositories.publicrepos.PublicTennantRepository;
import com.studying.backendservice.utils.Role;
import com.studying.backendservice.utils.TennantCreationUtil;
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

  private final JdbcTemplate jdbcTemplate;
  private final PublicTennantRepository publicTennantRepository;
  private final UserServiceImpl userService;
  private final SchemaService schemaService;
  private final TennantCreationUtil tennantCreationUtil;
  private static final Pattern VALID_NAME = Pattern.compile("^[a-z][a-z0-9_]{2,29}$");

  @Autowired
  public TennantServiceImpl(JdbcTemplate jdbcTemplate,
      PublicTennantRepository publicTennantRepository,
      UserServiceImpl userService, TennantCreationUtil tennantCreationUtil,
      SchemaService schemaService) {
    this.jdbcTemplate = jdbcTemplate;
    this.publicTennantRepository = publicTennantRepository;
    this.userService = userService;
    this.tennantCreationUtil = tennantCreationUtil;
    this.schemaService = schemaService;
  }

  @Override
  public List<TennantDTO> getAllTennants() {
    return publicTennantRepository.findAll().stream()
        .filter(t -> !t.getName().equals("public"))
        .map(this::toDto)
        .toList();
  }

  @Override
  public TennantDTO getTennantByName(String name) {
    return publicTennantRepository.findByName(name)
        .map(this::toDto)
        .orElseThrow(() -> new EntityNotFoundException("Tennant not found: " + name));
  }

  @Override
  public TennantDTO createTennant(String name, int adminId) throws AccessDeniedException {
    if (!userService.getPublicCurrentUser().getTennant().equals("public")
        && userService.getPublicCurrentUser().getTennant() != null) {
      throw new AccessDeniedException("You are not allowed to create tennant");
    }
    validateName(name);
    createSchema(name);

    TennantDTO tennantDTO = tennantCreationUtil.savePublicMetadata(name, adminId);
    UserDTO admin = userService.getPublicUserById(adminId);
    admin.setRole(Role.ROLE_ADMIN);
    TenantContext.setTenantId(name);
    try {
      tennantCreationUtil.copyUserToTenant(admin);
    } catch (Exception e) {
      TenantContext.setTenantId("public");
      tennantCreationUtil.compensation(tennantDTO, adminId);
      jdbcTemplate.execute("DROP SCHEMA IF EXISTS " + name + " CASCADE");
      throw new RuntimeException("Failed to copy user to tenant schema", e);
    } finally {
      TenantContext.setTenantId("public");
    }
    schemaService.saveSchemas();
    return tennantDTO;
  }

  public void createSchema(String name) {
    List<String> statements = loadDdl(name);
    try {
      for (String sql : statements) {
        jdbcTemplate.execute(sql);
      }
    } catch (Exception e) {
      jdbcTemplate.execute("DROP SCHEMA IF EXISTS " + name + " CASCADE");
      throw new RuntimeException("Failed to create tenant schema: " + name, e);
    }
  }


  private void validateName(String name) {
    if (name == null || !VALID_NAME.matcher(name).matches()) {
      throw new IllegalArgumentException("Invalid tenant name: '" + name
          + "'. Use 3-30 lowercase letters, digits, or underscores.");
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
          .filter(s -> !s.isBlank())
          .toList();
    } catch (Exception e) {
      throw new RuntimeException("Cannot load tenant DDL template", e);
    }
  }

  @Override
  @Transactional
  public void deleteTennant(int id) {
    Tennant tennant = publicTennantRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Tennant not found"));
    publicTennantRepository.deleteById(id);
    jdbcTemplate.execute("DROP SCHEMA IF EXISTS " + tennant.getName() + " CASCADE");
    schemaService.saveSchemas();
  }

  @Override
  public TennantDTO toDto(Tennant tennant) {
    TennantDTO dto = new TennantDTO();
    dto.setId(tennant.getId());
    dto.setName(tennant.getName());
    dto.setAdminId(tennant.getAdminId() == null ? 0 : tennant.getAdminId());
    dto.setStatus(tennant.isEnabled());
    return dto;
  }
}
