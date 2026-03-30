# Script PowerShell para executar a sanitização em lote dos selos versão 112
# Endpoint: /maker/api/funarpen/selos/sanitizar/lote
# Requer API Key: MAKER5_INTEGRATION_KEY

param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$ApiKey = "MAKER5_INTEGRATION_KEY"
)

$endpoint = "$BaseUrl/maker/api/funarpen/selos/sanitizar/lote"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  SANITIZAÇÃO EM LOTE - SELOS FUNARPEN" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Endpoint: $endpoint" -ForegroundColor Yellow
Write-Host ""

try {
    Write-Host "[1/1] Executando sanitização em lote..." -ForegroundColor Green
    
    $response = Invoke-RestMethod -Uri $endpoint `
        -Method GET `
        -Headers @{
            "X-API-Key" = $ApiKey
            "Content-Type" = "application/json"
        } `
        -TimeoutSec 300
    
    Write-Host ""
    Write-Host "=== RESULTADO ===" -ForegroundColor Cyan
    Write-Host "Sucesso: $($response.sucesso)" -ForegroundColor Green
    Write-Host "Erros: $($response.erro)" -ForegroundColor Red
    Write-Host "Total: $($response.totalRegistros)" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Mensagem: $($response.mensagem)" -ForegroundColor White
    
} catch {
    Write-Host ""
    Write-Host "❌ ERRO: $($_.Exception.Message)" -ForegroundColor Red
    
    # Tentar obter detalhes do erro
    if ($_.Exception.Response) {
        $statusCode = [int]$_.Exception.Response.StatusCode
        Write-Host "   Status Code: $statusCode" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "Pressione ENTER para sair..." -ForegroundColor Gray
Read-Host
