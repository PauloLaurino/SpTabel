$ErrorActionPreference = 'Stop'

$repoRoot = Split-Path -Parent $PSScriptRoot
$rtcWin = Join-Path $repoRoot 'RTC\windows'

$install = Join-Path $rtcWin '1-instalar.bat'
$run = Join-Path $rtcWin '2-executar.bat'

if (!(Test-Path $install) -or !(Test-Path $run)) {
  Write-Host "Scripts nao encontrados em: $rtcWin"
  exit 1
}

Write-Host "Instalando calculadora no WSL (pode exigir admin na primeira vez)..."
& $install

Write-Host "Executando calculadora no WSL..."
& $run
