'use client';

import { useEffect, useState } from 'react';
import { useAuth } from '@/hooks/useAuth';
import { useGasStations, GasStation } from '@/hooks/useGasStations';
import { usePrices } from '@/hooks/usePrices';
import { StationModal } from '@/components/StationModal';

export function Dashboard() {
  const { isAuthenticated, logout } = useAuth();
  const { stations, loading, fetchNearby, fetchByCity } = useGasStations();
  const { fetchPrices } = usePrices();

  const [selectedStation, setSelectedStation] = useState<GasStation | null>(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [searchCity, setSearchCity] = useState('São Paulo');

  useEffect(() => {
    if (!isAuthenticated) return;
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const { latitude, longitude } = position.coords;
          fetchNearby(latitude, longitude);
        },
        () => {
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

  if (!isAuthenticated) {
    return <div>Redirecionando...</div>;
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-950 via-blue-900 to-cyan-900">
      {/* Header */}
      <header className="bg-white/10 backdrop-blur-md border-b border-white/20 z-10 sticky top-0">
        <div className="max-w-7xl mx-auto px-6 py-4 flex justify-between items-center">
          <div className="flex items-center gap-3">
            <span className="text-3xl">🚗</span>
            <h1 className="text-2xl font-bold bg-gradient-to-r from-blue-400 to-cyan-400 bg-clip-text text-transparent">
              Combustify
            </h1>
          </div>

          <form onSubmit={handleSearch} className="flex-1 max-w-md mx-8">
            <div className="flex gap-2">
              <input
                type="text"
                value={searchCity}
                onChange={(e) => setSearchCity(e.target.value)}
                placeholder="Buscar cidade..."
                className="flex-1 bg-white/10 border border-white/20 text-white placeholder-white/50 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-cyan-400 transition"
              />
              <button
                type="submit"
                className="bg-gradient-to-r from-blue-500 to-cyan-500 text-white px-6 py-2 rounded-lg font-medium hover:shadow-lg hover:shadow-cyan-500/50 transition"
              >
                🔍
              </button>
            </div>
          </form>

          <button
            onClick={logout}
            className="text-white/70 hover:text-white font-medium transition"
          >
            Sair
          </button>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-6 py-8">
        {/* Stats Card */}
        <div className="mb-8 bg-white/10 backdrop-blur-md border border-white/20 rounded-2xl p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-white/60 text-sm">Postos Encontrados</p>
              <p className="text-4xl font-bold text-white">{stations.length}</p>
            </div>
            <div className="text-5xl">⛽</div>
          </div>
        </div>

        {/* Control Buttons */}
        <div className="flex gap-3 mb-8">
          <button
            onClick={() => {
              if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition((position) => {
                  const { latitude, longitude } = position.coords;
                  fetchNearby(latitude, longitude);
                });
              }
            }}
            className="bg-white/10 hover:bg-white/20 border border-white/20 text-white px-6 py-3 rounded-lg font-medium transition backdrop-blur-md flex items-center gap-2"
          >
            📍 Meu Local
          </button>

          <button
            onClick={() => fetchByCity(searchCity)}
            disabled={loading}
            className="bg-gradient-to-r from-blue-500 to-cyan-500 text-white px-6 py-3 rounded-lg font-medium hover:shadow-lg hover:shadow-cyan-500/50 disabled:opacity-50 transition flex items-center gap-2"
          >
            🔄 {loading ? 'Carregando...' : 'Atualizar'}
          </button>
        </div>

        {/* Stations Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {stations.map((station) => (
            <div
              key={station.id}
              onClick={() => handleStationClick(station)}
              className="group bg-white/10 hover:bg-white/20 backdrop-blur-md border border-white/20 rounded-2xl p-6 cursor-pointer transition-all hover:scale-105 hover:shadow-xl hover:shadow-cyan-500/20"
            >
              <div className="flex items-start justify-between mb-4">
                <div>
                  <h3 className="text-lg font-bold text-white group-hover:text-cyan-300 transition">
                    {station.name}
                  </h3>
                  <p className="text-white/60 text-sm">
                    📍 {station.city}, {station.state}
                  </p>
                </div>
                <span className="text-3xl">⛽</span>
              </div>

              <p className="text-white/80 text-sm mb-4">
                {station.address || 'Endereço não disponível'}
              </p>

              <button className="w-full bg-gradient-to-r from-blue-500 to-cyan-500 text-white py-2 rounded-lg font-medium hover:shadow-lg transition-all">
                Ver Preços →
              </button>
            </div>
          ))}
        </div>

        {stations.length === 0 && !loading && (
          <div className="text-center py-12">
            <p className="text-white/60 text-lg">Nenhum posto encontrado</p>
          </div>
        )}
      </main>

      {/* Modal */}
      <StationModal
        station={selectedStation}
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
      />
    </div>
  );
}
