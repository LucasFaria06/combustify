package com.combustify.domain.repository;

import com.combustify.domain.entity.GasStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GasStationRepository extends JpaRepository<GasStation, UUID> {
    List<GasStation> findByCity(String city);

    @Query("SELECT g FROM GasStation g WHERE g.city = :city AND g.isActive = true")
    List<GasStation> findActiveByCityOrderByName(@Param("city") String city);
}
