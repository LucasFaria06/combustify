'use client';

import { useState } from 'react';
import { GasStation } from '@/hooks/useGasStations';
import { usePrices, FuelType } from '@/hooks/usePrices';

interface StationModalProps {
  station: GasStation | null;
  isOpen: boolean;
  onClose: () => void;
}

const fuelConfig: Record<FuelType, { label: string; icon: string; color: string }> = {
  GASOLINA: { label: 'Gasolina', icon: '⛽', color: 'text-blue-600' },
  DIESEL: { label: 'Diesel', icon: '🛢️', color: 'text-purple-600' },
  ETANOL: { label: 'Etanol', icon: '🌾', color: 'text-red-600' },
  GNV: { label: 'GNV', icon: '💨', color: 'text-indigo-600' },
};

export function StationModal({ station, isOpen, onClose }: StationModalProps) {
  const { prices, loading: pricesLoading, reportPrice } = usePrices();
  const [selectedFuel, setSelectedFuel] = useState<FuelType>('GASOLINA');
  const [priceInput, setPriceInput] = useState('');
  const [isReporting, setIsReporting] = useState(false);

  if (!isOpen || !station) return null;

  const handleReportPrice = async () => {
    if (!priceInput) return;

    setIsReporting(true);
    try {
      await reportPrice(station.id, selectedFuel, parseFloat(priceInput));
      setPriceInput('');
      alert('Preço reportado com sucesso!');
    } catch (error) {
      alert('Erro ao reportar preço');
    } finally {
      setIsReporting(false);
    }
  };

  return (
    <>
      {/* Overlay */}
      <div className="fixed inset-0 bg-black bg-opacity-50 z-40" onClick={onClose} />

      {/* Modal */}
      <div className="fixed bottom-0 left-0 right-0 bg-white rounded-t-2xl shadow-2xl z-50 max-h-[90vh] overflow-y-auto">
        <div className="p-6">
          {/* Header */}
          <div className="flex justify-between items-start mb-6">
            <div>
              <h2 className="text-2xl font-bold text-gray-900">{station.name}</h2>
              <p className="text-sm text-gray-600 mt-1">📍 {station.address}</p>
            </div>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600 text-2xl"
            >
              ✕
            </button>
          </div>

          {/* Map button */}
          <button className="w-full bg-blue-50 text-blue-600 py-2 rounded-lg mb-6 hover:bg-blue-100 transition">
            🗺️ Ver no Mapa
          </button>

          {/* Contact info */}
          <div className="space-y-2 mb-6 text-sm text-gray-700">
            <p>📞 (11) 3088-1234</p>
            <p>⏰ Aberto até 23:00</p>
          </div>

          {/* Prices */}
          <div className="mb-8">
            <h3 className="font-bold text-gray-900 mb-4">Preços Atuais</h3>
            <div className="grid grid-cols-2 gap-4">
              {(Object.keys(fuelConfig) as FuelType[]).map((fuelType) => (
                <div key={fuelType} className="bg-gray-50 p-4 rounded-lg">
                  <div className={`text-2xl mb-1 ${fuelConfig[fuelType].color}`}>
                    {fuelConfig[fuelType].icon}
                  </div>
                  <div className="text-xs text-gray-600">{fuelConfig[fuelType].label}</div>
                  {prices[fuelType]?.price ? (
                    <div className="text-lg font-bold text-gray-900 mt-2">
                      R$ {prices[fuelType]!.price.toFixed(2)}
                    </div>
                  ) : (
                    <div className="text-sm text-gray-500 mt-2">Sem dados</div>
                  )}
                </div>
              ))}
            </div>
          </div>

          {/* Report Price Form */}
          <div className="border-t pt-6">
            <h3 className="font-bold text-gray-900 mb-4">➕ Reportar Novo Preço</h3>

            <div className="space-y-4">
              {/* Fuel Type Selection */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Tipo de Combustível
                </label>
                <select
                  value={selectedFuel}
                  onChange={(e) => setSelectedFuel(e.target.value as FuelType)}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  {(Object.keys(fuelConfig) as FuelType[]).map((fuel) => (
                    <option key={fuel} value={fuel}>
                      {fuelConfig[fuel].icon} {fuelConfig[fuel].label}
                    </option>
                  ))}
                </select>
              </div>

              {/* Price Input */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Preço (R$)
                </label>
                <input
                  type="number"
                  step="0.01"
                  value={priceInput}
                  onChange={(e) => setPriceInput(e.target.value)}
                  placeholder="0.00"
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>

              {/* Submit Button */}
              <button
                onClick={handleReportPrice}
                disabled={isReporting || !priceInput}
                className="w-full bg-blue-600 text-white font-medium py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50 transition"
              >
                {isReporting ? 'Enviando...' : 'Enviar Preço'}
              </button>
            </div>
          </div>

          {/* Free tier info */}
          <div className="mt-6 p-4 bg-yellow-50 rounded-lg text-sm text-yellow-800 border border-yellow-200">
            ℹ️ Plano Free: 5 consultas/dia | <strong>Upgrade para ilimitado →</strong>
          </div>
        </div>
      </div>
    </>
  );
}
