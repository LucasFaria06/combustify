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
    <div className="min-h-screen bg-gradient-to-br from-slate-950 via-blue-900 to-cyan-900 flex items-center justify-center p-4 relative overflow-hidden">
      {/* Decorative elements */}
      <div className="absolute top-0 right-0 w-96 h-96 bg-blue-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse"></div>
      <div className="absolute bottom-0 left-0 w-96 h-96 bg-cyan-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse"></div>

      <div className="relative z-10">
        {/* Logo Card */}
        <div className="text-center mb-12">
          <div className="inline-block bg-gradient-to-br from-blue-400 to-cyan-400 p-4 rounded-2xl mb-4 shadow-2xl shadow-cyan-500/50">
            <svg className="w-20 h-20" viewBox="0 0 100 100" fill="none" xmlns="http://www.w3.org/2000/svg">
              {/* Pump body */}
              <rect x="30" y="45" width="40" height="35" rx="4" fill="#1F2937" />
              <rect x="28" y="43" width="44" height="37" rx="6" fill="#374151" opacity="0.5" />

              {/* Pump nozzle */}
              <rect x="35" y="15" width="30" height="35" rx="3" fill="#DC2626" />
              <rect x="33" y="13" width="34" height="38" rx="5" fill="#991B1B" opacity="0.4" />

              {/* Nozzle tip */}
              <circle cx="50" cy="12" r="5" fill="#FCA5A5" />

              {/* Display screen */}
              <rect x="38" y="52" width="24" height="12" rx="2" fill="#1E293B" />
              <rect x="40" y="54" width="20" height="8" rx="1" fill="#06B6D4" opacity="0.8" />

              {/* Button */}
              <rect x="42" y="68" width="16" height="8" rx="1" fill="#0EA5E9" />

              {/* Highlight/3D effect */}
              <ellipse cx="45" cy="50" rx="8" ry="12" fill="white" opacity="0.15" />
            </svg>
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
