Add-Type -AssemblyName System.IO.Compression.FileSystem
$war = 'target\spdealer-1.0.0.war'
if(-not (Test-Path $war)){
    Write-Output "ERROR: WAR not found at $war"
    exit 1
}
$zip = [System.IO.Compression.ZipFile]::OpenRead($war)
try{
    Write-Output "MAP_ENTRIES:"
    $maps = $zip.Entries | Where-Object { $_.FullName -match '\.map$' }
    foreach($m in $maps){ Write-Output "$($m.FullName) | $($m.Length)" }

    Write-Output "`nJS_SOURCE_MAPPING_REFERENCES:"
    $js = $zip.Entries | Where-Object { $_.FullName -match '\.js$' }
    foreach($e in $js){
        $ms = New-Object System.IO.MemoryStream
        $s = $e.Open()
        try{ $s.CopyTo($ms) } finally { $s.Dispose() }
        $bytes = $ms.ToArray(); $ms.Dispose()
        try{ $text = [System.Text.Encoding]::UTF8.GetString($bytes) } catch { $text = [System.Text.Encoding]::Default.GetString($bytes) }
        $matches = [regex]::Matches($text,'sourceMappingURL[^\r\n\'']{0,400}')
        if($matches.Count -gt 0){
            Write-Output "$($e.FullName) -> $($matches.Count)"
            foreach($m in $matches){ Write-Output '  ' + $m.Value }
        }
    }

    Write-Output "`nINSPECT_MANIFEST_AND_WEBXML:"
    $manifestEntry = $zip.GetEntry('META-INF/MANIFEST.MF')
    if($manifestEntry){
        $ms = New-Object System.IO.MemoryStream; $s = $manifestEntry.Open(); try{ $s.CopyTo($ms) } finally { $s.Dispose() }
        $txt = [System.Text.Encoding]::UTF8.GetString($ms.ToArray())
        Write-Output 'MANIFEST (head):'
        $txt -split "`n" | Select-Object -First 80 | ForEach-Object { Write-Output "  $_" }
    } else { Write-Output 'MANIFEST: not found' }

    $webxml = $zip.GetEntry('WEB-INF/web.xml')
    if($webxml){
        $ms = New-Object System.IO.MemoryStream; $s = $webxml.Open(); try{ $s.CopyTo($ms) } finally { $s.Dispose() }
        $txt = [System.Text.Encoding]::UTF8.GetString($ms.ToArray())
        Write-Output 'WEB_XML (head):'
        $txt -split "`n" | Select-Object -First 200 | ForEach-Object { Write-Output "  $_" }
    } else { Write-Output 'WEB_XML: not found' }
} finally { if($zip -ne $null){ $zip.Dispose() } }
