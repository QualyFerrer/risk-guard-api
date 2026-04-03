package com.cesar.riskguard.fraud;

public class FraudResult {

    private final int score;
    private final String reason;

    public FraudResult(int score, String reason) {
        this.score = score;
        this.reason = reason;
    }

    public int getScore() { return score; }
    public String getReason() { return reason; }
}