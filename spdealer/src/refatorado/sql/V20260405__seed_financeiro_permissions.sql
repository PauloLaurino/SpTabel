-- V20260405__seed_financeiro_permissions.sql
--.seed_financeiro_permissions.sql
-- Cria programas financeiros e permissões conforme ESTRUTURA_EXISTENTE_PERMISSOES.md
-- Executar após V20260116__add_programs_permissions_menu_entry.sql

START TRANSACTION;

-- ============================================================================
-- 1. PROGRAMAS FINANCEIROS (FINANCEIRO.PAGAMENTO.*)
-- ============================================================================

INSERT INTO programs (codigo, descricao, tipo, rota, icone, ordem, ativo, created_at, updated_at) VALUES
('FINANCEIRO.PAGAMENTO.VISUALIZAR', 'Financeiro - Pagamentos - Visualizar', 'O', NULL, NULL, 1, TRUE, NOW(), NOW()),
('FINANCEIRO.PAGAMENTO.INSERIR', 'Financeiro - Pagamentos - Criar Solicitações', 'O', NULL, NULL, 2, TRUE, NOW(), NOW()),
('FINANCEIRO.PAGAMENTO.AUTORIZAR', 'Financeiro - Pagamentos - Autorizar', 'O', NULL, NULL, 3, TRUE, NOW(), NOW()),
('FINANCEIRO.PAGAMENTO.EFETIVAR', 'Financeiro - Pagamentos - Efetivar', 'O', NULL, NULL, 4, TRUE, NOW(), NOW()),
('FINANCEIRO.PAGAMENTO.CANCELAR', 'Financeiro - Pagamentos - Cancelar', 'O', NULL, NULL, 5, TRUE, NOW(), NOW()),
('FINANCEIRO.PAGAMENTO.PARCIAL', 'Financeiro - Pagamentos - Valor Parcial', 'O', NULL, NULL, 6, TRUE, NOW(), NOW()),
('FINANCEIRO.PAGAMENTO.BANCO', 'Financeiro - Pagamentos - Definir Banco', 'O', NULL, NULL, 7, TRUE, NOW(), NOW()),
('FINANCEIRO.PAGAMENTO.ALTERAR', 'Financeiro - Pagamentos - Alterar Ajustes', 'O', NULL, NULL, 8, TRUE, NOW(), NOW())
ON DUPLICATE KEY UPDATE descricao = VALUES(descricao);

-- ============================================================================
-- 2. PROGRAMAS DE CAIXA (FINANCEIRO.CAIXA.*)
-- ============================================================================

INSERT INTO programs (codigo, descricao, tipo, rota, icone, ordem, ativo, created_at, updated_at) VALUES
('FINANCEIRO.CAIXA.VISUALIZAR', 'Financeiro - Caixa - Visualizar', 'M', '/financeiro/caixa', 'wallet', 10, TRUE, NOW(), NOW()),
('FINANCEIRO.CAIXA.INSERIR', 'Financeiro - Caixa - Inserir Lançamentos', 'O', NULL, NULL, 11, TRUE, NOW(), NOW())
ON DUPLICATE KEY UPDATE descricao = VALUES(descricao);

-- ============================================================================
-- 3. PROGRAMAS DE PRODUTO (PRODUTO.*)
-- ============================================================================

INSERT INTO programs (codigo, descricao, tipo, rota, icone, ordem, ativo, created_at, updated_at) VALUES
('PRODUTO.PRECO_CUSTO', 'Produtos - Visualizar Preço de Custo', 'O', NULL, NULL, 20, TRUE, NOW(), NOW())
ON DUPLICATE KEY UPDATE descricao = VALUES(descricao);

-- ============================================================================
-- 4. PERMISSÕES POR GRUPO - ADMIN (grupo_id = 1) - Acesso total
-- ============================================================================

INSERT INTO user_group_permissions (group_id, program_id, permitido, created_at, updated_at)
SELECT 1, id, TRUE, NOW(), NOW() FROM programs 
WHERE codigo LIKE 'FINANCEIRO.%' OR codigo LIKE 'PRODUTO.%'
ON DUPLICATE KEY UPDATE permitido = TRUE;

-- ============================================================================
-- 5. PERMISSÕES POR GRUPO - DIRETORIA (grupo_id = 2)
-- ============================================================================

INSERT INTO user_group_permissions (group_id, program_id, permitido, created_at, updated_at)
SELECT 2, id, TRUE, NOW(), NOW() FROM programs WHERE codigo = 'FINANCEIRO.PAGAMENTO.VISUALIZAR'
ON DUPLICATE KEY UPDATE permitido = TRUE;

INSERT INTO user_group_permissions (group_id, program_id, permitido, created_at, updated_at)
SELECT 2, id, TRUE, NOW(), NOW() FROM programs WHERE codigo = 'FINANCEIRO.PAGAMENTO.INSERIR'
ON DUPLICATE KEY UPDATE permitido = TRUE;

INSERT INTO user_group_permissions (group_id, program_id, permitido, created_at, updated_at)
SELECT 2, id, TRUE, NOW(), NOW() FROM programs WHERE codigo = 'FINANCEIRO.PAGAMENTO.AUTORIZAR'
ON DUPLICATE KEY UPDATE permitido = TRUE;

INSERT INTO user_group_permissions (group_id, program_id, permitido, created_at, updated_at)
SELECT 2, id, TRUE, NOW(), NOW() FROM programs WHERE codigo = 'FINANCEIRO.PAGAMENTO.PARCIAL'
ON DUPLICATE KEY UPDATE permitido = TRUE;

INSERT INTO user_group_permissions (group_id, program_id, permitido, created_at, updated_at)
SELECT 2, id, TRUE, NOW(), NOW() FROM programs WHERE codigo = 'FINANCEIRO.PAGAMENTO.BANCO'
ON DUPLICATE KEY UPDATE permitido = TRUE;

INSERT INTO user_group_permissions (group_id, program_id, permitido, created_at, updated_at)
SELECT 2, id, TRUE, NOW(), NOW() FROM programs WHERE codigo = 'FINANCEIRO.CAIXA.VISUALIZAR'
ON DUPLICATE KEY UPDATE permitido = TRUE;

INSERT INTO user_group_permissions (group_id, program_id, permitido, created_at, updated_at)
SELECT 2, id, TRUE, NOW(), NOW() FROM programs WHERE codigo = 'PRODUTO.PRECO_CUSTO'
ON DUPLICATE KEY UPDATE permitido = TRUE;

-- ============================================================================
-- 6. PERMISSÕES POR GRUPO - FINANCEIRO (grupo_id = 3)
-- ============================================================================

INSERT INTO user_group_permissions (group_id, program_id, permitido, created_at, updated_at)
SELECT 3, id, TRUE, NOW(), NOW() FROM programs WHERE codigo = 'FINANCEIRO.PAGAMENTO.VISUALIZAR'
ON DUPLICATE KEY UPDATE permitido = TRUE;

INSERT INTO user_group_permissions (group_id, program_id, permitido, created_at, updated_at)
SELECT 3, id, TRUE, NOW(), NOW() FROM programs WHERE codigo = 'FINANCEIRO.PAGAMENTO.INSERIR'
ON DUPLICATE KEY UPDATE permitido = TRUE;

INSERT INTO user_group_permissions (group_id, program_id, permitido, created_at, updated_at)
SELECT 3, id, TRUE, NOW(), NOW() FROM programs WHERE codigo = 'FINANCEIRO.PAGAMENTO.ALTERAR'
ON DUPLICATE KEY UPDATE permitido = TRUE;

INSERT INTO user_group_permissions (group_id, program_id, permitido, created_at, updated_at)
SELECT 3, id, TRUE, NOW(), NOW() FROM programs WHERE codigo = 'FINANCEIRO.PAGAMENTO.EFETIVAR'
ON DUPLICATE KEY UPDATE permitido = TRUE;

INSERT INTO user_group_permissions (group_id, program_id, permitido, created_at, updated_at)
SELECT 3, id, TRUE, NOW(), NOW() FROM programs WHERE codigo = 'FINANCEIRO.PAGAMENTO.CANCELAR'
ON DUPLICATE KEY UPDATE permitido = TRUE;

INSERT INTO user_group_permissions (group_id, program_id, permitido, created_at, updated_at)
SELECT 3, id, TRUE, NOW(), NOW() FROM programs WHERE codigo = 'FINANCEIRO.CAIXA.VISUALIZAR'
ON DUPLICATE KEY UPDATE permitido = TRUE;

INSERT INTO user_group_permissions (group_id, program_id, permitido, created_at, updated_at)
SELECT 3, id, TRUE, NOW(), NOW() FROM programs WHERE codigo = 'FINANCEIRO.CAIXA.INSERIR'
ON DUPLICATE KEY UPDATE permitido = TRUE;

-- ============================================================================
-- 7. PERMISSÕES POR GRUPO - VENDAS (grupo_id = 4)
-- ============================================================================

INSERT INTO user_group_permissions (group_id, program_id, permitido, created_at, updated_at)
SELECT 4, id, TRUE, NOW(), NOW() FROM programs WHERE codigo = 'FINANCEIRO.PAGAMENTO.VISUALIZAR'
ON DUPLICATE KEY UPDATE permitido = TRUE;

-- NÃO incluir PRECO_CUSTO para vendedores!

-- ============================================================================
-- 8. PERMISSÕES POR GRUPO - COMPRAS (grupo_id = 5)
-- ============================================================================

INSERT INTO user_group_permissions (group_id, program_id, permitido, created_at, updated_at)
SELECT 5, id, TRUE, NOW(), NOW() FROM programs WHERE codigo = 'FINANCEIRO.PAGAMENTO.VISUALIZAR'
ON DUPLICATE KEY UPDATE permitido = TRUE;

INSERT INTO user_group_permissions (group_id, program_id, permitido, created_at, updated_at)
SELECT 5, id, TRUE, NOW(), NOW() FROM programs WHERE codigo = 'FINANCEIRO.PAGAMENTO.INSERIR'
ON DUPLICATE KEY UPDATE permitido = TRUE;

INSERT INTO user_group_permissions (group_id, program_id, permitido, created_at, updated_at)
SELECT 5, id, TRUE, NOW(), NOW() FROM programs WHERE codigo = 'PRODUTO.PRECO_CUSTO'
ON DUPLICATE KEY UPDATE permitido = TRUE;

COMMIT;

-- ============================================================================
-- VERIFICAÇÃO: Matrix de Permissões Resultante
-- ============================================================================

SELECT 
    g.nome AS grupo,
    p.codigo AS programa,
    CASE WHEN ugp.permitido = TRUE THEN '✅' ELSE '❌' END AS permitido
FROM user_groups g
CROSS JOIN programs p
LEFT JOIN user_group_permissions ugp ON ugp.group_id = g.id AND ugp.program_id = p.id
WHERE p.codigo LIKE 'FINANCEIRO.%' OR p.codigo LIKE 'PRODUTO.%'
ORDER BY g.id, p.codigo;
