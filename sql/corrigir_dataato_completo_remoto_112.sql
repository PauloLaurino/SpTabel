-- =================================================================
-- Script para corrigir todos os casos de dataAtoPraticado no banco remoto
-- Versão 112 - Corrigir todos os formatos incorretos
-- =================================================================

-- Corrigir "-1/-0/"202" -> "04/03/2026"
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '"dataAtoPraticado": "-1/-0/"202"', '"dataAtoPraticado": "04/03/2026"')
WHERE s.versao = '112' AND sel.JSON LIKE '%-1/-0/%';

-- Corrigir "04/03/2026"202" -> "04/03/2026"
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = REPLACE(sel.JSON, '"dataAtoPraticado": "04/03/2026"202"', '"dataAtoPraticado": "04/03/2026"')
WHERE s.versao = '112' AND sel.JSON LIKE '%04/03/2026"202%';

-- Verificar resultado final
SELECT sel.ID, sel.SELO, SUBSTRING(sel.JSON, LOCATE('dataAtoPraticado', sel.JSON), 25) as dataAto
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112'
GROUP BY SUBSTRING(sel.JSON, LOCATE('dataAtoPraticado', sel.JSON), 25);
