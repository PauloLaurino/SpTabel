-- Script para consultar selos pendentes de sanitização
-- Período: 2026-03-01 até 2026-03-20

SELECT 
    s.selo_sel,
    s.idap_sel,
    s.JSON,
    s.STATUS,
    sel.DATAENVIO,
    p.DOC_PAR,
    p.AMBIENTE_PAR
FROM selos s
INNER JOIN selados sel ON sel.SELO = s.selo_sel
INNER JOIN parametros p ON p.CODIGO_PAR = 1
WHERE sel.DATAENVIO >= '2026-03-01'
AND sel.DATAENVIO <= '2026-03-20'
AND (sel.STATUS IS NULL OR sel.STATUS != 'SUCESSO')
ORDER BY sel.DATAENVIO;
