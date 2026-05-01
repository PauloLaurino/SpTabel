-- V20260117__populate_dictionary_tables_metadata.sql
-- Popula os novos campos de `dictionary_tables` com valores padrão inferidos
-- Execute APÓS aplicar a migration V20260117__add_dictionary_tables_menu_and_route.sql
-- Rodar em ambiente de homologação primeiro. Faça backup do DB antes de aplicar.

-- 1) Atualiza api_path para um padrão base quando estiver vazio
UPDATE dictionary_tables
SET api_path = CONCAT('/api/refatorado/', table_name)
WHERE api_path IS NULL OR TRIM(api_path) = '';

-- 2) Atualiza frontend_route para um padrão conservador
UPDATE dictionary_tables
SET frontend_route = CONCAT('/parametros/', LOWER(table_name))
WHERE frontend_route IS NULL OR TRIM(frontend_route) = '';

-- 3) frontend_component: usa `entity_name` quando disponível, senão `table_name`
UPDATE dictionary_tables
SET frontend_component = COALESCE(NULLIF(entity_name, ''), table_name)
WHERE frontend_component IS NULL OR TRIM(frontend_component) = '';

-- 4) menu_item: label amigável (usa `label` se existir)
UPDATE dictionary_tables
SET menu_item = COALESCE(NULLIF(label, ''), table_name)
WHERE menu_item IS NULL OR TRIM(menu_item) = '';

-- 5) menu_group: default para 'parametros' quando ausente
UPDATE dictionary_tables
SET menu_group = COALESCE(NULLIF(menu_group, ''), 'parametros')
WHERE menu_group IS NULL OR TRIM(menu_group) = '';

-- 6) default_permissions: definir permissões padrão para parâmetros
UPDATE dictionary_tables
SET default_permissions = COALESCE(NULLIF(default_permissions, ''), '["ROLE_PARAM","ROLE_ADMIN"]')
WHERE default_permissions IS NULL OR TRIM(default_permissions) = '';

-- 7) form_skeleton: small default JSON to ensure frontend defaults exist
UPDATE dictionary_tables
SET form_skeleton = COALESCE(NULLIF(form_skeleton, ''), '{"toolbar_buttons":["new","search"],"row_actions":["edit_drawer","delete"],"grid_options":{"pageSize":100}}')
WHERE form_skeleton IS NULL OR TRIM(form_skeleton) = '';

-- 8) template_source: garantir valor padrão
UPDATE dictionary_tables
SET template_source = COALESCE(NULLIF(template_source, ''), 'standard')
WHERE template_source IS NULL OR TRIM(template_source) = '';

-- 9) Preview: revisar as alterações antes de commitar/continuar
SELECT id, table_name, COALESCE(entity_name,'') AS entity_name, COALESCE(label,'') AS label,
       api_path, frontend_route, frontend_component, menu_group, menu_subgroup, menu_item,
       default_permissions, template_source
FROM dictionary_tables
ORDER BY table_name;

COMMIT;

-- Observações:
-- - Este script aplica regras conservadoras e reversíveis (override apenas quando NULL/""),
--   portanto não sobrescreve valores já definidos manualmente.
-- - Ajustes por tabela podem ser necessários: se quiser regras específicas (ex.: menus em
--   outro grupo), edite os UPDATEs antes de executar.
