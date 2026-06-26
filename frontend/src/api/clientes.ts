import { api } from './client';
import type { Cliente } from '../types';

export const clientesApi = {
  listar: () => api.get<Cliente[]>('/clientes').then((r) => r.data),
  crear: (c: Cliente) => api.post<Cliente>('/clientes', c).then((r) => r.data),
  actualizar: (id: number, c: Cliente) => api.put<Cliente>(`/clientes/${id}`, c).then((r) => r.data),
  eliminar: (id: number) => api.delete(`/clientes/${id}`),
};
