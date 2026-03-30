# Script PowerShell para sanitizar TIPO 455 diretamente via SQL
# Conecta ao MariaDB e atualiza JSON12

param(
    [string]$SeloDigital = "SFTN1.cG7Rb.Mwfwe-KRsZM.1122q",
    [string]$DBHost = "100.102.13.23",
    [string]$DBPort = "3306",
    [string]$DBName = "sptabel",
    [string]$DBUser = "root",
    [string]$DBPass = "k15720"
)

Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "  Sanitização TIPO 455 - Direto via SQL" -ForegroundColor Cyan
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Buscar dados do selo
Write-Host "[1/5] Buscando dados do selo: $SeloDigital" -ForegroundColor Yellow

$querySelo = @"
SELECT 
    sel.JSON,
    s.idap,
    s.codigoTipoAto,
    s.dataAtoPraticado,
    p.DOC_PAR
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
INNER JOIN parametros p ON p.CODIGO_PAR = 1
WHERE sel.SELO = '$SeloDigital'
"@

$jsonOriginal = $null
$idap = $null
$codigoTipoAto = $null
$dataAtoPraticado = $null
$docPar = $null

try {
    $result = mysql -h $DBHost -P $DBPort -u $DBUser -p$DBPass $DBName -e $querySelo --batch --skip-column-names 2>$null
    
    if ($result) {
        $parts = $result -split "`t"
        if ($parts.Count -ge 5) {
            $jsonOriginal = $parts[0]
            $idap = $parts[1]
            $codigoTipoAto = $parts[2]
            $dataAtoPraticado = $parts[3]
            $docPar = $parts[4]
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
Write-Host "   IDAP: $idap" -ForegroundColor Green
Write-Host "   Código Tipo Ato: $codigoTipoAto" -ForegroundColor Green
Write-Host "   DOC_PAR: $docPar" -ForegroundColor Green

# 2. Buscar solicitante em fin_reccab
Write-Host ""
Write-Host "[2/5] Buscando solicitante em fin_reccab" -ForegroundColor Yellow

# Extrair num_rec do IDAP (posições 11-20)
$numRec = $idap.Substring(10, 10)
Write-Host "   num_rec extraído: $numRec" -ForegroundColor Gray

$querySolicitante = @"
SELECT nomecli_rec, cpfcli_rec
FROM fin_reccab
WHERE num_rec = '$numRec'
"@

$nomeSolicitante = $null
$cpfSolicitante = $null

try {
    $resultSolicitante = mysql -h $DBHost -P $DBPort -u $DBUser -p$DBPass $DBName -e $querySolicitante --batch --skip-column-names 2>$null
    
    if ($resultSolicitante) {
        $partsSolicitante = $resultSolicitante -split "`t"
        if ($partsSolicitante.Count -ge 2) {
            $nomeSolicitante = $partsSolicitante[0]
            $cpfSolicitante = $partsSolicitante[1]
        }
    }
} catch {
    Write-Host "   Erro ao buscar solicitante: $_" -ForegroundColor Yellow
}

if ($nomeSolicitante) {
    Write-Host "   Solicitante encontrado: $nomeSolicitante" -ForegroundColor Green
    Write-Host "   CPF: $cpfSolicitante" -ForegroundColor Green
} else {
    Write-Host "   Solicitante não encontrado para num_rec: $numRec" -ForegroundColor Yellow
}

# 3. Extrair signatários do JSON original
Write-Host ""
Write-Host "[3/5] Extraindo signatários do JSON original" -ForegroundColor Yellow

# Parse do JSON original
$jsonObj = $jsonOriginal | ConvertFrom-Json

$signatarios = @()

# Procurar em ListaPropriedades
if ($jsonObj.selo.ListaPropriedades) {
    $ultimoNome = $null
    $ultimoNum = $null
    
    foreach ($prop in $jsonObj.selo.ListaPropriedades) {
        $nomeProp = $prop.NomePropriedade
        $valorProp = $prop.ValorPropriedade
        
        if ($nomeProp -match "envolvid" -or $nomeProp -match "emvolv") {
            if ($nomeProp -match "nome_razao" -or $nomeProp -match "nome") {
                $ultimoNome = $valorProp
            } elseif ($nomeProp -match "CPF_CNPJ" -or $nomeProp -match "cpf" -or $nomeProp -match "cnpj") {
                $ultimoNum = $valorProp
            }
            
            if ($ultimoNome -and $ultimoNum) {
                $signatarios += @{
                    nomeRazao = $ultimoNome
                    documentoTipo = if ($ultimoNum.Length -eq 11) { 1 } elseif ($ultimoNum.Length -eq 14) { 2 } else { 12 }
                    documentoNumero = $ultimoNum
                }
                $ultimoNome = $null
                $ultimoNum = $null
            }
        }
    }
}

Write-Host "   Signatários encontrados: $($signatarios.Count)" -ForegroundColor Green

# 4. Determinar tipo (1=Com Valor, 2=Sem Valor, 3=Sinal Público)
Write-Host ""
Write-Host "[4/5] Determinando tipo de reconhecimento" -ForegroundColor Yellow

$tipo = 2 # Default: Sem Valor Declarado
if ($codigoTipoAto -eq 403) { $tipo = 1 }
elseif ($codigoTipoAto -eq 402) { $tipo = 2 }
elseif ($codigoTipoAto -eq 404) { $tipo = 3 }

Write-Host "   Tipo: $tipo (código original: $codigoTipoAto)" -ForegroundColor Green

# 5. Construir JSON sanitizado
Write-Host ""
Write-Host "[5/5] Construindo JSON sanitizado" -ForegroundColor Yellow

# Determinar tipo de documento do solicitante
$tipoDocSolicitante = 12 # Não Informado
if ($cpfSolicitante) {
    if ($cpfSolicitante.Length -eq 11) { $tipoDocSolicitante = 1 } # CPF
    elseif ($cpfSolicitante.Length -eq 14) { $tipoDocSolicitante = 2 } # CNPJ
}

# Construir signatários array
$signatariosJson = @()
foreach ($sig in $signatarios) {
    $signatariosJson += @{
        nomeRazao = $sig.nomeRazao
        documentoTipo = $sig.documentoTipo
        documentoNumero = $sig.documentoNumero
    }
}

# Construir JSON sanitizado
$jsonSanitizado = @{
    ambiente = $jsonObj.ambiente
    documentoResponsavel = $docPar
    codigoEmpresa = $jsonObj.codigoEmpresa
    codigoOficio = $jsonObj.codigoOficio
    selo = @{
        seloDigital = $jsonObj.selo.seloDigital
        codigoPedido = if ($jsonObj.selo.codigoPedido -is [double]) { [long]($jsonObj.selo.codigoPedido * 10) } else { $jsonObj.selo.codigoPedido }
        tipoGratuidade = $jsonObj.selo.tipoGratuidade
        codigoTipoAto = 455
        tipoEmissaoAto = $jsonObj.selo.tipoEmissaoAto
        idap = $jsonObj.selo.idap
        versao = $jsonObj.selo.versao
        dataSeloEmitido = $jsonObj.selo.dataSeloEmitido
        dataAtoPraticado = $dataAtoPraticado
        seloRetificado = $null
        propriedades = @{
            tipo = $tipo
            solicitanteAto = @{
                nomeRazao = $nomeSolicitante
                tipoDocumento = $tipoDocSolicitante
                numeroDocumento = $cpfSolicitante
            }
            reconhecimento = @{
                especie = 1
                quantidadePaginasAto = $null
                quantidadePartesEnvolvidasAto = $signatarios.Count
                data = $dataAtoPraticado
                descricao = $null
            }
            signatarios = $signatariosJson
        }
        verbas = @{
            emolumentos = $jsonObj.selo.verbas.emolumentos
            vrcExt = $jsonObj.selo.verbas.vrcExt
            funrejus = $jsonObj.selo.verbas.funrejus
            iss = $jsonObj.selo.verbas.iss
            fundep = $jsonObj.selo.verbas.fundep
            funarpen = $jsonObj.selo.verbas.funarpen
            distribuidor = $jsonObj.selo.verbas.distribuidor
            valorAdicional = $jsonObj.selo.verbas.valorAdicional
        }
    }
}

# Converter para JSON string
$jsonSanitizadoStr = $jsonSanitizado | ConvertTo-Json -Depth 10

# Salvar em arquivo
$outputFile = "selados.JSON12"
$jsonSanitizadoStr | Out-File -FilePath $outputFile -Encoding UTF8

Write-Host "   JSON sanitizado salvo em: $outputFile" -ForegroundColor Green

# Atualizar no banco
Write-Host ""
Write-Host "Atualizando JSON12 no banco de dados..." -ForegroundColor Yellow

# Escapar aspas simples para SQL
$jsonSanitizadoEscaped = $jsonSanitizadoStr.Replace("'", "''")

$queryUpdate = @"
UPDATE selados 
SET JSON12 = '$jsonSanitizadoEscaped'
WHERE SELO = '$SeloDigital'
"@

try {
    mysql -h $DBHost -P $DBPort -u $DBUser -p$DBPass $DBName -e $queryUpdate 2>$null
    Write-Host "   JSON12 atualizado com sucesso no banco!" -ForegroundColor Green
} catch {
    Write-Host "   Erro ao atualizar banco: $_" -ForegroundColor Red
}

# Exibir resumo
Write-Host ""
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "  Resumo da Sanitização" -ForegroundColor Cyan
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "  Selo Digital: $SeloDigital" -ForegroundColor White
Write-Host "  Código Tipo Ato: 455 (RECONHECIMENTO DE FIRMA)" -ForegroundColor White
Write-Host "  Tipo: $tipo" -ForegroundColor White
Write-Host "  Solicitante: $nomeSolicitante" -ForegroundColor White
Write-Host "  Documento Responsável: $docPar" -ForegroundColor White
Write-Host "  Signatários: $($signatarios.Count)" -ForegroundColor White
Write-Host "  Arquivo: $outputFile" -ForegroundColor White
Write-Host "===========================================" -ForegroundColor Cyan
