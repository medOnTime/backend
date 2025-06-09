package com.medOnTime.reminderService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.medOnTime.reminderService.dto.ReminderSchedulesDTO;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Service;

@Service
public class MqttPublisher {

    private final String brokerUrl = "tcp://localhost:1883"; // Or use ws:// for WebSocket
    private final String clientId = "spring-reminder-publisher";
    private final MqttClient mqttClient;
    private final ObjectMapper objectMapper;

    public MqttPublisher() throws MqttException {
        mqttClient = new MqttClient(brokerUrl, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        mqttClient.connect(options);

        // Initialize ObjectMapper with JavaTimeModule to handle LocalDateTime
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void publishReminderDto(String userId, ReminderSchedulesDTO dto) {
        String topic = "user/" + userId + "/reminder";
        try {
            String jsonPayload = objectMapper.writeValueAsString(dto);
            MqttMessage mqttMessage = new MqttMessage(jsonPayload.getBytes());
            mqttMessage.setQos(1);
            mqttClient.publish(topic, mqttMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
