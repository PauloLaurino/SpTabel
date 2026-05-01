# Documento de Migração — M003 - Editor
**Gerado em:** 2026-04-13 | **Artefato:** Editor.frz | **Analista:** Claude Sonnet 4.6

---

## 1. Resumo Executivo

O **M003 - Editor** é a tela de **redação e qualificação de atos notariais** de um sistema de cartório (tabelionato). É usada pelo **escrevente e pelo tabelião** para redigir, qualificar, gravar e imprimir atos jurídicos (escrituras, procurações, etc.) vinculados a um protocolo de atendimento.

### Números do módulo

| Métrica | Valor |
|---|---|
| Formulários | 5 |
| Componentes | 162 |
| Eventos | 47 |
| Regras | 31 |
| Queries | 20 |
| Tabelas referenciadas | 9 |
| Funções conhecidas | 62 / 63 (98,4%) |
| Funções desconhecidas | 1 (`l2sFlowRunWithMessage`) |
| Function Points estimados | 148,5 (126–178 intervalo) |

### Avaliação de prontidão

A prontidão está classificada como **baixa**. O formulário não possui tabela principal vinculada (campos detectados = 0), o que indica que toda a persistência ocorre por meio de session attributes WebRun e chamadas SQL inline. A função desconhecida `l2sFlowRunWithMessage` bloqueia dois dos botões de maior criticidade funcional: **Gravar Ato** e **Qualificar**. Há ainda 14+ CALLRULEs para regras externas ao escopo deste FRZ.

### Recomendação

**Migração incremental** em 5 fases. Não iniciar pela qualificação/gravação do ato antes de resolver `l2sFlowRunWithMessage` e as regras externas (C001, GER, ALM). Iniciar pela estrutura visual e fluxos de navegação seguros.

---

## 2. Leitura Funcional da Tela

### 2.1 Contexto geral

O Editor é aberto a partir de outro formulário (não está diretamente em um menu de cadastro). Recebe via session attributes o protocolo ativo (`m_protocolo`), o estado da UF (`s_uf`), e parâmetros de tempo (`s_tempoato`). Ao entrar, configura o formulário com base nesses valores e exibe ou oculta campos conforme a UF (ex.: se UF = PR, oculta campos de Traslado e Quantidade).

### 2.2 Árvores de seleção (ARVATOS e ARVELEMENTOS)

Dois lookups em formato de árvore controlam o conteúdo central do editor:

- **ARVATOS** (árvore de Atos): ao clicar em um ato, valida se há protocolo sem partes lançadas e não lavrado; carrega a minuta do ato via `GER Ler Minuta Pelo Ato`. É o ponto de entrada para o texto do ato.
- **ARVELEMENTOS** (árvore de Elementos): ao dar duplo clique, injeta o elemento no campo de texto invisível `edtElemento` e abre o formulário do elemento para parametrização. Habilita/desabilita o botão GRAVAR ATO conforme existência de texto.

### 2.3 Editor de texto do ato

O campo `edttexto` contém o texto HTML do ato (referencia TinyMCE 5 pelos caminhos de imagem). O botão **btQUALIFICAR** coleta ~40 campos do formulário (identificação do escrevente, juramentado, tabelião, livro, folha, CENSEC, tipo de ato, natureza, emolumentos, CRECI, DOI, imoveis, etc.) e os envia via `l2sFlowRunWithMessage` para o flow `ALM 003 CAMPOS`, que processa a qualificação do texto da minuta.

### 2.4 Gravação do ato

O botão **btnGravarAto** valida que uma minuta foi selecionada e que há protocolo, em seguida chama `l2sFlowRunWithMessage` com o flow `M003-Editor-Ao Clicar-GRAVAR ATO Servidor`, passando: Protocolo, Texto, Folha do Livro, Traslado e flag de existência do ato. O processamento é **assíncrono com exibição de mensagem** ("Gravando Ato… Aguarde!").

### 2.5 Modos de impressão (Folha vs. Traslado)

O botão **btFOLHA** alterna para o modo "Folha do Livro" (mostra campos BTIMPRIMIRLIVRO, TxFolhaDoLivro, btEDITORF, edtFolha). Os botões **BTIMPRIMIRLIVRO** e **BTIMPRIMIRTRASLADO** buscam o texto do ato na tabela `ato`, substituem o caminho das imagens pelo caminho do servidor atual e chamam a rule `M003-Editor-Visualizar Impressao`. Botões **btEDITORF** e **btEDITORT** abrem o editor HTML (provavelmente em iframe/frame externo).

### 2.6 Imagens e documentos digitais

Três botões gerenciam arquivos:
- **BTATUALIZAIMG**: percorre diretório de imagens no servidor e popula a tabela `imgatotemp`
- **BTATUALIZAIMG1**: mesma lógica, mas para PDFs
- **BTATUALIZAIMG2**: abre formulário de upload {77A67895-3A6E-4B7D-9C7C-D9393F557F14}
- **BTATUMINI**: exibe ou oculta o painel de miniaturas do PDF; chama `M003 - Editor - Miniatura do PDF - Gerar PDF`

### 2.7 Emolumentos e custas

O botão **btCalcCustas** recalcula emolumentos chamando três regras externas do módulo C001 (base FRJ, selos, emolumentos finais) e atualiza a grade `grdCUSTAS1` e o campo `EDTEMOLUMENTOS_PRO`.

### 2.8 Traslado e inconsistências

**btTRASLADO** é o fluxo mais complexo em SQL: lê `inconsistencia`, executa `ebfSQLCommitTransaction`, itera resultados e verifica inconsistências. Ao detectar problemas, foca o botão `btInconsistencia`. **btInconsistencia** abre formulário de inconsistências via `ebfFormOpenFilteredForm`.

### 2.9 Recibos

**BTRECIBO** abre o relatório F001 de recibo pelo ID do recibo (`EDTNUMREC`). **btRecibo2** gera um recibo formatando datas, validando cliente e data de vencimento.

### 2.10 Gravar dados do protocolo

**btGRAVAR / btGRAVAR1** (dois botões com a mesma regra) salvam o protocolo e a minuta chamando `GER Regravar Protocolo Minuta` e `GER Regravar Not1`.

### 2.11 Saída

**btSAIR / btSAIRA / btSAIRL** chamam `GER Remover Variaveis de Sessao` e `ebfCloseForm`. Há três variações do botão sair, presumivelmente para contextos visuais diferentes.

---

## 3. Mapa de Riscos

### 3.1 Funções desconhecidas

| Função | Risco | Motivo | Contexto de uso |
|---|---|---|---|
| `l2sFlowRunWithMessage` | **ALTO** | `custom_project_function` — ausente do catálogo EBF; bloqueia gravação e qualificação do ato | `btQUALIFICAR:Ao Clicar` → flow "ALM 003 CAMPOS"; `btnGravarAto:Ao Clicar` → flow "M003-Editor-Ao Clicar-GRAVAR ATO Servidor". Provavelmente executa um flow assíncrono no servidor exibindo mensagem de progresso ao usuário |

### 3.2 Funções com adapter_required = true (médio)

| Função | Categoria | Ocorrências | Estratégia |
|---|---|---|---|
| `ebfFormChangeComponentValue` | flow/ui | 10 | React state dispatcher ou ref mutation |
| `ebfFormRefreshComponent` | flow | 8 | Invalidar query/state do componente React |
| `ebfFormGetComponentValue` | flow | 4 | Leitura de ref ou estado controlado |
| `ebfFormSetVisible` | flow | 4 | `display: none` via state booleano |
| `ebfFormSetEnabled` | flow | 5 | Atributo `disabled` via state |
| `ebfFormSetFocus` | flow | 4 | `ref.current.focus()` |
| `ebfFormGetLookupName` | ui | 3 | Valor label do select controlado |
| `ebfFormSetLookupName` | ui | 1 | Setter do label do select |
| `ebfFormOpenForm` | ui | 3 | Router navigation ou modal |
| `ebfFormOpenFilteredForm` | ui | 1 | Modal com filtro na query |
| `ebfChangeComponentValueOtherForm` | ui | 1 | State lift ou context |
| `ebfGetComponentValueFromOtherForm` | ui | 1 | Context ou store compartilhado |
| `ebfRefreshComponentOtherForm` | ui | 1 | Invalidar query de outro componente |
| `ebfGetUserCode` | system | 1 | `ctx.operadorId` do JWT claim |
| `ebfGridGetValue` | grid | 3 | Acesso a linha/coluna de tabela React |
| `ebfGroupBoxShowComponents` | ui | 1 | Toggle visibility de grupo |
| `ebfGetFormResultset` | ui | 1 | Retorno de resultado de formulário pai |
| `ebfResultSetAbsolute` | system | 1 | Cursor em resultset |

### 3.3 Regras/fluxos externos (CALLRULEs fora do escopo)

| Regra chamada | Risco | Onde é chamada |
|---|---|---|
| `GER Remover Variaveis de Sessao` | **ALTO** | Ao Sair, ARVELEMENTOS, múltiplos botões |
| `GER Ler Minuta Pelo Ato (minuta_ato)` | **ALTO** | ARVATOS ao clicar |
| `GER Retornar Nomes das Partes` | **ALTO** | ARVATOS ao clicar |
| `M003-Editor-Ao Clicar-GRAVAR ATO Servidor` | **ALTO** | btnGravarAto (via l2sFlowRunWithMessage) |
| `ALM 003 CAMPOS` | **ALTO** | btQUALIFICAR (via l2sFlowRunWithMessage) |
| `C001 - Inserir Dados Automaticamente - fin_protocolo` | Médio | btCalcCustas |
| `C001 - Protocolo - Calcular Emolumentos` | Médio | btCalcCustas, btrecalcula |
| `C001 - Protocolo - Ao Modificar Grade - Calcular Base FRJ` | Médio | btrecalcula |
| `C001 - Protocolo - Calcular Selos Grade Custas` | Médio | btrecalcula |
| `GER Regravar Protocolo Minuta` | **ALTO** | btGRAVAR/btGRAVAR1 |
| `GER Regravar Not1` | **ALTO** | btGRAVAR/btGRAVAR1 |
| `M003 - Editor - Miniatura do PDF - Gerar PDF` | Médio | BTATUMINI |
| `GER - Gerar GUID - Generico` | Baixo | BTATUMINI |
| `GER Data para Extenso` | Baixo | btQUALIFICAR (dentro da lista de params) |
| `M003-Editor-Visualizar Impressao` | Médio | BTIMPRIMIRLIVRO, BTIMPRIMIRTRASLADO |
| `F001 - Recibo - Abrir Relatorio Recibo` | Médio | BTRECIBO |
| `SweetAlert - Notificar - Botão Executar` | Baixo | btRecibo2 (notificação) |
| `Ger Retornar Nomes das Partes` | **ALTO** | ARVATOS |

### 3.4 Variáveis de sessão WebRun (sem equivalente direto)

| Variável | Risco | Estratégia |
|---|---|---|
| `m_protocolo` | Médio | Parâmetro de rota ou estado global (store) |
| `s_editor` | Baixo | Estado local React |
| `s_uf` | Médio | JWT claim `cartorio.uf` |
| `s_tempoato` | Baixo | Estado local |
| `s_livro` | Médio | Estado local ou store |
| `s_existeato` | Médio | Resultado de query (existência prévia do ato) |
| `s_jalavrado` | **ALTO** | Flag de estado do ato — lógica de negócio crítica |
| `s_texto`, `s_folha`, `s_traslado` | Baixo | Temporários locais antes de envio |
| `abaimovel` | Baixo | Flag de contexto para abertura filtrada |

### 3.5 Integrações e operações de sistema

| Item | Risco | Motivo |
|---|---|---|
| Acesso ao filesystem do servidor (BTATUALIZAIMG/1) | **ALTO** | `ebfFileBase()`, leitura de diretório por protocolo — exige mapeamento de caminho no novo backend |
| Geração de miniatura PDF (BTATUMINI) | Médio | Depende de lib Java do Maker; substituir por biblioteca Go (ex: pdfcpu, poppler via exec) |
| Editor rich-text TinyMCE 5 (imagens embutidas) | Médio | URLs de imagem contêm caminho do servidor WebRun; exige remapeamento |
| Relatório de impressão via RB (ReportBuilder) | **ALTO** | `M003-Editor-Visualizar Impressao` provavelmente usa o engine de relatórios do Maker — sem equivalente direto |

---

## 4. Estratégia de Arquitetura Alvo

### 4.1 Stack

- **Backend:** Go + Fiber v2 — padrão `Handler → Service → Repository`
- **Frontend:** TypeScript + React (componentes funcionais com hooks)
- **Auth:** JWT com claims `operador_id`, `empresa_id`, `cartorio_uf`, `cartorio_id`
- **Banco:** SQL Server — queries explícitas por repository, sem `USE database`
- **Editor de texto:** TinyMCE 5 (ou 6) integrado ao React com upload de imagens via endpoint dedicado

### 4.2 Endpoints propostos

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/editor/:protocolo` | Carrega dados do protocolo/ato para o editor |
| `GET` | `/editor/:protocolo/minuta/:ato_id` | Retorna o texto da minuta pelo ato selecionado |
| `POST` | `/editor/:protocolo/ato` | Grava o ato (substitui l2sFlowRunWithMessage + GRAVAR ATO Servidor) |
| `POST` | `/editor/:protocolo/qualificar` | Executa a qualificação (substitui ALM 003 CAMPOS) |
| `GET` | `/editor/:protocolo/elementos/:ato_id` | Lista elementos da minuta |
| `GET` | `/editor/:protocolo/emolumentos` | Calcula emolumentos |
| `GET` | `/editor/:protocolo/imagens` | Lista imagens do diretório do protocolo |
| `POST` | `/editor/:protocolo/imagens/sync` | Sincroniza imagens com `imgatotemp` |
| `POST` | `/upload/ato/:protocolo` | Upload de documentos do ato |
| `GET` | `/editor/:protocolo/impressao/:tipo` | Gera PDF para impressão (livro ou traslado) |
| `GET` | `/recibo/:id` | Abre relatório de recibo |
| `GET` | `/editor/:protocolo/miniatura` | Gera/retorna miniatura do PDF do ato |

### 4.3 Substituição de session attributes

As session attributes WebRun devem ser mapeadas para:
- `m_protocolo`, `s_uf`, `s_livro` → parâmetros da rota ou store Zustand/Redux
- `s_existeato`, `s_jalavrado` → resultado de query no `GET /editor/:protocolo`
- Temporários (`s_texto`, `s_folha`, `s_traslado`) → estado local React antes do `POST`

### 4.4 Substituição de l2sFlowRunWithMessage

Esta função executa um flow assíncrono exibindo mensagem de progresso. No novo sistema:
- Substituir por chamada `fetch` com loading state no React
- Exibir toast/spinner enquanto aguarda a resposta do endpoint
- O endpoint executa a lógica que era do "flow servidor" (GRAVAR ATO Servidor / ALM 003 CAMPOS)

### 4.5 Substituição de CALLRULEs externas

Cada CALLRULE vira um método de Service ou Repository dedicado:

```
GER Ler Minuta Pelo Ato      → minutaService.getMinutaByAto(ato_id)
GER Retornar Nomes das Partes → parteService.getPartesDoProtocolo(protocolo_id)
GER Regravar Protocolo Minuta → protocoloService.regrava(protocolo_id, params)
ALM 003 CAMPOS               → atoService.qualificar(protocolo_id, campos)
C001 - Calcular Emolumentos  → emolumentoService.calcular(params)
```

---

## 5. Plano de Fases

### Fase 1 — Descoberta (Sem código de produção)
- Resolver `l2sFlowRunWithMessage`: obter código-fonte ou especificação funcional
- Mapear flows `ALM 003 CAMPOS` e `M003-Editor-Ao Clicar-GRAVAR ATO Servidor`
- Mapear todas as CALLRULEs externas (C001, GER, ALM, F001)
- Confirmar mapeamento de filesystem para o novo backend
- Confirmar estratégia de relatórios (PDF substituto para ReportBuilder)

### Fase 2 — PoC (Prova de conceito)
- Implementar endpoint `GET /editor/:protocolo` com dados do ato
- Implementar editor TinyMCE 5 no React conectado ao backend
- Implementar `POST /editor/:protocolo/ato` com lógica mínima de gravação
- Validar integração editor ↔ backend sem qualificação

### Fase 3 — Migração Incremental (menor → maior risco)
1. Navegação e inicialização da tela (session attrs → store)
2. Lookups: ARVATOS, ARVELEMENTOS, ldiEscrevente, ldiJuramentado, EDTCODSER
3. Visibilidade de campos por UF (lógica de `s_uf`)
4. btGRAVAR / btGRAVAR1 (GER Regravar — após mapear rules externas)
5. Cálculo de emolumentos (btCalcCustas, btrecalcula — após C001 mapeado)
6. Gestão de imagens/PDFs (BTATUALIZAIMG, BTATUMINI)
7. Upload de documentos (BTATUALIZAIMG2)
8. Qualificação do ato (btQUALIFICAR → ALM 003 CAMPOS — após fase 1)
9. Gravação do ato (btnGravarAto → GRAVAR ATO Servidor — após fase 1)
10. Impressão livro/traslado (relatório PDF)
11. Recibos (BTRECIBO, btRecibo2)
12. Traslado e inconsistências (btTRASLADO, btInconsistencia)

### Fase 4 — Homologação
- Validar gravação de atos com dados reais de cartório
- Validar impressão de livros e traslados
- Validar qualificação de minutas com todos os tipos de ato

### Fase 5 — Produção
- Deploy com feature flag por cartório
- Monitoramento de erros de gravação de ato por 30 dias
- Rollback plan via feature flag

---

## 6. Perguntas ao Cliente

### Sobre `l2sFlowRunWithMessage`

1. A função `l2sFlowRunWithMessage` existe como arquivo `.js` ou `.jar` no projeto? Se sim, em qual caminho? *(Contexto: usada em btQUALIFICAR e btnGravarAto — bloqueia migração)*
2. O primeiro parâmetro é a mensagem de progresso exibida ao usuário e o segundo é o nome do flow/channel a executar? Essa função sempre executa de forma assíncrona? *(Contexto: inferido pelo uso `l2sFlowRunWithMessage("Gravando Ato, Aguarde!", "M003-Editor-Ao Clicar-GRAVAR ATO Servidor", params)`)*

### Sobre ALM 003 CAMPOS e Qualificação

3. O flow `ALM 003 CAMPOS` aplica as variáveis coletadas (escrevente, juramentado, data, CENSEC, etc.) ao texto da minuta (merge de template)? O resultado é salvo na tabela `ato` ou `minuta_ato`?
4. Qual é o contrato de entrada do flow `M003-Editor-Ao Clicar-GRAVAR ATO Servidor`? Os 5 parâmetros são: Protocolo, Texto, Folha do Livro, Traslado, Existe (flag boolean)?

### Sobre Regras externas

5. As rules `GER Regravar Protocolo Minuta` e `GER Regravar Not1` fazem UPDATE em quais tabelas? Elas são transacionais (commit/rollback)?
6. O módulo C001 (Emolumentos) já foi ou está sendo migrado? Podemos consumir um endpoint existente ou precisamos migrar as três rules também?
7. `F001 - Recibo - Abrir Relatorio Recibo` usa ReportBuilder ou gera um HTML/PDF próprio?

### Sobre sistema de relatórios

8. A impressão via `M003-Editor-Visualizar Impressao` usa o ReportBuilder do Maker? Como ele será substituído? Já existe uma estratégia definida (ex: Jasper, wkhtmltopdf, geração de PDF em Go)?

### Sobre filesystem

9. O servidor de produção tem acesso a um diretório de imagens por protocolo (`CAMINHOIMGATO_PAR + / + NOMEDOC_LIV + / + protocolo`)? Esse caminho será migrado para object storage (S3/MinIO) ou continuará em disco?

### Sobre comportamento por UF

10. A lógica de ocultar campos de Traslado quando UF = PR é uma regra fixa ou pode mudar? Existem outras UFs com comportamentos distintos além do Paraná?

### Sobre `s_jalavrado`

11. A variável `s_jalavrado` indica que um ato já foi lavrado (publicado oficialmente)? Um ato lavrado pode ainda ser editado ou está bloqueado para qualquer alteração?

---

## 7. Backlog Técnico Priorizado

### P0 — Bloqueadores (resolver antes de qualquer código de produção)

| # | Descrição | Pontos | Referência |
|---|---|---|---|
| P0-1 | Obter contrato de `l2sFlowRunWithMessage` (código-fonte ou spec) | 3 | `btQUALIFICAR`, `btnGravarAto` |
| P0-2 | Mapear e documentar flow `ALM 003 CAMPOS` (qualificação) | 4 | `btQUALIFICAR` |
| P0-3 | Mapear e documentar flow `M003-Editor-Ao Clicar-GRAVAR ATO Servidor` | 4 | `btnGravarAto` |
| P0-4 | Mapear `GER Regravar Protocolo Minuta` e `GER Regravar Not1` | 3 | `btGRAVAR` |
| P0-5 | Definir estratégia de relatórios PDF (substituto do ReportBuilder) | 3 | `BTIMPRIMIRLIVRO`, `BTIMPRIMIRTRASLADO` |

### P1 — Core funcional

| # | Descrição | Pontos | Referência |
|---|---|---|---|
| P1-1 | Endpoint `GET /editor/:protocolo` — carregar dados iniciais | 3 | Inicialização da tela |
| P1-2 | Integração TinyMCE 5 + React com upload de imagens | 4 | `edttexto` |
| P1-3 | Store de estado do editor (substituto das session vars) | 3 | 20+ session attributes |
| P1-4 | Lookups ARVATOS e ARVELEMENTOS (árvores SQL) | 3 | `minuta_ato`, `minuta_min` |
| P1-5 | Endpoint `POST /editor/:protocolo/ato` (gravar ato) | 4 | `btnGravarAto` |
| P1-6 | Endpoint `POST /editor/:protocolo/qualificar` | 5 | `btQUALIFICAR` |
| P1-7 | Lógica de visibilidade por UF no frontend | 2 | `btEDITORF`/`btFOLHA`/`s_uf` |

### P2 — Funcionalidades secundárias

| # | Descrição | Pontos | Referência |
|---|---|---|---|
| P2-1 | Endpoint de emolumentos `GET /editor/:protocolo/emolumentos` | 3 | `btCalcCustas` |
| P2-2 | Grade de custas (`grdCUSTAS1`) no React | 2 | `btrecalcula` |
| P2-3 | Endpoint `POST /editor/:protocolo/ato/gravar-protocolo` (GER Regravar) | 3 | `btGRAVAR` |
| P2-4 | Endpoint de busca da última folha do livro | 2 | `M003 - Editor - Buscar ultima folha do livro` |
| P2-5 | Qualificação de ato provisório (btnQualAtoProv) | 4 | `btnQualAtoProv` |

### P3 — Documentos e impressão

| # | Descrição | Pontos | Referência |
|---|---|---|---|
| P3-1 | Sincronização de imagens por diretório | 4 | `BTATUALIZAIMG` |
| P3-2 | Upload de documentos do ato | 3 | `BTATUALIZAIMG2` |
| P3-3 | Painel de miniaturas do PDF | 4 | `BTATUMINI` |
| P3-4 | Impressão livro / traslado (PDF) | 5 | `BTIMPRIMIRLIVRO`/`BTIMPRIMIRTRASLADO` |
| P3-5 | Recibo (BTRECIBO, btRecibo2) | 3 | `BTRECIBO`, `btRecibo2` |

### P4 — Supervisão e inconsistências

| # | Descrição | Pontos | Referência |
|---|---|---|---|
| P4-1 | Traslado com verificação de inconsistências + commit transacional | 5 | `btTRASLADO` |
| P4-2 | Modal de inconsistências filtrado por protocolo | 3 | `btInconsistencia` |
| P4-3 | Anotações do ato (grade GDANOTACOES) | 3 | `M003 - Editor - Grade Anotação - Atualizar texto` |
| P4-4 | Formulário de imóveis filtrado (MakerButton3) | 2 | `ebfFormOpenFilteredForm` → protocolo |

---

## 8. Pontos que Exigem Revisão Humana

| # | Ponto | Justificativa | Responsável |
|---|---|---|---|
| 1 | Contrato de `l2sFlowRunWithMessage` | Função ausente do catálogo — pode ter comportamento transacional, de retry ou de queue | Dev sênior + Cliente |
| 2 | Lógica interna de `ALM 003 CAMPOS` | Merge de template com ~40 campos; pode ter formatação específica por tipo de ato (Escritura vs. Procuração vs. Substabelecimento) | Analista + Cliente |
| 3 | Estratégia de `s_jalavrado` | Um ato lavrado ainda pode ser editado? Há consequências jurídicas | Cliente (regra de negócio) |
| 4 | Caminho de imagens no servidor | `CAMINHOIMGATO_PAR` é configurável; migrar para object storage exige decisão de infraestrutura | Ops + Cliente |
| 5 | Relato de emolumentos (grdCUSTAS1) | O cálculo usa tabelas de tarifas regionais (`tabtip`, `tabtin`) que variam por UF e por ano | Analista + Cliente |
| 6 | Miniatura de PDF | A geração usa biblioteca Java do Maker; escolher lib Go equivalente (pdfcpu, poppler) e validar resultado visual | Dev sênior |
| 7 | Relatório de impressão (ReportBuilder) | Template de impresso cartorial tem requisitos jurídicos de formatação; o substituto deve passar por validação do tabelião | Cliente + Dev |
| 8 | `btTRASLADO` — commit transacional | Fluxo usa `ebfSQLCommitTransaction` explícito; verificar se há BEGIN TRANSACTION implícito e se o isolamento está correto | Dev sênior |
| 9 | Compatibilidade do TinyMCE 5 com o novo sistema | Imagens embutidas no texto do ato referenciam caminho do servidor WebRun; exige estratégia de migração de conteúdo existente | Dev + Ops |
| 10 | Formulário pai do Editor | O Editor é aberto a partir de outro formulário (session `m_protocolo`); identificar qual é o formulário pai e como o protocolo será passado na nova navegação | Dev sênior + Analista |
