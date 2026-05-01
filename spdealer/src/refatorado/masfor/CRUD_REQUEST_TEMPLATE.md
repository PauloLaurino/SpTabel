# Template otimizado para solicitação de CRUD (modelo reutilizável)

Use este template em issues ou solicitações de mudança. Ele aproveita os defaults definidos em `dictionary_tables`/`dictionary_columns` e evita repetir regras óbvias do projeto.

---

## 1) Metadados do CRUD (preenchimento obrigatório)
- table_name: masfor
- entity_name: Masfor
- label: Tipos de Fornecedores
- api_path: /api/refatorado/masfor
- menu: { group: parametros, subgroup: gerais, item: Tipos de Fornecedores }
- render_mode: drawer
- default_permissions: ["ROLE_PARAM","ROLE_ADMIN"]
- id_fil_required: true
- enable_grid: true
- unique_constraints: [["tipo_for","id_fil"]]

> Observação: não repetir `id_fil_required=true` quando for padrão; mantenha apenas se for diferente do padrão.

---

## 2) Colunas (mínimo necessário)
Forneça só as colunas que precisam de definições específicas; os campos de auditoria e defaults são aplicados automaticamente.

Colunas (exemplo compacto):

- { column_name: tipo_for, label: Código, type: string, length: 30, required: true, form_visible: true, grid_visible: true, search_visible: true, position: 1, unique: true }
- { column_name: descr_for, label: Descrição, type: string, length: 200, required: true, form_visible: true, grid_visible: true, search_visible: true, position: 2 }
- { column_name: filler, label: Ativo, type: boolean, required: false, form_visible: true, grid_visible: true, is_checkbox: true, position: 3 }

Regras de economia de repetição (aplicar automaticamente se não informado):
- `form_visible = true` por padrão.
- `grid_visible = true` apenas para campos com `position <= 6` salvo override.
- `search_visible = true` para campos `required=true` por padrão.
- `length` assume `varchar(255)` se não informado para `type=string`.

---

## 3) Opções avançadas (quando necessário)
- options_source: "table:xxx" ou `[{value:1,label:'X'}]` para selects.
- foreign_key: { table: 'pais', column: 'id', display: 'nome' }
- validation_regex / min_length / max_length / min_value / max_value

Use apenas quando a validação padrão não for suficiente.

---

## 4) Checklist de entrega (para a equipe de dev)
- [ ] Gerar artefatos com FormBuilder v2 (CODE mode) e gravar em `src/refatorado/`.
- [ ] Commit dos artefatos de homologação (não mesclar em prod direto).
- [ ] Executar `npm run build` no frontend e `mvn clean package -DskipTests` no backend.
- [ ] Parar instância Java antiga e iniciar novo JAR (Windows: `taskkill /F /IM java.exe`).
- [ ] Testar endpoints: `GET /api/refatorado/masfor`, `POST`, `PUT`, `DELETE` com `id_fil` na sessão.
- [ ] Validar menu: usuário com `ROLE_PARAM` vê `Parâmetros -> Gerais -> Tipos de Fornecedores`, `render_mode=drawer`.

---

## 5) Exemplo pronto para colar na issue
Título: [CRUD] Tipos de Fornecedores — masfor

Corpo (copiar/colar):

```
Solicito criação/homologação do CRUD "Tipos de Fornecedores" (tabela `masfor`).

Metadados:
- table_name: masfor
- label: Tipos de Fornecedores
- api_path: /api/refatorado/masfor
- menu: Parâmetros -> Gerais -> Tipos de Fornecedores (render_mode=drawer)
- permissões: ROLE_PARAM, ROLE_ADMIN

Colunas (mínimo):
- tipo_for: Código — string(30), obrigatório, único por filial
- descr_for: Descrição — string(200), obrigatório
- filler: Ativo — boolean, checkbox

Observações:
- Usar session.getAttribute("id_fil") para filtrar todas queries.
- Gerar artefatos com FormBuilder v2 (CODE mode) e salvar em src/refatorado/ para homologação.
```

---

Fim do template. Use este arquivo para abrir a issue/ticket ou preencher formulários internos; ele foi desenhado para evitar repetição e incorporar as regras do repositório.