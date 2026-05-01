-- EXAMPLE_dictionary_tables_insert_template.sql
-- Modelo de insert para `dictionary_tables` com os novos campos (preencher valores reais)

INSERT INTO dictionary_tables (
  table_name,
  entity_name,
  label,
  api_path,
  frontend_route,
  frontend_component,
  menu_group,
  menu_subgroup,
  menu_item,
  render_mode,
  default_permissions,
  form_skeleton,
  template_source,
  id_fil_required,
  enable_grid
) VALUES (
  'example_table',
  'ExampleEntity',
  'Exemplo de Entidade',
  '/api/refatorado/example_table',
  '/parametros/exemplo',
  'src/refatorado/frontend/example/ExampleView.tsx',
  'parametros',
  'gerais',
  'Exemplo',
  'drawer',
  '["ROLE_PARAM","ROLE_ADMIN"]',
  '{"toolbar_buttons":["new","search"],"grid_options":{"pageSize":100}}',
  'standard',
  true,
  true
);

-- Depois de inserir, popule as colunas relacionadas em dictionary_columns para corresponder às colunas físicas da tabela.
COMMIT;
