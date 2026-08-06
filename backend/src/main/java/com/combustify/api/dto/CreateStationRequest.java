package com.combustify.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateStationRequest(
    @NotBlank(message = "Nome do posto é obrigatório")
    String name,

    @NotNull(message = "Latitude é obrigatória")
    Double latitude,

    @NotNull(message = "Longitude é obrigatória")
    Double longitude,

    @NotBlank(message = "Cidade é obrigatória")
    String city,

    String state,

    String zipCode,

    String address
) {}
