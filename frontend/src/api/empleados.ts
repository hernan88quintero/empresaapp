import { api } from './client';
import type { Empleado } from '../types';

export const empleadosApi = {
  listar: () => api.get<Empleado[]>('/empleados').then((r) => r.data),
  crear: (e: Empleado) => api.post<Empleado>('/empleados', e).then((r) => r.data),
  actualizar: (id: number, e: Empleado) => api.put<Empleado>(`/empleados/${id}`, e).then((r) => r.data),
  eliminar: (id: number) => api.delete(`/empleados/${id}`),
};
