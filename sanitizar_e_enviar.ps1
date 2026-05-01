# Script Orquestrador de Sanitização e Envio (MOTOR JAVA)
# Este script invoca o serviço oficial do projeto: com.selador.util.SeloJsonSanitizerNotas

param(
    [string]$DataInicio = "2026-02-26",
    [string]$DataFim = "2026-04-28",
    [string]$Modo = "sanitizar", # Opções: "sanitizar", "enviar", "completo"
    [string]$DBHost = "localhost", # IP do Servidor de Banco (100.75.153.127 ou 100.102.13.23)
    [string]$DBName = "sptabel",
    [string]$Selo = $null # Selo específico para forçar resanitização
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  ORQUESTRADOR JAVA - FUNARPEN V11.12" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# 1. Configurar Caminhos
$baseDir = "C:\Desenvolvimento\Seprocom\Notas"
$classesDir = "$baseDir\target\classes"
$libDir = "$baseDir\target\notas\WEB-INF\lib"

if (-not (Test-Path $classesDir)) {
    Write-Host "[ERRO] Binários não encontrados em $classesDir. O projeto foi compilado?" -ForegroundColor Red
    exit 1
}

# 2. Montar Classpath
$jars = Get-ChildItem "$libDir\*.jar" | ForEach-Object { $_.FullName }
$classpath = ($jars + $classesDir) -join ";"

# 3. Preparar Argumentos Java
$dbUrl = "jdbc:mariadb://{0}:3306/{1}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true" -f $DBHost, $DBName
$argModoJava = if ($Modo -eq "enviar") { "sendOnly" } elseif ($Modo -eq "sanitizar") { "sanitizeOnly" } else { "none" }

Write-Host ">>> Executando Serviço Java..." -ForegroundColor White
Write-Host ">>> Banco: $dbUrl" -ForegroundColor Gray
Write-Host ">>> Período: $DataInicio a $DataFim" -ForegroundColor Gray
Write-Host ">>> Modo: $Modo" -ForegroundColor Gray

# 4. Invocação do Java
# Escapamos a URL JDBC para evitar erros com o caractere '&' no PowerShell
$arg1 = if ($Selo) { "selo:$Selo" } else { $DataInicio }

$javaArgs = @(
    "-Ddb.url=$dbUrl",
    "-cp",
    "`"$classpath`"",
    "com.selador.util.SeloJsonSanitizerNotas",
    "`"$arg1`"",
    "`"$DataFim`"",
    "`"$argModoJava`""
)

Write-Host ">>> Iniciando JVM..." -ForegroundColor White
$process = Start-Process java -ArgumentList $javaArgs -Wait -NoNewWindow -PassThru

if ($process.ExitCode -ne 0) {
    Write-Host "[ERRO] O serviço Java retornou código de erro: $($process.ExitCode)" -ForegroundColor Red
}

Write-Host "`nProcessamento concluído." -ForegroundColor Cyan
