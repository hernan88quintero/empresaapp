# Módulo de Tipo de Cambio Venezuela

## Decisión de proveedor

Evaluación realizada el 29 de junio de 2026:

| Fuente | Gratis | Estabilidad / integración | USD y EUR | Histórico | JSON | API key |
|---|---|---|---|---|---|---|
| **BCV Today** | Sí | Archivos estáticos en GitHub Pages; contrato mínimo y OpenAPI | Sí | Hasta 1.830 días | Sí | No |
| **DolarAPI Venezuela** | Sí, código abierto MIT | REST simple y bien documentada | Sí; oficial y paralelo | Sí, por moneda y tipo | Sí | No |
| **DolarFlow** | Sí | REST/edge sencillo; proyecto más reciente | Sí; también paralelo USD | Publica historial web | Sí | No |
| **Cotizave** | Plan gratuito limitado | Contrato estable y varias fuentes/P2P | Sí | Según plan | Sí | Sí |
| **BCV directo** | Público | No ofrece una API JSON pública documentada; integrar implica scraping frágil | Sí en el sitio | No como API | No | No |

Se eligió **BCV Today** como proveedor primario porque entrega USD y EUR oficiales en una
sola llamada, incluye `effective_date`, no requiere credenciales y su histórico está
versionado como JSON estático. DolarAPI es la primera alternativa recomendada para una
segunda implementación de `VenezuelaExchangeProvider`, especialmente si se incorporan
tasas paralelas.

Documentación consultada:

- BCV Today: https://bcv.today/en/api/
- DolarAPI: https://dolarapi.com/docs/venezuela/
- DolarFlow: https://dolarflow.com/docs/
- Cotizave: https://cotizave.com/

> BCV Today y DolarAPI son intermediarios no oficiales que recopilan datos públicos del
> Banco Central de Venezuela. Para usos contables debe conservarse fuente, fecha efectiva
> y evidencia de la consulta, como hace este módulo.

## Arquitectura

El módulo vive en `com.hernan.empresaapp.venezuelaexchange` y no modifica la arquitectura
general:

```text
controller -> service -> client (API externa)
                     -> repository -> MySQL
dto          entity          exception          config
```

- `VenezuelaExchangeController`: contrato HTTP y parámetros.
- `VenezuelaExchangeService`: sincronización, fallback, histórico y conversiones.
- `VenezuelaExchangeProvider`: puerto extensible de proveedores.
- `BcvTodayClient`: adaptador WebClient con timeouts y caché Caffeine.
- `VenezuelaExchangeRateRepository`: persistencia Spring Data JPA.
- `VenezuelaExchangeScheduler`: actualización hábil diaria a las 18:15 de Caracas.

La tasa persistida sigue la convención `1 originCurrency = rate destinationCurrency`;
por ejemplo, `1 USD = 622.2135 VES`. Las conversiones USD/EUR usan VES como pivote.

## Integración

1. Ejecutar `src/main/resources/db/venezuela_exchange_module.sql` en `empresa_app`.
   Con `spring.jpa.hibernate.ddl-auto=update` la tabla también se crea automáticamente
   en desarrollo, pero el script es el mecanismo recomendado en ambientes controlados.
2. No hace falta agregar dependencias: el proyecto ya incluye WebFlux, JPA, MySQL,
   Cache y Caffeine.
3. Configurar, si se desea, las variables:
   `VENEZUELA_EXCHANGE_API_URL`, `VENEZUELA_EXCHANGE_SCHEDULER_ENABLED`,
   `VENEZUELA_EXCHANGE_SCHEDULER_CRON` y `VENEZUELA_EXCHANGE_SCHEDULER_ZONE`.
4. Iniciar Spring Boot. `@ConfigurationPropertiesScan`, `@EnableCaching` y
   `@EnableScheduling` ya estaban habilitados.
5. Importar la colección Postman y ejecutar Login antes de `POST /sync`.

## Endpoints y respuestas

`GET /api/venezuela/exchange/latest/USD`

```json
{
  "id": 1,
  "originCurrency": "USD",
  "destinationCurrency": "VES",
  "rate": 622.2135000000,
  "source": "BCV_TODAY",
  "quotationDate": "2026-06-29",
  "registeredAt": "2026-06-29T18:15:01",
  "rateType": "OFICIAL",
  "stale": false
}
```

`GET /api/venezuela/exchange/convert?from=USD&to=VES&amount=100`

```json
{
  "from": "USD",
  "to": "VES",
  "amount": 100,
  "rate": 622.2135000000,
  "convertedAmount": 62221.3500,
  "quotationDate": "2026-06-29",
  "source": "BCV_TODAY",
  "stale": false
}
```

Si la API falla, `latest` y `convert` usan el registro más reciente y responden
`stale: true`, `source: "DATABASE_FALLBACK"`. Sin datos locales se devuelve HTTP 503.
Parámetros inválidos devuelven HTTP 400 con el formato de error global del proyecto.

## Extensión

- Implementar otro adaptador de `VenezuelaExchangeProvider` y seleccionar por
  configuración o prioridad para un fallback externo encadenado.
- Incorporar DolarAPI para `PARALELO`/`PROMEDIO` sin cambiar entidad ni repositorio.
- Extraer moneda/país a catálogos y convertir el paquete en un módulo FX multinacional.
- Usar Flyway para versionar el SQL y Resilience4j para circuit breaker/métricas.
- Agregar auditoría, retención, alertas por variaciones anómalas y conciliación entre
  dos fuentes antes de aplicar tasas a facturación.
