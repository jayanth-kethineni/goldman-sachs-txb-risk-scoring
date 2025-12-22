package com.goldmansachs.txb.infrastructure.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transaction_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "client_id", nullable = false)
    private String clientId;
    
    @Column(name = "beneficiary_id", nullable = false)
    private String beneficiaryId;
    
    @Column(name = "avg_amount", precision = 18, scale = 2)
    private BigDecimal avgAmount;
    
    @Column(name = "last_seen")
    private Instant lastSeen;
}
