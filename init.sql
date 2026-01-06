-- Enable pgcrypto extension for gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Create transaction_risk_scores table
CREATE TABLE IF NOT EXISTS transaction_risk_scores (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_id VARCHAR(255) NOT NULL UNIQUE,
    risk_score INTEGER NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    reason_codes TEXT[],
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM'
);

-- Create transaction_history table
CREATE TABLE IF NOT EXISTS transaction_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id VARCHAR(255) NOT NULL,
    beneficiary_id VARCHAR(255) NOT NULL,
    avg_amount DECIMAL(18, 2),
    last_seen TIMESTAMPTZ
);

-- Create indexes for performance
CREATE INDEX idx_transaction_risk_scores_transaction_id ON transaction_risk_scores(transaction_id);
CREATE INDEX idx_transaction_history_client_beneficiary ON transaction_history(client_id, beneficiary_id);

-- Seed transaction_history with baseline data
INSERT INTO transaction_history (client_id, beneficiary_id, avg_amount, last_seen) VALUES
('CLIENT-001', 'BENEFICIARY-001', 10000.00, NOW() - INTERVAL '30 days'),
('CLIENT-001', 'BENEFICIARY-002', 5000.00, NOW() - INTERVAL '60 days'),
('CLIENT-002', 'BENEFICIARY-003', 50000.00, NOW() - INTERVAL '10 days');
