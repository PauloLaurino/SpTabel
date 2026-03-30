-- ==============================================
-- QUERIES PARA SELADOS (tabela selados)
-- ==============================================

-- 1. INSERIR REGISTRO DE SELAGEM
INSERT INTO selados (
    numapo1_sld,
    numapo2_sld,
    selo_utilizado_sld,
    tiposelo_sld,
    tipo_operacao_sld,
    data_apontamento_sld,
    status_original_sld,
    idap_sld,
    usuario_selagem_sld,
    metodo_selagem_sld,
    status_selagem_sld,
    mensagem_erro_sld
) VALUES (
    :numapo1,
    :numapo2,
    :seloUtilizado,
    :tipoSelo,
    :tipoOperacao,
    :dataApontamento,
    :statusOriginal,
    :idap,
    :usuario,
    :metodo,
    :statusSelagem,
    :mensagemErro
);

-- 2. BUSCAR HISTÓRICO DE SELAGENS
SELECT 
    s.id_sld AS id,
    s.numapo1_sld AS numapo1,
    s.numapo2_sld AS numapo2,
    s.selo_utilizado_sld AS selo,
    s.tiposelo_sld AS tipoSelo,
    s.tipo_operacao_sld AS tipoOperacao,
    s.data_apontamento_sld AS dataApontamento,
    s.status_original_sld AS statusOriginal,
    s.idap_sld AS idap,
    s.data_selagem_sld AS dataSelagem,
    s.usuario_selagem_sld AS usuario,
    s.metodo_selagem_sld AS metodo,
    s.status_selagem_sld AS status,
    s.mensagem_erro_sld AS mensagemErro,
    
    -- Informações do apontamento (se necessário)
    a.status_001 AS statusAtual,
    a.seloaponta_001 AS seloAtual
    
FROM selados s
LEFT JOIN ctp001 a ON s.numapo1_sld = a.numapo1_001 AND s.numapo2_sld = a.numapo2_001
WHERE 1=1
    -- Filtros
    AND (:dataInicial IS NULL OR s.data_selagem_sld >= :dataInicial)
    AND (:dataFinal IS NULL OR s.data_selagem_sld <= :dataFinal)
    AND (:usuario IS NULL OR s.usuario_selagem_sld = :usuario)
    AND (:status IS NULL OR s.status_selagem_sld = :status)
ORDER BY s.data_selagem_sld DESC
LIMIT :limit OFFSET :offset;

-- 3. RELATÓRIO DE PRODUTIVIDADE
SELECT 
    DATE(data_selagem_sld) AS data,
    usuario_selagem_sld AS usuario,
    metodo_selagem_sld AS metodo,
    
    -- Totais
    COUNT(*) AS total_selagens,
    SUM(CASE WHEN status_selagem_sld = 'CONCLUIDO' THEN 1 ELSE 0 END) AS sucessos,
    SUM(CASE WHEN status_selagem_sld = 'ERRO' THEN 1 ELSE 0 END) AS erros,
    
    -- Percentuais
    ROUND(
        SUM(CASE WHEN status_selagem_sld = 'CONCLUIDO' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 
        2
    ) AS taxa_sucesso,
    
    -- Média por hora
    ROUND(COUNT(*) / 
        NULLIF(
            SUM(
                CASE 
                    WHEN DATE(data_selagem_sld) = DATE(NOW()) 
                    THEN TIMESTAMPDIFF(HOUR, MIN(data_selagem_sld), MAX(data_selagem_sld))
                    ELSE 8 -- jornada padrão
                END
            ), 
        0), 
    2) AS media_hora
    
FROM selados
WHERE data_selagem_sld BETWEEN :dataInicial AND :dataFinal
GROUP BY DATE(data_selagem_sld), usuario_selagem_sld, metodo_selagem_sld
ORDER BY data DESC, total_selagens DESC;

-- 4. VERIFICAR SELAGEM DUPLICADA
SELECT 
    numapo1_sld,
    numapo2_sld,
    COUNT(*) AS vezes_selado,
    GROUP_CONCAT(selo_utilizado_sld ORDER BY data_selagem_sld) AS selos_utilizados
FROM selados
GROUP BY numapo1_sld, numapo2_sld
HAVING COUNT(*) > 1;

-- 5. ESTATÍSTICAS POR TIPO DE OPERAÇÃO
SELECT 
    tipo_operacao_sld AS tipo_operacao,
    
    -- Contagens
    COUNT(*) AS total,
    SUM(CASE WHEN status_selagem_sld = 'CONCLUIDO' THEN 1 ELSE 0 END) AS sucessos,
    SUM(CASE WHEN status_selagem_sld = 'ERRO' THEN 1 ELSE 0 END) AS erros,
    
    -- Datas
    MIN(data_selagem_sld) AS primeira_selagem,
    MAX(data_selagem_sld) AS ultima_selagem,
    
    -- Média de tempo entre selagens (em minutos)
    ROUND(
        AVG(
            TIMESTAMPDIFF(
                MINUTE, 
                LAG(data_selagem_sld) OVER (PARTITION BY tipo_operacao_sld ORDER BY data_selagem_sld),
                data_selagem_sld
            )
        ), 
    2) AS tempo_medio_entre_selagens
    
FROM selados
WHERE data_selagem_sld BETWEEN :dataInicial AND :dataFinal
GROUP BY tipo_operacao_sld
ORDER BY total DESC;

-- 6. TOP 10 USUÁRIOS QUE MAIS SELARAM
SELECT 
    usuario_selagem_sld AS usuario,
    COUNT(*) AS total_selagens,
    MIN(data_selagem_sld) AS primeira_selagem,
    MAX(data_selagem_sld) AS ultima_selagem,
    ROUND(
        COUNT(*) / NULLIF(
            COUNT(DISTINCT DATE(data_selagem_sld)), 
            0
        ), 
    2) AS media_diaria
    
FROM selados
WHERE data_selagem_sld >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY usuario_selagem_sld
ORDER BY total_selagens DESC
LIMIT 10;

-- 7. ERROS MAIS COMUNS
SELECT 
    mensagem_erro_sld AS erro,
    COUNT(*) AS ocorrencias,
    MIN(data_selagem_sld) AS primeiro_erro,
    MAX(data_selagem_sld) AS ultimo_erro,
    GROUP_CONCAT(DISTINCT usuario_selagem_sld) AS usuarios_afetados
FROM selados
WHERE status_selagem_sld = 'ERRO'
  AND mensagem_erro_sld IS NOT NULL
  AND mensagem_erro_sld != ''
GROUP BY mensagem_erro_sld
ORDER BY ocorrencias DESC;

-- 8. SELAGENS POR HORA DO DIA
SELECT 
    HOUR(data_selagem_sld) AS hora,
    COUNT(*) AS total_selagens,
    COUNT(DISTINCT usuario_selagem_sld) AS usuarios_ativos,
    ROUND(
        COUNT(*) / NULLIF(
            COUNT(DISTINCT DATE(data_selagem_sld)), 
            0
        ), 
    2) AS media_por_dia
    
FROM selados
WHERE data_selagem_sld BETWEEN :dataInicial AND :dataFinal
GROUP BY HOUR(data_selagem_sld)
ORDER BY hora;

-- 9. TEMPO MÉDIO DE PROCESSAMENTO
SELECT 
    metodo_selagem_sld AS metodo,
    
    -- Estatísticas de tempo (se tiver timestamp de início e fim)
    ROUND(
        AVG(
            TIMESTAMPDIFF(
                SECOND, 
                data_inicio_processamento, -- assumindo que existe
                data_fim_processamento     -- assumindo que existe
            )
        ), 
    2) AS tempo_medio_segundos,
    
    ROUND(
        MAX(
            TIMESTAMPDIFF(
                SECOND, 
                data_inicio_processamento,
                data_fim_processamento
            )
        ), 
    2) AS tempo_maximo_segundos,
    
    ROUND(
        MIN(
            TIMESTAMPDIFF(
                SECOND, 
                data_inicio_processamento,
                data_fim_processamento
            )
        ), 
    2) AS tempo_minimo_segundos
    
FROM selados
WHERE data_selagem_sld BETWEEN :dataInicial AND :dataFinal
  AND status_selagem_sld = 'CONCLUIDO'
GROUP BY metodo_selagem_sld;

-- 10. EXPORTAR DADOS PARA AUDITORIA
SELECT 
    s.*,
    a.tipooper_001 AS tipo_operacao_atual,
    a.status_001 AS status_atual,
    a.dataaponta_001 AS data_apontamento_atual,
    a.seloaponta_001 AS selo_atual_no_apontamento
FROM selados s
LEFT JOIN ctp001 a ON s.numapo1_sld = a.numapo1_001 AND s.numapo2_sld = a.numapo2_001
WHERE s.data_selagem_sld BETWEEN :dataInicial AND :dataFinal
ORDER BY s.data_selagem_sld;