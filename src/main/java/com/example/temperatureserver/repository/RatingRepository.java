package com.example.temperatureserver.repository;

import com.example.temperatureserver.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}