<#
.SYNOPSIS
  Deploy do WAR do SPDealer para um Tomcat remoto via SCP/SSH.

.DESCRIPTION
  Suporta dois ambientes:
  - DEV: 192.168.10.70, banco erp (teste)
  - PROD: 100.126.166.63, banco erp (produção)
  
  O script compila o projeto com Maven e faz deploy do WAR.

  Para DEV:   .\deploy.ps1 -D DEV   (ou -Ambiente DEV)
  Para PROD:  .\deploy.ps1 -P PROD  (ou -Ambiente PROD)

.EXAMPLE
  .\deploy.ps1 -D DEV
  .\deploy.ps1 -P PROD
  .\deploy.ps1 -Ambiente PROD

#>

[CmdletBinding()]
param(
    [Parameter(Mandatory=$false)]
    [ValidateSet("DEV", "PROD")]
    [Alias("D", "P")]
    [string]$Ambiente = "DEV",
    
    [string]$RemoteUser = "root",
    [string]$RemoteTomcatWebapps = "/usr/local/tomcat10/webapps",
    [int]$SshPort = 22,
    [int]$SshTimeoutSec = 30,
    [switch]$SkipCompile,
    [switch]$SkipDeploy
)

# Configurações por ambiente
$config = @{
    DEV = @{
        Host = "192.168.10.70"
        WarName = "spdealer_test.war"
        Profile = "prod"
        Servico = "spdealer.service"
        CORS = "http://192.168.10.70:3000,http://192.168.10.70:5070"
    }
    PROD = @{
        Host = "100.126.166.63"
        WarName = "spdealer.war"
        Profile = "prod"
        Servico = "spdealer.service"
        CORS = "https://spdealer.seprocom.com.br"
    }
}

$cfg = $config[$Ambiente]
$RemoteHost = $cfg.Host
$warName = $cfg.WarName
$profile = $cfg.Profile
$servico = $cfg.Servico

# Comandos nativos do Windows (preferencial para usar chaves SSH automaticamente)
$ScpCmd = "scp"
$SshCmd = "ssh"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  AMBIENTE: $Ambiente" -ForegroundColor Cyan
Write-Host "  Servidor: $RemoteHost" -ForegroundColor Cyan
Write-Host "  WAR: $warName" -ForegroundColor Cyan
Write-Host "  Profile: $profile" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

function AbortIfError($code, $msg) {
    if ($code -ne 0) {
        Write-Error $msg
        exit $code
    }
}

# ============================================================================
# PASSO 1: Compilar projeto com Maven
# ============================================================================

if (-not $SkipCompile) {
    Write-Output "=============================================="
    Write-Output "PASSO 1: Compilando projeto com Maven..."
    Write-Output "=============================================="
    
    $mvnCmd = "mvn clean package -DskipTests -P$profile"
    Write-Output "Executando: $mvnCmd"
    
    Invoke-Expression $mvnCmd
    $mvnExitCode = $LASTEXITCODE
    
    if ($mvnExitCode -ne 0) {
        Write-Error "FALHA NA COMPILAÇÃO! Verifique os erros acima."
        Write-Error "Deploy cancelado devido a erros de compilação."
        exit $mvnExitCode
    }
    
    Write-Host "Compilação concluída com sucesso!" -ForegroundColor Green
} else {
    Write-Output "=============================================="
    Write-Output "PASSO 1: Compilação ignorada (-SkipCompile)"
    Write-Output "=============================================="
}

# ============================================================================
# PASSO 2: Verificar/Criar WAR
# ============================================================================

Write-Output "=============================================="
Write-Output "PASSO 2: Preparando WAR..."
Write-Output "=============================================="

$warFiles = Get-ChildItem -Path .\target -Filter "spdealer*.war" | Sort-Object LastWriteTime -Descending
if ($warFiles.Count -eq 0) {
    Write-Error "Nenhum WAR encontrado em .\target"
    exit 1
}

$localWarBase = $warFiles[0].FullName
$localWar = Join-Path (Split-Path $localWarBase -Parent) $warName

# Copiar para nome correto
Copy-Item $localWarBase $localWar -Force
Write-Host "WAR preparado: $localWar" -ForegroundColor Green

if ($SkipDeploy) {
    Write-Output "=============================================="
    Write-Output "Deploy ignorado (-SkipDeploy)"
    Write-Output "=============================================="
    Write-Output "WAR disponível em: $localWar"
    exit 0
}

# ============================================================================
# PASSO 3: Deploy para servidor remoto
# ============================================================================

Write-Output "=============================================="
Write-Output "PASSO 3: Deploy para servidor remoto..."
Write-Output "=============================================="

$timestampStr = Get-Date -Format "yyyyMMddHHmmss"
$remoteTmp = "/tmp/" + $warName + "." + $timestampStr
$dest = $RemoteUser + "@" + $RemoteHost + ":" + $remoteTmp

Write-Output "Copiando $localWar para $dest ..."
& $ScpCmd -o StrictHostKeyChecking=no -o BatchMode=yes -P $SshPort $localWar $dest
AbortIfError $LASTEXITCODE "SCP falhou. Verifique se o SSH nativo do Windows está funcionando e com sua chave autorizada."

# Remote bash script
$remoteTemplate = @'
set -e
echo "Parando serviço {SERVICO} (se existir)..."
systemctl stop {SERVICO} || true

timestamp=$(date +%Y%m%d%H%M%S)
WEBAPPS="{WEBAPPS}"
WARNAME="{WARNAME}"

# Backup antigo
if [ -f "{WEBAPPS}/{WARNAME}" ]; then
  mv "{WEBAPPS}/{WARNAME}" "{WEBAPPS}/{WARNAME}.bak.$timestamp"
  echo "WAR antigo movido para backup"
fi
# Limpeza do contexto para evitar arquivos corrompidos
echo "Limpando aplicação {WARFOLDER} antiga..."
rm -rf "$WEBAPPS/{WARFOLDER}"
rm -f "$WEBAPPS/{WARNAME}"

echo "Limpando pastas temporárias (temp e work)..."
rm -rf /usr/local/tomcat10/temp/*
rm -rf /usr/local/tomcat10/work/*

echo "Movendo novo WAR para webapps..."
mv "{REMOTE_TMP}" "{WEBAPPS}/{WARNAME}"

echo "Definindo permissões (tentativa: tomcat:tomcat)..."
chown -R tomcat:tomcat "{WEBAPPS}/{WARNAME}" || true

echo "Iniciando serviço {SERVICO}..."
systemctl start {SERVICO} || { 
    echo "Falha ao iniciar {SERVICO}, tentando iniciar Tomcat diretamente..."
    if [ -f /usr/local/tomcat10/bin/startup.sh ]; then
        /usr/local/tomcat10/bin/shutdown.sh 2>/dev/null || true
        sleep 2
        /usr/local/tomcat10/bin/startup.sh
    fi
}

echo "Aguardando 8 segundos para Tomcat explodir WAR..."
sleep 8

echo "Estado do Tomcat:"
if [ -f /usr/local/tomcat10/logs/catalina.out ]; then
    echo "--- Últimas 50 linhas de catalina.out ---"
    tail -n 50 /usr/local/tomcat10/logs/catalina.out
fi

echo "Deploy remoto concluído com sucesso"
exit 0
'@

$warFolder = $warName -replace '\.war$', ''
$remoteCmd = $remoteTemplate -replace '\{SERVICO\}', $servico
$remoteCmd = $remoteCmd -replace '\{WEBAPPS\}', $RemoteTomcatWebapps
$remoteCmd = $remoteCmd -replace '\{WARNAME\}', $warName
$remoteCmd = $remoteCmd -replace '\{WARFOLDER\}', $warFolder
$remoteCmd = $remoteCmd -replace '\{REMOTE_TMP\}', $remoteTmp

$localRemoteScript = Join-Path $env:TEMP "spdealer-deploy-remote.sh.$timestampStr"
# Write with LF-only (Unix line endings) to avoid bash errors on remote Linux
[System.IO.File]::WriteAllText($localRemoteScript, ($remoteCmd -replace "`r\n", "`n"))

$sharedScriptSuffix = $timestampStr
$scriptDest = $RemoteUser + "@" + $RemoteHost + ":/tmp/spdealer-deploy-remote.sh." + $sharedScriptSuffix
Write-Output "Copiando script remoto para $scriptDest ..."
& $ScpCmd -o StrictHostKeyChecking=no -o BatchMode=yes -P $SshPort $localRemoteScript $scriptDest
AbortIfError $LASTEXITCODE "SCP do script remoto falhou"

Write-Output "Executando script remoto..."
& $SshCmd -o StrictHostKeyChecking=no -o BatchMode=yes -p $SshPort $RemoteUser@$RemoteHost "bash /tmp/spdealer-deploy-remote.sh.$sharedScriptSuffix"
$exitCode = $LASTEXITCODE

if ($exitCode -ne 0) {
    Write-Warning "Execução remota retornou código $exitCode. Verifique logs remotos."
} else {
    Write-Host "Script remoto finalizado com código 0" -ForegroundColor Green
}

# ============================================================================
# RESUMO
# ============================================================================

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  DEPLOY CONCLUÍDO" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

if ($Ambiente -eq "DEV") {
    Write-Host "URL de Teste: http://$RemoteHost:5070/$($warName -replace '\.war$', '')" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Para promover para PRODUÇÃO:" -ForegroundColor Yellow
    Write-Host "  .\deploy.ps1 -Ambiente PROD" -ForegroundColor Yellow
} else {
    Write-Host "URL de Produção: https://spdealer.seprocom.com.br" -ForegroundColor Cyan
}

Write-Host ""
