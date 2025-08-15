package com.example.temperatureserver.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.temperatureserver.model.Reservation;
import com.example.temperatureserver.model.Space;
import com.example.temperatureserver.repository.ReservationRepository;
import com.example.temperatureserver.repository.SpaceRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SpaceRepository spaceRepository;

    public ReservationService(ReservationRepository reservationRepository, SpaceRepository spaceRepository) {
        this.reservationRepository = reservationRepository;
        this.spaceRepository = spaceRepository;
    }
    @Transactional
    public Reservation createReservation(Reservation reservation) {
        if (reservation.getSpaceType() == null || reservation.getSpaceType().isEmpty()) {
            throw new IllegalArgumentException("Le champ spaceType est obligatoire.");
        }

        // Récupérer l'espace actif par type
        Space space = spaceRepository.findByTypeAndIsActiveTrue(reservation.getSpaceType())
                .orElseThrow(() -> new IllegalArgumentException("Espace introuvable pour le type : " + reservation.getSpaceType()));

        // Vérifier la disponibilité
        if (space.getAvailableCount() < reservation.getAttendees()) {
            throw new IllegalArgumentException("Pas assez de places disponibles dans cet espace.");
        }

        // Assigner l'espace à la réservation
        reservation.setSpace(space);

        // Mettre à jour availableCount
        space.setAvailableCount(space.getAvailableCount() - reservation.getAttendees());
        spaceRepository.save(space);

        // Nettoyer spaceType pour éviter confusion
        reservation.setSpaceType(null);

        // Sauvegarder la réservation
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Réservation non trouvée avec l'id : " + id));

        if (!"cancelled".equalsIgnoreCase(reservation.getStatus())) {
            // Remettre les places à dispo
            Space space = reservation.getSpace();
            space.setAvailableCount(space.getAvailableCount() + reservation.getAttendees());
            spaceRepository.save(space);

            // Mettre à jour le status
            reservation.setStatus("cancelled");
            reservationRepository.save(reservation);
        }
    }
}
