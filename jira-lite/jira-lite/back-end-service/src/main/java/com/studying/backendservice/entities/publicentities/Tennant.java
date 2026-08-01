package com.studying.backendservice.entities.publicentities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tennants")
public class Tennant {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  @Column(name = "name", nullable = false, unique = true)
  private String name;
  @Column(name = "status", nullable = false)
  private boolean status = true;
  @Column(name = "admin_id")
  private Integer adminId;

  public Tennant(String name, int adminId) {
    this.name = name;
    this.adminId = adminId;
  }

  protected Tennant() {}

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public boolean isEnabled() {
    return status;
  }

  public void setEnabled(boolean status) {
    this.status = status;
  }

  public Integer getAdminId() {
    return adminId;
  }
}
