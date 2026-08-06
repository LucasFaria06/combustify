package com.combustify.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record ReportPriceRequest(
    @NotNull(message = "ID do posto é obrigatório")
    UUID stationId,

    @NotNull(message = "Tipo de combustível é obrigatório")
    String fuelType,

    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser maior que 0")
    BigDecimal price,

    Double latitude,
    Double longitude
) {}
