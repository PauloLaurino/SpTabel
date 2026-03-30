#!/bin/bash
# ==============================================
# SCRIPT DE EXECUÇÃO LOCAL - SISTEMA SELADOR
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
TOMCAT_PORT="5000"
TOMCAT_VERSION="9.0.85"
TOMCAT_DIR="apache-tomcat-$TOMCAT_VERSION"
TOMCAT_URL="https://archive.apache.org/dist/tomcat/tomcat-9/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz"
WAR_FILE="target/selador.war"
LOG_DIR="logs"
PID_FILE="tomcat.pid"

# Função para log
log() {
    echo -e "$(date '+%Y-%m-%d %H:%M:%S') - $1"
}

# Função para verificar dependências
check_dependencies() {
    log "${BLUE}Verificando dependências...${NC}"
    
    local missing_deps=0
    
    # Verificar Java
    if ! command -v java &> /dev/null; then
        log "${RED}✗ Java não encontrado${NC}"
        log "  Instale com: sudo apt install openjdk-8-jdk"
        missing_deps=1
    else
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d '"' -f 2)
        log "${GREEN}✓ Java encontrado: $JAVA_VERSION${NC}"
    fi
    
    # Verificar Maven
    if ! command -v mvn &> /dev/null; then
        log "${RED}✗ Maven não encontrado${NC}"
        log "  Instale com: sudo apt install maven"
        missing_deps=1
    else
        MAVEN_VERSION=$(mvn -version 2>&1 | head -n 1 | cut -d ' ' -f 3)
        log "${GREEN}✓ Maven encontrado: $MAVEN_VERSION${NC}"
    fi
    
    # Verificar curl
    if ! command -v curl &> /dev/null; then
        log "${RED}✗ curl não encontrado${NC}"
        log "  Instale com: sudo apt install curl"
        missing_deps=1
    else
        log "${GREEN}✓ curl encontrado${NC}"
    fi
    
    if [ $missing_deps -eq 1 ]; then
        log "${RED}Dependências faltando. Instale antes de continuar.${NC}"
        exit 1
    fi
}

# Função para baixar Tomcat
download_tomcat() {
    log "${BLUE}Baixando Tomcat $TOMCAT_VERSION...${NC}"
    
    if [ -d "$TOMCAT_DIR" ]; then
        log "${GREEN}✓ Tomcat já baixado${NC}"
        return
    fi
    
    if curl -L "$TOMCAT_URL" -o tomcat.tar.gz; then
        log "${GREEN}✓ Download concluído${NC}"
        
        # Extrair
        tar -xzf tomcat.tar.gz
        rm tomcat.tar.gz
        
        # Configurar permissões
        chmod +x "$TOMCAT_DIR"/bin/*.sh
        
        log "${GREEN}✓ Tomcat extraído em $TOMCAT_DIR${NC}"
    else
        log "${RED}✗ Falha ao baixar Tomcat${NC}"
        exit 1
    fi
}

# Função para construir aplicação
build_application() {
    log "${BLUE}Construindo aplicação...${NC}"
    
    if [ ! -f "pom.xml" ]; then
        log "${RED}✗ Arquivo pom.xml não encontrado${NC}"
        exit 1
    fi
    
    # Limpar e construir
    log "Executando mvn clean package..."
    if mvn clean package -DskipTests; then
        log "${GREEN}✓ Build concluído${NC}"
        
        if [ -f "$WAR_FILE" ]; then
            local war_size=$(du -h "$WAR_FILE" | cut -f1)
            log "  WAR gerado: $WAR_FILE ($war_size)"
        else
            log "${RED}✗ WAR não encontrado em $WAR_FILE${NC}"
            exit 1
        fi
    else
        log "${RED}✗ Falha no build${NC}"
        exit 1
    fi
}

# Função para configurar Tomcat
configure_tomcat() {
    log "${BLUE}Configurando Tomcat...${NC}"
    
    # Criar diretório de logs
    mkdir -p "$LOG_DIR"
    
    # Configurar server.xml para porta correta
    local server_xml="$TOMCAT_DIR/conf/server.xml"
    if [ -f "$server_xml" ]; then
        # Alterar porta se necessário
        if grep -q "port=\"$TOMCAT_PORT\"" "$server_xml"; then
            log "${GREEN}✓ Tomcat já configurado na porta $TOMCAT_PORT${NC}"
        else
            log "Configurando porta $TOMCAT_PORT..."
            sed -i "s/port=\"5000\"/port=\"$TOMCAT_PORT\"/g" "$server_xml"
            log "${GREEN}✓ Porta configurada para $TOMCAT_PORT${NC}"
        fi
    fi
    
    # Remover aplicações padrão para liberar memória
    log "Removendo aplicações padrão..."
    rm -rf "$TOMCAT_DIR"/webapps/*
    
    # Copiar WAR
    log "Copiando aplicação..."
    cp "$WAR_FILE" "$TOMCAT_DIR/webapps/ROOT.war"
    
    log "${GREEN}✓ Tomcat configurado${NC}"
}

# Função para iniciar Tomcat
start_tomcat() {
    log "${BLUE}Iniciando Tomcat...${NC}"
    
    # Verificar se já está rodando
    if [ -f "$PID_FILE" ] && ps -p "$(cat "$PID_FILE")" > /dev/null 2>&1; then
        log "${YELLOW}⚠ Tomcat já está em execução (PID: $(cat "$PID_FILE"))${NC}"
        return
    fi
    
    # Iniciar Tomcat
    cd "$TOMCAT_DIR"
    ./bin/startup.sh
    cd ..
    
    # Salvar PID
    echo $(lsof -ti:$TOMCAT_PORT) > "$PID_FILE" 2>/dev/null || true
    
    log "${GREEN}✓ Tomcat iniciado na porta $TOMCAT_PORT${NC}"
    log "  Acesse: http://localhost:$TOMCAT_PORT"
    log "  Interface: http://localhost:$TOMCAT_PORT/views/selador.html"
    log "  API Health: http://localhost:$TOMCAT_PORT/api/config/health"
}

# Função para parar Tomcat
stop_tomcat() {
    log "${BLUE}Parando Tomcat...${NC}"
    
    if [ ! -f "$PID_FILE" ]; then
        log "${YELLOW}⚠ PID file não encontrado, tentando encontrar processo...${NC}"
        local pid=$(lsof -ti:$TOMCAT_PORT 2>/dev/null || echo "")
        
        if [ -n "$pid" ]; then
            log "Encontrado processo na porta $TOMCAT_PORT (PID: $pid)"
            kill -9 "$pid"
            log "${GREEN}✓ Tomcat parado${NC}"
        else
            log "${YELLOW}⚠ Nenhum processo Tomcat encontrado${NC}"
        fi
    else
        local pid=$(cat "$PID_FILE")
        
        if ps -p "$pid" > /dev/null 2>&1; then
            log "Parando Tomcat (PID: $pid)..."
            kill -9 "$pid"
            log "${GREEN}✓ Tomcat parado${NC}"
        else
            log "${YELLOW}⚠ Processo Tomcat não encontrado (PID: $pid)${NC}"
        fi
        
        rm -f "$PID_FILE"
    fi
    
    # Parar via script do Tomcat também
    if [ -f "$TOMCAT_DIR/bin/shutdown.sh" ]; then
        "$TOMCAT_DIR/bin/shutdown.sh" > /dev/null 2>&1 || true
    fi
}

# Função para verificar status
status_tomcat() {
    log "${BLUE}Verificando status do Tomcat...${NC}"
    
    local pid=""
    
    if [ -f "$PID_FILE" ]; then
        pid=$(cat "$PID_FILE")
    else
        pid=$(lsof -ti:$TOMCAT_PORT 2>/dev/null || echo "")
    fi
    
    if [ -n "$pid" ] && ps -p "$pid" > /dev/null 2>&1; then
        log "${GREEN}✓ Tomcat está em execução (PID: $pid)${NC}"
        log "  Porta: $TOMCAT_PORT"
        log "  Uptime: $(ps -o etime= -p "$pid" | xargs)"
        
        # Verificar saúde da aplicação
        if curl -s "http://localhost:$TOMCAT_PORT/api/config/health" > /dev/null 2>&1; then
            log "${GREEN}✓ Aplicação respondendo${NC}"
        else
            log "${YELLOW}⚠ Aplicação não respondendo${NC}"
        fi
    else
        log "${RED}✗ Tomcat não está em execução${NC}"
    fi
}

# Função para monitorar logs
monitor_logs() {
    log "${BLUE}Monitorando logs...${NC}"
    
    local catalina_log="$TOMCAT_DIR/logs/catalina.out"
    
    if [ ! -f "$catalina_log" ]; then
        log "${YELLOW}⚠ Arquivo de log não encontrado: $catalina_log${NC}"
        return
    fi
    
    log "Pressione Ctrl+C para parar o monitoramento"
    log "${BLUE}========================================${NC}"
    
    tail -f "$catalina_log" | while read line; do
        # Colorir linhas de erro
        if echo "$line" | grep -qi "error\|exception"; then
            echo -e "${RED}$line${NC}"
        elif echo "$line" | grep -qi "warn"; then
            echo -e "${YELLOW}$line${NC}"
        elif echo "$line" | grep -qi "started\|INFO"; then
            echo -e "${GREEN}$line${NC}"
        else
            echo "$line"
        fi
    done
}

# Função para limpar
cleanup() {
    log "${BLUE}Limpando...${NC}"
    
    # Parar Tomcat se estiver rodando
    stop_tomcat
    
    # Remover Tomcat
    if [ -d "$TOMCAT_DIR" ]; then
        log "Removendo Tomcat..."
        rm -rf "$TOMCAT_DIR"
        log "${GREEN}✓ Tomcat removido${NC}"
    fi
    
    # Remover PID file
    rm -f "$PID_FILE"
    
    # Remover logs
    if [ -d "$LOG_DIR" ]; then
        rm -rf "$LOG_DIR"
    fi
    
    log "${GREEN}✓ Limpeza concluída${NC}"
}

# Função para mostrar ajuda
show_help() {
    cat << EOF
Script de execução local do Sistema Selador

Uso: $0 [COMANDO]

Comandos:
  start      Inicia o Tomcat com a aplicação
  stop       Para o Tomcat
  restart    Reinicia o Tomcat
  status     Mostra status do Tomcat
  logs       Monitora logs em tempo real
  build      Apenas constrói a aplicação
  setup      Configura ambiente (download Tomcat + build)
  clean      Para Tomcat e limpa arquivos
  help       Mostra esta ajuda

Exemplos:
  $0 setup    # Configura ambiente pela primeira vez
  $0 start    # Inicia aplicação
  $0 logs     # Monitora logs
  $0 stop     # Para aplicação
EOF
}

# Função principal
main() {
    local command="$1"
    
    case "$command" in
        start)
            check_dependencies
            download_tomcat
            configure_tomcat
            start_tomcat
            ;;
        stop)
            stop_tomcat
            ;;
        restart)
            stop_tomcat
            sleep 2
            start_tomcat
            ;;
        status)
            status_tomcat
            ;;
        logs)
            monitor_logs
            ;;
        build)
            check_dependencies
            build_application
            ;;
        setup)
            check_dependencies
            download_tomcat
            build_application
            configure_tomcat
            log "${GREEN}✓ Ambiente configurado!${NC}"
            log "  Execute: $0 start"
            ;;
        clean)
            cleanup
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            if [ -z "$command" ]; then
                # Modo interativo
                log "${GREEN}Sistema Selador - Execução Local${NC}"
                log "${BLUE}Selecione uma opção:${NC}"
                echo "1) Setup inicial (download Tomcat + build)"
                echo "2) Start aplicação"
                echo "3) Stop aplicação"
                echo "4) Status"
                echo "5) Monitorar logs"
                echo "6) Limpar tudo"
                echo "7) Sair"
                echo -n "Opção: "
                
                read -r option
                
                case $option in
                    1) main "setup" ;;
                    2) main "start" ;;
                    3) main "stop" ;;
                    4) main "status" ;;
                    5) main "logs" ;;
                    6) main "clean" ;;
                    7) exit 0 ;;
                    *) log "${RED}Opção inválida${NC}" ;;
                esac
            else
                log "${RED}Comando desconhecido: $command${NC}"
                show_help
                exit 1
            fi
            ;;
    esac
}

# Tratamento de sinais
trap 'log "${RED}Script interrompido${NC}"; stop_tomcat; exit 1' INT TERM

# Executar função principal
main "$1"