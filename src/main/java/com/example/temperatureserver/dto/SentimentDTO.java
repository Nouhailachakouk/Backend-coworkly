package com.example.temperatureserver.dto;

import java.time.LocalDate;

public class SentimentDTO {
    private LocalDate date;
    private int positive;
    private int neutral;
    private int negative;

    private double positivePercent;
    private double neutralPercent;
    private double negativePercent;

    public SentimentDTO(LocalDate date, int positive, int neutral, int negative) {
        this.date = date;
        this.positive = positive;
        this.neutral = neutral;
        this.negative = negative;
        int total = positive + neutral + negative;
        if (total > 0) {
            this.positivePercent = 100.0 * positive / total;
            this.neutralPercent = 100.0 * neutral / total;
            this.negativePercent = 100.0 * negative / total;
        } else {
            this.positivePercent = 0;
            this.neutralPercent = 0;
            this.negativePercent = 0;
        }
    }

    public LocalDate getDate() {
        return date;
    }

    public int getPositive() {
        return positive;
    }

    public int getNeutral() {
        return neutral;
    }

    public int getNegative() {
        return negative;
    }

    public double getPositivePercent() {
        return positivePercent;
    }

    public double getNeutralPercent() {
        return neutralPercent;
    }

    public double getNegativePercent() {
        return negativePercent;
    }
}
