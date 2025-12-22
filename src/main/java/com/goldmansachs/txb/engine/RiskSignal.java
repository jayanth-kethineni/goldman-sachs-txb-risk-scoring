package com.goldmansachs.txb.engine;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RiskSignal {
    String reasonCode;
    Integer scoreIncrement;
    String description;
}
