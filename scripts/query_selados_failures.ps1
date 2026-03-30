param(
    [string]$DBHost = "100.75.153.127",
    [string]$DBUser = "root",
    [string]$DBPass = "k15720",
    [string]$DBName = "sptabel",
    [string]$OutFile = "selos_falha_20260301_20260317.tsv",
    [string]$DateFrom = "2026-03-01",
    [string]$DateTo = "2026-03-17"
)

$ErrorActionPreference = 'Stop'
$query = "SELECT selados.SELO AS Selo, selados.RETORNO, selados.STATUS, selados.DATAENVIO FROM selados WHERE STATUS !='SUCESSO' AND DATAENVIO >= '$DateFrom' AND DATAENVIO <= '$DateTo';"

Write-Host "Executando query em $DBName@$DBHost de $DateFrom até $DateTo..." -ForegroundColor Cyan

# Antes de executar, buscar DTVERSAO_FUNARPEN da tabela parametros e garantir que DateFrom >= DTVERSAO_FUNARPEN
try {
    $paramQuery = "SELECT DTVERSAO_FUNARPEN FROM parametros WHERE CODIGO_PAR = 1 LIMIT 1;"
    $paramArgs = @('-h', $DBHost, '-u', $DBUser, $DBName, '-N', '-e', $paramQuery)
    if ($DBPass -ne '') { $paramArgs = @('-h', $DBHost, '-u', $DBUser, "-p$DBPass", $DBName, '-N', '-e', $paramQuery) }
    $paramOut = & mysql @paramArgs 2>$null
    if ($paramOut) {
        $paramDateStr = $paramOut.Trim()
        if ($paramDateStr -ne '') {
            try {
                $paramDate = [datetime]::Parse($paramDateStr)
                $inputDate = [datetime]::Parse($DateFrom)
                if ($inputDate -lt $paramDate) {
                    Write-Host "Ajustando DateFrom ($DateFrom) para DTVERSAO_FUNARPEN ($paramDateStr)" -ForegroundColor Yellow
                    $DateFrom = $paramDate.ToString('yyyy-MM-dd')
                }
            } catch {
                Write-Warning "Não foi possível interpretar DTVERSAO_FUNARPEN retornado: $paramDateStr"
            }
        }
    }
} catch {
    Write-Warning "Falha ao ler parametros.DTVERSAO_FUNARPEN: $_"
}

$mysqlArgs = @('-h', $DBHost, '-u', $DBUser, $DBName, '-B', '-e', $query)
if ($DBPass -ne '') { $mysqlArgs = @('-h', $DBHost, '-u', $DBUser, "-p$DBPass", $DBName, '-B', '-e', $query) }

try {
    $out = & mysql @mysqlArgs 2>&1
} catch {
    Write-Error "Falha ao executar mysql: $_"
    exit 1
}

if (!$out) {
    Write-Warning "Nenhum resultado retornado pela query."
    exit 0
}

# Gravar saída em arquivo TSV
[System.IO.File]::WriteAllText($OutFile, $out)
Write-Host "Resultado salvo em: $OutFile" -ForegroundColor Green

Write-Host "Exemplo para abrir no Excel: importar como delimitado por tabulação." -ForegroundColor Yellow
