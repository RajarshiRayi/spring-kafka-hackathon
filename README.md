## Problem Statement

Build an event-driven microservices system using Spring Boot and Apache Kafka.

### Scenario

Users place orders. The system must:
- Validate inventory
- Perform fraud checks
- Confirm or reject the order

All communication must happen via Kafka events. No direct REST communication between services.

---

## Architecture Overview

Order Service → order-events → Inventory Service
Order Service → order-events → Fraud Service
Inventory Service → inventory-events → Order Service
Fraud Service → fraud-events → Order Service

---

## Topics

- order-events
- inventory-events
- fraud-events

Key = orderId  

---

## Functional Requirements

### Order Service
- POST /orders API
- Save order with status PENDING
- Publish OrderCreated event
- Listen for Inventory and Fraud responses
- Update final order status

### Inventory Service
- Consume OrderCreated
- Validate stock
- Publish InventoryApproved / InventoryRejected

### Fraud Service
- Consume OrderCreated
- If amount > 50000 → FraudRejected
- Otherwise → FraudApproved


## Expected Outcome

A fully event-driven system demonstrating:
- Kafka producers and consumers
- Consumer groups
- Event-driven architecture
- Decoupled microservices
