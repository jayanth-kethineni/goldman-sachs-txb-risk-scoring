package com.goldmansachs.txb.domain.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RiskScore {
    String transactionId;
    Integer score;
    String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL
    List<String> reasonCodes;
    Long calculationTimeMs;
    
    public static String calculateRiskLevel(int score) {
        if (score >= 700) return "CRITICAL";
        if (score >= 500) return "HIGH";
        if (score >= 300) return "MEDIUM";
        return "LOW";
    }
}
