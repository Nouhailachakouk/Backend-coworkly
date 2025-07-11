// src/main/java/com/example/temperatureserver/websocket/WebSocketController.java
package com.example.temperatureserver.websocket;

import com.example.temperatureserver.model.SensorData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void broadcastSensorData(SensorData sensorData) {
        messagingTemplate.convertAndSend("/topic/sensors", sensorData);
        messagingTemplate.convertAndSend("/topic/sensors/" + sensorData.getSpace().getId(), sensorData);
    }

    public void broadcastReservationUpdate(String reservationData) {
        messagingTemplate.convertAndSend("/topic/reservations", reservationData);
    }

    public void broadcastDashboardStats(Object stats) {
        messagingTemplate.convertAndSend("/topic/dashboard", stats);
    }
}
