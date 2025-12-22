package com.goldmansachs.txb.domain.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;

@Value
@Builder
public class Transaction {
    String transactionId;
    String clientId;
    String beneficiaryId;
    String beneficiaryCountry;
    BigDecimal amount;
    String currency;
    Instant timestamp;
}
