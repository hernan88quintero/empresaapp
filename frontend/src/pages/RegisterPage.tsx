import { useState, type FormEvent } from 'react';
import { Link, Navigate } from 'react-router-dom';
import { registro } from '../api/auth';
import { saveUser, saveToken } from '../api/client';
import { useAuth } from '../context/AuthContext';
import { Alert, Btn, Card, Input, Label } from '../components/ui';

export function RegisterPage() {
  const { isAuthenticated, setUserFromRegister } = useAuth();
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  if (isAuthenticated) return <Navigate to="/" replace />;

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');

    if (password !== confirmPassword) {
      setError('Las contraseñas no coinciden');
      return;
    }

    setLoading(true);
    try {
      const data = await registro({ username, email, password, rol: 'EMPLEADO' });
      saveToken(data.token);
      saveUser(data);
      setUserFromRegister(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al registrarse');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-brand-900 via-brand-700 to-brand-600 flex items-center justify-center p-4">
      <Card className="w-full max-w-md p-8">
        <div className="text-center mb-8">
          <h1 className="text-2xl font-bold text-slate-800">Crear cuenta</h1>
          <p className="text-slate-500 mt-1">Regístrate para acceder al sistema</p>
        </div>
        {error && <Alert message={error} />}
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <Label>Usuario</Label>
            <Input value={username} onChange={(e) => setUsername(e.target.value)} required minLength={3} />
          </div>
          <div>
            <Label>Email</Label>
            <Input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
          </div>
          <div>
            <Label>Contraseña</Label>
            <Input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required minLength={6} />
          </div>
          <div>
            <Label>Confirmar contraseña</Label>
            <Input type="password" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} required minLength={6} />
          </div>
          <Btn type="submit" className="w-full" disabled={loading}>
            {loading ? 'Registrando...' : 'Registrarse'}
          </Btn>
        </form>
        <p className="text-sm text-center mt-6 text-slate-500">
          ¿Ya tienes cuenta?{' '}
          <Link to="/login" className="text-brand-600 font-medium hover:underline">
            Iniciar sesión
          </Link>
        </p>
      </Card>
    </div>
  );
}
