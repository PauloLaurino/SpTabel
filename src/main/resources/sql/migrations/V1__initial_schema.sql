-- ==============================================
-- SISTEMA SELADOR - MIGRAÇÃO INICIAL V1
-- Banco: MariaDB 10.5
-- Schema: spprot (existente) + tabelas do Selador
-- ==============================================

-- ==============================================
-- 1. TABELA DE SELOS (selos)
-- Armazena os selos disponíveis para uso
-- ==============================================
CREATE TABLE IF NOT EXISTS selos (
    -- Identificação
    id_sel INT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID único do selo',
    selo_sel VARCHAR(20) NOT NULL UNIQUE COMMENT 'Número do selo (ex: SELO00001)',
    
    -- Tipo e classificação
    tiposelo_sel VARCHAR(4) NOT NULL DEFAULT '0004' COMMENT 'Tipo do selo (ex: 0004)',
    tipo_sel VARCHAR(4) NOT NULL DEFAULT '0000' COMMENT 'Tipo adicional (ex: 0000)',
    codigo_tipo_ato_sel VARCHAR(3) NULL DEFAULT '701' COMMENT 'Código do tipo de ato',
    
    -- Situação atual
    situacao_sel VARCHAR(20) NOT NULL DEFAULT 'DISPONIVEL' 
        COMMENT 'Situação: DISPONIVEL, RESERVADO, UTILIZADO, CANCELADO, BLOQUEADO',
    
    -- Reserva
    data_reserva_sel DATETIME NULL COMMENT 'Data/hora da reserva',
    usuario_reserva_sel VARCHAR(50) NULL COMMENT 'Usuário que reservou',
    
    -- Utilização
    data_utilizacao_sel DATETIME NULL COMMENT 'Data/hora da utilização',
    usuario_utilizacao_sel VARCHAR(50) NULL COMMENT 'Usuário que utilizou',
    apontamento_utilizado_sel VARCHAR(100) NULL COMMENT 'Apontamento que utilizou (formato: 001/000001)',
    
    -- Datas de auditoria
    data_criacao_sel DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de criação do registro',
    data_atualizacao_sel DATETIME NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Data da última atualização',
    
    -- Comentários e observações
    observacao_sel TEXT NULL COMMENT 'Observações sobre o selo',
    
    -- Índices para performance
    INDEX idx_selo_numero (selo_sel),
    INDEX idx_situacao (situacao_sel),
    INDEX idx_tiposelo (tiposelo_sel),
    INDEX idx_tipo (tipo_sel),
    INDEX idx_data_reserva (data_reserva_sel),
    INDEX idx_data_utilizacao (data_utilizacao_sel),
    INDEX idx_apontamento_utilizado (apontamento_utilizado_sel),
    INDEX idx_usuario_reserva (usuario_reserva_sel),
    INDEX idx_usuario_utilizacao (usuario_utilizacao_sel),
    
    -- Restrições
    CONSTRAINT chk_situacao_valida 
        CHECK (situacao_sel IN ('DISPONIVEL', 'RESERVADO', 'UTILIZADO', 'CANCELADO', 'BLOQUEADO')),
    
    CONSTRAINT chk_formato_selo 
        CHECK (selo_sel REGEXP '^SELO[0-9]{5,20}$')
        
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Tabela de selos disponíveis para selagem de apontamentos';

-- ==============================================
-- 2. TABELA DE HISTÓRICO DE SELAGENS (selados)
-- Registra todas as selagens realizadas
-- ==============================================
CREATE TABLE IF NOT EXISTS selados (
    -- Identificação
    id_sld INT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID único da selagem',
    
    -- Chave do apontamento (relação com ctp001)
    numapo1_sld VARCHAR(10) NOT NULL COMMENT 'Primeira parte do número do apontamento (ex: 001)',
    numapo2_sld VARCHAR(10) NOT NULL COMMENT 'Segunda parte do número do apontamento (ex: 000001)',
    
    -- Selo utilizado (relação com selos)
    selo_utilizado_sld VARCHAR(20) NOT NULL COMMENT 'Número do selo utilizado',
    tiposelo_sld VARCHAR(4) NOT NULL COMMENT 'Tipo do selo utilizado',
    
    -- Dados do apontamento (cópia no momento da selagem)
    tipo_operacao_sld VARCHAR(2) NULL COMMENT 'Tipo de operação do apontamento',
    data_apontamento_sld DATE NULL COMMENT 'Data do apontamento',
    status_original_sld VARCHAR(2) NULL COMMENT 'Status original do apontamento',
    valor_apontamento_sld DECIMAL(15,2) NULL COMMENT 'Valor do apontamento',
    
    -- Identificador único da selagem (IDAP)
    idap_sld VARCHAR(100) NULL COMMENT 'IDAP: yyyyMMddTPnumapo2000000000000000000000',
    
    -- Informações do processo de selagem
    data_selagem_sld DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data/hora da selagem',
    usuario_selagem_sld VARCHAR(50) NOT NULL DEFAULT 'SISTEMA' COMMENT 'Usuário que realizou a selagem',
    metodo_selagem_sld VARCHAR(20) NOT NULL DEFAULT 'AUTO' COMMENT 'Método: AUTO, MANUAL, IMPORT',
    origem_selagem_sld VARCHAR(20) NOT NULL DEFAULT 'SISTEMA' COMMENT 'Origem: SISTEMA, MAKER, API',
    
    -- Status do processo
    status_selagem_sld VARCHAR(20) NOT NULL DEFAULT 'CONCLUIDO' 
        COMMENT 'Status: CONCLUIDO, ERRO, CANCELADO, PENDENTE',
    
    -- Detalhes do resultado
    mensagem_erro_sld TEXT NULL COMMENT 'Mensagem de erro (se houver)',
    detalhes_sld JSON NULL COMMENT 'Detalhes adicionais em JSON',
    
    -- Datas de auditoria
    data_criacao_sld DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de criação do registro',
    
    -- Índices para performance
    INDEX idx_apontamento_chave (numapo1_sld, numapo2_sld),
    INDEX idx_selo_utilizado (selo_utilizado_sld),
    INDEX idx_data_selagem (data_selagem_sld),
    INDEX idx_usuario_selagem (usuario_selagem_sld),
    INDEX idx_status_selagem (status_selagem_sld),
    INDEX idx_data_apontamento (data_apontamento_sld),
    INDEX idx_tipo_operacao (tipo_operacao_sld),
    INDEX idx_idap (idap_sld),
    INDEX idx_origem (origem_selagem_sld),
    
    -- Chaves estrangeiras
    CONSTRAINT fk_selo_utilizado 
        FOREIGN KEY (selo_utilizado_sld) 
        REFERENCES selos(selo_sel)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    
    -- Restrições
    CONSTRAINT chk_status_selagem_valido
        CHECK (status_selagem_sld IN ('CONCLUIDO', 'ERRO', 'CANCELADO', 'PENDENTE', 'REVERTIDO')),
    
    CONSTRAINT chk_metodo_valido
        CHECK (metodo_selagem_sld IN ('AUTO', 'MANUAL', 'IMPORT', 'BATCH')),
    
    CONSTRAINT chk_origem_valida
        CHECK (origem_selagem_sld IN ('SISTEMA', 'MAKER', 'API', 'CONSOLE', 'SCRIPT')),
    
    -- Garantir selo único por selagem
    UNIQUE INDEX idx_selo_unico_por_selagem (selo_utilizado_sld)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Histórico de selagens realizadas no sistema';

-- ==============================================
-- 3. TABELA DE CONFIGURAÇÕES (config_selador)
-- Configurações do sistema (chave-valor)
-- ==============================================
CREATE TABLE IF NOT EXISTS config_selador (
    -- Identificação
    id_config INT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID único da configuração',
    chave_config VARCHAR(100) NOT NULL UNIQUE COMMENT 'Chave da configuração (ex: app.version)',
    
    -- Valor e tipo
    valor_config TEXT NOT NULL COMMENT 'Valor da configuração',
    tipo_config VARCHAR(20) NOT NULL DEFAULT 'STRING' COMMENT 'Tipo: STRING, INTEGER, BOOLEAN, JSON, DATE',
    
    -- Metadados
    descricao_config TEXT NULL COMMENT 'Descrição da configuração',
    categoria_config VARCHAR(50) NOT NULL DEFAULT 'GERAL' COMMENT 'Categoria: GERAL, BANCO, SEGURANCA, LOG, EMAIL',
    grupo_config VARCHAR(50) NULL COMMENT 'Grupo (para organização)',
    
    -- Controle de versão
    versao_config VARCHAR(20) NULL COMMENT 'Versão da configuração',
    data_alteracao_config DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP 
        COMMENT 'Data da última alteração',
    usuario_alteracao_config VARCHAR(50) NULL COMMENT 'Usuário que alterou',
    
    -- Validações
    valor_padrao_config TEXT NULL COMMENT 'Valor padrão',
    regex_validacao_config VARCHAR(200) NULL COMMENT 'Regex para validação',
    valores_permitidos_config TEXT NULL COMMENT 'Valores permitidos (separados por vírgula)',
    
    -- Índices
    INDEX idx_chave (chave_config),
    INDEX idx_categoria (categoria_config),
    INDEX idx_grupo (grupo_config),
    INDEX idx_data_alteracao (data_alteracao_config),
    
    -- Restrições
    CONSTRAINT chk_tipo_config_valido
        CHECK (tipo_config IN ('STRING', 'INTEGER', 'BOOLEAN', 'JSON', 'DATE', 'DECIMAL', 'LIST')),
    
    CONSTRAINT chk_categoria_valida
        CHECK (categoria_config IN ('GERAL', 'BANCO', 'SEGURANCA', 'LOG', 'EMAIL', 'SELO', 'API', 'INTERFACE'))
        
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Configurações do sistema Selador (chave-valor)';

-- ==============================================
-- 4. TABELA DE LOGS DO SISTEMA (logs_selador)
-- Logs de operações e auditoria
-- ==============================================
CREATE TABLE IF NOT EXISTS logs_selador (
    -- Identificação
    id_log INT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID único do log',
    
    -- Informações do log
    nivel_log VARCHAR(10) NOT NULL COMMENT 'Nível: DEBUG, INFO, WARN, ERROR, FATAL',
    tipo_log VARCHAR(50) NOT NULL COMMENT 'Tipo: SELAGEM, RESERVA, CONFIG, LOGIN, ERRO',
    modulo_log VARCHAR(50) NULL COMMENT 'Módulo: APONTAMENTO, SELO, SELAGEM, CONFIG',
    
    -- Mensagem
    mensagem_log TEXT NOT NULL COMMENT 'Mensagem do log',
    detalhes_log JSON NULL COMMENT 'Detalhes adicionais em JSON',
    stack_trace_log TEXT NULL COMMENT 'Stack trace (para erros)',
    
    -- Contexto da operação
    usuario_log VARCHAR(50) NULL COMMENT 'Usuário relacionado',
    ip_log VARCHAR(45) NULL COMMENT 'IP de origem',
    sessao_log VARCHAR(100) NULL COMMENT 'ID da sessão',
    user_agent_log VARCHAR(500) NULL COMMENT 'User agent do navegador',
    
    -- Referência à operação
    operacao_id VARCHAR(100) NULL COMMENT 'ID da operação relacionada',
    referencia_log VARCHAR(100) NULL COMMENT 'Referência (ex: número do apontamento)',
    transacao_id VARCHAR(100) NULL COMMENT 'ID da transação',
    
    -- Timestamps
    data_criacao_log DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de criação do log',
    
    -- Índices
    INDEX idx_nivel (nivel_log),
    INDEX idx_tipo (tipo_log),
    INDEX idx_modulo (modulo_log),
    INDEX idx_data_criacao (data_criacao_log),
    INDEX idx_usuario (usuario_log),
    INDEX idx_operacao (operacao_id),
    INDEX idx_referencia (referencia_log),
    
    -- Restrições
    CONSTRAINT chk_nivel_valido
        CHECK (nivel_log IN ('DEBUG', 'INFO', 'WARN', 'ERROR', 'FATAL', 'TRACE')),
    
    CONSTRAINT chk_tipo_valido
        CHECK (tipo_log IN (
            'SELAGEM', 'RESERVA_SELO', 'LIBERACAO_SELO', 'CONSULTA', 
            'CONFIGURACAO', 'LOGIN', 'LOGOUT', 'ERRO', 'AUDITORIA',
            'INTEGRACAO', 'BACKUP', 'RESTAURACAO', 'PERFORMANCE'
        ))
        
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Logs de operações do sistema Selador';

-- ==============================================
-- 5. TABELA DE ESTATÍSTICAS (estatisticas_selador)
-- Estatísticas de uso e performance
-- ==============================================
CREATE TABLE IF NOT EXISTS estatisticas_selador (
    -- Identificação
    id_est INT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID único da estatística',
    data_est DATE NOT NULL COMMENT 'Data da estatística',
    hora_est TIME NULL COMMENT 'Hora (para estatísticas por hora)',
    periodo_est VARCHAR(20) DEFAULT 'DIARIO' COMMENT 'Período: DIARIO, HORARIO, SEMANAL, MENSAL',
    
    -- Contadores de apontamentos
    total_apontamentos_est INT DEFAULT 0 COMMENT 'Total de apontamentos processados',
    aptos_selagem_est INT DEFAULT 0 COMMENT 'Apontamentos aptos para selagem',
    inaptos_selagem_est INT DEFAULT 0 COMMENT 'Apontamentos inaptos para selagem',
    ja_selados_est INT DEFAULT 0 COMMENT 'Apontamentos já selados',
    
    -- Contadores de selos
    selos_disponiveis_est INT DEFAULT 0 COMMENT 'Selos disponíveis',
    selos_reservados_est INT DEFAULT 0 COMMENT 'Selos reservados',
    selos_utilizados_est INT DEFAULT 0 COMMENT 'Selos utilizados',
    selos_importados_est INT DEFAULT 0 COMMENT 'Selos importados',
    
    -- Contadores de selagem
    selagens_realizadas_est INT DEFAULT 0 COMMENT 'Selagens realizadas',
    selagens_sucesso_est INT DEFAULT 0 COMMENT 'Selagens com sucesso',
    selagens_erro_est INT DEFAULT 0 COMMENT 'Selagens com erro',
    selagens_canceladas_est INT DEFAULT 0 COMMENT 'Selagens canceladas',
    
    -- Métricas de performance
    tempo_medio_busca_est DECIMAL(10,3) NULL COMMENT 'Tempo médio de busca (ms)',
    tempo_medio_selagem_est DECIMAL(10,3) NULL COMMENT 'Tempo médio de selagem (ms)',
    tempo_maximo_selagem_est DECIMAL(10,3) NULL COMMENT 'Tempo máximo de selagem (ms)',
    
    -- Usuários e sessões
    usuarios_ativos_est INT DEFAULT 0 COMMENT 'Usuários ativos',
    sessoes_ativas_est INT DEFAULT 0 COMMENT 'Sessões ativas',
    acessos_api_est INT DEFAULT 0 COMMENT 'Acessos à API',
    
    -- Métricas de qualidade
    taxa_sucesso_est DECIMAL(5,2) NULL COMMENT 'Taxa de sucesso (%)',
    taxa_erro_est DECIMAL(5,2) NULL COMMENT 'Taxa de erro (%)',
    disponibilidade_est DECIMAL(5,2) NULL COMMENT 'Disponibilidade do sistema (%)',
    
    -- Metadata
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de criação do registro',
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Data de atualização',
    
    -- Índices
    UNIQUE INDEX idx_data_hora_periodo (data_est, hora_est, periodo_est),
    INDEX idx_data (data_est),
    INDEX idx_periodo (periodo_est),
    INDEX idx_taxa_sucesso (taxa_sucesso_est),
    
    -- Restrições
    CONSTRAINT chk_periodo_valido
        CHECK (periodo_est IN ('DIARIO', 'HORARIO', 'SEMANAL', 'MENSAL', 'ANUAL'))
        
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Estatísticas de uso e performance do sistema';

-- ==============================================
-- 6. TABELA DE USUÁRIOS (usuarios_selador) - OPCIONAL
-- Usuários do sistema (se autenticação for habilitada)
-- ==============================================
CREATE TABLE IF NOT EXISTS usuarios_selador (
    id_usr INT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID único do usuário',
    usuario_usr VARCHAR(50) NOT NULL UNIQUE COMMENT 'Nome de usuário',
    nome_usr VARCHAR(100) NOT NULL COMMENT 'Nome completo',
    email_usr VARCHAR(100) NOT NULL UNIQUE COMMENT 'E-mail',
    
    -- Credenciais
    senha_usr VARCHAR(255) NOT NULL COMMENT 'Senha criptografada',
    salt_usr VARCHAR(50) NOT NULL COMMENT 'Salt para criptografia',
    
    -- Perfil e permissões
    perfil_usr VARCHAR(20) NOT NULL DEFAULT 'OPERADOR' COMMENT 'Perfil: ADMIN, SUPERVISOR, OPERADOR, CONSULTA',
    permissoes_usr JSON NULL COMMENT 'Permissões específicas em JSON',
    
    -- Status
    ativo_usr BOOLEAN DEFAULT TRUE COMMENT 'Usuário ativo',
    bloqueado_usr BOOLEAN DEFAULT FALSE COMMENT 'Usuário bloqueado',
    motivo_bloqueio_usr TEXT NULL COMMENT 'Motivo do bloqueio',
    
    -- Datas
    data_criacao_usr DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de criação',
    data_ultimo_login_usr DATETIME NULL COMMENT 'Data do último login',
    data_ultima_alteracao_usr DATETIME NULL COMMENT 'Data da última alteração',
    data_expiracao_senha_usr DATE NULL COMMENT 'Data de expiração da senha',
    
    -- Contadores
    tentativas_login_usr INT DEFAULT 0 COMMENT 'Tentativas de login falhas',
    total_logins_usr INT DEFAULT 0 COMMENT 'Total de logins realizados',
    
    -- Índices
    INDEX idx_usuario (usuario_usr),
    INDEX idx_email (email_usr),
    INDEX idx_perfil (perfil_usr),
    INDEX idx_ativo (ativo_usr),
    INDEX idx_data_criacao (data_criacao_usr),
    
    -- Restrições
    CONSTRAINT chk_perfil_valido
        CHECK (perfil_usr IN ('ADMIN', 'SUPERVISOR', 'OPERADOR', 'CONSULTA', 'AUDITOR'))
        
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Usuários do sistema Selador (se autenticação for habilitada)';

-- ==============================================
-- 7. TABELA DE ERROS CONHECIDOS (erros_selador)
-- Catálogo de erros do sistema
-- ==============================================
CREATE TABLE IF NOT EXISTS erros_selador (
    id_err INT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID único do erro',
    codigo_err VARCHAR(50) NOT NULL UNIQUE COMMENT 'Código do erro (ex: SEL-001)',
    
    -- Descrição
    titulo_err VARCHAR(200) NOT NULL COMMENT 'Título do erro',
    descricao_err TEXT NOT NULL COMMENT 'Descrição detalhada',
    categoria_err VARCHAR(50) NOT NULL COMMENT 'Categoria: VALIDACAO, BANCO, INTEGRACAO, SISTEMA',
    
    -- Solução
    causa_err TEXT NULL COMMENT 'Causa provável',
    solucao_err TEXT NULL COMMENT 'Solução recomendada',
    acao_usuario_err TEXT NULL COMMENT 'Ação que o usuário deve tomar',
    
    -- Impacto
    severidade_err VARCHAR(20) NOT NULL DEFAULT 'MEDIA' COMMENT 'Severidade: BAIXA, MEDIA, ALTA, CRITICA',
    impacto_err TEXT NULL COMMENT 'Impacto no sistema',
    
    -- Metadados
    modulo_err VARCHAR(50) NULL COMMENT 'Módulo onde ocorre',
    versao_corrigida_err VARCHAR(20) NULL COMMENT 'Versão onde foi corrigido',
    
    -- Contadores
    ocorrencias_err INT DEFAULT 0 COMMENT 'Número de ocorrências',
    primeira_ocorrencia_err DATETIME NULL COMMENT 'Data da primeira ocorrência',
    ultima_ocorrencia_err DATETIME NULL COMMENT 'Data da última ocorrência',
    
    -- Datas
    data_criacao_err DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de criação',
    data_atualizacao_err DATETIME NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Data de atualização',
    
    -- Índices
    INDEX idx_codigo (codigo_err),
    INDEX idx_categoria (categoria_err),
    INDEX idx_severidade (severidade_err),
    INDEX idx_modulo (modulo_err),
    INDEX idx_ocorrencias (ocorrencias_err),
    
    -- Restrições
    CONSTRAINT chk_severidade_valida
        CHECK (severidade_err IN ('BAIXA', 'MEDIA', 'ALTA', 'CRITICA')),
    
    CONSTRAINT chk_categoria_valida
        CHECK (categoria_err IN ('VALIDACAO', 'BANCO', 'INTEGRACAO', 'SISTEMA', 'SEGURANCA', 'PERFORMANCE'))
        
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Catálogo de erros conhecidos do sistema';

-- ==============================================
-- 8. TABELA DE INTEGRAÇÃO MAKER (integracao_maker)
-- Registro de integrações com o Maker 5
-- ==============================================
CREATE TABLE IF NOT EXISTS integracao_maker (
    id_int INT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID único da integração',
    
    -- Dados da chamada
    data_chamada_int DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Data/hora da chamada',
    funcao_chamada_int VARCHAR(50) NOT NULL COMMENT 'Função chamada (ex: sprChamaSelador)',
    
    -- Parâmetros recebidos
    parametro_data_int VARCHAR(8) NULL COMMENT 'Parâmetro data (AAAAMMDD)',
    parametro_tipo_int VARCHAR(2) NULL COMMENT 'Parâmetro tipo operação',
    parametros_extras_int JSON NULL COMMENT 'Parâmetros extras em JSON',
    
    -- Origem
    ip_origem_int VARCHAR(45) NULL COMMENT 'IP de origem',
    user_agent_int VARCHAR(500) NULL COMMENT 'User agent',
    sessao_maker_int VARCHAR(100) NULL COMMENT 'Sessão do Maker',
    
    -- Resultado
    sucesso_int BOOLEAN DEFAULT TRUE COMMENT 'Integração bem-sucedida',
    mensagem_erro_int TEXT NULL COMMENT 'Mensagem de erro (se houver)',
    tempo_resposta_int DECIMAL(10,3) NULL COMMENT 'Tempo de resposta (ms)',
    
    -- Resultados da selagem (se aplicável)
    apontamentos_processados_int INT DEFAULT 0 COMMENT 'Apontamentos processados',
    selagens_realizadas_int INT DEFAULT 0 COMMENT 'Selagens realizadas',
    selos_utilizados_int INT DEFAULT 0 COMMENT 'Selos utilizados',
    
    -- Metadata
    data_finalizacao_int DATETIME NULL COMMENT 'Data/hora da finalização',
    
    -- Índices
    INDEX idx_data_chamada (data_chamada_int),
    INDEX idx_funcao (funcao_chamada_int),
    INDEX idx_sucesso (sucesso_int),
    INDEX idx_ip_origem (ip_origem_int),
    
    -- Restrições
    CONSTRAINT chk_funcao_valida
        CHECK (funcao_chamada_int IN ('sprChamaSelador', 'sprFechaSelador', 'sprVerificarDisponibilidade'))
        
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Registro de integrações com o Maker 5';

-- ==============================================
-- 9. INSERIR DADOS INICIAIS
-- ==============================================

-- Configurações padrão do sistema
INSERT INTO config_selador (chave_config, valor_config, tipo_config, descricao_config, categoria_config, valor_padrao_config) VALUES
-- Sistema
('app.name', 'Sistema Selador', 'STRING', 'Nome da aplicação', 'GERAL', 'Sistema Selador'),
('app.version', '1.0.0', 'STRING', 'Versão do sistema', 'GERAL', '1.0.0'),
('app.environment', 'production', 'STRING', 'Ambiente: development, testing, production', 'GERAL', 'production'),

-- Banco de dados
('db.driver', 'com.mysql.cj.jdbc.Driver', 'STRING', 'Driver JDBC do banco', 'BANCO', 'com.mysql.cj.jdbc.Driver'),
('db.pool.max.size', '10', 'INTEGER', 'Tamanho máximo do pool de conexões', 'BANCO', '10'),
('db.pool.min.idle', '5', 'INTEGER', 'Mínimo de conexões idle no pool', 'BANCO', '5'),

-- Selos
('selo.tipo.padrao', '0004', 'STRING', 'Tipo de selo padrão', 'SELO', '0004'),
('selo.codigo.tipo.ato', '701', 'STRING', 'Código do tipo de ato padrão', 'SELO', '701'),
('selo.quantidade.minima', '10', 'INTEGER', 'Quantidade mínima de selos para alerta', 'SELO', '10'),
('selo.alerta.quantidade', '20', 'INTEGER', 'Quantidade para alerta de estoque baixo', 'SELO', '20'),

-- Integração Maker
('maker.integration.enabled', 'true', 'BOOLEAN', 'Habilita integração com Maker 5', 'INTEGRACAO', 'true'),
('maker.base.url', 'http://localhost:5000/webrun5', 'STRING', 'URL base do Maker 5', 'INTEGRACAO', 'http://localhost:5000/webrun5'),
('maker.api.timeout', '30000', 'INTEGER', 'Timeout para chamadas ao Maker (ms)', 'INTEGRACAO', '30000'),

-- API
('api.cors.enabled', 'true', 'BOOLEAN', 'Habilita CORS para API', 'API', 'true'),
('api.cors.allowed.origins', '*', 'STRING', 'Origens permitidas no CORS', 'API', '*'),
('api.cors.allowed.methods', 'GET,POST,PUT,DELETE,OPTIONS', 'STRING', 'Métodos HTTP permitidos', 'API', 'GET,POST,PUT,DELETE,OPTIONS'),
('api.default.timeout', '30000', 'INTEGER', 'Timeout padrão da API (ms)', 'API', '30000'),

-- Validações
('validation.max.apontamentos.lote', '100', 'INTEGER', 'Máximo de apontamentos por lote', 'VALIDACAO', '100'),
('validation.data.max.dias', '365', 'INTEGER', 'Máximo de dias para busca de apontamentos', 'VALIDACAO', '365'),
('validation.retry.attempts', '3', 'INTEGER', 'Número de tentativas de retry', 'VALIDACAO', '3'),

-- Logging
('log.level', 'INFO', 'STRING', 'Nível de log padrão', 'LOG', 'INFO'),
('log.directory', 'logs', 'STRING', 'Diretório para arquivos de log', 'LOG', 'logs'),
('log.max.size.mb', '10', 'INTEGER', 'Tamanho máximo do arquivo de log (MB)', 'LOG', '10'),
('log.retention.days', '30', 'INTEGER', 'Dias de retenção dos logs', 'LOG', '30'),

-- Segurança
('security.auth.enabled', 'false', 'BOOLEAN', 'Habilita autenticação no sistema', 'SEGURANCA', 'false'),
('security.auth.token.header', 'X-API-Key', 'STRING', 'Header para token de API', 'SEGURANCA', 'X-API-Key'),
('security.cors.enabled', 'true', 'BOOLEAN', 'Habilita CORS para segurança', 'SEGURANCA', 'true'),

-- Performance
('performance.query.timeout', '30', 'INTEGER', 'Timeout para queries (segundos)', 'PERFORMANCE', '30'),
('performance.connection.pool.size', '10', 'INTEGER', 'Tamanho do pool de conexões', 'PERFORMANCE', '10'),
('performance.max.threads', '50', 'INTEGER', 'Máximo de threads simultâneas', 'PERFORMANCE', '50'),

-- Interface
('interface.theme', 'light', 'STRING', 'Tema da interface: light, dark', 'INTERFACE', 'light'),
('interface.language', 'pt_BR', 'STRING', 'Idioma da interface', 'INTERFACE', 'pt_BR'),
('interface.default.date.format', 'DD/MM/AAAA', 'STRING', 'Formato padrão de data', 'INTERFACE', 'DD/MM/AAAA'),

-- Notificações
('notification.enabled', 'false', 'BOOLEAN', 'Habilita notificações', 'NOTIFICACAO', 'false'),
('notification.email.host', 'smtp.gmail.com', 'STRING', 'Host do servidor SMTP', 'NOTIFICACAO', 'smtp.gmail.com'),
('notification.email.port', '587', 'INTEGER', 'Porta do servidor SMTP', 'NOTIFICACAO', '587'),

-- Backup
('backup.enabled', 'true', 'BOOLEAN', 'Habilita backup automático', 'BACKUP', 'true'),
('backup.cron.expression', '0 2 * * *', 'STRING', 'Expressão Cron para backup', 'BACKUP', '0 2 * * *'),
('backup.retention.days', '30', 'INTEGER', 'Dias de retenção dos backups', 'BACKUP', '30'),

-- Status de apontamentos que permitem selagem
('status.aptos.selagem', '01,02,06,08,11', 'STRING', 'Status que permitem selagem', 'VALIDACAO', '01,02,06,08,11'),
('status.inaptos.selagem', '03,04,07,09,10', 'STRING', 'Status que NÃO permitem selagem', 'VALIDACAO', '03,04,07,09,10')

ON DUPLICATE KEY UPDATE 
    valor_config = VALUES(valor_config),
    data_alteracao_config = CURRENT_TIMESTAMP,
    usuario_alteracao_config = 'SYSTEM';

-- ==============================================
-- 10. PROCEDURES E FUNÇÕES ÚTEIS
-- ==============================================

DELIMITER //

-- Procedure para registrar selagem
CREATE PROCEDURE IF NOT EXISTS sp_registrar_selagem(
    IN p_numapo1 VARCHAR(10),
    IN p_numapo2 VARCHAR(10),
    IN p_selo VARCHAR(20),
    IN p_tiposelo VARCHAR(4),
    IN p_usuario VARCHAR(50),
    IN p_metodo VARCHAR(20),
    IN p_origem VARCHAR(20)
)
BEGIN
    DECLARE v_selo_existe INT;
    DECLARE v_idap VARCHAR(100);
    DECLARE v_data_apontamento DATE;
    DECLARE v_tipo_operacao VARCHAR(2);
    DECLARE v_status_original VARCHAR(2);
    
    -- Verificar se selo existe e está disponível
    SELECT COUNT(*) INTO v_selo_existe 
    FROM selos 
    WHERE selo_sel = p_selo 
      AND situacao_sel = 'DISPONIVEL';
    
    IF v_selo_existe = 0 THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'SELO_NAO_DISPONIVEL: Selo não encontrado ou não disponível';
    END IF;
    
    -- Obter dados do apontamento (se existir na ctp001)
    SELECT 
        dataaponta_001,
        tipooper_001,
        status_001
    INTO 
        v_data_apontamento,
        v_tipo_operacao,
        v_status_original
    FROM ctp001 
    WHERE numapo1_001 = p_numapo1 
      AND numapo2_001 = p_numapo2;
    
    -- Gerar IDAP
    SET v_idap = CONCAT(
        DATE_FORMAT(NOW(), '%Y%m%d'),
        'TP',
        LPAD(p_numapo2, 10, '0'),
        '00000000000000000000'
    );
    
    -- Iniciar transação
    START TRANSACTION;
    
    -- Inserir registro de selagem
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
        origem_selagem_sld,
        status_selagem_sld,
        data_selagem_sld
    ) VALUES (
        p_numapo1,
        p_numapo2,
        p_selo,
        p_tiposelo,
        v_tipo_operacao,
        v_data_apontamento,
        v_status_original,
        v_idap,
        p_usuario,
        p_metodo,
        p_origem,
        'CONCLUIDO',
        NOW()
    );
    
    -- Atualizar status do selo
    UPDATE selos 
    SET 
        situacao_sel = 'UTILIZADO',
        data_utilizacao_sel = NOW(),
        usuario_utilizacao_sel = p_usuario,
        apontamento_utilizado_sel = CONCAT(p_numapo1, '/', p_numapo2),
        data_atualizacao_sel = NOW()
    WHERE selo_sel = p_selo;
    
    -- Atualizar apontamento na ctp001 (se existir)
    UPDATE ctp001 
    SET 
        seloaponta_001 = p_selo,
        data_ultima_atualizacao = NOW()
    WHERE numapo1_001 = p_numapo1 
      AND numapo2_001 = p_numapo2;
    
    -- Registrar log
    INSERT INTO logs_selador (
        nivel_log,
        tipo_log,
        modulo_log,
        mensagem_log,
        detalhes_log,
        usuario_log,
        operacao_id,
        referencia_log
    ) VALUES (
        'INFO',
        'SELAGEM',
        'SELAGEM',
        CONCAT('Selagem realizada: ', p_numapo1, '/', p_numapo2, ' com selo ', p_selo),
        JSON_OBJECT(
            'numapo1', p_numapo1,
            'numapo2', p_numapo2,
            'selo', p_selo,
            'usuario', p_usuario,
            'metodo', p_metodo,
            'origem', p_origem,
            'idap', v_idap
        ),
        p_usuario,
        v_idap,
        CONCAT(p_numapo1, '/', p_numapo2)
    );
    
    -- Commit da transação
    COMMIT;
    
    -- Retornar sucesso
    SELECT 
        'Selagem registrada com sucesso' AS mensagem,
        v_idap AS idap,
        p_selo AS selo_utilizado,
        CONCAT(p_numapo1, '/', p_numapo2) AS apontamento;
END //

-- Function para contar selos disponíveis por tipo
CREATE FUNCTION IF NOT EXISTS fn_contar_selos_disponiveis(
    p_tiposelo VARCHAR(4),
    p_tipo VARCHAR(4)
)
RETURNS INT
READS SQL DATA
BEGIN
    DECLARE v_total INT;
    
    SELECT COUNT(*) INTO v_total
    FROM selos
    WHERE tiposelo_sel = COALESCE(p_tiposelo, '0004')
      AND tipo_sel = COALESCE(p_tipo, '0000')
      AND situacao_sel = 'DISPONIVEL';
    
    RETURN v_total;
END //

-- Procedure para liberar selos reservados vencidos
CREATE PROCEDURE IF NOT EXISTS sp_liberar_selos_reservados_vencidos()
BEGIN
    DECLARE v_liberados INT DEFAULT 0;
    
    -- Liberar selos reservados há mais de 10 minutos
    UPDATE selos 
    SET 
        situacao_sel = 'DISPONIVEL',
        data_reserva_sel = NULL,
        usuario_reserva_sel = NULL,
        data_atualizacao_sel = NOW()
    WHERE situacao_sel = 'RESERVADO'
      AND data_reserva_sel < DATE_SUB(NOW(), INTERVAL 10 MINUTE);
    
    SET v_liberados = ROW_COUNT();
    
    -- Registrar log se houve liberações
    IF v_liberados > 0 THEN
        INSERT INTO logs_selador (
            nivel_log,
            tipo_log,
            modulo_log,
            mensagem_log,
            detalhes_log
        ) VALUES (
            'INFO',
            'MANUTENCAO',
            'SELO',
            CONCAT('Liberados ', v_liberados, ' selos reservados vencidos'),
            JSON_OBJECT('selos_liberados', v_liberados)
        );
    END IF;
    
    SELECT v_liberados AS selos_liberados;
END //

-- Function para gerar IDAP
CREATE FUNCTION IF NOT EXISTS fn_gerar_idap(
    p_data DATE,
    p_numapo2 VARCHAR(10)
)
RETURNS VARCHAR(100)
DETERMINISTIC
BEGIN
    RETURN CONCAT(
        DATE_FORMAT(p_data, '%Y%m%d'),
        'TP',
        LPAD(p_numapo2, 10, '0'),
        '00000000000000000000'
    );
END //

DELIMITER ;

-- ==============================================
-- 11. TRIGGERS PARA AUDITORIA E INTEGRIDADE
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
            modulo_log,
            mensagem_log,
            detalhes_log,
            usuario_log
        ) VALUES (
            'INFO',
            'AUDITORIA',
            'SELO',
            CONCAT('Status do selo ', NEW.selo_sel, ' alterado de ', OLD.situacao_sel, ' para ', NEW.situacao_sel),
            JSON_OBJECT(
                'selo', NEW.selo_sel,
                'old_status', OLD.situacao_sel,
                'new_status', NEW.situacao_sel,
                'timestamp', NOW()
            ),
            COALESCE(NEW.usuario_utilizacao_sel, NEW.usuario_reserva_sel, 'SYSTEM')
        );
    END IF;
END //

-- Trigger para log de inserção de selagens
CREATE TRIGGER IF NOT EXISTS tr_selados_after_insert
AFTER INSERT ON selados
FOR EACH ROW
BEGIN
    INSERT INTO logs_selador (
        nivel_log,
        tipo_log,
        modulo_log,
        mensagem_log,
        detalhes_log,
        usuario_log,
        operacao_id,
        referencia_log
    ) VALUES (
        'INFO',
        'SELAGEM',
        'SELAGEM',
        CONCAT('Selagem registrada: ', NEW.numapo1_sld, '/', NEW.numapo2_sld),
        JSON_OBJECT(
            'id_sld', NEW.id_sld,
            'selo', NEW.selo_utilizado_sld,
            'usuario', NEW.usuario_selagem_sld,
            'metodo', NEW.metodo_selagem_sld,
            'status', NEW.status_selagem_sld,
            'idap', NEW.idap_sld
        ),
        NEW.usuario_selagem_sld,
        NEW.idap_sld,
        CONCAT(NEW.numapo1_sld, '/', NEW.numapo2_sld)
    );
END //

-- Trigger para atualizar estatísticas após selagem
CREATE TRIGGER IF NOT EXISTS tr_selados_after_insert_estatisticas
AFTER INSERT ON selados
FOR EACH ROW
BEGIN
    DECLARE v_data_atual DATE;
    SET v_data_atual = DATE(NEW.data_selagem_sld);
    
    -- Atualizar estatísticas do dia
    INSERT INTO estatisticas_selador (
        data_est,
        periodo_est,
        selagens_realizadas_est,
        selagens_sucesso_est,
        selos_utilizados_est,
        updated_at
    ) VALUES (
        v_data_atual,
        'DIARIO',
        1,
        IF(NEW.status_selagem_sld = 'CONCLUIDO', 1, 0),
        1,
        NOW()
    )
    ON DUPLICATE KEY UPDATE
        selagens_realizadas_est = selagens_realizadas_est + 1,
        selagens_sucesso_est = selagens_sucesso_est + IF(NEW.status_selagem_sld = 'CONCLUIDO', 1, 0),
        selos_utilizados_est = selos_utilizados_est + 1,
        updated_at = NOW();
END //

DELIMITER ;

-- ==============================================
-- 12. VIEWS PARA CONSULTAS COMUNS
-- ==============================================

-- View para relatório de selos por situação
CREATE OR REPLACE VIEW vw_relatorio_selos AS
SELECT 
    tiposelo_sel AS tipo_selo,
    tipo_sel AS tipo,
    situacao_sel AS situacao,
    COUNT(*) AS quantidade,
    MIN(data_criacao_sel) AS data_mais_antiga,
    MAX(data_criacao_sel) AS data_mais_recente,
    SUM(CASE WHEN situacao_sel = 'UTILIZADO' THEN 1 ELSE 0 END) AS total_utilizados,
    SUM(CASE WHEN situacao_sel = 'DISPONIVEL' THEN 1 ELSE 0 END) AS total_disponiveis
FROM selos
GROUP BY tiposelo_sel, tipo_sel, situacao_sel
ORDER BY tiposelo_sel, tipo_sel, situacao_sel;

-- View para histórico de selagens com detalhes
CREATE OR REPLACE VIEW vw_historico_selagens AS
SELECT 
    s.id_sld,
    s.numapo1_sld,
    s.numapo2_sld,
    CONCAT(s.numapo1_sld, '/', s.numapo2_sld) AS apontamento,
    s.selo_utilizado_sld AS selo,
    s.tiposelo_sld AS tipo_selo,
    s.tipo_operacao_sld AS tipo_operacao,
    s.data_apontamento_sld AS data_apontamento,
    s.status_original_sld AS status_original,
    s.idap_sld AS idap,
    DATE(s.data_selagem_sld) AS data_selagem,
    TIME(s.data_selagem_sld) AS hora_selagem,
    s.usuario_selagem_sld AS usuario,
    s.metodo_selagem_sld AS metodo,
    s.origem_selagem_sld AS origem,
    s.status_selagem_sld AS status,
    s.mensagem_erro_sld AS mensagem_erro,
    sl.situacao_sel AS situacao_atual_selo,
    a.status_001 AS status_atual_apontamento,
    a.seloaponta_001 AS selo_atual_apontamento
FROM selados s
LEFT JOIN selos sl ON s.selo_utilizado_sld = sl.selo_sel
LEFT JOIN ctp001 a ON s.numapo1_sld = a.numapo1_001 AND s.numapo2_sld = a.numapo2_001
ORDER BY s.data_selagem_sld DESC;

-- View para estatísticas diárias de selagem
CREATE OR REPLACE VIEW vw_estatisticas_diarias AS
SELECT 
    data_est AS data,
    periodo_est AS periodo,
    total_apontamentos_est AS total_apontamentos,
    aptos_selagem_est AS aptos_selagem,
    selos_disponiveis_est AS selos_disponiveis,
    selagens_realizadas_est AS selagens_realizadas,
    selagens_sucesso_est AS selagens_sucesso,
    selagens_erro_est AS selagens_erro,
    CASE 
        WHEN selagens_realizadas_est > 0 
        THEN ROUND((selagens_sucesso_est * 100.0 / selagens_realizadas_est), 2)
        ELSE 0 
    END AS taxa_sucesso_percentual,
    tempo_medio_selagem_est AS tempo_medio_selagem_ms,
    usuarios_ativos_est AS usuarios_ativos
FROM estatisticas_selador
WHERE periodo_est = 'DIARIO'
ORDER BY data_est DESC;

-- View para selos disponíveis ordenados
CREATE OR REPLACE VIEW vw_selos_disponiveis_ordenados AS
SELECT 
    selo_sel AS selo,
    tiposelo_sel AS tipo_selo,
    tipo_sel AS tipo,
    data_criacao_sel AS data_criacao,
    DATEDIFF(CURDATE(), DATE(data_criacao_sel)) AS dias_desde_criacao
FROM selos
WHERE situacao_sel = 'DISPONIVEL'
ORDER BY 
    tiposelo_sel,
    tipo_sel,
    data_criacao_sel ASC,
    selo_sel ASC;

-- ==============================================
-- 13. ÍNDICES ADICIONAIS PARA PERFORMANCE
-- ==============================================

-- Índices para consultas frequentes na ctp001
-- (Apenas se tiver permissão para criar índices na tabela do cliente)
-- CREATE INDEX IF NOT EXISTS idx_ctp001_status ON ctp001(status_001);
-- CREATE INDEX IF NOT EXISTS idx_ctp001_data ON ctp001(dataaponta_001);
-- CREATE INDEX IF NOT EXISTS idx_ctp001_selo ON ctp001(seloaponta_001);
-- CREATE INDEX IF NOT EXISTS idx_ctp001_numapo ON ctp001(numapo1_001, numapo2_001);
-- CREATE INDEX IF NOT EXISTS idx_ctp001_tipo_data ON ctp001(tipooper_001, dataaponta_001);

-- ==============================================
-- 14. INSERIR ALGUNS SELOS DE EXEMPLO
-- ==============================================

-- Inserir 50 selos de exemplo (tipo 0004)
INSERT INTO selos (selo_sel, tiposelo_sel, data_criacao_sel) VALUES
('SELO00001', '0004', DATE_SUB(NOW(), INTERVAL 30 DAY)),
('SELO00002', '0004', DATE_SUB(NOW(), INTERVAL 29 DAY)),
('SELO00003', '0004', DATE_SUB(NOW(), INTERVAL 28 DAY)),
('SELO00004', '0004', DATE_SUB(NOW(), INTERVAL 27 DAY)),
('SELO00005', '0004', DATE_SUB(NOW(), INTERVAL 26 DAY)),
('SELO00006', '0004', DATE_SUB(NOW(), INTERVAL 25 DAY)),
('SELO00007', '0004', DATE_SUB(NOW(), INTERVAL 24 DAY)),
('SELO00008', '0004', DATE_SUB(NOW(), INTERVAL 23 DAY)),
('SELO00009', '0004', DATE_SUB(NOW(), INTERVAL 22 DAY)),
('SELO00010', '0004', DATE_SUB(NOW(), INTERVAL 21 DAY)),
('SELO00011', '0004', DATE_SUB(NOW(), INTERVAL 20 DAY)),
('SELO00012', '0004', DATE_SUB(NOW(), INTERVAL 19 DAY)),
('SELO00013', '0004', DATE_SUB(NOW(), INTERVAL 18 DAY)),
('SELO00014', '0004', DATE_SUB(NOW(), INTERVAL 17 DAY)),
('SELO00015', '0004', DATE_SUB(NOW(), INTERVAL 16 DAY)),
('SELO00016', '0004', DATE_SUB(NOW(), INTERVAL 15 DAY)),
('SELO00017', '0004', DATE_SUB(NOW(), INTERVAL 14 DAY)),
('SELO00018', '0004', DATE_SUB(NOW(), INTERVAL 13 DAY)),
('SELO00019', '0004', DATE_SUB(NOW(), INTERVAL 12 DAY)),
('SELO00020', '0004', DATE_SUB(NOW(), INTERVAL 11 DAY)),
('SELO00021', '0004', DATE_SUB(NOW(), INTERVAL 10 DAY)),
('SELO00022', '0004', DATE_SUB(NOW(), INTERVAL 9 DAY)),
('SELO00023', '0004', DATE_SUB(NOW(), INTERVAL 8 DAY)),
('SELO00024', '0004', DATE_SUB(NOW(), INTERVAL 7 DAY)),
('SELO00025', '0004', DATE_SUB(NOW(), INTERVAL 6 DAY)),
('SELO00026', '0004', DATE_SUB(NOW(), INTERVAL 5 DAY)),
('SELO00027', '0004', DATE_SUB(NOW(), INTERVAL 4 DAY)),
('SELO00028', '0004', DATE_SUB(NOW(), INTERVAL 3 DAY)),
('SELO00029', '0004', DATE_SUB(NOW(), INTERVAL 2 DAY)),
('SELO00030', '0004', DATE_SUB(NOW(), INTERVAL 1 DAY)),
('SELO00031', '0004', NOW()),
('SELO00032', '0004', NOW()),
('SELO00033', '0004', NOW()),
('SELO00034', '0004', NOW()),
('SELO00035', '0004', NOW()),
('SELO00036', '0004', NOW()),
('SELO00037', '0004', NOW()),
('SELO00038', '0004', NOW()),
('SELO00039', '0004', NOW()),
('SELO00040', '0004', NOW()),
('SELO00041', '0004', NOW()),
('SELO00042', '0004', NOW()),
('SELO00043', '0004', NOW()),
('SELO00044', '0004', NOW()),
('SELO00045', '0004', NOW()),
('SELO00046', '0004', NOW()),
('SELO00047', '0004', NOW()),
('SELO00048', '0004', NOW()),
('SELO00049', '0004', NOW()),
('SELO00050', '0004', NOW())
ON DUPLICATE KEY UPDATE 
    tiposelo_sel = VALUES(tiposelo_sel),
    data_atualizacao_sel = NOW();

-- Inserir alguns selos de outros tipos
INSERT INTO selos (selo_sel, tiposelo_sel, tipo_sel) VALUES
('SELO10001', '0001', '0000'),
('SELO10002', '0001', '0000'),
('SELO10003', '0001', '0000'),
('SELO20001', '0002', '0000'),
('SELO20002', '0002', '0000'),
('SELO30001', '0003', '0000'),
('SELO30002', '0003', '0000')
ON DUPLICATE KEY UPDATE 
    tiposelo_sel = VALUES(tiposelo_sel),
    tipo_sel = VALUES(tipo_sel),
    data_atualizacao_sel = NOW();

-- ==============================================
-- 15. VERIFICAÇÃO FINAL DO SCHEMA
-- ==============================================

-- Mostrar tabelas criadas
SELECT 
    '=== TABELAS CRIADAS ===' AS info;

SELECT 
    TABLE_NAME AS tabela,
    TABLE_COMMENT AS descricao,
    TABLE_ROWS AS registros,
    DATA_LENGTH/1024/1024 AS tamanho_mb
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME LIKE '%selador%'
ORDER BY TABLE_NAME;

-- Mostrar procedures e functions
SELECT 
    '=== PROCEDURES E FUNCTIONS ===' AS info;

SELECT 
    ROUTINE_NAME AS nome,
    ROUTINE_TYPE AS tipo,
    ROUTINE_DEFINITION AS definicao
FROM INFORMATION_SCHEMA.ROUTINES 
WHERE ROUTINE_SCHEMA = DATABASE()
ORDER BY ROUTINE_NAME;

-- Mostrar triggers
SELECT 
    '=== TRIGGERS ===' AS info;

SELECT 
    TRIGGER_NAME AS trigger,
    EVENT_OBJECT_TABLE AS tabela,
    ACTION_TIMING AS timing,
    EVENT_MANIPULATION AS evento
FROM INFORMATION_SCHEMA.TRIGGERS 
WHERE TRIGGER_SCHEMA = DATABASE()
ORDER BY TRIGGER_NAME;

-- Mostrar views
SELECT 
    '=== VIEWS ===' AS info;

SELECT 
    TABLE_NAME AS view,
    VIEW_DEFINITION AS definicao
FROM INFORMATION_SCHEMA.VIEWS 
WHERE TABLE_SCHEMA = DATABASE()
ORDER BY TABLE_NAME;

-- ==============================================
-- FIM DO SCRIPT DE MIGRAÇÃO V1
-- ==============================================

-- Comentário final
SELECT '✅ Migração V1 concluída com sucesso!' AS mensagem_final;