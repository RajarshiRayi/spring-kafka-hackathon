package com.hackathon.orderservice.producer;

import com.hackathon.orderservice.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderCreated(OrderEvent event) {
        log.info("Publishing OrderCreated event for orderId: {}", event.getOrderId());
        kafkaTemplate.send("order-events", event.getOrderId(), event);
    }
}
