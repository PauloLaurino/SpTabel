# Script para testar sanitização do TIPO 455
# Uso: .\test_sanitizar_455.ps1

param(
    [string]$SeloDigital = "SFTN1.cG7Rb.Mwfwe-KRsZM.1122q",
    [string]$DBHost = "100.102.13.23",
    [string]$DBPort = "3306",
    [string]$DBName = "sptabel",
    [string]$DBUser = "root",
    [string]$DBPass = "k15720"
)

Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "  Teste Sanitização TIPO 455" -ForegroundColor Cyan
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Buscar JSON original do banco
Write-Host "[1/4] Buscando JSON original do selo: $SeloDigital" -ForegroundColor Yellow

$query = @"
SELECT sel.JSON, sel.JSON12, p.DOC_PAR, p.DTVERSAO_FUNARPEN
FROM selados sel
INNER JOIN parametros p ON p.CODIGO_PAR = 1
WHERE sel.SELO = '$SeloDigital'
"@

$jsonOriginal = $null
$json12Atual = $null
$docPar = $null

try {
    $result = mysql -h $DBHost -P $DBPort -u $DBUser -p$DBPass $DBName -e $query --batch --skip-column-names 2>$null
    
    if ($result) {
        $parts = $result -split "`t"
        if ($parts.Count -ge 2) {
            $jsonOriginal = $parts[0]
            $json12Atual = $parts[1]
            $docPar = $parts[2]
        }
    }
} catch {
    Write-Host "   Erro ao conectar ao banco: $_" -ForegroundColor Red
    exit 1
}

if (-not $jsonOriginal) {
    Write-Host "   JSON original não encontrado para o selo: $SeloDigital" -ForegroundColor Red
    exit 1
}

Write-Host "   JSON original encontrado ($($jsonOriginal.Length) caracteres)" -ForegroundColor Green
Write-Host "   DOC_PAR: $docPar" -ForegroundColor Green

# 2. Salvar JSON original em arquivo temporário
Write-Host ""
Write-Host "[2/4] Salvando JSON original em arquivo temporário" -ForegroundColor Yellow

$tempFile = "$env:TEMP\selado_original_455.json"
$jsonOriginal | Out-File -FilePath $tempFile -Encoding UTF8
Write-Host "   Arquivo: $tempFile" -ForegroundColor Green

# 3. Executar sanitização via Java
Write-Host ""
Write-Host "[3/4] Executando sanitização..." -ForegroundColor Yellow

# Compilar se necessário
$javaFile = "src\main\java\com\selador\util\SeloJsonSanitizerNotas.java"
$classFile = "target\classes\com\selador\util\SeloJsonSanitizerNotas.class"

if (-not (Test-Path $classFile)) {
    Write-Host "   Compilando Java..." -ForegroundColor Yellow
    javac -cp "target\classes;target\dependency\*" -d target\classes $javaFile 2>$null
}

# Executar sanitização
$javaCmd = "java -cp `"target\classes;target\dependency\*`" com.selador.util.SeloJsonSanitizerNotas `"$SeloDigital`""
Write-Host "   Comando: $javaCmd" -ForegroundColor Gray

Invoke-Expression $javaCmd

# 4. Verificar resultado
Write-Host ""
Write-Host "[4/4] Verificando resultado..." -ForegroundColor Yellow

$queryResult = @"
SELECT JSON12 FROM selados WHERE SELO = '$SeloDigital'
"@

$json12Novo = mysql -h $DBHost -P $DBPort -u $DBUser -p$DBPass $DBName -e $queryResult --batch --skip-column-names 2>$null

if ($json12Novo) {
    Write-Host "   JSON12 atualizado com sucesso!" -ForegroundColor Green
    
    # Salvar JSON12 em arquivo para análise
    $outputFile = "selados.JSON12"
    $json12Novo | Out-File -FilePath $outputFile -Encoding UTF8
    Write-Host "   Arquivo salvo: $outputFile" -ForegroundColor Green
    
    # Exibir resumo
    Write-Host ""
    Write-Host "===========================================" -ForegroundColor Cyan
    Write-Host "  Resumo da Sanitização" -ForegroundColor Cyan
    Write-Host "===========================================" -ForegroundColor Cyan
    
    # Parse do JSON para exibir informações
    try {
        $jsonObj = $json12Novo | ConvertFrom-Json
        
        Write-Host "  Ambiente: $($jsonObj.ambiente)" -ForegroundColor White
        Write-Host "  Documento Responsável: $($jsonObj.documentoResponsavel)" -ForegroundColor White
        Write-Host "  Código Ofício: $($jsonObj.codigoOficio)" -ForegroundColor White
        Write-Host ""
        Write-Host "  Selo:" -ForegroundColor Yellow
        Write-Host "    Selo Digital: $($jsonObj.selo.seloDigital)" -ForegroundColor White
        Write-Host "    Código Tipo Ato: $($jsonObj.selo.codigoTipoAto)" -ForegroundColor White
        Write-Host "    IDAP: $($jsonObj.selo.idap)" -ForegroundColor White
        Write-Host ""
        Write-Host "  Propriedades:" -ForegroundColor Yellow
        Write-Host "    Tipo: $($jsonObj.selo.propriedades.tipo)" -ForegroundColor White
        
        if ($jsonObj.selo.propriedades.solicitanteAto) {
            Write-Host "    Solicitante: $($jsonObj.selo.propriedades.solicitanteAto.nomeRazao)" -ForegroundColor White
            Write-Host "    Documento: $($jsonObj.selo.propriedades.solicitanteAto.numeroDocumento)" -ForegroundColor White
        }
        
        if ($jsonObj.selo.propriedades.signatarios) {
            Write-Host "    Signatários: $($jsonObj.selo.propriedades.signatarios.Count)" -ForegroundColor White
        }
        
        Write-Host ""
        Write-Host "  Verbas:" -ForegroundColor Yellow
        Write-Host "    Emolumentos: $($jsonObj.selo.verbas.emolumentos)" -ForegroundColor White
        Write-Host "    FUNREJUS: $($jsonObj.selo.verbas.funrejus)" -ForegroundColor White
        Write-Host "    ISS: $($jsonObj.selo.verbas.iss)" -ForegroundColor White
        
    } catch {
        Write-Host "   Erro ao parsear JSON: $_" -ForegroundColor Red
    }
    
} else {
    Write-Host "   Erro: JSON12 não foi atualizado" -ForegroundColor Red
}

Write-Host ""
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "  Teste concluído" -ForegroundColor Cyan
Write-Host "===========================================" -ForegroundColor Cyan
