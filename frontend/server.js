/**
 * Servidor Node.js para producción local.
 * Sirve el build de React y redirige /api al backend Spring Boot.
 *
 * Uso: npm run build && npm run server
 */
import express from 'express';
import { createProxyMiddleware } from 'http-proxy-middleware';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const app = express();
const PORT = process.env.PORT || 3000;
const API_URL = process.env.API_URL || 'http://localhost:8080';

app.use('/api', createProxyMiddleware({
  target: API_URL,
  changeOrigin: true,
}));

app.use(express.static(path.join(__dirname, 'dist')));

app.get('*', (_req, res) => {
  res.sendFile(path.join(__dirname, 'dist', 'index.html'));
});

app.listen(PORT, () => {
  console.log(`Frontend en http://localhost:${PORT}`);
  console.log(`API proxy → ${API_URL}`);
});
