package com.goldmansachs.txb.domain;

import com.goldmansachs.txb.domain.model.RiskScore;
import com.goldmansachs.txb.domain.model.Transaction;
import com.goldmansachs.txb.engine.RiskScoringEngine;
import com.goldmansachs.txb.infrastructure.audit.AuditService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiskScoringService {
    
    private final RiskScoringEngine engine;
    private final AuditService auditService;
    
    @CircuitBreaker(name = "transactionHistory", fallbackMethod = "calculateScoreFallback")
    public RiskScore calculateRiskScore(Transaction transaction) {
        log.info("Calculating risk score for transaction: {}", transaction.getTransactionId());
        
        RiskScore riskScore = engine.calculateScore(transaction);
        
        // Audit trail for compliance
        auditService.auditRiskScore(riskScore);
        
        return riskScore;
    }
    
    private RiskScore calculateScoreFallback(Transaction transaction, Exception e) {
        log.error("Circuit breaker activated for transaction: {}. Returning conservative risk score.", 
                transaction.getTransactionId(), e);
        
        // Conservative fallback: return HIGH risk to be safe
        return RiskScore.builder()
                .transactionId(transaction.getTransactionId())
                .score(600)
                .riskLevel("HIGH")
                .reasonCodes(java.util.List.of("SYSTEM_UNAVAILABLE"))
                .calculationTimeMs(0L)
                .build();
    }
}
