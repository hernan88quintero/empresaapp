export type Rol = 'ADMIN' | 'EMPLEADO';

export interface AuthUser {
  token: string;
  tipo: string;
  id: number;
  username: string;
  email: string;
  rol: Rol;
}

export interface Producto {
  id?: number;
  codigo: string;
  nombre: string;
  descripcion?: string;
  stock: number;
  stockMinimo?: number;
  precio: number;
}

export interface Empleado {
  id?: number;
  nombre: string;
  apellido: string;
  documento?: string;
  email?: string;
  telefono?: string;
  cargo?: string;
  departamento?: string;
  salario?: number;
  fechaIngreso?: string;
  activo: boolean;
  usuario?: { id: number };
}

export interface Proveedor {
  id?: number;
  nombre: string;
  documento?: string;
  email?: string;
  telefono?: string;
  direccion?: string;
  activo: boolean;
}

export interface Cliente {
  id?: number;
  nombre: string;
  documento?: string;
  email?: string;
  telefono?: string;
  direccion?: string;
  activo: boolean;
}

export interface DetalleCompra {
  id?: number;
  producto: Producto;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

export interface Compra {
  id?: number;
  proveedor: Proveedor;
  numeroFactura?: string;
  total: number;
  observacion?: string;
  fechaCompra?: string;
  detalles: DetalleCompra[];
}

export interface DetalleVenta {
  id?: number;
  producto: Producto;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

export interface Venta {
  id?: number;
  cliente: Cliente;
  numeroFactura?: string;
  total: number;
  observacion?: string;
  fechaVenta?: string;
  detalles: DetalleVenta[];
}

export type TipoMovimiento = 'ENTRADA' | 'SALIDA' | 'AJUSTE';

export interface MovimientoInventario {
  id: number;
  producto: Producto;
  tipo: TipoMovimiento;
  cantidad: number;
  stockResultante: number;
  observacion?: string;
  fechaMovimiento: string;
}

export interface ApiError {
  mensaje: string;
  status: number;
  fecha: string;
}
