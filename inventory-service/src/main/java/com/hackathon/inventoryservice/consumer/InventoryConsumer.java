package com.hackathon.inventoryservice.consumer;

import com.hackathon.inventoryservice.event.OrderEvent;
import com.hackathon.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(topics = "order-events", groupId = "inventory-group")
    public void handleOrderEvent(OrderEvent event) {
        log.info("Received OrderCreated event for orderId: {}", event.getOrderId());
        inventoryService.validateStock(event);
    }
}
