-- =================================================================
-- Script para limpar o JSON (remover \n e ajustar seloRetificado)
-- Selo: SFTN1.cG2Rb.Mwfwe-GR3ZM.1122q
-- =================================================================

-- Remover \n literais (MySQL stored como \\n)
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel 
SET sel.JSON = REPLACE(REPLACE(REPLACE(sel.JSON, '\\n', ''), '\\r', ''), '    ', '')
WHERE s.selo_sel = 'SFTN1.cG2Rb.Mwfwe-GR3ZM.1122q';

-- Verificar se o JSON é válido
SELECT JSON_VALID(sel.JSON) as json_valido FROM selados sel 
INNER JOIN selos s ON sel.SELO = s.selo_sel 
WHERE s.selo_sel = 'SFTN1.cG2Rb.Mwfwe-GR3ZM.1122q';

-- Ver o JSON final
SELECT sel.JSON FROM selados sel 
INNER JOIN selos s ON sel.SELO = s.selo_sel 
WHERE s.selo_sel = 'SFTN1.cG2Rb.Mwfwe-GR3ZM.1122q';
