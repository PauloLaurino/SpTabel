# ==================================================
# Deploy NOTAS / SPTABEL - Cálculo de Custas Cartoriais
# Enhanced script: verifica pré-requisitos, faz backup, tenta parar/ligar Tomcat, espera expansão
# ==================================================

$ErrorActionPreference = 'Stop'

$baseDir = Split-Path $MyInvocation.MyCommand.Path -Parent

Write-Host ""
Write-Host "== Deploy NOTAS / SPTABEL (WAR) ==" -ForegroundColor Cyan

# ---------- Configuráveis ----------
# Pode sobrescrever via variável de ambiente se necessário
$WarName = $env:NOTAS_WARNAME; if (-not $WarName) { $WarName = 'notas.war' }
$WarDest = $env:NOTAS_WARDEST; if (-not $WarDest) { $WarDest = 'C:\Program Files (x86)\Softwell Solutions\Maker 5\Webrun 5\tomcat\webapps\notas.war' }
$ContextPath = $env:NOTAS_CONTEXT; if (-not $ContextPath) { $ContextPath = 'notas' }
$SkipTests = $env:SKIP_TESTS; if (-not $SkipTests) { $SkipTests = $false }
$WaitExpandSeconds = 30

$warSource = Join-Path $baseDir "target\$WarName"

# Se o WAR esperado não existir, tentar localizar qualquer WAR gerado
if (-not (Test-Path $warSource)) {
    Write-Host ("WAR {0} não encontrado - procurando por *.war em target..." -f $warSource) -ForegroundColor Yellow
    $found = Get-ChildItem -Path (Join-Path $baseDir 'target') -Filter '*.war' -File -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($found) {
        $WarName = $found.Name
        $warSource = $found.FullName
        Write-Host ("Usando WAR detectado: {0}" -f $WarName) -ForegroundColor Green
    }
}

# ---------- Pré-checagens ----------
Write-Host "Verificando pré-requisitos..." -ForegroundColor Cyan

# Maven
$mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue
if (-not $mvnCmd) {
    Write-Error "mvn (Maven) não encontrado no PATH. Instale o Maven ou adicione ao PATH."; exit 1
}
# Java
$javaCmd = Get-Command java -ErrorAction SilentlyContinue
if (-not $javaCmd) {
    Write-Warning "java não encontrado no PATH. Verifique instalação do Java.";
}
else {
    try { & java -version 2>&1 | Select-Object -First 1 | Write-Host } catch {}
}

Write-Host "Diretório do projeto: $baseDir"
Write-Host "WAR origem: $warSource"
Write-Host "WAR destino: $WarDest"
Write-Host "Context Path: /$ContextPath"

# ---------- BUILD MAVEN ----------
Write-Host "Executando mvn clean package..." -ForegroundColor Cyan
$mvArgs = @('-f', "${baseDir}\pom.xml", 'clean', 'package')
if ($SkipTests -eq 'true' -or $SkipTests -eq $true) { $mvArgs += '-DskipTests=true' }

try {
    Write-Host "Chamando: mvn $([string]::Join(' ', $mvArgs))"
    & mvn @mvArgs
    $exitCode = $LASTEXITCODE
}
catch {
    Write-Error "Falha ao executar mvn: $_"
    exit 1
}

if ($exitCode -ne 0) {
    Write-Error "Build Maven falhou (exit $exitCode). Verifique logs do Maven."; exit $exitCode
}
Write-Host "Build Maven OK." -ForegroundColor Green

if (-not (Test-Path $warSource)) {
    Write-Error "WAR não encontrado em $warSource. Verifique output do build."; exit 1
}

# ---------- Preparar Tomcat / Backup ----------
$webappsDir = Split-Path $WarDest -Parent
$tomcatDir = Split-Path $webappsDir -Parent
$explodedDir = Join-Path $webappsDir $ContextPath
$ts = Get-Date -Format 'yyyyMMddHHmmss'

if (Test-Path $explodedDir) {
    Write-Host "Removendo diretório explodido: $explodedDir"
    try {
        Remove-Item -Path $explodedDir -Recurse -Force -ErrorAction Stop
        Write-Host 'Exploded dir removido.' -ForegroundColor Yellow
    }
    catch {
        Write-Warning "Não foi possível remover exploded dir: $_"
    }
}

# ---------- Tentar parar Tomcat (opcional) ----------
$shutdownRan = $false
$shutdownScript = Join-Path $tomcatDir 'bin\shutdown.bat'
$startupScript = Join-Path $tomcatDir 'bin\startup.bat'

# tentar parar pelo serviço Windows se existir
$TomcatServiceName = $env:TOMCAT_SERVICE_NAME; if (-not $TomcatServiceName) { $TomcatServiceName = 'Webrun_5' }
$svc = Get-Service -Name $TomcatServiceName -ErrorAction SilentlyContinue
if ($svc) {
    Write-Host "Encontrado serviço Windows '${TomcatServiceName}' - tentando pará-lo via Stop-Service" -ForegroundColor Cyan
    try {
        Stop-Service -Name $TomcatServiceName -Force -ErrorAction Stop
        $wait = 0
        while ((Get-Service -Name $TomcatServiceName).Status -ne 'Stopped' -and $wait -lt 15) { Start-Sleep -Seconds 1; $wait += 1 }
        if ((Get-Service -Name $TomcatServiceName).Status -eq 'Stopped') {
            $shutdownRan = $true
            Write-Host "Serviço ${TomcatServiceName} parado." -ForegroundColor Yellow
        }
        else {
            Write-Warning "Serviço ${TomcatServiceName} não parou dentro do timeout."
        }
    }
    catch { Write-Warning "Falha ao parar serviço ${TomcatServiceName}: $_" }
}
else {
    if (Test-Path $shutdownScript) {
        Write-Host "Tentando executar shutdown do Tomcat (shutdown.bat): $shutdownScript" -ForegroundColor Cyan
        try {
            & "$shutdownScript"
            Start-Sleep -Seconds 4
            $shutdownRan = $true
            Write-Host "Shutdown solicitado via shutdown.bat." -ForegroundColor Yellow
        }
        catch { Write-Warning "Falha ao executar shutdown.bat: $_" }
    }
    else {
        Write-Host ("shutdown.bat não encontrado em {0}\bin - pulando tentativa de parada automática." -f $tomcatDir) -ForegroundColor DarkYellow
    }
}

# ---------- Copiar WAR ----------
Write-Host "Copiando WAR para Tomcat Webrun..." -ForegroundColor Cyan
try {
    Copy-Item -Path $warSource -Destination $WarDest -Force
    Write-Host "WAR publicado com sucesso em $WarDest" -ForegroundColor Green
}
catch {
    Write-Error "Falha ao copiar WAR: $_"; exit 1
}

# ---------- Tentar iniciar Tomcat se parámos ----------
if ($shutdownRan) {
    $svc2 = Get-Service -Name $TomcatServiceName -ErrorAction SilentlyContinue
    if ($svc2) {
        Write-Host "Iniciando serviço Windows ${TomcatServiceName}..." -ForegroundColor Cyan
        try { Start-Service -Name $TomcatServiceName -ErrorAction Stop; Start-Sleep -Seconds 6; Write-Host "Serviço ${TomcatServiceName} solicitado start." -ForegroundColor Yellow } catch { Write-Warning "Falha ao iniciar serviço ${TomcatServiceName}: $_" }
    }
    elseif (Test-Path $startupScript) {
        Write-Host "Iniciando Tomcat via $startupScript" -ForegroundColor Cyan
        try { & "$startupScript"; Start-Sleep -Seconds 6; Write-Host "Startup solicitado." -ForegroundColor Yellow } catch { Write-Warning "Falha ao executar startup.bat: $_" }
    }
}

# ---------- Esperar expansão do WAR (verifica arquivo HTML) ----------
$expectedFile = Join-Path $explodedDir "webapp\custas.html"
Write-Host "Aguardando expansão do WAR (checando $expectedFile) por até $WaitExpandSeconds segundos..."
$elapsed = 0; $found = $false
while ($elapsed -lt $WaitExpandSeconds) {
    if (Test-Path $expectedFile) { $found = $true; break }
    Start-Sleep -Seconds 2; $elapsed += 2
}
if ($found) {
    Write-Host "Arquivo encontrado - deploy concluído e o conteúdo está acessível." -ForegroundColor Green
    Write-Host "Acesse: http://localhost:8080/$ContextPath/custas.html" -ForegroundColor Cyan
}
if (-not $found) {
    Write-Warning "Arquivo esperado não encontrado após $WaitExpandSeconds segundos. Pode ser necessário reiniciar o serviço Tomcat manualmente."
}

Write-Host ""
Write-Host "Deploy finalizado." -ForegroundColor Cyan
