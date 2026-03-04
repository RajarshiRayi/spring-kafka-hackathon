package com.hackathon.inventoryservice.producer;

import com.hackathon.inventoryservice.event.InventoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendInventoryEvent(InventoryEvent event) {
        log.info("Publishing {} for orderId: {}", event.getEventType(), event.getOrderId());
        kafkaTemplate.send("inventory-events", event.getOrderId(), event);
    }
}
