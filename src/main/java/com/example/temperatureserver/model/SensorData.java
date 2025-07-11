package com.example.temperatureserver.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sensor_data")
public class SensorData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;

    @Enumerated(EnumType.STRING)
    @Column(name = "sensor_type")
    private SensorType sensorType;

    private Double value;

    private String unit;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "is_valid", nullable = true)
    private Boolean isValid = true;

    public SensorData() {}

    public SensorData(Space space, SensorType sensorType, Double value, String unit) {
        this.space = space;
        this.sensorType = sensorType;
        this.value = value;
        this.unit = unit;
        this.timestamp = LocalDateTime.now();  // Correct usage of current timestamp
    }

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Space getSpace() { return space; }
    public void setSpace(Space space) { this.space = space; }

    public SensorType getSensorType() { return sensorType; }
    public void setSensorType(SensorType sensorType) { this.sensorType = sensorType; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Boolean getIsValid() { return isValid; }
    public void setIsValid(Boolean isValid) { this.isValid = isValid; }
}
