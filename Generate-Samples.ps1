#Requires -Version 5.1
<#
.SYNOPSIS
    Generate sanitized JSON samples for FUNARPEN validation (Notas).
    Runs the GerarAmostraValidacao Java utility.

.DESCRIPTION
    Compiles the Notas project and runs GerarAmostraValidacao to generate
    sanitized JSON samples for types 401-459.

.SERVER
    100.75.153.127

.DATABASE
    sptabel

.OUTPUT
    C:\temp\funarpen-samples\Notas\
#>

param(
    [string]$Type = "",  # 401-459 or empty for all common types
    [int]$SamplesPerType = 5
)

$ErrorActionPreference = "Stop"
$projectRoot = "C:\Desenvolvimento\Seprocom\Notas"

# === FUNCTIONS ===
function Write-Step {
    param([string]$Message)
    Write-Host "[$(Get-Date -Format 'HH:mm:ss')] $Message" -ForegroundColor Cyan
}

function Write-Success {
    param([string]$Message)
    Write-Host "[$(Get-Date -Format 'HH:mm:ss')] $Message" -ForegroundColor Green
}

function Write-Err {
    param([string]$Message)
    Write-Host "[$(Get-Date -Format 'HH:mm:ss')] $Message" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Magenta
Write-Host "  Notas Sample Generator" -ForegroundColor Magenta
Write-Host "========================================" -ForegroundColor Magenta
Write-Host ""

# Output directory
$outputBase = "C:\temp\funarpen-samples\Notas"

# Change to project directory
Set-Location $projectRoot

# Set database environment variables
$env:DB_URL = "jdbc:mysql://100.75.153.127:3306/sptabel?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DB_USER = "root"
$env:DB_PASS = "k15720"

# Clean and compile with dependencies
Write-Step "Compiling Notas project..."
mvn clean compile -q -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Err "Maven compile failed"
    exit 1
}

Write-Success "Compile successful"

# Copy dependencies
Write-Step "Copying dependencies..."
mvn dependency:copy-dependencies -DoutputDirectory=target/lib -q

if ($LASTEXITCODE -ne 0) {
    Write-Err "Dependency copy failed"
    exit 1
}

Write-Success "Dependencies copied"

# Build classpath
$cp = "$projectRoot\target\classes;$projectRoot\target\lib\*"

Write-Step "Running GerarAmostraValidacao for Notas..."

# Build arguments as a single string
$javaArgs = "-cp `"$cp`" -Ddb.url=$env:DB_URL -Ddb.username=$env:DB_USER -Ddb.password=$env:DB_PASS com.selador.util.GerarAmostraValidacao"

if ($Type) {
    $javaArgs += " $Type"
}

# Run the Java utility
Start-Process -FilePath "java" -ArgumentList $javaArgs -NoNewWindow -Wait

if ($LASTEXITCODE -ne 0) {
    Write-Err "GerarAmostraValidacao failed with exit code $LASTEXITCODE"
} else {
    Write-Success "GerarAmostraValidacao completed"
}

# Find the generated output directory
$amostrasDir = Get-ChildItem -Path $projectRoot -Directory -Filter "amostras_validacao_*" |
    Sort-Object LastWriteTime -Descending | Select-Object -First 1

if ($amostrasDir) {
    Write-Step "Found output directory: $($amostrasDir.FullName)"
    
    # Copy to C:\temp\funarpen-samples
    Write-Step "Copying to $outputBase..."
    
    if (Test-Path $outputBase) {
        Remove-Item $outputBase -Recurse -Force
    }
    
    Copy-Item -Path $amostrasDir.FullName -Destination $outputBase -Recurse
    
    Write-Success "Samples copied to: $outputBase"
    
    # Show summary
    Write-Host ""
    Write-Host "Generated files:" -ForegroundColor Yellow
    Get-ChildItem -Path $outputBase -Recurse -Filter "*_pretty.json" | ForEach-Object {
        Write-Host "  $($_.FullName.Replace($outputBase, ''))" -ForegroundColor Gray
    }
} else {
    Write-Err "No output directory found"
}

Write-Host ""
