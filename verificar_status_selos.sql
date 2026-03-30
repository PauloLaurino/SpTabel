-- Script para verificar status geral dos selos
-- Período: 2026-03-01 até 2026-03-20

SELECT 
    COUNT(*) as total_selos,
    SUM(CASE WHEN STATUS = 'SUCESSO' THEN 1 ELSE 0 END) as sucessos,
    SUM(CASE WHEN STATUS IS NULL OR STATUS != 'SUCESSO' THEN 1 ELSE 0 END) as pendentes,
    SUM(CASE WHEN STATUS = 'ERRO' THEN 1 ELSE 0 END) as erros
FROM selados
WHERE DATAENVIO >= '2026-03-01'
AND DATAENVIO <= '2026-03-20';
