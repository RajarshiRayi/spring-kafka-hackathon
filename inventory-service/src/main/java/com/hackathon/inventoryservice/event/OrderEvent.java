package com.hackathon.inventoryservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private String eventType;
    private String orderId;
    private String productId;
    private int quantity;
    private double amount;
}
