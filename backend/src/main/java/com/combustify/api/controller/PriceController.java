package com.combustify.api.controller;

import com.combustify.api.dto.PriceResponse;
import com.combustify.api.dto.ReportPriceRequest;
import com.combustify.domain.entity.Price;
import com.combustify.domain.service.PriceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/prices")
public class PriceController {

    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping("/history/{stationId}")
    public ResponseEntity<List<PriceResponse>> getPriceHistory(@PathVariable UUID stationId) {
        List<Price> prices = priceService.getPriceHistory(stationId);
        return ResponseEntity.ok(prices.stream()
                .map(this::toResponse)
                .toList());
    }

    @PostMapping
    public ResponseEntity<PriceResponse> reportPrice(
            @Valid @RequestBody ReportPriceRequest request,
            Authentication auth) {

        UUID userId = UUID.fromString(auth.getName());
        Price price = priceService.reportPrice(
                request.stationId(),
                request.fuelType(),
                request.price(),
                userId
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(price));
    }

    @PostMapping("/{priceId}/verify")
    public ResponseEntity<Void> verifyPrice(
            @PathVariable UUID priceId,
            Authentication auth) {

        UUID userId = UUID.fromString(auth.getName());
        priceService.verifyPrice(priceId, userId);
        return ResponseEntity.noContent().build();
    }

    private PriceResponse toResponse(Price price) {
        return new PriceResponse(
                price.getId(),
                price.getStation().getId(),
                price.getFuelType().toString(),
                price.getPrice(),
                price.getReportedBy().getId(),
                price.getVerificationCount(),
                price.getReportedAt()
        );
    }
}
