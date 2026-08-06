package com.combustify.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record StationResponse(
    UUID id,
    String name,
    Double latitude,
    Double longitude,
    String city,
    String state,
    String zipCode,
    String address,
    Boolean isActive,
    Integer verificationCount,
    LocalDateTime createdAt
) {}
