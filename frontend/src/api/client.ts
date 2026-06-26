import axios from 'axios';

const TOKEN_KEY = 'empresaapp_token';

/**
 * En local: /api (proxy Vite → localhost:8080)
 * En producción: VITE_API_URL=https://tu-backend.onrender.com/api
 */
const API_BASE = import.meta.env.VITE_API_URL || '/api';

/** Cliente HTTP central: adjunta JWT automáticamente a cada petición */
export const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (res) => res,
  (error) => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem('empresaapp_user');
      if (!window.location.pathname.includes('/login')) {
        window.location.href = '/login';
      }
    }
    const mensaje = error.response?.data?.mensaje || error.message;
    return Promise.reject(new Error(mensaje));
  }
);

export function saveToken(token: string) {
  localStorage.setItem(TOKEN_KEY, token);
}

export function clearAuth() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem('empresaapp_user');
}

export function getStoredUser() {
  if (!localStorage.getItem(TOKEN_KEY)) return null;
  const raw = localStorage.getItem('empresaapp_user');
  return raw ? JSON.parse(raw) : null;
}

export function saveUser(user: object) {
  localStorage.setItem('empresaapp_user', JSON.stringify(user));
}
