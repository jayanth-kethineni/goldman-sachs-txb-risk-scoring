package com.goldmansachs.txb.infrastructure.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "transaction_risk_scores")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRiskScoreEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId;
    
    @Column(name = "risk_score", nullable = false)
    private Integer riskScore;
    
    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel;
    
    @Column(name = "reason_codes", columnDefinition = "text[]")
    private String[] reasonCodes;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "created_by", nullable = false)
    private String createdBy;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (createdBy == null) {
            createdBy = "SYSTEM";
        }
    }
}
