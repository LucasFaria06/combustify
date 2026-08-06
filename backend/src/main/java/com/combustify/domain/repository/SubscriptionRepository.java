package com.combustify.domain.repository;

import com.combustify.domain.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.status = 'ACTIVE' ORDER BY s.startsAt DESC LIMIT 1")
    Optional<Subscription> findActiveByUserId(@Param("userId") UUID userId);

    Optional<Subscription> findByUserIdAndStatus(UUID userId, Subscription.Status status);

    Optional<Subscription> findByUserId(UUID userId);

    long countByStatus(String status);
}
