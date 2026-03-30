-- =================================================================
-- Script de Correção Final dos Selos versão 112
-- Data: 2026-03-15
-- Objetivo: Corrigir valores de verbas (dividir por 100) e documentar objetos faltantes
-- =================================================================

-- =================================================================
-- 1. CORREÇÃO DE VERBAS
-- Os valores de verbas estão em formato incorreto (ex: 1080 → 10.80)
-- =================================================================

-- Verificar quantos registros precisam de correção
SELECT 
    'Verbas a corrigir' as analise,
    COUNT(*) as total,
    SUM(CASE WHEN JSON LIKE '%\"ValorVerba\":1080%' THEN 1 ELSE 0 END) as emolumentos_1080,
    SUM(CASE WHEN JSON LIKE '%\"ValorVerba\":270%' THEN 1 ELSE 0 END) as funrejus_270,
    SUM(CASE WHEN JSON LIKE '%\"ValorVerba\":395%' THEN 1 ELSE 0 END) as iss_395,
    SUM(CASE WHEN JSON LIKE '%\"ValorVerba\":302%' THEN 1 ELSE 0 END) as funrejus_302,
    SUM(CASE WHEN JSON LIKE '%\"ValorVerba\":60%' THEN 1 ELSE 0 END) as fundep_60,
    SUM(CASE WHEN JSON LIKE '%\"ValorVerba\":100%' THEN 1 ELSE 0 END) as funarpen_100
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112';

-- =================================================================
-- 2. ANÁLISE DE OBJETOS FALTANTES
-- =================================================================

-- Verificar registros com tipo 430 (Traslado) - precisam de livro, folha, termo
SELECT 
    'Tipo 430 - Traslado' as tipo,
    COUNT(*) as total,
    SUM(CASE WHEN JSON LIKE '%livro%' THEN 1 ELSE 0 END) as tem_livro,
    SUM(CASE WHEN JSON LIKE '%folha%' THEN 1 ELSE 0 END) as tem_folha,
    SUM(CASE WHEN JSON LIKE '%termo%' THEN 1 ELSE 0 END) as tem_termo
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112' AND s.codtipoato = 430;

-- Verificar registros com tipo 432 (Apostilamento) - precisam de solicitanteAto, apostila
SELECT 
    'Tipo 432 - Apostilamento' as tipo,
    COUNT(*) as total,
    SUM(CASE WHEN JSON LIKE '%solicitanteAto%' THEN 1 ELSE 0 END) as tem_solicitanteAto,
    SUM(CASE WHEN JSON LIKE '%apostila%' THEN 1 ELSE 0 END) as tem_apostila
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112' AND s.codtipoato = 432;

-- =================================================================
-- 3. RESUMO FINAL DOS REGISTROS VERSÃO 112
-- =================================================================
SELECT 
    s.versao,
    COUNT(*) as total_registros,
    SUM(CASE WHEN sel.JSON LIKE '%|ambiente|%' OR sel.JSON LIKE '%|codigoEmpresa|%' OR sel.JSON LIKE '%|codoficio|%' OR sel.JSON LIKE '%|dataato|%' THEN 1 ELSE 0 END) as com_placeholders,
    SUM(CASE WHEN sel.JSON LIKE '%ListaVerbas%' THEN 1 ELSE 0 END) as com_listaVerbas,
    SUM(CASE WHEN sel.JSON LIKE '%ListaPropriedades%' THEN 1 ELSE 0 END) as com_listaPropriedades
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112'
GROUP BY s.versao;

-- =================================================================
-- Observações Finais:
-- 1. Todos os placeholders foram substituídos
-- 2. Os valores de verbas precisam ser corrigidos (dividir por 100)
-- 3. ListaPropriedades precisa ser convertida para objetos separados
-- 4. ListaVerbas precisa ser convertida para objeto 'verbas'
-- 5. Para tipos 430 e 432, os campos livro, folha, termo, apostila, solicitanteAto
--    precisam ser adicionados a partir de outras tabelas do banco
-- =================================================================
