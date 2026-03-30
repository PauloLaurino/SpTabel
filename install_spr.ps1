<#
.Installador mínimo para projeto Notas (TTB)
- Gera payload com master.key e db.properties
- Envia para servidor remoto (WinRM / UNC) e instala como sptabel.war em webapps
#>

$RemoteIp = Read-Host "IP Tailscale do servidor (ex: 100.101.102.103)"
$inputMaster = Read-Host "MasterKey para criptografia (deixe vazio para gerar nova)"
if ($inputMaster -and $inputMaster.Trim() -ne ""){
    $masterKey = $inputMaster.Trim()
    Write-Host "MasterKey fornecida e sera usada para gerar valores ENC(...)" -ForegroundColor Green
} else {
    $masterKey = $null
    Write-Host "Nenhuma MasterKey informada - sera gerada automaticamente (se necessario)." -ForegroundColor Yellow
}

# Parâmetros padrão adaptados para Notas
$DbUrl = 'jdbc:mysql://localhost:3306/spprot?serverTimezone=UTC&useSSL=false'
$DbUser = "root"
$DbPass = "k15720"

$RemoteTomcatHome = "C:\Program Files (x86)\Apache Software Foundation\Tomcat 9.0"
$RemoteTomcatServiceAccount = "NT AUTHORITY\NetworkService"

# War local padrão (script dentro da pasta Notas)
$WarLocalPath = Join-Path $PSScriptRoot 'target\sptabel.war'
$MysqlConnectorJarLocal = "C:\installs\mysql-connector-java-8.0.33.jar"

$ConfigureJndi = $true
$JndiName = "jdbc/spr"
$SetSystemEnvRemote = $false

$LocalTemp = Join-Path $env:TEMP "install_sptabel_payload"

function Fail($msg){ Write-Error $msg; exit 1 }

Write-Host "[install_sptabel] Iniciando..." -ForegroundColor Cyan

if (Test-Path $LocalTemp){ Remove-Item $LocalTemp -Recurse -Force -ErrorAction SilentlyContinue }
New-Item -ItemType Directory -Path $LocalTemp | Out-Null
$cfgDir = Join-Path $LocalTemp "spr"
New-Item -ItemType Directory -Path $cfgDir -Force | Out-Null

$masterPath = Join-Path $cfgDir "master.key"
if (-not $masterKey -or $masterKey -eq $null){
    $masterKey = [System.Guid]::NewGuid().ToString("N")
    Write-Host "MasterKey nao informada - gerando nova key local." -ForegroundColor Yellow
} else {
    Write-Host "Usando MasterKey informada pelo usuario." -ForegroundColor Green
}
[System.IO.File]::WriteAllText($masterPath,$masterKey)
Write-Host "Gravado master.key (local) -> $masterPath"

function Try-EncryptPassword([string]$master, [string]$plain){
    $cipher = $null
    Write-Host "Tentando criptografar DB_PASS localmente usando maven+Spr..." -ForegroundColor Yellow
    try{
        $mvnExe = Get-Command mvn -ErrorAction SilentlyContinue
        if ($null -ne $mvnExe){
            $argsEnc = "encrypt $master $plain"
            $out = & mvn -q "org.codehaus.mojo:exec-maven-plugin:3.0.0:java" "-Dexec.mainClass=com.monitor.funarpen.util.spr.Spr" "-Dexec.args=$argsEnc" 2>&1
            if ($LASTEXITCODE -eq 0 -and $out){
                $lines = $out | Where-Object { $_ -and $_.Trim() -ne '' }
                if ($lines) { $cipher = $lines[-1].Trim() }
            } else {
                Write-Warning "mvn/Spr nao retornou cipher (exit $LASTEXITCODE). Saida: $out"
            }
        } else {
            Write-Warning "Maven nao encontrado no PATH do desenvolvedor; nao sera possivel criptografar automaticamente."
        }
    } catch {
        Write-Warning "Erro ao tentar criptografar: $($_.Exception.Message)"
    }
    return $cipher
}

$cipherText = Try-EncryptPassword $masterKey $DbPass
$dbPropsPath = Join-Path $cfgDir "db.properties"
if ($cipherText){
    $props = @("DB_URL=$DbUrl","DB_USER=$DbUser","DB_PASS=ENC($cipherText)")
    Write-Host "db.properties sera gerado com DB_PASS criptografado (ENC(...))"
} else {
    $props = @("DB_URL=$DbUrl","DB_USER=$DbUser","DB_PASS=$DbPass")
    Write-Host "db.properties sera gerado com DB_PASS em texto claro. Recomenda-se rodar criptografia no dev e reenviar." -ForegroundColor Yellow
}
[System.IO.File]::WriteAllLines($dbPropsPath,$props,[System.Text.Encoding]::UTF8)
Write-Host "Criado db.properties (local) -> $dbPropsPath"

# preparar payload
$payloadZip = Join-Path $LocalTemp "sptabel_payload.zip"
if (Test-Path $payloadZip){ Remove-Item $payloadZip -Force }
Add-Type -AssemblyName System.IO.Compression.FileSystem
[System.IO.Compression.ZipFile]::CreateFromDirectory($cfgDir,$payloadZip)
Write-Host "Payload gerado: $payloadZip"

Write-Host "Tentando enviar payload para o servidor remoto $RemoteIp..." -ForegroundColor Cyan
$cred = Get-Credential -Message "Credenciais administrativas para $RemoteIp (use ADMIN)"

function Try-UsePSSession($ip, $credential){
    try{ $sess = New-PSSession -ComputerName $ip -Credential $credential -ErrorAction Stop; return $sess } catch { return $null }
}

$session = Try-UsePSSession $RemoteIp $cred
if ($session -ne $null){
    Write-Host "Conectado via PowerShell Remoting (WinRM)." -ForegroundColor Green
    Copy-Item -ToSession $session -Path $payloadZip -Destination "C:\Windows\Temp\sptabel_payload.zip" -Force
    if (Test-Path $WarLocalPath){ Copy-Item -ToSession $session -Path $WarLocalPath -Destination "C:\Windows\Temp\$(Split-Path $WarLocalPath -Leaf)" -Force }
    Invoke-Command -Session $session -ScriptBlock {
        param($destZip,$tomcatHome,$warLocalName,$mysqlJarName,$configureJndi,$jndiName,$dbDir,$setEnv,$dbUser,$dbPass,$dbUrl)
        Expand-Archive -Path $destZip -DestinationPath $dbDir -Force
        New-Item -ItemType Directory -Path "C:\ProgramData\spr" -Force | Out-Null
        Copy-Item -Path (Join-Path $dbDir "*") -Destination "C:\ProgramData\spr" -Recurse -Force
        icacls "C:\ProgramData\spr\master.key" /inheritance:r | Out-Null
        icacls "C:\ProgramData\spr\master.key" /grant:r SYSTEM:F | Out-Null
        icacls "C:\ProgramData\spr\db.properties" /inheritance:r | Out-Null
        icacls "C:\ProgramData\spr\db.properties" /grant:r SYSTEM:F | Out-Null
        # copiar mysql connector
        if ($mysqlJarName -and (Test-Path (Join-Path $env:TEMP $mysqlJarName))){
            $tomcatLib = Join-Path $tomcatHome 'lib'
            New-Item -ItemType Directory -Path $tomcatLib -Force | Out-Null
            Copy-Item -Path (Join-Path $env:TEMP $mysqlJarName) -Destination $tomcatLib -Force
        }
        # copiar WAR e renomear para sptabel.war
        $webapps = Join-Path $tomcatHome 'webapps'
        if ($warLocalName -and (Test-Path (Join-Path $env:TEMP $warLocalName))){
            Copy-Item -Path (Join-Path $env:TEMP $warLocalName) -Destination (Join-Path $webapps 'sptabel.war') -Force
        }
        # inserir JNDI
        if ($configureJndi){
            $confContext = Join-Path $tomcatHome 'conf\context.xml'
            if (Test-Path $confContext){ Copy-Item $confContext "$confContext.bak_$(Get-Date -Format yyyyMMddHHmmss)" -Force }
            $resourceXml = '  <Resource name="' + $jndiName + '" auth="Container" type="javax.sql.DataSource" username="' + $dbUser + '" password="' + $dbPass + '" driverClassName="com.mysql.cj.jdbc.Driver" url="' + $dbUrl + '"/>'
            $content = ""
            if (Test-Path $confContext){ $content = Get-Content $confContext -Raw }
            if ($content -match "</Context>"){
                $content = $content -replace "</Context>", "$resourceXml`n</Context>"
            } else { $content = "<Context>`n$resourceXml`n</Context>" }
            $content | Out-File -FilePath $confContext -Encoding UTF8 -Force
        }
        return @{ success = $true }
    } -ArgumentList "C:\Windows\Temp\sptabel_payload.zip", $RemoteTomcatHome, (Split-Path $WarLocalPath -Leaf), (Split-Path $MysqlConnectorJarLocal -Leaf), $ConfigureJndi, $JndiName, "C:\Windows\Temp\spr", $SetSystemEnvRemote, $DbUser, $DbPass, $DbUrl
    Write-Host "Payload e WAR instalados via WinRM." -ForegroundColor Green
    if (Test-Path $LocalTemp){ Remove-Item $LocalTemp -Recurse -Force -ErrorAction SilentlyContinue }
    Remove-PSSession $session
} else {
    Write-Warning "Nao foi possivel conectar via PowerShell Remoting. Tentando copiar via UNC (\\$RemoteIp\\C$)..."
    $remoteRoot = "\\$RemoteIp\C$"
    try{
        $psDriveName = "RMT"
        New-PSDrive -Name $psDriveName -PSProvider FileSystem -Root $remoteRoot -Credential $cred -ErrorAction Stop | Out-Null
        # copiar WAR se existir
        $remoteWebappsWar = Join-Path $RemoteTomcatHome 'webapps\sptabel.war'
        if (Test-Path $WarLocalPath){ Copy-Item -Path $WarLocalPath -Destination $remoteWebappsWar -Force -Credential $cred }
        # copiar arquivos spr
        $remoteSpr = "\\$RemoteIp\C$\ProgramData\spr"
        if (-not (Test-Path "$remoteSpr")){ New-Item -Path "$remoteSpr" -ItemType Directory -Force | Out-Null }
        Copy-Item -Path $cfgDir\* -Destination $remoteSpr -Recurse -Force -Credential $cred
        Write-Host "Arquivos copiados via UNC para $remoteSpr" -ForegroundColor Green
        Remove-PSDrive -Name $psDriveName -Force -ErrorAction SilentlyContinue
        Remove-Item $LocalTemp -Recurse -Force -ErrorAction SilentlyContinue
    } catch {
        Write-Error "Copia via UNC falhou: $($_.Exception.Message)"
    }
}

# abrir pasta local do payload
try{
    if (Test-Path $payloadZip){ $payloadFolder = Split-Path $payloadZip -Parent } elseif (Test-Path $LocalTemp){ $payloadFolder = $LocalTemp } else { $payloadFolder = $env:TEMP }
    Write-Host "Abrindo pasta do payload: $payloadFolder" -ForegroundColor Cyan
    Start-Process explorer $payloadFolder
} catch { Write-Warning "Falha ao abrir Explorer: $($_.Exception.Message)" }
