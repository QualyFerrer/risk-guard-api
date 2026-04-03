package com.cesar.riskguard.fraud;

import com.cesar.riskguard.entity.User;
import com.cesar.riskguard.repository.TransactionRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class FraudAnalyzer {

    private final TransactionRepository transactionRepository;


    public FraudAnalyzer(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public FraudResult analyze(User user, BigDecimal amount) {
        int score = 0;
        StringBuilder reasons = new StringBuilder();

        if (user.getAverageTransactionAmount().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal threshold = user.getAverageTransactionAmount()
                    .multiply(BigDecimal.valueOf(3));
            if (amount.compareTo(threshold) > 0) {
                score += 40;
                reasons.append("Valor 3x acima da média do usuário. ");
            }
        }

        // Regra 2 — muitas transações no último minuto
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        int recentCount = transactionRepository
                .countByUserIdAndCreatedAtAfter(user.getId(), oneMinuteAgo);
        if (recentCount >= 5) {
            score += 30;
            reasons.append("Mais de 5 transações no último minuto. ");
        }

        // Regra 3 — valor absoluto alto
        if (amount.compareTo(BigDecimal.valueOf(10000)) > 0) {
            score += 20;
            reasons.append("Valor acima de R$ 10.000. ");
        }

        return new FraudResult(score, reasons.toString().trim());
    }
}

