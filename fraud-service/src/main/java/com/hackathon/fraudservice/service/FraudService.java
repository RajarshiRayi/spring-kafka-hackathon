package com.hackathon.fraudservice.service;

import com.hackathon.fraudservice.event.FraudEvent;
import com.hackathon.fraudservice.event.OrderEvent;
import com.hackathon.fraudservice.producer.FraudProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudService {

    private final FraudProducer fraudProducer;

    public void checkFraud(OrderEvent orderEvent) {
        String eventType;
        if (orderEvent.getAmount() > 50000) {
            eventType = "FRAUD_REJECTED";
            log.info("Fraud detected for orderId={}, amount={}", orderEvent.getOrderId(), orderEvent.getAmount());
        } else {
            eventType = "FRAUD_APPROVED";
            log.info("Fraud check passed for orderId={}, amount={}", orderEvent.getOrderId(), orderEvent.getAmount());
        }

        FraudEvent event = new FraudEvent(eventType, orderEvent.getOrderId());
        fraudProducer.sendFraudEvent(event);
    }
}
