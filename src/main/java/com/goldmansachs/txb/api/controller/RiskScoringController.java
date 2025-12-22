package com.goldmansachs.txb.api.controller;

import com.goldmansachs.txb.api.dto.RiskScoreRequest;
import com.goldmansachs.txb.api.dto.RiskScoreResponse;
import com.goldmansachs.txb.domain.RiskScoringService;
import com.goldmansachs.txb.domain.model.RiskScore;
import com.goldmansachs.txb.domain.model.Transaction;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/v1/scores")
@RequiredArgsConstructor
public class RiskScoringController {
    
    private final RiskScoringService service;
    
    @PostMapping("/calculate")
    public ResponseEntity<RiskScoreResponse> calculateRiskScore(@Valid @RequestBody RiskScoreRequest request) {
        Transaction transaction = Transaction.builder()
                .transactionId(request.getTransactionId())
                .clientId(request.getClientId())
                .beneficiaryId(request.getBeneficiaryId())
                .beneficiaryCountry(request.getBeneficiaryCountry())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .timestamp(Instant.now())
                .build();
        
        RiskScore riskScore = service.calculateRiskScore(transaction);
        
        RiskScoreResponse response = RiskScoreResponse.builder()
                .transactionId(riskScore.getTransactionId())
                .riskScore(riskScore.getScore())
                .riskLevel(riskScore.getRiskLevel())
                .reasonCodes(riskScore.getReasonCodes())
                .calculationTimeMs(riskScore.getCalculationTimeMs())
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
