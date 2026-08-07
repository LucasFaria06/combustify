package com.combustify.api.controller;

import com.combustify.api.dto.AdminStatsResponse;
import com.combustify.domain.repository.GasStationRepository;
import com.combustify.domain.repository.PriceRepository;
import com.combustify.domain.repository.SubscriptionRepository;
import com.combustify.domain.repository.UserRepository;
import com.combustify.infrastructure.external.OverpassApiClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final GasStationRepository gasStationRepository;
    private final PriceRepository priceRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final OverpassApiClient overpassApiClient;

    public AdminController(GasStationRepository gasStationRepository,
                         PriceRepository priceRepository,
                         UserRepository userRepository,
                         SubscriptionRepository subscriptionRepository,
                         OverpassApiClient overpassApiClient) {
        this.gasStationRepository = gasStationRepository;
        this.priceRepository = priceRepository;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.overpassApiClient = overpassApiClient;
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        long totalStations = gasStationRepository.count();
        long totalPrices = priceRepository.count();
        long totalUsers = userRepository.count();
        long activeSubscriptions = 0; // TODO: fix subscription enum issue

        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        long pricesReportedToday = priceRepository.findByReportedAtAfter(today).size();

        AdminStatsResponse stats = new AdminStatsResponse(
                totalStations,
                totalPrices,
                totalUsers,
                activeSubscriptions,
                pricesReportedToday
        );

        return ResponseEntity.ok(stats);
    }

    @PostMapping("/import-stations/state/{stateCode}")
    public ResponseEntity<Map<String, Object>> importStationsByState(@PathVariable String stateCode) {
        try {
            Map<String, Object> result = overpassApiClient.importStationsByState(stateCode);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/import-stations/all-states")
    public ResponseEntity<Map<String, Object>> importAllStations() {
        try {
            Map<String, Object> result = overpassApiClient.importAllStates();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/seed-stations/all-states")
    public ResponseEntity<Map<String, Object>> seedAllStations() {
        try {
            Map<String, Object> result = overpassApiClient.seedAllStatesWithTestData();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
