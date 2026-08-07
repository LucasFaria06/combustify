package com.combustify.infrastructure.external;

import com.combustify.domain.entity.GasStation;
import com.combustify.domain.repository.GasStationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Component
public class OverpassApiClient {

    private final RestTemplate restTemplate;
    private final GasStationRepository gasStationRepository;
    private final ObjectMapper objectMapper;
    private final GeometryFactory geometryFactory = new GeometryFactory(new org.locationtech.jts.geom.PrecisionModel(), 4326);

    private static final String OVERPASS_API_URL = "https://overpass-api.de/api/interpreter";

    private static final Map<String, String[]> STATES = Map.ofEntries(
            Map.entry("SP", new String[]{"São Paulo", "-54.573,-23.325,-44.164,-19.776"}),
            Map.entry("MG", new String[]{"Minas Gerais", "-46.873,-22.855,-39.552,-14.002"}),
            Map.entry("RJ", new String[]{"Rio de Janeiro", "-48.353,-23.770,-40.957,-20.769"}),
            Map.entry("BA", new String[]{"Bahia", "-46.275,-18.348,-38.246,-9.028"}),
            Map.entry("PR", new String[]{"Paraná", "-54.622,-27.545,-48.067,-22.484"}),
            Map.entry("RS", new String[]{"Rio Grande do Sul", "-56.501,-33.987,-49.693,-27.043"}),
            Map.entry("PE", new String[]{"Pernambuco", "-41.350,-9.471,-34.823,-7.266"}),
            Map.entry("CE", new String[]{"Ceará", "-41.319,-7.924,-37.249,-2.790"}),
            Map.entry("PA", new String[]{"Pará", "-60.021,-4.258,-49.648,1.303"}),
            Map.entry("SC", new String[]{"Santa Catarina", "-53.830,-29.649,-48.280,-24.569"}),
            Map.entry("GO", new String[]{"Goiás", "-53.628,-19.504,-46.037,-12.224"}),
            Map.entry("PB", new String[]{"Paraíba", "-38.305,-8.224,-34.797,-6.502"}),
            Map.entry("MA", new String[]{"Maranhão", "-46.276,-10.280,-42.814,-2.800"}),
            Map.entry("ES", new String[]{"Espírito Santo", "-41.908,-21.299,-40.260,-16.301"}),
            Map.entry("PI", new String[]{"Piauí", "-45.901,-10.897,-41.823,-2.726"}),
            Map.entry("RN", new String[]{"Rio Grande do Norte", "-37.344,-6.349,-34.950,-4.823"}),
            Map.entry("AL", new String[]{"Alagoas", "-38.269,-9.501,-35.793,-8.813"}),
            Map.entry("MT", new String[]{"Mato Grosso", "-61.977,-16.050,-51.644,-7.471"}),
            Map.entry("MS", new String[]{"Mato Grosso do Sul", "-56.501,-22.556,-53.627,-19.005"}),
            Map.entry("DF", new String[]{"Distrito Federal", "-48.277,-15.798,-47.430,-15.501"}),
            Map.entry("SE", new String[]{"Sergipe", "-37.570,-10.563,-36.410,-9.531"}),
            Map.entry("TO", new String[]{"Tocantins", "-50.646,-10.233,-48.322,-4.983"}),
            Map.entry("AC", new String[]{"Acre", "-73.985,8.760,-66.859,11.784"}),
            Map.entry("AM", new String[]{"Amazonas", "-73.985,-0.733,-59.801,5.272"}),
            Map.entry("AP", new String[]{"Amapá", "-54.803,0.829,-51.635,4.555"}),
            Map.entry("RO", new String[]{"Rondônia", "-66.042,-12.784,-60.675,-8.760"}),
            Map.entry("RR", new String[]{"Roraima", "-66.820,1.815,-60.202,4.555"})
    );

    public OverpassApiClient(RestTemplate restTemplate, GasStationRepository gasStationRepository, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.gasStationRepository = gasStationRepository;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> importStationsByState(String stateCode) throws Exception {
        stateCode = stateCode.toUpperCase();
        String[] stateInfo = STATES.get(stateCode);

        if (stateInfo == null) {
            throw new IllegalArgumentException("Estado inválido: " + stateCode);
        }

        String stateName = stateInfo[0];
        String bbox = stateInfo[1];

        log.info("Buscando postos de gasolina para {} ({})", stateName, stateCode);

        List<Map<String, Object>> stations = queryOverpass(bbox, stateCode, stateName);

        log.info("Encontrados {} postos em {}", stations.size(), stateName);

        int inserted = 0;
        for (Map<String, Object> station : stations) {
            if (saveStation(station)) {
                inserted++;
            }
        }

        return Map.of(
                "state", stateCode,
                "stateName", stateName,
                "found", stations.size(),
                "inserted", inserted
        );
    }

    public Map<String, Object> importAllStates() throws Exception {
        Map<String, Object> allResults = new LinkedHashMap<>();
        int totalFound = 0;
        int totalInserted = 0;

        for (String stateCode : STATES.keySet()) {
            try {
                Thread.sleep(2000); // Aguardar 2 segundos entre requisições
                Map<String, Object> result = importStationsByState(stateCode);
                allResults.put(stateCode, result);

                totalFound += (int) result.get("found");
                totalInserted += (int) result.get("inserted");
            } catch (Exception e) {
                log.error("Erro ao importar {}: {}", stateCode, e.getMessage());
                allResults.put(stateCode, Map.of("error", e.getMessage()));
            }
        }

        return Map.of(
                "totalFound", totalFound,
                "totalInserted", totalInserted,
                "states", allResults
        );
    }

    private List<Map<String, Object>> queryOverpass(String bbox, String stateCode, String stateName) throws Exception {
        String query = "[bbox:" + bbox + "];\n" +
                "(\n" +
                "  node[\"amenity\"=\"fuel\"];\n" +
                "  way[\"amenity\"=\"fuel\"];\n" +
                "  relation[\"amenity\"=\"fuel\"];\n" +
                ");\n" +
                "out json center;";

        try {
            String response = restTemplate.postForObject(OVERPASS_API_URL, query, String.class);
            return parseOverpassResponse(response, stateCode, stateName);
        } catch (Exception e) {
            log.error("Erro ao consultar Overpass para {}: {}", stateName, e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<Map<String, Object>> parseOverpassResponse(String response, String stateCode, String stateName) throws Exception {
        List<Map<String, Object>> stations = new ArrayList<>();
        JsonNode root = objectMapper.readTree(response);
        JsonNode elements = root.get("elements");

        if (elements == null || !elements.isArray()) {
            return stations;
        }

        for (JsonNode element : elements) {
            Map<String, Object> station = extractStationData(element, stateCode, stateName);
            if (station != null) {
                stations.add(station);
            }
        }

        return stations;
    }

    private Map<String, Object> extractStationData(JsonNode element, String stateCode, String stateName) {
        try {
            JsonNode tags = element.get("tags");
            if (tags == null) {
                return null;
            }

            double lat, lon;
            if (element.has("center")) {
                lat = element.get("center").get("lat").asDouble();
                lon = element.get("center").get("lon").asDouble();
            } else if (element.has("lat")) {
                lat = element.get("lat").asDouble();
                lon = element.get("lon").asDouble();
            } else {
                return null;
            }

            String name = tags.has("name") ? tags.get("name").asText() : "Posto de Gasolina - " + stateName;
            String address = null;

            if (tags.has("addr:full")) {
                address = tags.get("addr:full").asText();
            } else {
                StringBuilder addr = new StringBuilder();
                if (tags.has("addr:street")) {
                    addr.append(tags.get("addr:street").asText());
                }
                if (tags.has("addr:housenumber")) {
                    if (addr.length() > 0) addr.append(" ");
                    addr.append(tags.get("addr:housenumber").asText());
                }
                if (addr.length() > 0) {
                    address = addr.toString();
                }
            }

            String zipCode = tags.has("addr:postcode") ? tags.get("addr:postcode").asText() : null;

            return Map.of(
                    "name", name,
                    "latitude", lat,
                    "longitude", lon,
                    "city", stateName,
                    "state", stateCode,
                    "address", address != null ? address : "",
                    "zipCode", zipCode != null ? zipCode : ""
            );

        } catch (Exception e) {
            log.warn("Erro ao extrair dados de elemento: {}", e.getMessage());
            return null;
        }
    }

    public Map<String, Object> seedAllStatesWithTestData() {
        int totalInserted = 0;

        for (Map.Entry<String, String[]> entry : STATES.entrySet()) {
            String stateCode = entry.getKey();
            String[] stateInfo = entry.getValue();
            String stateName = stateInfo[0];

            // Gerar 3-5 postos de teste por estado
            List<Map<String, Object>> testStations = generateTestStations(stateCode, stateName);

            for (Map<String, Object> station : testStations) {
                if (saveStation(station)) {
                    totalInserted++;
                }
            }

            log.info("Seeded {} with {} stations", stateName, testStations.size());
        }

        return Map.of(
                "totalSeeded", totalInserted,
                "status", "Test data seeded successfully"
        );
    }

    private List<Map<String, Object>> generateTestStations(String stateCode, String stateName) {
        List<Map<String, Object>> stations = new ArrayList<>();

        // Coordenadas aproximadas das capitais/principais cidades
        Map<String, double[]> capitals = Map.ofEntries(
                Map.entry("AC", new double[]{-8.760, -70.533}),   // Rio Branco
                Map.entry("AL", new double[]{-9.649, -35.735}),   // Maceió
                Map.entry("AP", new double[]{0.041, -51.065}),    // Macapá
                Map.entry("AM", new double[]{-3.119, -60.022}),   // Manaus
                Map.entry("BA", new double[]{-12.972, -38.510}),  // Salvador
                Map.entry("CE", new double[]{-3.731, -38.527}),   // Fortaleza
                Map.entry("DF", new double[]{-15.789, -47.879}),  // Brasília
                Map.entry("ES", new double[]{-20.319, -40.338}),  // Vitória
                Map.entry("GO", new double[]{-16.679, -49.255}),  // Goiânia
                Map.entry("MA", new double[]{-2.897, -44.309}),   // São Luís
                Map.entry("MT", new double[]{-15.595, -56.097}),  // Cuiabá
                Map.entry("MS", new double[]{-20.443, -54.620}),  // Campo Grande
                Map.entry("MG", new double[]{-19.920, -43.927}),  // Belo Horizonte
                Map.entry("PA", new double[]{-1.456, -48.503}),   // Belém
                Map.entry("PB", new double[]{-7.115, -34.865}),   // João Pessoa
                Map.entry("PR", new double[]{-25.431, -49.257}),  // Curitiba
                Map.entry("PE", new double[]{-8.046, -34.877}),   // Recife
                Map.entry("PI", new double[]{-5.089, -42.802}),   // Teresina
                Map.entry("RJ", new double[]{-22.903, -43.209}),  // Rio de Janeiro
                Map.entry("RN", new double[]{-5.794, -35.211}),   // Natal
                Map.entry("RS", new double[]{-30.035, -51.217}),  // Porto Alegre
                Map.entry("RO", new double[]{-8.760, -63.900}),   // Porto Velho
                Map.entry("RR", new double[]{2.824, -60.675}),    // Boa Vista
                Map.entry("SC", new double[]{-27.596, -48.549}),  // Florianópolis
                Map.entry("SP", new double[]{-23.550, -46.633}),  // São Paulo
                Map.entry("SE", new double[]{-10.913, -37.071}),  // Aracaju
                Map.entry("TO", new double[]{-10.213, -48.328})   // Palmas
        );

        double[] capital = capitals.getOrDefault(stateCode, new double[]{-15.0, -50.0});
        String[] brandNames = {"Shell", "Petrobras", "Ipiranga", "Texaco", "BR"};

        for (int i = 0; i < 3; i++) {
            double lat = capital[0] + (Math.random() - 0.5) * 0.5;
            double lon = capital[1] + (Math.random() - 0.5) * 0.5;

            String name = brandNames[(int) (Math.random() * brandNames.length)] + " - " + stateName + " #" + (i + 1);

            stations.add(Map.of(
                    "name", name,
                    "latitude", lat,
                    "longitude", lon,
                    "city", stateName,
                    "state", stateCode,
                    "address", "Avenida Principal, " + (100 * (i + 1)),
                    "zipCode", ""
            ));
        }

        return stations;
    }

    private boolean saveStation(Map<String, Object> stationData) {
        try {
            String name = (String) stationData.get("name");
            double latitude = (double) stationData.get("latitude");
            double longitude = (double) stationData.get("longitude");
            String city = (String) stationData.get("city");
            String state = (String) stationData.get("state");
            String address = (String) stationData.get("address");
            String zipCode = (String) stationData.get("zipCode");

            // Criar Point para PostGIS
            Point location = geometryFactory.createPoint(new Coordinate(longitude, latitude));

            GasStation station = GasStation.builder()
                    .name(name)
                    .location(location)
                    .city(city)
                    .state(state)
                    .address(address.isEmpty() ? null : address)
                    .zipCode(zipCode.isEmpty() ? null : zipCode)
                    .isActive(true)
                    .verificationCount(0)
                    .build();

            try {
                gasStationRepository.save(station);
                return true;
            } catch (Exception e) {
                // Duplicata - ignora
                log.debug("Posto já existe: {} em {}", name, city);
                return false;
            }

        } catch (Exception e) {
            log.error("Erro ao salvar estação: {}", e.getMessage());
            return false;
        }
    }
}
