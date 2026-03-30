-- ==============================================
-- SISTEMA SELADOR - ESQUEMA DO BANCO DE DADOS
-- Banco: MariaDB 10.5
-- Schema: spprot (existente)
-- ==============================================

-- ==============================================
-- 1. TABELA SELOS (selos)
-- Armazena os selos disponíveis para uso
-- ==============================================
CREATE TABLE IF NOT EXISTS selos (
    id_sel INT PRIMARY KEY AUTO_INCREMENT,
    selo_sel VARCHAR(20) NOT NULL UNIQUE,
    tiposelo_sel VARCHAR(4) NOT NULL DEFAULT '0004',
    tipo_sel VARCHAR(4) NOT NULL DEFAULT '0000',
    situacao_sel VARCHAR(20) NOT NULL DEFAULT 'DISPONIVEL',
    data_reserva_sel DATETIME NULL,
    data_utilizacao_sel DATETIME NULL,
    usuario_reserva_sel VARCHAR(50) NULL,
    usuario_utilizacao_sel VARCHAR(50) NULL,
    apontamento_utilizado_sel VARCHAR(100) NULL,
    codigo_tipo_ato_sel VARCHAR(3) NULL DEFAULT '701',
    
    -- Datas de auditoria
    data_criacao_sel DATETIME DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao_sel DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    
    -- Índices para performance
    INDEX idx_selo (selo_sel),
    INDEX idx_situacao (situacao_sel),
    INDEX idx_tiposelo (tiposelo_sel),
    INDEX idx_data_reserva (data_reserva_sel),
    INDEX idx_data_utilizacao (data_utilizacao_sel),
    INDEX idx_apontamento (apontamento_utilizado_sel),
    
    -- Restrições
    CONSTRAINT chk_situacao_valida 
        CHECK (situacao_sel IN ('DISPONIVEL', 'RESERVADO', 'UTILIZADO', 'CANCELADO', 'BLOQUEADO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================
-- 2. TABELA SELADOS (selados)
-- Histórico de selagens realizadas
-- ==============================================
CREATE TABLE IF NOT EXISTS selados (
    id_sld INT PRIMARY KEY AUTO_INCREMENT,
    
    -- Chave do apontamento
    numapo1_sld VARCHAR(10) NOT NULL,
    numapo2_sld VARCHAR(10) NOT NULL,
    
    -- Dados do selo utilizado
    selo_utilizado_sld VARCHAR(20) NOT NULL,
    tiposelo_sld VARCHAR(4) NOT NULL,
    
    -- Dados da operação
    tipo_operacao_sld VARCHAR(2) NULL,
    data_apontamento_sld DATE NULL,
    status_original_sld VARCHAR(2) NULL,
    
    -- Informações do processo
    idap_sld VARCHAR(100) NULL,
    data_selagem_sld DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_selagem_sld VARCHAR(50) NOT NULL DEFAULT 'SISTEMA',
    metodo_selagem_sld VARCHAR(20) NOT NULL DEFAULT 'AUTO',
    
    -- Status do processo
    status_selagem_sld VARCHAR(20) NOT NULL DEFAULT 'CONCLUIDO',
    mensagem_erro_sld TEXT NULL,
    
    -- Datas de auditoria
    data_criacao_sld DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    -- Índices para performance
    INDEX idx_apontamento_chave (numapo1_sld, numapo2_sld),
    INDEX idx_selo (selo_utilizado_sld),
    INDEX idx_data_selagem (data_selagem_sld),
    INDEX idx_usuario (usuario_selagem_sld),
    INDEX idx_status (status_selagem_sld),
    INDEX idx_data_apontamento (data_apontamento_sld),
    UNIQUE INDEX idx_selo_unico (selo_utilizado_sld),
    
    -- Chaves estrangeiras
    CONSTRAINT fk_selo_utilizado 
        FOREIGN KEY (selo_utilizado_sld) 
        REFERENCES selos(selo_sel)
        ON UPDATE CASCADE,
    
    -- Restrições
    CONSTRAINT chk_status_selagem_valido
        CHECK (status_selagem_sld IN ('CONCLUIDO', 'ERRO', 'CANCELADO', 'PENDENTE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================
-- 3. TABELA DE CONFIGURAÇÕES (config_selador)
-- Configurações do sistema
-- ==============================================
CREATE TABLE IF NOT EXISTS config_selador (
    id_config INT PRIMARY KEY AUTO_INCREMENT,
    chave_config VARCHAR(100) NOT NULL UNIQUE,
    valor_config TEXT NOT NULL,
    tipo_config VARCHAR(20) NOT NULL DEFAULT 'STRING',
    descricao_config TEXT NULL,
    categoria_config VARCHAR(50) NOT NULL DEFAULT 'GERAL',
    
    -- Controle de versão
    versao_config VARCHAR(20) NULL,
    data_alteracao_config DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    usuario_alteracao_config VARCHAR(50) NULL,
    
    -- Índices
    INDEX idx_chave (chave_config),
    INDEX idx_categoria (categoria_config),
    
    -- Restrições
    CONSTRAINT chk_tipo_valido
        CHECK (tipo_config IN ('STRING', 'INTEGER', 'BOOLEAN', 'JSON', 'DATE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================
-- 4. TABELA DE LOGS (logs_selador)
-- Logs de operações do sistema
-- ==============================================
CREATE TABLE IF NOT EXISTS logs_selador (
    id_log INT PRIMARY KEY AUTO_INCREMENT,
    nivel_log VARCHAR(10) NOT NULL,
    tipo_log VARCHAR(50) NOT NULL,
    mensagem_log TEXT NOT NULL,
    detalhes_log JSON NULL,
    
    -- Contexto da operação
    usuario_log VARCHAR(50) NULL,
    ip_log VARCHAR(45) NULL,
    sessao_log VARCHAR(100) NULL,
    
    -- Referência à operação
    operacao_id VARCHAR(100) NULL,
    referencia_log VARCHAR(100) NULL,
    
    -- Timestamps
    data_criacao_log DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    -- Índices
    INDEX idx_nivel (nivel_log),
    INDEX idx_tipo (tipo_log),
    INDEX idx_data (data_criacao_log),
    INDEX idx_usuario (usuario_log),
    INDEX idx_operacao (operacao_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================
-- 5. TABELA DE ESTATÍSTICAS (estatisticas_selador)
-- Estatísticas de uso do sistema
-- ==============================================
CREATE TABLE IF NOT EXISTS estatisticas_selador (
    id_est INT PRIMARY KEY AUTO_INCREMENT,
    data_est DATE NOT NULL,
    hora_est TIME NULL,
    
    -- Contadores
    total_apontamentos_est INT DEFAULT 0,
    aptos_selagem_est INT DEFAULT 0,
    selos_disponiveis_est INT DEFAULT 0,
    selos_utilizados_est INT DEFAULT 0,
    selagens_realizadas_est INT DEFAULT 0,
    selagens_erro_est INT DEFAULT 0,
    
    -- Tempos médios
    tempo_medio_busca_est DECIMAL(10,3) NULL,
    tempo_medio_selagem_est DECIMAL(10,3) NULL,
    
    -- Usuários
    usuarios_ativos_est INT DEFAULT 0,
    
    -- Metadata
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    -- Índices
    UNIQUE INDEX idx_data_hora (data_est, hora_est),
    INDEX idx_data (data_est)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================
-- 6. INSERIR DADOS INICIAIS
-- ==============================================

-- Configurações padrão
INSERT INTO config_selador (chave_config, valor_config, tipo_config, descricao_config, categoria_config) VALUES
('app.version', '1.0.0', 'STRING', 'Versão do Sistema Selador', 'SISTEMA'),
('selo.tipo.padrao', '0004', 'STRING', 'Tipo de selo padrão', 'SELOS'),
('selo.codigo.tipo.ato', '701', 'STRING', 'Código do tipo de ato', 'SELOS'),
('maker.integration.enabled', 'true', 'BOOLEAN', 'Habilita integração com Maker 5', 'INTEGRACAO'),
('api.cors.enabled', 'true', 'BOOLEAN', 'Habilita CORS para API', 'SEGURANCA'),
('validation.max.apontamentos.lote', '100', 'INTEGER', 'Máximo de apontamentos por lote', 'VALIDACAO'),
('log.level', 'INFO', 'STRING', 'Nível de logging padrão', 'LOGGING')
ON DUPLICATE KEY UPDATE 
    valor_config = VALUES(valor_config),
    data_alteracao_config = CURRENT_TIMESTAMP;

-- ==============================================
-- 7. PROCEDURES E FUNÇÕES ÚTEIS
-- ==============================================

DELIMITER //

-- Procedure para registrar selagem
CREATE PROCEDURE IF NOT EXISTS sp_registrar_selagem(
    IN p_numapo1 VARCHAR(10),
    IN p_numapo2 VARCHAR(10),
    IN p_selo VARCHAR(20),
    IN p_tiposelo VARCHAR(4),
    IN p_usuario VARCHAR(50)
)
BEGIN
    DECLARE v_selo_existe INT;
    
    -- Verificar se selo existe e está disponível
    SELECT COUNT(*) INTO v_selo_existe 
    FROM selos 
    WHERE selo_sel = p_selo 
      AND situacao_sel = 'DISPONIVEL';
    
    IF v_selo_existe = 0 THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Selo não encontrado ou não disponível';
    END IF;
    
    -- Inserir registro de selagem
    INSERT INTO selados (
        numapo1_sld,
        numapo2_sld,
        selo_utilizado_sld,
        tiposelo_sld,
        usuario_selagem_sld,
        data_selagem_sld
    ) VALUES (
        p_numapo1,
        p_numapo2,
        p_selo,
        p_tiposelo,
        p_usuario,
        NOW()
    );
    
    -- Atualizar status do selo
    UPDATE selos 
    SET 
        situacao_sel = 'UTILIZADO',
        data_utilizacao_sel = NOW(),
        usuario_utilizacao_sel = p_usuario,
        apontamento_utilizado_sel = CONCAT(p_numapo1, '/', p_numapo2)
    WHERE selo_sel = p_selo;
    
    -- Retornar sucesso
    SELECT 'Selagem registrada com sucesso' AS mensagem;
END //

-- Function para contar selos disponíveis
CREATE FUNCTION IF NOT EXISTS fn_contar_selos_disponiveis(p_tiposelo VARCHAR(4))
RETURNS INT
READS SQL DATA
BEGIN
    DECLARE v_total INT;
    
    SELECT COUNT(*) INTO v_total
    FROM selos
    WHERE tiposelo_sel = COALESCE(p_tiposelo, '0004')
      AND situacao_sel = 'DISPONIVEL';
    
    RETURN v_total;
END //

DELIMITER ;

-- ==============================================
-- 8. TRIGGERS PARA AUDITORIA
-- ==============================================

DELIMITER //

-- Trigger para log de alterações em selos
CREATE TRIGGER IF NOT EXISTS tr_selos_after_update
AFTER UPDATE ON selos
FOR EACH ROW
BEGIN
    IF OLD.situacao_sel != NEW.situacao_sel THEN
        INSERT INTO logs_selador (
            nivel_log,
            tipo_log,
            mensagem_log,
            detalhes_log,
            usuario_log
        ) VALUES (
            'INFO',
            'SELO_STATUS_CHANGE',
            CONCAT('Status do selo ', NEW.selo_sel, ' alterado de ', OLD.situacao_sel, ' para ', NEW.situacao_sel),
            JSON_OBJECT(
                'selo', NEW.selo_sel,
                'old_status', OLD.situacao_sel,
                'new_status', NEW.situacao_sel,
                'timestamp', NOW()
            ),
            NEW.usuario_utilizacao_sel
        );
    END IF;
END //

DELIMITER ;

-- ==============================================
-- 9. VIEWS PARA CONSULTAS COMUNS
-- ==============================================

-- View para relatório de selos
CREATE OR REPLACE VIEW vw_relatorio_selos AS
SELECT 
    tiposelo_sel AS tipo_selo,
    situacao_sel AS situacao,
    COUNT(*) AS quantidade,
    MIN(data_criacao_sel) AS data_mais_antiga,
    MAX(data_criacao_sel) AS data_mais_recente
FROM selos
GROUP BY tiposelo_sel, situacao_sel
ORDER BY tiposelo_sel, situacao_sel;

-- View para histórico de selagens
CREATE OR REPLACE VIEW vw_historico_selagens AS
SELECT 
    DATE(data_selagem_sld) AS data_selagem,
    usuario_selagem_sld AS usuario,
    COUNT(*) AS total_selagens,
    SUM(CASE WHEN status_selagem_sld = 'CONCLUIDO' THEN 1 ELSE 0 END) AS sucessos,
    SUM(CASE WHEN status_selagem_sld = 'ERRO' THEN 1 ELSE 0 END) AS erros
FROM selados
GROUP BY DATE(data_selagem_sld), usuario_selagem_sld
ORDER BY data_selagem DESC;

-- ==============================================
-- 10. ÍNDICES ADICIONAIS PARA PERFORMANCE
-- ==============================================

-- Índices para consultas frequentes na ctp001 (se existir)
-- CREATE INDEX IF NOT EXISTS idx_ctp001_status ON ctp001(status_001);
-- CREATE INDEX IF NOT EXISTS idx_ctp001_data ON ctp001(dataaponta_001);
-- CREATE INDEX IF NOT EXISTS idx_ctp001_selo ON ctp001(seloaponta_001);

-- ==============================================
-- FIM DO SCRIPT DE SCHEMA
-- ==============================================