# Guia Genérico de `dictionary_tables` / `dictionary_columns`

Objetivo: definir um conjunto compacto e prático de metadados que permita ao desenvolvedor gerar CRUDs confiáveis e homogêneos sem ter que repetir regras já padronizadas pelo projeto.

Princípios
- DRY: evitar repetir informações óbvias. Se um valor é padrão do projeto, não precisa ser informado (ex.: `id_fil_required = true`).
- Defaults confiáveis: o sistema deve inferir comportamentos comuns (ex.: `form_visible = true` por padrão).
- Dual validation: regras declaradas em `dictionary_columns` são aplicadas no frontend e reafirmadas no backend.
- Traceabilidade: cada `dictionary_tables` registra `api_path` e `frontend_component` para facilitar deploy e testes.

---

1) Campos recomendados em `dictionary_tables` (mínimos + sugeridos)
- `table_name` (varchar) — nome físico da tabela. *Obrigatório.*
- `entity_name` (varchar) — nome da entidade/DTO/TS type (ex.: `Masfor`). *Recomendado.*
- `label` (varchar) — título amigável (ex.: `Tipos de Fornecedores`). *Obrigatório.*
- `api_path` (varchar) — rota REST base (ex.: `/api/refatorado/masfor`). *Obrigatório.*
- `frontend_route` (varchar|null) — rota SPA para navegar direto (ex.: `/parametros/tipos-fornecedores`).
- `frontend_component` (varchar|null) — caminho do componente React (ex.: `src/refatorado/frontend/masfor/MasforView.tsx`).
- `menu_group` (varchar) — grupo do menu (ex.: `parametros`).
- `menu_subgroup` (varchar|null) — subgrupo (ex.: `gerais`).
- `menu_item` (varchar) — label do item do menu.
- `menu_icon` (varchar|null) — ícone padrão (opcional).
- `render_mode` (enum: `page|drawer|modal`) — como o form abre. Default: `drawer` para parâmetros.
- `default_permissions` (json) — roles default (ex.: `["ROLE_PARAM","ROLE_ADMIN"]`).
- `id_fil_required` (boolean) — filtrar por filial. Default: `true`.
- `enable_grid` (boolean) — usar AG Grid para listagem. Default: `true`.
- `default_page_size` (int) — paginação padrão (ex.: 25).
- `default_sort` (json|null) — ex.: `{col:'tipo_for',dir:'asc'}`.
- `unique_constraints` (json|null) — lista de arrays de colunas p/ índice único por filial.
- `audit` (boolean) — incluir campos de auditoria (default: `true`).
- `soft_delete` (boolean) — habilitar marcação em vez de exclusão física (opcional).
- `menu_order` (int|null) — ordem do item no menu.
- `menu_visible_expression` (varchar|null) — expressão condicional para visibilidade (ex.: `hasRole('ADMIN') && !isDemo`).

Rationale: `api_path`, `frontend_route` e `frontend_component` evitam que o desenvolvedor precise informar separadamente rota/menu/componentes ao abrir a request; o CI/pipe poderá validar existência do componente gerado.

---

2) Campos recomendados em `dictionary_columns` (padrão reutilizável)
- `table_name` (varchar) — FK para `dictionary_tables`.
- `column_name` (varchar) — nome físico da coluna. *Obrigatório.*
- `label` (varchar) — rótulo do campo (já existente).
- `type` (enum) — `string|number|decimal|boolean|date|datetime|time|select|lookup|textarea|richtext|money`.
- `length` (int|null) — para strings.
- `required` (boolean|null) — quando omitido, inferir de `is_key` ou DDL (`nullable=false`).
- `is_key` (boolean) — chave de negócio.
- `form_visible` (boolean|null) — default: `true`.
- `grid_visible` (boolean|null) — default: `position<=6`.
- `search_visible` (boolean|null) — default: `required == true`.
- `is_checkbox` (boolean) — otimização para booleanos.
- `is_lista` (boolean) — indica enum/lista.
- `options_source` (varchar|json|null) — `static:[{v,l}]|table:xxx|api:/path|enum:NAME`.
- `mask` (varchar|null) — máscara para input.
- `validation_regex` (varchar|null).
- `min_length` / `max_length` (int|null).
- `min_value` / `max_value` (number|null).
- `placeholder` (varchar|null).
- `tooltip` (varchar|null).
- `tab` / `aba` (varchar|null) — permite organizar campos por abas.
- `position` (int) — ordem no formulário.
- `grid_order` (int|null) — ordem na grid.
- `readonly` (boolean) — somente leitura.
- `default_value` (string|null).
- `unique` (boolean|null) — se true, combinar com `unique_constraints` em `dictionary_tables`.
- `foreign_key` (json|null) — `{table:'xxx', column:'id', display:'label_col', cache_ttl:60}`.
- `input_component` (varchar|null) — override do componente (ex.: `datepicker`, `autocomplete`).
- `column_format` (varchar|null) — ex.: `currency:BRL`, `date:dd/MM/yyyy`.
- `client_validate_only` (boolean) — quando validação não precisa do backend (raro).

Convenições de inferência
- Campos omitidos são preenchidos por defaults: `form_visible=true`, `position` auto incremental, `grid_visible` para top N.
- Se `type=select` e `options_source` vazio, procurar tabela convenção `{table_name}_lookup`.
- Se `foreign_key` presente, o frontend gera um `lookup` com debounce e caching (usar `cache_ttl`).

---

3) Metadados de UI/CRUD (padronizar botões, atalhos, comportamento)
Usar um único bloco metadata por `dictionary_tables` que define UI padrão do CRUD. Campos sugeridos:
- `form_skeleton` (json) — estrutura base do formulário:
  - `toolbar_buttons`: array ordenado com nomes (`new`, `search`, `export`, `refresh`).
  - `row_actions`: array (`edit_inline`, `edit_drawer`, `delete`, `duplicate`).
  - `primary_action`: `save` (label/icone/shortcut).
  - `buttons_position`: `top|bottom|both` (default `top`).
- `keyboard_shortcuts` (json) — ex.: `{ new:'N', search:'/', save:'Ctrl+S' }`.
- `search_mode` (enum) — `simple|advanced|none`.
- `exportable` (boolean) — habilitar export CSV/PDF.
- `grid_options` (json) — `pageSize`, `resizableColumns`, `pinnedColumns`, `defaultColumns`.

Padronizações rápidas (para não repetir):
- CRUDs de parâmetros: `toolbar_buttons = ["new","search"]`, `row_actions = ["edit_drawer","delete"]`, `buttons_position=top`, `primary_action=save`.
- Posição do botão `Novo` (novo registro): sempre no topo esquerdo do toolbar; `Salvar/Cancelar` no drawer/footer do form.
- Ícones: usar biblioteca unificada do projeto.

---

4) Regras operacionais (validações e segurança)
- Todas as queries devem incluir `id_fil` quando `id_fil_required=true`.
- Validações definidas em `dictionary_columns` são aplicadas no frontend e no backend com `@Valid`/Service checks.
- Campos `unique` devem gerar índice único por `(cols..., id_fil)` quando `id_fil_required=true`.
- Não confiar apenas em `client_validate_only` — backend sempre valida.

---

5) Exemplos de uso mínimo (padrões aplicados automaticamente)
- Se o request de CRUD omitir `grid_visible` em algumas colunas, o renderer mostrará as primeiras 5 colunas com `grid_visible=true` por padrão.
- Se `menu_item` informado e `frontend_component` não existir, pipeline de CI falhará (garantia de rastreabilidade).

---

6) Sugestões de campos novos para agilizar desenvolvedor (resumo rápido)
- `api_path`, `frontend_route`, `frontend_component` — evita informar rota/menu/component novamente.
- `form_skeleton` e `grid_options` — padronizam UI sem repetir botões/atalhos.
- `menu_visible_expression` — permite regras condicionais sem alterar código.
- `lookup_cache_ttl` e `options_source` unificados — padroniza selects/lookup.
- `column_format` e `input_component` — evita reespecificar componente em TSX.
- `soft_delete` e `audit` — comportamento comum, padronizar para evitar omissão.

---

7) Processo recomendado ao abrir uma nova request CRUD
1. Criar entry em `dictionary_tables` com `table_name`, `label`, `api_path`, `menu_*`, `render_mode` e `default_permissions`.
2. Inserir apenas colunas que divergem do default em `dictionary_columns` (labels, type, required, position, options_source, foreign_key).
3. Gerar artefatos com FormBuilder v2 (CODE mode) e gravar em `src/refatorado/`.
4. Executar checklist de build/test/DEPLOY local (verificar `frontend_component` e endpoints).

---

8) Checklist rápido (para o desenvolvedor que solicita)
- [ ] Preenchido: `table_name`, `label`, `api_path`, `menu_group`, `menu_item`, `render_mode`.
- [ ] Indicadas colunas que são `required`, `unique` ou `lookup`.
- [ ] Se for lookup, indicar `options_source` ou `foreign_key`.
- [ ] Confirmada a necessidade de `soft_delete`/`audit`.
- [ ] Marcar permissões default: `ROLE_PARAM, ROLE_ADMIN` para parâmetros.

---

Fim do guia. Use este arquivo como referência ao preencher `dictionary_tables`/`dictionary_columns` e para criar o `FormBuilder` artifacts de forma enxuta e padronizada.