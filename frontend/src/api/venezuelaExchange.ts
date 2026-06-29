import { api } from './client';
import type { VenezuelaConversion, VenezuelaCurrency, VenezuelaExchangeRate, VenezuelaSyncResponse } from '../types';

const base = '/venezuela/exchange';

export const venezuelaExchangeApi = {
  latest: async () => (await api.get<VenezuelaExchangeRate[]>(`${base}/latest`)).data,
  history: async (currency: Exclude<VenezuelaCurrency, 'VES'>, from: string, to: string) =>
    (await api.get<VenezuelaExchangeRate[]>(`${base}/history`, { params: { currency, from, to } })).data,
  convert: async (from: VenezuelaCurrency, to: VenezuelaCurrency, amount: number) =>
    (await api.get<VenezuelaConversion>(`${base}/convert`, { params: { from, to, amount } })).data,
  sync: async () => (await api.post<VenezuelaSyncResponse>(`${base}/sync`)).data,
};
