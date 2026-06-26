import { useEffect, useState, type FormEvent } from 'react';
import { clientesApi } from '../api/clientes';
import type { Cliente } from '../types';
import { Alert, Btn, Card, Input, Label, Loading, PageHeader } from '../components/ui';

const empty: Cliente = { nombre: '', documento: '', email: '', telefono: '', direccion: '', activo: true };

export function ClientesPage() {
  const [items, setItems] = useState<Cliente[]>([]);
  const [form, setForm] = useState<Cliente>(empty);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);

  const load = async () => {
    setLoading(true);
    try { setItems(await clientesApi.listar()); }
    catch (e) { setError(e instanceof Error ? e.message : 'Error'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    try {
      if (editingId) await clientesApi.actualizar(editingId, form);
      else await clientesApi.crear(form);
      setForm(empty); setEditingId(null); setShowForm(false); load();
    } catch (e) { setError(e instanceof Error ? e.message : 'Error'); }
  };

  return (
    <div>
      <PageHeader title="Clientes" action={<Btn onClick={() => { setForm(empty); setEditingId(null); setShowForm(true); }}>+ Nuevo cliente</Btn>} />
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
              <Btn type="submit">{editingId ? 'Actualizar cliente' : 'Guardar cliente'}</Btn>
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
              {items.map((c) => (
                <tr key={c.id} className="border-b hover:bg-slate-50">
                  <td className="p-3">{c.nombre}</td>
                  <td className="p-3">{c.documento}</td>
                  <td className="p-3">{c.email}</td>
                  <td className="p-3 text-right space-x-2">
                    <Btn variant="secondary" onClick={() => { setForm(c); setEditingId(c.id!); setShowForm(true); }}>Editar cliente</Btn>
                    <Btn variant="danger" onClick={async () => { if (confirm('¿Eliminar cliente?')) { await clientesApi.eliminar(c.id!); load(); } }}>Eliminar cliente</Btn>
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
