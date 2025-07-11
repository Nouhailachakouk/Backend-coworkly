package com.example.temperatureserver.dto;

import java.time.LocalDate;

public class OccupancyDataDTO {
    private LocalDate date;
    private double predicted;
    private double actual;

    public OccupancyDataDTO(LocalDate date, double predicted, double actual) {
        this.date = date;
        this.predicted = predicted;
        this.actual = actual;
    }

    // getters & setters
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public double getPredicted() { return predicted; }
    public void setPredicted(double predicted) { this.predicted = predicted; }
    public double getActual() { return actual; }
    public void setActual(double actual) { this.actual = actual; }
}
