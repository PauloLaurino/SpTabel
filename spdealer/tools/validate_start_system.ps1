$path = 'C:\Desenvolvimento\Seprocom\spdealer\start_system.ps1'
if (-not (Test-Path $path)) { Write-Host "File not found: $path"; exit 2 }
$code = Get-Content -Raw $path
try {
    [ScriptBlock]::Create($code) | Out-Null
    Write-Host 'SYNTAX OK' -ForegroundColor Green
    exit 0
} catch {
    Write-Host ('SYNTAX ERROR: ' + $_.Exception.Message) -ForegroundColor Red
    exit 1
}
