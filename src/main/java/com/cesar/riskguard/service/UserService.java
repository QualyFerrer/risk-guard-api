package com.cesar.riskguard.service;

import com.cesar.riskguard.dto.UserRegisterDTO;
import com.cesar.riskguard.dto.UserResponseDTO;
import com.cesar.riskguard.entity.User;
import com.cesar.riskguard.enums.Role;
import com.cesar.riskguard.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO register(UserRegisterDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já cadastrado.");
        }

        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setBalance(BigDecimal.valueOf(dto.getInitialBalance().doubleValue()));
        user.setAverageTransactionAmount(BigDecimal.ZERO);
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
        return toResponseDTO(user);
    }

    public UserResponseDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        return toResponseDTO(user);
    }

    public List<UserResponseDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setBalance(user.getBalance().doubleValue());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}