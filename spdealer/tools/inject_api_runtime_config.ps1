param(
    [string]$WarPath = "target\spdealer-1.0.0.war",
    [string]$ApiUrl = "http://192.168.10.70:8081/spdealer/api",
    [string]$TempDir = "$env:TEMP\spdealer_war_mod"
)

if (-Not (Test-Path $WarPath)) {
    Write-Error "WAR file not found: $WarPath"
    exit 1
}

if (Test-Path $TempDir) { Remove-Item -Recurse -Force $TempDir }
New-Item -ItemType Directory -Path $TempDir | Out-Null

Add-Type -AssemblyName System.IO.Compression.FileSystem
[System.IO.Compression.ZipFile]::ExtractToDirectory($WarPath, $TempDir)

# Locate index.html
$indexPathCandidates = Get-ChildItem -Path $TempDir -Recurse -Filter index.html | Select-Object -ExpandProperty FullName
if (-Not $indexPathCandidates) {
    Write-Error "index.html not found in extracted WAR"
    exit 1
}
$indexPath = $indexPathCandidates[0]
Write-Output "Found index.html: $indexPath"

# Read index.html
$indexHtml = Get-Content -Raw -LiteralPath $indexPath -Encoding UTF8

# Build injection snippet via string concatenation to avoid complex quoting issues
$injectionParts = @()
$injectionParts += '<script>'
$injectionParts += '// Runtime API URL injection (added by deploy script)'
$injectionParts += "window.__SPDEALER_API_URL = '$ApiUrl';"
$injectionParts += '(function(){'
$injectionParts += '  var _fetch = window.fetch.bind(window);'
$injectionParts += '  window.fetch = function(input, init){'
$injectionParts += '    try{'
$injectionParts += '      var url = input;'
$injectionParts += '      if (typeof input === "object" && input && input.url) { url = input.url; }'
$injectionParts += '      if (typeof url === "string"){'
$injectionParts += '        if (url.indexOf("http://localhost:8080/api") === 0) {'
$injectionParts += '          url = url.replace("http://localhost:8080/api", window.__SPDEALER_API_URL);'
$injectionParts += '        } else if (url.indexOf("/api/") === 0 || url === "/api") {'
$injectionParts += '          url = window.__SPDEALER_API_URL + (url.startsWith("/")? url : ("/" + url));'
$injectionParts += '        }'
$injectionParts += '      }'
$injectionParts += '      if (typeof input === "object" && input && input.url) { input = new Request(url, input); } else { input = url; }'
$injectionParts += '    } catch(e) { }'
$injectionParts += '    return _fetch(input, init);'
$injectionParts += '  };'
$injectionParts += '  try{'
$injectionParts += '    var XHRopen = XMLHttpRequest.prototype.open;'
$injectionParts += '    XMLHttpRequest.prototype.open = function(method, url){'
$injectionParts += '      try{'
$injectionParts += '        if (typeof url === "string"){'
$injectionParts += '          if (url.indexOf("http://localhost:8080/api") === 0) {'
$injectionParts += '            url = url.replace("http://localhost:8080/api", window.__SPDEALER_API_URL);'
$injectionParts += '          } else if (url.indexOf("/api/") === 0 || url === "/api") {'
$injectionParts += '            url = window.__SPDEALER_API_URL + (url.startsWith("/")? url : ("/" + url));'
$injectionParts += '          }'
$injectionParts += '        }'
$injectionParts += '      } catch(e){}'
$injectionParts += '      return XHRopen.apply(this, [method, url].concat(Array.prototype.slice.call(arguments,2)));'
$injectionParts += '    };'
$injectionParts += '  }catch(e){ }'
$injectionParts += '})();'
$injectionParts += '</script>'
param(
  [string]$WarPath = "target\spdealer-1.0.0.war",
  [string]$ApiUrl = "http://192.168.10.70:8081/spdealer/api",
  [string]$TempDir = "$env:TEMP\spdealer_war_mod"
)

if (-Not (Test-Path $WarPath)) {
  Write-Error "WAR file not found: $WarPath"
  exit 1
}

if (Test-Path $TempDir) { Remove-Item -Recurse -Force $TempDir }
New-Item -ItemType Directory -Path $TempDir | Out-Null

Add-Type -AssemblyName System.IO.Compression.FileSystem
[System.IO.Compression.ZipFile]::ExtractToDirectory($WarPath, $TempDir)

# Locate index.html
$indexPathCandidates = Get-ChildItem -Path $TempDir -Recurse -Filter index.html | Select-Object -ExpandProperty FullName
if (-Not $indexPathCandidates) {
  Write-Error "index.html not found in extracted WAR"
  exit 1
}
$indexPath = $indexPathCandidates[0]
Write-Output "Found index.html: $indexPath"

# Read index.html
$indexHtml = Get-Content -Raw -LiteralPath $indexPath -Encoding UTF8

# Build injection snippet via string concatenation to avoid complex quoting issues
$injectionParts = @()
$injectionParts += '<script>'
$injectionParts += '// Runtime API URL injection (added by deploy script)'
$injectionParts += "window.__SPDEALER_API_URL = '$ApiUrl';"
$injectionParts += '(function(){'
$injectionParts += '  var _fetch = window.fetch.bind(window);'
$injectionParts += '  window.fetch = function(input, init){'
$injectionParts += '    try{'
$injectionParts += '      var url = input;'
$injectionParts += '      if (typeof input === "object" && input && input.url) { url = input.url; }'
$injectionParts += '      if (typeof url === "string"){'
$injectionParts += '        if (url.indexOf("http://localhost:8080/api") === 0) {'
$injectionParts += '          url = url.replace("http://localhost:8080/api", window.__SPDEALER_API_URL);'
$injectionParts += '        } else if (url.indexOf("/api/") === 0 || url === "/api") {'
$injectionParts += '          url = window.__SPDEALER_API_URL + (url.startsWith("/")? url : ("/" + url));'
$injectionParts += '        }'
$injectionParts += '      }'
$injectionParts += '      if (typeof input === "object" && input && input.url) { input = new Request(url, input); } else { input = url; }'
$injectionParts += '    } catch(e) { }'
$injectionParts += '    return _fetch(input, init);'
$injectionParts += '  };'
$injectionParts += '  try{'
$injectionParts += '    var XHRopen = XMLHttpRequest.prototype.open;'
$injectionParts += '    XMLHttpRequest.prototype.open = function(method, url){'
$injectionParts += '      try{'
$injectionParts += '        if (typeof url === "string"){'
$injectionParts += '          if (url.indexOf("http://localhost:8080/api") === 0) {'
$injectionParts += '            url = url.replace("http://localhost:8080/api", window.__SPDEALER_API_URL);'
$injectionParts += '          } else if (url.indexOf("/api/") === 0 || url === "/api") {'
$injectionParts += '            url = window.__SPDEALER_API_URL + (url.startsWith("/")? url : ("/" + url));'
$injectionParts += '          }'
$injectionParts += '        }'
$injectionParts += '      } catch(e){}'
$injectionParts += '      return XHRopen.apply(this, [method, url].concat(Array.prototype.slice.call(arguments,2)));'
$injectionParts += '    };'
$injectionParts += '  }catch(e){ }'
$injectionParts += '})();'
$injectionParts += '</script>'
$injection = ($injectionParts -join "`n")

# Inject before first occurrence of /static/js script tag (string-based insertion)
$pos = $indexHtml.IndexOf('/static/js')
if ($pos -ne -1) {
  $startTagPos = $indexHtml.LastIndexOf('<script', $pos)
  if ($startTagPos -ne -1) {
    $indexHtml = $indexHtml.Substring(0, $startTagPos) + $injection + $indexHtml.Substring($startTagPos)
    Write-Output "Injected snippet before first static js script tag (by string search)"
  } else {
    if ($indexHtml.Contains('</head>')) {
      $indexHtml = $indexHtml.Replace('</head>', $injection + "`n</head>")
      Write-Output "Injected snippet before </head> (fallback)"
    } else {
      $indexHtml = $injection + "`n" + $indexHtml
      Write-Output "Appended injection to top of index.html (fallback)"
    }
  }
} elseif ($indexHtml.Contains('</head>')) {
  $indexHtml = $indexHtml.Replace('</head>', $injection + "`n</head>")
  Write-Output "Injected snippet before </head"
} else {
  $indexHtml = $injection + "`n" + $indexHtml
  Write-Output "Appended injection to top of index.html"
}

# Save modified index.html
Set-Content -LiteralPath $indexPath -Value $indexHtml -Encoding UTF8

# Repack WAR: backup, remove and create new
$backupWar = "$WarPath.bak"
Copy-Item -Path $WarPath -Destination $backupWar -Force
Remove-Item -Path $WarPath -Force

[System.IO.Compression.ZipFile]::CreateFromDirectory($TempDir, $WarPath)

# Cleanup
Remove-Item -Recurse -Force $TempDir

Write-Output "Patched WAR created: $WarPath (backup at $backupWar)"
      }
      if (typeof input === 'object' && input && input.url) { input = new Request(url, input); } else { input = url; }
    } catch(e) { }
    return _fetch(input, init);
  };
  try{
    var XHRopen = XMLHttpRequest.prototype.open;
    XMLHttpRequest.prototype.open = function(method, url){
      try{
        if (typeof url === 'string'){
          if (url.indexOf('http://localhost:8080/api') === 0) {
            url = url.replace('http://localhost:8080/api', window.__SPDEALER_API_URL);
          } else if (url.indexOf('/api/') === 0 || url === '/api') {
            url = window.__SPDEALER_API_URL + (url.startsWith('/')? url : ('/' + url));
          }
        }
      } catch(e){}
      return XHRopen.apply(this, [method, url].concat(Array.prototype.slice.call(arguments,2)));
    };
  }catch(e){ }
})();
</script>
"@

# Inject before first occurrence of /static/js script tag (avoid complex regex)
$pos = $indexHtml.IndexOf('/static/js')
if ($pos -ne -1) {
  $startTagPos = $indexHtml.LastIndexOf('<script', $pos)
  if ($startTagPos -ne -1) {
    $indexHtml = $indexHtml.Substring(0, $startTagPos) + $injection + $indexHtml.Substring($startTagPos)
    Write-Output "Injected snippet before first static js script tag (by string search)"
  } else {
    # fallback to head insertion
    if ($indexHtml.Contains('</head>')) {
      $indexHtml = $indexHtml.Replace('</head>', $injection + "`n</head>")
      Write-Output "Injected snippet before </head> (fallback)"
    } else {
      $indexHtml = $injection + "`n" + $indexHtml
      Write-Output "Appended injection to top of index.html (fallback)"
    }
  }
} elseif ($indexHtml.Contains('</head>')) {
  $indexHtml = $indexHtml.Replace('</head>', $injection + "`n</head>")
  Write-Output "Injected snippet before </head>"
} else {
  $indexHtml = $injection + "`n" + $indexHtml
  Write-Output "Appended injection to top of index.html"
}

# Save modified index.html
Set-Content -LiteralPath $indexPath -Value $indexHtml -Encoding UTF8

# Repack WAR: backup, remove and create new
$backupWar = "$WarPath.bak"
Copy-Item -Path $WarPath -Destination $backupWar -Force
Remove-Item -Path $WarPath -Force

[System.IO.Compression.ZipFile]::CreateFromDirectory($TempDir, $WarPath)

# Cleanup
Remove-Item -Recurse -Force $TempDir

Write-Output "Patched WAR created: $WarPath (backup at $backupWar)"
