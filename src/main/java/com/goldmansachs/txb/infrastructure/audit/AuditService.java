package com.goldmansachs.txb.infrastructure.audit;

import com.goldmansachs.txb.domain.model.RiskScore;
import com.goldmansachs.txb.infrastructure.repository.TransactionRiskScoreEntity;
import com.goldmansachs.txb.infrastructure.repository.TransactionRiskScoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for maintaining an immutable audit trail of all risk scoring decisions.
 * Every risk score is persisted to the database with a timestamp and system user.
 * 
 * This audit trail is critical for:
 * - Regulatory compliance (demonstrating due diligence)
 * - Forensic analysis (investigating fraud patterns)
 * - System monitoring (tracking false positives/negatives)
 * - Dispute resolution (providing evidence for transaction decisions)
 */
@Service
public class AuditService {
    
    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    
    private final TransactionRiskScoreRepository repository;
    
    @Value("${txb.risk.audit-enabled:true}")
    private boolean auditEnabled;
    
    public AuditService(TransactionRiskScoreRepository repository) {
        this.repository = repository;
    }
    
    /**
     * Persists a risk score to the audit trail.
     * This operation is transactional to ensure data integrity.
     * 
     * Note: Audit failures are logged but do not block the transaction.
     * In a production system, you might want to use async processing or a message queue
     * to ensure audit records are eventually persisted.
     * 
     * @param riskScore The risk score to audit
     */
    @Transactional
    public void auditRiskScore(RiskScore riskScore) {
        if (!auditEnabled) {
            log.debug("Audit is disabled, skipping audit for transaction {}", riskScore.transactionId());
            return;
        }
        
        try {
            TransactionRiskScoreEntity entity = TransactionRiskScoreEntity.builder()
                .transactionId(riskScore.transactionId())
                .riskScore(riskScore.score())
                .riskLevel(riskScore.level().name())
                .reasonCodes(riskScore.reasonCodes().toArray(new String[0]))
                .build();
            
            repository.save(entity);
            log.info("Audited risk score for transaction {} (score={}, level={})", 
                     riskScore.transactionId(), riskScore.score(), riskScore.level());
        } catch (Exception ex) {
            // Log but don't fail the request - audit failures should not block transactions
            // In production, consider sending to a dead letter queue for retry
            log.error("Failed to audit risk score for transaction {}: {}", 
                     riskScore.transactionId(), ex.getMessage(), ex);
        }
    }
}
