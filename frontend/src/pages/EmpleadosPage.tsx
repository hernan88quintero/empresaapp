import { useEffect, useState, type FormEvent } from 'react';
import { empleadosApi } from '../api/empleados';
import type { Empleado } from '../types';
import { Alert, Btn, Card, Input, Label, Loading, PageHeader } from '../components/ui';

const empty: Empleado = {
  nombre: '', apellido: '', documento: '', email: '', telefono: '',
  cargo: '', departamento: '', salario: 0, fechaIngreso: '', activo: true,
};

export function EmpleadosPage() {
  const [items, setItems] = useState<Empleado[]>([]);
  const [form, setForm] = useState<Empleado>(empty);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);

  const load = async () => {
    setLoading(true);
    try { setItems(await empleadosApi.listar()); }
    catch (e) { setError(e instanceof Error ? e.message : 'Error'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    try {
      if (editingId) await empleadosApi.actualizar(editingId, form);
      else await empleadosApi.crear(form);
      setForm(empty); setEditingId(null); setShowForm(false); load();
    } catch (e) { setError(e instanceof Error ? e.message : 'Error'); }
  };

  return (
    <div>
      <PageHeader title="Empleados" action={<Btn onClick={() => { setForm(empty); setEditingId(null); setShowForm(true); }}>+ Nuevo empleado</Btn>} />
      {error && <Alert message={error} />}
      {showForm && (
        <Card className="p-6 mb-6">
          <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div><Label>Nombre</Label><Input value={form.nombre} onChange={(e) => setForm({ ...form, nombre: e.target.value })} required /></div>
            <div><Label>Apellido</Label><Input value={form.apellido} onChange={(e) => setForm({ ...form, apellido: e.target.value })} required /></div>
            <div><Label>Documento</Label><Input value={form.documento} onChange={(e) => setForm({ ...form, documento: e.target.value })} /></div>
            <div><Label>Email</Label><Input value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></div>
            <div><Label>Cargo</Label><Input value={form.cargo} onChange={(e) => setForm({ ...form, cargo: e.target.value })} /></div>
            <div><Label>Departamento</Label><Input value={form.departamento} onChange={(e) => setForm({ ...form, departamento: e.target.value })} /></div>
            <div><Label>Salario</Label><Input type="number" step="0.01" value={form.salario} onChange={(e) => setForm({ ...form, salario: +e.target.value })} /></div>
            <div><Label>Fecha ingreso</Label><Input type="date" value={form.fechaIngreso} onChange={(e) => setForm({ ...form, fechaIngreso: e.target.value })} /></div>
            <div className="md:col-span-2 flex gap-2">
              <Btn type="submit">{editingId ? 'Actualizar empleado' : 'Guardar empleado'}</Btn>
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
                <th className="text-left p-3">Cargo</th>
                <th className="text-left p-3">Departamento</th>
                <th className="text-right p-3">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {items.map((e) => (
                <tr key={e.id} className="border-b hover:bg-slate-50">
                  <td className="p-3">{e.nombre} {e.apellido}</td>
                  <td className="p-3">{e.cargo}</td>
                  <td className="p-3">{e.departamento}</td>
                  <td className="p-3 text-right space-x-2">
                    <Btn variant="secondary" onClick={() => { setForm(e); setEditingId(e.id!); setShowForm(true); }}>Editar empleado</Btn>
                    <Btn variant="danger" onClick={async () => { if (confirm('¿Eliminar empleado?')) { await empleadosApi.eliminar(e.id!); load(); } }}>Eliminar empleado</Btn>
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
