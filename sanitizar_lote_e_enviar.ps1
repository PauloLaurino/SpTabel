# Script para sanitização em lote e envio para o FUNARPEN
# Processa selos do período 2026-03-01 até 2026-03-20 com STATUS != 'SUCESSO'

param(
    [string]$DBHost = "100.102.13.23",
    [string]$DBPort = "3306",
    [string]$DBName = "sptabel",
    [string]$DBUser = "root",
    [string]$DBPass = "k15720",
    [string]$DataInicio = "2026-02-20",
    [string]$DataFim = "2026-03-27",
    [string]$Url = "https://v11plus.funarpen.com.br/selos/recepcao"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Sanitização em Lote e Envio FUNARPEN" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Consultar parâmetros
Write-Host "[1/6] Consultando parâmetros..." -ForegroundColor Yellow

$queryParametros = @"
SELECT 
    AMBIENTE_PAR,
    TOKENFUNARPEN_PAR,
    CAMINHOCERTIFICADO_PAR,
    SENHACERTIFICADO_PAR,
    DOC_PAR
FROM parametros 
WHERE CODIGO_PAR = 1;
"@

$resultParametros = mysql -h $DBHost -P $DBPort -u $DBUser -p$DBPass $DBName -e $queryParametros 2>$null

if ($resultParametros -eq $null -or $resultParametros.Count -eq 0) {
    Write-Host "   Parâmetros não encontrados." -ForegroundColor Red
    exit 1
}

$linesParam = $resultParametros -split "`n"
$dataParam = $linesParam[1] -split "`t"
$ambiente = $dataParam[0]
$token = $dataParam[1]
$caminhoCertificado = $dataParam[2]
$senhaCertificado = $dataParam[3]
$docPar = $dataParam[4]

Write-Host "   Ambiente: $ambiente" -ForegroundColor Green
Write-Host "   Token: $($token.Substring(0, 20))..." -ForegroundColor Green
Write-Host ""

# 2. Consultar selos pendentes
Write-Host "[2/6] Consultando selos pendentes..." -ForegroundColor Yellow
Write-Host "   Período: $DataInicio até $DataFim" -ForegroundColor Green

$querySelosPendentes = @"
SELECT DISTINCT 
    s.selo_sel,
    s.idap_sel,
    s.JSON,
    s.STATUS
FROM selos s
INNER JOIN selados sel ON sel.SELO = s.selo_sel
WHERE sel.DATAENVIO >= '$DataInicio'
AND sel.DATAENVIO <= '$DataFim'
AND (sel.STATUS IS NULL OR sel.STATUS != 'SUCESSO')
ORDER BY sel.DATAENVIO;
"@

$resultSelosPendentes = mysql -h $DBHost -P $DBPort -u $DBUser -p$DBPass $DBName -e $querySelosPendentes 2>$null

if ($resultSelosPendentes -eq $null -or $resultSelosPendentes.Count -eq 0) {
    Write-Host "   Nenhum selo pendente encontrado no período." -ForegroundColor Yellow
    exit 0
}

$linesSelos = $resultSelosPendentes -split "`n"
$totalSelos = $linesSelos.Count - 1

Write-Host "   Total de selos pendentes: $totalSelos" -ForegroundColor Green
Write-Host ""

# 3. Processar cada selo
Write-Host "[3/6] Processando selos..." -ForegroundColor Yellow

$sucessos = 0
$erros = 0

for ($i = 1; $i -le $totalSelos; $i++) {
    $dataLine = $linesSelos[$i] -split "`t"
    $seloDigital = $dataLine[0]
    $idap = $dataLine[1]
    $jsonOriginal = $dataLine[2]
    $status = $dataLine[3]
    
    Write-Host "   Processando $i/$totalSelos : $seloDigital" -ForegroundColor White
    
    try {
        # Extrair num_rec do IDAP
        $numRec = $idap.Substring(10, 10)
        
        # Consultar fin_reccab
        $queryFinRecCab = @"
SELECT nomecli_rec, cpfcli_rec 
FROM fin_reccab 
WHERE num_rec = '$numRec';
"@
        $resultFinRecCab = mysql -h $DBHost -P $DBPort -u $DBUser -p$DBPass $DBName -e $queryFinRecCab 2>$null
        
        $nomeSolicitante = $null
        $cpfSolicitante = $null
        
        if ($resultFinRecCab -ne $null -and $resultFinRecCab.Count -gt 1) {
            $linesFin = $resultFinRecCab -split "`n"
            $dataFin = $linesFin[1] -split "`t"
            $nomeSolicitante = $dataFin[0]
            $cpfSolicitante = $dataFin[1]
        }
        
        # Processar JSON
        $jsonObj = $jsonOriginal | ConvertFrom-Json
        
        $codigoTipoAto = $jsonObj.selo.codigoTipoAto
        
        $tipoConsulta = 2
        if ($codigoTipoAto -eq 403) {
            $tipoConsulta = 1
        } elseif ($codigoTipoAto -eq 402) {
            $tipoConsulta = 2
        } elseif ($codigoTipoAto -eq 404) {
            $tipoConsulta = 3
        }
        
        # Extrair signatários
        $signatarios = @()
        if ($jsonObj.selo.ListaPropriedades) {
            $listaProp = $jsonObj.selo.ListaPropriedades
            $ultimoNome = $null
            $ultimoDoc = $null
            
            foreach ($item in $listaProp) {
                $nomeProp = $item.NomePropriedade
                $valorProp = $item.ValorPropriedade
                
                if ($nomeProp -match "envolvid" -or $nomeProp -match "envolv") {
                    if ($nomeProp -match "nome" -or $nomeProp -match "razao" -or $nomeProp -match "nome_razao") {
                        $ultimoNome = $valorProp
                    } elseif ($nomeProp -match "cpf" -or $nomeProp -match "cnpj" -or $nomeProp -match "cpf_cnpj" -or $nomeProp -match "documento") {
                        $ultimoDoc = $valorProp
                    }
                    
                    if ($ultimoNome -ne $null -and $ultimoDoc -ne $null) {
                        $docTipo = 12
                        $docLimpo = $ultimoDoc -replace "[^0-9]", ""
                        if ($docLimpo.Length -eq 11) {
                            $docTipo = 1
                        } elseif ($docLimpo.Length -eq 14) {
                            $docTipo = 2
                        }
                        
                        $signatarios += @{
                            nomeRazao = $ultimoNome
                            documentoTipo = $docTipo
                            documentoNumero = $ultimoDoc
                        }
                        
                        $ultimoNome = $null
                        $ultimoDoc = $null
                    }
                }
            }
        }
        
        # Construir JSON sanitizado
        $jsonSanitizado = @{
            ambiente = $jsonObj.ambiente
            codigoEmpresa = $jsonObj.codigoEmpresa
            codigoOficio = $jsonObj.codigoOficio
            documentoResponsavel = if ($docPar -ne $null -and $docPar -ne "0") { $docPar } else { $jsonObj.documentoResponsavel }
            selo = @{
                seloDigital = $jsonObj.selo.seloDigital
                codigoPedido = $jsonObj.selo.codigoPedido
                tipoGratuidade = $jsonObj.selo.tipoGratuidade
                codigoTipoAto = 455
                tipoEmissaoAto = $jsonObj.selo.tipoEmissaoAto
                idap = $jsonObj.selo.idap
                versao = $jsonObj.selo.versao
                dataSeloEmitido = $jsonObj.selo.dataSeloEmitido
                dataAtoPraticado = $jsonObj.selo.dataAtoPraticado
                seloRetificado = $jsonObj.selo.seloRetificado
            }
            solicitanteAto = @{
                nomeRazao = if ($nomeSolicitante -ne $null) { $nomeSolicitante } else { $null }
                documentoTipo = if ($cpfSolicitante -ne $null) { 
                    $docLimpo = $cpfSolicitante -replace "[^0-9]", ""
                    if ($docLimpo.Length -eq 11) { 1 } elseif ($docLimpo.Length -eq 14) { 2 } else { 12 }
                } else { 12 }
                documentoNumero = if ($cpfSolicitante -ne $null) { $cpfSolicitante } else { $null }
                endereco = $null
            }
            autenticacao = @{
                totalPaginas = $null
            }
            certidao = @{
                consulta = @{
                    tipo = $tipoConsulta
                }
            }
            reconhecimento = @{
                especie = 1
                quantidadePaginasAto = $null
                quantidadePartesEnvolvidasAto = $signatarios.Count
                data = $jsonObj.selo.dataAtoPraticado
                descricao = $null
            }
            signatarios = $signatarios
            verbas = @{
                emolumentos = if ($jsonObj.selo.verbas.emolumentos) { $jsonObj.selo.verbas.emolumentos } else { 0.0 }
                vrcExt = if ($jsonObj.selo.verbas.vrcExt) { $jsonObj.selo.verbas.vrcExt } else { 0.0 }
                funrejus = if ($jsonObj.selo.verbas.funrejus) { $jsonObj.selo.verbas.funrejus } else { 0.0 }
                iss = if ($jsonObj.selo.verbas.iss) { $jsonObj.selo.verbas.iss } else { 0.0 }
                fundep = if ($jsonObj.selo.verbas.fundep) { $jsonObj.selo.verbas.fundep } else { 0.0 }
                funarpen = if ($jsonObj.selo.verbas.funarpen) { $jsonObj.selo.verbas.funarpen } else { 0.0 }
                distribuidor = if ($jsonObj.selo.verbas.distribuidor) { $jsonObj.selo.verbas.distribuidor } else { 0.0 }
                valorAdicional = if ($jsonObj.selo.verbas.valorAdicional) { $jsonObj.selo.verbas.valorAdicional } else { 0.0 }
            }
        }
        
        # Salvar arquivo JSON12
        $jsonSanitizado | ConvertTo-Json -Depth 10 | Out-File -FilePath "selados_$seloDigital.JSON12" -Encoding UTF8
        
        # Enviar para o FUNARPEN
        $jsonPayload = $jsonSanitizado | ConvertTo-Json -Depth 10
        
        $headers = @{
            "Authorization" = "Bearer $token"
            "Content-Type" = "application/json"
        }
        
        if ($caminhoCertificado -ne $null -and $caminhoCertificado -ne "") {
            $cert = New-Object System.Security.Cryptography.X509Certificates.X509Certificate2($caminhoCertificado, $senhaCertificado)
            $response = Invoke-RestMethod -Uri $Url -Method Post -Body $jsonPayload -Headers $headers -Certificate $cert -ErrorAction Stop
        } else {
            $response = Invoke-RestMethod -Uri $Url -Method Post -Body $jsonPayload -Headers $headers -ErrorAction Stop
        }
        
        # Atualizar status no banco
        $queryUpdateStatus = @"
UPDATE selados 
SET STATUS = 'SUCESSO', 
    JSON12 = '$($jsonSanitizado | ConvertTo-Json -Depth 10 -Compress)'
WHERE SELO = '$seloDigital';
"@
        mysql -h $DBHost -P $DBPort -u $DBUser -p$DBPass $DBName -e $queryUpdateStatus 2>$null
        
        Write-Host "      OK - Enviado e atualizado" -ForegroundColor Green
        $sucessos++
        
    } catch {
        Write-Host "      ERRO: $_" -ForegroundColor Red
        
        # Atualizar status de erro
        $queryUpdateErro = @"
UPDATE selados 
SET STATUS = 'ERRO'
WHERE SELO = '$seloDigital';
"@
        mysql -h $DBHost -P $DBPort -u $DBUser -p$DBPass $DBName -e $queryUpdateErro 2>$null
        
        $erros++
    }
}

Write-Host ""
Write-Host "[4/6] Resumo do processamento:" -ForegroundColor Yellow
Write-Host "   Total de selos: $totalSelos" -ForegroundColor White
Write-Host "   Sucessos: $sucessos" -ForegroundColor Green
Write-Host "   Erros: $erros" -ForegroundColor Red
Write-Host ""

# 5. Verificar status geral
Write-Host "[5/6] Verificando status geral..." -ForegroundColor Yellow

$queryStatusGeral = @"
SELECT 
    COUNT(*) as total,
    SUM(CASE WHEN STATUS = 'SUCESSO' THEN 1 ELSE 0 END) as sucessos,
    SUM(CASE WHEN STATUS IS NULL OR STATUS != 'SUCESSO' THEN 1 ELSE 0 END) as pendentes
FROM selados
WHERE DATAENVIO >= '$DataInicio'
AND DATAENVIO <= '$DataFim';
"@

$resultStatusGeral = mysql -h $DBHost -P $DBPort -u $DBUser -p$DBPass $DBName -e $queryStatusGeral 2>$null

if ($resultStatusGeral -ne $null -and $resultStatusGeral.Count -gt 1) {
    $linesStatus = $resultStatusGeral -split "`n"
    $dataStatus = $linesStatus[1] -split "`t"
    $total = $dataStatus[0]
    $sucessosGeral = $dataStatus[1]
    $pendentes = $dataStatus[2]
    
    Write-Host "   Total de selados no período: $total" -ForegroundColor White
    Write-Host "   Recepcionados com sucesso: $sucessosGeral" -ForegroundColor Green
    Write-Host "   Pendentes: $pendentes" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "[6/6] Processamento concluído!" -ForegroundColor Yellow
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Processamento em lote concluído!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
