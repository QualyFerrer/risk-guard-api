package com.cesar.riskguard.service;

import com.cesar.riskguard.dto.TransactionRequestDTO;
import com.cesar.riskguard.dto.TransactionResponseDTO;
import com.cesar.riskguard.entity.FraudAlert;
import com.cesar.riskguard.entity.Transaction;
import com.cesar.riskguard.entity.User;
import com.cesar.riskguard.enums.TransactionStatus;
import com.cesar.riskguard.enums.TransactionType;
import com.cesar.riskguard.exceptions.BusinessException;
import com.cesar.riskguard.exceptions.ResourceNotFoundException;
import com.cesar.riskguard.fraud.FraudAnalyzer;
import com.cesar.riskguard.fraud.FraudResult;
import com.cesar.riskguard.repository.FraudAlertRepository;
import com.cesar.riskguard.repository.TransactionRepository;
import com.cesar.riskguard.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {
    private final FraudAnalyzer fraudAnalyzer;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final FraudAlertRepository fraudAlertRepository;

    public TransactionService(FraudAnalyzer fraudAnalyzer, UserRepository userRepository,
                              TransactionRepository transactionRepository, FraudAlertRepository fraudAlertRepository) {
        this.fraudAnalyzer = fraudAnalyzer;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.fraudAlertRepository = fraudAlertRepository;
    }

    @Transactional
    public TransactionResponseDTO processTransaction(Long userId, TransactionRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        BigDecimal amount = BigDecimal.valueOf(dto.getAmount());

        if (user.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("Saldo insuficiente.");
        }

        FraudResult result = fraudAnalyzer.analyze(user, amount);

        TransactionStatus status = TransactionStatus.APPROVED;
        if (result.getScore() >= 60) {
            status = TransactionStatus.BLOCKED;
        } else if (result.getScore() >= 30) {
            status = TransactionStatus.FLAGGED;
        }

        if (status != TransactionStatus.BLOCKED) {
            user.setBalance(user.getBalance().subtract(amount));
            updateAvarageTransacionAmount(user, amount);
            userRepository.save(user);
        }

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.TRANSFER);
        transaction.setStatus(status);
        transaction.setDescription(dto.getDescription());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction = transactionRepository.save(transaction);

        if (result.getScore() > 0) {
            FraudAlert alert = new FraudAlert();
            alert.setTransaction(transaction);
            alert.setUser(user);
            alert.setRiskScore(result.getScore());
            alert.setReason(result.getReason());
            alert.setDetectedAt(LocalDateTime.now());
            fraudAlertRepository.save(alert);
        }

        return mapToResponseDTO(transaction, result.getScore());
    }

    private void updateAvarageTransacionAmount(User user, BigDecimal newAmount) {
        long totalTransactions = transactionRepository.countByUserId(user.getId());


        BigDecimal currentAvg = user.getAverageTransactionAmount();
        if (currentAvg == null) currentAvg = BigDecimal.ZERO;

        BigDecimal newAvg = currentAvg
                .multiply(BigDecimal.valueOf(totalTransactions))
                .add(newAmount)
                .divide(BigDecimal.valueOf(totalTransactions + 1), 2, RoundingMode.HALF_UP);

        user.setAverageTransactionAmount(newAvg);
    }

    public List<FraudAlert> getUserAlertHistory(Long userId) {
        return fraudAlertRepository.findByTransactionUserId(userId);
    }

    public List<TransactionResponseDTO> findByUserId(Long userId) {
        return transactionRepository.findByUserId(userId).stream()
                .map(t -> mapToResponseDTO(t, null))
                .toList();
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction t, Integer score) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(t.getId());
        dto.setAmount(t.getAmount().doubleValue());
        dto.setDescription(t.getDescription());
        dto.setStatus(t.getStatus());
        dto.setRiskScore(score);
        dto.setCreatedAt(t.getCreatedAt());
        return dto;
    }
}