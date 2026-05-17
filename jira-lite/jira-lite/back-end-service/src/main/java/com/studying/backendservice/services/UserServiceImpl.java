package com.studying.backendservice.services;

import com.studying.backendservice.dto.UserDTO;
import com.studying.backendservice.models.PasswordResetToken;
import com.studying.backendservice.models.User;
import com.studying.backendservice.repositories.PasswordResetTokenRepository;
import com.studying.backendservice.repositories.UserRepository;
import com.studying.backendservice.utils.Role;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder encoder;


  @Autowired
  public UserServiceImpl(UserRepository userRepository, PasswordEncoder encoder) {
    this.userRepository = userRepository;
    this.encoder = encoder;
  }

  @Override
  public List<UserDTO> searchUsers(String query) {
    return userRepository.findByUsernameContainingIgnoreCase(query)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"))
        .stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  @Override
  public UserDTO getUserById(int id) {
    return userRepository.findById(id)
        .map(this::toDto)
        .orElseThrow();
  }

  @Override
  public List<UserDTO> getAllUsers() {
    return userRepository.findAll()
        .stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(int id) {
    userRepository.deleteById(id);
  }

  @Override
  @Transactional
  public void update(UserDTO user) {
    User updatedUser = userRepository.findById(user.getId()).orElseThrow();
    updatedUser.setUsername(user.getName());
    updatedUser.setEmail(user.getEmail());
    updatedUser.setEnabled(user.isActive());
    updatedUser.setRole(user.getRole());
    updatedUser.setTenant(user.getTennant());
    userRepository.save(updatedUser);
  }

  @Override
  public void save(UserDTO user) {
    User userEntity = new User(user.getEmail(), user.getPassword(), user.getName(),
        user.getTennant(), user.getRole());
    userRepository.save(userEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDTO getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    return toDto(user);
  }

  @Override
  public void register(UserDTO userDto) {
    User user = new User(userDto.getEmail(), encoder.encode(userDto.getPassword()),
        userDto.getName(), userDto.getTennant(), Role.ROLE_USER);
    user.setEnabled(true);
    userRepository.save(user);
  }

  @Override
  public UserDTO toDto(User user) {
    return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.isEnabled(), user.getRole(),
        user.getPassword(), user.getTenant());
  }

  @Transactional
  public UserDTO toggleActive(int id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    user.setEnabled(!user.isEnabled());
    userRepository.save(user);
    return toDto(user);
  }
}
