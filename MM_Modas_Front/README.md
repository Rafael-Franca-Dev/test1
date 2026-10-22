# MM GAMES — Front-end & Integração

E-commerce de jogos de tabuleiro, RPG de mesa, card games e acessórios com personalidade. O nome vem do mascote da marca: **Menino Mau**.

Stack: **React 19 · JavaScript · CSS tradicional · Vite · React Router 7 · Integração REST com Spring Boot + Fallback Local**.

## Como rodar

```bash
npm install
npm run dev       # abre em http://localhost:5173
npm run build     # gera a versão de produção em /dist
npm run preview   # testa o build
```

## Integração com o Back-end (`MM_Modas_Back`)

O módulo `src/services/api.js` conecta automaticamente o Front-end à API Spring Boot (`http://localhost:8080/api`, configurável via `VITE_API_URL`):
- `GET /api/categorias` e `GET /api/produtos` (Catálogo e estoque)
- `POST /api/auth/registrar` e `POST /api/auth/login` (Autenticação JWT + Endereço principal)
- `POST /api/frete/simular` e `GET /api/cupons/validar/{codigo}` (Cálculo de frete por UF e validação de cupom)
- `POST /api/pedidos` (Fechamento de pedido transacional com `idempotencyKey`)

Caso o servidor Java ou o banco PostgreSQL não estejam em execução no momento, o Front-end opera de forma transparente em **fallback local** (`localStorage` + `src/data/products.js`).
