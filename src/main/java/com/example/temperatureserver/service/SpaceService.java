package com.example.temperatureserver.service;

import com.example.temperatureserver.model.Space;
import com.example.temperatureserver.repository.SpaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpaceService {

    private final SpaceRepository spaceRepository;

    public SpaceService(SpaceRepository spaceRepository) {
        this.spaceRepository = spaceRepository;
    }

    // Retourne tous les espaces actifs (isActive = true)
    public List<Space> getActiveSpaces() {
        return spaceRepository.findAll().stream()
                .filter(space -> Boolean.TRUE.equals(space.getIsActive()))
                .toList();
    }

    // Trouve un espace actif par type
    public Optional<Space> findActiveByType(String type) {
        return spaceRepository.findByTypeAndIsActiveTrue(type);
    }

    // Trouve un espace par ID (méthode manquante ajoutée ici)
    public Optional<Space> getSpaceById(Long id) {
        return spaceRepository.findById(id);
    }

    // Ajouter un espace (exemple)
    public Space addSpace(Space space) {
        space.setIsActive(true);
        space.setAvailableCount(space.getAvailableCount() != null ? space.getAvailableCount() : 0);
        return spaceRepository.save(space);
    }

    // Modifier un espace
    public Space updateSpace(Long id, Space updatedSpace) {
        Space existing = spaceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Espace non trouvé"));

        existing.setName(updatedSpace.getName());
        existing.setType(updatedSpace.getType());
        existing.setCapacity(updatedSpace.getCapacity());
        existing.setIsActive(updatedSpace.getIsActive());
        existing.setAvailableCount(updatedSpace.getAvailableCount());

        return spaceRepository.save(existing);
    }
}
