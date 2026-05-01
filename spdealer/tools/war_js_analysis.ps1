param(
    [string]$warPath = "target\spdealer-1.0.0.war",
    [string]$reportPath = "war_main_deep_analysis.txt"
)

# Safe, non-interactive script to analyze JS entries inside a WAR/ZIP and write a report file.
# Designed to avoid PSReadLine rendering crashes by not emitting large outputs to the interactive console.

try { Add-Type -AssemblyName System.IO.Compression.FileSystem -ErrorAction Stop } catch { }

function To-HexString($bytes){
    $sb = New-Object -TypeName System.Text.StringBuilder
    foreach($b in $bytes){ [void]$sb.AppendFormat("{0:x2}",$b) }
    $sb.ToString()
}

$out = New-Object System.Collections.Generic.List[string]
$out.Add("ANALYSIS REPORT: $((Get-Date).ToString('u'))")
$out.Add("Source WAR: $warPath")
$out.Add('')

if(-not (Test-Path $warPath)){
    $out.Add("ERROR: WAR not found at path: $warPath")
    $out | Out-File -FilePath $reportPath -Encoding utf8
    Write-Output "ANALYSIS_WRITTEN: $reportPath"
    exit 1
}

$zip = [System.IO.Compression.ZipFile]::OpenRead($warPath)
try{
    $jsEntries = $zip.Entries | Where-Object { $_.FullName -match '\.js$' }
    $out.Add("Found JS entries: $($jsEntries.Count)")
    $out.Add('')

    foreach($entry in $jsEntries){
        $out.Add("--- ENTRY: $($entry.FullName)")
        $out.Add("Size(bytes): $($entry.Length)")

        # Read bytes into memory
        $ms = New-Object System.IO.MemoryStream
        $s = $entry.Open()
        try{ $s.CopyTo($ms) } finally { $s.Dispose() }
        $bytes = $ms.ToArray()
        $ms.Dispose()

        # SHA256
        $sha = [System.Security.Cryptography.SHA256]::Create()
        $hashBytes = $sha.ComputeHash($bytes)
        $sha.Dispose()
        $shaHex = To-HexString $hashBytes
        $out.Add("SHA256: $shaHex")

        # Try decode text (utf8 fallback to default)
        $text = ''
        try{ $text = [System.Text.Encoding]::UTF8.GetString($bytes) } catch { try { $text = [System.Text.Encoding]::Default.GetString($bytes) } catch { $text = '' } }

        # Start / tail snippets (keeps size bounded)
        $head = if($text.Length -gt 2000){ $text.Substring(0,2000).Replace("`r","").Replace("`n","\n") } else { $text.Replace("`r","").Replace("`n","\n") }
        $tail = if($text.Length -gt 1000){ $text.Substring([Math]::Max(0,$text.Length-1000),1000).Replace("`r","").Replace("`n","\n") } else { '' }
        $out.Add('--- Start snippet (first up to 2000 chars) ---')
        $out.Add($head)
        $out.Add('')
        $out.Add('--- Tail snippet (last ~1000 chars) ---')
        $out.Add($tail)
        $out.Add('')

        # Heuristic scans (limited samples)
        $patterns = @('__webpack_require__','webpackJsonp','self\.webpackChunk','\.push\s*\(\[\[')
        foreach($p in $patterns){
            $count = ([regex]::Matches($text,$p)).Count
            $out.Add("Pattern '$p' occurrences: $count")
        }
        $out.Add('')

        # Library scan (small sample lines limited)
        $libPatterns = @('react','react-dom','redux','axios','styled-components','@emotion','material-ui','@mui','lodash','moment','dayjs')
        foreach($lp in $libPatterns){
            $matches = [regex]::Matches($text,$lp,'IgnoreCase')
            $out.Add("LIB SCAN: $lp -> $($matches.Count) hits")
            if($matches.Count -gt 0){
                # capture small context samples to avoid giant output
                foreach($m in $matches | Select-Object -First 4){
                    $contextStart = [Math]::Max(0,$m.Index - 40)
                    $len = [Math]::Min(120, $text.Length - $contextStart)
                    $samp = $text.Substring($contextStart,$len).Replace("`r"," ").Replace("`n"," ")
                    $out.Add("  SAMPLE: $samp")
                }
            }
        }
        $out.Add('')

        # package@version-like tokens
        $verMatches = [regex]::Matches($text,'[A-Za-z0-9_\-\/@]+@\d+\.\d+\.\d+') | Select-Object -Unique
        $out.Add("Detected package@version-like tokens: $($verMatches.Count)")
        foreach($m in $verMatches | Select-Object -First 10){ $out.Add("  $($m.Value)") }
        $out.Add('')

        # node_modules references (sample)
        $nm = [regex]::Matches($text,'node_modules/[^\t\s"''\)\]]{1,200}') | Select-Object -Unique
        $out.Add("node_modules references found: $($nm.Count)")
        foreach($n in $nm | Select-Object -First 6){ $out.Add("  $($n.Value)") }
        $out.Add('')

        # URLs (non-localhost)
        $urlMatches = [regex]::Matches($text,'https?://[^\s"''\)\]]{5,200}') | ForEach-Object { $_.Value } | Select-Object -Unique
        $external = $urlMatches | Where-Object { $_ -notmatch 'localhost|127\.0\.0\.1' }
        $out.Add("Detected URLs total: $($urlMatches.Count); external (non-localhost) unique: $($external.Count)")
        foreach($u in $external | Select-Object -First 10){ $out.Add("  $u") }
        $out.Add('')

        # Secret heuristics (limited samples)
        $secretPatterns = @('AKIA[0-9A-Z]{16}','AIza[0-9A-Za-z\-_.]{35}','sk_live_[0-9a-zA-Z]{24,}','-----BEGIN PRIVATE KEY-----','Bearer\s+[A-Za-z0-9\-\._~\+\/]+=*','apiKey\s*[:=]\s*["'']?[A-Za-z0-9\-\._]{16,}\b','password\s*[:=]')
        foreach($sp in $secretPatterns){
            $m = [regex]::Matches($text,$sp,'IgnoreCase')
            $out.Add("SECRET PATTERN: $sp -> $($m.Count) matches")
            if($m.Count -gt 0){
                foreach($mm in $m | Select-Object -First 4){
                    $contextStart = [Math]::Max(0,$mm.Index - 40)
                    $len = [Math]::Min(120, $text.Length - $contextStart)
                    $samp = $text.Substring($contextStart,$len).Replace("`r"," ").Replace("`n"," ")
                    $out.Add("  LINE: $samp")
                }
            }
        }
        $out.Add('')

        # Long alphanumeric strings (possible tokens)
        $longs = [regex]::Matches($text,'[A-Za-z0-9\-_=]{40,}') | Sort-Object -Property Length -Descending | Select-Object -Unique
        $out.Add("Long alnum strings (>=40) found: $($longs.Count)")
        foreach($s in $longs | Select-Object -First 6){ $out.Add("  $($s.Value)") }
        $out.Add('')

        # License references
        $lic = [regex]::Matches($text,'\.LICENSE\.txt|For license information|license',[System.Text.RegularExpressions.RegexOptions]::IgnoreCase) | Select-Object -Unique
        $out.Add("License-related matches: $($lic.Count)")
        foreach($l in $lic | Select-Object -First 6){ $out.Add("  SAMPLE: $($l.Value)") }
        $out.Add('')

        # Minification indicator
        $avgLineLen = if($text.Length -gt 0){ ($text.Length / ([regex]::Matches($text,'`n').Count + 1)) } else { 0 }
        $out.Add("Average line length (indicator of minification): $([math]::Round($avgLineLen,1))")
        $out.Add('')
    }

    # write summary file
    $out.Add("END OF REPORT: $((Get-Date).ToString('u'))")
    $out | Out-File -FilePath $reportPath -Encoding utf8
    Write-Output "ANALYSIS_WRITTEN: $reportPath"
}
finally{
    if($zip -ne $null){ $zip.Dispose() }
}
