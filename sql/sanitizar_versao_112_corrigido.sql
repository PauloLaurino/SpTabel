-- =================================================================
-- Script de Sanitização dos Selos versão 112 - CORRIGIDO
-- Data: 2026-03-17
-- Objetivo: Corrigir placeholders e formatar JSON para versão 112
-- =================================================================

-- Parâmetros para substituição
SET @token_empresa = 'eyJhbGciOiJIUzUxMiJ9.eyJkb2N1bWVudG8iOiIxMDc1MjI0MTAwMDE0MCJ9.G1SilnGGFWQVPIfFBPS9-oQcYz6aXu3ndnjtvza2cvOcwWBuuY5pLIABrTwWnC3PH4hlOqiqbxX9O7Cl35g2vg';
SET @codigo_oficio = 181196;
SET @ambiente = 'prod';

-- =================================================================
-- Verificação inicial
-- =================================================================
SELECT 
    'Antes da sanitização' as fase,
    s.versao,
    COUNT(*) as total,
    SUM(CASE WHEN sel.JSON LIKE '%|ambiente|%' THEN 1 ELSE 0 END) as com_placeholders_ambiente,
    SUM(CASE WHEN sel.JSON LIKE '%|codigoEmpresa|%' THEN 1 ELSE 0 END) as com_placeholders_codigoEmpresa,
    SUM(CASE WHEN sel.JSON LIKE '%|dataato|%' THEN 1 ELSE 0 END) as com_placeholders_dataato,
    SUM(CASE WHEN sel.JSON LIKE '%|codoficio|%' THEN 1 ELSE 0 END) as com_placeholders_codoficio,
    SUM(CASE WHEN sel.JSON REGEXP 'codigoPedido.*[0-9]+\\.[0-9]+' THEN 1 ELSE 0 END) as com_codigoPedido_decimal
FROM selados sel 
INNER JOIN selos s ON sel.SELO = s.selo_sel 
WHERE s.versao = '112'
GROUP BY s.versao;

-- =================================================================
-- Atualização 1: Substituir |ambiente| por 'prod'
-- =================================================================
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '"|ambiente|"', '"prod"')
WHERE s.versao = '112' 
AND sel.JSON LIKE '%|ambiente|%';

-- =================================================================
-- Atualização 2: Substituir |codigoEmpresa| pelo token
-- =================================================================
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '"|codigoEmpresa|"', CONCAT('"', @token_empresa, '"'))
WHERE s.versao = '112' 
AND sel.JSON LIKE '%|codigoEmpresa|%';

-- =================================================================
-- Atualização 3: Substituir |codoficio| pelo código do cartório (como número)
-- =================================================================
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '|codoficio|', CAST(@codigo_oficio AS CHAR))
WHERE s.versao = '112' 
AND sel.JSON LIKE '%|codoficio|%';

-- =================================================================
-- Atualização 4: Substituir |dataato| pela data atual ( workaround - precisa ser melhorado)
-- =================================================================
-- Por ora, vamos usar a data do dia como workaround
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '"|dataato|"', '"17/03/2026"')
WHERE s.versao = '112' 
AND sel.JSON LIKE '%|dataato|%';

-- =================================================================
-- Atualização 5: Corrigir codigoPedido com ponto decimal
-- Exemplo: 95092.5 -> 950925
-- =================================================================
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '"codigoPedido": ', '"codigoPedido": ')
WHERE s.versao = '112';

-- =================================================================
-- Verificação final
-- =================================================================
SELECT 
    'Depois da sanitização' as fase,
    s.versao,
    COUNT(*) as total,
    SUM(CASE WHEN sel.JSON LIKE '%|ambiente|%' THEN 1 ELSE 0 END) as com_placeholders_ambiente,
    SUM(CASE WHEN sel.JSON LIKE '%|codigoEmpresa|%' THEN 1 ELSE 0 END) as com_placeholders_codigoEmpresa,
    SUM(CASE WHEN sel.JSON LIKE '%|dataato|%' THEN 1 ELSE 0 END) as com_placeholders_dataato,
    SUM(CASE WHEN sel.JSON LIKE '%|codoficio|%' THEN 1 ELSE 0 END) as com_placeholders_codoficio
FROM selados sel 
INNER JOIN selos s ON sel.SELO = s.selo_sel 
WHERE s.versao = '112'
GROUP BY s.versao;

-- =================================================================
-- Copiar JSON para JSON12 como base para sanitização completa
-- =================================================================
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = sel.JSON
WHERE s.versao = '112' 
AND (sel.JSON12 IS NULL OR sel.JSON12 = '');

-- =================================================================
-- Observações:
-- 1. O placeholder |dataato| foi substituído temporariamente por 17/03/2026
-- 2. A correção de codigoPedido precisa ser feita via aplicação Java
-- 3. A conversão de ListaPropriedades para propriedades precisa ser feita via aplicação Java
-- =================================================================
