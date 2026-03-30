# Install Node.js LTS (Windows) script
# Usage: Execute this script in an elevated PowerShell (Run as Administrator)
# Example: Open PowerShell as Admin and run:
#   powershell -ExecutionPolicy Bypass -File .\scripts\install-node.ps1

Set-StrictMode -Version Latest

Write-Host "Iniciando instalação do Node.js LTS..." -ForegroundColor Cyan

try {
    Write-Host "Buscando lista de versões do Node.js..." -NoNewline
    $index = Invoke-RestMethod -Uri 'https://nodejs.org/dist/index.json' -UseBasicParsing
    Write-Host " OK"
} catch {
    Write-Host " ERRO" -ForegroundColor Red
    Write-Error "Não foi possível buscar a lista de versões do Node.js. Verifique a conexão de internet."
    exit 1
}

# Obter primeira versão LTS (index.json costuma vir em ordem decrescente)
$ltsEntry = $index | Where-Object { $_.lts -ne $false } | Select-Object -First 1
if (-not $ltsEntry) {
    Write-Error "Não foi possível determinar a versão LTS do Node.js a partir do index.json"
    exit 1
}

$version = $ltsEntry.version # ex: v18.20.0
Write-Host "Versão LTS detectada: $version"

$msiName = "node-$version-x64.msi"
$downloadUrl = "https://nodejs.org/dist/$version/$msiName"
$dest = Join-Path -Path $env:TEMP -ChildPath $msiName

Write-Host "Baixando $downloadUrl para $dest..."
try {
    Invoke-WebRequest -Uri $downloadUrl -OutFile $dest -UseBasicParsing -Verbose:$false
    Write-Host "Download concluído." -ForegroundColor Green
} catch {
    Write-Host "Falha no download: $_" -ForegroundColor Red
    exit 1
}

Write-Host "Executando instalador MSI (pode solicitar elevação de privilégios)..."
try {
    $args = "/i `"$dest`" /qn /norestart"
    $proc = Start-Process -FilePath msiexec.exe -ArgumentList $args -Wait -PassThru -NoNewWindow
    if ($proc.ExitCode -eq 0) {
        Write-Host "Instalação concluída com sucesso." -ForegroundColor Green
    } else {
        Write-Warning "O instalador retornou código de saída: $($proc.ExitCode). Pode ser necessário executar manualmente como Administrador."
    }
} catch {
    Write-Host "Erro ao executar o instalador: $_" -ForegroundColor Red
    Write-Host "Tente executar este script como Administrador ou execute manualmente o MSI: $dest"
    exit 1
}

# Verificar instalação
try {
    $node = & node -v 2>$null
    $npm = & npm -v 2>$null
    if ($node -and $npm) {
        Write-Host "Node instalado: $node" -ForegroundColor Green
        Write-Host "npm instalado: $npm" -ForegroundColor Green
    } else {
        Write-Warning "Node ou npm não encontrados no PATH após a instalação. Reinicie o terminal e verifique novamente."
    }
} catch {
    Write-Warning "Não foi possível executar 'node -v' após a instalação. Reinicie o terminal e verifique manualmente."
}

Write-Host "Fim do script." -ForegroundColor Cyan
