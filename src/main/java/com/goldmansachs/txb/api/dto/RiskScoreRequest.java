package com.goldmansachs.txb.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Request DTO for risk score calculation.
 * All fields are validated to ensure data integrity.
 */
public record RiskScoreRequest(
    @NotBlank(message = "Transaction ID is required")
    String transactionId,
    
    @NotBlank(message = "Client ID is required")
    String clientId,
    
    @NotBlank(message = "Beneficiary ID is required")
    String beneficiaryId,
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    BigDecimal amount,
    
    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
    String currency,
    
    @NotNull(message = "Transaction time is required")
    OffsetDateTime transactionTime,
    
    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 2, message = "Country must be a 2-letter ISO code")
    String country
) {}
