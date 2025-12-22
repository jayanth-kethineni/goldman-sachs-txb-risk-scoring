package com.goldmansachs.txb.infrastructure.audit;

import com.goldmansachs.txb.domain.model.RiskScore;
import com.goldmansachs.txb.infrastructure.repository.TransactionRiskScoreEntity;
import com.goldmansachs.txb.infrastructure.repository.TransactionRiskScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {
    
    private final TransactionRiskScoreRepository repository;
    
    @Value("${txb.risk.audit-enabled}")
    private boolean auditEnabled;
    
    public void auditRiskScore(RiskScore riskScore) {
        if (!auditEnabled) {
            log.debug("Audit is disabled, skipping audit for transaction: {}", riskScore.getTransactionId());
            return;
        }
        
        TransactionRiskScoreEntity entity = TransactionRiskScoreEntity.builder()
                .transactionId(riskScore.getTransactionId())
                .riskScore(riskScore.getScore())
                .riskLevel(riskScore.getRiskLevel())
                .reasonCodes(riskScore.getReasonCodes().toArray(new String[0]))
                .build();
        
        repository.save(entity);
        
        log.info("Audit trail created for transaction: {} (score: {}, level: {})",
                riskScore.getTransactionId(), riskScore.getScore(), riskScore.getRiskLevel());
    }
}
