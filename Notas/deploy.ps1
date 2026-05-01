# ==================================================
# SUPER DEPLOY UNIFICADO: NOTAS + GERENCIAL PRO-MAX
# Build completo de Java, React Frontends e Sub-serviços
# Suporte: Webrun Studio & Webrun 5 (Maker 5)
# ==================================================

$ErrorActionPreference = 'Stop'

# Diretório base (onde o script está: ...\Notas)
$scriptPath = Split-Path $MyInvocation.MyCommand.Path -Parent
# Diretório raiz dos projetos (...\Seprocom)
$rootDir = Split-Path $scriptPath -Parent

Write-Host ""
Write-Host "== [SUPER DEPLOY] Iniciando Build Unificado ==" -ForegroundColor Cyan

# 1. CONFIGURAÇÕES DE DESTINO
$targetWebapps = @(
    "C:\Program Files (x86)\Softwell Solutions\Maker Studio\Webrun Studio\tomcat\webapps",
    "C:\Program Files (x86)\Softwell Solutions\Maker 5\Webrun 5\tomcat\webapps"
)
$services = @("Webrun_Studio", "Webrun_5")

# 2. PARAR SERVIÇOS
Write-Host "`n>>> Parando serviços de aplicação..." -ForegroundColor Cyan
foreach ($svcName in $services) {
    $svc = Get-Service -Name $svcName -ErrorAction SilentlyContinue
    if ($svc -and $svc.Status -eq 'Running') {
        Write-Host "Parando $svcName..." -ForegroundColor Yellow
        try { 
            Stop-Service -Name $svcName -Force -ErrorAction Stop
            Write-Host "Serviço $svcName parado." -ForegroundColor Green
        } catch {
            Write-Warning "Não foi possível parar $svcName. Continuando assim mesmo..."
        }
    }
}

# 3. BUILD PROJETO: NOTAS
Write-Host "`n=== PROJETO: NOTAS (Legado) ===" -ForegroundColor Cyan
$notasPath = Join-Path $rootDir "Notas"
Set-Location $notasPath
Write-Host "Executando build Maven Notas..." -ForegroundColor Gray
& mvn clean package -DskipTests=true
if ($LASTEXITCODE -ne 0) { throw "Erro no build do projeto Notas" }

# 4. BUILD PROJETO: GERENCIAL (Moderno)
Write-Host "`n=== PROJETO: GERENCIAL (Moderno) ===" -ForegroundColor Cyan
$gerencialPath = Join-Path $rootDir "Gerencial"

# 4.1 Build Frontends (React)
$frontends = @(
    @{ Name = "NFS-e"; Path = "nfse-frontend"; Target = "src\main\webapp\nfse" },
    @{ Name = "Assinatura"; Path = "servico-assinatura-frontend"; Target = "src\main\webapp\servico-assinatura" }
)

foreach ($fe in $frontends) {
    $fePath = Join-Path $gerencialPath $fe.Path
    if (Test-Path $fePath) {
        Write-Host "Build Frontend $($fe.Name)..." -ForegroundColor Yellow
        Set-Location $fePath
        
        # Uso de cmd /c para garantir execução correta do npm no Windows
        Write-Host "Executando npm install..." -ForegroundColor Gray
        cmd /c npm install --quiet
        
        Write-Host "Executando npm run build..." -ForegroundColor Gray
        cmd /c npm run build
        
        # Copiar para webapp
        $distPath = Join-Path $fePath "dist"
        $targetPath = Join-Path $gerencialPath $fe.Target
        if (Test-Path $distPath) {
            if (Test-Path $targetPath) { Remove-Item $targetPath -Recurse -Force -ErrorAction SilentlyContinue }
            New-Item -ItemType Directory -Path $targetPath -Force | Out-Null
            Copy-Item -Path "$distPath\*" -Destination $targetPath -Recurse -Force
            Write-Host "Frontend $($fe.Name) atualizado em webapp." -ForegroundColor Green
        }
    }
}

# 4.2 Build Sub-serviço Assinatura (Java)
$assinaturaServicePath = Join-Path $gerencialPath "servico-assinatura"
if (Test-Path $assinaturaServicePath) {
    Write-Host "Build Sub-serviço Assinatura Java..." -ForegroundColor Yellow
    Set-Location $assinaturaServicePath
    & mvn clean package -DskipTests=true
    if ($LASTEXITCODE -ne 0) { Write-Warning "Falha no sub-serviço assinatura, mas continuando..." }
}

# 4.3 Build Principal Gerencial
Write-Host "Build Principal Gerencial Maven..." -ForegroundColor Yellow
Set-Location $gerencialPath
& mvn clean package -DskipTests=true
if ($LASTEXITCODE -ne 0) { throw "Erro no build do projeto Gerencial" }

# 5. DISTRIBUIÇÃO DOS WARS
$wars = @(
    @{ Name = "notas"; File = Join-Path $notasPath "target\notas.war" },
    @{ Name = "gerencial"; File = Join-Path $gerencialPath "target\gerencial.war" }
)

foreach ($war in $wars) {
    foreach ($webappsDir in $targetWebapps) {
        if (Test-Path $webappsDir) {
            $dest = Join-Path $webappsDir "$($war.Name).war"
            $exploded = Join-Path $webappsDir $war.Name
            
            Write-Host "Publicando $($war.Name) em $webappsDir" -ForegroundColor Gray
            if (Test-Path $exploded) { Remove-Item $exploded -Recurse -Force -ErrorAction SilentlyContinue }
            Copy-Item -Path $war.File -Destination $dest -Force
        }
    }
}

# 6. REINICIAR SERVIÇOS
Write-Host "`n>>> Reiniciando serviços..." -ForegroundColor Cyan
foreach ($svcName in $services) {
    $svc = Get-Service -Name $svcName -ErrorAction SilentlyContinue
    if ($svc) {
        Write-Host "Iniciando $svcName..." -ForegroundColor Yellow
        Start-Service -Name $svcName
        Write-Host "Serviço $svcName iniciado." -ForegroundColor Green
    }
}

Write-Host "`n[CONCLUÍDO] Sistema atualizado com sucesso!" -ForegroundColor Cyan
Set-Location $notasPath
