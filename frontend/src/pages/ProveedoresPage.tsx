import { useEffect, useState, type FormEvent } from 'react';
import { proveedoresApi } from '../api/proveedores';
import type { Proveedor } from '../types';
import { Alert, Btn, Card, Input, Label, Loading, PageHeader } from '../components/ui';

const empty: Proveedor = { nombre: '', documento: '', email: '', telefono: '', direccion: '', activo: true };

export function ProveedoresPage() {
  const [items, setItems] = useState<Proveedor[]>([]);
  const [form, setForm] = useState<Proveedor>(empty);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);

  const load = async () => {
    setLoading(true);
    try { setItems(await proveedoresApi.listar()); }
    catch (e) { setError(e instanceof Error ? e.message : 'Error'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    try {
      if (editingId) await proveedoresApi.actualizar(editingId, form);
      else await proveedoresApi.crear(form);
      setForm(empty); setEditingId(null); setShowForm(false); load();
    } catch (e) { setError(e instanceof Error ? e.message : 'Error'); }
  };

  return (
    <div>
      <PageHeader title="Proveedores" action={<Btn onClick={() => { setForm(empty); setEditingId(null); setShowForm(true); }}>+ Nuevo proveedor</Btn>} />
      {error && <Alert message={error} />}
      {showForm && (
        <Card className="p-6 mb-6">
          <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div><Label>Nombre</Label><Input value={form.nombre} onChange={(e) => setForm({ ...form, nombre: e.target.value })} required /></div>
            <div><Label>Documento</Label><Input value={form.documento} onChange={(e) => setForm({ ...form, documento: e.target.value })} /></div>
            <div><Label>Email</Label><Input value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></div>
            <div><Label>Teléfono</Label><Input value={form.telefono} onChange={(e) => setForm({ ...form, telefono: e.target.value })} /></div>
            <div className="md:col-span-2"><Label>Dirección</Label><Input value={form.direccion} onChange={(e) => setForm({ ...form, direccion: e.target.value })} /></div>
            <div className="md:col-span-2 flex gap-2">
              <Btn type="submit">{editingId ? 'Actualizar proveedor' : 'Guardar proveedor'}</Btn>
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
                <th className="text-left p-3">Nombre</th>
                <th className="text-left p-3">Documento</th>
                <th className="text-left p-3">Email</th>
                <th className="text-right p-3">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {items.map((p) => (
                <tr key={p.id} className="border-b hover:bg-slate-50">
                  <td className="p-3">{p.nombre}</td>
                  <td className="p-3">{p.documento}</td>
                  <td className="p-3">{p.email}</td>
                  <td className="p-3 text-right space-x-2">
                    <Btn variant="secondary" onClick={() => { setForm(p); setEditingId(p.id!); setShowForm(true); }}>Editar proveedor</Btn>
                    <Btn variant="danger" onClick={async () => { if (confirm('¿Eliminar proveedor?')) { await proveedoresApi.eliminar(p.id!); load(); } }}>Eliminar proveedor</Btn>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}
    </div>
  );
}
