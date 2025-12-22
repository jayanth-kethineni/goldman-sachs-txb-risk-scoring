#!/bin/bash

set -e

echo "========================================="
echo "Goldman Sachs TxB Risk Scoring Demo"
echo "========================================="
echo ""

BASE_URL="http://localhost:8080/v1/scores"

echo "Scenario 1: Normal Transaction (Expected: LOW risk)"
echo "-----------------------------------------------------"
curl -X POST "$BASE_URL/calculate" \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "TXN-001",
    "clientId": "CLIENT-001",
    "beneficiaryId": "BENEFICIARY-001",
    "beneficiaryCountry": "US",
    "amount": 10000.00,
    "currency": "USD"
  }' | jq '.'
echo ""
echo "✓ Known beneficiary with normal amount"
echo "✓ Expected score: 0 (LOW risk)"
echo ""

sleep 2

echo "Scenario 2: High-Value Transaction (Expected: MEDIUM risk)"
echo "------------------------------------------------------------"
curl -X POST "$BASE_URL/calculate" \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "TXN-002",
    "clientId": "CLIENT-001",
    "beneficiaryId": "BENEFICIARY-001",
    "beneficiaryCountry": "US",
    "amount": 35000.00,
    "currency": "USD"
  }' | jq '.'
echo ""
echo "✗ Amount is 3.5x the average (35000 vs 10000)"
echo "✓ Expected score: 200 (MEDIUM risk)"
echo ""

sleep 2

echo "Scenario 3: New Beneficiary + High-Risk Country (Expected: HIGH risk)"
echo "-----------------------------------------------------------------------"
curl -X POST "$BASE_URL/calculate" \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "TXN-003",
    "clientId": "CLIENT-001",
    "beneficiaryId": "BENEFICIARY-NEW",
    "beneficiaryCountry": "IR",
    "amount": 25000.00,
    "currency": "USD"
  }' | jq '.'
echo ""
echo "✗ New beneficiary (never paid before)"
echo "✗ High-risk country (Iran)"
echo "✓ Expected score: 400 (HIGH risk)"
echo ""

sleep 2

echo "Scenario 4: All Risk Factors (Expected: CRITICAL risk)"
echo "--------------------------------------------------------"
curl -X POST "$BASE_URL/calculate" \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "TXN-004",
    "clientId": "CLIENT-002",
    "beneficiaryId": "BENEFICIARY-NEW-2",
    "beneficiaryCountry": "KP",
    "amount": 200000.00,
    "currency": "USD"
  }' | jq '.'
echo ""
echo "✗ New beneficiary"
echo "✗ High-risk country (North Korea)"
echo "✗ High-value transaction (4x average)"
echo "✓ Expected score: 600+ (CRITICAL risk)"
echo ""

echo "========================================="
echo "Demo Complete!"
echo "========================================="
echo ""
echo "Key Observations:"
echo "- Normal transaction: LOW risk (score 0)"
echo "- High-value transaction: MEDIUM risk (score 200)"
echo "- New beneficiary + high-risk country: HIGH risk (score 400)"
echo "- All risk factors: CRITICAL risk (score 600+)"
echo ""
echo "View metrics at: http://localhost:9090"
echo "Check audit trail in PostgreSQL: docker exec -it gs-txb-postgres psql -U postgres -d txb_risk -c 'SELECT * FROM transaction_risk_scores;'"
