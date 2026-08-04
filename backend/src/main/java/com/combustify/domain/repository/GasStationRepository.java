package com.combustify.domain.repository;

import com.combustify.domain.entity.GasStation;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GasStationRepository extends JpaRepository<GasStation, UUID> {
    List<GasStation> findByCity(String city);

    @Query("SELECT g FROM GasStation g WHERE g.city = :city AND g.isActive = true ORDER BY g.name")
    List<GasStation> findActiveByCityOrderByName(@Param("city") String city);

    @Query(value = "SELECT * FROM gas_stations WHERE ST_DWithin(location, :location, :radiusMeters) AND is_active = true ORDER BY ST_Distance(location, :location)", nativeQuery = true)
    List<GasStation> findNearby(@Param("location") Point location, @Param("radiusMeters") Double radiusMeters);

    @Query("SELECT g FROM GasStation g WHERE g.state = :state AND g.isActive = true")
    List<GasStation> findByState(@Param("state") String state);
}
