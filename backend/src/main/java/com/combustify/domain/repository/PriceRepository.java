package com.combustify.domain.repository;

import com.combustify.domain.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PriceRepository extends JpaRepository<Price, UUID> {
    List<Price> findByStationIdOrderByReportedAtDesc(UUID stationId);

    @Query("SELECT p FROM Price p WHERE p.station.id = :stationId AND p.fuelType = :fuelType ORDER BY p.reportedAt DESC LIMIT 1")
    Optional<Price> findLatestPriceByStationAndFuel(@Param("stationId") UUID stationId, @Param("fuelType") Price.FuelType fuelType);

    List<Price> findByStationIdAndReportedAtAfterOrderByReportedAtDesc(UUID stationId, LocalDateTime after);
}
