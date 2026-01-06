package com.goldmansachs.txb.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Represents a financial transaction. This is an immutable record to ensure data integrity.
 */
public record Transaction(
    String transactionId,
    String clientId,
    String beneficiaryId,
    BigDecimal amount,
    String currency,
    OffsetDateTime transactionTime,
    String country
) {}
