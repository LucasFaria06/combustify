package com.combustify.api.controller;

import com.combustify.api.dto.SubscriptionResponse;
import com.combustify.api.dto.UpdateUserRequest;
import com.combustify.api.dto.UserResponse;
import com.combustify.domain.entity.Subscription;
import com.combustify.domain.entity.User;
import com.combustify.domain.repository.UserRepository;
import com.combustify.domain.repository.SubscriptionRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    public UserController(UserRepository userRepository, SubscriptionRepository subscriptionRepository) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getProfile(Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        return ResponseEntity.ok(toUserResponse(user));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @Valid @RequestBody UpdateUserRequest request,
            Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (request.displayName() != null) {
            user.setDisplayName(request.displayName());
        }

        user = userRepository.save(user);
        return ResponseEntity.ok(toUserResponse(user));
    }

    @GetMapping("/me/subscription")
    public ResponseEntity<SubscriptionResponse> getSubscription(Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        Subscription subscription = subscriptionRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Subscrição não encontrada"));
        return ResponseEntity.ok(toSubscriptionResponse(subscription));
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getSubscriptionPlan().toString(),
                user.getQueriesUsedToday(),
                user.getIsActive()
        );
    }

    private SubscriptionResponse toSubscriptionResponse(Subscription sub) {
        return new SubscriptionResponse(
                sub.getId(),
                sub.getPlan().toString(),
                sub.getStatus().toString(),
                sub.getStartsAt(),
                sub.getEndsAt(),
                sub.getPaymentMethod()
        );
    }
}
