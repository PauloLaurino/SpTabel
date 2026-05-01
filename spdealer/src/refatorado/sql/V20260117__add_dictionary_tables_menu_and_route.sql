-- V20260117__add_dictionary_tables_menu_and_route.sql
-- Adiciona colunas necessárias para suportar rota/menu/frontend metadata em dictionary_tables
-- Execute em homologação primeiro e revise backups antes de aplicar em produção.

ALTER TABLE dictionary_tables
ADD COLUMN api_path VARCHAR(255) NULL,
ADD COLUMN frontend_route VARCHAR(255) NULL,
ADD COLUMN frontend_component VARCHAR(255) NULL,
ADD COLUMN menu_group VARCHAR(100) NULL,
ADD COLUMN menu_subgroup VARCHAR(100) NULL,
ADD COLUMN menu_item VARCHAR(150) NULL,
ADD COLUMN render_mode VARCHAR(20) NOT NULL DEFAULT 'drawer',
ADD COLUMN default_permissions TEXT NULL,
ADD COLUMN form_skeleton TEXT NULL,
ADD COLUMN template_source VARCHAR(20) NOT NULL DEFAULT 'standard';

-- Índice para busca por api_path (opcional)
CREATE INDEX IF NOT EXISTS idx_dictionary_tables_api_path ON dictionary_tables(api_path(255));

-- Observações:
-- - `default_permissions` pode armazenar JSON (ex.: '["ROLE_PARAM","ROLE_ADMIN"]')
-- - `form_skeleton` armazena JSON/text com toolbar/buttons/grid_options
-- - `template_source` indica se o template é 'standard' (seguindo o padrão) ou 'imported'

COMMIT;
