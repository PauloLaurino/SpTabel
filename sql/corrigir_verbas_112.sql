-- =================================================================
-- Script de Correção das Verbas - Versão 112
-- Data: 2026-03-15
-- Objetivo: Corrigir valores das verbas (dividir por 100)
-- =================================================================

-- =================================================================
-- Os valores das verbas estão no formato errado:
-- Exemplo: EMOLUMENTOS: 1080 -> deve ser 10.80
--          FUNREJUS: 395 -> deve ser 3.95
--          ISS: 79 -> deve ser 0.79
--          FUNDEP: 79 -> deve ser 0.79
--          FUNARPEN: 100 -> deve ser 1.00
-- =================================================================

-- Verificar a estrutura atual das verbas
SELECT 
    sel.NUMTIPATO,
    SUBSTRING(sel.JSON, LOCATE('"ListaVerbas":[', sel.JSON), 200) as verbas_sample
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE sel.DATAENVIO >= '2026-02-26' 
  AND s.versao = '112'
  AND sel.JSON LIKE '%ListaVerbas%'
LIMIT 3;

-- =================================================================
-- Correção dos valores das verbas
-- Nota: O sanitizador Java (SeloJsonSanitizerNotas) faz essa correção automaticamente
-- Este script SQL é apenas para verificação e correção manual
-- =================================================================

-- Verificar quantos registros têm ListaVerbas
SELECT 
    COUNT(*) as total_com_lista_verbas
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE sel.DATAENVIO >= '2026-02-26' 
  AND s.versao = '112'
  AND sel.JSON LIKE '%ListaVerbas%';
