package com.example.temperatureserver.dto;

import java.time.LocalDate;

public class OccupancyDTO {
    private LocalDate date;
    private double predicted;

    public OccupancyDTO(LocalDate date, double predicted) {
        this.date = date;
        this.predicted = predicted;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getPredicted() {
        return predicted;
    }
}
