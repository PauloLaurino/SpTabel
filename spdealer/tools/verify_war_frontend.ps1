Add-Type -AssemblyName System.IO.Compression.FileSystem
$warPath = 'target\spdealer-1.0.0.war'
if(-not (Test-Path $warPath)) { Write-Output "WAR not found: $warPath"; exit 1 }
$zip = [System.IO.Compression.ZipFile]::OpenRead($warPath)
try{
  $matches = @()
  foreach($e in $zip.Entries){
    if($e.FullName -like 'index.html' -or $e.FullName -like 'static/*' -or $e.FullName -like 'static\\*' -or $e.FullName -like 'BOOT-INF/classes/static/*' -or $e.FullName -like 'WEB-INF/classes/static/*'){
      $matches += @{ name = $e.FullName; size = $e.Length }
    }
  }
  if($matches.Count -eq 0){ Write-Output "No frontend assets found in WAR"; exit 0 }
  $matches | ConvertTo-Json -Depth 3 | Out-File -Encoding utf8 .\war_frontend_manifest.json
  Write-Output "Found $($matches.Count) frontend entries. See war_frontend_manifest.json"
} finally { $zip.Dispose() }
