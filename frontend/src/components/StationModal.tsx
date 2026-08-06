'use client';

import { useState } from 'react';
import { GasStation } from '@/hooks/useGasStations';
import { usePrices, FuelType } from '@/hooks/usePrices';

interface StationModalProps {
  station: GasStation | null;
  isOpen: boolean;
  onClose: () => void;
}

const fuelConfig: Record<FuelType, { label: string; icon: string }> = {
  GASOLINA: { label: 'Gasolina', icon: '⛽' },
  DIESEL: { label: 'Diesel', icon: '🛢️' },
  ETANOL: { label: 'Etanol', icon: '🌾' },
  GNV: { label: 'GNV', icon: '💨' },
};

export function StationModal({ station, isOpen, onClose }: StationModalProps) {
  const { prices, reportPrice } = usePrices();
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
      <div className="fixed inset-0 bg-black/60 backdrop-blur-sm z-40" onClick={onClose} />

      <div className="fixed inset-x-0 bottom-0 bg-gradient-to-br from-slate-950 via-blue-900 to-cyan-900 rounded-t-3xl shadow-2xl z-50 max-h-[90vh] overflow-y-auto">
        <div className="p-8">
          {/* Header */}
          <div className="flex justify-between items-start mb-8">
            <div>
              <h2 className="text-3xl font-bold text-white">{station.name}</h2>
              <p className="text-cyan-300 text-sm mt-2">📍 {station.address}</p>
            </div>
            <button
              onClick={onClose}
              className="text-white/60 hover:text-white text-2xl transition"
            >
              ✕
            </button>
          </div>

          {/* Contact Info */}
          <div className="bg-white/10 backdrop-blur-md border border-white/20 rounded-2xl p-6 mb-8">
            <div className="flex gap-6">
              <div>
                <p className="text-white/60 text-sm">Telefone</p>
                <p className="text-white font-semibold">📞 (11) 3088-1234</p>
              </div>
              <div>
                <p className="text-white/60 text-sm">Horário</p>
                <p className="text-white font-semibold">⏰ Aberto até 23:00</p>
              </div>
            </div>
          </div>

          {/* Prices */}
          <div className="mb-8">
            <h3 className="text-xl font-bold text-white mb-6">⛽ Preços Atuais</h3>
            <div className="grid grid-cols-2 gap-4">
              {(Object.keys(fuelConfig) as FuelType[]).map((fuelType) => (
                <div key={fuelType} className="bg-white/10 backdrop-blur-md border border-white/20 rounded-xl p-5 hover:bg-white/20 transition">
                  <div className="text-3xl mb-2">{fuelConfig[fuelType].icon}</div>
                  <p className="text-white/70 text-xs uppercase tracking-wider">{fuelConfig[fuelType].label}</p>
                  {prices[fuelType]?.price ? (
                    <p className="text-2xl font-bold text-cyan-300 mt-3">
                      R$ {prices[fuelType]!.price.toFixed(2)}
                    </p>
                  ) : (
                    <p className="text-white/50 text-sm mt-3">Sem dados</p>
                  )}
                </div>
              ))}
            </div>
          </div>

          {/* Report Price Form */}
          <div className="border-t border-white/20 pt-8">
            <h3 className="text-xl font-bold text-white mb-6">➕ Reportar Novo Preço</h3>

            <div className="space-y-5">
              <div>
                <label className="block text-sm font-semibold text-white/90 mb-3">
                  Tipo de Combustível
                </label>
                <select
                  value={selectedFuel}
                  onChange={(e) => setSelectedFuel(e.target.value as FuelType)}
                  className="w-full bg-white/10 border border-white/20 text-white rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-cyan-400 transition backdrop-blur-md"
                >
                  {(Object.keys(fuelConfig) as FuelType[]).map((fuel) => (
                    <option key={fuel} value={fuel} className="bg-slate-900">
                      {fuelConfig[fuel].icon} {fuelConfig[fuel].label}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-semibold text-white/90 mb-3">
                  Preço (R$)
                </label>
                <input
                  type="number"
                  step="0.01"
                  value={priceInput}
                  onChange={(e) => setPriceInput(e.target.value)}
                  placeholder="0.00"
                  className="w-full bg-white/10 border border-white/20 text-white placeholder-white/40 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-cyan-400 transition backdrop-blur-md"
                />
              </div>

              <button
                onClick={handleReportPrice}
                disabled={isReporting || !priceInput}
                className="w-full bg-gradient-to-r from-blue-500 to-cyan-500 text-white font-bold py-3 rounded-xl hover:shadow-lg hover:shadow-cyan-500/50 disabled:opacity-50 transition-all duration-200"
              >
                {isReporting ? '⏳ Enviando...' : '🚀 Enviar Preço'}
              </button>
            </div>
          </div>

          {/* Plan Info */}
          <div className="mt-8 bg-white/10 backdrop-blur-md border border-white/20 rounded-xl p-5">
            <p className="text-white/80 text-sm">
              💡 Plano Free: 5 consultas/dia | <span className="text-cyan-300 font-semibold">Upgrade para ilimitado →</span>
            </p>
          </div>
        </div>
      </div>
    </>
  );
}
