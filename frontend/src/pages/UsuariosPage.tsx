import { useEffect, useState, type FormEvent } from 'react';
import { usuariosApi } from '../api/usuarios';
import type { Rol } from '../types';
import { Alert, Btn, Card, Input, Label, Loading, PageHeader } from '../components/ui';

interface FormState {
  username: string;
  email: string;
  password: string;
  rol: Rol;
  activo: boolean;
}

const empty: FormState = {
  username: '',
  email: '',
  password: '',
  rol: 'EMPLEADO',
  activo: true,
};

export function UsuariosPage() {
  const [items, setItems] = useState<Awaited<ReturnType<typeof usuariosApi.listar>>>([]);
  const [form, setForm] = useState<FormState>(empty);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showForm, setShowForm] = useState(false);

  const load = async () => {
    setLoading(true);
    try {
      setItems(await usuariosApi.listar());
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error al cargar usuarios');
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
        await usuariosApi.actualizar(editingId, {
          email: form.email,
          rol: form.rol,
          activo: form.activo,
          password: form.password || undefined,
        });
        setSuccess('Usuario actualizado');
      } else {
        await usuariosApi.crear({
          username: form.username,
          email: form.email,
          password: form.password,
          rol: form.rol,
        });
        setSuccess('Usuario registrado');
      }
      setForm(empty);
      setEditingId(null);
      setShowForm(false);
      load();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error');
    }
  };

  const handleEdit = (u: (typeof items)[0]) => {
    setForm({
      username: u.username,
      email: u.email,
      password: '',
      rol: u.rol,
      activo: u.activo,
    });
    setEditingId(u.id);
    setShowForm(true);
  };

  const handleDelete = async (id: number) => {
    if (!confirm('¿Eliminar este usuario?')) return;
    try {
      await usuariosApi.eliminar(id);
      setSuccess('Usuario eliminado');
      load();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error');
    }
  };

  return (
    <div>
      <PageHeader
        title="Usuarios"
        description="Registro y administración de cuentas del sistema"
        action={
          <Btn onClick={() => { setForm(empty); setEditingId(null); setShowForm(true); }}>
            + Nuevo usuario
          </Btn>
        }
      />
      {error && <Alert message={error} />}
      {success && <Alert message={success} type="success" />}

      {showForm && (
        <Card className="p-6 mb-6">
          <h3 className="font-semibold mb-4">{editingId ? 'Editar usuario' : 'Registrar usuario'}</h3>
          <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <Label>Usuario</Label>
              <Input
                value={form.username}
                onChange={(e) => setForm({ ...form, username: e.target.value })}
                required
                disabled={!!editingId}
                minLength={3}
              />
            </div>
            <div>
              <Label>Email</Label>
              <Input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required />
            </div>
            <div>
              <Label>{editingId ? 'Nueva contraseña (opcional)' : 'Contraseña'}</Label>
              <Input
                type="password"
                value={form.password}
                onChange={(e) => setForm({ ...form, password: e.target.value })}
                required={!editingId}
                minLength={6}
              />
            </div>
            <div>
              <Label>Rol</Label>
              <select
                className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm"
                value={form.rol}
                onChange={(e) => setForm({ ...form, rol: e.target.value as Rol })}
              >
                <option value="EMPLEADO">EMPLEADO</option>
                <option value="ADMIN">ADMIN</option>
              </select>
            </div>
            {editingId && (
              <div className="flex items-center gap-2 pt-6">
                <input
                  id="activo"
                  type="checkbox"
                  checked={form.activo}
                  onChange={(e) => setForm({ ...form, activo: e.target.checked })}
                />
                <label htmlFor="activo" className="text-sm text-slate-700">Usuario activo</label>
              </div>
            )}
            <div className="md:col-span-2 flex gap-2">
              <Btn type="submit">{editingId ? 'Actualizar usuario' : 'Registrar usuario'}</Btn>
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
                <th className="text-left p-3">Usuario</th>
                <th className="text-left p-3">Email</th>
                <th className="text-left p-3">Rol</th>
                <th className="text-left p-3">Estado</th>
                <th className="text-right p-3">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {items.map((u) => (
                <tr key={u.id} className="border-b hover:bg-slate-50">
                  <td className="p-3 font-medium">{u.username}</td>
                  <td className="p-3">{u.email}</td>
                  <td className="p-3">
                    <span className={`px-2 py-0.5 rounded text-xs font-medium ${u.rol === 'ADMIN' ? 'bg-violet-100 text-violet-700' : 'bg-slate-100 text-slate-600'}`}>
                      {u.rol}
                    </span>
                  </td>
                  <td className="p-3">
                    <span className={`px-2 py-0.5 rounded text-xs font-medium ${u.activo ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                      {u.activo ? 'Activo' : 'Inactivo'}
                    </span>
                  </td>
                  <td className="p-3 text-right space-x-2">
                    <Btn variant="secondary" onClick={() => handleEdit(u)}>Editar usuario</Btn>
                    <Btn variant="danger" onClick={() => handleDelete(u.id)}>Eliminar usuario</Btn>
                  </td>
                </tr>
              ))}
              {items.length === 0 && (
                <tr><td colSpan={5} className="p-8 text-center text-slate-400">No hay usuarios registrados</td></tr>
              )}
            </tbody>
          </table>
        </Card>
      )}
    </div>
  );
}
