import { useEffect, useState, type FormEvent } from 'react';
import { comprasApi } from '../api/compras';
import { proveedoresApi } from '../api/proveedores';
import { productosApi } from '../api/productos';
import type { Compra, Proveedor, Producto } from '../types';
import { Alert, Btn, Card, Input, Label, Loading, PageHeader } from '../components/ui';

interface LineaForm {
  productoId: number;
  cantidad: number;
  precioUnitario: number;
}

export function ComprasPage() {
  const [compras, setCompras] = useState<Compra[]>([]);
  const [proveedores, setProveedores] = useState<Proveedor[]>([]);
  const [productos, setProductos] = useState<Producto[]>([]);
  const [proveedorId, setProveedorId] = useState(0);
  const [numeroFactura, setNumeroFactura] = useState('');
  const [lineas, setLineas] = useState<LineaForm[]>([{ productoId: 0, cantidad: 1, precioUnitario: 0 }]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showForm, setShowForm] = useState(false);

  const load = async () => {
    setLoading(true);
    try {
      const [c, p, prod] = await Promise.all([
        comprasApi.listar(),
        proveedoresApi.listar(),
        productosApi.listar(),
      ]);
      setCompras(c);
      setProveedores(p);
      setProductos(prod);
      if (p.length && !proveedorId) setProveedorId(p[0].id!);
      if (prod.length) setLineas([{ productoId: prod[0].id!, cantidad: 1, precioUnitario: prod[0].precio }]);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    try {
      await comprasApi.registrar({
        proveedorId,
        numeroFactura: numeroFactura || undefined,
        detalles: lineas.filter((l) => l.productoId && l.cantidad > 0),
      });
      setSuccess('Compra registrada. El stock fue actualizado.');
      setShowForm(false);
      load();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error');
    }
  };

  const updateLinea = (idx: number, field: keyof LineaForm, value: number) => {
    const next = [...lineas];
    next[idx] = { ...next[idx], [field]: value };
    if (field === 'productoId') {
      const prod = productos.find((p) => p.id === value);
      if (prod) next[idx].precioUnitario = prod.precio;
    }
    setLineas(next);
  };

  return (
    <div>
      <PageHeader
        title="Compras"
        description="Registra compras a proveedores (aumenta el stock)"
        action={<Btn onClick={() => setShowForm(true)}>+ Nueva compra</Btn>}
      />
      {error && <Alert message={error} />}
      {success && <Alert message={success} type="success" />}

      {showForm && (
        <Card className="p-6 mb-6">
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <Label>Proveedor</Label>
                <select
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm"
                  value={proveedorId}
                  onChange={(e) => setProveedorId(+e.target.value)}
                  required
                >
                  {proveedores.map((p) => (
                    <option key={p.id} value={p.id}>{p.nombre}</option>
                  ))}
                </select>
              </div>
              <div>
                <Label>Nº Factura</Label>
                <Input value={numeroFactura} onChange={(e) => setNumeroFactura(e.target.value)} />
              </div>
            </div>

            <div>
              <Label>Productos</Label>
              {lineas.map((l, i) => (
                <div key={i} className="grid grid-cols-4 gap-2 mb-2">
                  <select
                    className="col-span-2 px-3 py-2 border rounded-lg text-sm"
                    value={l.productoId}
                    onChange={(e) => updateLinea(i, 'productoId', +e.target.value)}
                  >
                    {productos.map((p) => (
                      <option key={p.id} value={p.id}>{p.nombre}</option>
                    ))}
                  </select>
                  <Input type="number" placeholder="Cant." value={l.cantidad} onChange={(e) => updateLinea(i, 'cantidad', +e.target.value)} min={1} />
                  <Input type="number" step="0.01" placeholder="Precio" value={l.precioUnitario} onChange={(e) => updateLinea(i, 'precioUnitario', +e.target.value)} />
                </div>
              ))}
              <Btn type="button" variant="secondary" onClick={() => setLineas([...lineas, { productoId: productos[0]?.id ?? 0, cantidad: 1, precioUnitario: 0 }])}>
                + Agregar línea
              </Btn>
            </div>

            <div className="flex gap-2">
              <Btn type="submit">Registrar compra</Btn>
              <Btn type="button" variant="secondary" onClick={() => setShowForm(false)}>Cancelar</Btn>
            </div>
          </form>
        </Card>
      )}

      {loading ? <Loading /> : (
        <Card className="overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-slate-50 border-b">
              <tr>
                <th className="text-left p-3">ID</th>
                <th className="text-left p-3">Proveedor</th>
                <th className="text-left p-3">Factura</th>
                <th className="text-right p-3">Total</th>
                <th className="text-left p-3">Fecha</th>
              </tr>
            </thead>
            <tbody>
              {compras.map((c) => (
                <tr key={c.id} className="border-b hover:bg-slate-50">
                  <td className="p-3">#{c.id}</td>
                  <td className="p-3">{c.proveedor?.nombre}</td>
                  <td className="p-3">{c.numeroFactura || '—'}</td>
                  <td className="p-3 text-right font-medium">${Number(c.total).toFixed(2)}</td>
                  <td className="p-3 text-slate-500">{c.fechaCompra ? new Date(c.fechaCompra).toLocaleString() : '—'}</td>
                </tr>
              ))}
              {compras.length === 0 && (
                <tr><td colSpan={5} className="p-8 text-center text-slate-400">No hay compras registradas</td></tr>
              )}
            </tbody>
          </table>
        </Card>
      )}
    </div>
  );
}
