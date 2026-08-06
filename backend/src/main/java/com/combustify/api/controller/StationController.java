package com.combustify.api.controller;

import com.combustify.api.dto.CreateStationRequest;
import com.combustify.api.dto.NearbyStationsRequest;
import com.combustify.api.dto.StationResponse;
import com.combustify.domain.entity.GasStation;
import com.combustify.domain.service.GasStationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final GasStationService gasStationService;

    public StationController(GasStationService gasStationService) {
        this.gasStationService = gasStationService;
    }

    @GetMapping
    public ResponseEntity<List<StationResponse>> listByCity(@RequestParam String city) {
        List<GasStation> stations = gasStationService.findByCityOrderByName(city);
        return ResponseEntity.ok(stations.stream()
                .map(this::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationResponse> getById(@PathVariable UUID id) {
        GasStation station = gasStationService.findById(id);
        return ResponseEntity.ok(toResponse(station));
    }

    @PostMapping("/nearby")
    public ResponseEntity<List<StationResponse>> findNearby(@Valid @RequestBody NearbyStationsRequest request) {
        List<GasStation> stations = gasStationService.findNearby(
                request.latitude(),
                request.longitude(),
                request.radiusKm()
        );
        return ResponseEntity.ok(stations.stream()
                .map(this::toResponse)
                .toList());
    }

    @PostMapping
    public ResponseEntity<StationResponse> create(@Valid @RequestBody CreateStationRequest request) {
        GasStation station = gasStationService.create(
                request.name(),
                request.latitude(),
                request.longitude(),
                request.city(),
                request.state(),
                request.zipCode(),
                request.address()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(station));
    }

    private StationResponse toResponse(GasStation station) {
        return new StationResponse(
                station.getId(),
                station.getName(),
                station.getLocation().getY(),
                station.getLocation().getX(),
                station.getCity(),
                station.getState(),
                station.getZipCode(),
                station.getAddress(),
                station.getIsActive(),
                station.getVerificationCount(),
                station.getCreatedAt()
        );
    }
}
