param(
    [string]$Token = "",
    [string]$RequestFile = "request.json",
    [string]$Cert = "",
    [string]$Senha = "",
    [int]$WaitForExpandSeconds = 20,
    [switch]$SkipMavenBuild,
    [string]$DBUser = "",
    [string]$DBPass = "",
    [string]$DBName = "sptabel",
    [string]$DBHost = "localhost",
    [int]$DBPort = 3306
)

$ErrorActionPreference = 'Stop'
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$workspaceRoot = Split-Path -Parent $scriptDir

Write-Host "run_local_tests (NOTAS): workspace=$workspaceRoot"

# Ler parametros da tabela se credenciais DB fornecidas
if ($DBUser -ne '') {
    Write-Host "Consultando tabela parametros em $DBName@$DBHost:$DBPort como $DBUser" -ForegroundColor Cyan
    $select = "select AMBIENTE_PAR, CONTEXTO_PAR, CAMINHOCERTIFICADO_PAR, SENHACERTIFICADO_PAR, TOKENFUNARPEN_PAR FROM parametros LIMIT 1"
    try {
        $mysqlArgs = @('-h', $DBHost, '-P', $DBPort.ToString(), '-u', $DBUser, $DBName, '-N', '-e', $select)
        if ($DBPass -ne '') { $mysqlArgs = @('-h', $DBHost, '-P', $DBPort.ToString(), '-u', $DBUser, "-p$DBPass", $DBName, '-N', '-e', $select) }
        $out = & mysql @mysqlArgs 2>&1
    } catch {
        Write-Warning "Falha ao executar mysql: $_"; $out = $null
    }
    if ($out) {
        $vals = $out -split "\t"
        if ($vals.Count -ge 5) {
            $AMBIENTE_PAR = $vals[0].Trim()
            $CONTEXTO_PAR = $vals[1].Trim()
            $CAMINHOCERTIFICADO_PAR = $vals[2].Trim()
            $SENHACERTIFICADO_PAR = $vals[3].Trim()
            $TOKENFUNARPEN_PAR = $vals[4].Trim()

            Write-Host "Parametros lidos: AMBIENTE=$AMBIENTE_PAR CONTEXTO=$CONTEXTO_PAR" -ForegroundColor Green

            if ($TOKENFUNARPEN_PAR -and $TOKENFUNARPEN_PAR -ne '') { $Token = $TOKENFUNARPEN_PAR }
            if ($CAMINHOCERTIFICADO_PAR -and $CAMINHOCERTIFICADO_PAR -ne '') { $Cert = $CAMINHOCERTIFICADO_PAR }
            if ($SENHACERTIFICADO_PAR -and $SENHACERTIFICADO_PAR -ne '') { $Senha = $SENHACERTIFICADO_PAR }

            # Ajustar destino do WAR conforme regras NOTAS: AMBIENTE D -> Maker Studio path; P -> /usr/local/tomcat8/webapps/sptabel
            if ($AMBIENTE_PAR -eq 'P') {
                $env:NOTAS_WARDEST = "/usr/local/tomcat8/webapps/sptabel.war"
            } else {
                # ambiente D (desenvolvimento) — usar Maker Studio Webrun Studio path
                $env:NOTAS_WARDEST = "C:\\Program Files (x86)\\Softwell Solutions\\Maker Studio\\Webrun Studio\\tomcat\\webapps\\sptabel.war"
            }
            # Também ajustar contexto se fornecido
            if ($CONTEXTO_PAR -and $CONTEXTO_PAR -ne '') {
                $env:NOTAS_CONTEXT = $CONTEXTO_PAR
            }

            Write-Host "NOTAS_WARDEST definido: $env:NOTAS_WARDEST" -ForegroundColor Cyan
        } else {
            Write-Warning "Resposta inesperada do mysql: $out"
        }
    } else {
        Write-Warning "Nenhum resultado obtido da consulta parametros." 
    }
}

# Build Maven (opcional)
if (-not $SkipMavenBuild) {
    Write-Host "Executando mvn clean package..." -ForegroundColor Cyan
    & mvn -f "$workspaceRoot\pom.xml" clean package
    if ($LASTEXITCODE -ne 0) { throw "mvn falhou (exit $LASTEXITCODE)" }
    Write-Host "Build Maven concluído." -ForegroundColor Green
} else {
    Write-Host "Pulando build Maven (-SkipMavenBuild)." -ForegroundColor Yellow
}

# Chamar deploy.ps1 (usa NOTAS_WARDEST/NOTAS_CONTEXT se definidos)
Write-Host "Executando deploy.ps1..." -ForegroundColor Cyan
try {
    & "$workspaceRoot\deploy.ps1"
} catch {
    Write-Error "deploy.ps1 falhou: $_"
    exit 1
}

Write-Host "Aguardando expansão do WAR por $WaitForExpandSeconds segundos..." -ForegroundColor Cyan
Start-Sleep -Seconds $WaitForExpandSeconds

# Se houver um teste HTTP configurado, pode-se chamar aqui (opcional)
Write-Host "run_local_tests (NOTAS) finalizado." -ForegroundColor Green
