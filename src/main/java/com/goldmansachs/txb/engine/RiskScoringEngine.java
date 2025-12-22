package com.goldmansachs.txb.engine;

import com.goldmansachs.txb.domain.model.RiskScore;
import com.goldmansachs.txb.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RiskScoringEngine {
    
    private final List<RiskRule> rules;
    
    private static final int BASE_SCORE = 0;
    
    public RiskScore calculateScore(Transaction transaction) {
        long startTime = System.currentTimeMillis();
        
        int score = BASE_SCORE;
        List<String> reasonCodes = new ArrayList<>();
        
        for (RiskRule rule : rules) {
            RiskSignal signal = rule.evaluate(transaction);
            if (signal != null) {
                score += signal.getScoreIncrement();
                reasonCodes.add(signal.getReasonCode());
                log.info("Rule triggered: {} (+{}) for transaction: {}",
                        signal.getReasonCode(), signal.getScoreIncrement(), transaction.getTransactionId());
            }
        }
        
        String riskLevel = RiskScore.calculateRiskLevel(score);
        long calculationTime = System.currentTimeMillis() - startTime;
        
        log.info("Risk score calculated for transaction {}: score={}, level={}, time={}ms",
                transaction.getTransactionId(), score, riskLevel, calculationTime);
        
        return RiskScore.builder()
                .transactionId(transaction.getTransactionId())
                .score(score)
                .riskLevel(riskLevel)
                .reasonCodes(reasonCodes)
                .calculationTimeMs(calculationTime)
                .build();
    }
}
