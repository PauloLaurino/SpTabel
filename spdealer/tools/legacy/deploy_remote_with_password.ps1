<#
deploy_remote_with_password.ps1

Descrição:
- Script interativo que pede usuário/host e senha (SecureString) e usa Posh-SSH
  para copiar `deploy\spdealer-1.0.0.war` para `/tmp` no servidor e executar
  comandos remotos (backup, mover, restart systemd, exibir logs).

Requisitos:
- PowerShell 5.1+ (Windows)
- Módulo `Posh-SSH` instalado (o script oferece instalar no escopo do usuário se ausente)

Uso:
  .\tools\deploy_remote_with_password.ps1

Observações de segurança:
- Não armazene senhas em texto claro. O script usa `Read-Host -AsSecureString`.
- Se possível, prefira chave pública SSH e use o `tools\deploy_remote.ps1` já criado.
#>

param()

function Ensure-PoshSSH {
    if (-not (Get-Module -ListAvailable -Name Posh-SSH)) {
        Write-Output "Módulo 'Posh-SSH' não encontrado. Deseja instalar no escopo do usuário? (Y/N)"
        $ans = Read-Host "Install Posh-SSH?"
        if ($ans -in @('Y','y','Yes','yes')) {
            try {
                Install-Module -Name Posh-SSH -Force -Scope CurrentUser -AllowClobber -ErrorAction Stop
            } catch {
                Write-Error "Falha ao instalar Posh-SSH: $_"; exit 1
            }
        } else { Write-Error 'Posh-SSH é necessário. Abortando.'; exit 1 }
    }
    Import-Module Posh-SSH -ErrorAction Stop
}

Write-Output "=== Deploy SPDealer com senha (Posh-SSH) ==="

Ensure-PoshSSH

$remoteInput = Read-Host "Remote target (user@host) e.g. root@192.168.10.70"
if (-not $remoteInput -or $remoteInput -notmatch '@') { Write-Error 'Formato inválido. Use user@host.'; exit 1 }

$user, $host = $remoteInput -split '@',2

$remotePath = Read-Host "Remote destination path for WAR (default: /opt/spdealer/spdealer-1.0.0.war)"
if ([string]::IsNullOrWhiteSpace($remotePath)) { $remotePath = '/opt/spdealer/spdealer-1.0.0.war' }

$service = Read-Host "systemd service name to restart (default: spdealer.service)"
if ([string]::IsNullOrWhiteSpace($service)) { $service = 'spdealer.service' }

$localWar = Join-Path (Get-Location) 'deploy\spdealer-1.0.0.war'
if (-not (Test-Path $localWar)) { Write-Error "Local WAR not found: $localWar`nExecute o build e coloque o WAR em deploy\"; exit 1 }

# Ler senha como SecureString e construir credential
$secure = Read-Host -AsSecureString "Password for $user@$host (input hidden)"
$cred = New-Object System.Management.Automation.PSCredential ($user, $secure)

Write-Output "Tentando conectar a $host como $user..."

# Criar sessão SSH
try {
    $session = New-SSHSession -ComputerName $host -Credential $cred -AcceptKey -ErrorAction Stop
} catch {
    Write-Error "Falha ao conectar SSH: $_"; exit 1
}

if (-not $session) { Write-Error 'Não foi possível criar sessão SSH'; exit 1 }

Write-Output "Upload do WAR para /tmp via SCP..."
try {
    Set-SCPFile -LocalFile $localWar -RemotePath '/tmp/spdealer-1.0.0.war' -SessionId $session.SessionId -ErrorAction Stop
} catch {
    Write-Error "Falha no upload SCP: $_"; Remove-SSHSession -SessionId $session.SessionId -ErrorAction SilentlyContinue; exit 1
}

Write-Output "Executando comandos remotos: backup, mover, restart service..."

$cmd = @"
if [ -f '$remotePath' ]; then
  ts=$(date +%Y%m%d_%H%M%S)
  cp '$remotePath' "${remotePath}.${ts}.bak"
  echo "Backed up existing WAR to ${remotePath}.${ts}.bak"
fi
mv -f /tmp/spdealer-1.0.0.war '$remotePath'
echo "Moved new WAR to $remotePath"
sudo systemctl stop $service || echo 'Warning: failed to stop service'
sudo systemctl start $service || echo 'Warning: failed to start service'
sudo journalctl -u $service -n 200 --no-pager
"@

try {
    $result = Invoke-SSHCommand -SessionId $session.SessionId -Command $cmd -ErrorAction Stop
    $result.Output | ForEach-Object { Write-Output $_ }
    if ($result.ExitStatus -ne 0) { Write-Warning "Remote command finished with exit status $($result.ExitStatus)" }
} catch {
    Write-Error "Falha ao executar comandos remotos: $_"
} finally {
    Remove-SSHSession -SessionId $session.SessionId -ErrorAction SilentlyContinue
}

Write-Output "Deploy finalizado. Execute smoke tests conforme runbook."
