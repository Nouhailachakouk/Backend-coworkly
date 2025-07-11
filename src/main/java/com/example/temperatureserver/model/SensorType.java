package com.example.temperatureserver.model;

public enum SensorType {
    TEMPERATURE("°C"),
    HUMIDITY("%"),
    OCCUPANCY("people"),
    LIGHT("lux"),
    NOISE("dB"),
    CO2("ppm"),
    MOTION("boolean");

    private final String defaultUnit;

    SensorType(String defaultUnit) {
        this.defaultUnit = defaultUnit;
    }

    public String getDefaultUnit() {
        return defaultUnit;
    }
}
