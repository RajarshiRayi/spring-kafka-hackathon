package com.hackathon.fraudservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic fraudEventsTopic() {
        return new NewTopic("fraud-events", 3, (short) 1);
    }
}
