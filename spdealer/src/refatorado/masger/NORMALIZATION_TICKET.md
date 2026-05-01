# Ticket: Normalização do formulário `masger` (Parâmetros Gerais)

Status: aberto

Resumo:
- `masger` é um formulário legacy com comportamento fora do padrão de CRUD de entrada de dados do projeto.
- Objetivo: avaliar, estimar e — se aprovado — migrar/normalizar para o `PADRAO_ESQUELETO_FORMULARIO` ou documentar motivo para preservação.

Impacto:
- UI: possíveis mudanças no fluxo do usuário (drawers/modals, handlers customizados).
- Backend: endpoints permanecem, mas `dictionary_columns` precisa ser alinhado.
- Risco: regressão em validações/condicionais específicas do cliente.

Checklist de análise (passos mínimos):
1. Revisar o TSX atual e listar componentes customizados (arquivo fonte: `Paulo/frontend-skeleton-MenuAdminForm.tsx` e artefatos em `src/refatorado/masfor`/`masger` se existirem).
2. Mapear todas as colunas no DB para `dictionary_columns` (nome, tipo, required, opções).
3. Identificar regras de negócio/validações client-side que exigem re-implementação no backend.
4. Definir se a normalização será total (adotar padrão) ou parcial (encapsular componentes customizados e expor API compatível).
5. Estimar esforço (horas) por etapa: análise, implementação frontend, implementação backend, testes, homologação.

Plano de ação (se aprovado):
- Branch: `normalize/masger`
- Gerar artefatos FormBuilder CODE mode em `src/refatorado/masger/` (TSX, CSS, TYPES, SQL, Java)
- Atualizar `dictionary_columns` com metadados gerados
- Testes: unitários + manuales em homologação (ver checklist em `FORM_SKELETON_PATTERNS.md`)
- Homologação: validar com usuário de negócio e registrar aprovação antes de mover para `src/components`.

Critérios de aceitação:
- Formulário reproduz todas as regras de validação e fluxos críticos existentes.
- Nenhuma perda de dados ou inconsistência introduzida.
- Passar checklist automático (grid, form, segurança) definido em `FORM_SKELETON_PATTERNS.md`.

Notas:
- Enquanto o ticket estiver aberto, `dictionary_tables.template_source` deve permanecer `imported` e `is_project_specific=1`.
- Se for decidido que o formulário deve permanecer fora do padrão, adicionar documentação de exceções e pontos de atenção no repositório.

Responsáveis iniciais:
- Owner: @paulo (sugestão)  
- Devs: equipe frontend/backend responsável por parâmetros

Tempo estimado (rápido): análise 4h; implementação 1-3 dias dependendo do escopo.
