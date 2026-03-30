-- =================================================================
-- Script de Sanitização para Banco Remoto (100.102.13.23)
-- Versão 112
-- =================================================================

SET @token_empresa = 'eyJhbGciOiJIUzUxMiJ9.eyJkb2N1bWVudG8iOiIxMDc1MjI0MTAwMDE0MCJ9.G1SilnGGFWQVPIfFBPS9-oQcYz6aXu3ndnjtvza2cvOcwWBuuY5pLIABrTwWnC3PH4hlOqiqbxX9O7Cl35g2vg';
SET @codigo_oficio = '181196';
SET @ambiente = 'prod';

-- Substituir |ambiente|
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '"|ambiente|"', '"prod"')
WHERE s.versao = '112' AND sel.JSON LIKE '%|ambiente|%';

-- Substituir |codigoEmpresa|
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '"|codigoEmpresa|"', CONCAT('"', @token_empresa, '"'))
WHERE s.versao = '112' AND sel.JSON LIKE '%|codigoEmpresa|%';

-- Substituir |codoficio|
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '|codoficio|', @codigo_oficio)
WHERE s.versao = '112' AND sel.JSON LIKE '%|codoficio|%';

-- Verificar resultado
SELECT s.versao, COUNT(*) as total,
    SUM(CASE WHEN sel.JSON LIKE '%|ambiente|%' THEN 1 ELSE 0 END) as com_ambiente,
    SUM(CASE WHEN sel.JSON LIKE '%|codigoEmpresa|%' THEN 1 ELSE 0 END) as com_codigoEmpresa,
    SUM(CASE WHEN sel.JSON LIKE '%|codoficio|%' THEN 1 ELSE 0 END) as com_codoficio,
    SUM(CASE WHEN sel.JSON LIKE '%|dataato|%' THEN 1 ELSE 0 END) as com_dataato
FROM selados sel 
INNER JOIN selos s ON sel.SELO = s.selo_sel 
WHERE s.versao = '112'
GROUP BY s.versao;
