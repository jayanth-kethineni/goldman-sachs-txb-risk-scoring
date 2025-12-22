package com.goldmansachs.txb.engine.rules;

import com.goldmansachs.txb.domain.model.Transaction;
import com.goldmansachs.txb.engine.RiskRule;
import com.goldmansachs.txb.engine.RiskSignal;
import com.goldmansachs.txb.infrastructure.repository.TransactionHistoryEntity;
import com.goldmansachs.txb.infrastructure.repository.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class HighValueTransactionRule implements RiskRule {
    
    private final TransactionHistoryRepository historyRepository;
    
    private static final int SCORE_INCREMENT = 200;
    private static final BigDecimal HIGH_VALUE_MULTIPLIER = new BigDecimal("3.0");
    
    @Override
    public RiskSignal evaluate(Transaction transaction) {
        TransactionHistoryEntity history = historyRepository
                .findByClientIdAndBeneficiaryId(transaction.getClientId(), transaction.getBeneficiaryId())
                .orElse(null);
        
        if (history != null && history.getAvgAmount() != null) {
            BigDecimal threshold = history.getAvgAmount().multiply(HIGH_VALUE_MULTIPLIER);
            
            if (transaction.getAmount().compareTo(threshold) > 0) {
                log.info("HIGH_VALUE_TRANSACTION detected for transaction: {} (amount: {}, avg: {})",
                        transaction.getTransactionId(), transaction.getAmount(), history.getAvgAmount());
                return RiskSignal.builder()
                        .reasonCode("HIGH_VALUE_TRANSACTION")
                        .scoreIncrement(SCORE_INCREMENT)
                        .description("The transaction amount is significantly higher than the client's average")
                        .build();
            }
        }
        
        return null;
    }
}
