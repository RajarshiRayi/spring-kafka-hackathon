package com.hackathon.inventoryservice.service;

import com.hackathon.inventoryservice.event.InventoryEvent;
import com.hackathon.inventoryservice.event.OrderEvent;
import com.hackathon.inventoryservice.producer.InventoryProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryProducer inventoryProducer;

    private final Map<String, Integer> inventory = new ConcurrentHashMap<>(Map.of(
            "PRODUCT-001", 100,
            "PRODUCT-002", 50,
            "PRODUCT-003", 200,
            "PRODUCT-004", 0
    ));

    public void validateStock(OrderEvent orderEvent) {
        String productId = orderEvent.getProductId();
        int requested = orderEvent.getQuantity();
        int available = inventory.getOrDefault(productId, 0);

        String eventType;
        if (available >= requested) {
            inventory.put(productId, available - requested);
            eventType = "INVENTORY_APPROVED";
            log.info("Stock approved for orderId={}, productId={}, remaining={}",
                    orderEvent.getOrderId(), productId, available - requested);
        } else {
            eventType = "INVENTORY_REJECTED";
            log.info("Stock rejected for orderId={}, productId={}, available={}, requested={}",
                    orderEvent.getOrderId(), productId, available, requested);
        }

        InventoryEvent event = new InventoryEvent(eventType, orderEvent.getOrderId());
        inventoryProducer.sendInventoryEvent(event);
    }
}
