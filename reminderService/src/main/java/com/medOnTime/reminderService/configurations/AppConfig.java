package com.medOnTime.reminderService.configurations;

import com.medOnTime.reminderService.service.MqttPublisher;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public MqttPublisher mqttPublisher() throws MqttException {
        return new MqttPublisher();
    }

}
