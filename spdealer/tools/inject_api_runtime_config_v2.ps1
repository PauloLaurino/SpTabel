param(
    [string]$WarPath = "target\spdealer-1.0.0.war",
    [string]$ApiUrl = "http://192.168.10.70:8081/spdealer/api",
    [string]$TempDir = "$env:TEMP\spdealer_war_mod_v2"
)

if (-Not (Test-Path $WarPath)) {
    Write-Error "WAR file not found: $WarPath"
    exit 1
}

if (Test-Path $TempDir) { Remove-Item -Recurse -Force $TempDir }
New-Item -ItemType Directory -Path $TempDir | Out-Null

Add-Type -AssemblyName System.IO.Compression.FileSystem
[System.IO.Compression.ZipFile]::ExtractToDirectory($WarPath, $TempDir)

$index = Get-ChildItem -Path $TempDir -Recurse -Filter index.html | Select-Object -First 1 -ExpandProperty FullName
if (-Not $index) { Write-Error "index.html not found"; exit 1 }

$html = Get-Content -Raw -LiteralPath $index -Encoding UTF8

$snippet = "<script>window.__SPDEALER_API_URL='" + $ApiUrl + "';(function(){var f=window.fetch.bind(window);window.fetch=function(i,u){try{var url=(typeof i==='object'&&i&&i.url)?i.url:i; if(typeof url==='string'){ if(url.indexOf('http://localhost:8080/api')===0){ url=url.replace('http://localhost:8080/api',window.__SPDEALER_API_URL);} else if(url.indexOf('/api/')===0||url==='/api'){ url=window.__SPDEALER_API_URL + (url.startsWith('/')?url:'/'+url);} } if(typeof i==='object'&&i&&i.url){ i=new Request(url,i);} else { i=url;} }catch(e){} return f(i,u);} ; var X=XMLHttpRequest.prototype.open; XMLHttpRequest.prototype.open=function(m,u){try{ if(typeof u==='string'){ if(u.indexOf('http://localhost:8080/api')===0){ u=u.replace('http://localhost:8080/api',window.__SPDEALER_API_URL);} else if(u.indexOf('/api/')===0||u==='/api'){ u=window.__SPDEALER_API_URL + (u.startsWith('/')?u:'/'+u);} } }catch(e){} return X.apply(this,arguments);} })();</script>"

# Insert before first /static/js script occurrence
$pos = $html.IndexOf('/static/js')
if ($pos -ge 0) {
    $start = $html.LastIndexOf('<script', $pos)
    if ($start -ge 0) { $html = $html.Substring(0,$start) + $snippet + $html.Substring($start) }
    else { $html = $snippet + "`n" + $html }
} elseif ($html.Contains('</head>')) {
    $html = $html.Replace('</head>', $snippet + "`n</head>")
} else { $html = $snippet + "`n" + $html }

Set-Content -LiteralPath $index -Value $html -Encoding UTF8

# Repack
$bak = $WarPath + '.bak'
Copy-Item -Path $WarPath -Destination $bak -Force
Remove-Item -Path $WarPath -Force
[System.IO.Compression.ZipFile]::CreateFromDirectory($TempDir, $WarPath)

Remove-Item -Recurse -Force $TempDir
Write-Output "Patched WAR created: $WarPath (backup at $bak)"
