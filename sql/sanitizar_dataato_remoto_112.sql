-- =================================================================
-- Script para corrigir placeholder |dataato| no banco remoto
-- Versão 112
-- =================================================================

-- Criar tabela temporária para extrair datas
DROP TEMPORARY TABLE IF EXISTS temp_datas_remoto;

CREATE TEMPORARY TABLE temp_datas_remoto AS
SELECT 
    sel.ID,
    sel.SELO,
    SUBSTRING(
        SUBSTRING(sel.JSON, LOCATE('dataSeloEmitido', sel.JSON) + 18, 19),
        9, 2
    ) as dia,
    SUBSTRING(
        SUBSTRING(sel.JSON, LOCATE('dataSeloEmitido', sel.JSON) + 18, 19),
        6, 2
    ) as mes,
    SUBSTRING(
        SUBSTRING(sel.JSON, LOCATE('dataSeloEmitido', sel.JSON) + 18, 19),
        1, 4
    ) as ano
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112' AND sel.JSON LIKE '%|dataato|%';

-- Verificar dados extraídos
SELECT * FROM temp_datas_remoto LIMIT 10;

-- Atualizar os registros
UPDATE selados sel
INNER JOIN temp_datas_remoto td ON sel.ID = td.ID
SET sel.JSON = REPLACE(sel.JSON, '|dataato|', CONCAT(td.dia, '/', td.mes, '/', td.ano))
WHERE sel.JSON LIKE '%|dataato|%';

-- Verificar resultado
SELECT s.versao, COUNT(*) as total,
    SUM(CASE WHEN sel.JSON LIKE '%|dataato|%' THEN 1 ELSE 0 END) as com_dataato
FROM selados sel 
INNER JOIN selos s ON sel.SELO = s.selo_sel 
WHERE s.versao = '112'
GROUP BY s.versao;

DROP TEMPORARY TABLE IF EXISTS temp_datas_remoto;
