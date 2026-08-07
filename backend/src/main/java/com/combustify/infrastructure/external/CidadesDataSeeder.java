package com.combustify.infrastructure.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CidadesDataSeeder {

    // Mapa de estados → cidades com coordenadas
    public static final Map<String, List<CidadeData>> CIDADES_POR_ESTADO = new LinkedHashMap<>();

    static {
        // SÃO PAULO - 60+ principais cidades
        CIDADES_POR_ESTADO.put("SP", Arrays.asList(
            new CidadeData("São Paulo", -23.5505, -46.6333),
            new CidadeData("Campinas", -22.9068, -47.0610),
            new CidadeData("Santos", -23.9608, -46.3339),
            new CidadeData("Ribeirão Preto", -21.1767, -47.8103),
            new CidadeData("Sorocaba", -23.5006, -47.4567),
            new CidadeData("Jundiaí", -23.1889, -46.8722),
            new CidadeData("Piracicaba", -22.7274, -47.6481),
            new CidadeData("Mauá", -23.6654, -46.4631),
            new CidadeData("São Bernardo do Campo", -23.6959, -46.5644),
            new CidadeData("Santo André", -23.6628, -46.5355),
            new CidadeData("Diadema", -23.6958, -46.6139),
            new CidadeData("Osasco", -23.5281, -46.7919),
            new CidadeData("Guarulhos", -23.4569, -46.4833),
            new CidadeData("São Caetano do Sul", -23.6201, -46.5581),
            new CidadeData("Carapicuíba", -23.5347, -46.8383),
            new CidadeData("Bauru", -22.3150, -49.0642),
            new CidadeData("Araçatuba", -21.2067, -50.4339),
            new CidadeData("Presidente Prudente", -22.1227, -51.3840),
            new CidadeData("Araraquara", -21.7964, -48.1756),
            new CidadeData("São Carlos", -22.0100, -47.8900),
            new CidadeData("Limeira", -22.5642, -47.4108),
            new CidadeData("Rio Claro", -22.4145, -47.5608),
            new CidadeData("Botucatu", -22.8757, -48.4743),
            new CidadeData("Lins", -21.6724, -49.7506),
            new CidadeData("Marília", -22.2139, -49.9469),
            new CidadeData("Franca", -20.5389, -47.4008),
            new CidadeData("Catanduva", -21.1369, -48.9828),
            new CidadeData("São José do Rio Preto", -20.8142, -49.3747),
            new CidadeData("Avaré", -23.1023, -48.9303),
            new CidadeData("Itapetininga", -23.5906, -48.0510),
            new CidadeData("Ourinhos", -22.9711, -49.8675),
            new CidadeData("Tatui", -23.2585, -47.8836),
            new CidadeData("Salto", -23.1963, -47.2922),
            new CidadeData("Itu", -23.2649, -47.2972),
            new CidadeData("Indaiatuba", -23.1815, -47.1847),
            new CidadeData("Valinhos", -23.0106, -47.0094),
            new CidadeData("Vinhedo", -23.0070, -47.0195),
            new CidadeData("Paulínia", -22.7617, -47.1453),
            new CidadeData("Sumaré", -22.8256, -47.2556),
            new CidadeData("Hortolândia", -22.8486, -47.2283),
            new CidadeData("Morungaba", -22.5108, -46.8931),
            new CidadeData("Monte Mor", -23.0397, -47.3025),
            new CidadeData("Americana", -22.7392, -47.3303),
            new CidadeData("Santa Bárbara do Oeste", -22.7417, -47.4123),
            new CidadeData("Itupeva", -23.0797, -47.1533),
            new CidadeData("Elias Fausto", -23.1758, -47.3850),
            new CidadeData("Capivari", -23.0717, -47.5214),
            new CidadeData("Mogi Guaçu", -22.3686, -46.9147),
            new CidadeData("Mogi Mirim", -22.4272, -46.8461),
            new CidadeData("Itapira", -22.4236, -46.8225),
            new CidadeData("Socorro", -22.6131, -46.3281),
            new CidadeData("Amparo", -22.7069, -46.7683),
            new CidadeData("Lindoia", -22.6022, -46.7103),
            new CidadeData("Monte Alegre do Sul", -22.6556, -46.6839),
            new CidadeData("Pedreira", -22.7378, -47.3950),
            new CidadeData("Estiva Gerbi", -22.5767, -47.1542),
            new CidadeData("Tuiuti", -22.2217, -48.6228),
            new CidadeData("Fartura", -22.4903, -49.2458)
        ));

        // MINAS GERAIS
        CIDADES_POR_ESTADO.put("MG", Arrays.asList(
            new CidadeData("Belo Horizonte", -19.9167, -43.9345),
            new CidadeData("Uberlândia", -18.9147, -48.2777),
            new CidadeData("Contagem", -19.9314, -44.0547),
            new CidadeData("Juiz de Fora", -21.7628, -43.3575),
            new CidadeData("Betim", -19.9689, -44.1947),
            new CidadeData("Montes Claros", -16.7393, -43.8597),
            new CidadeData("Governador Valadares", -18.8567, -41.9439),
            new CidadeData("Divinópolis", -20.1395, -44.8989),
            new CidadeData("Ipatinga", -19.4672, -42.4889),
            new CidadeData("Teófilo Otoni", -17.8639, -41.5131),
            new CidadeData("Passos", -20.7258, -46.6097),
            new CidadeData("Patos de Minas", -18.5761, -46.3950),
            new CidadeData("Poços de Caldas", -21.7842, -46.5603),
            new CidadeData("Araxá", -19.5889, -46.9242),
            new CidadeData("Varginha", -21.5517, -45.4309)
        ));

        // RIO DE JANEIRO
        CIDADES_POR_ESTADO.put("RJ", Arrays.asList(
            new CidadeData("Rio de Janeiro", -22.9068, -43.1729),
            new CidadeData("Niterói", -22.8833, -43.1017),
            new CidadeData("Duque de Caxias", -22.7847, -43.3119),
            new CidadeData("São Gonçalo", -22.8333, -43.0500),
            new CidadeData("Nova Iguaçu", -22.7639, -43.4564),
            new CidadeData("Campos dos Goytacazes", -21.3211, -41.3311),
            new CidadeData("Volta Redonda", -22.5063, -44.0821),
            new CidadeData("Petrópolis", -22.5063, -43.1789),
            new CidadeData("Cabo Frio", -22.8864, -42.0350),
            new CidadeData("Macaé", -22.3833, -41.5833)
        ));

        // BAHIA
        CIDADES_POR_ESTADO.put("BA", Arrays.asList(
            new CidadeData("Salvador", -12.9714, -38.5014),
            new CidadeData("Feira de Santana", -12.2653, -39.9542),
            new CidadeData("Vitória da Conquista", -14.8633, -41.8042),
            new CidadeData("Camaçari", -12.7353, -38.3244),
            new CidadeData("Lauro de Freitas", -12.9167, -38.3356),
            new CidadeData("Ilhéus", -14.7839, -39.0267),
            new CidadeData("Itabuna", -14.7836, -39.2761),
            new CidadeData("Jequié", -13.8617, -40.0806),
            new CidadeData("Porto Seguro", -16.4408, -39.0671),
            new CidadeData("Teixeira de Freitas", -17.2533, -39.7411)
        ));

        // PARANÁ
        CIDADES_POR_ESTADO.put("PR", Arrays.asList(
            new CidadeData("Curitiba", -25.4284, -49.2733),
            new CidadeData("Londrina", -23.3103, -51.1628),
            new CidadeData("Maringá", -23.4250, -51.4693),
            new CidadeData("Ponta Grossa", -25.0953, -50.1629),
            new CidadeData("Cascavel", -24.9542, -53.4628),
            new CidadeData("Campo Mourão", -24.0483, -51.3764),
            new CidadeData("Foz do Iguaçu", -25.5951, -54.5757),
            new CidadeData("Guarapuava", -25.3903, -51.4475),
            new CidadeData("Apucarana", -23.5703, -51.4697),
            new CidadeData("Paranaguá", -25.5144, -48.7119)
        ));

        // RIO GRANDE DO SUL
        CIDADES_POR_ESTADO.put("RS", Arrays.asList(
            new CidadeData("Porto Alegre", -30.0277, -51.2287),
            new CidadeData("Caxias do Sul", -29.1808, -51.4939),
            new CidadeData("Pelotas", -31.7648, -52.3356),
            new CidadeData("Santa Maria", -29.6839, -53.8066),
            new CidadeData("Novo Hamburgo", -29.6690, -51.1282),
            new CidadeData("Gravataí", -29.9792, -50.9386),
            new CidadeData("Viamão", -30.0697, -51.0228),
            new CidadeData("Canoas", -29.9144, -51.1803),
            new CidadeData("Rio Grande", -32.0342, -52.0986),
            new CidadeData("Santo Ângelo", -28.2959, -55.9417)
        ));

        // Mais estados podem ser adicionados aqui...
        // PERNAMBUCO
        CIDADES_POR_ESTADO.put("PE", Arrays.asList(
            new CidadeData("Recife", -8.0476, -34.8770),
            new CidadeData("Jaboatão dos Guararapes", -8.2128, -34.9350),
            new CidadeData("Olinda", -8.0089, -34.8567),
            new CidadeData("Caruaru", -8.2828, -35.9764),
            new CidadeData("Petrolina", -9.3944, -40.5031)
        ));

        // CEARÁ
        CIDADES_POR_ESTADO.put("CE", Arrays.asList(
            new CidadeData("Fortaleza", -3.7319, -38.5267),
            new CidadeData("Caucaia", -3.7397, -38.6547),
            new CidadeData("Juazeiro do Norte", -7.2156, -39.3181),
            new CidadeData("Maracanaú", -3.6408, -38.6244),
            new CidadeData("Sobral", -3.6878, -40.3428)
        ));

        // PARÁ
        CIDADES_POR_ESTADO.put("PA", Arrays.asList(
            new CidadeData("Belém", -1.4558, -48.4892),
            new CidadeData("Ananindeua", -1.3667, -48.3733),
            new CidadeData("Santarém", -2.4267, -54.7050),
            new CidadeData("Marabá", -5.3697, -49.3450),
            new CidadeData("Parauapebas", -6.0664, -49.9083)
        ));

        // SANTA CATARINA
        CIDADES_POR_ESTADO.put("SC", Arrays.asList(
            new CidadeData("Joinville", -26.3045, -48.8487),
            new CidadeData("Florianópolis", -27.5965, -48.5494),
            new CidadeData("Blumenau", -26.8789, -49.0508),
            new CidadeData("Chapecó", -27.1019, -52.6167),
            new CidadeData("Itajaí", -26.9136, -48.6603)
        ));

        // GOIÁS
        CIDADES_POR_ESTADO.put("GO", Arrays.asList(
            new CidadeData("Goiânia", -16.6869, -49.2645),
            new CidadeData("Aparecida de Goiânia", -16.8256, -49.0239),
            new CidadeData("Anápolis", -16.3344, -48.9511),
            new CidadeData("Rio Verde", -17.7850, -50.9203),
            new CidadeData("Luziânia", -15.8267, -48.0633)
        ));

        // MARANHÃO
        CIDADES_POR_ESTADO.put("MA", Arrays.asList(
            new CidadeData("São Luís", -2.5344, -44.3068),
            new CidadeData("Imperatriz", -5.5264, -47.4667),
            new CidadeData("Caxias", -4.8639, -43.3522),
            new CidadeData("Timon", -5.1833, -42.8392),
            new CidadeData("Bacabal", -4.2178, -44.7681)
        ));
    }

    @Data
    @AllArgsConstructor
    public static class CidadeData {
        private String nome;
        private Double latitude;
        private Double longitude;
    }
}
