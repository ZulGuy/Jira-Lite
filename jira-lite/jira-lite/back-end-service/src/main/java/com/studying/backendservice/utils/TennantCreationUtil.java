package com.studying.backendservice.utils;

import com.studying.backendservice.dto.TennantDTO;
import com.studying.backendservice.dto.UserDTO;
import com.studying.backendservice.entities.publicentities.Tennant;
import com.studying.backendservice.repositories.publicrepos.PublicTennantRepository;
import com.studying.backendservice.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TennantCreationUtil {

  private final PublicTennantRepository publicTennantRepository;
  private final UserService userService;

  @Autowired
  public TennantCreationUtil(PublicTennantRepository publicTennantRepository, UserService userService) {
    this.publicTennantRepository = publicTennantRepository;
    this.userService = userService;
  }
  @Transactional
  public TennantDTO savePublicMetadata(String name, int adminId) {
    Tennant tennant = new Tennant(name, adminId);
    TennantDTO tennantDTO = toDto(publicTennantRepository.save(tennant));
    UserDTO admin = userService.getUserById(adminId);
    admin.setTennant(name);
    userService.update(admin);
    return tennantDTO;
  }

  @Transactional
  public void copyUserToTenant(UserDTO admin) {
    userService.save(admin);
  }

  @Transactional
  public void compensation(TennantDTO tennantDTO, int adminId) {
    UserDTO admin = userService.getUserById(adminId);
    admin.setTennant("public");
    userService.update(admin);
    publicTennantRepository.deleteById(tennantDTO.getId());

  }

  public TennantDTO toDto(Tennant tennant) {
    TennantDTO dto = new TennantDTO();
    dto.setId(tennant.getId());
    dto.setName(tennant.getName());
    dto.setAdminId(tennant.getAdminId());
    dto.setStatus(tennant.isEnabled());
    return dto;
  }

}
