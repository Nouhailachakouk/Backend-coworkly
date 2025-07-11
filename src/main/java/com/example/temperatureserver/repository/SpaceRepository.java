package com.example.temperatureserver.repository;

import com.example.temperatureserver.model.Space;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpaceRepository extends JpaRepository<Space, Long> {
    Optional<Space> findByTypeAndIsActiveTrue(String type);
}
