#Requires -Version 5.1
<#
.SYNOPSIS
    Generate 1 sample per type (405-459) for FUNARPEN validation (Notas).

.DESCRIPTION
    CompilesNotas and runs GerarAmostraValidacao for types 405-459.

.SERVER
    100.75.153.127

.DATABASE
    sptabel

.OUTPUT
    C:\temp\funarpen-samples\Notas\405-459\
#>

$ErrorActionPreference = "Continue"
$projectRoot = "C:\Desenvolvimento\Seprocom\Notas"

function Write-Step($msg) { Write-Host "[$(Get-Date -Format 'HH:mm:ss')] $msg" -ForegroundColor Cyan }
function Write-Success($msg) { Write-Host "[$(Get-Date -Format 'HH:mm:ss')] $msg" -ForegroundColor Green }
function Write-Err($msg) { Write-Host "[$(Get-Date -Format 'HH:mm:ss')] $msg" -ForegroundColor Red }

Write-Host ""
Write-Host "========================================" -ForegroundColor Magenta
Write-Host "  Notas Sample Generator (405-459)" -ForegroundColor Magenta
Write-Host "========================================" -ForegroundColor Magenta
Write-Host ""

$OutputBase = "C:\temp\funarpen-samples\Notas\405-459"
Set-Location $projectRoot

# Compile if needed
if (-not (Test-Path "$projectRoot\target\classes")) {
    Write-Step "Compiling..."
    mvn clean compile -q -DskipTests
    mvn dependency:copy-dependencies -DoutputDirectory=target/lib -q
}

$cp = "$projectRoot\target\classes;$projectRoot\target\lib\*"
$env:DB_URL = "jdbc:mysql://100.75.153.127:3306/sptabel?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DB_USER = "root"
$env:DB_PASS = "k15720"

Write-Step "Starting generation for types 405-459..."
Write-Host ""

$total = 0
$sucesso = 0

# First, query which types actually have data
Write-Step "Detecting types with data..."
$detectSql = "SELECT DISTINCT CAST(s.tipoato_selo AS UNSIGNED) as TIPO FROM selados s INNER JOIN selos sl ON sl.selo_sel = s.SELO WHERE s.tipoato_selo BETWEEN 405 AND 459 AND s.DATAENVIO >= '2026-01-01' AND s.JSON IS NOT NULL AND s.JSON != '' ORDER BY TIPO"

$env:DB_URL = "jdbc:mysql://100.75.153.127:3306/sptabel?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DB_USER = "root"
$env:DB_PASS = "k15720"

# Use MySQL CLI to detect types
$mysqlCmd = "mysql.exe -h 100.75.153.127 -P 3306 -u root -pk15720 sptabel -se `"$detectSql`""
$typesResult = Invoke-Expression $mysqlCmd 2>&1

$types = @()
if ($typesResult) {
    $lines = $typesResult -split "`n" | Where-Object { $_ -match '^\d+$' }
    foreach ($line in $lines) {
        $tipo = $line.Trim()
        if ($tipo -ge 405 -and $tipo -le 459) {
            $types += $tipo
        }
    }
}

Write-Success "Found $($types.Count) types with data"
Write-Host ""

# Create output directory
if (Test-Path $OutputBase) { Remove-Item $OutputBase -Recurse -Force }
New-Item -ItemType Directory -Path $OutputBase -Force | Out-Null

# Process each type
foreach ($tipo in $types) {
    $total++
    Write-Host "[$total/$($types.Count)] Type $tipo ... " -NoNewline -ForegroundColor Cyan
    
    # Run Java utility for this type
    $javaArgs = "-cp `"$cp`" -Ddb.url=$env:DB_URL -Ddb.username=$env:DB_USER -Ddb.password=$env:DB_PASS com.selador.util.GerarAmostraValidacao $tipo"
    
    # Clean up any old output directories
    Get-ChildItem -Path $projectRoot -Directory -Filter "amostras_validacao_*" -ErrorAction SilentlyContinue | 
        Remove-Item -Recurse -Force -ErrorAction SilentlyContinue
    
    # Run Java
    $process = Start-Process -FilePath "java" -ArgumentList $javaArgs -NoNewWindow -Wait -PassThru
    
    # Find the generated directory
    $amostrasDir = Get-ChildItem -Path $projectRoot -Directory -Filter "amostras_validacao_*" -ErrorAction SilentlyContinue | 
        Sort-Object LastWriteTime -Descending | Select-Object -First 1
    
    if ($amostrasDir) {
        $tipoDir = "$($amostrasDir.FullName)\tipo_$tipo"
        if (Test-Path $tipoDir) {
            $files = Get-ChildItem -Path $tipoDir -File -ErrorAction SilentlyContinue
            if ($files) {
                $destDir = "$OutputBase\tipo_$tipo"
                New-Item -ItemType Directory -Path $destDir -Force | Out-Null
                Copy-Item -Path $files.FullName -Destination $destDir -Force
                Write-Host "OK" -ForegroundColor Green
                $sucesso++
            } else {
                Write-Host "NO_FILES" -ForegroundColor Gray
            }
        } else {
            Write-Host "NO_TIPO_DIR" -ForegroundColor Gray
        }
        
        # Clean up
        Remove-Item $amostrasDir.FullName -Recurse -Force -ErrorAction SilentlyContinue
    } else {
        Write-Host "NO_OUTPUT" -ForegroundColor Gray
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Magenta
Write-Host "  SUMMARY" -ForegroundColor Magenta
Write-Host "========================================" -ForegroundColor Magenta
Write-Host "Types processed: $total"
Write-Host "Types with samples: $sucesso"
Write-Host ""

# List generated
Write-Step "Generated samples:"
Get-ChildItem -Path $OutputBase -Directory | ForEach-Object {
    $count = @(Get-ChildItem -Path $_.FullName -Filter "*_pretty.json" -ErrorAction SilentlyContinue).Count
    Write-Host "  $($_.Name): $count sample(s)" -ForegroundColor Yellow
}

Write-Host ""
Write-Success "Output: $OutputBase"
