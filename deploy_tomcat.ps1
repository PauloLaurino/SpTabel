# Deploy para Tomcat 100.102.13.23:8049
$sourceWar = "C:\Desenvolvimento\Seprocom\Notas\target\notas.war"
$tomcatHost = "100.102.13.23"
$tomcatPort = 8049
$tomcatUser = "admin"
$tomcatPwd = "admin"

Write-Host "=== Deploy Notas V11.12 ===" -ForegroundColor Cyan
Write-Host "Servidor: $tomcatHost`:$tomcatPort"

# Verificar se WAR existe
if (!(Test-Path $sourceWar)) {
    Write-Host "ERRO: WAR nao encontrado: $sourceWar" -ForegroundColor Red
    exit 1
}

# Copiar WAR para servidor
Write-Host "`nCopiando WAR para servidor..." -ForegroundColor Yellow
$destPath = "\\$tomcatHost\c$\tomcat9\webapps\notas.war"
try {
    Copy-Item $sourceWar $destPath -Force
    Write-Host "WAR copiado com sucesso" -ForegroundColor Green
} catch {
    Write-Host "ERRO ao copiar: $_" -ForegroundColor Red
    exit 1
}

# Aguardar deploy
Write-Host "`nAguardando deploy (10 segundos)..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Testar endpoint
Write-Host "`nTestando endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://$tomcatHost`:$tomcatPort/notas/maker/api/funarpen/selos/status" -Method GET -TimeoutSec 15 -ErrorAction Stop
    Write-Host "Status: OK" -ForegroundColor Green
} catch {
    Write-Host "Aviso: Endpoint nao responde ainda" -ForegroundColor Yellow
}

Write-Host "`n=== Deploy Concluido ===" -ForegroundColor Cyan
Write-Host "Acesse: http://$tomcatHost`:$tomcatPort/notas"