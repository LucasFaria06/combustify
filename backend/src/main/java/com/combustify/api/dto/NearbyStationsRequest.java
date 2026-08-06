package com.combustify.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record NearbyStationsRequest(
    @NotNull(message = "Latitude é obrigatória")
    Double latitude,

    @NotNull(message = "Longitude é obrigatória")
    Double longitude,

    @Positive(message = "Raio deve ser maior que 0")
    Double radiusKm
) {}
