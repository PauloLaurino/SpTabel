-- ==============================================
-- QUERIES PARA APONTAMENTOS (tabela ctp001)
-- ==============================================

-- 1. BUSCA DE APONTAMENTOS COM FILTROS
-- Query utilizada em ApontamentoDAO.java
SELECT 
    numapo1_001 AS numapo1,
    numapo2_001 AS numapo2,
    tipooper_001 AS tipoOperacao,
    dataaponta_001 AS dataApontamento,
    status_001 AS status,
    seloaponta_001 AS seloAtual,
    valor_001 AS valor,
    dataprot_001 AS dataProtesto,
    databaixa_001 AS dataBaixa,
    dataretir_001 AS dataRetirada,
    datapagto_001 AS dataPagamento,
    dataintim_001 AS dataIntimacao,
    datadevol_001 AS dataDevolucao,
    datasust_001 AS dataSustacao,
    datadig_001 AS dataDigitalizacao,
    -- ... outros 200+ campos conforme necessário
FROM ctp001
WHERE 1=1
    -- Filtro por data de apontamento
    AND (:dataInicial IS NULL OR dataaponta_001 >= :dataInicial)
    AND (:dataFinal IS NULL OR dataaponta_001 <= :dataFinal)
    
    -- Filtro por tipo de operação
    AND (:tipoOperacao IS NULL OR tipooper_001 = :tipoOperacao)
    
    -- Filtro por status
    AND (:status IS NULL OR status_001 = :status)
    
    -- Filtro para excluir já selados (se necessário)
    AND (:incluirSelados = true OR seloaponta_001 IS NULL OR seloaponta_001 = '')
    
    -- Ordenação padrão
ORDER BY dataaponta_001 DESC, numapo1_001, numapo2_001
LIMIT :limit OFFSET :offset;

-- 2. BUSCA POR CHAVE PRIMÁRIA
SELECT * FROM ctp001 
WHERE numapo1_001 = :numapo1 
  AND numapo2_001 = :numapo2;

-- 3. ATUALIZAR SELO DO APONTAMENTO
UPDATE ctp001 
SET seloaponta_001 = :selo,
    data_ultima_atualizacao = CURRENT_TIMESTAMP,
    usuario_ultima_atualizacao = :usuario
WHERE numapo1_001 = :numapo1 
  AND numapo2_001 = :numapo2;

-- 4. CONTAGEM DE APONTAMENTOS POR STATUS
SELECT 
    status_001 AS status,
    COUNT(*) AS quantidade,
    SUM(CASE WHEN seloaponta_001 IS NULL OR seloaponta_001 = '' THEN 1 ELSE 0 END) AS semSelo
FROM ctp001
WHERE dataaponta_001 BETWEEN :dataInicial AND :dataFinal
GROUP BY status_001
ORDER BY quantidade DESC;

-- 5. VERIFICAR APTIDÃO PARA SELAGEM
-- Status que permitem selagem: 01, 02, 06, 08, 11
SELECT 
    numapo1_001,
    numapo2_001,
    tipooper_001,
    dataaponta_001,
    status_001,
    seloaponta_001,
    CASE 
        WHEN status_001 IN ('01', '02', '06', '08', '11') 
             AND (seloaponta_001 IS NULL OR seloaponta_001 = '')
        THEN 'APTO'
        ELSE 'INAPTO'
    END AS situacao_selagem
FROM ctp001
WHERE dataaponta_001 = :data
  AND tipooper_001 = :tipoOperacao;

-- 6. ESTATÍSTICAS POR TIPO DE OPERAÇÃO
SELECT 
    tipooper_001 AS tipo_operacao,
    COUNT(*) AS total_apontamentos,
    SUM(CASE WHEN seloaponta_001 IS NOT NULL AND seloaponta_001 != '' THEN 1 ELSE 0 END) AS selados,
    MIN(dataaponta_001) AS data_mais_antiga,
    MAX(dataaponta_001) AS data_mais_recente
FROM ctp001
WHERE dataaponta_001 BETWEEN :dataInicial AND :dataFinal
GROUP BY tipooper_001
ORDER BY total_apontamentos DESC;

-- 7. BUSCA COM PAGINAÇÃO (para interface)
SELECT * FROM (
    SELECT 
        numapo1_001,
        numapo2_001,
        tipooper_001,
        dataaponta_001,
        status_001,
        seloaponta_001,
        ROW_NUMBER() OVER (ORDER BY dataaponta_001 DESC) AS row_num
    FROM ctp001
    WHERE 1=1
        AND (:dataInicial IS NULL OR dataaponta_001 >= :dataInicial)
        AND (:dataFinal IS NULL OR dataaponta_001 <= :dataFinal)
) AS numbered_rows
WHERE row_num BETWEEN :inicio AND :fim;

-- 8. VERIFICAR APONTAMENTOS DUPLICADOS
SELECT 
    numapo1_001,
    numapo2_001,
    COUNT(*) AS ocorrencias
FROM ctp001
GROUP BY numapo1_001, numapo2_001
HAVING COUNT(*) > 1;

-- 9. APONTAMENTOS SEM SELO EM PERÍODO
SELECT 
    numapo1_001,
    numapo2_001,
    tipooper_001,
    dataaponta_001,
    status_001
FROM ctp001
WHERE dataaponta_001 BETWEEN :dataInicial AND :dataFinal
  AND (seloaponta_001 IS NULL OR seloaponta_001 = '')
  AND status_001 IN ('01', '02', '06', '08', '11')
ORDER BY dataaponta_001;

-- 10. HISTÓRICO DE ALTERAÇÕES DE SELO
-- (Assume tabela de histórico se existir)
SELECT 
    apontamento_id,
    selo_antigo,
    selo_novo,
    data_alteracao,
    usuario_alteracao
FROM historico_selos
WHERE data_alteracao BETWEEN :dataInicial AND :dataFinal
ORDER BY data_alteracao DESC;