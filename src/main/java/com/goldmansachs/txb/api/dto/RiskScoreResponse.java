package com.goldmansachs.txb.api.dto;

import java.util.List;

/**
 * Response DTO for risk score calculation.
 * Contains the transaction ID, numeric score, risk level, and reason codes.
 */
public record RiskScoreResponse(
    String transactionId,
    int riskScore,
    String riskLevel,
    List<String> reasonCodes
) {}
