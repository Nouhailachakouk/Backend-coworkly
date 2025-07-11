package com.example.temperatureserver.service;

import com.example.temperatureserver.model.SensorData;
import com.example.temperatureserver.model.SensorType;
import com.example.temperatureserver.repository.SensorDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SensorDataService {

    @Autowired
    private SensorDataRepository sensorDataRepository;

    public SensorData saveSensorData(SensorData sensorData) {
        return sensorDataRepository.save(sensorData);
    }

    public List<SensorData> getSensorDataBySpaceAndType(Long spaceId, SensorType sensorType) {
        return sensorDataRepository.findBySpaceIdAndSensorTypeOrderByTimestampDesc(spaceId, sensorType);
    }

    public Optional<SensorData> getLatestSensorData(Long spaceId, SensorType sensorType) {
        return sensorDataRepository.findFirstBySpaceIdAndSensorTypeOrderByTimestampDesc(spaceId, sensorType);
    }

    public List<SensorData> getSensorDataBySpaceAndTypeAndDateRange(Long spaceId, SensorType sensorType,
                                                                    LocalDateTime startTime, LocalDateTime endTime) {
        return sensorDataRepository.findBySpaceIdAndSensorTypeAndTimestampBetweenOrderByTimestampDesc(
                spaceId, sensorType, startTime, endTime);
    }

    public List<SensorData> getRecentSensorData(int hoursBack) {
        LocalDateTime startTime = LocalDateTime.now().minusHours(hoursBack);
        return sensorDataRepository.findByTimestampAfterOrderByTimestampDesc(startTime);
    }

    // 🔵 Ajouts pour le SensorController :

    public List<SensorData> getSensorDataBySpace(Long spaceId) {
        return sensorDataRepository.findAll()
                .stream()
                .filter(data -> data.getSpace().getId().equals(spaceId))
                .toList();
    }

    public Optional<SensorData> getSensorDataById(Long id) {
        return sensorDataRepository.findById(id);
    }

    public List<SensorData> getAllSensorData() {
        return sensorDataRepository.findAll();
    }

    public boolean deleteSensorData(Long id) {
        if (sensorDataRepository.existsById(id)) {
            sensorDataRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
