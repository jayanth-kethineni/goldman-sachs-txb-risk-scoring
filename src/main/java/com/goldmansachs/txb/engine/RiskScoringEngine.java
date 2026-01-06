package com.goldmansachs.txb.engine;

import com.goldmansachs.txb.domain.config.RiskThresholdConfig;
import com.goldmansachs.txb.domain.model.RiskScore;
import com.goldmansachs.txb.domain.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The core risk scoring engine that evaluates all rules and aggregates the results.
 * This engine is deterministic and explainable - every score can be traced back to
 * specific rules that were triggered.
 * 
 * The engine evaluates all rules sequentially, then aggregates the results into a single risk score.
 */
@Component
public class RiskScoringEngine {
    
    private static final Logger log = LoggerFactory.getLogger(RiskScoringEngine.class);
    
    private final List<RiskRule> rules;
    private final RiskThresholdConfig thresholdConfig;
    
    public RiskScoringEngine(List<RiskRule> rules, RiskThresholdConfig thresholdConfig) {
        this.rules = rules;
        this.thresholdConfig = thresholdConfig;
        log.info("Initialized RiskScoringEngine with {} rules", rules.size());
    }
    
    /**
     * Evaluates all rules against the transaction and produces a risk score.
     * 
     * @param transaction The transaction to evaluate
     * @return A RiskScore containing the total score, risk level, and reason codes
     */
    public RiskScore calculateScore(Transaction transaction) {
        long startTime = System.currentTimeMillis();
        log.debug("Calculating risk score for transaction {}", transaction.transactionId());
        
        // Evaluate all rules
        List<RiskSignal> signals = rules.stream()
            .map(rule -> rule.evaluate(transaction))
            .collect(Collectors.toList());
        
        // Aggregate triggered signals
        List<RiskSignal> triggeredSignals = signals.stream()
            .filter(RiskSignal::triggered)
            .collect(Collectors.toList());
        
        // Calculate total score
        int totalScore = triggeredSignals.stream()
            .mapToInt(RiskSignal::weight)
            .sum();
        
        // Extract reason codes
        List<String> reasonCodes = triggeredSignals.stream()
            .map(RiskSignal::reasonCode)
            .collect(Collectors.toList());
        
        // Determine risk level using configurable thresholds
        RiskScore.RiskLevel riskLevel = RiskScore.calculateRiskLevel(
            totalScore,
            thresholdConfig.getRiskThresholdMedium(),
            thresholdConfig.getRiskThresholdHigh(),
            thresholdConfig.getRiskThresholdCritical()
        );
        
        long calculationTime = System.currentTimeMillis() - startTime;
        log.info("Risk score calculated for transaction {}: score={}, level={}, reasons={}, time={}ms",
                 transaction.transactionId(), totalScore, riskLevel, reasonCodes, calculationTime);
        
        return new RiskScore(transaction.transactionId(), totalScore, riskLevel, reasonCodes);
    }
}
