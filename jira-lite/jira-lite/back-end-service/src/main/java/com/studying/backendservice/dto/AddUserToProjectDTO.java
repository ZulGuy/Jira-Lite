package com.studying.backendservice.dto;

import com.studying.backendservice.utils.ProjectRole;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record AddUserToProjectDTO(@NotNull int userId, @NotNull Set<ProjectRole> roles) {}
