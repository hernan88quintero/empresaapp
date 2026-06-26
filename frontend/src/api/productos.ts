import { api } from './client';
import type { Producto } from '../types';

export const productosApi = {
  listar: () => api.get<Producto[]>('/productos').then((r) => r.data),
  buscar: (id: number) => api.get<Producto>(`/productos/${id}`).then((r) => r.data),
  crear: (p: Producto) => api.post<Producto>('/productos', p).then((r) => r.data),
  actualizar: (id: number, p: Producto) => api.put<Producto>(`/productos/${id}`, p).then((r) => r.data),
  eliminar: (id: number) => api.delete(`/productos/${id}`),
};
