import { useEffect, useState, type FormEvent } from 'react';
import { conversionesApi, exchangeApi } from '../api/conversiones';
import type { ConversionMoneda, ExchangeConversion, LatestExchange, Moneda } from '../types';
import { Alert, Btn, Card, Input, Label, Loading, PageHeader } from '../components/ui';

const MONEDAS: { codigo: Moneda; nombre: string; locale: string }[] = [
  { codigo: 'USD', nombre: 'Dólar estadounidense', locale: 'en-US' },
  { codigo: 'EUR', nombre: 'Euro', locale: 'de-DE' },
  { codigo: 'ARS', nombre: 'Peso argentino', locale: 'es-AR' },
  { codigo: 'BRL', nombre: 'Real brasileño', locale: 'pt-BR' },
  { codigo: 'CLP', nombre: 'Peso chileno', locale: 'es-CL' },
  { codigo: 'UYU', nombre: 'Peso uruguayo', locale: 'es-UY' },
  { codigo: 'GBP', nombre: 'Libra esterlina', locale: 'en-GB' },
  { codigo: 'JPY', nombre: 'Yen japonés', locale: 'ja-JP' },
];

const nombreMoneda = (codigo: Moneda) =>
  MONEDAS.find((moneda) => moneda.codigo === codigo)?.nombre ?? codigo;

const formatoMoneda = (valor: number, moneda: Moneda) =>
  new Intl.NumberFormat(
    MONEDAS.find((item) => item.codigo === moneda)?.locale ?? 'es-AR',
    { style: 'currency', currency: moneda, maximumFractionDigits: 4 },
  ).format(valor);

export function ConversionMonedasPage() {
  const [monedaOrigen, setMonedaOrigen] = useState<Moneda>('USD');
  const [monedaDestino, setMonedaDestino] = useState<Moneda>('ARS');
  const [monto, setMonto] = useState('');
  const [calculo, setCalculo] = useState<ExchangeConversion | null>(null);
  const [cotizaciones, setCotizaciones] = useState<LatestExchange | null>(null);
  const [historial, setHistorial] = useState<ConversionMoneda[]>([]);
  const [loading, setLoading] = useState(true);
  const [calculating, setCalculating] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    const cargar = async () => {
      setLoading(true);
      setError('');
      try {
        const [rates, records] = await Promise.all([
          exchangeApi.latest('ARS'),
          conversionesApi.listar(),
        ]);
        setCotizaciones(rates);
        setHistorial(records);
      } catch (e) {
        setError(e instanceof Error ? e.message : 'No se pudieron cargar las cotizaciones');
      } finally {
        setLoading(false);
      }
    };
    cargar();
  }, []);

  useEffect(() => {
    setCalculo(null);
    setSuccess('');
  }, [monedaOrigen, monedaDestino, monto]);

  const calcular = async (event: FormEvent) => {
    event.preventDefault();
    setError('');
    setSuccess('');
    if (monedaOrigen === monedaDestino) {
      setError('Seleccioná dos monedas diferentes');
      return;
    }
    setCalculating(true);
    try {
      setCalculo(await exchangeApi.convert(monedaOrigen, monedaDestino, Number(monto)));
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo calcular la conversión');
    } finally {
      setCalculating(false);
    }
  };

  const convertir = async () => {
    if (!calculo) return;
    setSaving(true);
    setError('');
    try {
      const guardada = await conversionesApi.convertir({
        monedaOrigen,
        monedaDestino,
        monto: Number(monto),
      });
      setHistorial((actual) => [guardada, ...actual]);
      setSuccess('Conversión guardada correctamente.');
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo guardar la conversión');
    } finally {
      setSaving(false);
    }
  };

  const intercambiar = () => {
    setMonedaOrigen(monedaDestino);
    setMonedaDestino(monedaOrigen);
  };

  return (
    <div>
      <PageHeader
        title="Conversión de monedas"
        description="Calculá y registrá conversiones con cotizaciones actualizadas"
      />
      {error && <Alert message={error} />}
      {success && <Alert message={success} type="success" />}

      {loading ? <Loading /> : (
        <>
          <div className="grid grid-cols-1 xl:grid-cols-5 gap-6 mb-8 items-stretch">
            <Card className="p-6 xl:col-span-3">
              <form onSubmit={calcular}>
                <div className="flex flex-col md:flex-row gap-4 items-end">
                  <div className="w-full md:flex-1">
                    <Label>Quiero convertir</Label>
                    <select
                      className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm mb-2"
                      value={monedaOrigen}
                      onChange={(e) => setMonedaOrigen(e.target.value as Moneda)}
                    >
                      {MONEDAS.map((item) => (
                        <option key={item.codigo} value={item.codigo}>{item.nombre}</option>
                      ))}
                    </select>
                    <Input
                      aria-label="Monto a convertir"
                      type="number"
                      min="0.01"
                      step="0.01"
                      placeholder="Ingresá un monto"
                      value={monto}
                      onChange={(e) => setMonto(e.target.value)}
                      required
                    />
                  </div>

                  <button
                    type="button"
                    onClick={intercambiar}
                    aria-label="Intercambiar monedas"
                    className="mb-1 h-10 w-10 rounded-full bg-slate-100 hover:bg-slate-200 text-slate-600 text-xl"
                  >
                    ⇄
                  </button>

                  <div className="w-full md:flex-1">
                    <Label>Recibo</Label>
                    <select
                      className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm mb-2"
                      value={monedaDestino}
                      onChange={(e) => setMonedaDestino(e.target.value as Moneda)}
                    >
                      {MONEDAS.map((item) => (
                        <option key={item.codigo} value={item.codigo}>{item.nombre}</option>
                      ))}
                    </select>
                    <Input
                      aria-label="Resultado de la conversión"
                      readOnly
                      value={calculo ? formatoMoneda(Number(calculo.convertedAmount), monedaDestino) : ''}
                      placeholder="Resultado"
                    />
                  </div>
                </div>

                <div className="flex gap-3 mt-6">
                  <Btn type="submit" disabled={calculating}>
                    {calculating ? 'Calculando…' : 'Calcular'}
                  </Btn>
                  <Btn
                    type="button"
                    variant="secondary"
                    onClick={convertir}
                    disabled={!calculo || saving}
                  >
                    {saving ? 'Guardando…' : 'Convertir'}
                  </Btn>
                </div>
              </form>

              {calculo && (
                <div className="mt-6 p-5 rounded-xl bg-brand-50 border border-brand-100">
                  <div className="flex items-start justify-between gap-3">
                    <div>
                      <p className="text-sm text-brand-700">Resultado de la conversión</p>
                      <p className="text-3xl font-bold text-brand-900 mt-1">
                        {formatoMoneda(Number(calculo.convertedAmount), monedaDestino)}
                      </p>
                    </div>
                    <span className={`text-xs px-2 py-1 rounded-full ${
                      calculo.stale ? 'bg-amber-100 text-amber-700' : 'bg-green-100 text-green-700'
                    }`}>
                      {calculo.stale ? 'Dato de respaldo' : 'Actualizado'}
                    </span>
                  </div>
                  <p className="text-sm text-slate-600 mt-2">
                    1 {monedaOrigen} = {Number(calculo.rate).toLocaleString('es-AR', { maximumFractionDigits: 8 })} {monedaDestino}
                  </p>
                </div>
              )}
            </Card>

            <Card className="p-6 xl:col-span-2">
              <h3 className="font-semibold text-slate-800 mb-1">Cotizaciones actuales</h3>
              <p className="text-xs text-slate-500 mb-4">Valores expresados en pesos argentinos</p>
              <div className="space-y-3">
                {MONEDAS.map(({ codigo }) => (
                  <div key={codigo} className="flex justify-between gap-4 border-b border-slate-100 pb-3">
                    <span className="text-sm text-slate-600">{nombreMoneda(codigo)}</span>
                    <span className="text-sm font-semibold whitespace-nowrap">
                      {codigo === 'ARS'
                        ? '$ 1'
                        : (cotizaciones?.rates[codigo]
                            ? 1 / Number(cotizaciones.rates[codigo])
                            : 0).toLocaleString('es-AR', {
                            style: 'currency',
                            currency: 'ARS',
                            maximumFractionDigits: 4,
                          })}
                    </span>
                  </div>
                ))}
              </div>
              {cotizaciones && (
                <p className="text-xs text-slate-400 mt-4">
                  Fecha: {new Date(`${cotizaciones.date}T00:00:00`).toLocaleDateString('es-AR')}
                  {cotizaciones.stale ? ' · respaldo local' : ' · Frankfurter'}
                </p>
              )}
            </Card>
          </div>

          <h3 className="text-lg font-semibold text-slate-800 mb-3">Conversiones guardadas</h3>
          <Card className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-slate-50 border-b">
                <tr>
                  <th className="text-left p-3">Origen</th>
                  <th className="text-left p-3">Destino</th>
                  <th className="text-right p-3">Monto</th>
                  <th className="text-right p-3">Tipo de cambio</th>
                  <th className="text-right p-3">Resultado</th>
                  <th className="text-left p-3">Fecha</th>
                </tr>
              </thead>
              <tbody>
                {historial.map((item) => (
                  <tr key={item.id} className="border-b hover:bg-slate-50">
                    <td className="p-3">{nombreMoneda(item.monedaOrigen)}</td>
                    <td className="p-3">{nombreMoneda(item.monedaDestino)}</td>
                    <td className="p-3 text-right">{formatoMoneda(Number(item.monto), item.monedaOrigen)}</td>
                    <td className="p-3 text-right">{Number(item.tipoCambio).toLocaleString('es-AR', { maximumFractionDigits: 8 })}</td>
                    <td className="p-3 text-right font-medium">{formatoMoneda(Number(item.resultado), item.monedaDestino)}</td>
                    <td className="p-3 text-slate-500">
                      {item.fechaConversion ? new Date(item.fechaConversion).toLocaleString('es-AR') : '—'}
                    </td>
                  </tr>
                ))}
                {historial.length === 0 && (
                  <tr>
                    <td colSpan={6} className="p-8 text-center text-slate-400">
                      Todavía no hay conversiones guardadas
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </Card>
        </>
      )}
    </div>
  );
}
