import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const links = [
  { to: '/', label: 'Inicio', end: true },
  { to: '/usuarios', label: 'Usuarios', adminOnly: true },
  { to: '/productos', label: 'Productos' },
  { to: '/proveedores', label: 'Proveedores' },
  { to: '/clientes', label: 'Clientes' },
  { to: '/empleados', label: 'Empleados' },
  { to: '/compras', label: 'Compras' },
  { to: '/ventas', label: 'Ventas' },
  { to: '/inventario', label: 'Inventario' },
  { to: '/conversion-monedas', label: 'Conversión de monedas' },
];

export function Layout() {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const visibleLinks = links.filter((link) => !link.adminOnly || isAdmin);

  return (
    <div className="min-h-screen bg-slate-50 flex">
      <aside className="w-64 bg-brand-900 text-white flex flex-col shrink-0">
        <div className="p-6 border-b border-white/10">
          <h1 className="text-xl font-bold tracking-tight">EmpresaApp</h1>
          <p className="text-sm text-blue-200 mt-1">Panel de gestión</p>
        </div>
        <nav className="flex-1 p-4 space-y-1">
          {visibleLinks.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              end={link.end}
              className={({ isActive }) =>
                `block px-4 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-brand-600 text-white'
                    : 'text-blue-100 hover:bg-white/10'
                }`
              }
            >
              {link.label}
            </NavLink>
          ))}
        </nav>
        <div className="p-4 border-t border-white/10">
          <p className="text-sm text-blue-200 truncate">{user?.username}</p>
          <p className="text-xs text-blue-300/70">{user?.rol}</p>
          <button
            onClick={handleLogout}
            className="mt-3 w-full text-sm py-2 rounded-lg bg-white/10 hover:bg-white/20 transition-colors"
          >
            Cerrar sesión
          </button>
        </div>
      </aside>
      <main className="flex-1 overflow-auto">
        <div className="p-8 max-w-7xl mx-auto">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
