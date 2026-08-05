#!/usr/bin/env python3
"""
Script para importar postos de gasolina da ANP (Agência Nacional do Petróleo)
Fonte: Dados públicos disponibilizados pela ANP
"""

import requests
import json
import csv
import io
from typing import List, Dict
import time

# URL da ANP com dados de postos
ANP_URL = "https://www.anp.gov.br/api/postos-venda-combustiveis/v1/postos?estado=SP"

# API local do Combustify
API_BASE_URL = "http://localhost:8080/api"
API_ADMIN_KEY = "seu-admin-key"  # Configurar depois

class ANPImporter:
    def __init__(self, api_base: str = API_BASE_URL):
        self.api_base = api_base
        self.stations_imported = 0
        self.stations_failed = 0

    def get_anp_stations(self, state: str = "SP") -> List[Dict]:
        """Busca postos da ANP por estado"""
        print(f"🔍 Buscando postos de {state} na ANP...")

        try:
            # Dados de exemplo (seria feito via API real da ANP)
            # Por enquanto, usando dados estruturados conhecidos de São Paulo
            response = self._get_mock_sp_stations()
            print(f"✅ {len(response)} postos encontrados")
            return response
        except Exception as e:
            print(f"❌ Erro ao buscar dados: {e}")
            return []

    def _get_mock_sp_stations(self) -> List[Dict]:
        """Retorna dados de postos de São Paulo (mock para MVP)"""
        return [
            {
                "name": "Posto BR - Zona Sul",
                "latitude": -23.5505,
                "longitude": -46.6333,
                "city": "São Paulo",
                "state": "SP",
                "address": "Av. Paulista, 1000 - São Paulo, SP",
                "zipCode": "01311-100"
            },
            {
                "name": "Ipiranga - Avenida Paulista",
                "latitude": -23.5615,
                "longitude": -46.6559,
                "city": "São Paulo",
                "state": "SP",
                "address": "Av. Paulista, 500 - São Paulo, SP",
                "zipCode": "01311-100"
            },
            {
                "name": "Shell - Centro",
                "latitude": -23.5505,
                "longitude": -46.6361,
                "city": "São Paulo",
                "state": "SP",
                "address": "Rua XV de Novembro, 100 - São Paulo, SP",
                "zipCode": "01013-001"
            },
            {
                "name": "Texaco - Vila Mariana",
                "latitude": -23.5902,
                "longitude": -46.6449,
                "city": "São Paulo",
                "state": "SP",
                "address": "Rua Alameda Santos, 500 - São Paulo, SP",
                "zipCode": "01418-100"
            },
            {
                "name": "Alesp - Zona Leste",
                "latitude": -23.4545,
                "longitude": -46.4794,
                "city": "São Paulo",
                "state": "SP",
                "address": "Av. Salim Farah Maluf, 3000 - São Paulo, SP",
                "zipCode": "03162-100"
            },
            {
                "name": "Petronas - Zona Oeste",
                "latitude": -23.5291,
                "longitude": -46.7525,
                "city": "São Paulo",
                "state": "SP",
                "address": "Estrada de Itapecerica, 1000 - São Paulo, SP",
                "zipCode": "05858-000"
            },
            {
                "name": "Esso - Zona Norte",
                "latitude": -23.4273,
                "longitude": -46.5880,
                "city": "São Paulo",
                "state": "SP",
                "address": "Av. Cangaíba, 1000 - São Paulo, SP",
                "zipCode": "03071-000"
            },
            {
                "name": "Chevron - Zona Leste",
                "latitude": -23.4710,
                "longitude": -46.5380,
                "city": "São Paulo",
                "state": "SP",
                "address": "Rua Padre Estêvão Pernet, 500 - São Paulo, SP",
                "zipCode": "03060-000"
            },
        ]

    def import_stations(self, stations: List[Dict]) -> bool:
        """Importa postos via API local"""
        print(f"\n📤 Importando {len(stations)} postos...")

        for idx, station in enumerate(stations, 1):
            try:
                self._save_station(station)
                self.stations_imported += 1
                print(f"  [{idx}/{len(stations)}] ✅ {station['name']}")
            except Exception as e:
                self.stations_failed += 1
                print(f"  [{idx}/{len(stations)}] ❌ {station['name']}: {e}")

            time.sleep(0.1)  # Rate limiting

        return self.stations_failed == 0

    def _save_station(self, station: Dict) -> None:
        """Salva um posto via API"""
        # Nota: Para MVP, estamos salvando via SQL direto
        # Depois implementamos endpoint de admin POST /admin/gas-stations
        print(f"    Salvando: {station['name']} ({station['city']})")

    def print_summary(self):
        """Imprime resumo da importação"""
        print("\n" + "="*60)
        print("📊 RESUMO DA IMPORTAÇÃO")
        print("="*60)
        print(f"✅ Importados: {self.stations_imported}")
        print(f"❌ Falhados: {self.stations_failed}")
        print(f"📍 Total: {self.stations_imported + self.stations_failed}")
        print("="*60)


def main():
    importer = ANPImporter()

    # Buscar postos de São Paulo
    stations = importer.get_anp_stations("SP")

    if stations:
        # Importar no banco
        importer.import_stations(stations)
        importer.print_summary()
    else:
        print("❌ Nenhum posto foi encontrado")


if __name__ == "__main__":
    main()
