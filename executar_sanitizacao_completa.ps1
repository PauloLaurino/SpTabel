# Script para executar sanitização completa
# Processa atos 402, 403, 404, 405, 455

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
Write-Host "  Execução de Sanitização Completa" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Verificar status inicial
Write-Host "[1/4] Verificando status inicial..." -ForegroundColor Yellow

$queryStatusInicial = @"
SELECT 
    COUNT(*) as total_selos,
    SUM(CASE WHEN STATUS = 'SUCESSO' THEN 1 ELSE 0 END) as sucessos,
    SUM(CASE WHEN STATUS IS NULL OR STATUS != 'SUCESSO' THEN 1 ELSE 0 END) as pendentes
FROM selados
WHERE DATAENVIO >= '$DataInicio'
AND DATAENVIO <= '$DataFim';
"@

$resultStatusInicial = mysql -h $DBHost -P $DBPort -u $DBUser -p$DBPass $DBName -e $queryStatusInicial 2>$null

if ($resultStatusInicial -ne $null -and $resultStatusInicial.Count -gt 1) {
    $linesStatus = $resultStatusInicial -split "`n"
    $dataStatus = $linesStatus[1] -split "`t"
    $total = $dataStatus[0]
    $sucessos = $dataStatus[1]
    $pendentes = $dataStatus[2]
    
    Write-Host "   Período: $DataInicio até $DataFim" -ForegroundColor Green
    Write-Host "   Total de selados: $total" -ForegroundColor White
    Write-Host "   Recepcionados com sucesso: $sucessos" -ForegroundColor Green
    Write-Host "   Pendentes: $pendentes" -ForegroundColor Yellow
}

Write-Host ""

# 2. Executar sanitização em lote
Write-Host "[2/4] Executando sanitização em lote..." -ForegroundColor Yellow

# Chamar script de sanitização em lote
& ".\sanitizar_lote_selados.ps1" -DBHost $DBHost -DBPort $DBPort -DBName $DBName -DBUser $DBUser -DBPass $DBPass -DataInicio $DataInicio -DataFim $DataFim

Write-Host ""

# 3. Verificar status após sanitização
Write-Host "[3/4] Verificando status após sanitização..." -ForegroundColor Yellow

$queryStatusFinal = @"
SELECT 
    COUNT(*) as total_selos,
    SUM(CASE WHEN STATUS = 'SUCESSO' THEN 1 ELSE 0 END) as sucessos,
    SUM(CASE WHEN STATUS IS NULL OR STATUS != 'SUCESSO' THEN 1 ELSE 0 END) as pendentes
FROM selados
WHERE DATAENVIO >= '$DataInicio'
AND DATAENVIO <= '$DataFim';
"@

$resultStatusFinal = mysql -h $DBHost -P $DBPort -u $DBUser -p$DBPass $DBName -e $queryStatusFinal 2>$null

if ($resultStatusFinal -ne $null -and $resultStatusFinal.Count -gt 1) {
    $linesStatus = $resultStatusFinal -split "`n"
    $dataStatus = $linesStatus[1] -split "`t"
    $total = $dataStatus[0]
    $sucessos = $dataStatus[1]
    $pendentes = $dataStatus[2]
    
    Write-Host "   Período: $DataInicio até $DataFim" -ForegroundColor Green
    Write-Host "   Total de selados: $total" -ForegroundColor White
    Write-Host "   Recepcionados com sucesso: $sucessos" -ForegroundColor Green
    Write-Host "   Pendentes: $pendentes" -ForegroundColor Yellow
}

Write-Host ""

# 4. Resumo final
Write-Host "[4/4] Resumo final:" -ForegroundColor Yellow
Write-Host "   Processo de sanitização concluído." -ForegroundColor Green
Write-Host "   Verifique os arquivos JSON12 gerados." -ForegroundColor White
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Execução concluída!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
