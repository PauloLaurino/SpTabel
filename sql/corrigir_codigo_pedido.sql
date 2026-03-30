s-- =================================================================
-- Script para corrigir codigoPedido (remover ponto decimal) e propriedades
-- Selo: SFTN1.cG2Rb.Mwfwe-GR3ZM.1122q
-- =================================================================

-- 1. Corrigir codigoPedido: 94487.4 -> 944874
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '"codigoPedido": 94487.4', '"codigoPedido": 944874')
WHERE s.selo_sel = 'SFTN1.cG2Rb.Mwfwe-GR3ZM.1122q';

-- Verificar resultado
SELECT sel.JSON FROM selados sel 
INNER JOIN selos s ON sel.SELO = s.selo_sel 
WHERE s.selo_sel = 'SFTN1.cG2Rb.Mwfwe-GR3ZM.1122q';
