package com.combustify.api.controller;

import com.combustify.domain.service.GasStationImportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final GasStationImportService gasStationImportService;

    public AdminController(GasStationImportService gasStationImportService) {
        this.gasStationImportService = gasStationImportService;
    }

    @PostMapping("/import-stations")
    public ResponseEntity<AdminImportResponse> importStations(
            @RequestBody List<GasStationImportService.ImportStationRequest> stations,
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {

        validateAdminKey(adminKey);

        GasStationImportService.GasStationImportResult result = gasStationImportService.importStations(stations);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AdminImportResponse(
                        "Import concluído",
                        result.imported(),
                        result.failed()
                ));
    }

    private void validateAdminKey(String adminKey) {
        // Por enquanto, apenas para MVP
        // Em produção, usar autenticação adequada
        if (adminKey == null || adminKey.isEmpty()) {
            throw new IllegalArgumentException("X-Admin-Key header obrigatório");
        }
    }

    public record AdminImportResponse(
            String message,
            int imported,
            int failed
    ) {}

}
