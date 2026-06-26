import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Card, PageHeader } from '../components/ui';

const modules = [
  { to: '/usuarios', title: 'Usuarios', desc: 'Registro y administración de cuentas', color: 'bg-violet-500', adminOnly: true },
  { to: '/productos', title: 'Productos', desc: 'Catálogo e inventario base', color: 'bg-blue-500' },
  { to: '/proveedores', title: 'Proveedores', desc: 'Gestión de proveedores', color: 'bg-violet-500' },
  { to: '/clientes', title: 'Clientes', desc: 'Base de clientes', color: 'bg-emerald-500' },
  { to: '/empleados', title: 'Empleados', desc: 'Personal de la empresa', color: 'bg-amber-500' },
  { to: '/compras', title: 'Compras', desc: 'Ingreso de mercadería (+ stock)', color: 'bg-cyan-500' },
  { to: '/ventas', title: 'Ventas', desc: 'Registro de ventas (− stock)', color: 'bg-rose-500' },
  { to: '/inventario', title: 'Inventario', desc: 'Movimientos y alertas', color: 'bg-orange-500' },
];

export function DashboardPage() {
  const { user, isAdmin } = useAuth();

  const visibleModules = modules.filter((m) => !m.adminOnly || isAdmin);

  return (
    <div>
      <PageHeader
        title={`Hola, ${user?.username}`}
        description="Selecciona un módulo para comenzar"
      />
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {visibleModules.map((m) => (
          <Link key={m.to} to={m.to}>
            <Card className="p-5 hover:shadow-md transition-shadow cursor-pointer h-full">
              <div className={`w-10 h-10 rounded-lg ${m.color} mb-3`} />
              <h3 className="font-semibold text-slate-800">{m.title}</h3>
              <p className="text-sm text-slate-500 mt-1">{m.desc}</p>
            </Card>
          </Link>
        ))}
      </div>
    </div>
  );
}
