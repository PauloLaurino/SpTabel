-- =================================================================
-- Script para corrigir registros com formato diferente (numeroTipoAto)
-- Versão 112 - 9 registros
-- =================================================================

-- Verificar quantos registros precisam de correção
SELECT COUNT(*) as total_para_corrigir
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112' AND sel.JSON LIKE '%numeroTipoAto%';

-- Corrigir os registros com formato diferente
-- 1. Adicionar ambiente, codigoEmpresa, codigoOficio
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON = CONCAT(
  '{"ambiente": "prod",',
  '"documentoResponsavel": "0",',
  '"codigoEmpresa": "eyJhbGciOiJIUzUxMiJ9.eyJkb2N1bWVudG8iOiIxMDc1MjI0MTAwMDE0MCJ9.G1SilnGGFWQVPIfFBPS9-oQcYz6aXu3ndnjtvza2cvOcwWBuuY5pLIABrTwWnC3PH4hlOqiqbxX9O7Cl35g2vg",',
  '"codigoOficio": 181196,',
  REPLACE(sel.JSON, '{"numeroTipoAto":', '"selo":{"codigoTipoAto":')
)
WHERE s.versao = '112' AND sel.JSON LIKE '%numeroTipoAto%';

-- Verificar resultado
SELECT sel.ID, sel.SELO, SUBSTRING(sel.JSON, 1, 200) as json_inicio
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112' AND sel.JSON LIKE '%numeroTipoAto%'
LIMIT 3;
