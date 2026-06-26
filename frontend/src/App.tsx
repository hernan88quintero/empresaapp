import { Routes, Route, Navigate } from 'react-router-dom';
import { Layout } from './components/Layout';
import { ProtectedRoute } from './components/ProtectedRoute';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { DashboardPage } from './pages/DashboardPage';
import { ProductosPage } from './pages/ProductosPage';
import { EmpleadosPage } from './pages/EmpleadosPage';
import { ProveedoresPage } from './pages/ProveedoresPage';
import { ClientesPage } from './pages/ClientesPage';
import { ComprasPage } from './pages/ComprasPage';
import { VentasPage } from './pages/VentasPage';
import { InventarioPage } from './pages/InventarioPage';
import { UsuariosPage } from './pages/UsuariosPage';
import { AdminRoute } from './components/AdminRoute';

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/registro" element={<RegisterPage />} />
      <Route
        element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route path="/" element={<DashboardPage />} />
        <Route path="/usuarios" element={<AdminRoute><UsuariosPage /></AdminRoute>} />
        <Route path="/productos" element={<ProductosPage />} />
        <Route path="/empleados" element={<EmpleadosPage />} />
        <Route path="/proveedores" element={<ProveedoresPage />} />
        <Route path="/clientes" element={<ClientesPage />} />
        <Route path="/compras" element={<ComprasPage />} />
        <Route path="/ventas" element={<VentasPage />} />
        <Route path="/inventario" element={<InventarioPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
