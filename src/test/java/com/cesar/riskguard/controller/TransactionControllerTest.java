package com.cesar.riskguard.controller;

import com.cesar.riskguard.dto.TransactionRequestDTO;
import com.cesar.riskguard.entity.User;
import com.cesar.riskguard.enums.Role;
import com.cesar.riskguard.repository.FraudAlertRepository;
import com.cesar.riskguard.repository.TransactionRepository;
import com.cesar.riskguard.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// IMPORTS ESTÁTICOS CORRETOS PARA MOCKMVC
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private FraudAlertRepository fraudAlertRepository;

    private User savedUser;

    @BeforeEach
    void setup() {
        fraudAlertRepository.deleteAll();
        transactionRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setFullName("Cesar Teste");
        user.setEmail("teste@email.com");
        user.setPassword("123");
        user.setBalance(BigDecimal.valueOf(20000));
        user.setAverageTransactionAmount(BigDecimal.valueOf(100));
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(Role.USER);

        savedUser = userRepository.save(user);
    }

    @Test
    @DisplayName("Should block transaction and return 403 when amount is over risk limit")
    void shouldReturn403WhenTransactionIsBlocked() throws Exception {
        TransactionRequestDTO dto = new TransactionRequestDTO();
        dto.setAmount(15000.0); // Ativa Regra 1 (40) + Regra 3 (60) = 100 de score
        dto.setDescription("High risk transfer");

        mockMvc.perform(post("/api/transactions/user/" + savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("BLOCKED"))
                .andExpect(jsonPath("$.riskScore").value(100)); // Valor ajustado para a lógica real
    }

    @Test
    @DisplayName("Should approve normal transaction for registered user")
    void shouldApproveNormalTransaction() throws Exception {
        TransactionRequestDTO dto = new TransactionRequestDTO();
        dto.setAmount(50.0);
        dto.setDescription("Coffee");

        mockMvc.perform(post("/api/transactions/user/" + savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}