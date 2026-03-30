# sprGraficoEstoque — Integração rápida (Notas)

Arquivo adicionado: `src/main/webapp/html/assets/sprGraficoEstoque.js`

Instruções rápidas:

- Inclusão: adicione a tag `<script src="/html/assets/sprGraficoEstoque.js"></script>` no template do `Notas` (por exemplo no footer ou dentro do `Menu`), ou carregue-o via loader já presente.
- Chamada: o script auto-invoca `window.sprGraficoEstoque()` após o carregamento da página. Também é possível chamar manualmente `window.sprGraficoEstoque()`.
- Endpoints esperados (o script tenta caminhos relativos e prefixos):
  - `/maker/api/funarpen/selos/cards/estoque` — retorna JSON com `estoque` (map tipo->qtd) ou um object direto.
  - `/maker/api/funarpen/selos/cards/tipoLimites` — retorna `tipoLimites` (map tipo->limite) opcional.
  - `/certificado/status` ou `/funarpen/certificado/status` — retorna status do certificado com campos `valid`, `notAfter`, `daysToExpire`, `matchesDoc`, `matchesName`, `message`.

Observações:
- O script é resiliente a 404s: tenta prefixos `''`, `/webrun5`, `/funarpen` ao buscar os endpoints.
- Estilos são injetados pelo próprio script. Ajustes visuais podem ser feitos via CSS adicional no projeto Notas.
- Se o elemento `#Menu` não existir, o script tenta posicionar-se em áreas alternativas; se precisar de posicionamento específico, altere o `ensureContainer()` no arquivo.

Testes locais sugeridos:

1. Copie o arquivo (já adicionado) e abra uma página do `Notas` que contenha o menu.
2. Verifique console do navegador para erros de fetch. Se os endpoints não existirem, o script renderiza fallback 'Sem dados de estoque'.
3. Para testar o card do certificado, exponha temporariamente um endpoint `/certificado/status` com JSON de exemplo:

```json
{ "valid": true, "notAfter": "2026-02-27T00:00:00Z", "daysToExpire": 22 }
```

Se preferir, eu posso:
- ajustar a inclusão automática na página `Menu` do `Notas`;
- ou executar um teste de integração (se me autorizar a rodar comandos e reiniciar o serviço local).
