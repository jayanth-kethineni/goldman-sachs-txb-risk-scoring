package com.goldmansachs.txb.domain;

import com.goldmansachs.txb.domain.model.RiskScore;
import com.goldmansachs.txb.domain.model.Transaction;
import com.goldmansachs.txb.engine.RiskScoringEngine;
import com.goldmansachs.txb.infrastructure.audit.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Domain service that orchestrates the risk scoring process.
 * This service coordinates between the scoring engine and the audit service.
 * 
 * Responsibilities:
 * 1. Invoke the risk scoring engine
 * 2. Persist the result to the audit trail
 * 3. Return the risk score to the caller
 * 
 * Note: Circuit breaker is applied at the rule level (not here) to allow
 * granular control over which dependencies are failing.
 */
@Service
public class RiskScoringService {
    
    private static final Logger log = LoggerFactory.getLogger(RiskScoringService.class);
    
    private final RiskScoringEngine engine;
    private final AuditService auditService;
    
    public RiskScoringService(RiskScoringEngine engine, AuditService auditService) {
        this.engine = engine;
        this.auditService = auditService;
    }
    
    /**
     * Calculates the risk score for a transaction and audits the result.
     * 
     * @param transaction The transaction to score
     * @return The calculated risk score
     */
    public RiskScore calculateRiskScore(Transaction transaction) {
        log.info("Scoring transaction {}", transaction.transactionId());
        
        // Calculate risk score
        RiskScore riskScore = engine.calculateScore(transaction);
        
        // Audit the result
        auditService.auditRiskScore(riskScore);
        
        return riskScore;
    }
}
