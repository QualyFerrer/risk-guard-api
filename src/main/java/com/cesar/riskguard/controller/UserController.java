package com.cesar.riskguard.controller;

import com.cesar.riskguard.dto.UserRegisterDTO;
import com.cesar.riskguard.dto.UserResponseDTO;
import com.cesar.riskguard.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> register (
            @Valid @RequestBody UserRegisterDTO dto) {
                UserResponseDTO response = userService.register(dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO>findById(@PathVariable Long id){
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> findAll(){
        return ResponseEntity.ok(userService.findAll());
    }
}
