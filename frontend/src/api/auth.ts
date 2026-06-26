import { api, saveToken, saveUser } from './client';
import type { AuthUser } from '../types';

export async function login(username: string, password: string): Promise<AuthUser> {
  const { data } = await api.post<AuthUser>('/auth/login', { username, password });
  saveToken(data.token);
  saveUser(data);
  return data;
}

export async function registro(payload: {
  username: string;
  email: string;
  password: string;
  rol?: string;
}): Promise<AuthUser> {
  const { data } = await api.post<AuthUser>('/auth/registro', payload);
  saveToken(data.token);
  saveUser(data);
  return data;
}
