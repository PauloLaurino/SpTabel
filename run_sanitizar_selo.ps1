# Script PowerShell para testar a sanitização de um selo específico
# Endpoint: /maker/api/funarpen/selos/sanitizar/{seloDigital}
# Requer API Key: MAKER5_INTEGRATION_KEY

param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$ApiKey = "MAKER5_INTEGRATION_KEY",
    [string]$SelloDigital = "SFTN1.cG2Rb.Mwfwe-GR3ZM.1122q"
)

$endpoint = "$BaseUrl/maker/api/funarpen/selos/sanitizar/$SelloDigital"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  SANITIZAR SELO ESPECIFICO" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Selo: $SelloDigital" -ForegroundColor Yellow
Write-Host "Endpoint: $endpoint" -ForegroundColor Yellow
Write-Host ""

try {
    Write-Host "[1/1] Executando sanitização..." -ForegroundColor Green
    
    $response = Invoke-RestMethod -Uri $endpoint `
        -Method GET `
        -Headers @{
        "X-API-Key"    = $ApiKey
        "Content-Type" = "application/json"
    } `
        -TimeoutSec 60
    
    Write-Host ""
    Write-Host "=== RESULTADO ===" -ForegroundColor Cyan
    Write-Host "Success: $($response.success)" -ForegroundColor $(if ($response.success) { "Green" } else { "Red" })
    Write-Host "Mensagem: $($response.mensagem)" -ForegroundColor White
    
    if ($response.seloDigital) {
        Write-Host "Selo Digital: $($response.seloDigital)" -ForegroundColor Yellow
    }
    
}
catch {
    Write-Host ""
    Write-Host "ERRO: $($_.Exception.Message)" -ForegroundColor Red
    
    if ($_.Exception.Response) {
        $statusCode = [int]$_.Exception.Response.StatusCode
        Write-Host "Status Code: $statusCode" -ForegroundColor Red
    }
}

Write-Host ""
