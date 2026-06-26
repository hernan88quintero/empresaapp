import { api } from './client';
import type { Rol } from '../types';

export interface Usuario {
  id: number;
  username: string;
  email: string;
  rol: Rol;
  activo: boolean;
  fechaCreacion: string;
}

export interface CrearUsuarioPayload {
  username: string;
  email: string;
  password: string;
  rol?: Rol;
}

export interface ActualizarUsuarioPayload {
  email?: string;
  rol?: Rol;
  activo?: boolean;
  password?: string;
}

export const usuariosApi = {
  listar: () => api.get<Usuario[]>('/usuarios').then((r) => r.data),
  buscar: (id: number) => api.get<Usuario>(`/usuarios/${id}`).then((r) => r.data),
  crear: (payload: CrearUsuarioPayload) => api.post<Usuario>('/usuarios', payload).then((r) => r.data),
  actualizar: (id: number, payload: ActualizarUsuarioPayload) =>
    api.put<Usuario>(`/usuarios/${id}`, payload).then((r) => r.data),
  eliminar: (id: number) => api.delete(`/usuarios/${id}`),
};
