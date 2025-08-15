package com.example.temperatureserver.dto;

import java.util.List;

public class AIInsightDTO {
    private String title;
    private String description;
    private List<String> recommendations;

    public AIInsightDTO(String title, String description, List<String> recommendations) {
        this.title = title;
        this.description = description;
        this.recommendations = recommendations;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
}
