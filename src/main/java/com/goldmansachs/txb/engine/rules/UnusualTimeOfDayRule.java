package com.goldmansachs.txb.engine.rules;

import com.goldmansachs.txb.domain.model.Transaction;
import com.goldmansachs.txb.engine.RiskRule;
import com.goldmansachs.txb.engine.RiskSignal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Rule that checks if the transaction is being made outside of normal business hours.
 * Normal business hours: 09:00 - 17:00 Eastern Time (configurable)
 * Weight: +100
 * 
 * This rule helps detect potentially fraudulent transactions that occur when
 * legitimate business operations are typically closed.
 */
@Component
public class UnusualTimeOfDayRule implements RiskRule {
    
    private static final Logger log = LoggerFactory.getLogger(UnusualTimeOfDayRule.class);
    private static final String REASON_CODE = "UNUSUAL_TIME_OF_DAY";
    private static final int WEIGHT = 100;
    
    private static final int BUSINESS_HOURS_START = 9;
    private static final int BUSINESS_HOURS_END = 17;
    private static final ZoneId BUSINESS_TIMEZONE = ZoneId.of("America/New_York");
    
    @Override
    public RiskSignal evaluate(Transaction transaction) {
        ZonedDateTime transactionTime = transaction.transactionTime().atZoneSameInstant(BUSINESS_TIMEZONE);
        int hour = transactionTime.getHour();
        
        boolean isOutsideBusinessHours = hour < BUSINESS_HOURS_START || hour >= BUSINESS_HOURS_END;
        
        if (isOutsideBusinessHours) {
            log.info("UNUSUAL_TIME_OF_DAY triggered for transaction {}. Hour: {} ET",
                     transaction.transactionId(), hour);
            return RiskSignal.triggered(REASON_CODE, WEIGHT);
        }
        
        return RiskSignal.notTriggered(REASON_CODE);
    }
}
