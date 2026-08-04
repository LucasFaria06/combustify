package com.combustify.api.controller;

import com.combustify.domain.entity.User;
import com.combustify.domain.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        User user = authService.signup(request.email(), request.password(), request.displayName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SignupResponse(
                        user.getId().toString(),
                        user.getEmail(),
                        user.getDisplayName(),
                        "Conta criada com sucesso"
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.email(), request.password());
        return ResponseEntity.ok(new LoginResponse(token, "Login realizado com sucesso"));
    }

    public record SignupRequest(String email, String password, String displayName) {}

    public record SignupResponse(String id, String email, String displayName, String message) {}

    public record LoginRequest(String email, String password) {}

    public record LoginResponse(String token, String message) {}

}
