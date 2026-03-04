package com.hackathon.fraudservice.producer;

import com.hackathon.fraudservice.event.FraudEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendFraudEvent(FraudEvent event) {
        log.info("Publishing {} for orderId: {}", event.getEventType(), event.getOrderId());
        kafkaTemplate.send("fraud-events", event.getOrderId(), event);
    }
}
