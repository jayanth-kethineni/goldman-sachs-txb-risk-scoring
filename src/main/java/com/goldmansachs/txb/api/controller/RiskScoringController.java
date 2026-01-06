package com.goldmansachs.txb.api.controller;

import com.goldmansachs.txb.api.dto.RiskScoreRequest;
import com.goldmansachs.txb.api.dto.RiskScoreResponse;
import com.goldmansachs.txb.domain.RiskScoringService;
import com.goldmansachs.txb.domain.model.RiskScore;
import com.goldmansachs.txb.domain.model.Transaction;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API controller for risk scoring operations.
 * Exposes the /v1/scores/calculate endpoint for synchronous risk assessment.
 * 
 * This controller includes:
 * - Request validation via Jakarta Bean Validation
 * - DTO to domain model mapping
 * - Prometheus metrics for observability
 * - Structured logging for audit and debugging
 */
@RestController
@RequestMapping("/v1/scores")
public class RiskScoringController {
    
    private static final Logger log = LoggerFactory.getLogger(RiskScoringController.class);
    
    private final RiskScoringService riskScoringService;
    private final Timer scoreCalculationTimer;
    private final Counter scoreCalculationCounter;
    
    public RiskScoringController(RiskScoringService riskScoringService, MeterRegistry meterRegistry) {
        this.riskScoringService = riskScoringService;
        this.scoreCalculationTimer = Timer.builder("risk.score.calculation.time")
            .description("Time taken to calculate risk score")
            .register(meterRegistry);
        this.scoreCalculationCounter = Counter.builder("risk.score.calculation.total")
            .description("Total number of risk score calculations")
            .register(meterRegistry);
    }
    
    /**
     * Calculates a risk score for a transaction.
     * 
     * @param request The risk score request containing transaction details
     * @return The calculated risk score with reason codes
     */
    @PostMapping("/calculate")
    public ResponseEntity<RiskScoreResponse> calculateRiskScore(@Valid @RequestBody RiskScoreRequest request) {
        log.info("Received risk score request for transaction {}", request.transactionId());
        
        return scoreCalculationTimer.record(() -> {
            scoreCalculationCounter.increment();
            
            // Map DTO to domain model
            Transaction transaction = new Transaction(
                request.transactionId(),
                request.clientId(),
                request.beneficiaryId(),
                request.amount(),
                request.currency(),
                request.transactionTime(),
                request.country()
            );
            
            // Calculate risk score
            RiskScore riskScore = riskScoringService.calculateRiskScore(transaction);
            
            // Map domain model to DTO
            RiskScoreResponse response = new RiskScoreResponse(
                riskScore.transactionId(),
                riskScore.score(),
                riskScore.level().name(),
                riskScore.reasonCodes()
            );
            
            log.info("Returning risk score for transaction {}: score={}, level={}",
                     request.transactionId(), riskScore.score(), riskScore.level());
            
            return ResponseEntity.ok(response);
        });
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
