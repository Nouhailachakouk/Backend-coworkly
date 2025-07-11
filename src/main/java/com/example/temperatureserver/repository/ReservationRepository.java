package com.example.temperatureserver.repository;

import com.example.temperatureserver.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDate;
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Trouve les réservations dont la date est strictement après la date donnée, triées par date croissante
    List<Reservation> findByDateAfterOrderByDateAsc(String date);
}
