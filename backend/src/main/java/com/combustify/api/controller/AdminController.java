package com.combustify.api.controller;

import com.combustify.api.dto.AdminStatsResponse;
import com.combustify.domain.repository.GasStationRepository;
import com.combustify.domain.repository.PriceRepository;
import com.combustify.domain.repository.SubscriptionRepository;
import com.combustify.domain.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final GasStationRepository gasStationRepository;
    private final PriceRepository priceRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    public AdminController(GasStationRepository gasStationRepository,
                         PriceRepository priceRepository,
                         UserRepository userRepository,
                         SubscriptionRepository subscriptionRepository) {
        this.gasStationRepository = gasStationRepository;
        this.priceRepository = priceRepository;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        long totalStations = gasStationRepository.count();
        long totalPrices = priceRepository.count();
        long totalUsers = userRepository.count();
        long activeSubscriptions = subscriptionRepository.countByStatus("ACTIVE");

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
}
