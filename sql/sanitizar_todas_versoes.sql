-- =================================================================
-- Script para sanitizar TODAS as versões (100, 110, 112)
-- Período: 2026-03-01 até 2026-03-31
-- =================================================================

-- Primeiro, copiar JSON para JSON12 onde JSON12 está vazio ou igual a JSON
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = sel.JSON
WHERE sel.DATAENVIO >= '2026-03-01' 
AND sel.DATAENVIO <= '2026-03-31'
AND (sel.JSON12 IS NULL OR sel.JSON12 = sel.JSON);

-- Verificar depois da copia
SELECT 
    s.versao,
    COUNT(*) as total,
    SUM(CASE WHEN sel.JSON = sel.JSON12 THEN 1 ELSE 0 END) as igual,
    SUM(CASE WHEN sel.JSON != sel.JSON12 THEN 1 ELSE 0 END) as diferente
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE sel.DATAENVIO >= '2026-03-01' 
AND sel.DATAENVIO <= '2026-03-31'
GROUP BY s.versao;

-- ==== CORRECOES DE CABECALHO ====
-- 1. Corrigir documento placeholder
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '\n"documento":"0"', '\n"documento":"09851329657"')
WHERE sel.DATAENVIO >= '2026-03-01' 
AND sel.DATAENVIO <= '2026-03-31'
AND sel.JSON12 LIKE '%"documento":"0"%';

-- 2. Corrigir codoficio para codigoOficio
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '\n"codoficio"', '\n"codigoOficio"')
WHERE sel.DATAENVIO >= '2026-03-01' 
AND sel.DATAENVIO <= '2026-03-31'
AND sel.JSON12 LIKE '%"codoficio"%';

-- 3. Corrigir versao 100/110/112 para 11.12
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '\n"versao":"100"', '\n"versao":"11.12"')
WHERE sel.DATAENVIO >= '2026-03-01' 
AND sel.DATAENVIO <= '2026-03-31';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '\n"versao":"110"', '\n"versao":"11.12"')
WHERE sel.DATAENVIO >= '2026-03-01' 
AND sel.DATAENVIO <= '2026-03-31';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '\n"versao":"112"', '\n"versao":"11.12"')
WHERE sel.DATAENVIO >= '2026-03-01' 
AND sel.DATAENVIO <= '2026-03-31';

-- 4. Corrigir numeroTipoAto para codigoTipoAto
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"numeroTipoAto"', '"codigoTipoAto"')
WHERE sel.DATAENVIO >= '2026-03-01' 
AND sel.DATAENVIO <= '2026-03-31'
AND sel.JSON12 LIKE '%"numeroTipoAto"%';

-- ==== CORRECOES DE VERBAS ====
-- 5. Renomear ListaVerbas para verbas
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"ListaVerbas"', '"verbas"')
WHERE sel.DATAENVIO >= '2026-03-01' 
AND sel.DATAENVIO <= '2026-03-31'
AND sel.JSON12 LIKE '%"ListaVerbas"%';

-- 6. Corrigir valores das verbas (dividir por 100)
-- EMOLUMENTOS
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"EMOLUMENTOS",1207', '"emolumentos":12.07')
WHERE sel.DATAENVIO >= '2026-03-01' AND sel.DATAENVIO <= '2026-03-31';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"EMOLUMENTOS",2414', '"emolumentos":24.14')
WHERE sel.DATAENVIO >= '2026-03-01' AND sel.DATAENVIO <= '2026-03-31';

-- FUNREJUS
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"FUNREJUS",301', '"funrejus":3.01')
WHERE sel.DATAENVIO >= '2026-03-01' AND sel.DATAENVIO <= '2026-03-31';

-- ISS
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"ISS",36', '"iss":0.36')
WHERE sel.DATAENVIO >= '2026-03-01' AND sel.DATAENVIO <= '2026-03-31';

-- FUNDEP
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"FUNDEP",60', '"fundep":0.60')
WHERE sel.DATAENVIO >= '2026-03-01' AND sel.DATAENVIO <= '2026-03-31';

-- FUNARPEN
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"FUNARPEN",100', '"funarpen":1.00')
WHERE sel.DATAENVIO >= '2026-03-01' AND sel.DATAENVIO <= '2026-03-31';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"FUNARPEN",200', '"funarpen":2.00')
WHERE sel.DATAENVIO >= '2026-03-01' AND sel.DATAENVIO <= '2026-03-31';

-- VRCEXT
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"VRCEXT",0', '"vrcExt":0')
WHERE sel.DATAENVIO >= '2026-03-01' AND sel.DATAENVIO <= '2026-03-31';

-- DISTRIBUIDOR
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"DISTRIBUIDOR",0', '"distribuidor":0,"valorAdicional":0')
WHERE sel.DATAENVIO >= '2026-03-01' AND sel.DATAENVIO <= '2026-03-31';

-- ==== CORRECOES DE PROPRIEDADES ====
-- 7. Renomear ListaPropriedades para propriedades
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"ListaPropriedades"', '"propriedades"')
WHERE sel.DATAENVIO >= '2026-03-01' AND sel.DATAENVIO <= '2026-03-31';

-- Resultado final
SELECT 
    s.versao,
    COUNT(*) as total,
    SUM(CASE WHEN sel.JSON = sel.JSON12 THEN 1 ELSE 0 END) as igual,
    SUM(CASE WHEN sel.JSON != sel.JSON12 THEN 1 ELSE 0 END) as diferente
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE sel.DATAENVIO >= '2026-03-01' 
AND sel.DATAENVIO <= '2026-03-31'
GROUP BY s.versao;-- Script para sanitizar TODAS as versões (100, 110, 112)
-- Período: 2026-03-01 até 2026-03-31
-- =================================================================

-- Primeiro, copiar JSON para JSON12 onde JSON12 está vazio ou igual a JSON
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = sel.JSON
WHERE sel.DATAENVIO >= '2026-03-01' 
AND sel.DATAENVIO <= '2026-03-31'
AND (sel.JSON12 IS NULL OR sel.JSON12 = sel.JSON);

-- Verificar depois da copia
SELECT 
    s.versao,
    COUNT(*) as total,
    SUM(CASE WHEN sel.JSON = sel.JSON12 THEN 1 ELSE 0 END) as igual,
    SUM(CASE WHEN sel.JSON != sel.JSON12 THEN 1 ELSE 0 END) as diferente
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE sel.DATAENVIO >= '2026-03-01' 
AND sel.DATAENVIO <= '2026-03-31'
GROUP BY s.versao;

-- ==== CORRECOES DE CABECALHO ====
-- 1. Corrigir documento placeholder
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '\n"documento":"0"', '\n"documento":"09851329657"')
WHERE sel.DATAENVIO >= '2026-03-01' 
AND sel.DATAENVIO <= '2026-03-31'
AND sel.JSON12 LIKE '%"documento":"0"%';

-- 2. Corrigir codoficio para codigoOficio
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '\n"codoficio"', '\n"codigoOficio"')
WHERE sel.DATAENVIO >= '2026-03-01' 
AND sel.DATAENVIO <= '2026-03-31'
AND sel.JSON12 LIKE '%"codoficio"%';

-- 3. Corrigir versao 100/110/112 para 11.12
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '\n"versao":"100"', '\n"versao":"11.12"')
WHERE sel.DATAENVIO >= '2026-03-01' 
AND sel.DATAENVIO <= '2026-03-31';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '\n"versao":"110"', '\n"versao":"11.12"')
WHERE sel.DATAENVIO >= '2026-03-01' 
AND sel.DATAENVIO <= '2026-03-31';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '\n"versao":"112"', '\n"versao":"11.12"')
WHERE sel.DATAENVIO >= '2026-03-01' 
AND sel.DATAENVIO <= '2026-03-31';

-- 4. Corrigir numeroTipoAto para codigoTipoAto
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"numeroTipoAto"', '"codigoTipoAto"')
WHERE sel.DATAENVIO >= '2026-03-01' 
AND sel.DATAENVIO <= '2026-03-31'
AND sel.JSON12 LIKE '%"numeroTipoAto"%';

-- ==== CORRECOES DE VERBAS ====
-- 5. Renomear ListaVerbas para verbas
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"ListaVerbas"', '"verbas"')
WHERE sel.DATAENVIO >= '2026-03-01' 
AND sel.DATAENVIO <= '2026-03-31'
AND sel.JSON12 LIKE '%"ListaVerbas"%';

-- 6. Corrigir valores das verbas (dividir por 100)
-- EMOLUMENTOS
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"EMOLUMENTOS",1207', '"emolumentos":12.07')
WHERE sel.DATAENVIO >= '2026-03-01' AND sel.DATAENVIO <= '2026-03-31';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"EMOLUMENTOS",2414', '"emolumentos":24.14')
WHERE sel.DATAENVIO >= '2026-03-01' AND sel.DATAENVIO <= '2026-03-31';

-- FUNREJUS
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"FUNREJUS",301', '"funrejus":3.01')
WHERE sel.DATAENVIO >= '2026-03-01' AND sel.DATAENVIO <= '2026-03-31';

-- ISS
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"ISS",36', '"iss":0.36')
WHERE sel.DATAENVIO >= '2026-03-01' AND sel.DATAENVIO <= '2026-03-31';

-- FUNDEP
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"FUNDEP",60', '"fundep":0.60')
WHERE sel.DATAENVIO >= '2026-03-01' AND sel.DATAENVIO <= '2026-03-31';

-- FUNARPEN
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"FUNARPEN",100', '"funarpen":1.00')
WHERE sel.DATAENVIO >= '2026-03-01' AND sel.DATAENVIO <= '2026-03-31';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"FUNARPEN",200', '"funarpen":2.00')
WHERE sel.DATAENVIO >= '2026-03-01' AND sel.DATAENVIO <= '2026-03-31';

-- VRCEXT
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"VRCEXT",0', '"vrcExt":0')
WHERE sel.DATAENVIO >= '2026-03-01' AND sel.DATAENVIO <= '2026-03-31';

-- DISTRIBUIDOR
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"DISTRIBUIDOR",0', '"distribuidor":0,"valorAdicional":0')
WHERE sel.DATAENVIO >= '2026-03-01' AND sel.DATAENVIO <= '2026-03-31';

-- ==== CORRECOES DE PROPRIEDADES ====
-- 7. Renomear ListaPropriedades para propriedades
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"ListaPropriedades"', '"propriedades"')
WHERE sel.DATAENVIO >= '2026-03-01' AND sel.DATAENVIO <= '2026-03-31';

-- Resultado final
SELECT 
    s.versao,
    COUNT(*) as total,
    SUM(CASE WHEN sel.JSON = sel.JSON12 THEN 1 ELSE 0 END) as igual,
    SUM(CASE WHEN sel.JSON != sel.JSON12 THEN 1 ELSE 0 END) as diferente
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE sel.DATAENVIO >= '2026-03-01' 
AND sel.DATAENVIO <= '2026-03-31'
GROUP BY s.versao;
