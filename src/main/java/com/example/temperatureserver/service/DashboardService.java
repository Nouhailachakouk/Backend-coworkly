package com.example.temperatureserver.service;

import com.example.temperatureserver.dto.DashboardStatsDTO;
import com.example.temperatureserver.dto.OccupancyDataDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    public DashboardStatsDTO getDashboardStats() {
        return new DashboardStatsDTO(125, 18, 78.5, 4.6);
    }

    public List<OccupancyDataDTO> getRecentOccupancyData() {
        // ta méthode actuelle
        return List.of(
            new OccupancyDataDTO(LocalDate.now().minusDays(6), 65.0, 60.0),
            new OccupancyDataDTO(LocalDate.now().minusDays(5), 68.0, 64.0),
            new OccupancyDataDTO(LocalDate.now().minusDays(4), 70.0, 67.0),
            new OccupancyDataDTO(LocalDate.now().minusDays(3), 75.0, 73.0),
            new OccupancyDataDTO(LocalDate.now().minusDays(2), 80.0, 78.0),
            new OccupancyDataDTO(LocalDate.now().minusDays(1), 82.0, 79.0),
            new OccupancyDataDTO(LocalDate.now(), 85.0, 83.0)
        );
    }

    // Ajoute cette méthode pour correspondre à l'appel du contrôleur
    public List<OccupancyDataDTO> getOccupancyData(int days) {
        // Ici, tu peux filtrer ou générer des données dynamiquement selon "days"
        List<OccupancyDataDTO> allData = getRecentOccupancyData();

        // Simple filtrage pour retourner seulement les derniers "days" jours
        if (days >= allData.size()) {
            return allData;
        } else {
            return new ArrayList<>(allData.subList(allData.size() - days, allData.size()));
        }
    }
}
