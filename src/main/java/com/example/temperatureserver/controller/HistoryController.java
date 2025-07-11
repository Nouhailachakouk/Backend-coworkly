package com.example.temperatureserver.controller;

import com.example.temperatureserver.model.Reservation;
import com.example.temperatureserver.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/history")
@CrossOrigin(origins = "*")
public class HistoryController {

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping
    public ResponseEntity<?> getAllReservations() {
        try {
            List<Reservation> allReservations = reservationRepository.findAll();

            LocalDate today = LocalDate.now();

            // Construction de la liste enrichie
            List<Map<String, Object>> result = new ArrayList<>();

            for (Reservation res : allReservations) {
                if (res == null) continue;

                Map<String, Object> item = new HashMap<>();
                item.put("id", res.getId());
                item.put("spaceType", res.getSpaceType() != null ? res.getSpaceType() : "Inconnu");
                item.put("attendees", res.getAttendees() != null ? res.getAttendees() : 0);
                item.put("startTime", res.getStartTime());
                item.put("endTime", res.getEndTime());
                item.put("date", res.getDate());

                String status = res.getStatus();
                if (status == null) {
                    if (res.getDate() != null) {
                        LocalDate resDate = LocalDate.parse(res.getDate());
                        if (resDate.isBefore(today)) {
                            status = "cancelled";
                        } else if (resDate.isEqual(today)) {
                            status = "confirmed";
                        } else {
                            status = "pending";
                        }
                    } else {
                        status = "pending";
                    }
                }

                item.put("status", status.toLowerCase());
                result.add(item);
            }

            // Tri décroissant par date + heure de début
            result.sort((a, b) -> {
                String dateA = (String) a.get("date");
                String dateB = (String) b.get("date");
                String startA = (String) a.get("startTime");
                String startB = (String) b.get("startTime");

                // Comparaison date
                int cmpDate = dateB.compareTo(dateA); // décroissant
                if (cmpDate != 0) return cmpDate;

                // Si même date, comparer heure de début
                if (startA == null) return 1;
                if (startB == null) return -1;
                return startB.compareTo(startA); // décroissant
            });

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
