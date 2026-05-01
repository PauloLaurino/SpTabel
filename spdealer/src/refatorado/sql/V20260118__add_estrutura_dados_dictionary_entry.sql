-- V20260118__add_estrutura_dados_dictionary_entry.sql
-- Insere/atualiza a entrada em dictionary_tables para o menu 'Estrutura de Dados'
INSERT INTO dictionary_tables (table_name, display_name, api_path, frontend_route, frontend_component, menu_group, menu_subgroup, menu_item, render_mode, default_permissions, template_source)
VALUES
('estrutura_dados', 'Estrutura de Dados', '/api/refatorado/estrutura-dados', '/parametros/dictionary/estrutura_dados/localizar', 'DictionaryLocalizar', 'parametros', 'gerais', 'Estrutura de Dados', 'page', 'ROLE_PARAM,ROLE_ADMIN', 'standard')
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

-- Nota: se a ordenação do menu for controlada por outra tabela (menu_items), será necessário inserir lá também.
