-- =================================================================
-- Script para copiar JSON para JSON12 antes de continuar a sanitização
-- Versão 112 - Banco Remoto
-- =================================================================

-- Copiar o JSON atual para JSON12 (como backup)
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = sel.JSON
WHERE s.versao = '112';

-- Verificar resultado
SELECT sel.ID, sel.SELO, LENGTH(sel.JSON) as json_len, LENGTH(sel.JSON12) as json12_len
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112'
LIMIT 5;
