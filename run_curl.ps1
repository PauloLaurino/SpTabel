param(
    [string]$RequestFile = "request.json",
    [string]$Url = "",
    [string]$Cert = "",
    [string]$Senha = "",
    [string]$Token = "",
    [string]$DBUser = "",
    [string]$DBPass = "",
    [string]$DBName = "sptabel",
    [string]$DBHost = "localhost",
    [int]$DBPort = 3306
)

$ErrorActionPreference = 'Stop'
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition

Write-Output "run_curl (NOTAS) -> request=$RequestFile"

# If DBUser provided, read parametros from DB
if ($DBUser -ne '') {
    Write-Output "Reading parametros from DB $DBName@$DBHost:$DBPort as $DBUser"
    $select = "select AMBIENTE_PAR, CONTEXTO_PAR, CAMINHOCERTIFICADO_PAR, SENHACERTIFICADO_PAR, TOKENFUNARPEN_PAR FROM parametros LIMIT 1"
    try {
        $mysqlArgs = @('-h', $DBHost, '-P', $DBPort.ToString(), '-u', $DBUser, $DBName, '-N', '-e', $select)
        if ($DBPass -ne '') { $mysqlArgs = @('-h', $DBHost, '-P', $DBPort.ToString(), '-u', $DBUser, "-p$DBPass", $DBName, '-N', '-e', $select) }
        $out = & mysql @mysqlArgs 2>&1
    } catch {
        Write-Warning "mysql execution failed: $_"; $out = $null
    }
    if ($out) {
        $vals = $out -split "\t"
        if ($vals.Count -ge 5) {
            $AMBIENTE_PAR = $vals[0].Trim()
            $CONTEXTO_PAR = $vals[1].Trim()
            $CAMINHOCERTIFICADO_PAR = $vals[2].Trim()
            $SENHACERTIFICADO_PAR = $vals[3].Trim()
            $TOKENFUNARPEN_PAR = $vals[4].Trim()

            if ($CAMINHOCERTIFICADO_PAR -and $CAMINHOCERTIFICADO_PAR -ne '') { $Cert = $CAMINHOCERTIFICADO_PAR }
            if ($SENHACERTIFICADO_PAR -and $SENHACERTIFICADO_PAR -ne '') { $Senha = $SENHACERTIFICADO_PAR }
            if ($TOKENFUNARPEN_PAR -and $TOKENFUNARPEN_PAR -ne '') { $Token = $TOKENFUNARPEN_PAR }
            Write-Output "Parametros loaded: AMBIENTE=$AMBIENTE_PAR CONTEXTO=$CONTEXTO_PAR"
        } else {
            Write-Warning "Unexpected mysql output: $out"
        }
    } else {
        Write-Warning "No result from mysql query"
    }
}

# Validate inputs
if (-not $Url -or $Url -eq '') {
    Write-Warning "Parameter -Url not provided; defaulting to https://dev-v11plus.funarpen.com.br/selos/recepcao"
    $Url = "https://dev-v11plus.funarpen.com.br/selos/recepcao"
}
if (-not (Test-Path (Join-Path $scriptDir $RequestFile))) {
    Write-Error "Request file not found: $RequestFile"; exit 1
}
if (-not $Cert -or -not (Test-Path $Cert)) { Write-Error "Certificate not found: $Cert"; exit 1 }
if (-not $Senha) { Write-Warning "Certificate password is empty" }
if (-not $Token) { Write-Warning "Token is empty; request may be rejected by server" }

$responseFile = Join-Path $scriptDir 'resposta_notas_curl.txt'
$traceFile = Join-Path $scriptDir 'resposta_notas_curl_trace.txt'
$summaryFile = Join-Path $scriptDir 'run_curl_notas_summary.txt'

Write-Output "Running curl against: $Url"

$args = @(
  "--ssl-no-revoke",
  "--cert", "${Cert}:${Senha}",
  "--cert-type", "P12",
  "-H", "Content-Type: application/json; charset=utf-8",
  "-H", "Accept: application/json",
  "-H", "Authorization: Bearer $Token",
  "--data-binary", "@${RequestFile}",
  "-v",
  "--output", $responseFile,
  "--write-out", "%{http_code}\n",
  $Url
)

# Execute curl and capture verbose trace (stderr)
try {
    & curl.exe @args 2> $traceFile
    $exit = $LASTEXITCODE
} catch {
    Write-Warning "curl.exe execution failed: $_"; $exit = 1
}

# Mask token for summary
function Mask-Token($t) {
    if (-not $t) { return "(empty)" }
    if ($t.Length -le 16) { return $t }
    return $t.Substring(0,6) + '...' + $t.Substring($t.Length-4)
}

"run_curl_notas summary - $(Get-Date -Format o)" | Out-File -FilePath $summaryFile -Encoding utf8
"RequestFile: $RequestFile" | Out-File -FilePath $summaryFile -Append -Encoding utf8
"Url: $Url" | Out-File -FilePath $summaryFile -Append -Encoding utf8
"Cert: $Cert" | Out-File -FilePath $summaryFile -Append -Encoding utf8
"Token: $(Mask-Token $Token)" | Out-File -FilePath $summaryFile -Append -Encoding utf8
"Curl exit: $exit" | Out-File -FilePath $summaryFile -Append -Encoding utf8
"Response saved: $responseFile" | Out-File -FilePath $summaryFile -Append -Encoding utf8
"Trace saved: $traceFile" | Out-File -FilePath $summaryFile -Append -Encoding utf8

if ($exit -ne 0) { Write-Error "curl finished with exit code $exit. See $traceFile and $responseFile"; exit $exit }

Write-Output "curl completed successfully. Response in: $responseFile; trace: $traceFile"
Write-Output "Summary: $summaryFile"
