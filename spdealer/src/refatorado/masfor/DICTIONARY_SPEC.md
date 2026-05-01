# Especificação para `dictionary_tables` e `dictionary_columns`

Objetivo: garantir metadados essenciais para que um CRUD gerado siga as regras do projeto (FormBuilder v2 CODE mode, menu, permissões, filtragem por filial, exibição em grid, validações, máscaras).

**Observações gerais do projeto**
- CRUDs de entrada devem ser gerados pelo FormBuilder v2 (CODE mode) e os artefatos gravados em `src/refatorado/` para homologação.
- Sempre filtrar por filial: use `session.getAttribute("id_fil")` em endpoints e crie constraints/queries que incluam `id_fil`.
- Campos obrigatórios no formulário devem mostrar um asterisco vermelho (*) ao lado do label.
- Permissões por padrão: `ROLE_PARAM` e `ROLE_ADMIN` para menus do grupo Parâmetros.
- Menu solicitado: `Parâmetros -> Gerais -> Tipos de Fornecedores` com `render_mode = drawer`.

---

## `dictionary_tables` — campos obrigatórios e recomendados
Cada linha representa uma entidade/CRUD registrada no sistema. Campos sugeridos (nome, tipo e explicação):

- `table_name` (varchar) — nome físico da tabela no banco (ex.: `masfor`). *Obrigatório.*
- `entity_name` (varchar) — nome da entidade/DTO/JS type (ex.: `Masfor`). *Recomendado.*
- `label` (varchar) — label amigável para menus e títulos (ex.: `Tipos de Fornecedores`). *Obrigatório.*
- `api_path` (varchar) — caminho base do endpoint REST (ex.: `/api/refatorado/masfor`). *Obrigatório.*
- `menu_group` (varchar) — grupo do menu (ex.: `parametros`). *Recomendado.*
- `menu_subgroup` (varchar|null) — subgrupo do menu (ex.: `gerais`).
- `menu_item` (varchar) — label do item de menu (ex.: `Tipos de Fornecedores`).
- `render_mode` (enum: `page|drawer|modal`) — como o frontend deve abrir o formulário. *Obrigatório para menu-driven CRUDs.*
- `default_permissions` (json/text) — lista de roles por padrão (`["ROLE_PARAM","ROLE_ADMIN"]`).
- `id_fil_required` (boolean) — se `true`, todos os selects/queries devem incluir `id_fil` (default `true`).
- `enable_grid` (boolean) — se deve ter AG Grid padrão listando registros (default `true`).
- `default_grid_columns` (json) — lista de colunas padrão para grid quando não houver `dictionary_columns` específico.
- `form_builder_path` (varchar) — caminho onde o artefato gerado deve ser salvo (ex.: `src/refatorado/frontend/masfor/`).
- `unique_constraints` (json) — lista de conjuntos de colunas que devem ser únicos por filial (ex.: `[ ["tipo_for","id_fil"] ]`).
- `audit` (boolean) — se o registro deve ter campos de auditoria (`criado_em`, `criado_por`, `atualizado_em`, `atualizado_por`).

Regras/Defaults do projeto (aplicar quando campo não definido):
- `id_fil_required = true` por padrão.
- `render_mode = drawer` para formulários administrativos pequenos (como parâmetros).
- `default_permissions = ["ROLE_PARAM","ROLE_ADMIN"]` para itens do grupo `parametros`.

---

## `dictionary_columns` — campos e validações
Cada coluna define como será renderizado no CRUD, grid e busca.

Campos sugeridos (nome, tipo, explicação):

- `table_name` (varchar) — FK para `dictionary_tables.table_name`.
- `column_name` (varchar) — nome físico da coluna na tabela (ex.: `tipo_for`). *Obrigatório.*
- `label` (varchar) — label exibido no formulário (já existente). *Obrigatório.*
- `type` (enum) — `string|number|decimal|boolean|date|datetime|time|select|lookup|textarea`.
- `length` (int|null) — tamanho máximo (aplica para `string`).
- `required` (boolean) — se campo é obrigatório no formulário (coloca `*` no label). *Derivado de regras: se `is_key` true ou `nullable=false`.*
- `is_key` (boolean) — indica chave de negócio (usar para uniqueness/required).
- `form_visible` (boolean) — se aparece no formulário (default `true`).
- `grid_visible` (boolean) — se aparece na AG Grid (default `false` se campo `blob`/`text`).
- `search_visible` (boolean) — se aparece nos filtros de busca (default `false`).
- `is_checkbox` (boolean) — renderiza como checkbox (aplica para boolean).
- `is_lista` (boolean) — se representa uma lista/enum (quando `type=select` ou `lookup`).
- `options_source` (varchar|json) — `static|table:xxx|api:/path` ou JSON array para selects.
- `mask` (varchar|null) — máscara (ex.: `###.###.###-##`, `00:00`, `dd/MM/yyyy`).
- `validation_regex` (varchar|null) — regex para validação no frontend e backend.
- `min_length` / `max_length` (int|null) — validações de tamanho.
- `min_value` / `max_value` (number|null) — validações para número/decimal.
- `placeholder` (varchar|null) — texto placeholder no form.
- `tooltip` (varchar|null) — ajuda contextual.
- `tab` / `aba` (varchar|null) — para agrupar campos em abas (ex.: `Geral`).
- `position` (int) — ordem do campo no formulário (1..n).
- `grid_order` (int|null) — ordem na grid.
- `readonly` (boolean) — campo somente leitura no form.
- `default_value` (string|null) — valor default ao criar.
- `unique` (boolean) — marca índice único (combine com `dictionary_tables.unique_constraints`).
- `foreign_key` (json|null) — objeto `{ table: "x", column: "y", display: "label_col" }` para lookups.

Regras e comportamentos automáticos (normalizar/evitar repetição):
- Se `required` não estiver setado, inferir de `is_key` ou DDL (`nullable=false`).
- Se `type=select` e `options_source` vazio, inferir `options_source=table:{table_name}_lookup` se existir convenção.
- `form_visible=false` tipicamente usado para campos de auditoria ou FK técnicas; `grid_visible=true` para campos importantes em listagens.
- Validações simples (min/max/regex) devem ser aplicadas tanto no frontend (react-hook-form) quanto no backend (`@Valid` + service checks).

---

## Exemplo mínimo para `masfor` (recomendado)

`dictionary_tables` (resumo):
- `table_name`: `masfor`
- `entity_name`: `Masfor`
- `label`: `Tipos de Fornecedores`
- `api_path`: `/api/refatorado/masfor`
- `menu_group`: `parametros`
- `menu_subgroup`: `gerais`
- `menu_item`: `Tipos de Fornecedores`
- `render_mode`: `drawer`
- `default_permissions`: `["ROLE_PARAM","ROLE_ADMIN"]`
- `id_fil_required`: `true`
- `unique_constraints`: `[ ["tipo_for","id_fil"] ]`

`dictionary_columns` (resumo):
- `column_name`: `tipo_for`
  - `label`: `Código`
  - `type`: `string`
  - `length`: 30
  - `required`: true
  - `form_visible`: true
  - `grid_visible`: true
  - `search_visible`: true
  - `position`: 1
  - `unique`: true

- `column_name`: `descr_for`
  - `label`: `Descrição`
  - `type`: `string`
  - `length`: 200
  - `required`: true
  - `form_visible`: true
  - `grid_visible`: true
  - `search_visible`: true
  - `position`: 2

- `column_name`: `filler`
  - `label`: `Ativo`
  - `type`: `boolean`
  - `required`: false
  - `form_visible`: true
  - `grid_visible`: true
  - `is_checkbox`: true
  - `position`: 3

---

## Boas práticas de implementação
- Geração: usar FormBuilder v2 (CODE mode) para gerar TSX/CSS/Java/SQL/TYPES e salvar em `src/refatorado/`.
- Homologação: testar localmente com `npm run build` (frontend) e `mvn clean package -DskipTests` (backend); parar instâncias Java antigas (`taskkill /F /IM java.exe`) antes de iniciar o novo `java -jar target/...`.
- Validações: aplicar regras no frontend e revalidar no backend. Nunca confiar apenas no frontend.
- Indexes: criar índice único por `(tipo_for, id_fil)` conforme `unique_constraints`.

---

Arquivo de referência rápido (exemplo SQL para `dictionary_tables`/`dictionary_columns`) — inserir com revisão em ambiente de homologação antes de aplicar em produção.

```sql
-- Exemplo: inserir metadados mínimos (adapte nomes de colunas conforme schema)
INSERT INTO dictionary_tables (table_name, entity_name, label, api_path, menu_group, menu_subgroup, menu_item, render_mode, default_permissions, id_fil_required)
VALUES ('masfor','Masfor','Tipos de Fornecedores','/api/refatorado/masfor','parametros','gerais','Tipos de Fornecedores','drawer','["ROLE_PARAM","ROLE_ADMIN"]', true);

INSERT INTO dictionary_columns (table_name, column_name, label, type, length, required, form_visible, grid_visible, search_visible, position, is_checkbox, `unique`)
VALUES
 ('masfor','tipo_for','Código','string',30, true, true, true, true, 1, false, true),
 ('masfor','descr_for','Descrição','string',200, true, true, true, true, 2, false, false),
 ('masfor','filler','Ativo','boolean',null, false, true, true, false, 3, true, false);
```

---

Fim da especificação. Use este documento como fonte para preencher `dictionary_tables` e `dictionary_columns` e para orientar o FormBuilder durante a geração do CRUD.