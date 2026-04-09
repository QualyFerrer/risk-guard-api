package com.cesar.riskguard.fraud;

import com.cesar.riskguard.entity.User;
import com.cesar.riskguard.repository.TransactionRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class FraudAnalyzer {

    private final TransactionRepository transactionRepository;

    public FraudAnalyzer(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public FraudResult analyze(User user, BigDecimal amount) {
        int score = 0;
        List<String> reasons = new ArrayList<>();

        // Regra 1 — Valor muito acima da média do usuário (3x)
        if (user.getAverageTransactionAmount() != null && user.getAverageTransactionAmount().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal threshold = user.getAverageTransactionAmount().multiply(BigDecimal.valueOf(3));
            if (amount.compareTo(threshold) > 0) {
                score += 40;
                reasons.add("Valor 3x acima da média do usuário");
            }
        }

        // Regra 2 — Muitas transações no último minuto
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        int recentCount = transactionRepository.countByUserIdAndCreatedAtAfter(user.getId(), oneMinuteAgo);
        if (recentCount >= 5) {
            score += 80;
            reasons.add("Suspeita de ataque de repetição (5+ transações/min)");
        }

        // Regra 3 — Valor absoluto crítico (Acima de 10k)
        if (amount.compareTo(BigDecimal.valueOf(10000)) > 0) {
            score += 60;
            reasons.add("Valor crítico acima de R$ 10.000");
        }

        // CORREÇÃO: Normalizar score para o teto de 100
        int finalScore = Math.min(score, 100);
        String finalReason = String.join(". ", reasons);

        return new FraudResult(finalScore, finalReason);
    }
}