param(
  [int]$HostApiPort = 18081,
  [int]$HostUiPort = 18000,
  [int]$HostAltPort = 18080
)

$ErrorActionPreference = 'Stop'

$repoRoot = Split-Path -Parent $PSScriptRoot
$tarPath = Join-Path $repoRoot 'RTC\calculadora.tar.gz'

if (!(Test-Path $tarPath)) {
  Write-Host "Arquivo nao encontrado: $tarPath"
  exit 1
}

Write-Host "Verificando Docker..."
$docker = Get-Command docker -ErrorAction SilentlyContinue
if (-not $docker) {
  Write-Host "Docker nao encontrado no PATH. Instale Docker Desktop (Windows) ou execute via WSL conforme RTC\\windows\\*.bat."
  exit 1
}

Write-Host "Importando imagem (se necessario)..."
& docker image inspect calculadora:latest *> $null
if ($LASTEXITCODE -ne 0) {
  & docker import $tarPath calculadora:latest | Out-Null
}

Write-Host "Iniciando calculadora offline em Docker..."
Write-Host "- UI (se existir): http://localhost:$HostUiPort/"
Write-Host "- API: http://localhost:$HostApiPort/api/calculadora/regime-geral"
Write-Host "- Alt: http://localhost:$HostAltPort/"

# IMPORTANTE: nao usar 8080 no host (conflita com SPDealer)
& docker run -t -i --rm `
  -p "$HostAltPort:8080" `
  -p "$HostApiPort:8081" `
  -p "$HostUiPort:80" `
  -w /calculadora `
  --name calculadora-container `
  calculadora:latest bash start.sh
