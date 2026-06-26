import { api } from './client';
import type { Proveedor } from '../types';

export const proveedoresApi = {
  listar: () => api.get<Proveedor[]>('/proveedores').then((r) => r.data),
  crear: (p: Proveedor) => api.post<Proveedor>('/proveedores', p).then((r) => r.data),
  actualizar: (id: number, p: Proveedor) => api.put<Proveedor>(`/proveedores/${id}`, p).then((r) => r.data),
  eliminar: (id: number) => api.delete(`/proveedores/${id}`),
};
