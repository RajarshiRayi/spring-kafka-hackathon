package com.hackathon.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderEventsTopic() {
        return new NewTopic("order-events", 3, (short) 1);
    }

    @Bean
    public NewTopic inventoryEventsTopic() {
        return new NewTopic("inventory-events", 3, (short) 1);
    }

    @Bean
    public NewTopic fraudEventsTopic() {
        return new NewTopic("fraud-events", 3, (short) 1);
    }
}
