package com.example.temperatureserver.dto;

public class UserClusterDTO {
    private String type;
    private int count;
    private int percentage;
    private String color;

    public UserClusterDTO(String type, int count, int percentage, String color) {
        this.type = type;
        this.count = count;
        this.percentage = percentage;
        this.color = color;
    }

    // getters + setters (ou @Data si tu utilises Lombok)
    public String getType() { return type; }
    public int getCount() { return count; }
    public int getPercentage() { return percentage; }
    public String getColor() { return color; }
    public void setType(String type) { this.type = type; }
    public void setCount(int count) { this.count = count; }
    public void setPercentage(int percentage) { this.percentage = percentage; }
    public void setColor(String color) { this.color = color; }
}
