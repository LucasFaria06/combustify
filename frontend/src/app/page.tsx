'use client';

import { useState, useEffect } from 'react';
import { useAuthStore } from '@/store/authStore';
import { AuthPage } from '@/components/AuthPage';
import { Dashboard } from '@/components/Dashboard';

export default function Home() {
  const token = useAuthStore((state) => state.token);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    setIsAuthenticated(!!token);
    setIsLoading(false);
  }, [token]);

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gray-100">
        <div className="text-center">
          <h1 className="text-4xl font-bold text-blue-600 mb-4">🚗 Combustify</h1>
          <p className="text-gray-600">Carregando...</p>
        </div>
      </div>
    );
  }

  return (
    <>
      {isAuthenticated ? (
        <Dashboard />
      ) : (
        <AuthPage onAuthSuccess={() => setIsAuthenticated(true)} />
      )}
    </>
  );
}
