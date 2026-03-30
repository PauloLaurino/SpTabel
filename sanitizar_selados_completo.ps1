# Script para sanitizar selados.JSON e gerar selados.JSON12
# Regras de sanitização para atos 402, 403, 404, 405, 455

param(
    [string]$Selo = "SFTN1.cG7Rb.Mwfwe-KRsZM.1122q",
    [string]$DBHost = "100.102.13.23",
    [string]$DBPort = "3306",
    [string]$DBName = "sptabel",
    [string]$DBUser = "root",
    [string]$DBPass = "k15720"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Sanitização de selados.JSON" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Consultar dados do selo
Write-Host "[1/6] Consultando dados do selo: $Selo" -ForegroundColor Yellow

$querySelados = @"
SELECT 
    s.selo_sel,
    s.idap_sel,
    s.JSON,
    s.JSON12,
    s.STATUS,
    p.DOC_PAR,
    p.AMBIENTE_PAR,
    p.TOKENFUNARPEN_PAR,
    p.CODTABEL_PAR
FROM selos s
INNER JOIN selados sel ON sel.SELO = s.selo_sel
INNER JOIN parametros p ON p.CODIGO_PAR = 1
WHERE s.selo_sel = '$Selo';
"@

$resultSelados = mysql -h $DBHost -P $DBPort -u $DBUser -p$DBPass $DBName -e $querySelados 2>$null

if ($resultSelados -eq $null -or $resultSelados.Count -eq 0) {
    Write-Host "   Selo não encontrado: $Selo" -ForegroundColor Red
    exit 1
}

# Parsear resultado
$lines = $resultSelados -split "`n"
if ($lines.Count -lt 2) {
    Write-Host "   Selo não encontrado: $Selo" -ForegroundColor Red
    exit 1
}

$dataLine = $lines[1] -split "`t"
$seloDigital = $dataLine[0]
$idap = $dataLine[1]
$jsonOriginal = $dataLine[2]
$json12Atual = $dataLine[3]
$status = $dataLine[4]
$docPar = $dataLine[5]
$ambiente = $dataLine[6]
$token = $dataLine[7]
$codigoTabel = $dataLine[8]

Write-Host "   Selo encontrado: $seloDigital" -ForegroundColor Green
Write-Host "   IDAP: $idap" -ForegroundColor Green
Write-Host "   DOC_PAR: $docPar" -ForegroundColor Green
Write-Host "   Status atual: $status" -ForegroundColor Green
Write-Host ""

# 2. Consultar fin_reccab para obter solicitante
Write-Host "[2/6] Consultando fin_reccab para solicitante..." -ForegroundColor Yellow

# Extrair num_rec do IDAP (posição 11 com 10 caracteres)
$numRec = $idap.Substring(10, 10)
Write-Host "   num_rec extraído do IDAP: $numRec" -ForegroundColor Green

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
    Write-Host "   Solicitante encontrado: $nomeSolicitante" -ForegroundColor Green
    Write-Host "   CPF: $cpfSolicitante" -ForegroundColor Green
} else {
    Write-Host "   Solicitante não encontrado em fin_reccab" -ForegroundColor Yellow
}
Write-Host ""

# 3. Processar JSON original
Write-Host "[3/6] Processando JSON original..." -ForegroundColor Yellow

$jsonObj = $jsonOriginal | ConvertFrom-Json

# Determinar tipo de ato
$codigoTipoAto = $jsonObj.selo.codigoTipoAto
Write-Host "   Código Tipo Ato: $codigoTipoAto" -ForegroundColor Green

# Determinar tipo de consulta (certidao.consulta.tipo)
$tipoConsulta = 2  # Default: Sem Valor Declarado
if ($codigoTipoAto -eq 403) {
    $tipoConsulta = 1  # Com Valor Declarado
} elseif ($codigoTipoAto -eq 402) {
    $tipoConsulta = 2  # Sem Valor Declarado
} elseif ($codigoTipoAto -eq 404) {
    $tipoConsulta = 3  # Sinal Público
}

Write-Host "   Tipo de consulta: $tipoConsulta" -ForegroundColor Green

# Extrair signatários de ListaPropriedades
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
                # Determinar tipo de documento
                $docTipo = 12  # Não Informado
                $docLimpo = $ultimoDoc -replace "[^0-9]", ""
                if ($docLimpo.Length -eq 11) {
                    $docTipo = 1  # CPF
                } elseif ($docLimpo.Length -eq 14) {
                    $docTipo = 2  # CNPJ
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

Write-Host "   Signatários encontrados: $($signatarios.Count)" -ForegroundColor Green

# 4. Construir JSON sanitizado
Write-Host "[4/6] Construindo JSON sanitizado..." -ForegroundColor Yellow

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
        especie = 1  # Por Verdadeira ou Autêntica
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

# 5. Salvar arquivo JSON12
Write-Host "[5/6] Salvando arquivo selados.JSON12..." -ForegroundColor Yellow

$jsonSanitizado | ConvertTo-Json -Depth 10 | Out-File -FilePath "selados.JSON12" -Encoding UTF8

Write-Host "   Arquivo selados.JSON12 salvo com sucesso." -ForegroundColor Green
Write-Host ""

# 6. Verificar se o selo foi recepcionado pelo FUNARPEN
Write-Host "[6/6] Verificando status de recepção..." -ForegroundColor Yellow

$queryStatus = @"
SELECT STATUS 
FROM selados 
WHERE SELO = '$Selo';
"@

$resultStatus = mysql -h $DBHost -P $DBPort -u $DBUser -p$DBPass $DBName -e $queryStatus 2>$null

if ($resultStatus -ne $null -and $resultStatus.Count -gt 1) {
    $linesStatus = $resultStatus -split "`n"
    $statusAtual = $linesStatus[1]
    Write-Host "   Status atual: $statusAtual" -ForegroundColor Green
    
    if ($statusAtual -eq "SUCESSO") {
        Write-Host "   Selo já foi recepcionado com sucesso pelo FUNARPEN." -ForegroundColor Green
    } else {
        Write-Host "   Selo ainda não foi recepcionado. Status: $statusAtual" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Sanitização concluída com sucesso!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Resumo:" -ForegroundColor Yellow
Write-Host "  Selo: $seloDigital" -ForegroundColor White
Write-Host "  Tipo Ato: 455 (Reconhecimento de Firma)" -ForegroundColor White
Write-Host "  Solicitante: $nomeSolicitante" -ForegroundColor White
Write-Host "  Signatários: $($signatarios.Count)" -ForegroundColor White
Write-Host "  Arquivo gerado: selados.JSON12" -ForegroundColor White
