-- ==============================================
-- QUERIES PARA SELOS (tabela selos)
-- ==============================================

-- 1. BUSCAR SELOS DISPONÍVEIS
-- Query utilizada em SeloDAO.java
SELECT 
    selo_sel AS selo,
    tiposelo_sel AS tipoSelo,
    tipo_sel AS tipo,
    situacao_sel AS situacao,
    data_criacao_sel AS dataCriacao
FROM selos
WHERE situacao_sel = 'DISPONIVEL'
  AND tiposelo_sel = COALESCE(:tipoSelo, '0004')
  AND tipo_sel = COALESCE(:tipo, '0000')
ORDER BY selo_sel ASC
LIMIT :quantidade
FOR UPDATE; -- Lock para evitar concorrência

-- 2. RESERVAR SELO
UPDATE selos
SET 
    situacao_sel = 'RESERVADO',
    data_reserva_sel = CURRENT_TIMESTAMP,
    usuario_reserva_sel = :usuario
WHERE selo_sel = :selo
  AND situacao_sel = 'DISPONIVEL';

-- 3. MARCAR SELO COMO UTILIZADO
UPDATE selos
SET 
    situacao_sel = 'UTILIZADO',
    data_utilizacao_sel = CURRENT_TIMESTAMP,
    usuario_utilizacao_sel = :usuario,
    apontamento_utilizado_sel = :apontamento
WHERE selo_sel = :selo
  AND situacao_sel = 'RESERVADO';

-- 4. LIBERAR SELO (cancelar reserva)
UPDATE selos
SET 
    situacao_sel = 'DISPONIVEL',
    data_reserva_sel = NULL,
    usuario_reserva_sel = NULL
WHERE selo_sel = :selo
  AND situacao_sel = 'RESERVADO';

-- 5. CONTAR SELOS POR SITUAÇÃO
SELECT 
    situacao_sel AS situacao,
    tiposelo_sel AS tipoSelo,
    COUNT(*) AS quantidade
FROM selos
GROUP BY situacao_sel, tiposelo_sel
ORDER BY tiposelo_sel, situacao_sel;

-- 6. BUSCAR HISTÓRICO DE USO DO SELO
SELECT 
    s.selo_sel AS selo,
    s.situacao_sel AS situacao_atual,
    s.data_reserva_sel AS data_reserva,
    s.data_utilizacao_sel AS data_utilizacao,
    s.usuario_utilizacao_sel AS usuario_utilizacao,
    s.apontamento_utilizado_sel AS apontamento_utilizado,
    sl.status_selagem_sld AS status_selagem,
    sl.data_selagem_sld AS data_selagem
FROM selos s
LEFT JOIN selados sl ON s.selo_sel = sl.selo_utilizado_sld
WHERE s.selo_sel = :selo
ORDER BY sl.data_selagem_sld DESC;

-- 7. SELOS VENCIDOS DE RESERVA (mais de 10 minutos)
SELECT 
    selo_sel AS selo,
    data_reserva_sel AS data_reserva,
    usuario_reserva_sel AS usuario,
    TIMESTAMPDIFF(MINUTE, data_reserva_sel, NOW()) AS minutos_reservado
FROM selos
WHERE situacao_sel = 'RESERVADO'
  AND data_reserva_sel < DATE_SUB(NOW(), INTERVAL 10 MINUTE);

-- 8. LIBERAR SELOS COM RESERVA VENCIDA
UPDATE selos
SET 
    situacao_sel = 'DISPONIVEL',
    data_reserva_sel = NULL,
    usuario_reserva_sel = NULL
WHERE situacao_sel = 'RESERVADO'
  AND data_reserva_sel < DATE_SUB(NOW(), INTERVAL 10 MINUTE);

-- 9. RELATÓRIO COMPLETO DE SELOS
SELECT 
    tiposelo_sel AS tipo_selo,
    
    -- Totais por situação
    SUM(CASE WHEN situacao_sel = 'DISPONIVEL' THEN 1 ELSE 0 END) AS disponiveis,
    SUM(CASE WHEN situacao_sel = 'RESERVADO' THEN 1 ELSE 0 END) AS reservados,
    SUM(CASE WHEN situacao_sel = 'UTILIZADO' THEN 1 ELSE 0 END) AS utilizados,
    SUM(CASE WHEN situacao_sel = 'CANCELADO' THEN 1 ELSE 0 END) AS cancelados,
    SUM(CASE WHEN situacao_sel = 'BLOQUEADO' THEN 1 ELSE 0 END) AS bloqueados,
    
    COUNT(*) AS total,
    
    -- Datas
    MIN(data_criacao_sel) AS data_primeiro_selo,
    MAX(data_criacao_sel) AS data_ultimo_selo,
    MAX(data_utilizacao_sel) AS data_ultima_utilizacao
    
FROM selos
GROUP BY tiposelo_sel
ORDER BY tiposelo_sel;

-- 10. IMPORTAR NOVOS SELOS (batch)
INSERT INTO selos (selo_sel, tiposelo_sel, tipo_sel, situacao_sel)
VALUES 
    (:selo1, :tipoSelo, :tipo, 'DISPONIVEL'),
    (:selo2, :tipoSelo, :tipo, 'DISPONIVEL'),
    -- ... mais selos
ON DUPLICATE KEY UPDATE
    tiposelo_sel = VALUES(tiposelo_sel),
    tipo_sel = VALUES(tipo_sel),
    data_atualizacao_sel = CURRENT_TIMESTAMP;

-- 11. VERIFICAR INTEGRIDADE DOS SELOS
-- Selos utilizados devem ter registro na tabela selados
SELECT 
    s.selo_sel AS selo_sem_registro
FROM selos s
LEFT JOIN selados sl ON s.selo_sel = sl.selo_utilizado_sld
WHERE s.situacao_sel = 'UTILIZADO'
  AND sl.id_sld IS NULL;

-- 12. SELOS MAIS UTILIZADOS (top 10)
SELECT 
    s.selo_sel AS selo,
    COUNT(sl.id_sld) AS vezes_utilizado,
    MIN(sl.data_selagem_sld) AS primeira_utilizacao,
    MAX(sl.data_selagem_sld) AS ultima_utilizacao
FROM selos s
LEFT JOIN selados sl ON s.selo_sel = sl.selo_utilizado_sld
WHERE s.situacao_sel = 'UTILIZADO'
GROUP BY s.selo_sel
ORDER BY vezes_utilizado DESC
LIMIT 10;