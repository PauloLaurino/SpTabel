Add-Type -AssemblyName System.IO.Compression.FileSystem
$war='target\spdealer-1.0.0.war'
$zip=[System.IO.Compression.ZipFile]::OpenRead($war)
try{
  $entry = $zip.GetEntry('WEB-INF/classes/application-prod.properties')
  if(-not $entry){ Write-Output 'application-prod.properties not found'; exit 0 }
  $ms = New-Object System.IO.MemoryStream; $s = $entry.Open(); try{ $s.CopyTo($ms) } finally { $s.Dispose() }
  $txt = [System.Text.Encoding]::UTF8.GetString($ms.ToArray())
  $txt -split "`n" | Select-Object -First 200 | ForEach-Object { Write-Output $_ }
} finally { if($zip -ne $null){ $zip.Dispose() } }
