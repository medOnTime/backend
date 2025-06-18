package com.MedOnTime.chatBotService.config;

import io.pinecone.clients.Index;
import io.pinecone.configs.PineconeConfig;
import io.pinecone.configs.PineconeConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Value("${pinecone.api.key}")
    private String pineconeKey;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Index medontimefaqIndex(){
        //first check whether is it faq type question
        PineconeConfig config = new PineconeConfig(pineconeKey);
        config.setHost("medontimefaq-d54s7jh.svc.aped-4627-b74a.pinecone.io");

        PineconeConnection connection = new PineconeConnection(config);
        return new Index(config,connection,"medontimefaq");
    }
}
