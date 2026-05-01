# Padrões extraídos — Esqueleto de Formulário (Localizar → Editar/Incluir)

Baseado em `00_ESQUELETO_FORM_MASDEP_09JAN2026.MD` e no padrão `PADRAO_ESQUELETO_FORMULARIO`.

Resumo objetivo: padronizar o fluxo, botões, handlers, componentes auxiliares e endpoints para que o processo de geração do CRUD seja consistente, testável e seguro.

---

1) Estados e Fluxo
- Estados padrões: `localizar`, `editar`, `incluir`.
- Fluxo inicial: `localizar` → `incluir` (novo) ou `localizar` → `editar` (linha do grid).
- Ao gravar (incluir/editar) retornar para `localizar`.
- `codigo` (PK) é editável apenas no modo `incluir`; readonly em `editar`.

2) Toolbar / Botões (posição, ícones e comportamento)
- Toolbar superior (`Header`) com: título, `Novo` (lado esquerdo ou direito conforme layout), e ações primárias conforme `dictionary_tables.form_skeleton.toolbar_buttons`.
- Botões padrão no `localizar`: `Novo` (➕) — atalho `N`, `Buscar` (🔍) — atalho `/`, `Limpar Filtros` (🔄).
- Botões padrão no `form`: `← Voltar` (secondary), `💾 Gravar` (primary/success) — atalho `Ctrl+S`.
- Ações por linha no grid: `✏️ Editar` (abre drawer ou modal conforme `render_mode`), `🗑️ Excluir` (confirmação modal).
- Estilização: classes utilitárias `primary`, `secondary`, `success`, `danger` aplicadas consistentemente.

3) Grid padrão (AG Grid)
- Configuração base:
	- `rowSelection='single'`
	- `pagination=true`
	- `paginationPageSize` default global = `100` (sobrescrevível por `dictionary_tables.grid_options.pageSize`).
	- `defaultColDef: { resizable: true, sortable: true, filter: true, suppressMenu: true }`.
	- Coluna de Ações fixa/pinned left com width configurável (`dictionary_tables.grid_options.actionColumnWidth`, sugerido 90-120).

- Performance e modos:
	- Usar `clientSide` para datasets pequenos (<1000 registros).
	- Usar `infinite`/server-side para grandes volumes; API deve suportar `page`/`pageSize`/`sort`.

- Export e opções:
	- Export CSV/PDF controlado por `dictionary_tables.exportable`.
	- `defaultSort` e `pinnedColumns` definidos a partir de `dictionary_tables.grid_options`.

4) Filtros/Busca
- `FilterSection` gerado a partir de colunas com `dictionary_columns.search_visible = true`.
- Botões `Buscar` e `Limpar` abaixo dos filtros; filtros enviados como query params para o endpoint de listagem.

5) Formulário (layout e validações)
- Layout: `FormGrid` com `grid-template-columns: repeat(2,1fr)` por padrão; mobile: 1 coluna.
- Labels: usar i18n keys `entity.{table}.{column}.label`; exibir `*` em vermelho quando `required=true`.
- Integração com `react-hook-form`:
	- Gerar `register` rules a partir de `dictionary_columns` (`required`, `minLength`, `maxLength`, `pattern`).
	- Exibir mensagens de erro inline abaixo do campo.
- PK behavior: campo marcado `is_key=true` => `readonly` em `editar`.
- Validações avançadas:
	- Unicidade: endpoint `POST /api/refatorado/{entity}/validate` suportado para checks assíncronos.
	- Máscaras aplicadas conforme `dictionary_columns.mask`.
	- Form submission sempre inclui `id_fil` (injetado do contexto/session se omitido).

6) Componentes auxiliares e contratos
- `DynamicSelect` props: `{ referenceTable, value, onChange, placeholder, isMultiple }`.
- Contrato backend: `GET /api/v1/lists/{referenceTable}` → validação por whitelist e retorno `[{ codigo, descricao }]`.
- Cache: frontend cachea opções por `lookup_cache_ttl` (configurável via `dictionary_columns.foreign_key.cache_ttl`).

Segurança: whitelist configurável (ex.: `application-prod.properties` ou tabela `lists_whitelist`) para evitar SQL injection.

7) Endpoints backend (contrato detalhado)
- Listagem paginada: `GET /api/v1/{entity}?page=0&pageSize=100&sort=col,asc&filters...` → resposta padrão quando server-side paging: `{ content: [], totalElements, totalPages, page, pageSize }`.
- Create: `POST /api/v1/{entity}` — validar payload, aplicar `id_fil` da sessão se necessário.
- Update: `PUT /api/v1/{entity}/{id}` — validar `id_fil` e campos.
- Delete: `DELETE /api/v1/{entity}/{id}` — usar soft-delete se `dictionary_tables.soft_delete=true`.
- Validate: `POST /api/refatorado/{entity}/validate` — payload `{ field, value, id_fil, id? }` → `{ valid: boolean, message?: string }`.
- Lists: `GET /api/v1/lists/{tableName}` — retorna `[{ codigo, descricao }]`; rejeitar com 400 se `tableName` não estiver na whitelist.

8) Checklist automático para QA (gerável a partir do metadata)
- Grid: renderiza, paginação (100), colunas redimensionáveis, ações visíveis, export funciona.
- Form: campos obrigatórios validados, dynamic selects populados, mensagens de erro visíveis, PK readonly em edição.
- Segurança: endpoints validam `id_fil`, validações de uniqueness aplicadas no backend.

9) Convenções de código/UI e acessibilidade
- Handler names: `carregarRegistros`, `handleIncluir`, `handleEditar`, `handleExcluir`, `handleVoltar`, `handleGravar`, `handleBuscar`, `handleLimparFiltros`, `handleFormChange`.
- Component names: `{Entity}Page`, `DynamicSelect`.
- i18n: chaves `entity.{table}.{column}.label` e `entity.{table}.title`.
- Accessibility: botões com `aria-label`, formulários `role=form`, inputs com `aria-describedby` para mensagens de erro.
- Audit: ações CRUD logadas quando `dictionary_tables.audit=true` (guardar `user_id`, `id_fil`, `action`, `timestamp`).

10) Defaults reutilizáveis (reduzem especificação manual)
- `paginationPageSize`: 100 (default global — sobrescrevível por `dictionary_tables.grid_options.pageSize`).
- `grid_visible`: default para colunas com `position <= 6`.
- `form_grid_columns`: 2 (fallback mobile 1).
- `render_mode`: `drawer` para parâmetros.

11) Integração com `dictionary_tables` / `dictionary_columns` (mapeamentos diretos)
- `dictionary_columns.required=true` → `Label *` + `react-hook-form` rule `{ required: true }` + backend `@NotNull`/service check.
- `dictionary_columns.is_lista=1` → `DynamicSelect` com `referenceTable = options_source || foreign_key.table`.
- `dictionary_columns.mask` → aplicar máscara no input (usar lib padrão do projeto).
- `dictionary_columns.column_format` → formatador exibido na grid (`currency`, `date`, `percent`).

Uso
- Este arquivo deve ser referenciado pelo `GENERIC_DICTIONARY_GUIDE.md` e utilizado pelo FormBuilder exporter (CODE mode) para preencher defaults e gerar o skeleton do componente automaticamente.

---

### Exemplo `form_skeleton` (json) — snippet que pode viver em `dictionary_tables.form_skeleton`

```json
{
	"toolbar_buttons": ["new","search"],
	"row_actions": ["edit_drawer","delete"],
	"buttons_position": "top",
	"primary_action": "save",
	"keyboard_shortcuts": { "new": "N", "search": "/", "save": "Ctrl+S" },
	"grid_options": { "pageSize": 100, "resizableColumns": true, "actionColumnWidth": 100 }
}
```


---

## PADRÃO: fluxo AI / FormBuilder / Importação de TSX

Objetivo: padronizar três caminhos de criação/edição de formulários e garantir consistência com `FORM_SKELETON_PATTERNS.md` quando aplicável.

1) Cenário A — A IA gera um CRUD (recomendado)
- Desenvolvedor pede à IA: "Gere CRUD para X"; a IA usa as regras em `FORM_SKELETON_PATTERNS.md` para gerar:
	- `dictionary_tables` entry (metadados mínimos),
	- `dictionary_columns` (colunas e validações inferidas),
	- artefatos FormBuilder (TSX/CSS/TYPES/SQL/Java) salvos em `src/refatorado/{entity}`.
- Após geração, o desenvolvedor pode homologar, ajustar e promover os artefatos para `src/components`/`src/main/java` conforme processo do projeto.

2) Cenário B — Desenvolvedor cria formulário novo diretamente no FormBuilder
- Regras: o FormBuilder CODE mode deve usar `FORM_SKELETON_PATTERNS.md` como referência para defaults (grid, toolbar, validações, `id_fil`), ou o operador seleciona o template `form_skeleton` ao iniciar.
- Resultado: artefato TSX gerado deve conter os metadados (export `FORM_SKELETON_HINTS`, `MAS{ENTITY}_FORM_DEFINITION` ou similares) para ser reconhecido pelo pipeline e pelo processo de homologação.

3) Cenário C — Importar um TSX existente (de outro dev ou IA) para o FormBuilder Editor
- Regra operacional:
	- Se o TSX importado corresponde a uma entrada em `dictionary_tables`/`dictionary_columns` e foi gerado seguindo as regras, o FormBuilder, após melhorias, deve manter a compatibilidade com `FORM_SKELETON_PATTERNS.md` ao recompilar (ou seja, o artefato final deve seguir os defaults do padrão).
	- REGRA IMPORTANTE: se o TSX importado NÃO foi originalmente gerado seguindo `FORM_SKELETON_PATTERNS.md` (caso pontual/legacy), e o desenvolvedor fizer melhorias manuais no FormBuilder Editor, o sistema NÃO deve reescrever o template importado para forçá-lo a seguir o padrão. Ou seja:
			* O template original importado deve ser preservado como fonte primaria.
			* Melhorias aplicadas no editor devem respeitar a estrutura original; não aplicar automaticamente a transformação completa para o padrão.
			* Se o time decidir posteriormente padronizar o formulário, essa migração deve ser explícita (nova tarefa de normalização) e não automática pelo editor.

Racional: ERPs/CRMs frequentemente têm formulários pontuais com regras de negócio específicas; forçar reescrita automática pode quebrar comportamentos esperados.

---

## Integração com `00_TEMPLATES_ATUALIZADOS_11JAN2026`

Incluir este arquivo de templates como fonte de exemplos recicláveis para o FormBuilder. Quando o desenvolvedor selecionar um template, o FormBuilder apresenta 2 opções:
- `Standard Template (PADRAO_ESQUELETO_FORMULARIO)` — seguirá estritamente `FORM_SKELETON_PATTERNS.md` (recomendado para novos CRUDs de parâmetros).
- `Preserve Imported Template` — preserva a estrutura original e permite melhorias locais sem forçar padrão.

Implementação recomendada (pipeline):
- Ao salvar artefato do FormBuilder, gravar metadado `template_source` com valores `standard|imported` e `template_version`.
- CI deve validar: se `template_source=standard` então `dictionary_tables.form_skeleton` e `dictionary_columns` precisam existir; se `imported`, CI não reescreve automaticamente.

---

Atualize os guias e o FormBuilder Editor para expor essa escolha no fluxo de import/novo-template.

## Exemplo: formulário fora do padrão — `masger` (Parâmetros Gerais)

- Contexto: a tabela `masger` contém um formulário com comportamento e regras legacy que não se encaixam completamente no PADRÃO_ESQUELETO_FORMULARIO. Por isso ela foi marcada como `is_project_specific = 1` e `template_source = 'imported'` no `dictionary_tables`.
- Por que é fora do padrão: usa componentes híbridos, regras de renderização condicionais e ações customizadas no formulário que quebrariam comportamentos se fossem sobrescritas automaticamente pela pipeline de templates padrão.
- Regras aplicáveis para `masger`:
	- Tratar como `imported` — preservar o TSX original como fonte primária.
	- Não aplicar transformação automática completa para o padrão ao salvar no editor.
	- Registrar no repositório um ticket de normalização (opcional) contendo análise das diferenças, esforço estimado e plano de conversão para o `PADRAO_ESQUELETO_FORMULARIO` caso se decida padronizar.
	- Se for necessário aplicar pequenas melhorias, fazê-las no editor respeitando a estrutura original e documentando-as no `form_history`/`form_definitions`.

- Passos recomendados ao avaliar `masger`:
	1. Analisar o `TSX` existente e mapear campos para `dictionary_columns` (reconciliar nomes/labels/tipos).
	2. Identificar componentes e handlers customizados que precisam de substituição ou encapsulamento.
	3. Criar um branch `normalize/masger` e gerar artefatos mínimos em `src/refatorado/masger/` (FormBuilder CODE mode) para homologação.
	4. Testar comportamentos críticos (validações, condicionais e integração com backend) em homologação antes de qualquer promoção para `src/components`.
	5. Se optar por manter como `imported`, registre claramente as exceções no `FORM_SKELETON_PATTERNS.md` e no ticket de projeto.

Referência rápida: [Exemplo de ticket de normalização](src/refatorado/masger/NORMALIZATION_TICKET.md)

Fim.
Fim.