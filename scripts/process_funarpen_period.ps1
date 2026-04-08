<#
Processa selos no período especificado contra o receptor Funarpen e gera updates.

Instruções rápidas:
- Ajuste as variáveis de conexão se necessário.
- O script tenta usar `mysql` CLI para extrair selos. Se não existir, ele grava a query em `scripts/query_selos.sql`.
- Para cada selo (amostra de 5), se `RETORNO` existir consulta o endpoint de protocolo; caso contrário usa o `PublicQueryEndpoint` configurado.
- O script NÃO aplica updates automaticamente. Ele gera `scripts/updates_confirmados.sql` para revisão.
#>

# Tentativa de obter credenciais descriptografadas do sistema (db.properties + master.key)
$configPath = 'C:/ProgramData/spr/notas/db.properties'
$masterKey  = 'C:/ProgramData/spr/master.key'

$DbUrl = ''
$DbHost = ''
$DbPort = 3306
$DbName = ''
$DbUser = ''
$DbPassword = ''

function Has-Java { $null -ne (Get-Command java -ErrorAction SilentlyContinue) }
function Has-Maven { $null -ne (Get-Command mvn -ErrorAction SilentlyContinue) }

# Tentar java (usa tools/DecryptDbProps.class), senão tentar mvn exec, senão fallback
if (Has-Java) {
    Write-Host "Java encontrado. Invocando DecryptDbProps via java..."
    $args = "$configPath $masterKey"
    try {
        $out = & java -cp ".;tools" DecryptDbProps $configPath $masterKey 2>&1
    } catch {
        $out = $_.Exception.Message
    }
    foreach ($l in $out) {
        if ($l -match '^DB_URL=(.*)$') { $DbUrl = $Matches[1].Trim() }
        if ($l -match '^DB_USER=(.*)$') { $DbUser = $Matches[1].Trim() }
        if ($l -match '^DB_PASS=(.*)$') { $DbPassword = $Matches[1].Trim() }
    }
    if (-not $DbUser -or -not $DbUrl) {
        Write-Warning "Não foi possível obter DB_USER/DB_URL via DecryptDbProps Java. Tentando via mvn..."
        if (Has-Maven) {
            $args = "$configPath $masterKey"
            $out = & mvn -q exec:java -Dexec.mainClass="com.monitor.funarpen.tools.DecryptDbProps" -Dexec.args="$args" 2>&1
            foreach ($l in $out) {
                if ($l -match '^DB_URL=(.*)$') { $DbUrl = $Matches[1].Trim() }
                if ($l -match '^DB_USER=(.*)$') { $DbUser = $Matches[1].Trim() }
                if ($l -match '^DB_PASS=(.*)$') { $DbPassword = $Matches[1].Trim() }
            }
        }
    }
} elseif (Has-Maven) {
    Write-Host "Maven encontrado. Invocando DecryptDbProps via mvn exec:java..."
    $args = "$configPath $masterKey"
    $out = & mvn -q exec:java -Dexec.mainClass="com.monitor.funarpen.tools.DecryptDbProps" -Dexec.args="$args" 2>&1
    foreach ($l in $out) {
        if ($l -match '^DB_URL=(.*)$') { $DbUrl = $Matches[1].Trim() }
        if ($l -match '^DB_USER=(.*)$') { $DbUser = $Matches[1].Trim() }
        if ($l -match '^DB_PASS=(.*)$') { $DbPassword = $Matches[1].Trim() }
    }
} else {
    Write-Warning "Nem java nem mvn encontrados no PATH. Usando fallback hardcoded."
}

if (-not $DbUser -or -not $DbUrl) {
    Write-Warning "Usando credenciais de fallback hardcoded. Atualize o ambiente ou instale java/mvn."
    $DbHost = '100.102.13.23'
    $DbPort = 3306
    $DbName = 'sptabel'
    $DbUser = 'root'
    $DbPassword = 'k15720'
} else {
    # Extrair host/port/dbname de DB_URL (ex: jdbc:mysql://host:3306/dbname?params)
    # Suporta jdbc:mysql e jdbc:mariadb
    if ($DbUrl -match 'jdbc:(?:mysql|mariadb)://([^:/?#]+)(?::(\d+))?/([^\?;]+)') {
        $DbHost = $Matches[1]
        if ($Matches[2]) { $DbPort = [int]$Matches[2] }
        $DbName = $Matches[3]
    } else {
        Write-Warning "Não foi possível extrair host/port/dbname do DB_URL; DB_URL='$DbUrl'"
    }
    Write-Host "DB_URL obtido. Host=$DbHost Port=$DbPort DB=$DbName User=$DbUser"
    Write-Host "DB_URL cru: $DbUrl"
}

$StartDate = '2026-03-01'
$EndDate   = '2026-03-30'

$LocalReceptorBase = 'http://100.102.13.23:8049/maker/api/funarpen/selos'
$ExternalBase = 'https://v11plus.funarpen.com.br/maker/api/funarpen/selos'

$ProtocoloEndpoint = "$LocalReceptorBase/protocolo"
$PublicQueryEndpoint = "$ExternalBase/consulta-publica"

$SampleSize = 5

$OutFolder = 'scripts'
New-Item -Path $OutFolder -ItemType Directory -Force | Out-Null

$QueryFile = Join-Path $OutFolder 'query_selos.sql'
$CsvFile   = Join-Path $OutFolder 'selos_periodo.csv'
$UpdatesFile = Join-Path $OutFolder 'updates_confirmados.sql'

if (Test-Path $UpdatesFile) { Remove-Item $UpdatesFile -Force }

Write-Host "Preparando query para selos entre $StartDate e $EndDate..."

$Sql = @"
SELECT SELO, RETORNO, STATUS, JSON
FROM selados
WHERE (STATUS IS NULL OR STATUS != 'SUCESSO')
    AND DATE(STR_TO_DATE(DATAENVIO, '%Y-%m-%d %H:%i:%s')) BETWEEN '$StartDate' AND '$EndDate'
ORDER BY STR_TO_DATE(DATAENVIO, '%Y-%m-%d %H:%i:%s')
LIMIT 1000;
"@

Set-Content -Path $QueryFile -Value $Sql -Encoding UTF8

function Has-MySqlCli {
    $null -ne (Get-Command mysql -ErrorAction SilentlyContinue)
}

if (Has-MySqlCli) {
    Write-Host "mysql CLI encontrado. Executando query e exportando CSV para $CsvFile"
    $mysqlArgs = @()
    if ($DbHost -and $DbHost.Trim() -ne '') { $mysqlArgs += '-h'; $mysqlArgs += $DbHost }
    if ($DbPort) { $mysqlArgs += '-P'; $mysqlArgs += $DbPort }
    if ($DbUser -and $DbUser.Trim() -ne '') { $mysqlArgs += '-u'; $mysqlArgs += $DbUser }
    if ($DbName -and $DbName.Trim() -ne '') { $mysqlArgs += '-D'; $mysqlArgs += $DbName }
    # adicionar senha como último parâmetro -p<pass> (evita exposição fácil no log)
    if ($DbPassword -ne $null -and $DbPassword.Trim() -ne '') { $mysqlArgs += ("-p{0}" -f $DbPassword) }
    $mysqlArgs += '-N'
    # Passar SQL via -e como um único argumento
    $mysqlArgs += '-e'
    # Garantir que a SQL esteja em uma linha ou entre aspas
    $sqlSingleLine = $Sql -replace "\r?\n"," "
    $mysqlArgs += $sqlSingleLine

    # Log de debug (sem senha)
    $debugArgs = $mysqlArgs -join ' '
    $debugArgsNoPass = ($debugArgs -replace "-p[^\s]+","-p******")
    Write-Host "Invoking mysql: mysql $debugArgsNoPass"

    & mysql @mysqlArgs | Out-File -FilePath $CsvFile -Encoding UTF8
} else {
    Write-Warning "mysql CLI não encontrado. A query foi escrita em $QueryFile. Execute manualmente a query e gere um CSV tab-separated em $CsvFile com colunas SELO,RETORNO,STATUS,JSON"
    exit 0
}

$rows = Get-Content $CsvFile | Where-Object { $_ -and $_.Trim() -ne '' } | ForEach-Object {
    $parts = $_ -split "\t"
    [PSCustomObject]@{ SELO = $parts[0]; RETORNO = $parts[1]; STATUS = $parts[2]; JSON = ($parts[3..($parts.Length-1)] -join "\t") }
}

if (-not $rows) { Write-Host "Nenhum selo encontrado no período com STATUS != 'SUCESSO'."; exit 0 }

Write-Host "Total de selos extraídos: $($rows.Count). Gerando amostra de $SampleSize selos."

$sample = $rows | Select-Object -First $SampleSize

foreach ($r in $sample) {
    $rawSelo = $r.SELO
    $rawRetorno = $r.RETORNO
    # Sanitiza: remove prefixos '=' e espaços, e trim
    $selo = if ($rawSelo) { ($rawSelo -replace '^[=\s]+','').Trim() } else { $null }
    $retorno = if ($rawRetorno) { ($rawRetorno -replace '^[=\s]+','').Trim() } else { $null }

    Write-Host "`nProcessando selo: $selo  (RETORNO: $retorno)"

    if ($retorno -and $retorno -ne '') {
        $retornoEncoded = [System.Uri]::EscapeDataString($retorno)
        $url = "$ProtocoloEndpoint?protocolo=$retornoEncoded"
        Write-Host "Consultando protocolo (local): $url"
        try { $resp = Invoke-RestMethod -Method Get -Uri $url -TimeoutSec 30 -ErrorAction Stop; Write-Host "Resposta protocolo local recebida." } catch { Write-Warning "Falha na consulta local por protocolo: $($_.Exception.Message)"; $resp = $null }

        if ($resp -and ($resp.status -eq 'SUCESSO' -or $resp.result -eq 'SUCESSO')) {
            $updateSql = "UPDATE selados SET STATUS='SUCESSO' WHERE SELO='$selo';"
            Add-Content -Path $UpdatesFile -Value $updateSql
            Write-Host "Marcado para update: $selo -> SUCESSO"
            continue
        }
    }

    if ($selo -and $selo -ne '') {
        Write-Host "Tentando consulta pública/external para selo $selo"
        $seloEncoded = [System.Uri]::EscapeDataString($selo)
        $publicUrl = "$PublicQueryEndpoint?selo=$seloEncoded"
        Write-Host "Consultando: $publicUrl"
        try { $pubResp = Invoke-RestMethod -Method Get -Uri $publicUrl -TimeoutSec 30 -ErrorAction Stop; Write-Host "Resposta pública recebida." } catch { Write-Warning "Falha na consulta pública: $($_.Exception.Message)"; $pubResp = $null }

        if ($pubResp -and ($pubResp.status -eq 'SUCESSO' -or $pubResp.result -eq 'SUCESSO')) {
            $updateSql = "UPDATE selados SET STATUS='SUCESSO' WHERE SELO='$selo';"
            Add-Content -Path $UpdatesFile -Value $updateSql
            Write-Host "Marcado para update (público): $selo -> SUCESSO"
        } else {
            Write-Host "Não confirmada recepção para selo $selo"
        }
    } else {
        Write-Host "Selo vazio ou inválido. Pulando."
    }
}

Write-Host "Processamento da amostra concluído. Revise: $UpdatesFile e execute contra o banco quando apropriado."
