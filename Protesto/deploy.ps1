# ==================================================
# Deploy FUNARPEN + GERENCIAL - Maker 5 / Webrun 5
# Enhanced script: deploya funarpen.war (sistema antigo) e gerencial.war (Chat + NFSe + Boletos)
# ==================================================

$ErrorActionPreference = 'Stop'

$baseDir = Split-Path $MyInvocation.MyCommand.Path -Parent

Write-Host ""
Write-Host "== Deploy FUNARPEN (WAR) ==" -ForegroundColor Cyan

# ---------- CONFIGURÁVEIS ----------
$webappsDir = 'C:\Program Files (x86)\Softwell Solutions\Maker 5\Webrun 5\tomcat\webapps'
$WarName = $env:FUNARPEN_WARNAME; if (-not $WarName) { $WarName = 'funarpen.war' }
$WarDest = Join-Path $webappsDir $WarName
$SkipTests = $env:SKIP_TESTS; if (-not $SkipTests) { $SkipTests = $true }
$WaitExpandSeconds = 30
$SkipExtraModules = $env:SKIP_EXTRA_MODULES; if (-not $SkipExtraModules) { $SkipExtraModules = $false }
$FailOnExtraModuleBuildError = $env:FAIL_ON_EXTRA_MODULE_BUILD_ERROR; if (-not $FailOnExtraModuleBuildError) { $FailOnExtraModuleBuildError = $false }

function Add-MavenSkipTestArgs {
    param(
        [Parameter(Mandatory = $true)]
        [System.Collections.Generic.List[string]]$Args
    )

    if ($SkipTests -eq 'true' -or $SkipTests -eq $true) {
        $Args.Add('-DskipTests=true') | Out-Null
        $Args.Add('-Dmaven.test.skip=true') | Out-Null
    }
}

$warSource = Join-Path $baseDir "target\$WarName"

# Se o WAR esperado não existir, tentar localizar qualquer WAR gerado com prefixo funarpen
if (-not (Test-Path $warSource)) {
    Write-Host ("WAR {0} não encontrado - procurando por funarpen*.war em target..." -f $warSource) -ForegroundColor Yellow
    $found = Get-ChildItem -Path (Join-Path $baseDir 'target') -Filter 'funarpen*.war' -File -ErrorAction SilentlyContinue | Select-Object -First 1
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
} else {
    try { & java -version 2>&1 | Select-Object -First 1 | Write-Host } catch {}
}

Write-Host "Diretório do projeto: $baseDir"
Write-Host "WAR origem: $warSource"
Write-Host "WAR destino: $WarDest"

# ---------- FRONTEND BUILD (opcional) ----------
# Se existir a pasta frontend/selos-utilizados, tenta construir e copiar os artefatos
$FrontendDir = Join-Path $baseDir 'frontend\selos-utilizados'
$SkipFrontend = $env:SKIP_FRONTEND_BUILD; if (-not $SkipFrontend) { $SkipFrontend = $true }
if ($SkipFrontend -ne 'true' -and $SkipFrontend -ne $true) {
    if (Test-Path $FrontendDir) {
        Write-Host "Detectada app frontend em: $FrontendDir - iniciando build" -ForegroundColor Cyan
        $npmCmd = Get-Command npm -ErrorAction SilentlyContinue
        if ($npmCmd) {
            $pushed = $false
            try {
                Push-Location $FrontendDir; $pushed = $true

                # Preferir npm.cmd em Windows para evitar comportamentos do wrapper PowerShell
                $npmCmdExe = $null
                try {
                    $npmCmdObj = Get-Command npm -ErrorAction SilentlyContinue
                    if ($npmCmdObj) {
                        $npmFolder = Split-Path $npmCmdObj.Path -Parent
                        $candidate = Join-Path $npmFolder 'npm.cmd'
                        if (Test-Path $candidate) { $npmCmdExe = $candidate } else { $npmCmdExe = $npmCmdObj.Path }
                    } else {
                        $npmCmdExe = 'npm'
                    }
                } catch { $npmCmdExe = 'npm' }

                Write-Host "Executando: $npmCmdExe ci" -ForegroundColor Cyan
                try { & $npmCmdExe 'ci' } catch { Write-Warning "$npmCmdExe ci retornou erro: $_" }
                Write-Host "Executando: $npmCmdExe run build" -ForegroundColor Cyan
                try { & $npmCmdExe 'run' 'build' } catch { Write-Warning "$npmCmdExe run build retornou erro: $_" }
            } finally {
                if ($pushed) { Pop-Location }
            }

            # copiar dist para webapp/html/assets/selos-utilizados
            $distDir = Join-Path $FrontendDir 'dist'
            if (Test-Path $distDir) {
                $targetAssets = Join-Path $baseDir 'src\main\webapp\html\assets\selos-utilizados'
                try {
                    if (Test-Path $targetAssets) { Remove-Item -Path $targetAssets -Recurse -Force -ErrorAction SilentlyContinue }
                    New-Item -ItemType Directory -Path $targetAssets -Force | Out-Null
                    Copy-Item -Path (Join-Path $distDir '*') -Destination $targetAssets -Recurse -Force
                    Write-Host "Frontend assets copiados para: $targetAssets" -ForegroundColor Green
                    # --- FAST FIX: ensure frontend requests include application context (/funarpen)
                    try {
                        Write-Host "Aplicando correção rápida nos arquivos JS para prefixar /funarpen nas chamadas /maker/api/funarpen" -ForegroundColor Cyan
                        $jsFiles = Get-ChildItem -Path $targetAssets -Recurse -Include *.js -File -ErrorAction SilentlyContinue | Where-Object { $_.Name -notmatch 'normalizeFunarpen|chat-widget' }
                        foreach ($f in $jsFiles) {
                            try {
                                # Ler explicitamente como UTF8 para evitar leitura com encoding ANSI (causa mojibake)
                                $txt = Get-Content -LiteralPath $f.FullName -Raw -Encoding UTF8
                                if ($txt -match '/maker/api/funarpen') {
                                    $new = $txt -replace '/maker/api/funarpen', '/funarpen/maker/api/funarpen'
                                    Set-Content -LiteralPath $f.FullName -Value $new -Encoding UTF8
                                }
                            } catch {
                                Write-Warning "Falha ao processar $($f.FullName): $_"
                            }
                        }
                        Write-Host "Substituições aplicadas em $($jsFiles.Count) arquivos JS." -ForegroundColor Green
                    } catch {
                        Write-Warning "Erro ao aplicar correção rápida nos arquivos JS: $_"
                    }
                } catch {
                    Write-Warning "Falha ao copiar assets frontend: $_"
                }
            } else {
                Write-Warning "Pasta dist não encontrada em $distDir - pulando cópia dos assets."
            }
        } else {
            Write-Warning "npm não encontrado no PATH. Pulando build do frontend."
        }
    } else {
        Write-Host "Diretório frontend não encontrado ($FrontendDir) - pulando build frontend." -ForegroundColor DarkYellow
    }
} else {
    Write-Host "SKIP_FRONTEND_BUILD definido - pulando build frontend." -ForegroundColor Yellow
}

# ---------- BUILD MAVEN ----------
Write-Host "Executando mvn clean package..." -ForegroundColor Cyan
# montar argumentos como array para evitar problemas com espaços e parsing
$mvArgs = [System.Collections.Generic.List[string]]::new()
$mvArgs.AddRange([string[]]@('-f', "${baseDir}\pom.xml", 'clean', 'package'))
Add-MavenSkipTestArgs -Args $mvArgs

# Executa mvn e captura código
try {
    Write-Host "Chamando: mvn $([string]::Join(' ', $mvArgs))"
    & mvn @mvArgs
    $exitCode = $LASTEXITCODE
} catch {
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

# ---------- BUILD MÓDULOS ADICIONAIS ----------
# Compila todos os subprojetos Maven (exceto chat, Gerencial e diretórios temporários), para manter todas as funções sincronizadas.
$extraWarsToDeploy = @()
if ($SkipExtraModules -ne 'true' -and $SkipExtraModules -ne $true) {
    $excludedModuleNames = @('chat', 'tmp_war', 'target', 'serasa-microservice', 'Gerencial')
    $extraModuleDirs = Get-ChildItem -Path $baseDir -Directory -ErrorAction SilentlyContinue |
        Where-Object {
            $excludedModuleNames -notcontains $_.Name -and
            (Test-Path (Join-Path $_.FullName 'pom.xml'))
        }

    if ($extraModuleDirs.Count -gt 0) {
        Write-Host "Compilando módulos Maven adicionais..." -ForegroundColor Cyan
    }

    # Ordena para garantir que bibliotecas (jar) sejam compiladas antes de WARs que as utilizam
    $sortedModules = $extraModuleDirs | Sort-Object { 
        if ($_.Name -eq 'nfse') { "0" } 
        elseif ($_.Name -eq 'backend') { "1" }
        else { "2" + $_.Name } 
    }

    foreach ($moduleDir in $sortedModules) {
        $modulePom = Join-Path $moduleDir.FullName 'pom.xml'
        $moduleName = $moduleDir.Name
        $modulePackaging = 'jar'
        try {
            [xml]$modulePomXml = Get-Content -LiteralPath $modulePom -Raw
            if ($modulePomXml.project.packaging -and $modulePomXml.project.packaging.Trim()) {
                $modulePackaging = $modulePomXml.project.packaging.Trim().ToLowerInvariant()
            }
        } catch {
            Write-Warning "Não foi possível ler packaging do módulo '$moduleName'. Assumindo jar."
        }
        $moduleArgs = [System.Collections.Generic.List[string]]::new()
        $moduleArgs.AddRange([string[]]@('-f', $modulePom, 'clean', 'install'))
        Add-MavenSkipTestArgs -Args $moduleArgs

        try {
            Write-Host ("Chamando ({0}): mvn {1}" -f $moduleName, ([string]::Join(' ', $moduleArgs))) -ForegroundColor Cyan
            & mvn @moduleArgs
            $moduleExitCode = $LASTEXITCODE
            if ($moduleExitCode -ne 0) {
                $msg = "Build do módulo '$moduleName' falhou (exit $moduleExitCode)."
                $mustFail = ($modulePackaging -eq 'war') -or ($FailOnExtraModuleBuildError -eq 'true' -or $FailOnExtraModuleBuildError -eq $true)
                if ($mustFail) {
                    Write-Error $msg
                    exit $moduleExitCode
                }
                Write-Warning "$msg Continuando por configuração."
                continue
            }

            Write-Host "Build do módulo '$moduleName' OK." -ForegroundColor Green

            # Se o módulo também gerar WAR, adiciona para deploy automático no Tomcat.
            $moduleWar = Get-ChildItem -Path (Join-Path $moduleDir.FullName 'target') -Filter '*.war' -File -ErrorAction SilentlyContinue |
                Select-Object -First 1
            if ($moduleWar) {
                $extraWarsToDeploy += $moduleWar.FullName
                Write-Host ("WAR detectado no módulo '{0}': {1}" -f $moduleName, $moduleWar.Name) -ForegroundColor DarkGreen
            }
        } catch {
            $msg = "Falha ao executar build do módulo '$moduleName': $_"
            $mustFail = ($modulePackaging -eq 'war') -or ($FailOnExtraModuleBuildError -eq 'true' -or $FailOnExtraModuleBuildError -eq $true)
            if ($mustFail) {
                Write-Error $msg
                exit 1
            }
            Write-Warning "$msg Continuando por configuração."
        }
    }
} else {
    Write-Host "SKIP_EXTRA_MODULES definido - pulando build de módulos adicionais." -ForegroundColor Yellow
}

# ---------- BUILD CHAT (módulo separado) ----------
$ChatDir = Join-Path $baseDir 'chat'
$SkipChat = $env:SKIP_CHAT_BUILD; if (-not $SkipChat) { $SkipChat = $false }
if ($SkipChat -ne 'true' -and $SkipChat -ne $true) {
    if (Test-Path $ChatDir) {
        Write-Host "Detectado módulo chat em: $ChatDir - iniciando build" -ForegroundColor Cyan
        
        # 1. Build do frontend React (se existir)
        $ChatFrontendDir = Join-Path $ChatDir 'frontend'
        if (Test-Path $ChatFrontendDir) {
            $npmCmd = Get-Command npm -ErrorAction SilentlyContinue
            if ($npmCmd) {
                $pushed = $false
                try {
                    Push-Location $ChatFrontendDir; $pushed = $true
                    Write-Host "Build do frontend React do chat..." -ForegroundColor Cyan
                    
                    # Preferir npm.cmd em Windows
                    $npmCmdExe = $null
                    try {
                        $npmCmdObj = Get-Command npm -ErrorAction SilentlyContinue
                        if ($npmCmdObj) {
                            $npmFolder = Split-Path $npmCmdObj.Path -Parent
                            $candidate = Join-Path $npmFolder 'npm.cmd'
                            if (Test-Path $candidate) { $npmCmdExe = $candidate } else { $npmCmdExe = $npmCmdObj.Path }
                        } else {
                            $npmCmdExe = 'npm'
                        }
                    } catch { $npmCmdExe = 'npm' }

                    Write-Host "Executando: $npmCmdExe ci" -ForegroundColor Cyan
                    try { & $npmCmdExe 'ci' } catch { Write-Warning "$npmCmdExe ci retornou erro: $_" }
                    Write-Host "Executando: $npmCmdExe run build" -ForegroundColor Cyan
                    try { & $npmCmdExe 'run' 'build' } catch { Write-Warning "$npmCmdExe run build retornou erro: $_" }
                } finally {
                    if ($pushed) { Pop-Location }
                }
            } else {
                Write-Warning "npm não encontrado no PATH. Pulando build do frontend do chat."
            }
        }
        
        # 2. Build do WAR do chat
        Write-Host "Build do chat.war..." -ForegroundColor Cyan
        $chatMvArgs = [System.Collections.Generic.List[string]]::new()
        $chatMvArgs.AddRange([string[]]@('-f', "${ChatDir}\pom.xml", 'clean', 'package'))
        Add-MavenSkipTestArgs -Args $chatMvArgs
        
        try {
            Write-Host "Chamando: mvn $([string]::Join(' ', $chatMvArgs))"
            & mvn @chatMvArgs
            $chatExitCode = $LASTEXITCODE
            if ($chatExitCode -eq 0) {
                Write-Host "Build do chat.war OK." -ForegroundColor Green
                
                # Copiar chat.war para webapps
                $chatWarSource = Join-Path $ChatDir 'target\chat.war'
                $chatWarDest = Join-Path $webappsDir 'chat.war'
                if (Test-Path $chatWarSource) {
                    # Remover diretório explodido do chat se existir
                    $chatExplodedDir = Join-Path $webappsDir 'chat'
                    if (Test-Path $chatExplodedDir) {
                        Write-Host "Removendo diretório explodido do chat: $chatExplodedDir"
                        try {
                            Remove-Item -Path $chatExplodedDir -Recurse -Force -ErrorAction Stop
                        } catch {
                            Write-Warning "Não foi possível remover exploded dir do chat: $_"
                        }
                    }
                    
                    Copy-Item -Path $chatWarSource -Destination $chatWarDest -Force
                    Write-Host "chat.war publicado em: $chatWarDest" -ForegroundColor Green
                } else {
                    Write-Warning "chat.war não encontrado em: $chatWarSource"
                }
            } else {
                Write-Warning "Build do chat.war falhou (exit $chatExitCode). Continuando sem o chat."
            }
        } catch {
            Write-Warning "Falha ao executar build do chat: $_"
        }
    } else {
        Write-Host "Diretório do chat não encontrado ($ChatDir) - pulando build do chat." -ForegroundColor DarkYellow
    }
} else {
    Write-Host "SKIP_CHAT_BUILD definido - pulando build do chat." -ForegroundColor Yellow
}

# ---------- BUILD GERENCIAL (Chat + NFSe + Boletos unificados) ----------
$GerencialDir = Join-Path $baseDir 'Gerencial'
$SkipGerencial = $env:SKIP_GERENCIAL_BUILD; if (-not $SkipGerencial) { $SkipGerencial = $false }
if ($SkipGerencial -ne 'true' -and $SkipGerencial -ne $true) {
    if (Test-Path $GerencialDir) {
        Write-Host "Detectado módulo Gerencial em: $GerencialDir - iniciando build" -ForegroundColor Cyan
        Write-Host "Módulo unificado: Chat + NFSe + Boletos" -ForegroundColor DarkGreen
        
        # Build do Gerencial WAR
        Write-Host "Build do gerencial.war..." -ForegroundColor Cyan
        $gerencialMvArgs = [System.Collections.Generic.List[string]]::new()
        $gerencialMvArgs.AddRange([string[]]@('-f', "${GerencialDir}\pom.xml", 'clean', 'package'))
        Add-MavenSkipTestArgs -Args $gerencialMvArgs
        
        try {
            Write-Host "Chamando: mvn $([string]::Join(' ', $gerencialMvArgs))"
            & mvn @gerencialMvArgs
            $gerencialExitCode = $LASTEXITCODE
            if ($gerencialExitCode -eq 0) {
                Write-Host "Build do gerencial.war OK." -ForegroundColor Green
                
                # Copiar gerencial.war para webapps
                $gerencialWarSource = Join-Path $GerencialDir 'target\gerencial.war'
                $gerencialWarDest = Join-Path $webappsDir 'gerencial.war'
                if (Test-Path $gerencialWarSource) {
                    # Remover diretório explodido do gerencial se existir
                    $gerencialExplodedDir = Join-Path $webappsDir 'gerencial'
                    if (Test-Path $gerencialExplodedDir) {
                        Write-Host "Removendo diretório explodido do gerencial: $gerencialExplodedDir"
                        try {
                            Remove-Item -Path $gerencialExplodedDir -Recurse -Force -ErrorAction Stop
                        } catch {
                            Write-Warning "Não foi possível remover exploded dir do gerencial: $_"
                        }
                    }
                    
                    Copy-Item -Path $gerencialWarSource -Destination $gerencialWarDest -Force
                    Write-Host "gerencial.war publicado em: $gerencialWarDest" -ForegroundColor Green
                } else {
                    Write-Warning "gerencial.war não encontrado em: $gerencialWarSource"
                }
            } else {
                Write-Warning "Build do gerencial.war falhou (exit $gerencialExitCode). Continuando sem o gerencial."
            }
        } catch {
            Write-Warning "Falha ao executar build do gerencial: $_"
        }
    } else {
        Write-Host "Diretório do gerencial não encontrado ($GerencialDir) - pulando build do gerencial." -ForegroundColor DarkYellow
    }
} else {
    Write-Host "SKIP_GERENCIAL_BUILD definido - pulando build do gerencial." -ForegroundColor Yellow
}

# ---------- PREPARAR TOMCAT / BACKUP ----------
$explodedDir = Join-Path $webappsDir 'funarpen'
$ts = Get-Date -Format 'yyyyMMddHHmmss'

# (Removido) Backup automático do WAR destino: anteriormente o script copiava o WAR atual
# para um arquivo de backup com timestamp. Esto foi removido por solicitação.

# Removido: renomeação/backup do diretório explodido.
# Agora removemos o diretório explodido diretamente, sem criar cópia/backup.
if (Test-Path $explodedDir) {
    Write-Host "Removendo diretório explodido: $explodedDir"
    try {
        Remove-Item -Path $explodedDir -Recurse -Force -ErrorAction Stop
        Write-Host 'Exploded dir removido.' -ForegroundColor Yellow
    } catch {
        Write-Warning "Não foi possível remover exploded dir: $_"
    }
}

# ---------- Tentar parar Tomcat (opcional) ----------
$shutdownRan = $false
$tomcatDir = Split-Path $webappsDir -Parent
$shutdownScript = Join-Path $tomcatDir 'bin\shutdown.bat'
$startupScript = Join-Path $tomcatDir 'bin\startup.bat'

# tentar parar pelo serviço Windows se existir (nome padrão Webrun_5 ou variável de ambiente TOMCAT_SERVICE_NAME)
$TomcatServiceName = $env:TOMCAT_SERVICE_NAME; if (-not $TomcatServiceName) { $TomcatServiceName = 'Webrun_5' }
$svc = Get-Service -Name $TomcatServiceName -ErrorAction SilentlyContinue
if ($svc) {
    Write-Host "Encontrado serviço Windows '${TomcatServiceName}' - tentando pará-lo via Stop-Service" -ForegroundColor Cyan
    try {
        Stop-Service -Name $TomcatServiceName -Force -ErrorAction Stop
        # esperar até que o serviço pare (timeout curto)
        $wait = 0
        while ((Get-Service -Name $TomcatServiceName).Status -ne 'Stopped' -and $wait -lt 15) { Start-Sleep -Seconds 1; $wait += 1 }
        if ((Get-Service -Name $TomcatServiceName).Status -eq 'Stopped') {
            $shutdownRan = $true
            Write-Host "Serviço ${TomcatServiceName} parado." -ForegroundColor Yellow
        } else {
            Write-Warning "Serviço ${TomcatServiceName} não parou dentro do timeout."
        }
    } catch { Write-Warning "Falha ao parar serviço ${TomcatServiceName}: $_" }
} else {
    # fallback: tentar usar shutdown.bat
    if (Test-Path $shutdownScript) {
        Write-Host "Tentando executar shutdown do Tomcat (shutdown.bat): $shutdownScript" -ForegroundColor Cyan
        try {
            & "$shutdownScript"
            Start-Sleep -Seconds 4
            $shutdownRan = $true
            Write-Host "Shutdown solicitado via shutdown.bat." -ForegroundColor Yellow
        } catch { Write-Warning "Falha ao executar shutdown.bat: $_" }
    } else {
        Write-Host ("shutdown.bat não encontrado em {0}\bin - pulando tentativa de parada automática." -f $tomcatDir) -ForegroundColor DarkYellow
    }
}

# ---------- Copiar WAR ----------
Write-Host "Copiando WAR para Tomcat Webrun..." -ForegroundColor Cyan
try {
    Copy-Item -Path $warSource -Destination $WarDest -Force
    Write-Host "WAR publicado com sucesso em $WarDest" -ForegroundColor Green
} catch {
    Write-Error "Falha ao copiar WAR: $_"; exit 1
}

# ---------- Publicar WARs adicionais detectados ----------
if ($extraWarsToDeploy.Count -gt 0) {
    Write-Host "Publicando WARs adicionais detectados..." -ForegroundColor Cyan
    foreach ($extraWar in $extraWarsToDeploy) {
        try {
            $extraWarName = Split-Path $extraWar -Leaf
            if ($extraWarName -ieq $WarName -or $extraWarName -ieq 'chat.war') { continue }

            $extraWarDest = Join-Path $webappsDir $extraWarName
            $extraExplodedDirName = [System.IO.Path]::GetFileNameWithoutExtension($extraWarName)
            $extraExplodedDir = Join-Path $webappsDir $extraExplodedDirName

            if (Test-Path $extraExplodedDir) {
                Write-Host "Removendo diretório explodido adicional: $extraExplodedDir"
                try { Remove-Item -Path $extraExplodedDir -Recurse -Force -ErrorAction Stop } catch { Write-Warning "Falha ao remover ${extraExplodedDir}: $_" }
            }

            Copy-Item -Path $extraWar -Destination $extraWarDest -Force
            Write-Host "WAR adicional publicado: $extraWarDest" -ForegroundColor Green
        } catch {
            Write-Warning "Falha ao publicar WAR adicional '$extraWar': $_"
        }
    }
}

# ---------- Copiar Menu para Maker.Commons (sistemas STP) ----------
$menuSource = Join-Path $baseDir 'Menu'
$menuDestBase = 'C:\Program Files (x86)\Softwell Solutions\Maker 5\Webrun 5\systems\Maker.Commons'
if (Test-Path $menuSource) {
    Write-Host "Copiando Menu para Maker.Commons..." -ForegroundColor Cyan
    try {
        $menuDest = Join-Path $menuDestBase 'Menu'
        if (Test-Path $menuDest) {
            Remove-Item -Path $menuDest -Recurse -Force -ErrorAction SilentlyContinue
        }
        Copy-Item -Path $menuSource -Destination $menuDest -Recurse -Force
        Write-Host "Menu copiado para: $menuDest" -ForegroundColor Green
    } catch {
        Write-Warning "Falha ao copiar Menu: $_"
    }
} else {
    Write-Warning "Pasta Menu não encontrada em: $menuSource"
}

# ---------- Tentar iniciar Tomcat se parámos ----------
if ($shutdownRan) {
    # se o serviço existe, usaremos Start-Service
    $svc2 = Get-Service -Name $TomcatServiceName -ErrorAction SilentlyContinue
    if ($svc2) {
        Write-Host "Iniciando serviço Windows ${TomcatServiceName}..." -ForegroundColor Cyan
        try { Start-Service -Name $TomcatServiceName -ErrorAction Stop; Start-Sleep -Seconds 6; Write-Host "Serviço ${TomcatServiceName} solicitado start." -ForegroundColor Yellow } catch { Write-Warning "Falha ao iniciar serviço ${TomcatServiceName}: $_" }
    } elseif (Test-Path $startupScript) {
        Write-Host "Iniciando Tomcat via $startupScript" -ForegroundColor Cyan
        try { & "$startupScript"; Start-Sleep -Seconds 6; Write-Host "Startup solicitado." -ForegroundColor Yellow } catch { Write-Warning "Falha ao executar startup.bat: $_" }
    }
}

# ---------- Esperar expansão do WAR (verifica arquivo HTML) ----------
$expectedFile = Join-Path $explodedDir 'html\monitor_selos_funarpen.html'
Write-Host "Aguardando expansão do WAR (checando $expectedFile) por até $WaitExpandSeconds segundos..."
$elapsed = 0; $found = $false
while ($elapsed -lt $WaitExpandSeconds) {
    if (Test-Path $expectedFile) { $found = $true; break }
    Start-Sleep -Seconds 2; $elapsed += 2
}
if ($found) {
    Write-Host "Arquivo encontrado - deploy concluído e o conteúdo está acessível." -ForegroundColor Green
}
if (-not $found) {
    Write-Warning "Arquivo esperado não encontrado após $WaitExpandSeconds segundos. Pode ser necessário reiniciar o serviço Tomcat manualmente."
}

Write-Host ""; Write-Host "Deploy finalizado." -ForegroundColor Cyan
