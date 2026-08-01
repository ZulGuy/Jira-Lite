package com.studying.backendservice.repositories.tenantrepos;

import com.studying.backendservice.entities.tenantentities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

}
