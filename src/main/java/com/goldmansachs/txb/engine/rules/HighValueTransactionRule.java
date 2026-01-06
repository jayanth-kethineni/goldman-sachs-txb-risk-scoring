package com.goldmansachs.txb.engine.rules;

import com.goldmansachs.txb.domain.model.Transaction;
import com.goldmansachs.txb.engine.RiskRule;
import com.goldmansachs.txb.engine.RiskSignal;
import com.goldmansachs.txb.infrastructure.repository.TransactionHistoryEntity;
import com.goldmansachs.txb.infrastructure.repository.TransactionHistoryRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Rule that checks if the transaction amount is significantly higher than the client's average.
 * Threshold: 3x the average transaction amount
 * Weight: +200
 * 
 * Circuit Breaker: If the transaction history service is unavailable, this rule
 * does not trigger (fail-open approach, as we cannot determine the average).
 */
@Component
public class HighValueTransactionRule implements RiskRule {
    
    private static final Logger log = LoggerFactory.getLogger(HighValueTransactionRule.class);
    private static final String REASON_CODE = "HIGH_VALUE_TRANSACTION";
    private static final int WEIGHT = 200;
    private static final BigDecimal THRESHOLD_MULTIPLIER = new BigDecimal("3.0");
    
    private final TransactionHistoryRepository historyRepository;
    
    public HighValueTransactionRule(TransactionHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }
    
    @Override
    @CircuitBreaker(name = "transactionHistory", fallbackMethod = "fallbackEvaluate")
    public RiskSignal evaluate(Transaction transaction) {
        Optional<TransactionHistoryEntity> history = historyRepository
            .findByClientIdAndBeneficiaryId(transaction.clientId(), transaction.beneficiaryId());
        
        if (history.isEmpty() || history.get().getAvgAmount() == null) {
            // No history available - cannot determine if high value
            return RiskSignal.notTriggered(REASON_CODE);
        }
        
        BigDecimal avgAmount = history.get().getAvgAmount();
        BigDecimal threshold = avgAmount.multiply(THRESHOLD_MULTIPLIER);
        boolean isHighValue = transaction.amount().compareTo(threshold) > 0;
        
        if (isHighValue) {
            log.info("HIGH_VALUE_TRANSACTION triggered for transaction {}. Amount: {}, Avg: {}, Threshold: {}",
                     transaction.transactionId(), transaction.amount(), avgAmount, threshold);
            return RiskSignal.triggered(REASON_CODE, WEIGHT);
        }
        
        return RiskSignal.notTriggered(REASON_CODE);
    }
    
    /**
     * Fallback method when circuit breaker is open.
     * Does not trigger the rule (fail-open approach).
     */
    public RiskSignal fallbackEvaluate(Transaction transaction, Exception ex) {
        log.warn("Circuit breaker open for HIGH_VALUE_TRANSACTION rule. Not triggering rule. Error: {}", 
                 ex.getMessage());
        return RiskSignal.notTriggered(REASON_CODE);
    }
}
