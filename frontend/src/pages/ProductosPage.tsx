import { useEffect, useState, type FormEvent } from 'react';
import { productosApi } from '../api/productos';
import type { Producto } from '../types';
import { Alert, Btn, Card, Input, Label, Loading, PageHeader } from '../components/ui';

const empty: Producto = { codigo: '', nombre: '', descripcion: '', stock: 0, stockMinimo: 5, precio: 0 };

export function ProductosPage() {
  const [items, setItems] = useState<Producto[]>([]);
  const [form, setForm] = useState<Producto>(empty);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showForm, setShowForm] = useState(false);

  const load = async () => {
    setLoading(true);
    try {
      setItems(await productosApi.listar());
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
      if (editingId) {
        await productosApi.actualizar(editingId, form);
        setSuccess('Producto actualizado');
      } else {
        await productosApi.crear(form);
        setSuccess('Producto creado');
      }
      setForm(empty);
      setEditingId(null);
      setShowForm(false);
      load();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error');
    }
  };

  const handleEdit = (p: Producto) => {
    setForm(p);
    setEditingId(p.id!);
    setShowForm(true);
  };

  const handleDelete = async (id: number) => {
    if (!confirm('¿Eliminar producto?')) return;
    try {
      await productosApi.eliminar(id);
      load();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error');
    }
  };

  return (
    <div>
      <PageHeader
        title="Productos"
        description="Catálogo de productos e inventario"
        action={<Btn onClick={() => { setForm(empty); setEditingId(null); setShowForm(true); }}>+ Nuevo producto</Btn>}
      />
      {error && <Alert message={error} />}
      {success && <Alert message={success} type="success" />}

      {showForm && (
        <Card className="p-6 mb-6">
          <h3 className="font-semibold mb-4">{editingId ? 'Editar' : 'Nuevo'} producto</h3>
          <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div><Label>Código</Label><Input value={form.codigo} onChange={(e) => setForm({ ...form, codigo: e.target.value })} required /></div>
            <div><Label>Nombre</Label><Input value={form.nombre} onChange={(e) => setForm({ ...form, nombre: e.target.value })} required /></div>
            <div className="md:col-span-2"><Label>Descripción</Label><Input value={form.descripcion} onChange={(e) => setForm({ ...form, descripcion: e.target.value })} /></div>
            <div><Label>Stock</Label><Input type="number" value={form.stock} onChange={(e) => setForm({ ...form, stock: +e.target.value })} required /></div>
            <div><Label>Stock mínimo</Label><Input type="number" value={form.stockMinimo} onChange={(e) => setForm({ ...form, stockMinimo: +e.target.value })} /></div>
            <div><Label>Precio</Label><Input type="number" step="0.01" value={form.precio} onChange={(e) => setForm({ ...form, precio: +e.target.value })} required /></div>
            <div className="md:col-span-2 flex gap-2">
              <Btn type="submit">{editingId ? 'Actualizar producto' : 'Guardar producto'}</Btn>
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
                <th className="text-left p-3 font-medium text-slate-600">Código</th>
                <th className="text-left p-3 font-medium text-slate-600">Nombre</th>
                <th className="text-right p-3 font-medium text-slate-600">Stock</th>
                <th className="text-right p-3 font-medium text-slate-600">Precio</th>
                <th className="text-right p-3 font-medium text-slate-600">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {items.map((p) => (
                <tr key={p.id} className="border-b last:border-0 hover:bg-slate-50">
                  <td className="p-3">{p.codigo}</td>
                  <td className="p-3">{p.nombre}</td>
                  <td className={`p-3 text-right font-medium ${(p.stock ?? 0) <= (p.stockMinimo ?? 0) ? 'text-red-600' : ''}`}>{p.stock}</td>
                  <td className="p-3 text-right">${p.precio?.toFixed(2)}</td>
                  <td className="p-3 text-right space-x-2">
                    <Btn variant="secondary" onClick={() => handleEdit(p)}>Editar producto</Btn>
                    <Btn variant="danger" onClick={() => handleDelete(p.id!)}>Eliminar producto</Btn>
                  </td>
                </tr>
              ))}
              {items.length === 0 && (
                <tr><td colSpan={5} className="p-8 text-center text-slate-400">No hay productos</td></tr>
              )}
            </tbody>
          </table>
        </Card>
      )}
    </div>
  );
}
