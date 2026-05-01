-- V20260116__add_programs_permissions_menu_entry.sql
-- Cria ou atualiza a entrada em dictionary_tables para expor o menu
-- "Programas e Permissões" em Parâmetros -> Gerais

START TRANSACTION;

INSERT INTO dictionary_tables (
  table_name, display_name, description, api_path, frontend_route, frontend_component,
  menu_group, menu_subgroup, menu_item, render_mode, default_permissions
)
VALUES (
  'programs',
  'Programas e Permissões',
  'Administração de programas e permissões do sistema',
  '/api/refatorado/programs',
  '/parametros/programas-permissoes',
  'Programs',
  'parametros',
  'gerais',
  'Programas e Permissões',
  'page',
  '["ROLE_PARAM","ROLE_ADMIN"]'
)
ON DUPLICATE KEY UPDATE
  display_name = VALUES(display_name),
  description = VALUES(description),
  api_path = VALUES(api_path),
  frontend_route = VALUES(frontend_route),
  frontend_component = VALUES(frontend_component),
  menu_group = VALUES(menu_group),
  menu_subgroup = VALUES(menu_subgroup),
  menu_item = VALUES(menu_item),
  render_mode = VALUES(render_mode),
  default_permissions = VALUES(default_permissions),
  default_permissions = VALUES(default_permissions);

COMMIT;

-- Observação: Ajuste os campos conforme necessário antes de executar em produção.
