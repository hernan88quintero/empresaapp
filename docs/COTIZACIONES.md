# Módulo de cotizaciones

## Arquitectura

```text
React + Material UI
        |
        v
ExchangeRateController ---- ConversionMonedaController
        |                           |
        v                           v
ExchangeRateService <------ ConversionMonedaService
     |             |
     v             v
Frankfurter     JPA repositories
WebClient       snapshots + conversiones
```

El navegador nunca consulta directamente al proveedor. La API propia centraliza
validación, caché, timeouts, persistencia y fallback. `FrankfurterExchangeClient`
solo conoce el contrato HTTP externo; `ExchangeRateService` contiene las reglas
de aplicación; los repositorios aíslan la persistencia.

## Clases principales

- `ExchangeProperties`: configuración tipada de URL, timeouts, caché, tarea y monedas.
- `ExchangeClientConfig`: construye un `WebClient` con timeouts y Caffeine.
- `FrankfurterRateDTO`: mapea cada elemento JSON de Frankfurter v2.
- `FrankfurterExchangeClient`: adaptador del proveedor externo.
- `ExchangeRateService`: valida ISO, convierte, persiste snapshots y aplica fallback.
- `ExchangeRateSnapshot`: cotización diaria portable mediante JPA.
- `ExchangeRateSnapshotScheduler`: actualización programada de días hábiles.
- `ExchangeRateController`: contrato REST consumido por React.
- `ConversionMonedaService`: registra conversiones usando nuevamente la tasa del backend.

## Endpoints

Todos requieren el JWT de EmpresaApp.

```http
GET /api/exchange/latest
GET /api/exchange/latest/EUR
GET /api/exchange/convert?from=USD&to=ARS&amount=100
GET /api/exchange/currencies
GET /api/exchange/history?base=USD&quote=ARS&from=2026-01-01&to=2026-12-31
```

Respuesta de conversión:

```json
{
  "from": "USD",
  "to": "ARS",
  "amount": 100,
  "rate": 1420.35,
  "convertedAmount": 142035.0000,
  "date": "2026-06-26",
  "source": "FRANKFURTER",
  "stale": false
}
```

Si Frankfurter no responde y hay un snapshot:

```json
{
  "from": "USD",
  "to": "ARS",
  "amount": 100,
  "rate": 1418.90,
  "convertedAmount": 141890.0000,
  "date": "2026-06-25",
  "source": "DATABASE_FALLBACK",
  "stale": true
}
```

Sin proveedor ni dato histórico se responde HTTP `503` mediante
`ExternalExchangeServiceException`.

## Ejecución

La configuración está en `application.yml`. En producción se recomienda
sobrescribirla con `EXCHANGE_API_URL`, `EXCHANGE_SCHEDULER_CRON` y
`EXCHANGE_SCHEDULER_ZONE`.

```powershell
mvn spring-boot:run
cd frontend
npm run dev
```

Importar `postman/EmpresaApp.postman_collection.json`, ejecutar primero Login y
luego la carpeta Cotizaciones.

## Oracle e histórico

El proyecto conserva MySQL como único driver activo y las entidades usan tipos
JPA portables. Para una migración futura a Oracle, agregar `ojdbc11` al perfil
de producción y configurar:

```properties
spring.datasource.url=jdbc:oracle:thin:@//host:1521/servicio
spring.datasource.username=EMPRESA_APP
spring.datasource.password=${ORACLE_PASSWORD}
spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
spring.jpa.hibernate.ddl-auto=validate
```

En producción conviene crear las tablas e índices con Flyway o Liquibase y usar
`ddl-auto=validate`. La restricción única de `ExchangeRateSnapshot` evita
duplicar un par por fecha. `@Scheduled` obtiene la base USD cada día hábil y el
servicio también almacena respuestas exitosas bajo demanda. El endpoint
`/history` consulta rangos por par.

El modo degradado usa el último snapshot disponible y marca `stale=true`; así el
frontend puede advertir al usuario. Para escalar horizontalmente, sustituir
Caffeine por Redis y añadir un lock distribuido (por ejemplo ShedLock) a la
tarea programada. También se recomienda circuit breaker con Resilience4j,
métricas de latencia/error y alertas cuando el fallback se mantenga activo.

Las tasas son referencias institucionales agregadas. No sustituyen la tasa de
compra/venta de un banco ni incluyen comisiones o impuestos.
