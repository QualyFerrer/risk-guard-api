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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5432/riskguard",
        "spring.datasource.username=riskguard_user",
        "spring.datasource.password=riskguard_pass"
})
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
        user.setPassword("$2a$10$hasheado");
        user.setBalance(BigDecimal.valueOf(20000));
        user.setAverageTransactionAmount(BigDecimal.valueOf(1000));
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(Role.USER);

        savedUser = userRepository.save(user);
    }

    @Test
    @DisplayName("Should block transaction and return 403 when risk score is critical")
    @WithMockUser
    void shouldReturn403WhenTransactionIsBlocked() throws Exception {
        TransactionRequestDTO dto = new TransactionRequestDTO();
        dto.setAmount(15000.0);
        dto.setDescription("High risk transfer");

        mockMvc.perform(post("/api/transactions/user/" + savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("BLOCKED"))
                .andExpect(jsonPath("$.riskScore").value(100));
    }

    @Test
    @DisplayName("Should approve normal transaction and return 201")
    @WithMockUser
    void shouldApproveNormalTransaction() throws Exception {
        TransactionRequestDTO dto = new TransactionRequestDTO();
        dto.setAmount(50.0);
        dto.setDescription("Coffee");

        mockMvc.perform(post("/api/transactions/user/" + savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.riskScore").value(0));
    }

    @Test
    @DisplayName("Should return 404 when user does not exist")
    @WithMockUser
    void shouldReturn404WhenUserNotFound() throws Exception {
        TransactionRequestDTO dto = new TransactionRequestDTO();
        dto.setAmount(100.0);
        dto.setDescription("Test");

        mockMvc.perform(post("/api/transactions/user/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }
}