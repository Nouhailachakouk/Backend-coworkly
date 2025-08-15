package com.example.temperatureserver.service;

import com.example.temperatureserver.dto.OccupancyDTO;
import com.example.temperatureserver.dto.UserClusterDTO;
import com.example.temperatureserver.dto.SentimentDTO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InsightsService {

    private final JdbcTemplate jdbcTemplate;

    public InsightsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<OccupancyDTO> getOccupancyForecast() {
        String sql = """
            SELECT date(timestamp / 1000, 'unixepoch') as day,
                   AVG(value) as avg_occupancy
            FROM sensor_data
            WHERE sensor_type = 'OCCUPANCY' AND is_valid = 1
            GROUP BY day
            ORDER BY day DESC
            LIMIT 7
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            LocalDate date = LocalDate.parse(rs.getString("day"));
            double avgOccupancy = rs.getDouble("avg_occupancy");
            long roundedPredicted = Math.round(avgOccupancy);
            return new OccupancyDTO(date, roundedPredicted);
        });
    }

    public List<UserClusterDTO> getUserClusters() {
        String sql = """
            SELECT profession, COUNT(*) as count
            FROM user_info
            WHERE profession IS NOT NULL AND profession <> ''
            GROUP BY profession
            ORDER BY count DESC
        """;

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        long totalCount = rows.stream()
                .mapToLong(row -> ((Number) row.get("count")).longValue())
                .sum();

        Map<String, String> colorMap = Map.of(
                "entrepreneur", "#3b82f6",
                "freelance", "#8b5cf6",
                "consultant", "#f59e0b"
        );

        return rows.stream()
                .map(row -> {
                    String type = ((String) row.get("profession")).trim();
                    long count = ((Number) row.get("count")).longValue();
                    int percentage = (int) Math.round((count * 100.0) / totalCount);
                    String color = colorMap.getOrDefault(type.toLowerCase(), "#999999");
                    return new UserClusterDTO(type, (int) count, percentage, color);
                })
                .collect(Collectors.toList());
    }

    public List<SentimentDTO> getSentimentSummary() {
        String sql = """
            SELECT 
                date(timestamp / 1000, 'unixepoch') as day,
                timestamp,
                MAX(CASE WHEN sensor_type = 'TEMPERATURE' THEN value ELSE NULL END) AS temp,
                MAX(CASE WHEN sensor_type = 'HUMIDITY' THEN value ELSE NULL END) AS humidity,
                MAX(CASE WHEN sensor_type = 'CO2' THEN value ELSE NULL END) AS co2,
                MAX(CASE WHEN sensor_type = 'LIGHT' THEN value ELSE NULL END) AS light,
                MAX(CASE WHEN sensor_type = 'OCCUPANCY' THEN value ELSE NULL END) AS occupancy
            FROM sensor_data
            WHERE is_valid = 1
            GROUP BY day
        """;

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        Map<LocalDate, int[]> sentimentByDay = new HashMap<>();

        for (Map<String, Object> row : rows) {
            LocalDate day = LocalDate.parse((String) row.get("day"));

            double temp = row.get("temp") != null ? ((Number) row.get("temp")).doubleValue() : 0;
            double humidity = row.get("humidity") != null ? ((Number) row.get("humidity")).doubleValue() : 0;
            double co2 = row.get("co2") != null ? ((Number) row.get("co2")).doubleValue() : 0;
            double light = row.get("light") != null ? ((Number) row.get("light")).doubleValue() : 0;
            double occupancy = row.get("occupancy") != null ? ((Number) row.get("occupancy")).doubleValue() : 0;

            int score = 0;

            // Critères corrigés selon tes données / seuils idéaux
            if (temp >= 20 && temp <= 28) score++;          // 20-24°C idéal
            if (humidity >= 40 && humidity <= 60) score++;  // 40-60% humidité idéale
            if (co2 > 0 && co2 < 1000) score++;             // CO2 < 1000 ppm
            if (light >= 300 && light <= 1000) score++;     // Luminosité 300-1000 lux
            if (occupancy >= 1 && occupancy <= 25) score++; // Occupancy entre 1 et 25 personnes

            int[] counters = sentimentByDay.getOrDefault(day, new int[3]);

            if (score >= 4) counters[0]++;      // Positif
            else if (score == 3) counters[1]++; // Neutre
            else counters[2]++;                 // Négatif

            sentimentByDay.put(day, counters);
        }

        return sentimentByDay.entrySet().stream()
                .sorted(Map.Entry.<LocalDate, int[]>comparingByKey().reversed())
                .limit(7)
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    int[] counts = entry.getValue();
                    return new SentimentDTO(date, counts[0], counts[1], counts[2]);
                })
                .collect(Collectors.toList());
    }
}
