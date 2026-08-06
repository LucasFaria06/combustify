'use client';

import { useEffect, useState } from 'react';
import dynamic from 'next/dynamic';
import { useAuth } from '@/hooks/useAuth';
import { useGasStations, GasStation } from '@/hooks/useGasStations';
import { usePrices } from '@/hooks/usePrices';
import { StationModal } from '@/components/StationModal';

// TODO: Fix Leaflet SSR issue
const Map = () => <div className="w-full h-96 bg-blue-100 rounded-lg flex items-center justify-center text-gray-600">Mapa será implementado em breve</div>;

export function Dashboard() {
  const { isAuthenticated, logout } = useAuth();
  const { stations, loading, fetchNearby, fetchByCity } = useGasStations();
  const { fetchPrices } = usePrices();

  const [selectedStation, setSelectedStation] = useState<GasStation | null>(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [searchCity, setSearchCity] = useState('São Paulo');
  const [userLocation, setUserLocation] = useState<[number, number] | null>(null);

  useEffect(() => {
    if (!isAuthenticated) return;

    // Try to get user location
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const { latitude, longitude } = position.coords;
          setUserLocation([latitude, longitude]);
          fetchNearby(latitude, longitude);
        },
        () => {
          // Fallback to São Paulo
          fetchByCity(searchCity);
        }
      );
    } else {
      fetchByCity(searchCity);
    }
  }, [isAuthenticated]);

  const handleStationClick = (station: GasStation) => {
    setSelectedStation(station);
    fetchPrices(station.id);
    setModalOpen(true);
  };

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    await fetchByCity(searchCity);
  };

  const handleUseLocation = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition((position) => {
        const { latitude, longitude } = position.coords;
        setUserLocation([latitude, longitude]);
        fetchNearby(latitude, longitude);
      });
    }
  };

  if (!isAuthenticated) {
    return <div>Redirecionando para login...</div>;
  }

  return (
    <div className="flex flex-col h-screen">
      {/* Header */}
      <header className="bg-white border-b border-gray-200 shadow-sm z-10">
        <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
          <h1 className="text-2xl font-bold text-blue-600">🚗 Combustify</h1>

          <form onSubmit={handleSearch} className="flex-1 max-w-sm mx-8">
            <div className="flex gap-2">
              <input
                type="text"
                value={searchCity}
                onChange={(e) => setSearchCity(e.target.value)}
                placeholder="Buscar cidade..."
                className="flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <button
                type="submit"
                className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700 transition"
              >
                🔍
              </button>
            </div>
          </form>

          <button
            onClick={logout}
            className="text-gray-600 hover:text-gray-900 text-sm"
          >
            Sair
          </button>
        </div>
      </header>

      {/* Map + Controls */}
      <div className="flex-1 relative">
        <Map
          stations={stations}
          center={userLocation || [-23.5505, -46.6333]}
          onStationClick={handleStationClick}
        />

        {/* Control Buttons */}
        <div className="absolute bottom-6 right-6 flex flex-col gap-3">
          <button
            onClick={handleUseLocation}
            className="bg-white text-gray-800 px-4 py-2 rounded-lg shadow-lg hover:bg-gray-50 transition flex items-center gap-2"
          >
            📍 Meu Local
          </button>

          <button
            onClick={() => fetchByCity(searchCity)}
            disabled={loading}
            className="bg-white text-gray-800 px-4 py-2 rounded-lg shadow-lg hover:bg-gray-50 disabled:opacity-50 transition flex items-center gap-2"
          >
            🔄 {loading ? 'Carregando...' : 'Atualizar'}
          </button>
        </div>

        {/* Stats */}
        <div className="absolute top-6 left-6 bg-white rounded-lg shadow-lg px-4 py-3">
          <p className="text-sm text-gray-700">
            <strong>{stations.length}</strong> postos encontrados
          </p>
        </div>
      </div>

      {/* Modal */}
      <StationModal
        station={selectedStation}
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
      />
    </div>
  );
}
