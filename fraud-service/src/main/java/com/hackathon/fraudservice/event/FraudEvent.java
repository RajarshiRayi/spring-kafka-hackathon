package com.hackathon.fraudservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudEvent {
    private String eventType;
    private String orderId;
}
