package com.combustify.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SubscriptionResponse(
    UUID id,
    String plan,
    String status,
    LocalDateTime startsAt,
    LocalDateTime endsAt,
    String paymentMethod
) {}
