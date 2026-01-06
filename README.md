# Goldman Sachs TxB - Real-Time Transaction Risk Scoring Service

This project is a production-grade implementation of a real-time, explainable transaction risk scoring service, designed to meet the stringent requirements of Goldman Sachs' Transaction Banking (TxB) platform. It is built to be reviewed by internal engineering, compliance, and regulatory bodies.

This project is independently built for learning and portfolio purposes and is not affiliated with Goldman Sachs.

## 1. Business Problem

Goldman Sachs TxB processes high-value, time-sensitive corporate payments. To protect the firm and its clients from financial crime, each transaction must be scored for fraud and compliance risk in real-time before execution. The system must provide decisions that are **fast**, **explainable**, **auditable**, and **resilient**.

## 2. Architectural Principles

The system is designed around non-negotiable architectural constraints that prioritize regulatory compliance and production stability:

- **Explainability & Determinism**: A **rule-based engine** was chosen over machine learning to ensure every risk decision is transparent and reproducible for regulators.
- **Real-Time Decisioning**: A **synchronous REST API** provides immediate feedback required by upstream payment processing workflows.
- **Financial Correctness**: **PostgreSQL** is used for its strong ACID guarantees, ensuring data integrity for audit trails and historical analysis.
- **Resilience**: A **circuit breaker** pattern is implemented on the transaction history dependency to prevent cascading failures and ensure graceful degradation.
- **Immutability**: Domain models (`Transaction`, `RiskScore`) are implemented as immutable Java 17 records to ensure thread safety and system correctness.

For a detailed breakdown of architectural decisions and tradeoffs, see `ARCHITECTURE.md`.

## 3. Core Features

- **Clean Architecture**: The system follows a clean, layered architecture (`API` → `Domain` → `Engine` → `Infrastructure`) for maintainability and separation of concerns.
- **Rule-Based Scoring Engine**: A deterministic engine evaluates a set of configurable risk rules:
  - `NEW_BENEFICIARY`: +150 points
  - `HIGH_VALUE_TRANSACTION`: +200 points
  - `HIGH_RISK_COUNTRY`: +250 points
  - `UNUSUAL_TIME_OF_DAY`: +100 points
- **Financial-Grade Audit Trail**: Every scoring decision is persisted as an immutable, append-only record in a dedicated PostgreSQL table for compliance and forensic analysis.
- **Observability**: The service is instrumented with **Prometheus metrics** to monitor API latency, throughput, and error rates, providing critical visibility into production health.
- **Production-Ready Deployment**: The entire stack is containerized using **Docker Compose**, enabling consistent, one-command deployments.

## 4. API Contract

**Endpoint**: `POST /v1/scores/calculate`

Calculates a risk score for a new transaction.

**Request Body**:

```json
{
  "transactionId": "TXN-DEMO-001",
  "clientId": "CLIENT-001",
  "beneficiaryId": "BENEFICIARY-001",
  "amount": 10000.00,
  "currency": "USD",
  "transactionTime": "2024-01-15T14:30:00Z",
  "country": "US"
}
```

**Success Response (200 OK)**:

```json
{
  "transactionId": "TXN-DEMO-001",
  "riskScore": 100,
  "riskLevel": "LOW",
  "reasonCodes": []
}
```

## 5. How to Run Locally

**Prerequisites**: Docker and Docker Compose must be installed.

1.  **Build and Start the Stack**:

    ```bash
    docker-compose up --build -d
    ```

    This command will:
    - Build the Spring Boot application JAR.
    - Start the PostgreSQL, Prometheus, and application containers.
    - Initialize the database schema and seed data using `init.sql`.

2.  **Run the 60-Second Demo Script**:

    The `demo.sh` script simulates four transactions, demonstrating LOW, MEDIUM, HIGH, and CRITICAL risk scenarios.

    ```bash
    ./demo.sh
    ```

3.  **Access Supporting Services**:
    - **Prometheus Dashboard**: [http://localhost:9090](http://localhost:9090)
    - **PostgreSQL (Audit Trail)**:
      ```bash
      docker exec -it gs-txb-postgres psql -U postgres -d txb_risk
      ```
      Then run: `SELECT * FROM transaction_risk_scores;`

4.  **Shut Down the Stack**:

    ```bash
    docker-compose down
    ```
