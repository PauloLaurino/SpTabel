-- =================================================================
-- Script para substituir placeholder |dataato| nos registros versão 112
-- Data: 2026-03-15
-- Objetivo: Substituir |dataato| pela data de dataSeloEmitido
-- =================================================================

-- Verificar a estrutura do JSON para extrair a data
SELECT 
    sel.ID,
    sel.SELO,
    SUBSTRING(sel.JSON, LOCATE('dataSeloEmitido', sel.JSON), 30) as dataSeloEmitido_exemplo
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112' 
  AND sel.JSON LIKE '%|dataato|%'
LIMIT 3;

-- =================================================================
-- Estratégia: Usar SUBSTRING/LOCATE para extrair a data do campo dataSeloEmitido
-- O formato é: "dataSeloEmitido":"2026-03-04T15:21:52.735"
-- Precisamos converter para DD/MM/YYYY
-- =================================================================

-- =================================================================
-- Atualização: Substituir |dataato| pela data do ato
-- A data está no campo dataSeloEmitido no formato: 2026-03-04T15:21:52.735
-- Precisamos extrair apenas a parte da data (2026-03-04) e converter para DD/MM/YYYY
-- =================================================================

-- Verificar quantos registros serão afetados
SELECT 
    'Antes' as fase,
    COUNT(*) as total,
    SUM(CASE WHEN JSON LIKE '%|dataato|%' THEN 1 ELSE 0 END) as com_dataato
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112';

-- =================================================================
-- Atualizar os registros usando REPLACE com a data do campo dataSeloEmitido
-- =================================================================
-- Como não podemos extrair dinamicamente de um campo JSON em um UPDATE simples,
-- vamos usar uma abordagem alternativa: substituir |dataato| por uma data fixa
-- ou usar uma subconsulta com função自定义

-- Primeiro, vamos verificar se existe dataSeloEmitido em todos os registros
SELECT 
    sel.ID,
    sel.SELO,
    CASE 
        WHEN JSON LIKE '%dataSeloEmitido":"2026%' THEN '2026'
        WHEN JSON LIKE '%dataSeloEmitido":"2025%' THEN '2025'
        ELSE 'OUTRO'
    END as ano_encontrado,
    COUNT(*) as total
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112' AND sel.JSON LIKE '%|dataato|%'
GROUP BY ano_encontrado;

-- =================================================================
-- Criar uma tabela temporária com as datas extraídas
-- =================================================================
DROP TEMPORARY TABLE IF EXISTS temp_datas_ver112;

CREATE TEMPORARY TABLE temp_datas_ver112 AS
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
SELECT * FROM temp_datas_ver112 LIMIT 10;

-- =================================================================
-- Agora atualizar usando JOIN com a tabela temporária
-- =================================================================
UPDATE selados sel
INNER JOIN temp_datas_ver112 td ON sel.ID = td.ID
SET sel.JSON = REPLACE(sel.JSON, '|dataato|', CONCAT(td.dia, '/', td.mes, '/', td.ano))
WHERE sel.JSON LIKE '%|dataato|%';

-- Verificar resultado
SELECT 
    'Depois' as fase,
    COUNT(*) as total,
    SUM(CASE WHEN JSON LIKE '%|dataato|%' THEN 1 ELSE 0 END) as com_dataato
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112';

-- Limpar
DROP TEMPORARY TABLE IF EXISTS temp_datas_ver112;
