
package com.example.temperatureserver.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.temperatureserver.service.AIRecommendationService;

@RestController
@RequestMapping("/api/insights")
public class AIRecommendationController {

    private final AIRecommendationService aiRecommendationService;

    public AIRecommendationController(AIRecommendationService aiRecommendationService) {
        this.aiRecommendationService = aiRecommendationService;
    }

    @GetMapping("/recommendations")
    public Map<String, Object> getRecommendations() {
        // Fix: Call the correct method name from the service
        return aiRecommendationService.getRecommendationsFromFile();
    }
}
