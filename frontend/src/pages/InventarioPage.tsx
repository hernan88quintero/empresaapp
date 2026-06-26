import { useEffect, useState } from 'react';
import { inventarioApi } from '../api/compras';
import type { MovimientoInventario, Producto } from '../types';
import { Alert, Card, Loading, PageHeader } from '../components/ui';

export function InventarioPage() {
  const [stockBajo, setStockBajo] = useState<Producto[]>([]);
  const [movimientos, setMovimientos] = useState<MovimientoInventario[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    (async () => {
      try {
        const [bajo, movs] = await Promise.all([
          inventarioApi.stockBajo(),
          inventarioApi.movimientos(),
        ]);
        setStockBajo(bajo);
        setMovimientos(movs);
      } catch (e) {
        setError(e instanceof Error ? e.message : 'Error');
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const tipoColor = (tipo: string) => {
    if (tipo === 'ENTRADA') return 'text-green-600 bg-green-50';
    if (tipo === 'SALIDA') return 'text-red-600 bg-red-50';
    return 'text-amber-600 bg-amber-50';
  };

  if (loading) return <Loading />;

  return (
    <div>
      <PageHeader title="Inventario" description="Alertas de stock y historial de movimientos" />
      {error && <Alert message={error} />}

      <h3 className="font-semibold text-slate-700 mb-3">Stock bajo</h3>
      <Card className="overflow-hidden mb-8">
        <table className="w-full text-sm">
          <thead className="bg-slate-50 border-b">
            <tr>
              <th className="text-left p-3">Producto</th>
              <th className="text-right p-3">Stock actual</th>
              <th className="text-right p-3">Stock mínimo</th>
            </tr>
          </thead>
          <tbody>
            {stockBajo.map((p) => (
              <tr key={p.id} className="border-b">
                <td className="p-3">{p.nombre}</td>
                <td className="p-3 text-right text-red-600 font-medium">{p.stock}</td>
                <td className="p-3 text-right">{p.stockMinimo}</td>
              </tr>
            ))}
            {stockBajo.length === 0 && (
              <tr><td colSpan={3} className="p-6 text-center text-slate-400">Todo el stock está en niveles normales</td></tr>
            )}
          </tbody>
        </table>
      </Card>

      <h3 className="font-semibold text-slate-700 mb-3">Movimientos recientes</h3>
      <Card className="overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-slate-50 border-b">
            <tr>
              <th className="text-left p-3">Producto</th>
              <th className="text-left p-3">Tipo</th>
              <th className="text-right p-3">Cantidad</th>
              <th className="text-right p-3">Stock final</th>
              <th className="text-left p-3">Observación</th>
              <th className="text-left p-3">Fecha</th>
            </tr>
          </thead>
          <tbody>
            {movimientos.map((m) => (
              <tr key={m.id} className="border-b hover:bg-slate-50">
                <td className="p-3">{m.producto?.nombre}</td>
                <td className="p-3">
                  <span className={`px-2 py-0.5 rounded text-xs font-medium ${tipoColor(m.tipo)}`}>
                    {m.tipo}
                  </span>
                </td>
                <td className="p-3 text-right">{m.cantidad}</td>
                <td className="p-3 text-right">{m.stockResultante}</td>
                <td className="p-3 text-slate-500">{m.observacion || '—'}</td>
                <td className="p-3 text-slate-500">{new Date(m.fechaMovimiento).toLocaleString()}</td>
              </tr>
            ))}
            {movimientos.length === 0 && (
              <tr><td colSpan={6} className="p-8 text-center text-slate-400">Sin movimientos aún</td></tr>
            )}
          </tbody>
        </table>
      </Card>
    </div>
  );
}
