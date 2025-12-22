package com.goldmansachs.txb.engine.rules;

import com.goldmansachs.txb.domain.model.Transaction;
import com.goldmansachs.txb.engine.RiskRule;
import com.goldmansachs.txb.engine.RiskSignal;
import com.goldmansachs.txb.infrastructure.repository.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewBeneficiaryRule implements RiskRule {
    
    private final TransactionHistoryRepository historyRepository;
    
    private static final int SCORE_INCREMENT = 150;
    
    @Override
    public RiskSignal evaluate(Transaction transaction) {
        boolean isNewBeneficiary = historyRepository
                .findByClientIdAndBeneficiaryId(transaction.getClientId(), transaction.getBeneficiaryId())
                .isEmpty();
        
        if (isNewBeneficiary) {
            log.info("NEW_BENEFICIARY detected for transaction: {}", transaction.getTransactionId());
            return RiskSignal.builder()
                    .reasonCode("NEW_BENEFICIARY")
                    .scoreIncrement(SCORE_INCREMENT)
                    .description("The beneficiary has never been paid by this client before")
                    .build();
        }
        
        return null;
    }
}
