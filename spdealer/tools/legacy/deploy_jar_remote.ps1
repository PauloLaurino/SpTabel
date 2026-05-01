<#
deploy_jar_remote.ps1

Interactive script to deploy the built JAR to a remote host, backup existing JAR,
move the new JAR into place and restart the systemd service.

Usage:
  .\tools\deploy_jar_remote.ps1

Notes:
 - The script uses `scp` and `ssh` (will prompt for password if needed).
 - Make sure `target\spdealer-1.0.0.jar` exists (run `mvn package`).
#>

param()

function Read-Required([string]$prompt) {
    do { $v = Read-Host $prompt } while ([string]::IsNullOrWhiteSpace($v)) ; return $v
}

Write-Output "Deploy JAR remoto - interativo"

$remote = Read-Required "Remote target (user@host) e.g. root@192.168.10.70"
$remoteDest = Read-Host "Remote destination path for JAR (default: /opt/spdealer/spdealer-1.0.0.jar)"
if ([string]::IsNullOrWhiteSpace($remoteDest)) { $remoteDest = "/opt/spdealer/spdealer-1.0.0.jar" }
$service = Read-Host "systemd service name to restart (default: spdealer.service)"
if ([string]::IsNullOrWhiteSpace($service)) { $service = "spdealer.service" }

$localJar = Join-Path (Get-Location) "target\spdealer-1.0.0.jar"
if (-Not (Test-Path $localJar)) {
    Write-Error "Local JAR not found: $localJar`nRun 'mvn clean package -DskipTests' first and try again."; exit 1
}

Write-Output "About to upload: $localJar -> $($remote):/tmp/spdealer-1.0.0.jar"

if ($ok -notin @('y','Y','yes','Yes')) { Write-Output 'Aborting.'; exit 0 }

Write-Output "Uploading via scp (you may be prompted for password)."
& scp $localJar "$($remote):/tmp/spdealer-1.0.0.jar"
if ($LASTEXITCODE -ne 0) { Write-Error "scp failed with exit code $LASTEXITCODE"; exit $LASTEXITCODE }

Write-Output "Running remote backup + swap + restart (you may be prompted for password)."
$remoteCmd = @"
set -e
DEST='$remoteDest'
TMP='/tmp/spdealer-1.0.0.jar'
"@
