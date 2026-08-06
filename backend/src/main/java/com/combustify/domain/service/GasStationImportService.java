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

@Service
@Transactional
public class GasStationImportService {

    private final GasStationRepository gasStationRepository;
    private final GeometryFactory geometryFactory;

    public GasStationImportService(GasStationRepository gasStationRepository) {
        this.gasStationRepository = gasStationRepository;
        this.geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    }

    public GasStationImportResult importStations(List<ImportStationRequest> requests) {
        int imported = 0;
        int failed = 0;

        for (ImportStationRequest request : requests) {
            try {
                saveStation(request);
                imported++;
            } catch (Exception e) {
                failed++;
            }
        }

        return new GasStationImportResult(imported, failed);
    }

    private void saveStation(ImportStationRequest request) {
        if (request.name() == null || request.city() == null) {
            throw new IllegalArgumentException("Nome e cidade são obrigatórios");
        }

        Point location = geometryFactory.createPoint(
                new Coordinate(request.longitude(), request.latitude())
        );

        GasStation station = GasStation.builder()
                .name(request.name())
                .location(location)
                .city(request.city())
                .state(request.state())
                .address(request.address())
                .zipCode(request.zipCode())
                .isActive(true)
                .build();

        gasStationRepository.save(station);
    }

    public record ImportStationRequest(
            String name,
            Double latitude,
            Double longitude,
            String city,
            String state,
            String address,
            String zipCode
    ) {}

    public record GasStationImportResult(
            int imported,
            int failed
    ) {}

}
