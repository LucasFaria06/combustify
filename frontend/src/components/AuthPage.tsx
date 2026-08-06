'use client';

import { useState } from 'react';
import { useAuth } from '@/hooks/useAuth';

interface AuthPageProps {
  onAuthSuccess: () => void;
}

export function AuthPage({ onAuthSuccess }: AuthPageProps) {
  const { login, signup } = useAuth();
  const [isSignup, setIsSignup] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [displayName, setDisplayName] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      if (isSignup) {
        await signup(email, password, displayName);
      } else {
        await login(email, password);
      }
      onAuthSuccess();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro desconhecido');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-red-900 via-red-800 to-orange-700 flex items-center justify-center p-4 relative overflow-hidden">
      {/* Decorative elements */}
      <div className="absolute top-0 right-0 w-96 h-96 bg-blue-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse"></div>
      <div className="absolute bottom-0 left-0 w-96 h-96 bg-cyan-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse"></div>

      <div className="relative z-10">
        {/* Logo Card */}
        <div className="text-center mb-12">
          <div className="inline-block bg-gradient-to-br from-blue-400 to-cyan-400 p-4 rounded-2xl mb-4">
            <span className="text-5xl">🚗</span>
          </div>
          <h1 className="text-5xl font-black text-white mb-2">Combustify</h1>
          <p className="text-cyan-300 text-lg">Encontre os melhores preços de combustível</p>
        </div>

        {/* Auth Card */}
        <div className="bg-white/10 backdrop-blur-xl border border-white/20 rounded-3xl shadow-2xl w-full max-w-md p-8">
          {/* Form */}
          <form onSubmit={handleSubmit} className="space-y-5">
            {/* Display Name (Signup only) */}
            {isSignup && (
              <div className="animate-in fade-in">
                <label className="block text-sm font-semibold text-white mb-2">
                  Nome Completo
                </label>
                <input
                  type="text"
                  value={displayName}
                  onChange={(e) => setDisplayName(e.target.value)}
                  placeholder="Seu nome"
                  required
                  className="w-full bg-white/10 border border-white/20 text-white placeholder-white/50 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-cyan-400 transition"
                />
              </div>
            )}

            {/* Email */}
            <div>
              <label className="block text-sm font-semibold text-white mb-2">
                Email
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="seu@email.com"
                required
                className="w-full bg-white/10 border border-white/20 text-white placeholder-white/50 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-cyan-400 transition"
              />
            </div>

            {/* Password */}
            <div>
              <label className="block text-sm font-semibold text-white mb-2">
                Senha
              </label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                required
                className="w-full bg-white/10 border border-white/20 text-white placeholder-white/50 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-cyan-400 transition"
              />
            </div>

            {/* Error */}
            {error && (
              <div className="bg-red-500/20 border border-red-400/50 text-red-200 px-4 py-3 rounded-xl text-sm backdrop-blur-sm">
                {error}
              </div>
            )}

            {/* Submit Button */}
            <button
              type="submit"
              disabled={loading}
              className="w-full bg-gradient-to-r from-blue-500 to-cyan-500 text-white font-bold py-3 rounded-xl hover:shadow-lg hover:shadow-cyan-500/50 disabled:opacity-50 transition-all duration-200 transform hover:scale-105"
            >
              {loading ? '⏳ Carregando...' : isSignup ? '✨ Criar Conta' : '🚀 Entrar'}
            </button>
          </form>

          {/* Toggle */}
          <div className="text-center mt-8 border-t border-white/10 pt-6">
            <p className="text-white/80 text-sm">
              {isSignup ? 'Já tem conta?' : 'Não tem conta?'}{' '}
              <button
                onClick={() => {
                  setIsSignup(!isSignup);
                  setError('');
                }}
                className="text-cyan-300 font-semibold hover:text-cyan-200 transition"
              >
                {isSignup ? 'Entrar aqui' : 'Criar conta'}
              </button>
            </p>
          </div>
        </div>

        {/* Footer Info */}
        <div className="text-center mt-8 text-white/60 text-sm">
          <p>🔒 Sua segurança é nossa prioridade</p>
        </div>
      </div>
    </div>
  );
}
