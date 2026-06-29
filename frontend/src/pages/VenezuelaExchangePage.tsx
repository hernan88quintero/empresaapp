import { useEffect, useState, type FormEvent } from 'react';
import { venezuelaExchangeApi } from '../api/venezuelaExchange';
import type { VenezuelaConversion, VenezuelaCurrency, VenezuelaExchangeRate } from '../types';
import { Alert, Btn, Card, Input, Label, Loading, PageHeader } from '../components/ui';

const currencies: VenezuelaCurrency[] = ['USD', 'VES', 'EUR'];
const today = new Date().toISOString().slice(0, 10);
const initialFrom = new Date(new Date().setMonth(new Date().getMonth() - 6))
  .toISOString().slice(0, 10);
const format = (value: number, digits = 4) =>
  Number(value).toLocaleString('es-VE', { maximumFractionDigits: digits });

export function VenezuelaExchangePage() {
  const [rates, setRates] = useState<VenezuelaExchangeRate[]>([]);
  const [history, setHistory] = useState<VenezuelaExchangeRate[]>([]);
  const [historyCurrency, setHistoryCurrency] = useState<'USD' | 'EUR'>('USD');
  const [fromDate, setFromDate] = useState(initialFrom);
  const [toDate, setToDate] = useState(today);
  const [from, setFrom] = useState<VenezuelaCurrency>('USD');
  const [to, setTo] = useState<VenezuelaCurrency>('VES');
  const [amount, setAmount] = useState('100');
  const [conversion, setConversion] = useState<VenezuelaConversion | null>(null);
  const [loading, setLoading] = useState(true);
  const [working, setWorking] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    Promise.all([
      venezuelaExchangeApi.latest(),
      venezuelaExchangeApi.history('USD', initialFrom, today),
    ]).then(([latest, records]) => {
      setRates(latest);
      setHistory(records);
    }).catch((e: Error) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  const searchHistory = async (event: FormEvent) => {
    event.preventDefault();
    setWorking(true);
    setError('');
    try {
      setHistory(await venezuelaExchangeApi.history(historyCurrency, fromDate, toDate));
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo consultar el histórico');
    } finally {
      setWorking(false);
    }
  };

  const calculate = async (event: FormEvent) => {
    event.preventDefault();
    setWorking(true);
    setError('');
    try {
      setConversion(await venezuelaExchangeApi.convert(from, to, Number(amount)));
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo realizar la conversión');
    } finally {
      setWorking(false);
    }
  };

  const sync = async () => {
    setWorking(true);
    setError('');
    setSuccess('');
    try {
      const result = await venezuelaExchangeApi.sync();
      setRates(result.rates);
      setSuccess(`${result.synchronizedRates} tasas sincronizadas correctamente.`);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo sincronizar');
    } finally {
      setWorking(false);
    }
  };

  return (
    <div>
      <PageHeader title="Tipo de cambio Venezuela"
        description="Tasas oficiales BCV, histórico y conversiones entre VES, USD y EUR"
        action={<Btn onClick={sync} disabled={working}>Sincronizar ahora</Btn>} />
      {error && <Alert message={error} />}
      {success && <Alert message={success} type="success" />}
      {loading ? <Loading /> : <>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-5 mb-7">
          {rates.map((rate) => <Card key={rate.originCurrency} className="p-6">
            <div className="flex justify-between items-start">
              <div>
                <p className="text-sm text-slate-500">1 {rate.originCurrency}</p>
                <p className="text-3xl font-bold text-slate-900 mt-1">Bs. {format(rate.rate, 6)}</p>
              </div>
              <span className={`text-xs px-2 py-1 rounded-full ${
                rate.stale ? 'bg-amber-100 text-amber-700' : 'bg-green-100 text-green-700'
              }`}>{rate.stale ? 'Respaldo local' : rate.rateType}</span>
            </div>
            <p className="text-xs text-slate-400 mt-4">
              Vigente: {rate.quotationDate} · Fuente: {rate.source}
            </p>
          </Card>)}
        </div>

        <div className="grid grid-cols-1 xl:grid-cols-2 gap-6 mb-8">
          <Card className="p-6">
            <h3 className="font-semibold text-slate-800 mb-4">Convertir monto</h3>
            <form onSubmit={calculate} className="space-y-4">
              <div><Label>Monto</Label><Input type="number" min="0.01" step="0.01" required
                value={amount} onChange={(e) => setAmount(e.target.value)} /></div>
              <div className="grid grid-cols-2 gap-3">
                <div><Label>Desde</Label><select className="w-full px-3 py-2 border rounded-lg"
                  value={from} onChange={(e) => setFrom(e.target.value as VenezuelaCurrency)}>
                  {currencies.map((currency) => <option key={currency}>{currency}</option>)}
                </select></div>
                <div><Label>Hacia</Label><select className="w-full px-3 py-2 border rounded-lg"
                  value={to} onChange={(e) => setTo(e.target.value as VenezuelaCurrency)}>
                  {currencies.map((currency) => <option key={currency}>{currency}</option>)}
                </select></div>
              </div>
              <Btn type="submit" disabled={working}>Calcular</Btn>
            </form>
            {conversion && <div className="mt-5 p-4 bg-brand-50 rounded-lg">
              <p className="text-sm text-brand-700">Resultado</p>
              <p className="text-2xl font-bold text-brand-900">
                {format(conversion.convertedAmount)} {conversion.to}
              </p>
              <p className="text-xs text-slate-500 mt-1">
                1 {conversion.from} = {format(conversion.rate, 8)} {conversion.to}
              </p>
            </div>}
          </Card>

          <Card className="p-6">
            <h3 className="font-semibold text-slate-800 mb-4">Consultar histórico</h3>
            <form onSubmit={searchHistory} className="space-y-3">
              <div><Label>Moneda</Label><select className="w-full px-3 py-2 border rounded-lg"
                value={historyCurrency}
                onChange={(e) => setHistoryCurrency(e.target.value as 'USD' | 'EUR')}>
                <option>USD</option><option>EUR</option>
              </select></div>
              <div className="grid grid-cols-2 gap-3">
                <div><Label>Desde</Label><Input type="date" value={fromDate}
                  onChange={(e) => setFromDate(e.target.value)} /></div>
                <div><Label>Hasta</Label><Input type="date" value={toDate}
                  onChange={(e) => setToDate(e.target.value)} /></div>
              </div>
              <Btn type="submit" disabled={working}>Buscar</Btn>
            </form>
          </Card>
        </div>

        <Card className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="bg-slate-50 border-b"><tr>
              <th className="text-left p-3">Fecha</th><th className="text-left p-3">Par</th>
              <th className="text-right p-3">Tasa</th><th className="text-left p-3">Tipo</th>
              <th className="text-left p-3">Fuente</th>
            </tr></thead>
            <tbody>
              {history.map((rate) => <tr key={rate.id} className="border-b">
                <td className="p-3">{rate.quotationDate}</td>
                <td className="p-3">{rate.originCurrency}/VES</td>
                <td className="p-3 text-right font-medium">Bs. {format(rate.rate, 6)}</td>
                <td className="p-3">{rate.rateType}</td><td className="p-3">{rate.source}</td>
              </tr>)}
              {!history.length && <tr><td colSpan={5} className="p-8 text-center text-slate-400">
                No hay tasas guardadas en el período
              </td></tr>}
            </tbody>
          </table>
        </Card>
      </>}
    </div>
  );
}
