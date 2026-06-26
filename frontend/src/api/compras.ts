import { api } from './client';
import type { Compra, Venta, MovimientoInventario, Producto } from '../types';

export const comprasApi = {
  listar: () => api.get<Compra[]>('/compras').then((r) => r.data),
  buscar: (id: number) => api.get<Compra>(`/compras/${id}`).then((r) => r.data),
  registrar: (payload: {
    proveedorId: number;
    numeroFactura?: string;
    observacion?: string;
    detalles: { productoId: number; cantidad: number; precioUnitario: number }[];
  }) => api.post<Compra>('/compras', payload).then((r) => r.data),
};

export const ventasApi = {
  listar: () => api.get<Venta[]>('/ventas').then((r) => r.data),
  buscar: (id: number) => api.get<Venta>(`/ventas/${id}`).then((r) => r.data),
  registrar: (payload: {
    clienteId: number;
    numeroFactura?: string;
    observacion?: string;
    detalles: { productoId: number; cantidad: number; precioUnitario?: number }[];
  }) => api.post<Venta>('/ventas', payload).then((r) => r.data),
};

export const inventarioApi = {
  stockBajo: () => api.get<Producto[]>('/inventario/stock-bajo').then((r) => r.data),
  movimientos: () => api.get<MovimientoInventario[]>('/inventario/movimientos').then((r) => r.data),
  movimientosProducto: (id: number) =>
    api.get<MovimientoInventario[]>(`/inventario/movimientos/producto/${id}`).then((r) => r.data),
};
