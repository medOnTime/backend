package com.medOnTime.reminderService.configurations;

import com.medOnTime.reminderService.service.MqttPublisher;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    @Bean
    public MqttPublisher mqttPublisher() throws MqttException {
        return new MqttPublisher(brokerUrl);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

