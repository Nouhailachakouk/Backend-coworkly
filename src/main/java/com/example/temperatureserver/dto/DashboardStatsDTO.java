package com.example.temperatureserver.dto;

public class DashboardStatsDTO {
    private int totalReservations;
    private int activeSpaces;
    private double occupancyRate;
    private double satisfaction;

    // constructor
    public DashboardStatsDTO(int totalReservations, int activeSpaces, double occupancyRate, double satisfaction) {
        this.totalReservations = totalReservations;
        this.activeSpaces = activeSpaces;
        this.occupancyRate = occupancyRate;
        this.satisfaction = satisfaction;
    }

    // getters & setters
    public int getTotalReservations() { return totalReservations; }
    public void setTotalReservations(int totalReservations) { this.totalReservations = totalReservations; }
    public int getActiveSpaces() { return activeSpaces; }
    public void setActiveSpaces(int activeSpaces) { this.activeSpaces = activeSpaces; }
    public double getOccupancyRate() { return occupancyRate; }
    public void setOccupancyRate(double occupancyRate) { this.occupancyRate = occupancyRate; }
    public double getSatisfaction() { return satisfaction; }
    public void setSatisfaction(double satisfaction) { this.satisfaction = satisfaction; }
}
