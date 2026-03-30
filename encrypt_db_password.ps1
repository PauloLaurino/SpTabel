<#
Script: encrypt_db_password.ps1
Propósito: gerar cipher Jasypt usando a classe Spr via Maven e atualizar um arquivo db.properties

Uso:
.\encrypt_db_password.ps1 [-MasterKey <key>] [-Password <plain>] [-InFile db.properties.example] [-OutFile db.properties]

#>

param(
    [string]$MasterKey,
    [string]$Password,
    [string]$InFile = "db.properties.example",
    [string]$OutFile = "db.properties"
)

function Read-Plain([string]$prompt){
    $sec = Read-Host -AsSecureString $prompt
    return [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($sec))
}

if (-not $MasterKey -or $MasterKey -eq ""){
    $MasterKey = Read-Host "MasterKey (será gravada no servidor em C:\\ProgramData\\spr\\master.key)"
}
if (-not $Password -or $Password -eq ""){
    $Password = Read-Plain "Senha do DB (entrada oculta)"
}

$mvn = Get-Command mvn -ErrorAction SilentlyContinue
if ($null -eq $mvn){
    Write-Error "Maven não encontrado no PATH. Instale Maven ou execute o comando manualmente conforme README_DB_ENCRYPT.md"
    exit 1
}

Write-Host "Construindo projeto (mvn package -DskipTests) para garantir classe disponível..." -ForegroundColor Cyan
$rc = & mvn -q package -DskipTests
if ($LASTEXITCODE -ne 0){ Write-Warning "mvn package retornou código $LASTEXITCODE, continue mesmo assim" }

Write-Host "Executando Spr para gerar cipher..." -ForegroundColor Cyan
$argsEnc = "encrypt $MasterKey $Password"
$out = & mvn -q "org.codehaus.mojo:exec-maven-plugin:3.0.0:java" "-Dexec.mainClass=com.monitor.funarpen.util.spr.Spr" "-Dexec.args=$argsEnc" 2>&1
if ($LASTEXITCODE -ne 0){
    Write-Error "Falha ao executar Spr via Maven. Saída:\n$out"
    exit 1
}

# extrair última linha não-vazia como cipher
$lines = $out | Where-Object { $_ -and $_.Trim() -ne '' }
if (-not $lines){ Write-Error "Nenhuma saída capturada do Spr."; exit 1 }
$cipher = $lines[-1].Trim()
Write-Host "Cipher gerado: $cipher" -ForegroundColor Green

# preparar conteúdo de saída
if (Test-Path $InFile){
    $txt = Get-Content $InFile -Raw
} else {
    $txt = "DB_URL=jdbc:mysql://localhost:3306/sptabel?serverTimezone=UTC&useSSL=false`nDB_USER=root`nDB_PASS=ENC($cipher)`n"
}

if ($txt -match "(?m)^DB_PASS=.*$"){
    $txt = $txt -replace "(?m)^DB_PASS=.*$", "DB_PASS=ENC($cipher)"
} else {
    $txt = $txt.TrimEnd() + "`nDB_PASS=ENC($cipher)`n"
}

[System.IO.File]::WriteAllText($OutFile,$txt,[System.Text.Encoding]::UTF8)
Write-Host "Arquivo atualizado: $OutFile" -ForegroundColor Green
Write-Host "Copie $OutFile para C:\\ProgramData\\spr no servidor e crie master.key com a mesma MasterKey." -ForegroundColor Cyan
