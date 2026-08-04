package com.combustify.api.controller;

import com.combustify.domain.entity.Price;
import com.combustify.domain.service.PriceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/prices")
public class PriceController {

    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping("/{stationId}")
    public ResponseEntity<Map<Price.FuelType, PriceService.PriceDTO>> getPricesByStation(
            @PathVariable UUID stationId,
            HttpServletRequest request) {

        UUID userId = extractUserId(request);
        Map<Price.FuelType, PriceService.PriceDTO> prices = priceService.getPricesByStation(stationId, userId);
        return ResponseEntity.ok(prices);
    }

    @GetMapping("/{stationId}/history")
    public ResponseEntity<List<PriceService.PriceDTO>> getPriceHistory(
            @PathVariable UUID stationId,
            HttpServletRequest request) {

        UUID userId = extractUserId(request);
        List<PriceService.PriceDTO> history = priceService.getPriceHistory(stationId, userId);
        return ResponseEntity.ok(history);
    }

    @PostMapping
    public ResponseEntity<PriceReportResponse> reportPrice(
            @RequestBody ReportPriceRequest request,
            HttpServletRequest httpRequest) {

        UUID userId = extractUserId(httpRequest);
        priceService.reportPrice(request.stationId(), request.fuelType(), request.price(), userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PriceReportResponse(
                        "Preço reportado com sucesso",
                        request.fuelType().toString(),
                        request.price()
                ));
    }

    private UUID extractUserId(HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        if (userId == null) {
            throw new IllegalArgumentException("Usuário não autenticado. Envie o token JWT no header Authorization");
        }
        return userId;
    }

    public record ReportPriceRequest(
            UUID stationId,
            Price.FuelType fuelType,
            BigDecimal price
    ) {}

    public record PriceReportResponse(
            String message,
            String fuelType,
            BigDecimal price
    ) {}

}
