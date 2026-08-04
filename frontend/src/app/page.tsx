'use client';

import { useState, useEffect } from 'react';

export default function Home() {
  const [status, setStatus] = useState<string>('Carregando...');

  useEffect(() => {
    const checkHealth = async () => {
      try {
        const res = await fetch('/api/health', {
          headers: {
            'Content-Type': 'application/json',
          },
        });
        const data = await res.json();
        setStatus(data.message || 'API conectada');
      } catch (error) {
        setStatus('Erro ao conectar com API');
      }
    };

    checkHealth();
  }, []);

  return (
    <main className="flex min-h-screen flex-col items-center justify-center bg-gradient-to-br from-blue-600 to-blue-800 p-6">
      <div className="max-w-md w-full space-y-8 text-center text-white">
        <h1 className="text-5xl font-bold">🚗 Combustify</h1>
        <p className="text-xl opacity-90">
          Preços de Combustível em Tempo Real
        </p>

        <div className="bg-white/10 backdrop-blur-lg rounded-lg p-6 space-y-4">
          <div className="text-sm opacity-75">Status da API:</div>
          <div className="text-lg font-semibold">{status}</div>
        </div>

        <button className="w-full bg-white text-blue-600 font-bold py-3 rounded-lg hover:bg-gray-100 transition">
          Começar Agora
        </button>

        <p className="text-xs opacity-50">
          MVP v0.1.0 — Cuiabá, MT
        </p>
      </div>
    </main>
  );
}
