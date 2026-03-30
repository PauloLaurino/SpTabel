-- Script de sanitização completo para versão 112 (DTVERSAO_FUNARPEN = 2026-03-02)
-- Executado em 18/03/2026

-- Mapping NUMTIPATO: 
-- 403→455, 402→455, 404→455, 418→459, 430→459
-- 432→454, 431→454
-- 422→452, 423→452
-- 425→453
-- 408→459, 409→459

-- ============================================================
-- PARTE 1: Atualizar NUMTIPATO na tabela selados
-- ============================================================

-- ---------- BANCO 100.75.153.127 ----------
-- Usar JOIN com parametros para pegar DTVERSAO_FUNARPEN dinamicamente

UPDATE selados sl 
INNER JOIN parametros ON parametros.CODIGO_PAR = 1
SET sl.NUMTIPATO = '455' 
WHERE sl.NUMTIPATO = '403' AND sl.DATAENVIO >= parametros.DTVERSAO_FUNARPEN;

UPDATE selados sl 
INNER JOIN parametros ON parametros.CODIGO_PAR = 1
SET sl.NUMTIPATO = '455' 
WHERE sl.NUMTIPATO = '402' AND sl.DATAENVIO >= parametros.DTVERSAO_FUNARPEN;

UPDATE selados sl 
INNER JOIN parametros ON parametros.CODIGO_PAR = 1
SET sl.NUMTIPATO = '454' 
WHERE sl.NUMTIPATO = '432' AND sl.DATAENVIO >= parametros.DTVERSAO_FUNARPEN;

UPDATE selados sl 
INNER JOIN parametros ON parametros.CODIGO_PAR = 1
SET sl.NUMTIPATO = '459' 
WHERE sl.NUMTIPATO = '408' AND sl.DATAENVIO >= parametros.DTVERSAO_FUNARPEN;

UPDATE selados sl 
INNER JOIN parametros ON parametros.CODIGO_PAR = 1
SET sl.NUMTIPATO = '452' 
WHERE sl.NUMTIPATO = '422' AND sl.DATAENVIO >= parametros.DTVERSAO_FUNARPEN;

UPDATE selados sl 
INNER JOIN parametros ON parametros.CODIGO_PAR = 1
SET sl.NUMTIPATO = '453' 
WHERE sl.NUMTIPATO = '425' AND sl.DATAENVIO >= parametros.DTVERSAO_FUNARPEN;

UPDATE selados sl 
INNER JOIN parametros ON parametros.CODIGO_PAR = 1
SET sl.NUMTIPATO = '454' 
WHERE sl.NUMTIPATO = '431' AND sl.DATAENVIO >= parametros.DTVERSAO_FUNARPEN;

UPDATE selados sl 
INNER JOIN parametros ON parametros.CODIGO_PAR = 1
SET sl.NUMTIPATO = '459' 
WHERE sl.NUMTIPATO = '409' AND sl.DATAENVIO >= parametros.DTVERSAO_FUNARPEN;

UPDATE selados sl 
INNER JOIN parametros ON parametros.CODIGO_PAR = 1
SET sl.NUMTIPATO = '455' 
WHERE sl.NUMTIPATO = '404' AND sl.DATAENVIO >= parametros.DTVERSAO_FUNARPEN;

UPDATE selados sl 
INNER JOIN parametros ON parametros.CODIGO_PAR = 1
SET sl.NUMTIPATO = '452' 
WHERE sl.NUMTIPATO = '423' AND sl.DATAENVIO >= parametros.DTVERSAO_FUNARPEN;

UPDATE selados sl 
INNER JOIN parametros ON parametros.CODIGO_PAR = 1
SET sl.NUMTIPATO = '459' 
WHERE sl.NUMTIPATO = '430' AND sl.DATAENVIO >= parametros.DTVERSAO_FUNARPEN;

-- Resultado: 455=400, 459=22, 454=19, 452=4, 453=2

-- ---------- BANCO 100.102.13.23 ----------

UPDATE selados sl 
INNER JOIN parametros ON parametros.CODIGO_PAR = 1
SET sl.NUMTIPATO = '455' 
WHERE sl.NUMTIPATO = '403' AND sl.DATAENVIO >= parametros.DTVERSAO_FUNARPEN;

UPDATE selados sl 
INNER JOIN parametros ON parametros.CODIGO_PAR = 1
SET sl.NUMTIPATO = '455' 
WHERE sl.NUMTIPATO = '402' AND sl.DATAENVIO >= parametros.DTVERSAO_FUNARPEN;

UPDATE selados sl 
INNER JOIN parametros ON parametros.CODIGO_PAR = 1
SET sl.NUMTIPATO = '454' 
WHERE sl.NUMTIPATO = '432' AND sl.DATAENVIO >= parametros.DTVERSAO_FUNARPEN;

UPDATE selados sl 
INNER JOIN parametros ON parametros.CODIGO_PAR = 1
SET sl.NUMTIPATO = '459' 
WHERE sl.NUMTIPATO = '408' AND sl.DATAENVIO >= parametros.DTVERSAO_FUNARPEN;

UPDATE selados sl 
INNER JOIN parametros ON parametros.CODIGO_PAR = 1
SET sl.NUMTIPATO = '459' 
WHERE sl.NUMTIPATO = '409' AND sl.DATAENVIO >= parametros.DTVERSAO_FUNARPEN;

UPDATE selados sl 
INNER JOIN parametros ON parametros.CODIGO_PAR = 1
SET sl.NUMTIPATO = '455' 
WHERE sl.NUMTIPATO = '404' AND sl.DATAENVIO >= parametros.DTVERSAO_FUNARPEN;

UPDATE selados sl 
INNER JOIN parametros ON parametros.CODIGO_PAR = 1
SET sl.NUMTIPATO = '459' 
WHERE sl.NUMTIPATO = '418' AND sl.DATAENVIO >= parametros.DTVERSAO_FUNARPEN;

UPDATE selados sl 
INNER JOIN parametros ON parametros.CODIGO_PAR = 1
SET sl.NUMTIPATO = '459' 
WHERE sl.NUMTIPATO = '430' AND sl.DATAENVIO >= parametros.DTVERSAO_FUNARPEN;

-- Resultado: 455=566, 459=32, 454=9


-- ============================================================
-- PARTE 2: Copiar JSON para JSON12 (se necessário)
-- ============================================================

-- ---------- BANCO 100.75.153.127 ----------
UPDATE selados SET JSON12 = JSON 
WHERE DATAENVIO >= '2026-03-02' 
AND JSON IS NOT NULL AND JSON != '' 
AND (JSON12 IS NULL OR JSON12 = ''); 
-- 447 linhas

-- ---------- BANCO 100.102.13.23 ----------
UPDATE selados SET JSON12 = JSON 
WHERE DATAENVIO >= '2026-03-02' 
AND JSON IS NOT NULL AND JSON != '' 
AND (JSON12 IS NULL OR JSON12 = ''); 
-- 27 linhas


-- ============================================================
-- PARTE 3: Atualizar numeroTipoAto no JSON12
-- ============================================================

-- ---------- BANCO 100.75.153.127 ----------
UPDATE selados SET JSON12 = REPLACE(JSON12, '"numeroTipoAto":403', '"numeroTipoAto":455') WHERE DATAENVIO >= '2026-03-02' AND JSON12 LIKE '%numeroTipoAto\":403%';
UPDATE selados SET JSON12 = REPLACE(JSON12, '"numeroTipoAto":402', '"numeroTipoAto":455') WHERE DATAENVIO >= '2026-03-02' AND JSON12 LIKE '%numeroTipoAto\":402%';
UPDATE selados SET JSON12 = REPLACE(JSON12, '"numeroTipoAto":432', '"numeroTipoAto":454') WHERE DATAENVIO >= '2026-03-02' AND JSON12 LIKE '%numeroTipoAto\":432%';
UPDATE selados SET JSON12 = REPLACE(JSON12, '"numeroTipoAto":408', '"numeroTipoAto":459') WHERE DATAENVIO >= '2026-03-02' AND JSON12 LIKE '%numeroTipoAto\":408%';
UPDATE selados SET JSON12 = REPLACE(JSON12, '"numeroTipoAto":422', '"numeroTipoAto":452') WHERE DATAENVIO >= '2026-03-02' AND JSON12 LIKE '%numeroTipoAto\":422%';
UPDATE selados SET JSON12 = REPLACE(JSON12, '"numeroTipoAto":425', '"numeroTipoAto":453') WHERE DATAENVIO >= '2026-03-02' AND JSON12 LIKE '%numeroTipoAto\":425%';
UPDATE selados SET JSON12 = REPLACE(JSON12, '"numeroTipoAto":431', '"numeroTipoAto":454') WHERE DATAENVIO >= '2026-03-02' AND JSON12 LIKE '%numeroTipoAto\":431%';
UPDATE selados SET JSON12 = REPLACE(JSON12, '"numeroTipoAto":409', '"numeroTipoAto":459') WHERE DATAENVIO >= '2026-03-02' AND JSON12 LIKE '%numeroTipoAto\":409%';
UPDATE selados SET JSON12 = REPLACE(JSON12, '"numeroTipoAto":404', '"numeroTipoAto":455') WHERE DATAENVIO >= '2026-03-02' AND JSON12 LIKE '%numeroTipoAto\":404%';
UPDATE selados SET JSON12 = REPLACE(JSON12, '"numeroTipoAto":423', '"numeroTipoAto":452') WHERE DATAENVIO >= '2026-03-02' AND JSON12 LIKE '%numeroTipoAto\":423%';
UPDATE selados SET JSON12 = REPLACE(JSON12, '"numeroTipoAto":430', '"numeroTipoAto":459') WHERE DATAENVIO >= '2026-03-02' AND JSON12 LIKE '%numeroTipoAto\":430%';

-- ---------- BANCO 100.102.13.23 ----------
UPDATE selados SET JSON12 = REPLACE(JSON12, '"numeroTipoAto":432', '"numeroTipoAto":454') WHERE DATAENVIO >= '2026-03-02' AND JSON12 LIKE '%numeroTipoAto\":432%';
UPDATE selados SET JSON12 = REPLACE(JSON12, '"numeroTipoAto":408', '"numeroTipoAto":459') WHERE DATAENVIO >= '2026-03-02' AND JSON12 LIKE '%numeroTipoAto\":408%';
UPDATE selados SET JSON12 = REPLACE(JSON12, '"numeroTipoAto":404', '"numeroTipoAto":455') WHERE DATAENVIO >= '2026-03-02' AND JSON12 LIKE '%numeroTipoAto\":404%';
UPDATE selados SET JSON12 = REPLACE(JSON12, '"numeroTipoAto":418', '"numeroTipoAto":459') WHERE DATAENVIO >= '2026-03-02' AND JSON12 LIKE '%numeroTipoAto\":418%';
UPDATE selados SET JSON12 = REPLACE(JSON12, '"numeroTipoAto":430', '"numeroTipoAto":459') WHERE DATAENVIO >= '2026-03-02' AND JSON12 LIKE '%numeroTipoAto\":430%';
