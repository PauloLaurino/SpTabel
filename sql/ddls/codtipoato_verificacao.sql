-- ==============================================
-- VERIFICAÇÃO DA TABELA CODTIPOATO
-- Mapeamento de códigos de tipo de ato
-- ==============================================

-- 1. Verificar se a tabela existe
SHOW TABLES LIKE 'codtipoato';

-- 2. Verificar estrutura da tabela
DESCRIBE codtipoato;

-- 3. Listar todos os tipos de ato para Notas (TIPO_COD = 'N')
SELECT 
    CODCOD_COD AS codigo,
    NUMEROTIPO AS numeroTipo,
    CODSELO AS codSelo,
    DESCRICAO_COD AS descricao
FROM codtipoato
WHERE TIPO_COD = 'N'
ORDER BY NUMEROTIPO;

-- 4. Verificar mapeamento específico
SELECT 
    CODCOD_COD AS codigo,
    NUMEROTIPO AS numeroTipo,
    CODSELO AS codSelo
FROM codtipoato
WHERE TIPO_COD = 'N'
    AND NUMEROTIPO IN (401, 406, 407, 412, 416, 417, 418, 421, 424, 428, 429, 430, 433, 436, 437, 443, 445, 446, 450, 452, 453, 454, 455, 456, 457, 458, 459)
ORDER BY NUMEROTIPO;

-- 5. Verificar tipos faltando para Notas
SELECT expected.numero AS numeroTipo_expected
FROM (
    SELECT 401 AS numero UNION ALL
    SELECT 406 UNION ALL
    SELECT 407 UNION ALL
    SELECT 412 UNION ALL
    SELECT 416 UNION ALL
    SELECT 417 UNION ALL
    SELECT 418 UNION ALL
    SELECT 421 UNION ALL
    SELECT 424 UNION ALL
    SELECT 428 UNION ALL
    SELECT 429 UNION ALL
    SELECT 430 UNION ALL
    SELECT 433 UNION ALL
    SELECT 436 UNION ALL
    SELECT 437 UNION ALL
    SELECT 443 UNION ALL
    SELECT 445 UNION ALL
    SELECT 446 UNION ALL
    SELECT 450 UNION ALL
    SELECT 452 UNION ALL
    SELECT 453 UNION ALL
    SELECT 454 UNION ALL
    SELECT 455 UNION ALL
    SELECT 456 UNION ALL
    SELECT 457 UNION ALL
    SELECT 458 UNION ALL
    SELECT 459
) AS expected
LEFT JOIN codtipoato c ON c.NUMEROTIPO = expected.numero AND c.TIPO_COD = 'N'
WHERE c.NUMEROTIPO IS NULL;

-- ==============================================
-- ESTRUTURA ESPERADA DA TABELA CODTIPOATO
-- ==============================================
/*
Campo           | Tipo         | Descrição
----------------|--------------|------------------
COD_COD         | INT          | Código interno
NUMEROTIPO      | INT          | Número do tipo de ato (401, 452, etc.)
CODSELO         | VARCHAR(10)  | Código do selo (1=TN1, 2=TN2, etc.)
TIPO_COD        | CHAR(1)      | Tipo: 'N' = Notas
DESCRICAO_COD   | VARCHAR(255) | Descrição do tipo de ato
*/

-- ==============================================
-- INSERT DE EXEMPLO (ajustar conforme estrutura)
-- ==============================================
-- INSERT INTO codtipoato (COD_COD, NUMEROTIPO, CODSELO, TIPO_COD, DESCRICAO_COD)
-- VALUES 
--     (459, 459, '2', 'N', 'ESCRITURA'),
--     (452, 452, '2', 'N', 'PROCURACAO'),
--     (454, 454, '1', 'N', 'CERTIDAO'),
--     (456, 456, '2', 'N', 'ATA NOTARIAL'),
--     (453, 453, '2', 'N', 'TESTAMENTO'),
--     (430, 430, '2', 'N', 'TRASLADO'),
--     (437, 437, '5', 'N', 'APOSTILAMENTO'),
--     (455, 455, '1', 'N', 'RECONHECIMENTO DE FIRMA'),
--     (401, 401, '1', 'N', 'AUTENTICACAO'),
--     (428, 428, '1', 'N', 'PUBLICA FORMA'),
--     (407, 407, '2', 'N', 'CARTA DE SENTENCA'),
--     (416, 416, '2', 'N', 'CONSTITUICAO CONDOMINIO'),
--     (418, 418, '2', 'N', 'BEM ADICIONAL'),
--     (421, 421, '1', 'N', 'PAGINA EXTRA ATA'),
--     (424, 424, '3', 'N', 'PARTE EXTRA PROCURACAO'),
--     (429, 429, '1', 'N', 'PAGINA EXTRA PUBLICA FORMA'),
--     (433, 433, '3', 'N', 'PAGINA EXTRA CERTIDAO'),
--     (436, 436, '3', 'N', 'BUSCAS'),
--     (443, 443, '2', 'N', 'VAGA GARAGEM'),
--     (445, 445, '2', 'N', 'SESSAO CONCILIACAO'),
--     (446, 446, '2', 'N', 'TEMPO ADICIONAL'),
--     (450, 450, '1', 'N', 'BEM ADICIONAL 10'),
--     (457, 457, '1', 'N', 'MATERIALIZACAO'),
--     (458, 458, '1', 'N', 'CERTIDAO NEGATIVA'),
--     (406, 406, '1', 'N', 'TRANSFERENCIA VEICULO'),
--     (412, 412, '2', 'N', 'INVENTARIO'),
--     (417, 417, '1', 'N', 'UNIDADE ADICIONAL');
