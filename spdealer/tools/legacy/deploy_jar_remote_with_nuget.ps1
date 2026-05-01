<#
deploy_jar_remote_with_nuget.ps1

Improved deploy script that attempts to read SSH credentials from local NuGet.Config
and uses Posh-SSH for non-interactive SCP/SSH when available. Falls back to the
original interactive scp/ssh flow when credentials or Posh-SSH are not available.

Usage:
  # interactive (prompts for remote)
  .\tools\deploy_jar_remote_with_nuget.ps1

  # attempt automated deploy using NuGet credentials (if present)
  .\tools\deploy_jar_remote_with_nuget.ps1 -UseNuGetCredentials

Note: This script runs locally and reads NuGet.Config from common locations.
It does NOT transmit your NuGet.Config anywhere.
#>

param(
    [switch]$UseNuGetCredentials,
    [string]$NuGetSourceName
)

function Read-Required([string]$prompt) {
    do { $v = Read-Host $prompt } while ([string]::IsNullOrWhiteSpace($v)) ; return $v
}

function Get-NuGetConfigPaths {
    $paths = @()
    if ($env:APPDATA) { $paths += Join-Path $env:APPDATA 'NuGet\NuGet.Config' }
    $paths += Join-Path $PSScriptRoot '..\NuGet.Config'
    $paths += Join-Path (Get-Location) 'NuGet.Config'
    return $paths | Where-Object { Test-Path $_ }
}

function Get-CredentialFromNuGet([string]$sourceName) {
    $cfgPaths = Get-NuGetConfigPaths
    foreach ($p in $cfgPaths) {
        try {
            [xml]$xml = Get-Content $p -ErrorAction Stop
        } catch { continue }

        $ns = $xml.configuration.packageSourceCredentials
        if (-not $ns) { continue }

        foreach ($child in $ns.ChildNodes) {
            if ($sourceName -and ($child.Name -ne $sourceName)) { continue }

            $username = $null; $password = $null
            foreach ($add in $child.SelectNodes('add')) {
                $k = $add.GetAttribute('key')
                $v = $add.GetAttribute('value')
                if ($k -match 'User' -or $k -match 'user' -or $k -eq 'Username') { $username = $v }
                if ($k -match 'Pass' -or $k -match 'pass' -or $k -eq 'ClearTextPassword' -or $k -eq 'Password') { $password = $v }
            }

            if ($username -and $password) {
                try {
                    $secure = ConvertTo-SecureString $password -AsPlainText -Force
                    return New-Object System.Management.Automation.PSCredential($username, $secure)
                } catch { continue }
            }
        }
    }
    return $null
}

Write-Output "Deploy JAR remoto (improved)"

$localJar = Join-Path (Get-Location) 'target\spdealer-1.0.0.jar'
if (-Not (Test-Path $localJar)) {
    Write-Error "Local JAR not found: $localJar`nRun 'mvn clean package -DskipTests' first and try again."; exit 1
}

$remote = Read-Required 'Remote target (user@host) e.g. root@192.168.10.70'
$remoteDest = Read-Host 'Remote destination path for JAR (default: /opt/spdealer/spdealer-1.0.0.jar)'
if ([string]::IsNullOrWhiteSpace($remoteDest)) { $remoteDest = '/opt/spdealer/spdealer-1.0.0.jar' }
$service = Read-Host "systemd service name to restart (default: spdealer.service)"
if ([string]::IsNullOrWhiteSpace($service)) { $service = 'spdealer.service' }

$cred = $null
if ($UseNuGetCredentials) {
    Write-Output 'Attempting to read credentials from NuGet.Config...'
    $cred = Get-CredentialFromNuGet -sourceName $NuGetSourceName
    if ($cred) { Write-Output 'Credential found in NuGet.Config.' } else { Write-Output 'No usable credential found in NuGet.Config.' }
}

if ($cred -and (Get-Module -ListAvailable -Name Posh-SSH)) {
    try {
        Import-Module Posh-SSH -ErrorAction Stop
        $userHost = $remote -split '@'
        if ($userHost.Length -ne 2) { Write-Error 'Remote must be in user@host format for Posh-SSH usage.'; exit 1 }
        $user = $userHost[0]; $host = $userHost[1]

        Write-Output "Opening SSH session to $host as $user..."
        $session = New-SSHSession -ComputerName $host -Credential $cred -AcceptKey -ErrorAction Stop
        try {
            $sid = $session.SessionId
            Set-SCPFile -LocalFile $localJar -RemotePath '/tmp/spdealer-1.0.0.jar' -SessionId $sid -ErrorAction Stop

            $remoteCmd = @"/bin/sh -lc '
DEST="$remoteDest"
TMP="/tmp/spdealer-1.0.0.jar"
if [ -f "$DEST" ]; then ts=$(date +%Y%m%d_%H%M%S); cp "$DEST" "${DEST}.${ts}.bak"; echo "Backed up existing JAR to ${DEST}.${ts}.bak"; fi
mv -f "$TMP" "$DEST"; echo "Moved new JAR to $DEST"; sudo systemctl stop $service || echo 'warn stop'; sudo systemctl start $service || echo 'warn start'; sudo journalctl -u $service -n 200 --no-pager
'"@

            Write-Output 'Invoking remote commands'
            $out = Invoke-SSHCommand -SessionId $sid -Command $remoteCmd -ErrorAction Stop
            $out.Output | ForEach-Object { Write-Output $_ }
        } finally {
            Remove-SSHSession -SessionId $session.SessionId | Out-Null
        }

        Write-Output 'Deploy finished (Posh-SSH path).'
        exit 0
    } catch {
        Write-Warning "Posh-SSH path failed: $_. Falling back to interactive scp/ssh."
    }
}

# Interactive fallback
Write-Output "About to upload: $localJar -> $($remote):/tmp/spdealer-1.0.0.jar"
$ok = Read-Host "Proceed? (y/N)"
if ($ok -notin @('y','Y','yes','Yes')) { Write-Output 'Aborting.'; exit 0 }

Write-Output 'Uploading via scp (you may be prompted for password).'
& scp $localJar "$($remote):/tmp/spdealer-1.0.0.jar"
if ($LASTEXITCODE -ne 0) { Write-Error "scp failed with exit code $LASTEXITCODE"; exit $LASTEXITCODE }

Write-Output 'Running remote backup + swap + restart (you may be prompted for password).'
$remoteCmd = @"
set -e
DEST='$remoteDest'
TMP='/tmp/spdealer-1.0.0.jar'
if [ -f "$DEST" ]; then
  ts=$(date +%Y%m%d_%H%M%S)
  cp "$DEST" "${DEST}.${ts}.bak"
  echo "Backed up existing JAR to ${DEST}.${ts}.bak"
fi
mv -f "$TMP" "$DEST"
echo "Moved new JAR to $DEST"
sudo systemctl stop $service || echo 'Warning: failed to stop service (check service name)'
sudo systemctl start $service || echo 'Warning: failed to start service (check service logs)'
sudo journalctl -u $service -n 200 --no-pager
"@

ssh $remote $remoteCmd
if ($LASTEXITCODE -ne 0) { Write-Error "ssh remote commands failed with exit code $LASTEXITCODE"; exit $LASTEXITCODE }

Write-Output 'Deploy finished. Check remote logs and run smoke tests.'

Write-Output "Suggested smoke test (local PowerShell):"
Write-Output "Invoke-WebRequest -UseBasicParsing -Uri 'http://$(($remote -split '@')[-1]):8081/spdealer/api/v2/dashboards/1' -TimeoutSec 15"
