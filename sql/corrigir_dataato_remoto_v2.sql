-- =================================================================
-- Script para corrigir dataAtoPraticado no banco remoto
-- Versão 112 - O placeholder |dataato| foi substituído incorretamente
-- =================================================================

-- Verificar quantos registros precisam de correção (o formato incorreto)
SELECT COUNT(*) as total_para_corrigir
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112' AND sel.JSON LIKE '%-0/-0/%';

-- Primeiro, substituir o formato malformado "-0/-0/" (sem última aspa)
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '"dataAtoPraticado": "-0/-0/"', '"dataAtoPraticado": "04/03/2026"')
WHERE s.versao = '112' AND sel.JSON LIKE '%-0/-0/%';

-- Verificar resultado
SELECT s.versao, 
    COUNT(*) as total,
    SUM(CASE WHEN sel.JSON LIKE '%-0/-0/%' THEN 1 ELSE 0 END) as com_erro
FROM selados sel 
INNER JOIN selos s ON sel.SELO = s.selo_sel 
WHERE s.versao = '112'
GROUP BY s.versao;

-- Verificar alguns exemplos
SELECT sel.ID, sel.SELO, 
    SUBSTRING(sel.JSON, LOCATE('dataAtoPraticado', sel.JSON), 25) as dataAtoPraticado
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112'
LIMIT 5;
