package com.goldmansachs.txb.engine.rules;

import com.goldmansachs.txb.domain.model.Transaction;
import com.goldmansachs.txb.engine.RiskRule;
import com.goldmansachs.txb.engine.RiskSignal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnusualTimeOfDayRule implements RiskRule {
    
    private static final int SCORE_INCREMENT = 100;
    private static final int BUSINESS_HOURS_START = 9;
    private static final int BUSINESS_HOURS_END = 17;
    
    @Override
    public RiskSignal evaluate(Transaction transaction) {
        ZonedDateTime transactionTime = transaction.getTimestamp().atZone(ZoneId.of("America/New_York"));
        int hour = transactionTime.getHour();
        
        if (hour < BUSINESS_HOURS_START || hour >= BUSINESS_HOURS_END) {
            log.info("UNUSUAL_TIME_OF_DAY detected for transaction: {} (hour: {})",
                    transaction.getTransactionId(), hour);
            return RiskSignal.builder()
                    .reasonCode("UNUSUAL_TIME_OF_DAY")
                    .scoreIncrement(SCORE_INCREMENT)
                    .description("The transaction is being made outside of normal business hours")
                    .build();
        }
        
        return null;
    }
}
