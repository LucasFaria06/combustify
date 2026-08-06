package com.combustify.domain.service;

import com.combustify.domain.entity.Price;
import com.combustify.domain.entity.User;
import com.combustify.domain.repository.PriceRepository;
import com.combustify.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PriceService {

    private final PriceRepository priceRepository;
    private final UserRepository userRepository;
    private final GasStationService gasStationService;

    public PriceService(PriceRepository priceRepository, UserRepository userRepository, GasStationService gasStationService) {
        this.priceRepository = priceRepository;
        this.userRepository = userRepository;
        this.gasStationService = gasStationService;
    }

    public Price reportPrice(UUID stationId, String fuelType, BigDecimal price, UUID userId) {
        gasStationService.findById(stationId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Price.FuelType fuel;
        try {
            fuel = Price.FuelType.valueOf(fuelType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de combustível inválido: " + fuelType);
        }

        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero");
        }

        Price newPrice = Price.builder()
                .station(gasStationService.findById(stationId))
                .fuelType(fuel)
                .price(price)
                .reportedBy(user)
                .verificationCount(0)
                .build();

        return priceRepository.save(newPrice);
    }

    @Transactional(readOnly = true)
    public List<Price> getPriceHistory(UUID stationId) {
        return priceRepository.findByStationIdOrderByReportedAtDesc(stationId);
    }

    @Transactional(readOnly = true)
    public List<Price> getRecentPrices(UUID stationId, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return priceRepository.findByStationIdAndReportedAtAfterOrderByReportedAtDesc(stationId, since);
    }

    public void verifyPrice(UUID priceId, UUID userId) {
        Price price = priceRepository.findById(priceId)
                .orElseThrow(() -> new IllegalArgumentException("Preço não encontrado"));

        price.setVerificationCount(price.getVerificationCount() + 1);
        priceRepository.save(price);
    }
}
