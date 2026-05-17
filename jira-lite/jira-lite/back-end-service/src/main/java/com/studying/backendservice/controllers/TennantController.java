package com.studying.backendservice.controllers;

import com.studying.backendservice.dto.TennantDTO;
import com.studying.backendservice.services.TennantService;
import com.studying.backendservice.services.UserService;
import java.nio.file.AccessDeniedException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tennants")
public class TennantController {

  private final TennantService tennantService;
  private final UserService userService;

  @Autowired
  public TennantController(TennantService tennantService, UserService userService) {
    this.tennantService = tennantService;
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<List<TennantDTO>> getAllTennants() {
    return new ResponseEntity<>(tennantService.getAllTennants(), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<TennantDTO> createTennant(String name) {
    try {
      TennantDTO tennantDTO = tennantService.createTennant(name,
          userService.getCurrentUser().getId());
      return new ResponseEntity<>(tennantDTO, HttpStatus.CREATED);
    } catch (AccessDeniedException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  @GetMapping("/{name}")
  public ResponseEntity<TennantDTO> getTennantByName(@PathVariable String name) {
    return new ResponseEntity<>(tennantService.getTennantByName(name), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTennant(@PathVariable int id) {
    tennantService.deleteTennant(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
