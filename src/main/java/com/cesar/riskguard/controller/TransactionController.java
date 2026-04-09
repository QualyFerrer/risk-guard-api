package com.cesar.riskguard.controller;

import com.cesar.riskguard.dto.TransactionRequestDTO;
import com.cesar.riskguard.dto.TransactionResponseDTO;
import com.cesar.riskguard.entity.FraudAlert;
import com.cesar.riskguard.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Endpoint principal: Recebe a transação e retorna o veredito de risco
    @PostMapping("/user/{userId}")
    public ResponseEntity<TransactionResponseDTO> register(
            @PathVariable Long userId,
            @Valid @RequestBody TransactionRequestDTO dto) {

        TransactionResponseDTO response = transactionService.processTransaction(userId, dto);

        // Se for bloqueado, retorna 403 Forbidden para indicar violação de segurança
        if (response.getStatus().equals(com.cesar.riskguard.enums.TransactionStatus.BLOCKED)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Consulta histórico de transações de um usuário (Auditoria)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponseDTO>> getHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.findByUserId(userId));
    }

    // Consulta alertas de fraude gerados para um usuário
    @GetMapping("/alerts/user/{userId}")
    public ResponseEntity<List<FraudAlert>> getAlerts(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getUserAlertHistory(userId));
    }
}