package com.goldmansachs.txb.engine.rules;

import com.goldmansachs.txb.domain.model.Transaction;
import com.goldmansachs.txb.engine.RiskRule;
import com.goldmansachs.txb.engine.RiskSignal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class HighRiskCountryRule implements RiskRule {
    
    @Value("${txb.risk.high-risk-countries}")
    private String highRiskCountriesConfig;
    
    private static final int SCORE_INCREMENT = 250;
    
    @Override
    public RiskSignal evaluate(Transaction transaction) {
        Set<String> highRiskCountries = Arrays.stream(highRiskCountriesConfig.split(","))
                .collect(Collectors.toSet());
        
        if (highRiskCountries.contains(transaction.getBeneficiaryCountry())) {
            log.info("HIGH_RISK_COUNTRY detected for transaction: {} (country: {})",
                    transaction.getTransactionId(), transaction.getBeneficiaryCountry());
            return RiskSignal.builder()
                    .reasonCode("HIGH_RISK_COUNTRY")
                    .scoreIncrement(SCORE_INCREMENT)
                    .description("The beneficiary is in a high-risk country")
                    .build();
        }
        
        return null;
    }
}
