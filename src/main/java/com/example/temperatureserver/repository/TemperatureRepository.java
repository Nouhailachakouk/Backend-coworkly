package com.example.temperatureserver.repository;

import com.example.temperatureserver.model.Temperature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemperatureRepository extends JpaRepository<Temperature, Long> {
}
