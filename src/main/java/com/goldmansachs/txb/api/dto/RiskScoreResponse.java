package com.goldmansachs.txb.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskScoreResponse {
    
    private String transactionId;
    private Integer riskScore;
    private String riskLevel;
    private List<String> reasonCodes;
    private Long calculationTimeMs;
}
