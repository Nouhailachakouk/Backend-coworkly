package com.example.temperatureserver.model;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Temperature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Temperature(Long id, double value, long timestamp) {
        this.id = id;
        this.value = value;
        this.timestamp = timestamp;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Temperature id(Long id) {
        setId(id);
        return this;
    }

    public Temperature value(double value) {
        setValue(value);
        return this;
    }

    public Temperature timestamp(long timestamp) {
        setTimestamp(timestamp);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Temperature)) {
            return false;
        }
        Temperature temperature = (Temperature) o;
        return Objects.equals(id, temperature.id) && value == temperature.value && timestamp == temperature.timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, timestamp);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", value='" + getValue() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            "}";
    }

    private double value;
    private long timestamp;

    // Constructors
    public Temperature() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
