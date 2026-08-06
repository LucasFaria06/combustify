import { useState, useCallback } from 'react';
import { useAuth } from './useAuth';

export type FuelType = 'GASOLINA' | 'DIESEL' | 'ETANOL' | 'GNV';

export interface Price {
  id?: string;
  stationId: string;
  fuelType: FuelType;
  price: number;
  verificationCount?: number;
  reportedAt?: string;
}

export function usePrices() {
  const { token } = useAuth();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [prices, setPrices] = useState<Record<FuelType, Price | null>>({
    GASOLINA: null,
    DIESEL: null,
    ETANOL: null,
    GNV: null,
  });

  const fetchPrices = useCallback(
    async (stationId: string) => {
      if (!token) {
        setError('Faça login primeiro');
        return;
      }

      setLoading(true);
      setError(null);

      try {
        const response = await fetch(`http://localhost:8081/api/prices/history/${stationId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (!response.ok) throw new Error('Erro ao buscar preços');

        const data: Array<{
          id: string;
          stationId: string;
          fuelType: FuelType;
          price: number;
          verificationCount: number;
          reportedAt: string;
        }> = await response.json();

        // Transform array to indexed object by fuel type
        const indexedPrices: Record<FuelType, Price | null> = {
          GASOLINA: null,
          DIESEL: null,
          ETANOL: null,
          GNV: null,
        };

        data.forEach((item) => {
          indexedPrices[item.fuelType] = {
            id: item.id,
            stationId: item.stationId,
            fuelType: item.fuelType,
            price: parseFloat(item.price.toString()),
            verificationCount: item.verificationCount,
            reportedAt: item.reportedAt,
          };
        });

        setPrices(indexedPrices);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Erro desconhecido');
      } finally {
        setLoading(false);
      }
    },
    [token]
  );

  const reportPrice = useCallback(
    async (stationId: string, fuelType: FuelType, price: number) => {
      if (!token) {
        setError('Faça login primeiro');
        return;
      }

      setLoading(true);
      setError(null);

      try {
        const response = await fetch('http://localhost:8081/api/prices', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            stationId,
            fuelType,
            price,
          }),
        });

        if (!response.ok) throw new Error('Erro ao reportar preço');

        await fetchPrices(stationId);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Erro desconhecido');
      } finally {
        setLoading(false);
      }
    },
    [token, fetchPrices]
  );

  return { prices, loading, error, fetchPrices, reportPrice };
}
