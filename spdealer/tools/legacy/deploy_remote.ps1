<#
deploy_remote.ps1

Usage: run this script locally from the project root. It will:
- ask for remote target (user@host)
- upload `deploy\spdealer-1.0.0.war` to `/tmp` on the remote
- create a timestamped backup of the current remote WAR
- move the uploaded WAR into place
- restart a systemd service (specified by user)

Security notes:
- The script does NOT accept or store passwords in this file. scp/ssh will prompt for password interactively if needed.
- Prefer SSH key-based auth (copy your public key to remote `~/.ssh/authorized_keys`).
#>

param()

function Read-Required([string]$prompt) {
    do { $v = Read-Host $prompt } while ([string]::IsNullOrWhiteSpace($v)) ; return $v
}

Write-Output "Deploy script for SPDealer - interactive."

$remote = Read-Required "Remote target (user@host) e.g. root@192.168.10.70"
$remotePath = Read-Host "Remote destination path for WAR (default: /opt/spdealer/spdealer-1.0.0.war)";
if ([string]::IsNullOrWhiteSpace($remotePath)) { $remotePath = "/opt/spdealer/spdealer-1.0.0.war" }
$service = Read-Host "systemd service name to restart (default: spdealer.service)";
if ([string]::IsNullOrWhiteSpace($service)) { $service = "spdealer.service" }

$localWar = Join-Path (Get-Location) "deploy\spdealer-1.0.0.war"
if (-Not (Test-Path $localWar)) { Write-Error "Local WAR not found: $localWar`nMake sure you ran the build and the deploy step first." ; exit 1 }

Write-Output "About to upload: $localWar -> $($remote):/tmp/spdealer-1.0.0.war"
$ok = Read-Host "Proceed? (y/N)"
if ($ok -notin @('y','Y','yes','Yes')) { Write-Output 'Aborting.'; exit 0 }

Write-Output "Uploading via scp (you may be prompted for password)."
& scp $localWar "$($remote):/tmp/spdealer-1.0.0.war"
if ($LASTEXITCODE -ne 0) { Write-Error "scp failed with exit code $LASTEXITCODE"; exit $LASTEXITCODE }

Write-Output "Running remote backup + swap + restart (you may be prompted for password)."
$remoteCmd = @"
set -e
DEST='$remotePath'
TMP='/tmp/spdealer-1.0.0.war'
if [ -f "$DEST" ]; then
  ts=$(date +%Y%m%d_%H%M%S)
  cp "$DEST" "${DEST}.${ts}.bak"
  echo "Backed up existing WAR to ${DEST}.${ts}.bak"
fi
mv -f "$TMP" "$DEST"
echo "Moved new WAR to $DEST"
sudo systemctl stop $service || echo 'Warning: failed to stop service (check service name)'
sudo systemctl start $service || echo 'Warning: failed to start service (check service logs)'
sudo journalctl -u $service -n 200 --no-pager
echo exit
"@

ssh $remote $remoteCmd
if ($LASTEXITCODE -ne 0) { Write-Error "ssh remote commands failed with exit code $LASTEXITCODE"; exit $LASTEXITCODE }

Write-Output "Deploy finished. Check logs on remote host and run smoke tests."

Write-Output "Suggested smoke test (local PowerShell):"
Write-Output "Invoke-WebRequest -UseBasicParsing -Uri 'http://$(($remote -split '@')[-1]):8081/spdealer/api/v2/dashboards/1' -TimeoutSec 15"
