package com.example.temperatureserver.dto;

public class UserClusterDTO {
    private String type;
    private int count;
    private int percentage;
    private String color;

    public UserClusterDTO() {}

    public UserClusterDTO(String type, int count, int percentage, String color) {
        this.type = type;
        this.count = count;
        this.percentage = percentage;
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
