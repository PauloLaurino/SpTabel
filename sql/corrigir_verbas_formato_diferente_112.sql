-- =================================================================
-- Script para corrigir valores das verbas nos registros com formato diferente
-- Versão 112 - 9 registros
-- =================================================================

-- Corrigir EMOLUMENTOS (1246 -> 12.46)
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '"EMOLUMENTOS",1246', '"EMOLUMENTOS",12.46')
WHERE s.versao = '112' AND sel.JSON LIKE '%numeroTipoAto%';

-- Corrigir FUNREJUS (374 -> 3.74)
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '"FUNREJUS",374', '"FUNREJUS",3.74')
WHERE s.versao = '112' AND sel.JSON LIKE '%numeroTipoAto%';

-- Corrigir ISS (45 -> 0.45)
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '"ISS",45', '"ISS",0.45')
WHERE s.versao = '112' AND sel.JSON LIKE '%numeroTipoAto%';

-- Corrigir FUNDEP (75 -> 0.75)
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '"FUNDEP",75', '"FUNDEP",0.75')
WHERE s.versao = '112' AND sel.JSON LIKE '%numeroTipoAto%';

-- Corrigir FUNARPEN (100 -> 1.00) - já está correto
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '"FUNARPEN",100', '"FUNARPEN",1.00')
WHERE s.versao = '112' AND sel.JSON LIKE '%numeroTipoAto%';

-- Verificar resultado
SELECT sel.ID, sel.SELO, SUBSTRING(sel.JSON, LOCATE('ListaVerbas', sel.JSON), 100) as verbas
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112' AND sel.JSON LIKE '%numeroTipoAto%'
LIMIT 3;
