package com.example.temperatureserver.controller;

import com.example.temperatureserver.dto.OccupancyDTO;
import com.example.temperatureserver.dto.UserClusterDTO;
import com.example.temperatureserver.dto.SentimentDTO;
import com.example.temperatureserver.service.InsightsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insights")
@CrossOrigin(origins = "*") // si tu veux autoriser React en dev cross-origin
public class InsightsController {

    @Autowired
    private InsightsService insightsService;

    @GetMapping("/occupancy")
    public List<OccupancyDTO> getOccupancyForecast() {
        return insightsService.getOccupancyForecast();
    }

    @GetMapping("/user-clusters")
    public List<UserClusterDTO> getUserClusters() {
        return insightsService.getUserClusters();
    }

    @GetMapping("/sentiment-summary")
    public List<SentimentDTO> getSentimentSummary() {
        return insightsService.getSentimentSummary();
    }
}
