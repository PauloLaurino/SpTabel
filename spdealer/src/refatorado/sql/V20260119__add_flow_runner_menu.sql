-- V20260119__add_flow_runner_menu.sql
-- Insere/atualiza a entrada em dictionary_tables para o menu 'Flow Runner' (Parâmetros → Homologação)
INSERT INTO dictionary_tables (table_name, display_name, api_path, frontend_route, frontend_component, menu_group, menu_subgroup, menu_item, render_mode, default_permissions, template_source)
VALUES
('flow_runner', 'Flow Runner (Homologação)', '/api/refatorado/flow', '/parametros/flow-runner', 'FlowRunnerPage', 'parametros', 'homologacao', 'Flow Runner', 'page', 'ROLE_ADMIN,ROLE_PARAM', 'imported')
ON DUPLICATE KEY UPDATE
  display_name = VALUES(display_name),
  api_path = VALUES(api_path),
  frontend_route = VALUES(frontend_route),
  frontend_component = VALUES(frontend_component),
  menu_group = VALUES(menu_group),
  menu_subgroup = VALUES(menu_subgroup),
  menu_item = VALUES(menu_item),
  render_mode = VALUES(render_mode),
  default_permissions = VALUES(default_permissions),
  template_source = VALUES(template_source);

-- Observação: dependendo da implementação do sidebar, pode ser necessário reiniciar o frontend ou recarregar/forçar leitura das entries de `dictionary_tables`.
