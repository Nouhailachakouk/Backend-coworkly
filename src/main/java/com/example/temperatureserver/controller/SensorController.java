package com.example.temperatureserver.controller;

import com.example.temperatureserver.model.SensorData;
import com.example.temperatureserver.model.SensorType;
import com.example.temperatureserver.service.SensorDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/sensors")
@CrossOrigin(origins = "http://localhost:3000")
public class SensorController {

    @Autowired
    private SensorDataService sensorDataService;

    @GetMapping("/space/{spaceId}")
    public ResponseEntity<List<SensorData>> getSensorDataBySpace(@PathVariable Long spaceId) {
        List<SensorData> data = sensorDataService.getSensorDataBySpace(spaceId);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SensorData> getSensorDataById(@PathVariable Long id) {
        return sensorDataService.getSensorDataById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<SensorData>> getAllSensorData() {
        return ResponseEntity.ok(sensorDataService.getAllSensorData());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSensorData(@PathVariable Long id) {
        if (sensorDataService.deleteSensorData(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/space/{spaceId}/filter")
    public ResponseEntity<List<SensorData>> getFilteredSensorData(
        @PathVariable Long spaceId,
        @RequestParam SensorType sensorType,
        @RequestParam(required = false) String start,   // ISO date string, ex: 2025-07-02T00:00:00
        @RequestParam(required = false) String end) {
    LocalDateTime startTime = (start != null) ? LocalDateTime.parse(start) : LocalDateTime.now().minusDays(1);
    LocalDateTime endTime = (end != null) ? LocalDateTime.parse(end) : LocalDateTime.now();

    List<SensorData> filteredData = sensorDataService.getSensorDataBySpaceAndTypeAndDateRange(spaceId, sensorType, startTime, endTime);

    return ResponseEntity.ok(filteredData);
}


}
