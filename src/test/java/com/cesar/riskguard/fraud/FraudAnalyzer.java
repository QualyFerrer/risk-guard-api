package com.cesar.riskguard.fraud;

import com.cesar.riskguard.entity.User;
import com.cesar.riskguard.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FraudAnalyzerTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private FraudAnalyzer fraudAnalyzer;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setAverageTransactionAmount(new BigDecimal("1000.00"));

        when(transactionRepository.countByUserIdAndCreatedAtAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(0);
    }

    @Test
    @DisplayName("Deve retornar score 0 para transação dentro da normalidade")
    void shouldReturnZeroScoreForNormalTransaction() {
        FraudResult result = fraudAnalyzer.analyze(user, new BigDecimal("500.00"));

        assertThat(result.getScore()).isEqualTo(0);
        assertThat(result.getReason()).isEmpty();
    }

    @Test
    @DisplayName("Deve adicionar 40 pontos quando valor for 3x acima da média do usuário")
    void shouldAdd40PointsWhenAmountIsThreeTimesAboveAverage() {
        FraudResult result = fraudAnalyzer.analyze(user, new BigDecimal("4000.00"));

        assertThat(result.getScore()).isEqualTo(40);
        assertThat(result.getReason()).contains("3x acima da média");
    }

    @Test
    @DisplayName("Deve adicionar 60 pontos para valor crítico acima de R$ 10.000")
    void shouldAdd60PointsForCriticalAmount() {
        FraudResult result = fraudAnalyzer.analyze(user, new BigDecimal("11000.00"));

        assertThat(result.getScore()).isEqualTo(60);
        assertThat(result.getReason()).contains("R$ 10.000");
    }

    @Test
    @DisplayName("Deve adicionar 80 pontos para ataque de repetição (5+ transações/min)")
    void shouldAdd80PointsForReplayAttack() {
        when(transactionRepository.countByUserIdAndCreatedAtAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(5);

        FraudResult result = fraudAnalyzer.analyze(user, new BigDecimal("100.00"));

        assertThat(result.getScore()).isEqualTo(80);
        assertThat(result.getReason()).contains("repetição");
    }

    @Test
    @DisplayName("Score não deve ultrapassar 100 com múltiplas regras ativas")
    void scoreShouldBeCappedAt100() {
        when(transactionRepository.countByUserIdAndCreatedAtAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(10);

        FraudResult result = fraudAnalyzer.analyze(user, new BigDecimal("50000.00"));

        assertThat(result.getScore()).isLessThanOrEqualTo(100);
    }

    @Test
    @DisplayName("Deve ignorar regra de média quando averageTransactionAmount é zero (usuário novo)")
    void shouldIgnoreAverageRuleWhenAverageIsZero() {
        user.setAverageTransactionAmount(BigDecimal.ZERO);

        FraudResult result = fraudAnalyzer.analyze(user, new BigDecimal("500.00"));

        assertThat(result.getScore()).isEqualTo(0);
    }
}