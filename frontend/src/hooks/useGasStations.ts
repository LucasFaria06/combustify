import { useState, useCallback } from 'react';
import { useAuth } from './useAuth';

export interface GasStation {
  id: string;
  name: string;
  latitude: number;
  longitude: number;
  city: string;
  state: string;
  address: string;
  zipCode: string;
}

export function useGasStations() {
  const { token } = useAuth();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [stations, setStations] = useState<GasStation[]>([]);

  const fetchNearby = useCallback(
    async (latitude: number, longitude: number, radiusKm: number = 5) => {
      if (!token) {
        setError('Faça login primeiro');
        return;
      }

      setLoading(true);
      setError(null);

      try {
        const params = new URLSearchParams({
          latitude: latitude.toString(),
          longitude: longitude.toString(),
          radiusKm: radiusKm.toString(),
        });

        const response = await fetch(`/api/gas-stations?${params}`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (!response.ok) throw new Error('Erro ao buscar postos');

        const data = await response.json();
        setStations(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Erro desconhecido');
      } finally {
        setLoading(false);
      }
    },
    [token]
  );

  const fetchByCity = useCallback(
    async (city: string) => {
      if (!token) {
        setError('Faça login primeiro');
        return;
      }

      setLoading(true);
      setError(null);

      try {
        const response = await fetch(`/api/gas-stations?city=${city}`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (!response.ok) throw new Error('Erro ao buscar postos');

        const data = await response.json();
        setStations(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Erro desconhecido');
      } finally {
        setLoading(false);
      }
    },
    [token]
  );

  return { stations, loading, error, fetchNearby, fetchByCity };
}
