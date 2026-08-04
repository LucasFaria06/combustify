package com.combustify.api.controller;

import com.combustify.domain.entity.GasStation;
import com.combustify.domain.service.GasStationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/gas-stations")
public class GasStationController {

    private final GasStationService gasStationService;

    public GasStationController(GasStationService gasStationService) {
        this.gasStationService = gasStationService;
    }

    @GetMapping
    public ResponseEntity<List<GasStationDTO>> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false, defaultValue = "5") Double radiusKm) {

        List<GasStation> stations;

        if (latitude != null && longitude != null) {
            stations = gasStationService.findNearby(latitude, longitude, radiusKm);
        } else if (city != null) {
            stations = gasStationService.findByCityOrderByName(city);
        } else if (state != null) {
            stations = gasStationService.findByState(state);
        } else {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(stations.stream().map(GasStationDTO::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GasStationDTO> getById(@PathVariable UUID id) {
        GasStation station = gasStationService.findById(id);
        return ResponseEntity.ok(GasStationDTO.from(station));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<GasStationDTO>> getByCity(@PathVariable String city) {
        List<GasStation> stations = gasStationService.findByCityOrderByName(city);
        return ResponseEntity.ok(stations.stream().map(GasStationDTO::from).toList());
    }

    public record GasStationDTO(
            String id,
            String name,
            Double latitude,
            Double longitude,
            String city,
            String state,
            String zipCode,
            String address
    ) {
        public static GasStationDTO from(GasStation station) {
            return new GasStationDTO(
                    station.getId().toString(),
                    station.getName(),
                    station.getLocation().getY(),
                    station.getLocation().getX(),
                    station.getCity(),
                    station.getState(),
                    station.getZipCode(),
                    station.getAddress()
            );
        }
    }

}
