-- =================================================================
-- Script para extrair dados do solicitante e dados da escritura
-- Para complementar registros da versão 112
-- =================================================================

-- Dados do Solicitante (primeiro outorgante - QUALIF_NOT1 = 1)
SELECT 
    sel.ID as ID_SELADO,
    sel.SELO,
    -- Dados do Ato
    ato.PROTOC_LIVRO_ATO as PROTOCOLO,
    ato.DT_ATO as DATA_ATO,
    ato.LIVRO_ATO as LIVRO,
    ato.FOLHA_ATO as FOLHA,
    -- Dados do Primeiro Outorgante (Solicitante)
    not_1.NOME_NOT1 as NOME_SOLICITANTE,
    not_1.TIPOPESSOA_NOT1 as TIPO_PESSOA_SOLICITANTE,
    not_1.CPF_NOT1 as CPF_CNPJ_SOLICITANTE,
    -- Dados do Cliente
    cliente.NOME_CLI as NOME_CLIENTE,
    cliente.NUMERO_DOC_CLI as CPF_CLI,
    cliente.DATA_NASCIMENTO_CLI,
    cliente.NACIONALIDADE_CLI,
    cliente.REGCAS_CLI,
    cliente.ENDERECO_CLI,
    cliente.NUMEROEND_CLI,
    cliente.BAIRRO_CLI,
    cidade.NOME_CID as CIDADE,
    cidade.SIGLA_CID as UF,
    cliente.CEP_CLI
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
INNER JOIN parametros p ON p.CODIGO_PAR = 1
INNER JOIN ato ON sel.REGISTRO = ato.PROTOC_LIVRO_ATO
LEFT JOIN not_1 ON not_1.PROTATO_NOT1 = ato.PROTOC_LIVRO_ATO AND not_1.QUALIF_NOT1 = 1
LEFT JOIN cliente ON not_1.TIPOPESSOA_NOT1 = cliente.TIPO_CLI AND not_1.CODCLI_NOT1 = cliente.CODIGO_CLI
LEFT JOIN cidade ON cliente.CODCIDEND_CLI = cidade.CODIGO_CID
WHERE s.versao = '112' AND sel.JSON LIKE '%ambiente%'
LIMIT 10;
