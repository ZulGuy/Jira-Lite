package com.studying.backendservice.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
  @Column(name = "admin_id", nullable = false)
  private int adminId;

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

  public int getAdminId() {
    return adminId;
  }
}
