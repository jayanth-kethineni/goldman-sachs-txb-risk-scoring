package com.goldmansachs.txb.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistoryEntity, UUID> {
    Optional<TransactionHistoryEntity> findByClientIdAndBeneficiaryId(String clientId, String beneficiaryId);
}
