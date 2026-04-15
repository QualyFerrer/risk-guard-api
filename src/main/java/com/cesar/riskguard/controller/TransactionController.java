package com.cesar.riskguard.controller;

import com.cesar.riskguard.dto.TransactionRequestDTO;
import com.cesar.riskguard.dto.TransactionResponseDTO;
import com.cesar.riskguard.entity.FraudAlert;
import com.cesar.riskguard.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "Processamento e auditoria de transações financeiras")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

     @Operation(summary = "Processar transação",
     description = " Analisa a transação com o motor de fraude e retorna o veredito (APPROVED, FLAGGED ou BLOCKED)")
    @PostMapping("/user/{userId}")
    public ResponseEntity<TransactionResponseDTO> register(
            @PathVariable Long userId,
            @Valid @RequestBody TransactionRequestDTO dto) {

        TransactionResponseDTO response = transactionService.processTransaction(userId, dto);

        if (response.getStatus().equals(com.cesar.riskguard.enums.TransactionStatus.BLOCKED)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Histórico de transações do usuário")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponseDTO>> getHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.findByUserId(userId));
    }

    @Operation(description = "Alertas de fraude do usuário")
    @GetMapping("/alerts/user/{userId}")
    public ResponseEntity<List<FraudAlert>> getAlerts(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getUserAlertHistory(userId));
    }
}