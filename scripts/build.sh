#!/bin/bash
# ==============================================
# SCRIPT DE BUILD - SISTEMA SELADOR
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
LOG_FILE="build.log"
DATE=$(date +%Y%m%d_%H%M%S)

# Função para log
log() {
    echo -e "$(date '+%Y-%m-%d %H:%M:%S') - $1" | tee -a "$LOG_FILE"
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
        
        # Verificar se é Java 8
        if [[ ! "$JAVA_VERSION" =~ ^1\.8 ]]; then
            log "${YELLOW}⚠ Aviso: Java 8 recomendado. Encontrado: $JAVA_VERSION${NC}"
        fi
    fi
    
    # Verificar Maven
    if ! command -v mvn &> /dev/null; then
        log "${RED}✗ Maven não encontrado${NC}"
        missing_deps=1
    else
        MAVEN_VERSION=$(mvn -version 2>&1 | head -n 1 | cut -d ' ' -f 3)
        log "${GREEN}✓ Maven encontrado: $MAVEN_VERSION${NC}"
    fi
    
    # Verificar Git
    if ! command -v git &> /dev/null; then
        log "${YELLOW}⚠ Git não encontrado (opcional)${NC}"
    else
        log "${GREEN}✓ Git encontrado${NC}"
    fi
    
    if [ $missing_deps -eq 1 ]; then
        log "${RED}Dependências faltando. Instale antes de continuar.${NC}"
        exit 1
    fi
}

# Função para limpar build anterior
clean_build() {
    log "${BLUE}Limpando build anterior...${NC}"
    
    if [ -d "target" ]; then
        log "Removendo diretório target..."
        rm -rf target
        log "${GREEN}✓ Build anterior removido${NC}"
    else
        log "${GREEN}✓ Nenhum build anterior encontrado${NC}"
    fi
    
    # Limpar logs
    if [ -f "$LOG_FILE" ]; then
        > "$LOG_FILE"
    fi
}

# Função para executar testes
run_tests() {
    local test_option="$1"
    
    log "${BLUE}Executando testes...${NC}"
    
    case "$test_option" in
        "unit")
            log "Executando testes unitários..."
            if mvn test -Dtest="*Test" -DfailIfNoTests=false; then
                log "${GREEN}✓ Testes unitários passaram${NC}"
            else
                log "${RED}✗ Testes unitários falharam${NC}"
                return 1
            fi
            ;;
        "integration")
            log "Executando testes de integração..."
            if mvn verify -Dit.test="*IT"; then
                log "${GREEN}✓ Testes de integração passaram${NC}"
            else
                log "${RED}✗ Testes de integração falharam${NC}"
                return 1
            fi
            ;;
        "all"|"")
            log "Executando todos os testes..."
            if mvn test; then
                log "${GREEN}✓ Todos os testes passaram${NC}"
            else
                log "${RED}✗ Alguns testes falharam${NC}"
                return 1
            fi
            ;;
        "skip")
            log "${YELLOW}⚠ Pulando testes${NC}"
            return 0
            ;;
        *)
            log "${RED}✗ Opção de teste inválida: $test_option${NC}"
            return 1
            ;;
    esac
    
    # Gerar relatório de cobertura
    if [ -d "target/site/jacoco" ]; then
        log "${GREEN}✓ Relatório de cobertura gerado em target/site/jacoco${NC}"
    fi
}

# Função para compilar projeto
compile_project() {
    log "${BLUE}Compilando projeto...${NC}"
    
    if mvn compile; then
        log "${GREEN}✓ Compilação concluída${NC}"
    else
        log "${RED}✗ Falha na compilação${NC}"
        return 1
    fi
}

# Função para empacotar WAR
package_war() {
    local skip_tests="$1"
    
    log "${BLUE}Empacotando WAR...${NC}"
    
    local mvn_command="mvn package"
    
    if [ "$skip_tests" = "true" ]; then
        mvn_command="$mvn_command -DskipTests"
        log "${YELLOW}⚠ Testes pulados durante o empacotamento${NC}"
    fi
    
    if eval "$mvn_command"; then
        log "${GREEN}✓ WAR gerado com sucesso${NC}"
        
        # Verificar arquivo WAR
        if [ -f "target/$APP_NAME.war" ]; then
            local war_size=$(du -h "target/$APP_NAME.war" | cut -f1)
            local war_md5=$(md5sum "target/$APP_NAME.war" | cut -d' ' -f1)
            
            log "  Arquivo: target/$APP_NAME.war"
            log "  Tamanho: $war_size"
            log "  MD5: $war_md5"
            log "  Data: $(date)"
        else
            log "${RED}✗ WAR não encontrado em target/$APP_NAME.war${NC}"
            return 1
        fi
    else
        log "${RED}✗ Falha ao gerar WAR${NC}"
        return 1
    fi
}

# Função para analisar código
analyze_code() {
    log "${BLUE}Analisando código...${NC}"
    
    # Checkstyle
    log "Executando Checkstyle..."
    if mvn checkstyle:check; then
        log "${GREEN}✓ Checkstyle passou${NC}"
    else
        log "${YELLOW}⚠ Checkstyle encontrou problemas${NC}"
    fi
    
    # PMD
    log "Executando PMD..."
    if mvn pmd:check; then
        log "${GREEN}✓ PMD passou${NC}"
    else
        log "${YELLOW}⚠ PMD encontrou problemas${NC}"
    fi
    
    # SpotBugs
    log "Executando SpotBugs..."
    if mvn spotbugs:check; then
        log "${GREEN}✓ SpotBugs passou${NC}"
    else
        log "${YELLOW}⚠ SpotBugs encontrou problemas${NC}"
    fi
}

# Função para gerar documentação
generate_docs() {
    log "${BLUE}Gerando documentação...${NC}"
    
    # JavaDoc
    log "Gerando JavaDoc..."
    if mvn javadoc:javadoc; then
        log "${GREEN}✓ JavaDoc gerado em target/site/apidocs${NC}"
    else
        log "${YELLOW}⚠ Falha ao gerar JavaDoc${NC}"
    fi
    
    # Relatório de dependências
    log "Gerando relatório de dependências..."
    if mvn project-info-reports:dependencies; then
        log "${GREEN}✓ Relatório de dependências gerado${NC}"
    fi
    
    # Site do projeto
    log "Gerando site do projeto..."
    if mvn site; then
        log "${GREEN}✓ Site do projeto gerado em target/site${NC}"
    fi
}

# Função para criar pacote de release
create_release_package() {
    local version="$1"
    
    log "${BLUE}Criando pacote de release v$version...${NC}"
    
    local release_dir="release/$APP_NAME-$version"
    local release_zip="$APP_NAME-$version.zip"
    
    # Criar diretório de release
    mkdir -p "$release_dir"
    
    # Copiar arquivos necessários
    cp "target/$APP_NAME.war" "$release_dir/"
    cp -r "target/site" "$release_dir/docs"
    cp "README.md" "$release_dir/"
    cp "CHANGELOG.md" "$release_dir/"
    cp "LICENSE" "$release_dir/"
    cp -r "scripts" "$release_dir/"
    cp -r "src/main/resources/config" "$release_dir/config-examples"
    
    # Criar arquivo de versão
    echo "version=$version" > "$release_dir/VERSION"
    echo "build_date=$(date)" >> "$release_dir/VERSION"
    echo "build_md5=$(md5sum "target/$APP_NAME.war" | cut -d' ' -f1)" >> "$release_dir/VERSION"
    
    # Criar script de instalação
    cat > "$release_dir/install.sh" << 'EOF'
#!/bin/bash
# Script de instalação do Sistema Selador

set -e

echo "Instalando Sistema Selador..."
echo "Versão: $(cat VERSION | grep version | cut -d= -f2)"

# Verificar Java
if ! command -v java &> /dev/null; then
    echo "Erro: Java não encontrado"
    exit 1
fi

# Copiar WAR para Tomcat (ajustar caminho)
TOMCAT_WEBAPPS="/opt/tomcat/webapps"
if [ -d "$TOMCAT_WEBAPPS" ]; then
    cp selador.war "$TOMCAT_WEBAPPS/"
    echo "Aplicação copiada para $TOMCAT_WEBAPPS/"
else
    echo "Aviso: Diretório do Tomcat não encontrado"
    echo "Copie manualmente selador.war para o webapps do Tomcat"
fi

echo "Instalação concluída!"
EOF
    
    chmod +x "$release_dir/install.sh"
    
    # Compactar release
    log "Compactando release..."
    cd "release"
    zip -r "$release_zip" "$(basename "$release_dir")"
    cd ..
    
    log "${GREEN}✓ Release criada: release/$release_zip${NC}"
    log "  Tamanho: $(du -h "release/$release_zip" | cut -f1)"
}

# Função para mostrar ajuda
show_help() {
    cat << EOF
Script de build do Sistema Selador

Uso: $0 [OPÇÕES]

Opções:
  -h, --help           Mostra esta ajuda
  -c, --clean          Limpa build anterior
  -t, --test TIPO      Executa testes (unit, integration, all, skip)
  -p, --package        Gera pacote WAR
  -a, --analyze        Executa análise de código
  -d, --docs           Gera documentação
  -r, --release VER    Cria pacote de release
  -s, --skip-tests     Pula testes no empacotamento
  -v, --version        Mostra versão

Exemplos:
  $0 -c -t all -p          # Limpa, testa e empacota
  $0 -p -s                 # Empacota pulando testes
  $0 -a -d                 # Analisa código e gera docs
  $0 -r 1.0.0              # Cria release v1.0.0
EOF
}

# Função principal
main() {
    local clean=false
    local test_option="all"
    local package=false
    local analyze=false
    local docs=false
    local release_version=""
    local skip_tests=false
    
    # Parse arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_help
                exit 0
                ;;
            -c|--clean)
                clean=true
                shift
                ;;
            -t|--test)
                test_option="$2"
                shift 2
                ;;
            -p|--package)
                package=true
                shift
                ;;
            -a|--analyze)
                analyze=true
                shift
                ;;
            -d|--docs)
                docs=true
                shift
                ;;
            -r|--release)
                release_version="$2"
                shift 2
                ;;
            -s|--skip-tests)
                skip_tests=true
                shift
                ;;
            -v|--version)
                echo "Build Script v1.0.0"
                exit 0
                ;;
            *)
                log "${RED}Opção desconhecida: $1${NC}"
                show_help
                exit 1
                ;;
        esac
    done
    
    log "${GREEN}==========================================${NC}"
    log "${GREEN}INICIANDO BUILD DO SISTEMA SELADOR${NC}"
    log "${GREEN}Data/Hora: $(date)${NC}"
    log "${GREEN}==========================================${NC}"
    
    # Verificar dependências
    check_dependencies
    
    # Limpar se solicitado
    if [ "$clean" = true ]; then
        clean_build
    fi
    
    # Executar testes
    if [ "$test_option" != "skip" ] && [ "$package" = true ]; then
        run_tests "$test_option" || exit 1
    fi
    
    # Compilar
    compile_project || exit 1
    
    # Analisar código
    if [ "$analyze" = true ]; then
        analyze_code
    fi
    
    # Empacotar
    if [ "$package" = true ]; then
        package_war "$skip_tests" || exit 1
    fi
    
    # Gerar documentação
    if [ "$docs" = true ]; then
        generate_docs
    fi
    
    # Criar release
    if [ -n "$release_version" ]; then
        if [ ! -f "target/$APP_NAME.war" ]; then
            log "${RED}✗ WAR não encontrado. Execute --package primeiro.${NC}"
            exit 1
        fi
        create_release_package "$release_version"
    fi
    
    # Resumo
    log "${GREEN}==========================================${NC}"
    log "${GREEN}BUILD CONCLUÍDO COM SUCESSO!${NC}"
    
    if [ "$package" = true ] && [ -f "target/$APP_NAME.war" ]; then
        local war_size=$(du -h "target/$APP_NAME.war" | cut -f1)
        log "${GREEN}WAR gerado: target/$APP_NAME.war ($war_size)${NC}"
    fi
    
    if [ -n "$release_version" ]; then
        log "${GREEN}Release criada: release/$APP_NAME-$release_version.zip${NC}"
    fi
    
    log "${GREEN}==========================================${NC}"
    log "Log completo disponível em: $LOG_FILE"
}

# Tratamento de sinais
trap 'log "${RED}Build interrompido pelo usuário${NC}"; exit 1' INT TERM

# Executar função principal
main "$@"