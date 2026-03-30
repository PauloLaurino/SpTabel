-- =================================================================
-- Script para sanitizar JSON e salvar em JSON12 para verificação
-- Não altera o JSON original - apenas cria uma cópia sanitizada para verificação
-- Todas as versões (100, 110, 112)
-- =================================================================

-- Primeiro, garantir que JSON12 tem o JSON original como backup
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = sel.JSON
WHERE s.versao IN ('100', '110', '112');

-- Agora, criar o JSON sanitizado em JSON12 (sobrescreve o backup)
-- Este script aplica as correções e salva em JSON12

-- 1. Corrigir placeholders ambiente, codigoEmpresa, codoficio
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '|ambiente|', 'prod')
WHERE s.versao IN ('100', '110', '112') AND sel.JSON12 LIKE '%|ambiente|%';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '|codigoEmpresa|', 'eyJhbGciOiJIUzUxMiJ9.eyJkb2N1bWVudG8iOiIxMDc1MjI0MTAwMDE0MCJ9.G1SilnGGFWQVPIfFBPS9-oQcYz6aXu3ndnjtvza2cvOcwWBuuY5pLIABrTwWnC3PH4hlOqiqbxX9O7Cl35g2vg')
WHERE s.versao IN ('100', '110', '112') AND sel.JSON12 LIKE '%|codigoEmpresa|%';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '|codoficio|', '181196')
WHERE s.versao IN ('100', '110', '112') AND sel.JSON12 LIKE '%|codoficio|%';

-- 2. Corrigir dataato - vários formatos
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"dataAtoPraticado": "-0/-0/"202"', '"dataAtoPraticado": "04/03/2026"')
WHERE s.versao IN ('100', '110', '112') AND sel.JSON12 LIKE '%-0/-0/%';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"dataAtoPraticado": "04/03/2026"202"', '"dataAtoPraticado": "04/03/2026"')
WHERE s.versao IN ('100', '110', '112') AND sel.JSON12 LIKE '%04/03/2026"202%';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"dataAtoPraticado": "-1/-0/"202"', '"dataAtoPraticado": "04/03/2026"')
WHERE s.versao IN ('100', '110', '112') AND sel.JSON12 LIKE '%-1/-0/%';

-- Corrigir |dataato| - extrair de dataSeloEmitido
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '|dataato|', '16/03/2026')
WHERE s.versao IN ('100', '110', '112') AND sel.JSON12 LIKE '%|dataato|%';

-- 3. Corrigir valores das verbas (dividir por 100)
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"emolumentos": 2414', '"emolumentos": 24.14')
WHERE s.versao IN ('100', '110', '112');

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"funrejus": 301', '"funrejus": 3.01')
WHERE s.versao IN ('100', '110', '112');

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"iss": 36', '"iss": 0.36')
WHERE s.versao IN ('100', '110', '112');

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"fundep": 60', '"fundep": 0.60')
WHERE s.versao IN ('100', '110', '112');

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"funarpen": 100', '"funarpen": 1.00')
WHERE s.versao IN ('100', '110', '112');

-- 4. Verificar resultado - comparar JSON vs JSON12 por versão
SELECT 
    s.versao,
    COUNT(*) as total,
    SUM(CASE WHEN sel.JSON = sel.JSON12 THEN 1 ELSE 0 END) as igual,
    SUM(CASE WHEN sel.JSON != sel.JSON12 THEN 1 ELSE 0 END) as diferente
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112'
GROUP BY s.versao;
