import { createContext, useContext, useState, useCallback, type ReactNode } from 'react';
import { login as loginApi } from '../api/auth';
import { clearAuth, getStoredUser } from '../api/client';
import type { AuthUser } from '../types';

interface AuthContextType {
  user: AuthUser | null;
  login: (username: string, password: string) => Promise<void>;
  setUserFromRegister: (user: AuthUser) => void;
  logout: () => void;
  isAuthenticated: boolean;
  isAdmin: boolean;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(() => getStoredUser());

  const login = useCallback(async (username: string, password: string) => {
    const data = await loginApi(username, password);
    setUser(data);
  }, []);

  const setUserFromRegister = useCallback((authUser: AuthUser) => {
    setUser(authUser);
  }, []);

  const logout = useCallback(() => {
    clearAuth();
    setUser(null);
  }, []);

  return (
    <AuthContext.Provider
      value={{
        user,
        login,
        setUserFromRegister,
        logout,
        isAuthenticated: !!user,
        isAdmin: user?.rol === 'ADMIN',
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth debe usarse dentro de AuthProvider');
  return ctx;
}
