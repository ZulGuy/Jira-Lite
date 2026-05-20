package com.studying.backendservice.repositories;

import com.studying.backendservice.models.Tennant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TennantRepository extends JpaRepository<Tennant, Integer> {
  Optional<Tennant> findByName(String name);

}
