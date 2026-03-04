package com.hackathon.orderservice.consumer;

import com.hackathon.orderservice.event.FraudEvent;
import com.hackathon.orderservice.event.InventoryEvent;
import com.hackathon.orderservice.model.OrderStatus;
import com.hackathon.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderConsumer {

    private final OrderService orderService;

    private final Map<String, Map<String, Boolean>> approvalTracker = new ConcurrentHashMap<>();

    @KafkaListener(topics = "inventory-events", groupId = "order-group")
    public void handleInventoryEvent(InventoryEvent event) {
        log.info("Received {} for orderId: {}", event.getEventType(), event.getOrderId());
        boolean approved = "INVENTORY_APPROVED".equals(event.getEventType());
        trackAndResolve(event.getOrderId(), "inventory", approved);
    }

    @KafkaListener(topics = "fraud-events", groupId = "order-group")
    public void handleFraudEvent(FraudEvent event) {
        log.info("Received {} for orderId: {}", event.getEventType(), event.getOrderId());
        boolean approved = "FRAUD_APPROVED".equals(event.getEventType());
        trackAndResolve(event.getOrderId(), "fraud", approved);
    }

    private synchronized void trackAndResolve(String orderId, String source, boolean approved) {
        approvalTracker.computeIfAbsent(orderId, k -> new ConcurrentHashMap<>()).put(source, approved);

        Map<String, Boolean> statuses = approvalTracker.get(orderId);
        if (statuses.containsKey("inventory") && statuses.containsKey("fraud")) {
            boolean allApproved = statuses.values().stream().allMatch(v -> v);
            OrderStatus finalStatus = allApproved ? OrderStatus.CONFIRMED : OrderStatus.REJECTED;
            orderService.updateOrderStatus(orderId, finalStatus);
            log.info("Order {} resolved to {}", orderId, finalStatus);
            approvalTracker.remove(orderId);
        }
    }
}
