-- Script de sanitização para registros do dia 13/03/2026 no banco sptabel
-- Executar: mysql -u root -pk15720 sptabel < sanitizar_selos_13_03_2026.sql

USE sptabel;

-- Mostrar resumo dos registros antes da atualização
SELECT 
    NUMTIPATO as tipo_ato,
    COUNT(*) as total,
    SUM(CASE WHEN JSON LIKE '%|ambiente|%' THEN 1 ELSE 0 END) as com_placeholder_ambiente,
    SUM(CASE WHEN JSON LIKE '%|codigoEmpresa|%' THEN 1 ELSE 0 END) as com_placeholder_empresa,
    SUM(CASE WHEN JSON LIKE '%emvolvidos%' THEN 1 ELSE 0 END) as com_erro_emvolvidos
FROM selados 
WHERE DATAENVIO >= '2026-03-13' AND DATAENVIO < '2026-03-14'
GROUP BY NUMTIPATO;

-- Observações:
-- 1. Os JSONs contém placeholders como |ambiente|, |codigoEmpresa|, |dataato|
-- 2. O campo 'emvolvidos' está com erro de digitação (falta 'v')
-- 3. Estes registros precisam ser re-processados pelo sistema de origem (Maker)
-- 4. A correção deve ser feita na geração do JSON, não no banco

-- Para atualizar manualmente (exemplo para ambiente):
-- UPDATE selados 
-- SET JSON = REPLACE(JSON, '"|ambiente|"', '"prod"')
-- WHERE DATAENVIO >= '2026-03-13' AND DATAENVIO < '2026-03-14';

-- Lista de IDs afetados
SELECT ID, NUMTIPATO, SELO, DATAENVIO 
FROM selados 
WHERE DATAENVIO >= '2026-03-13' AND DATAENVIO < '2026-03-14'
ORDER BY ID;
