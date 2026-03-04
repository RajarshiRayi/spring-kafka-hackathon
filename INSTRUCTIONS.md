# Setup and Run Instructions

This document describes how to build, run, and test the event-driven order system (Order, Inventory, and Fraud services with Apache Kafka).

---

## Prerequisites

- **Java 17+** (required for Spring Boot 4.x)
- **Maven 3.6+**
- **Docker & Docker Compose** (for Kafka)

Verify:

```bash
java -version
mvn -version
docker --version
docker compose version
```

---

## Step 1: Start Kafka

From the project root:

```bash
docker compose up -d
```

Kafka will listen on **localhost:9092**. Check that the container is running:

```bash
docker compose ps
```

To stop Kafka later:

```bash
docker compose down
```

---

## Step 2: Build All Services

Each service is a separate Maven project. Build all three:

```bash
# Order Service (port 8080)
cd order-service && mvn clean package -DskipTests && cd ..

# Inventory Service (port 8081)
cd inventory-service && mvn clean package -DskipTests && cd ..

# Fraud Service (port 8082)
cd fraud-service && mvn clean package -DskipTests && cd ..
```

Or from the project root, one at a time:

```bash
mvn -f order-service/pom.xml clean package -DskipTests
mvn -f inventory-service/pom.xml clean package -DskipTests
mvn -f fraud-service/pom.xml clean package -DskipTests
```

---

## Step 3: Run the Services

Start the services in any order (they all connect to Kafka). Use **three separate terminals**.

**Terminal 1 – Order Service (API entry point):**

```bash
cd order-service
mvn spring-boot:run
```

Wait until you see something like: `Started OrderServiceApplication`.

**Terminal 2 – Inventory Service:**

```bash
cd inventory-service
mvn spring-boot:run
```

**Terminal 3 – Fraud Service:**

```bash
cd fraud-service
mvn spring-boot:run
```

| Service          | Port | Role                                      |
|------------------|------|-------------------------------------------|
| Order Service    | 8080 | REST API, publishes order-events, consumes inventory/fraud events |
| Inventory Service| 8081 | Consumes order-events, publishes inventory-events |
| Fraud Service    | 8082 | Consumes order-events, publishes fraud-events     |

---

## Step 4: Test the Flow

### Create an order (approved path)

**Request:**

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"productId":"PRODUCT-001","quantity":2,"amount":1000}'
```

**Expected:** Order is created with `status: "PENDING"`. After Inventory and Fraud respond, the order moves to `CONFIRMED` (inventory has stock for PRODUCT-001, amount &lt; 50000).

### Get order status

Use the `orderId` from the create response:

```bash
curl http://localhost:8080/orders/<orderId>
```

After a short delay, status should be `CONFIRMED`.

### Inventory rejected (out of stock)

PRODUCT-004 has 0 stock, so the order is rejected:

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"productId":"PRODUCT-004","quantity":1,"amount":500}'
```

Then:

```bash
curl http://localhost:8080/orders/<orderId>
```

Expected: `status: "REJECTED"` (inventory rejects).

### Fraud rejected (amount &gt; 50000)

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"productId":"PRODUCT-001","quantity":1,"amount":60000}'
```

Then:

```bash
curl http://localhost:8080/orders/<orderId>
```

Expected: `status: "REJECTED"` (fraud rejects).

### Sample product stock (Inventory Service)

| productId   | Stock |
|------------|-------|
| PRODUCT-001| 100   |
| PRODUCT-002| 50    |
| PRODUCT-003| 200   |
| PRODUCT-004| 0     |

---

## Step 5: Shutdown

1. Stop each Spring Boot app (Ctrl+C in each terminal).
2. Stop Kafka:

   ```bash
   docker compose down
   ```

---

## Troubleshooting

| Issue | What to check |
|-------|----------------|
| Order stays PENDING | Ensure Inventory and Fraud services are running and Kafka is up (`docker compose ps`). |
| Connection refused to Kafka | Start Kafka with `docker compose up -d` and wait a few seconds before starting the apps. |
| Port already in use | Change port in the service’s `application.yml` or stop the process using that port. |
| Build failures | Run `mvn clean package` in the failing module and fix any compile errors. |

---

## Verification Summary

Before using these steps, the following were verified:

- **Build:** `mvn compile` and `mvn package -DskipTests` succeed for `order-service`, `inventory-service`, and `fraud-service`.
- **Tests:** `mvn test` passes in `order-service`.
- **Kafka:** `docker compose up -d` starts Kafka; `docker compose ps` shows the container running on port 9092.
