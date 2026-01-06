package com.goldmansachs.txb.engine;

/**
 * Represents a signal from a risk rule evaluation.
 * Each rule produces a signal with a weight and reason code.
 */
public record RiskSignal(
    String reasonCode,
    int weight,
    boolean triggered
) {
    /**
     * Factory method for creating a triggered signal
     */
    public static RiskSignal triggered(String reasonCode, int weight) {
        return new RiskSignal(reasonCode, weight, true);
    }
    
    /**
     * Factory method for creating a non-triggered signal
     */
    public static RiskSignal notTriggered(String reasonCode) {
        return new RiskSignal(reasonCode, 0, false);
    }
}
