# Script para verificar status dos selos
# Período: 2026-03-01 até 2026-03-20

param(
    [string]$DBHost = "100.102.13.23",
    [string]$DBPort = "3306",
    [string]$DBName = "sptabel",
    [string]$DBUser = "root",
    [string]$DBPass = "k15720",
    [string]$DataInicio = "2026-03-01",
    [string]$DataFim = "2026-03-20"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Verificação de Status dos Selos" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Consultar status geral
Write-Host "[1/2] Consultando status geral..." -ForegroundColor Yellow

$queryStatusGeral = @"
SELECT 
    COUNT(*) as total_selos,
    SUM(CASE WHEN STATUS = 'SUCESSO' THEN 1 ELSE 0 END) as sucessos,
    SUM(CASE WHEN STATUS IS NULL OR STATUS != 'SUCESSO' THEN 1 ELSE 0 END) as pendentes,
    SUM(CASE WHEN STATUS = 'ERRO' THEN 1 ELSE 0 END) as erros
FROM selados
WHERE DATAENVIO >= '$DataInicio'
AND DATAENVIO <= '$DataFim';
"@

$resultStatusGeral = mysql -h $DBHost -P $DBPort -u $DBUser -p$DBPass $DBName -e $queryStatusGeral 2>$null

if ($resultStatusGeral -ne $null -and $resultStatusGeral.Count -gt 1) {
    $linesStatus = $resultStatusGeral -split "`n"
    $dataStatus = $linesStatus[1] -split "`t"
    $total = $dataStatus[0]
    $sucessos = $dataStatus[1]
    $pendentes = $dataStatus[2]
    $erros = $dataStatus[3]
    
    Write-Host "   Período: $DataInicio até $DataFim" -ForegroundColor Green
    Write-Host "   Total de selados: $total" -ForegroundColor White
    Write-Host "   Recepcionados com sucesso: $sucessos" -ForegroundColor Green
    Write-Host "   Pendentes: $pendentes" -ForegroundColor Yellow
    Write-Host "   Erros: $erros" -ForegroundColor Red
} else {
    Write-Host "   Nenhum selado encontrado no período." -ForegroundColor Yellow
}

Write-Host ""

# 2. Consultar selos pendentes
Write-Host "[2/2] Consultando selos pendentes..." -ForegroundColor Yellow

$querySelosPendentes = @"
SELECT 
    s.selo_sel,
    s.idap_sel,
    s.STATUS,
    sel.DATAENVIO
FROM selos s
INNER JOIN selados sel ON sel.SELO = s.selo_sel
WHERE sel.DATAENVIO >= '$DataInicio'
AND sel.DATAENVIO <= '$DataFim'
AND (sel.STATUS IS NULL OR sel.STATUS != 'SUCESSO')
ORDER BY sel.DATAENVIO
LIMIT 10;
"@

$resultSelosPendentes = mysql -h $DBHost -P $DBPort -u $DBUser -p$DBPass $DBName -e $querySelosPendentes 2>$null

if ($resultSelosPendentes -ne $null -and $resultSelosPendentes.Count -gt 1) {
    $linesSelos = $resultSelosPendentes -split "`n"
    $totalPendentes = $linesSelos.Count - 1
    
    Write-Host "   Total de selos pendentes (limitado a 10): $totalPendentes" -ForegroundColor Green
    Write-Host ""
    Write-Host "   Lista de selos pendentes:" -ForegroundColor Yellow
    
    for ($i = 1; $i -le $totalPendentes; $i++) {
        $dataLine = $linesSelos[$i] -split "`t"
        $seloDigital = $dataLine[0]
        $idap = $dataLine[1]
        $status = $dataLine[2]
        $dataEnvio = $dataLine[3]
        
        Write-Host "   $i. $seloDigital (Status: $status, Data: $dataEnvio)" -ForegroundColor White
    }
} else {
    Write-Host "   Nenhum selo pendente encontrado." -ForegroundColor Green
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Verificação concluída!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
