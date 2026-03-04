package com.hackathon.inventoryservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic inventoryEventsTopic() {
        return new NewTopic("inventory-events", 3, (short) 1);
    }
}
