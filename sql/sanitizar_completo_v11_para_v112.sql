-- =================================================================
-- Script completo para sanitizar JSON V11 para V11.12
-- Transforma a estrutura flat do V11 para o objeto "selo" do V11.12
-- Período: 2026-03-01 até 2026-03-31
-- =================================================================

-- Primeiro, verificar quantos registros precisam de conversão
SELECT 
    COUNT(*) as total,
    SUM(CASE WHEN JSON12 LIKE '%\"selo\":%' THEN 1 ELSE 0 END) as ja_possui_selo,
    SUM(CASE WHEN JSON12 NOT LIKE '%\"selo\":%' THEN 1 ELSE 0 END) as precisa_converter
FROM selados 
WHERE DATAENVIO >= '2026-03-01' AND DATAENVIO <= '2026-03-31';

-- Passo 1: Substituir campos de header (placeholders)
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"documento":"0"', '"documento":"09851329657"')
WHERE s.versao = '112'
AND sel.JSON12 LIKE '%"documento":"0"%';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"codoficio":"181122"', '"codigoOficio":181122')
WHERE s.versao = '112'
AND sel.JSON12 LIKE '%"codoficio"%';

-- Passo 2: Corrigir numeroTipoAto para codigoTipoAto dentro do objeto
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"numeroTipoAto"', '"codigoTipoAto"')
WHERE s.versao = '112'
AND sel.JSON12 LIKE '%"numeroTipoAto"%';

-- Passo 3: Corrigir versão
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"versao":"11"', '"versao":"11.12"')
WHERE s.versao = '112'
AND sel.JSON12 LIKE '%"versao":"11"%';

-- Passo 4: Corrigir codigoPedido (remover decimais se existir)
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"codigoPedido":', '"codigoPedido":')
WHERE s.versao = '112'
AND sel.JSON12 LIKE '%"codigoPedido":%';

-- Passo 5: Renomear ListaVerbas para verbas e converter valores
-- Primero renomear
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"ListaVerbas"', '"verbas"')
WHERE s.versao = '112'
AND sel.JSON12 LIKE '%"ListaVerbas"%';

-- Corrigir valores das verbas (dividir por 100 se >= 10)
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"EMOLUMENTOS",1207', '"emolumentos":12.07')
WHERE s.versao = '112';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"EMOLUMENTOS",2414', '"emolumentos":24.14')
WHERE s.versao = '112';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"FUNREJUS",301', '"funrejus":3.01')
WHERE s.versao = '112';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"ISS",36', '"iss":0.36')
WHERE s.versao = '112';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"FUNDEP",60', '"fundep":0.60')
WHERE s.versao = '112';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"FUNARPEN",100', '"funarpen":1.00')
WHERE s.versao = '112';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"FUNARPEN",200', '"funarpen":2.00')
WHERE s.versao = '112';

-- Corrigir vrcExt e distribuidor
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"VRCEXT",0', '"vrcExt":0')
WHERE s.versao = '112'
AND sel.JSON12 LIKE '%"VRCEXT"%';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"DISTRIBUIDOR",0', '"distribuidor":0')
WHERE s.versao = '112'
AND sel.JSON12 LIKE '%"DISTRIBUIDOR"%';

-- Adicionar valorAdicional
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"distribuidor":0}', '"distribuidor":0,"valorAdicional":0')
WHERE s.versao = '112'
AND sel.JSON12 LIKE '%"distribuidor":0}%';

-- Passo 6: Renomear ListaPropriedades para propriedades
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"ListaPropriedades"', '"propriedades"')
WHERE s.versao = '112'
AND sel.JSON12 LIKE '%"ListaPropriedades"%';

-- Verificar resultado
SELECT 
    s.versao,
    COUNT(*) as total,
    SUM(CASE WHEN sel.JSON12 LIKE '%\"selo\":%' THEN 1 ELSE 0 END) as tem_selo_object,
    SUM(CASE WHEN sel.JSON12 LIKE '%\"versao\":\"11.12\"%' THEN 1 ELSE 0 END) as tem_versao_112
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112'
GROUP BY s.versao;-- Script completo para sanitizar JSON V11 para V11.12
-- Transforma a estrutura flat do V11 para o objeto "selo" do V11.12
-- Período: 2026-03-01 até 2026-03-31
-- =================================================================

-- Primeiro, verificar quantos registros precisam de conversão
SELECT 
    COUNT(*) as total,
    SUM(CASE WHEN JSON12 LIKE '%\"selo\":%' THEN 1 ELSE 0 END) as ja_possui_selo,
    SUM(CASE WHEN JSON12 NOT LIKE '%\"selo\":%' THEN 1 ELSE 0 END) as precisa_converter
FROM selados 
WHERE DATAENVIO >= '2026-03-01' AND DATAENVIO <= '2026-03-31';

-- Passo 1: Substituir campos de header (placeholders)
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"documento":"0"', '"documento":"09851329657"')
WHERE s.versao = '112'
AND sel.JSON12 LIKE '%"documento":"0"%';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"codoficio":"181122"', '"codigoOficio":181122')
WHERE s.versao = '112'
AND sel.JSON12 LIKE '%"codoficio"%';

-- Passo 2: Corrigir numeroTipoAto para codigoTipoAto dentro do objeto
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"numeroTipoAto"', '"codigoTipoAto"')
WHERE s.versao = '112'
AND sel.JSON12 LIKE '%"numeroTipoAto"%';

-- Passo 3: Corrigir versão
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"versao":"11"', '"versao":"11.12"')
WHERE s.versao = '112'
AND sel.JSON12 LIKE '%"versao":"11"%';

-- Passo 4: Corrigir codigoPedido (remover decimais se existir)
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"codigoPedido":', '"codigoPedido":')
WHERE s.versao = '112'
AND sel.JSON12 LIKE '%"codigoPedido":%';

-- Passo 5: Renomear ListaVerbas para verbas e converter valores
-- Primero renomear
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"ListaVerbas"', '"verbas"')
WHERE s.versao = '112'
AND sel.JSON12 LIKE '%"ListaVerbas"%';

-- Corrigir valores das verbas (dividir por 100 se >= 10)
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"EMOLUMENTOS",1207', '"emolumentos":12.07')
WHERE s.versao = '112';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"EMOLUMENTOS",2414', '"emolumentos":24.14')
WHERE s.versao = '112';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"FUNREJUS",301', '"funrejus":3.01')
WHERE s.versao = '112';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"ISS",36', '"iss":0.36')
WHERE s.versao = '112';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"FUNDEP",60', '"fundep":0.60')
WHERE s.versao = '112';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"FUNARPEN",100', '"funarpen":1.00')
WHERE s.versao = '112';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"FUNARPEN",200', '"funarpen":2.00')
WHERE s.versao = '112';

-- Corrigir vrcExt e distribuidor
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"VRCEXT",0', '"vrcExt":0')
WHERE s.versao = '112'
AND sel.JSON12 LIKE '%"VRCEXT"%';

UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"DISTRIBUIDOR",0', '"distribuidor":0')
WHERE s.versao = '112'
AND sel.JSON12 LIKE '%"DISTRIBUIDOR"%';

-- Adicionar valorAdicional
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"distribuidor":0}', '"distribuidor":0,"valorAdicional":0')
WHERE s.versao = '112'
AND sel.JSON12 LIKE '%"distribuidor":0}%';

-- Passo 6: Renomear ListaPropriedades para propriedades
UPDATE selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
SET sel.JSON12 = REPLACE(sel.JSON12, '"ListaPropriedades"', '"propriedades"')
WHERE s.versao = '112'
AND sel.JSON12 LIKE '%"ListaPropriedades"%';

-- Verificar resultado
SELECT 
    s.versao,
    COUNT(*) as total,
    SUM(CASE WHEN sel.JSON12 LIKE '%\"selo\":%' THEN 1 ELSE 0 END) as tem_selo_object,
    SUM(CASE WHEN sel.JSON12 LIKE '%\"versao\":\"11.12\"%' THEN 1 ELSE 0 END) as tem_versao_112
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112'
GROUP BY s.versao;
