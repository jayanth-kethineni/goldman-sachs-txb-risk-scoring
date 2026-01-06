package com.goldmansachs.txb.engine.rules;

import com.goldmansachs.txb.domain.model.Transaction;
import com.goldmansachs.txb.engine.RiskRule;
import com.goldmansachs.txb.engine.RiskSignal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Rule that checks if the beneficiary is in a high-risk country.
 * Weight: +250
 * 
 * High-risk countries are configurable via application properties.
 * This list would typically be maintained by compliance teams and updated regularly
 * based on OFAC sanctions, FATF grey/black lists, and internal risk assessments.
 */
@Component
public class HighRiskCountryRule implements RiskRule {
    
    private static final Logger log = LoggerFactory.getLogger(HighRiskCountryRule.class);
    private static final String REASON_CODE = "HIGH_RISK_COUNTRY";
    private static final int WEIGHT = 250;
    
    private final Set<String> highRiskCountries;
    
    public HighRiskCountryRule(@Value("${txb.risk.high-risk-countries:IR,KP,SY,CU,VE}") String highRiskCountriesConfig) {
        this.highRiskCountries = Arrays.stream(highRiskCountriesConfig.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
        log.info("Initialized HighRiskCountryRule with countries: {}", this.highRiskCountries);
    }
    
    @Override
    public RiskSignal evaluate(Transaction transaction) {
        boolean isHighRiskCountry = highRiskCountries.contains(transaction.country());
        
        if (isHighRiskCountry) {
            log.info("HIGH_RISK_COUNTRY triggered for transaction {}. Country: {}",
                     transaction.transactionId(), transaction.country());
            return RiskSignal.triggered(REASON_CODE, WEIGHT);
        }
        
        return RiskSignal.notTriggered(REASON_CODE);
    }
}
