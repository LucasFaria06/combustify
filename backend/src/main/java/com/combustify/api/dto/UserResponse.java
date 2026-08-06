package com.combustify.api.dto;

import java.util.UUID;

public record UserResponse(
    UUID id,
    String email,
    String displayName,
    String subscriptionPlan,
    Integer queriesUsedToday,
    Boolean isActive
) {}
