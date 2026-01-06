package com.goldmansachs.txb.engine.rules;

import com.goldmansachs.txb.domain.model.Transaction;
import com.goldmansachs.txb.engine.RiskRule;
import com.goldmansachs.txb.engine.RiskSignal;
import com.goldmansachs.txb.infrastructure.repository.TransactionHistoryRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Rule that checks if the beneficiary has never been paid by this client before.
 * Weight: +150
 * 
 * Circuit Breaker: If the transaction history service is unavailable, this rule
 * conservatively assumes the beneficiary is new (fail-safe approach).
 */
@Component
public class NewBeneficiaryRule implements RiskRule {
    
    private static final Logger log = LoggerFactory.getLogger(NewBeneficiaryRule.class);
    private static final String REASON_CODE = "NEW_BENEFICIARY";
    private static final int WEIGHT = 150;
    
    private final TransactionHistoryRepository historyRepository;
    
    public NewBeneficiaryRule(TransactionHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }
    
    @Override
    @CircuitBreaker(name = "transactionHistory", fallbackMethod = "fallbackEvaluate")
    public RiskSignal evaluate(Transaction transaction) {
        boolean isNewBeneficiary = historyRepository
            .findByClientIdAndBeneficiaryId(transaction.clientId(), transaction.beneficiaryId())
            .isEmpty();
        
        if (isNewBeneficiary) {
            log.info("NEW_BENEFICIARY triggered for transaction {}", transaction.transactionId());
            return RiskSignal.triggered(REASON_CODE, WEIGHT);
        }
        
        return RiskSignal.notTriggered(REASON_CODE);
    }
    
    /**
     * Fallback method when circuit breaker is open.
     * Conservatively assumes the beneficiary is new (fail-safe).
     */
    public RiskSignal fallbackEvaluate(Transaction transaction, Exception ex) {
        log.warn("Circuit breaker open for NEW_BENEFICIARY rule. Assuming new beneficiary (conservative). Error: {}", 
                 ex.getMessage());
        return RiskSignal.triggered(REASON_CODE, WEIGHT);
    }
}
