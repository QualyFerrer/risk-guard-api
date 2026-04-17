package com.cesar.riskguard.service;

import com.cesar.riskguard.dto.LoginRequestDTO;
import com.cesar.riskguard.dto.LoginResponseDTO;
import com.cesar.riskguard.entity.User;
import com.cesar.riskguard.exceptions.BusinessException;
import com.cesar.riskguard.repository.UserRepository;
import com.cesar.riskguard.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    public final JwtService jwtService;


    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtService jwtService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }
    public LoginResponseDTO login (LoginRequestDTO dto){
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BusinessException("Credenciais inválidas"));

        if(!passwordEncoder.matches(dto.getPassword(), user.getPassword())){
            throw new BusinessException("Credenciais inválidas");
        }
        String token = jwtService.generateToken(user.getEmail());
        return new LoginResponseDTO(token, user.getEmail(), user.getFullName());
    }
}
