Add-Type -AssemblyName System.IO.Compression.FileSystem
$war = 'target\spdealer-1.0.0.war'
$out = 'war_main_inspect.txt'
if(-not (Test-Path $war)) { "WAR not found: $war" | Out-File -Encoding utf8 $out; exit 1 }
$zip = [System.IO.Compression.ZipFile]::OpenRead($war)
try{
  $entry = $zip.Entries | Where-Object { $_.Name -match '^main\..*\.js$' -and ($_.FullName -match 'static/js' -or $_.FullName -match '/static/js') } | Select-Object -First 1
  if(-not $entry){ $entry = $zip.Entries | Where-Object { $_.Name -match '^main\..*\.js$' } | Select-Object -First 1 }
  if(-not $entry){ "main.*.js not found in WAR" | Out-File -Encoding utf8 $out; exit 0 }
  $headReport = @()
  $headReport += "Found entry: $($entry.FullName) (size: $($entry.Length))"
  $ms = New-Object System.IO.MemoryStream; $s = $entry.Open(); try{ $s.CopyTo($ms) } finally { $s.Dispose() }
  $bytes = $ms.ToArray()
  # Try to decode as UTF8; if fails, fallback to Latin1
  try{ $text = [System.Text.Encoding]::UTF8.GetString($bytes) } catch { $text = [System.Text.Encoding]::GetEncoding(28591).GetString($bytes) }
  $snippet = if($text.Length -gt 20000){ $text.Substring(0,20000) } else { $text }
  $headReport += "--- Snippet (first 20k chars) ---"
  $headReport += $snippet
  $headReport += "--- Search hits ---"
  $patterns = @('menu-groups','menu-items','sidebar','Sidebar','REACT_APP_API_URL','REACT_APP_API_BASE_URL','PUBLIC_URL','window.__REACT_APP__','/api/','fetch(','axios.','document.getElementById("root")','setupProxy','pathRewrite')
  foreach($p in $patterns){
    $count = ([regex]::Matches($text, [regex]::Escape($p))).Count
    if($count -gt 0){
      $headReport += "$p : $count occurrences"
      # capture up to 5 nearby contexts
      $matches = [regex]::Matches($text, [regex]::Escape($p)) | Select-Object -First 5
      foreach($m in $matches){
        $i = [Math]::Max(0,$m.Index - 80)
        $len = [Math]::Min(160, $text.Length - $i)
        $context = $text.Substring($i,$len) -replace "\r|\n"," "
        $headReport += "... $context ..."
      }
    } else { $headReport += "$p : 0" }
  }
  $headReport | Out-File -Encoding utf8 $out
  Write-Output "WROTE: $out (entry: $($entry.FullName))"
} finally { $zip.Dispose() }
