import { useCallback } from 'react';
import { useAuthStore } from '@/store/authStore';

export function useAuth() {
  const token = useAuthStore((state) => state.token);
  const setToken = useAuthStore((state) => state.setToken);
  const clearToken = useAuthStore((state) => state.clearToken);

  const login = useCallback(async (email: string, password: string) => {
    try {
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) throw new Error('Login failed');

      const data = await response.json();
      setToken(data.token);
      return data.token;
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  }, [setToken]);

  const signup = useCallback(async (email: string, password: string, displayName: string) => {
    try {
      const response = await fetch('/api/auth/signup', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password, displayName }),
      });

      if (!response.ok) throw new Error('Signup failed');

      await login(email, password);
    } catch (error) {
      console.error('Signup error:', error);
      throw error;
    }
  }, [login]);

  const logout = useCallback(() => {
    clearToken();
  }, [clearToken]);

  return { token, login, signup, logout, isAuthenticated: !!token };
}
