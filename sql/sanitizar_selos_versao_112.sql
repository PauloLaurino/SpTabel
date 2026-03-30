-- =================================================================
-- Script de Sanitização dos Selos versão 112
-- Data: 2026-03-15
-- Objetivo: Substituir placeholders nos registros da versão 112
-- =================================================================

-- Parâmetros para substituição
SET @token_empresa = 'eyJhbGciOiJIUzUxMiJ9.eyJkb2N1bWVudG8iOiIxMDc1MjI0MTAwMDE0MCJ9.G1SilnGGFWQVPIfFBPS9-oQcYz6aXu3ndnjtvza2cvOcwWBuuY5pLIABrTwWnC3PH4hlOqiqbxX9O7Cl35g2vg';
SET @codigo_oficio = '181196';
SET @ambiente = 'prod';

-- Verificar registros antes da atualização
SELECT 
    'Antes da sanitização' as fase,
    s.versao,
    COUNT(*) as total,
    SUM(CASE WHEN sel.JSON LIKE '%|ambiente|%' THEN 1 ELSE 0 END) as com_placeholders
FROM selados sel 
INNER JOIN selos s ON sel.SELO = s.selo_sel 
WHERE sel.DATAENVIO >= '2026-02-26' AND s.versao = '112'
GROUP BY s.versao;

-- =================================================================
-- Atualização 1: Substituir |ambiente| por 'prod'
-- =================================================================
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '"|ambiente|"', '"prod"')
WHERE sel.DATAENVIO >= '2026-02-26' 
  AND s.versao = '112' 
  AND sel.JSON LIKE '%|ambiente|%';

-- =================================================================
-- Atualização 2: Substituir |codigoEmpresa| pelo token
-- =================================================================
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '"|codigoEmpresa|"', CONCAT('"', @token_empresa, '"'))
WHERE sel.DATAENVIO >= '2026-02-26' 
  AND s.versao = '112' 
  AND sel.JSON LIKE '%|codigoEmpresa|%';

-- =================================================================
-- Atualização 3: Substituir |codoficio| pelo código do cartório
-- =================================================================
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '|codoficio|', @codigo_oficio)
WHERE sel.DATAENVIO >= '2026-02-26' 
  AND s.versao = '112' 
  AND sel.JSON LIKE '%|codoficio|%';

-- =================================================================
-- Atualização 4: Substituir |dataato| pela data do ato (extraída de dataAtoPraticado)
-- =================================================================
-- Primeiro, verificamos quais registros ainda têm |dataato|
-- A data precisa ser convertida do formato ISO (2026-03-04T15:21:52.735) para DD/MM/YYYY

-- Verificar quantos registros ainda têm placeholders |dataato|
SELECT 
    'Registros com |dataato| restantes' as fase,
    COUNT(*) as total
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE sel.DATAENVIO >= '2026-02-26' 
  AND s.versao = '112' 
  AND sel.JSON LIKE '%|dataato|%';

-- Verificar registros depois da atualização
SELECT 
    'Depois da sanitização' as fase,
    s.versao,
    COUNT(*) as total,
    SUM(CASE WHEN sel.JSON LIKE '%|ambiente|%' THEN 1 ELSE 0 END) as com_placeholders_ambiente,
    SUM(CASE WHEN sel.JSON LIKE '%|codigoEmpresa|%' THEN 1 ELSE 0 END) as com_placeholders_codigoEmpresa,
    SUM(CASE WHEN sel.JSON LIKE '%|codoficio|%' THEN 1 ELSE 0 END) as com_placeholders_codoficio,
    SUM(CASE WHEN sel.JSON LIKE '%|dataato|%' THEN 1 ELSE 0 END) as com_placeholders_dataato
FROM selados sel 
INNER JOIN selos s ON sel.SELO = s.selo_sel 
WHERE sel.DATAENVIO >= '2026-02-26' AND s.versao = '112'
GROUP BY s.versao;

-- =================================================================
-- Observações:
-- 1. O placeholder |dataato| precisa ser substituído manualmente ou via procedure
-- 2. O código do cartório (181196) foi extraído das propriedades do JSON
-- 3. O token foi obtido da tabela parametros
-- =================================================================
