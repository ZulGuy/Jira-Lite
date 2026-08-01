package com.studying.backendservice.services;

import com.studying.backendservice.dto.UserDTO;
import com.studying.backendservice.entities.tenantentities.User;
import java.util.List;

public interface UserService {

  List<UserDTO> searchUsers(String query);
  UserDTO getUserById(int id);
  void save(UserDTO user);
  List<UserDTO> getAllUsers();
  void delete(int id);
  void update(UserDTO user);
  UserDTO toDto(User user);
  UserDTO getCurrentUser();
  void register(UserDTO userDto);

  UserDTO toggleActive(int id);

}
