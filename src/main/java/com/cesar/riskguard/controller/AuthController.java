package com.cesar.riskguard.controller;

import com.cesar.riskguard.dto.LoginRequestDTO;
import com.cesar.riskguard.dto.LoginResponseDTO;
import com.cesar.riskguard.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/auth")
@Tag(name = "Auth", description = "Autenticação e geração de token JWT")
public class AuthController {
    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public ResponseEntity<LoginResponseDTO> login (@Valid @RequestBody LoginRequestDTO dto){
        return ResponseEntity.ok((authService.login(dto)));
    }
}
