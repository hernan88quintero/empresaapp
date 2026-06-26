import { api } from './client';
import type {
  ConversionMoneda,
  ConversionRequest,
  Cotizaciones,
  ExchangeConversion,
  LatestExchange,
  Moneda,
} from '../types';

export const conversionesApi = {
  cotizaciones: () =>
    api.get<Cotizaciones>('/conversiones/cotizaciones').then((response) => response.data),
  calcular: (request: ConversionRequest) =>
    api.post<ConversionMoneda>('/conversiones/calcular', request).then((response) => response.data),
  convertir: (request: ConversionRequest) =>
    api.post<ConversionMoneda>('/conversiones', request).then((response) => response.data),
  listar: () =>
    api.get<ConversionMoneda[]>('/conversiones').then((response) => response.data),
};

export const exchangeApi = {
  latest: (base: Moneda = 'USD') =>
    api.get<LatestExchange>(`/exchange/latest/${base}`).then((response) => response.data),
  convert: (from: Moneda, to: Moneda, amount: number) =>
    api.get<ExchangeConversion>('/exchange/convert', { params: { from, to, amount } })
      .then((response) => response.data),
  currencies: () =>
    api.get<Moneda[]>('/exchange/currencies').then((response) => response.data),
};
