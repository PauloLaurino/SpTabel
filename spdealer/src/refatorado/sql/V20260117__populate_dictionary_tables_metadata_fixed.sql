-- V20260117__populate_dictionary_tables_metadata_fixed.sql
-- Versão corrigida do populate que usa colunas existentes (ex.: display_name)
-- Execute APÓS a migration; não sobrescreve valores já definidos (só quando NULL/'''').

UPDATE dictionary_tables
SET api_path = CONCAT('/api/refatorado/', table_name)
WHERE api_path IS NULL OR TRIM(api_path) = '';

UPDATE dictionary_tables
SET frontend_route = CONCAT('/parametros/', LOWER(table_name))
WHERE frontend_route IS NULL OR TRIM(frontend_route) = '';

UPDATE dictionary_tables
SET frontend_component = COALESCE(NULLIF(frontend_component, ''), table_name)
WHERE frontend_component IS NULL OR TRIM(frontend_component) = '';

UPDATE dictionary_tables
SET menu_item = COALESCE(NULLIF(menu_item, ''), NULLIF(display_name, ''), table_name)
WHERE menu_item IS NULL OR TRIM(menu_item) = '';

UPDATE dictionary_tables
SET menu_group = COALESCE(NULLIF(menu_group, ''), 'parametros')
WHERE menu_group IS NULL OR TRIM(menu_group) = '';

UPDATE dictionary_tables
SET default_permissions = COALESCE(NULLIF(default_permissions, ''), '["ROLE_PARAM","ROLE_ADMIN"]')
WHERE default_permissions IS NULL OR TRIM(default_permissions) = '';

UPDATE dictionary_tables
SET form_skeleton = COALESCE(NULLIF(form_skeleton, ''), '{"toolbar_buttons":["new","search"],"row_actions":["edit_drawer","delete"],"grid_options":{"pageSize":100}}')
WHERE form_skeleton IS NULL OR TRIM(form_skeleton) = '';

UPDATE dictionary_tables
SET template_source = COALESCE(NULLIF(template_source, ''), 'standard')
WHERE template_source IS NULL OR TRIM(template_source) = '';

-- Preview
SELECT id, table_name, COALESCE(display_name,'') AS display_name, api_path, frontend_route, frontend_component, menu_group, menu_subgroup, menu_item, default_permissions, template_source
FROM dictionary_tables
ORDER BY table_name;

COMMIT;
