package com.combustify.domain.service;

import com.combustify.domain.entity.User;
import com.combustify.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class RateLimitService {

    private final UserRepository userRepository;

    public RateLimitService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void checkRateLimit(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        int limit = getQueryLimit(user.getSubscriptionPlan());
        int used = user.getQueriesUsedToday() != null ? user.getQueriesUsedToday() : 0;

        if (used >= limit) {
            throw new IllegalStateException(
                    "Limite de " + limit + " consultas/dia atingido. Upgrade seu plano!"
            );
        }

        resetIfNewDay(user);
        user.setQueriesUsedToday(used + 1);
        userRepository.save(user);
    }

    private int getQueryLimit(User.SubscriptionPlan plan) {
        return switch (plan) {
            case FREE -> 5;
            case BASIC -> 200;
            case PRO -> Integer.MAX_VALUE;
            case BUSINESS -> Integer.MAX_VALUE;
        };
    }

    private void resetIfNewDay(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastReset = user.getLastQueryReset();

        if (lastReset == null || lastReset.toLocalDate().isBefore(now.toLocalDate())) {
            user.setQueriesUsedToday(0);
            user.setLastQueryReset(now);
        }
    }

}
