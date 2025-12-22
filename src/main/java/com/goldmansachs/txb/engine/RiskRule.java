package com.goldmansachs.txb.engine;

import com.goldmansachs.txb.domain.model.Transaction;

public interface RiskRule {
    
    /**
     * Evaluates the transaction and returns a risk signal if the rule is triggered.
     * 
     * @param transaction The transaction to evaluate
     * @return RiskSignal if the rule is triggered, null otherwise
     */
    RiskSignal evaluate(Transaction transaction);
}
