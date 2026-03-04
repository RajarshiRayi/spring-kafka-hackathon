package com.hackathon.orderservice.service;

import com.hackathon.orderservice.dto.OrderRequest;
import com.hackathon.orderservice.event.OrderEvent;
import com.hackathon.orderservice.model.Order;
import com.hackathon.orderservice.model.OrderStatus;
import com.hackathon.orderservice.producer.OrderProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderProducer orderProducer;
    private final Map<String, Order> orderStore = new ConcurrentHashMap<>();

    public Order createOrder(OrderRequest request) {
        String orderId = UUID.randomUUID().toString();
        Order order = new Order(orderId, request.getProductId(), request.getQuantity(),
                request.getAmount(), OrderStatus.PENDING);
        orderStore.put(orderId, order);
        log.info("Order created: id={}, status=PENDING", orderId);

        OrderEvent event = new OrderEvent("ORDER_CREATED", orderId,
                request.getProductId(), request.getQuantity(), request.getAmount());
        orderProducer.sendOrderCreated(event);

        return order;
    }

    public Order getOrder(String orderId) {
        return orderStore.get(orderId);
    }

    public void updateOrderStatus(String orderId, OrderStatus status) {
        Order order = orderStore.get(orderId);
        if (order != null) {
            order.setStatus(status);
            log.info("Order {} updated to status: {}", orderId, status);
        }
    }
}
