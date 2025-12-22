# Goldman Sachs TxB - Architectural Decisions

This document outlines the key architectural decisions and tradeoffs made during the design of the Real-Time Transaction Risk Scoring Service.

## 1. Rule-Based Engine vs. Machine Learning

- **Decision:** Use a rule-based engine instead of a machine learning model.
- **Reasoning:** Explainability and auditability are non-negotiable in financial systems. Regulators require a clear, deterministic reason for every risk decision. A rule-based engine provides this transparency, while ML models can be black boxes.
- **Tradeoff:** Lower accuracy than a well-trained ML model, but higher explainability and compliance.

## 2. Synchronous REST API vs. Asynchronous Event-Driven

- **Decision:** Use a synchronous REST API for risk scoring.
- **Reasoning:** Payment processing workflows often require an immediate risk assessment before proceeding. A synchronous API provides this immediate feedback.
- **Tradeoff:** Lower throughput than an asynchronous, event-driven architecture. However, the system can be scaled horizontally to meet throughput requirements.

## 3. PostgreSQL vs. NoSQL

- **Decision:** Use PostgreSQL for data storage.
- **Reasoning:** Financial transactions require strong ACID guarantees. PostgreSQL provides these guarantees, along with the flexibility of JSONB for storing semi-structured data like reason codes.
- **Tradeoff:** Lower write performance than a NoSQL database like Cassandra, but higher consistency and data integrity.

## 4. Circuit Breaker on Transaction History

- **Decision:** Implement a circuit breaker on the `TransactionHistoryRepository`.
- **Reasoning:** The transaction history is a secondary data source. If it becomes unavailable, the system should still be able to provide a risk score, albeit a more conservative one. The circuit breaker prevents cascading failures and allows the system to degrade gracefully.
- **Tradeoff:** Slightly higher complexity in the code, but significantly higher resilience.

## 5. Immutable Domain Models

- **Decision:** Use immutable domain models (`Transaction`, `RiskScore`).
- **Reasoning:** Immutability makes the system easier to reason about and less prone to bugs. It's a best practice for building robust, maintainable systems.
- **Tradeoff:** Slightly more memory usage due to object creation, but the benefits in terms of correctness and thread safety far outweigh this cost.
