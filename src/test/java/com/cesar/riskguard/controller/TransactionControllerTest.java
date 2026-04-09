package com.cesar.riskguard.controller;

import com.cesar.riskguard.dto.TransactionRequestDTO;
import com.cesar.riskguard.entity.User;
import com.cesar.riskguard.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        User user = new User();
        user.setFullName("Cesar Teste");
        user.setEmail("teste@email.com");
        user.setPassword("123");
        user.setBalance(BigDecimal.valueOf(20000));
        user.setAverageTransactionAmount(BigDecimal.valueOf(100));
        savedUser = userRepository.save(user);
    }

    @Test
    void shouldReturn403WhenTransactionIsBlocked() throws Exception {
        TransactionRequestDTO dto = new TransactionRequestDTO();
        dto.setAmount(15000.0);
        dto.setDescription("Tentativa de fraude");

        mockMvc.perform(post("/api/transactions/user/" + savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
}