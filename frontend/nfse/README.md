# NFS-e Frontend

Frontend React/TypeScript para o Emissor de Notas Fiscais de Serviços Eletrônicas (NFS-e).

## Pré-requisitos

- Node.js 18+ 
- npm 9+

## Instalação

```powershell
# Na pasta do projeto
cd frontend/nfse
npm install
```

## Executar Desenvolvimento

```powershell
npm run dev
```

O servidor iniciará em `http://localhost:3000`.

## Scripts Disponíveis

| Script | Descrição |
|--------|------------|
| `npm run dev` | Inicia servidor de desenvolvimento |
| `npm run build` | Build de produção |
| `npm run preview` | Visualizar build de produção |

## Configuração

O arquivo `vite.config.ts` contém configuração de proxy para API backend:

```typescript
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true,
  },
}
```

Ajuste o target conforme seu ambiente de desenvolvimento.

## Estrutura do Projeto

```
frontend/nfse/
├── src/
│   ├── components/
│   │   ├── Header/          # Componente de cabeçalho
│   │   └── NfseTabs/        # Abas principais (Emitir, Consultar, Cancelar, Listar)
│   ├── services/
│   │   └── nfseApi.ts       # Cliente API Axios
│   ├── styles/
│   │   └── global.css       # Estilos globais e variáveis CSS
│   ├── types/
│   │   └── nfse.ts          # TypeScript interfaces
│   ├── App.tsx
│   ├── App.css
│   └── main.tsx
├── index.html
├── package.json
├── tsconfig.json
└── vite.config.ts
```

## Integração com Backend

O frontend consome a API REST disponível em `/api/nfse/*` conforme definido em `NfseController.java`:

- `GET /api/nfse/notas` - Lista notas com paginação
- `GET /api/nfse/pendentes` - Lista notas pendentes
- `POST /api/nfse/notas` - Cria nova nota
- `POST /api/nfse/notas/{id}/emitir` - Emite NFS-e
- `POST /api/nfse/notas/{id}/cancelar` - Cancela NFS-e
- `GET /api/nfse/notas/periodo` - Busca por período

## Design

O sistema segue uma estética "Warm Neutral" com:
- Tipografia: DM Sans (display) + JetBrains Mono (código)
- Cores: Tons quentes de âmbar/laranja como accent
- Layout: Cards com sombras suaves e bordas arredondadas
- Animações: Transições suaves emEntradas e interações