package com.goldmansachs.txb.domain.model;

import java.util.List;

/**
 * Represents the result of a risk assessment. This is an immutable record to ensure data integrity.
 */
public record RiskScore(
    String transactionId,
    int score,
    RiskLevel level,
    List<String> reasonCodes
) {
    public enum RiskLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    /**
     * Calculates the risk level based on the numeric score using configurable thresholds.
     * 
     * @param score The numeric risk score
     * @param mediumThreshold Threshold for MEDIUM risk
     * @param highThreshold Threshold for HIGH risk
     * @param criticalThreshold Threshold for CRITICAL risk
     * @return The calculated risk level
     */
    public static RiskLevel calculateRiskLevel(int score, int mediumThreshold, int highThreshold, int criticalThreshold) {
        if (score >= criticalThreshold) return RiskLevel.CRITICAL;
        if (score >= highThreshold) return RiskLevel.HIGH;
        if (score >= mediumThreshold) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }
}
