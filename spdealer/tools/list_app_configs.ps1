Add-Type -AssemblyName System.IO.Compression.FileSystem
$war='target\spdealer-1.0.0.war'
if(-not (Test-Path $war)){ Write-Output "WAR not found: $war"; exit 1 }
$zip=[System.IO.Compression.ZipFile]::OpenRead($war)
try{
  $entries = $zip.Entries | Where-Object { $_.FullName -match 'WEB-INF/classes/(application.*\.properties|application.*\.yml)' }
  if($entries.Count -eq 0){ Write-Output 'No application*.properties or yml in WEB-INF/classes' }
  foreach($e in $entries){ Write-Output $e.FullName }
} finally { if($zip -ne $null){ $zip.Dispose() } }
