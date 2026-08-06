import { useCallback } from 'react';
import { useAuthStore } from '@/store/authStore';
import { apiCall } from '@/lib/api';

export function useAuth() {
  const token = useAuthStore((state) => state.token);
  const setToken = useAuthStore((state) => state.setToken);
  const clearToken = useAuthStore((state) => state.clearToken);

  const login = useCallback(async (email: string, password: string) => {
    try {
      const data = await apiCall('/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email, password }),
      });

      setToken(data.accessToken);
      return data.accessToken;
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  }, [setToken]);

  const signup = useCallback(async (email: string, password: string, displayName: string) => {
    try {
      await apiCall('/auth/signup', {
        method: 'POST',
        body: JSON.stringify({ email, password, displayName }),
      });

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
