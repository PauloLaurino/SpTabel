-- =================================================================
-- Script para corrigir formato completo dos registros com numeroTipoAto
-- Versão 112 - 9 registros
-- =================================================================

-- Verificar registros atuais
SELECT sel.ID, sel.SELO, LENGTH(sel.JSON) as tamanho
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112' AND sel.JSON LIKE '%numeroTipoAto%'
LIMIT 5;

-- Esses registros precisam ser reconstruídos completamente via Java
-- O sanitizador Java já faz isso automaticamente
-- Por enquanto, vamos marcar para processamento posterior
