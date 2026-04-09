package com.cesar.riskguard.fraud;

import com.cesar.riskguard.entity.User;
import com.cesar.riskguard.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FraudAnalyzerTest {

    private FraudAnalyzer fraudAnalyzer;
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository = Mockito.mock(TransactionRepository.class);
        fraudAnalyzer = new FraudAnalyzer(transactionRepository);
    }

    @Test
    @DisplayName("Deve bloquear transação com valor absoluto muito alto (>10k)")
    void shouldFlagVeryHighAmounts() {
        User user = new User();
        user.setId(1L);
        user.setAverageTransactionAmount(BigDecimal.ZERO);

        FraudResult result = fraudAnalyzer.analyze(user, BigDecimal.valueOf(11000));

        assertTrue(result.getScore() >= 60);
        assertTrue(result.getReason().contains("Valor crítico acima de R$ 10.000"));
    }
}