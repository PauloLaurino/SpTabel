-- =================================================================
-- Script para corrigir valores das verbas no banco remoto
-- Versão 112 - Dividir valores por 100
-- =================================================================

-- Verificar quantos registros têm verbas com valores altos (> 1000)
SELECT COUNT(*) as total_para_corrigir
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112' AND sel.JSON LIKE '%"emolumentos": 1%';

-- Corrigir emolumentos (2414 -> 24.14)
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '"emolumentos": 2414', '"emolumentos": 24.14')
WHERE s.versao = '112';

-- Corrigir funrejus (301 -> 3.01)
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '"funrejus": 301', '"funrejus": 3.01')
WHERE s.versao = '112';

-- Corrigir iss (36 -> 0.36)
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '"iss": 36', '"iss": 0.36')
WHERE s.versao = '112';

-- Corrigir fundep (60 -> 0.60)
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '"fundep": 60', '"fundep": 0.60')
WHERE s.versao = '112';

-- Corrigir funarpen (100 -> 1.00)
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '"funarpen": 100', '"funarpen": 1.00')
WHERE s.versao = '112';

-- Verificar resultado
SELECT sel.ID, sel.SELO, SUBSTRING(sel.JSON, LOCATE('"verbas"', sel.JSON), 100) as verbas
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112'
LIMIT 3;
