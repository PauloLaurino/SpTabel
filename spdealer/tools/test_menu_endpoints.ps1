$base='http://localhost:8080'
$endpoints = @('/api/menu-groups/2','/api/menu-groups','/api/menu-items/1')
$outDir='menu_endpoint_results'
if(-not (Test-Path $outDir)){ New-Item -Path $outDir -ItemType Directory | Out-Null }
foreach($ep in $endpoints){
  $url = $base + $ep
  $safeName = ($ep -Replace '[^a-zA-Z0-9]','_').Trim('_')
  $out = Join-Path $outDir ($safeName + '.txt')
  try{
    $r = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 30
    $status = $r.StatusCode
    $content = $r.Content
    $line = "URL: $url`nSTATUS: $status`n---BODY---`n$content"
    $line | Out-File -Encoding utf8 $out
    Write-Output "WROTE: $out (HTTP $status)"
  } catch{
    $err = $_.Exception.Message
    "URL: $url`nERROR: $err" | Out-File -Encoding utf8 $out
    Write-Output "WROTE: $out (ERROR)"
  }
}
