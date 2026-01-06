package com.goldmansachs.txb.engine;

import com.goldmansachs.txb.domain.model.Transaction;

/**
 * Interface for all risk rules.
 * Each rule evaluates a transaction and produces a risk signal.
 */
public interface RiskRule {
    /**
     * Evaluates the transaction against this rule.
     * 
     * @param transaction The transaction to evaluate
     * @return A RiskSignal indicating whether the rule was triggered and its weight
     */
    RiskSignal evaluate(Transaction transaction);
}
