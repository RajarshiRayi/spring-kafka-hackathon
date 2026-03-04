package com.hackathon.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String orderId;
    private String productId;
    private int quantity;
    private double amount;
    private OrderStatus status;
}
