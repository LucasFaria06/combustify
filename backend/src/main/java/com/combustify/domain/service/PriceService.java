package com.combustify.domain.service;

import com.combustify.domain.entity.GasStation;
import com.combustify.domain.entity.Price;
import com.combustify.domain.entity.User;
import com.combustify.domain.repository.GasStationRepository;
import com.combustify.domain.repository.PriceRepository;
import com.combustify.domain.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PriceService {

    private final PriceRepository priceRepository;
    private final GasStationRepository gasStationRepository;
    private final UserRepository userRepository;
    private final RateLimitService rateLimitService;

    public PriceService(PriceRepository priceRepository, GasStationRepository gasStationRepository,
                        UserRepository userRepository, RateLimitService rateLimitService) {
        this.priceRepository = priceRepository;
        this.gasStationRepository = gasStationRepository;
        this.userRepository = userRepository;
        this.rateLimitService = rateLimitService;
    }

    @Cacheable(value = "prices", key = "#stationId")
    public Map<Price.FuelType, PriceDTO> getPricesByStation(UUID stationId, UUID userId) {
        rateLimitService.checkRateLimit(userId);

        GasStation station = gasStationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("Posto não encontrado"));

        return List.of(Price.FuelType.values()).stream()
                .collect(Collectors.toMap(
                        fuelType -> fuelType,
                        fuelType -> getLatestPrice(stationId, fuelType)
                ));
    }

    @Cacheable(value = "latest_price", key = "#stationId + ':' + #fuelType")
    public PriceDTO getLatestPrice(UUID stationId, Price.FuelType fuelType) {
        return priceRepository.findLatestPriceByStationAndFuel(stationId, fuelType)
                .map(PriceDTO::from)
                .orElse(new PriceDTO(null, stationId.toString(), fuelType.toString(), null, 0, null));
    }

    public List<PriceDTO> getPriceHistory(UUID stationId, UUID userId) {
        rateLimitService.checkRateLimit(userId);

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return priceRepository.findByStationIdAndReportedAtAfterOrderByReportedAtDesc(stationId, sevenDaysAgo)
                .stream()
                .map(PriceDTO::from)
                .toList();
    }

    public void reportPrice(UUID stationId, Price.FuelType fuelType, BigDecimal price, UUID userId) {
        rateLimitService.checkRateLimit(userId);

        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero");
        }

        GasStation station = gasStationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("Posto não encontrado"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Price priceEntry = Price.builder()
                .station(station)
                .fuelType(fuelType)
                .price(price)
                .reportedBy(user)
                .verificationCount(1)
                .build();

        priceRepository.save(priceEntry);

        // Invalidar cache
        org.springframework.cache.CacheManager cacheManager = null;
        if (cacheManager != null) {
            cacheManager.getCache("prices").evict(stationId);
            cacheManager.getCache("latest_price").evict(stationId + ":" + fuelType);
        }
    }

    public record PriceDTO(
            String id,
            String stationId,
            String fuelType,
            BigDecimal price,
            Integer verificationCount,
            LocalDateTime reportedAt
    ) {
        public static PriceDTO from(Price price) {
            return new PriceDTO(
                    price.getId().toString(),
                    price.getStation().getId().toString(),
                    price.getFuelType().toString(),
                    price.getPrice(),
                    price.getVerificationCount(),
                    price.getReportedAt()
            );
        }
    }

}
