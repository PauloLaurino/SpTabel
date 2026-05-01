# FlowRunner e Flow Schema

Arquivos adicionados:
- `flow-schema.json` — esquema JSON (draft-07) para descrever flows.
- `example-flow-masfor.json` — exemplo mínimo para `masfor` (checa unicidade e salva).
- `FlowRunner.tsx` — componente React/TSX mínimo que executa steps sequenciais: `set`, `httpRequest`, `validate`, `condition`, `end`.

Como usar (local):

1. Abrir `src/refatorado/flow/FlowRunner.tsx` em uma página de testes (ex: `npm start`).
2. Importar e renderizar o componente passando `flow` ou `flowPath`:

```tsx
import FlowRunner from 'src/refatorado/flow/FlowRunner';
import example from 'src/refatorado/flow/example-flow-masfor.json';

<FlowRunner flow={example} />
// ou
<FlowRunner flowPath="/refatorado/flow/example-flow-masfor.json" />
```

Observações:
- O `FlowRunner` é propositalmente pequeno: ideal para demonstração e validação de conceito.
- Expressões em `validate` e `condition` aceitam funções ou expressões JS que recebem `ctx` como contexto.
- `httpRequest.url` e `body` suportam placeholders `{{var}}` que são substituídos pelo contexto.
- Em produção, valide e sanitize expressões antes de executar (a execução de código arbitrário é perigosa).

Próximos passos sugeridos:
- Adicionar validação do JSON contra `flow-schema.json` antes de executar.
- Implementar timeout e cancelamento de requests.
- Mapear erros e resultados para um objeto de auditoria que pode ser persistido no backend.
