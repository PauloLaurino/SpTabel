#!/bin/bash
# ==============================================
# SCRIPT DE DEPLOY - SISTEMA SELADOR
# ==============================================

set -e  # Sai imediatamente se algum comando falhar

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configurações
APP_NAME="selador"
WAR_FILE="target/selador.war"
TOMCAT_HOME="/opt/tomcat"
TOMCAT_WEBAPPS="$TOMCAT_HOME/webapps"
BACKUP_DIR="/backup/selador"
LOG_FILE="/var/log/selador/deploy.log"
DATE=$(date +%Y%m%d_%H%M%S)

# Função para log
log() {
    echo -e "$(date '+%Y-%m-%d %H:%M:%S') - $1" | tee -a "$LOG_FILE"
}

# Função para verificar se o usuário é root
check_root() {
    if [ "$EUID" -ne 0 ]; then
        log "${RED}Este script precisa ser executado como root ou com sudo${NC}"
        exit 1
    fi
}

# Função para verificar dependências
check_dependencies() {
    log "${BLUE}Verificando dependências...${NC}"
    
    local missing_deps=0
    
    # Verificar Java
    if ! command -v java &> /dev/null; then
        log "${RED}✗ Java não encontrado${NC}"
        missing_deps=1
    else
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d '"' -f 2)
        log "${GREEN}✓ Java encontrado: $JAVA_VERSION${NC}"
    fi
    
    # Verificar Maven
    if ! command -v mvn &> /dev/null; then
        log "${RED}✗ Maven não encontrado${NC}"
        missing_deps=1
    else
        MAVEN_VERSION=$(mvn -version 2>&1 | head -n 1 | cut -d ' ' -f 3)
        log "${GREEN}✓ Maven encontrado: $MAVEN_VERSION${NC}"
    fi
    
    # Verificar Tomcat
    if [ ! -d "$TOMCAT_HOME" ]; then
        log "${RED}✗ Tomcat não encontrado em $TOMCAT_HOME${NC}"
        missing_deps=1
    else
        log "${GREEN}✓ Tomcat encontrado em $TOMCAT_HOME${NC}"
    fi
    
    if [ $missing_deps -eq 1 ]; then
        log "${RED}Dependências faltando. Instale antes de continuar.${NC}"
        exit 1
    fi
}

# Função para criar backup
create_backup() {
    log "${BLUE}Criando backup do deploy atual...${NC}"
    
    # Criar diretório de backup se não existir
    mkdir -p "$BACKUP_DIR"
    
    # Backup do WAR atual
    if [ -f "$TOMCAT_WEBAPPS/$APP_NAME.war" ]; then
        cp "$TOMCAT_WEBAPPS/$APP_NAME.war" "$BACKUP_DIR/$APP_NAME.war.backup_$DATE"
        log "${GREEN}✓ Backup criado: $BACKUP_DIR/$APP_NAME.war.backup_$DATE${NC}"
    else
        log "${YELLOW}⚠ Nenhum WAR anterior encontrado para backup${NC}"
    fi
    
    # Backup do diretório descompactado
    if [ -d "$TOMCAT_WEBAPPS/$APP_NAME" ]; then
        tar -czf "$BACKUP_DIR/$APP_NAME.dir.backup_$DATE.tar.gz" -C "$TOMCAT_WEBAPPS" "$APP_NAME"
        log "${GREEN}✓ Backup do diretório criado${NC}"
    fi
}

# Função para construir a aplicação
build_application() {
    log "${BLUE}Construindo aplicação com Maven...${NC}"
    
    # Limpar builds anteriores
    log "Limpando builds anteriores..."
    mvn clean -q
    
    # Executar testes
    log "Executando testes..."
    if mvn test -q; then
        log "${GREEN}✓ Todos os testes passaram${NC}"
    else
        log "${RED}✗ Testes falharam. Abortando deploy.${NC}"
        exit 1
    fi
    
    # Construir pacote WAR
    log "Gerando pacote WAR..."
    if mvn package -q -DskipTests; then
        log "${GREEN}✓ WAR gerado com sucesso${NC}"
    else
        log "${RED}✗ Falha ao gerar WAR${NC}"
        exit 1
    fi
    
    # Verificar se o WAR foi criado
    if [ ! -f "$WAR_FILE" ]; then
        log "${RED}✗ WAR não encontrado em $WAR_FILE${NC}"
        exit 1
    fi
    
    log "${GREEN}✓ Tamanho do WAR: $(du -h "$WAR_FILE" | cut -f1)${NC}"
}

# Função para parar Tomcat
stop_tomcat() {
    log "${BLUE}Parando Tomcat...${NC}"
    
    if [ -f "$TOMCAT_HOME/bin/shutdown.sh" ]; then
        # Tentar parar graciosamente
        "$TOMCAT_HOME/bin/shutdown.sh" &> /dev/null
        sleep 10
        
        # Verificar se parou
        if pgrep -f tomcat > /dev/null; then
            log "${YELLOW}⚠ Tomcat ainda rodando, forçando parada...${NC}"
            pkill -9 -f tomcat
            sleep 5
        fi
        
        if ! pgrep -f tomcat > /dev/null; then
            log "${GREEN}✓ Tomcat parado com sucesso${NC}"
        else
            log "${RED}✗ Não foi possível parar o Tomcat${NC}"
            exit 1
        fi
    else
        log "${YELLOW}⚠ Script de shutdown não encontrado, tentando matar processo...${NC}"
        pkill -9 -f tomcat || true
        sleep 5
    fi
}

# Função para iniciar Tomcat
start_tomcat() {
    log "${BLUE}Iniciando Tomcat...${NC}"
    
    if [ -f "$TOMCAT_HOME/bin/startup.sh" ]; then
        "$TOMCAT_HOME/bin/startup.sh" &> /dev/null
        sleep 15
        
        # Verificar se iniciou
        if pgrep -f tomcat > /dev/null; then
            log "${GREEN}✓ Tomcat iniciado com sucesso${NC}"
        else
            log "${RED}✗ Falha ao iniciar Tomcat${NC}"
            exit 1
        fi
    else
        log "${RED}✗ Script de startup não encontrado${NC}"
        exit 1
    fi
}

# Função para deploy
deploy_application() {
    log "${BLUE}Realizando deploy da aplicação...${NC}"
    
    # Remover aplicação anterior
    if [ -d "$TOMCAT_WEBAPPS/$APP_NAME" ]; then
        log "Removendo diretório anterior..."
        rm -rf "$TOMCAT_WEBAPPS/$APP_NAME"
    fi
    
    if [ -f "$TOMCAT_WEBAPPS/$APP_NAME.war" ]; then
        log "Removendo WAR anterior..."
        rm -f "$TOMCAT_WEBAPPS/$APP_NAME.war"
    fi
    
    # Copiar novo WAR
    log "Copiando novo WAR..."
    cp "$WAR_FILE" "$TOMCAT_WEBAPPS/$APP_NAME.war"
    
    # Verificar cópia
    if [ -f "$TOMCAT_WEBAPPS/$APP_NAME.war" ]; then
        log "${GREEN}✓ WAR copiado com sucesso${NC}"
        log "  Local: $TOMCAT_WEBAPPS/$APP_NAME.war"
        log "  Tamanho: $(du -h "$TOMCAT_WEBAPPS/$APP_NAME.war" | cut -f1)"
    else
        log "${RED}✗ Falha ao copiar WAR${NC}"
        exit 1
    fi
}

# Função para verificar saúde da aplicação
check_health() {
    log "${BLUE}Verificando saúde da aplicação...${NC}"
    
    local max_attempts=30
    local attempt=1
    local health_url="http://localhost:5000/$APP_NAME/api/config/health"
    
    while [ $attempt -le $max_attempts ]; do
        log "Tentativa $attempt/$max_attempts..."
        
        if curl -s --max-time 10 "$health_url" | grep -q "success.*true"; then
            log "${GREEN}✓ Aplicação está respondendo e saudável${NC}"
            return 0
        fi
        
        sleep 5
        ((attempt++))
    done
    
    log "${RED}✗ Aplicação não respondeu após $max_attempts tentativas${NC}"
    return 1
}

# Função para verificar logs de erro
check_logs() {
    log "${BLUE}Verificando logs de erro...${NC}"
    
    local catalina_log="$TOMCAT_HOME/logs/catalina.out"
    local app_log="$TOMCAT_HOME/logs/localhost.$APP_NAME.log"
    
    if [ -f "$catalina_log" ]; then
        # Verificar últimos erros no catalina.out
        local errors=$(tail -100 "$catalina_log" | grep -i "error\|exception\|failed" | head -5)
        
        if [ -n "$errors" ]; then
            log "${YELLOW}⚠ Erros encontrados no catalina.out:${NC}"
            echo "$errors"
        else
            log "${GREEN}✓ Nenhum erro encontrado nos logs recentes${NC}"
        fi
    fi
}

# Função para limpar temporários
cleanup() {
    log "${BLUE}Limpando arquivos temporários...${NC}"
    
    # Limpar target do Maven (opcional, manter para debug se necessário)
    # mvn clean -q
    
    # Limpar backups antigos (mantém últimos 10)
    if [ -d "$BACKUP_DIR" ]; then
        cd "$BACKUP_DIR"
        ls -t | tail -n +11 | xargs rm -f 2>/dev/null || true
        log "${GREEN}✓ Backups antigos removidos${NC}"
    fi
    
    # Limpar logs de deploy antigos
    if [ -f "$LOG_FILE" ]; then
        # Manter últimos 100MB de logs
        tail -c 100M "$LOG_FILE" > "${LOG_FILE}.tmp" && mv "${LOG_FILE}.tmp" "$LOG_FILE"
    fi
}

# Função principal
main() {
    log "${GREEN}==========================================${NC}"
    log "${GREEN}INICIANDO DEPLOY DO SISTEMA SELADOR${NC}"
    log "${GREEN}Data/Hora: $(date)${NC}"
    log "${GREEN}==========================================${NC}"
    
    # Verificar se é root
    check_root
    
    # Verificar dependências
    check_dependencies
    
    # Criar backup
    create_backup
    
    # Construir aplicação
    build_application
    
    # Parar Tomcat
    stop_tomcat
    
    # Realizar deploy
    deploy_application
    
    # Iniciar Tomcat
    start_tomcat
    
    # Verificar saúde
    if check_health; then
        log "${GREEN}==========================================${NC}"
        log "${GREEN}DEPLOY CONCLUÍDO COM SUCESSO!${NC}"
        log "${GREEN}Aplicação disponível em:${NC}"
        log "${GREEN}  http://localhost:5000/$APP_NAME${NC}"
        log "${GREEN}  http://localhost:5000/$APP_NAME/views/selador.html${NC}"
        log "${GREEN}==========================================${NC}"
    else
        log "${RED}==========================================${NC}"
        log "${RED}DEPLOY CONCLUÍDO COM AVISOS${NC}"
        log "${RED}A aplicação pode não estar respondendo${NC}"
        log "${RED}==========================================${NC}"
        
        # Verificar logs para diagnóstico
        check_logs
    fi
    
    # Limpeza
    cleanup
    
    log "${GREEN}==========================================${NC}"
    log "${GREEN}FIM DO PROCESSO DE DEPLOY${NC}"
    log "${GREEN}==========================================${NC}"
}

# Tratamento de sinais
trap 'log "${RED}Script interrompido pelo usuário${NC}"; exit 1' INT TERM

# Executar função principal
main "$@"