package com.hackathon.fraudservice.consumer;

import com.hackathon.fraudservice.event.OrderEvent;
import com.hackathon.fraudservice.service.FraudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FraudConsumer {

    private final FraudService fraudService;

    @KafkaListener(topics = "order-events", groupId = "fraud-group")
    public void handleOrderEvent(OrderEvent event) {
        log.info("Received OrderCreated event for orderId: {}", event.getOrderId());
        fraudService.checkFraud(event);
    }
}
