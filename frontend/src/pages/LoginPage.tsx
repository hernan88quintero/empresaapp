import { useState, type FormEvent } from 'react';
import { Link, Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Alert, Btn, Card, Input, Label } from '../components/ui';

export function LoginPage() {
  const { login, isAuthenticated } = useAuth();
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('admin123');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  if (isAuthenticated) return <Navigate to="/" replace />;

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await login(username, password);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al iniciar sesión');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-brand-900 via-brand-700 to-brand-600 flex items-center justify-center p-4">
      <Card className="w-full max-w-md p-8">
        <div className="text-center mb-8">
          <h1 className="text-2xl font-bold text-slate-800">EmpresaApp</h1>
          <p className="text-slate-500 mt-1">Inicia sesión para continuar</p>
        </div>
        {error && <Alert message={error} />}
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <Label>Usuario</Label>
            <Input value={username} onChange={(e) => setUsername(e.target.value)} required />
          </div>
          <div>
            <Label>Contraseña</Label>
            <Input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
          </div>
          <Btn type="submit" className="w-full" disabled={loading}>
            {loading ? 'Ingresando...' : 'Ingresar'}
          </Btn>
        </form>
        <p className="text-sm text-center mt-6 text-slate-500">
          ¿No tienes cuenta?{' '}
          <Link to="/registro" className="text-brand-600 font-medium hover:underline">
            Registrarse
          </Link>
        </p>
        <p className="text-xs text-slate-400 text-center mt-3">
          Demo: admin / admin123
        </p>
      </Card>
    </div>
  );
}
