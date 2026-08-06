package com.combustify.domain.service;

import com.combustify.domain.entity.GasStation;
import com.combustify.domain.repository.GasStationRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GasStationService {

    private final GasStationRepository gasStationRepository;
    private final GeometryFactory geometryFactory;

    public GasStationService(GasStationRepository gasStationRepository) {
        this.gasStationRepository = gasStationRepository;
        this.geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    }

    public List<GasStation> findByCityOrderByName(String city) {
        return gasStationRepository.findActiveByCityOrderByName(city);
    }

    public GasStation findById(UUID id) {
        return gasStationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Posto não encontrado"));
    }

    public List<GasStation> findNearby(Double latitude, Double longitude, Double radiusKm) {
        Point userLocation = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        return gasStationRepository.findNearby(userLocation, radiusKm * 1000.0);
    }

    public List<GasStation> findByState(String state) {
        return gasStationRepository.findByState(state);
    }

}
