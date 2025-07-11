package com.example.temperatureserver.repository;


import com.example.temperatureserver.model.SensorData;
import com.example.temperatureserver.model.SensorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {

    List<SensorData> findBySpaceIdAndSensorTypeOrderByTimestampDesc(Long spaceId, SensorType sensorType);

    List<SensorData> findBySpaceIdAndSensorTypeAndTimestampBetweenOrderByTimestampDesc(
            Long spaceId, SensorType sensorType, LocalDateTime startTime, LocalDateTime endTime);

    Optional<SensorData> findFirstBySpaceIdAndSensorTypeOrderByTimestampDesc(Long spaceId, SensorType sensorType);

    List<SensorData> findByTimestampAfterOrderByTimestampDesc(LocalDateTime startTime);
}
