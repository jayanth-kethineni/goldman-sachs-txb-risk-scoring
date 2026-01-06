package com.goldmansachs.txb.domain.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for risk level thresholds.
 * These thresholds determine how numeric risk scores map to risk levels.
 */
@Configuration
@ConfigurationProperties(prefix = "txb.risk")
public class RiskThresholdConfig {
    
    private int riskThresholdMedium = 200;
    private int riskThresholdHigh = 400;
    private int riskThresholdCritical = 600;
    
    public int getRiskThresholdMedium() {
        return riskThresholdMedium;
    }
    
    public void setRiskThresholdMedium(int riskThresholdMedium) {
        this.riskThresholdMedium = riskThresholdMedium;
    }
    
    public int getRiskThresholdHigh() {
        return riskThresholdHigh;
    }
    
    public void setRiskThresholdHigh(int riskThresholdHigh) {
        this.riskThresholdHigh = riskThresholdHigh;
    }
    
    public int getRiskThresholdCritical() {
        return riskThresholdCritical;
    }
    
    public void setRiskThresholdCritical(int riskThresholdCritical) {
        this.riskThresholdCritical = riskThresholdCritical;
    }
}
