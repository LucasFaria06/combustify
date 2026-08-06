package com.combustify.domain.service;

import com.combustify.api.dto.AuthResponse;
import com.combustify.api.dto.LoginRequest;
import com.combustify.api.dto.SignupRequest;
import com.combustify.domain.entity.User;
import com.combustify.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .displayName(request.displayName())
                .subscriptionPlan(User.SubscriptionPlan.FREE)
                .isActive(true)
                .queriesUsedToday(0)
                .build();

        user = userRepository.save(user);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = generateRefreshToken(user);

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                accessToken,
                refreshToken,
                user.getSubscriptionPlan().toString(),
                3600000
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Senha incorreta");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = generateRefreshToken(user);

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                accessToken,
                refreshToken,
                user.getSubscriptionPlan().toString(),
                3600000
        );
    }

    public AuthResponse refreshToken(String refreshToken) {
        String userId = extractUserIdFromRefreshToken(refreshToken);
        User user = userRepository.findById(java.util.UUID.fromString(userId))
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = generateRefreshToken(user);

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                newAccessToken,
                newRefreshToken,
                user.getSubscriptionPlan().toString(),
                3600000
        );
    }

    private String generateRefreshToken(User user) {
        return jwtService.generateToken(user);
    }

    private String extractUserIdFromRefreshToken(String token) {
        return jwtService.extractUserId(token);
    }
}
