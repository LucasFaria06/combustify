package com.combustify.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PriceResponse(
    UUID id,
    UUID stationId,
    String fuelType,
    BigDecimal price,
    UUID reportedBy,
    Integer verificationCount,
    LocalDateTime reportedAt
) {}
