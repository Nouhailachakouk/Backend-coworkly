// src/main/java/com/example/temperatureserver/mqtt/MqttSensorDataHandler.java
package com.example.temperatureserver.mqtt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.temperatureserver.model.SensorData;
import com.example.temperatureserver.model.SensorType;
import com.example.temperatureserver.model.Space;
import com.example.temperatureserver.service.SensorDataService;
import com.example.temperatureserver.service.SpaceService;
import com.example.temperatureserver.websocket.WebSocketController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MqttSensorDataHandler {

    private static final Logger logger = LoggerFactory.getLogger(MqttSensorDataHandler.class);

    @Autowired
    private SensorDataService sensorDataService;

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private WebSocketController webSocketController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleSensorData(Message<?> message) {
        try {
            String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
            String payload = message.getPayload().toString();

            logger.info("Received MQTT message on topic: {} with payload: {}", topic, payload);

            if (topic != null && topic.startsWith("temperatureserver/sensors/")) {
                processSensorData(topic, payload);
            } else if (topic != null && topic.startsWith("temperatureserver/reservations/")) {
                processReservationUpdate(payload);
            }

        } catch (Exception e) {
            logger.error("Error processing MQTT message: {}", e.getMessage(), e);
        }
    }

    private void processSensorData(String topic, String payload) {
        try {
            String[] topicParts = topic.split("/");
            if (topicParts.length >= 4) {
                Long spaceId = Long.parseLong(topicParts[2]);
                SensorType sensorType = SensorType.valueOf(topicParts[3].toUpperCase());

                JsonNode jsonNode = objectMapper.readTree(payload);
                double value = jsonNode.get("value").asDouble();
                String unit = jsonNode.has("unit") ? jsonNode.get("unit").asText() : sensorType.getDefaultUnit();

                Optional<Space> spaceOpt = spaceService.getSpaceById(spaceId);
                if (spaceOpt.isPresent()) {
                    Space space = spaceOpt.get();

                    SensorData sensorData = new SensorData(space, sensorType, value, unit);
                    sensorDataService.saveSensorData(sensorData);

                    webSocketController.broadcastSensorData(sensorData);

                    logger.info("Processed sensor data: Space={}, Type={}, Value={}",
                            space.getName(), sensorType, value);
                } else {
                    logger.warn("Space not found for ID: {}", spaceId);
                }
            }
        } catch (Exception e) {
            logger.error("Error processing sensor data: {}", e.getMessage(), e);
        }
    }

    private void processReservationUpdate(String payload) {
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            webSocketController.broadcastReservationUpdate(jsonNode.toString());
            logger.info("Processed reservation update: {}", payload);
        } catch (Exception e) {
            logger.error("Error processing reservation update: {}", e.getMessage(), e);
        }
    }
}
