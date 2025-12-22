package com.goldmansachs.txb.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRiskScoreRepository extends JpaRepository<TransactionRiskScoreEntity, UUID> {
    Optional<TransactionRiskScoreEntity> findByTransactionId(String transactionId);
}
