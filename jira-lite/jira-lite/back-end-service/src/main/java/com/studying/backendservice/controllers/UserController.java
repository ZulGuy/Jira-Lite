package com.studying.backendservice.controllers;

import com.studying.backendservice.dto.UserDTO;
import com.studying.backendservice.repositories.UserRepository;
import com.studying.backendservice.services.UserService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public List<UserDTO> search(@RequestParam Optional<String> query) {
    return userService.searchUsers(query.orElse(""));
  }

  @GetMapping("/{id}")
  public UserDTO getById(@PathVariable int id) {
    return userService.getUserById(id);
  }

  @GetMapping("/current")
  public UserDTO getCurrentUser() {
    return userService.getCurrentUser();
  }

  @PreAuthorize("@securityService.haveAdminAccess(principal)")
  @PatchMapping("/{id}/toggle-active")
  public UserDTO toggleActive(@PathVariable int id) {
    UserDTO user = userService.toggleActive(id);
    return user;
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteUser(@PathVariable int id) {
    if (userService.getCurrentUser().getRole().name().equals("ROLE_ADMIN")) {
      return ResponseEntity.status(403).body("Unable to delete administrator");
    }
    userService.delete(id);
    return ResponseEntity.ok("User successfully deleted");
  }

  @PutMapping("/{id}")
  public void updateUser(@PathVariable int id, @RequestBody UserDTO dto) {
    userService.update(dto);
  }

}
