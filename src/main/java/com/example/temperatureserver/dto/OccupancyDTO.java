package com.example.temperatureserver.dto;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

public class OccupancyDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate day;

    private long predicted;

    public OccupancyDTO() {}

    public OccupancyDTO(LocalDate day, long predicted) {
        this.day = day;
        this.predicted = predicted;
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public long getPredicted() {
        return predicted;
    }

    public void setPredicted(long predicted) {
        this.predicted = predicted;
    }
}
