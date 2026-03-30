-- Script para buscar dados do TIPO 455 e verificar sanitização
-- Uso: mysql -u root -pk15720 sptabel < buscar_tipo_455_sucesso.sql

-- 1. Buscar selo específico
SELECT 
    sel.SELO,
    sel.JSON,
    sel.JSON12,
    sel.STATUS,
    sel.DATAENVIO,
    s.codigoTipoAto,
    s.idap,
    p.DOC_PAR,
    p.DTVERSAO_FUNARPEN
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
INNER JOIN parametros p ON p.CODIGO_PAR = 1
WHERE sel.SELO = 'SFTN1.cG7Rb.Mwfwe-KRsZM.1122q';

-- 2. Buscar solicitante em fin_reccab usando IDAP
-- IDAP: 0000000000000045305R00000000000000000000
-- num_rec: 0000045305 (posições 11-20)
SELECT 
    num_rec,
    nomecli_rec,
    cpfcli_rec
FROM fin_reccab
WHERE num_rec = '0000045305';

-- 3. Buscar parâmetros
SELECT 
    DOC_PAR,
    DTVERSAO_FUNARPEN,
    AMBIENTE_PAR,
    CODTABEL_PAR
FROM parametros
WHERE CODIGO_PAR = 1;
