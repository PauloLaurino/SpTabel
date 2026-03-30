-- ==============================================
-- VERIFICAÇÃO DA TABELA FUNARPEN - V11.12 Plus
-- Tabelionato de Notas
-- ==============================================

-- 1. Verificar se a tabela existe
SHOW TABLES LIKE 'funarpen';

-- 2. Verificar estrutura da tabela funarpen
DESCRIBE funarpen;

-- 3. Listar todos os tipos de ato cadastrados
SELECT 
    CODIGO_FUN AS codigo,
    DESCRICAO_FUN AS descricao,
    VALOR_FUN AS valor,
    TIPO_FUN AS tipo
FROM funarpen
ORDER BY CODIGO_FUN;

-- 4. Verificar tipos de ato do Tabelionato de Notas (N)
SELECT 
    CODIGO_FUN AS codigo,
    DESCRICAO_FUN AS descricao,
    VALOR_FUN AS valor
FROM funarpen
WHERE TIPO_FUN = 'N'
ORDER BY CODIGO_FUN;

-- 5. Verificar se existe o tipo de ato específico (ex: 459 - Escritura)
SELECT * FROM funarpen 
WHERE CODIGO_FUN = 459 AND TIPO_FUN = 'N';

-- ==============================================
-- TIPOS DE ATO ESPERADOS PARA V11.12 PLUS
-- Tabelionato de Notas
-- ==============================================
/*
Código | Descrição                           | Tipo Selo
-------|-------------------------------------|----------
401    | Autenticação                        | TN1
406    | Transferência de Veículo            | TN1
407    | Carta de Sentença                   | TN2
412    | Inventário                          | TN2
416    | Constituição de Condomínio          | TN2
417    | Unidade Adicional                   | TN1
418    | Bem Adicional (até 9ª)              | TN2
421    | Página Extra - Ata                  | TN1
424    | Parte Extra - Procuração            | TN3
428    | Pública Forma                       | TN1
429    | Página Extra - Pública Forma       | TN1
430    | Traslado                            | TN2
433    | Página Extra - Certidão             | TN3
436    | Buscas (10 anos)                    | TN3
437    | Apostilamento                       | TN5
443    | Vaga de Garagem                    | TN2
445    | Sessão de Conciliação               | TN2
446    | Tempo Adicional                     | TN2
450    | Bem Adicional (10ª+)               | TN1
452    | Procuração                          | TN2
453    | Testament o                         | TN2
454    | Certidão                            | TN1
455    | Reconhecimento de Firma            | TN1
456    | Ata Notarial                       | TN2
457    | Materialização                      | TN1
458    | Certidão Negativa                   | TN1
459    | Escritura                           | TN2
*/

-- ==============================================
-- VERIFICAR TIPOS FALTANDO
-- ==============================================
SELECT '401' AS codigo_expected
UNION ALL SELECT '406'
UNION ALL SELECT '407'
UNION ALL SELECT '412'
UNION ALL SELECT '416'
UNION ALL SELECT '417'
UNION ALL SELECT '418'
UNION ALL SELECT '421'
UNION ALL SELECT '424'
UNION ALL SELECT '428'
UNION ALL SELECT '429'
UNION ALL SELECT '430'
UNION ALL SELECT '433'
UNION ALL SELECT '436'
UNION ALL SELECT '437'
UNION ALL SELECT '443'
UNION ALL SELECT '445'
UNION ALL SELECT '446'
UNION ALL SELECT '450'
UNION ALL SELECT '452'
UNION ALL SELECT '453'
UNION ALL SELECT '454'
UNION ALL SELECT '455'
UNION ALL SELECT '456'
UNION ALL SELECT '457'
UNION ALL SELECT '458'
UNION ALL SELECT '459';

-- Query para verificar quais códigos estão faltando
SELECT expected.codigo_expected AS codigo
FROM (
    SELECT '401' AS codigo_expected
    UNION ALL SELECT '406'
    UNION ALL SELECT '407'
    UNION ALL SELECT '412'
    UNION ALL SELECT '416'
    UNION ALL SELECT '417'
    UNION ALL SELECT '418'
    UNION ALL SELECT '421'
    UNION ALL SELECT '424'
    UNION ALL SELECT '428'
    UNION ALL SELECT '429'
    UNION ALL SELECT '430'
    UNION ALL SELECT '433'
    UNION ALL SELECT '436'
    UNION ALL SELECT '437'
    UNION ALL SELECT '443'
    UNION ALL SELECT '445'
    UNION ALL SELECT '446'
    UNION ALL SELECT '450'
    UNION ALL SELECT '452'
    UNION ALL SELECT '453'
    UNION ALL SELECT '454'
    UNION ALL SELECT '455'
    UNION ALL SELECT '456'
    UNION ALL SELECT '457'
    UNION ALL SELECT '458'
    UNION ALL SELECT '459'
) AS expected
LEFT JOIN funarpen f ON f.CODIGO_FUN = expected.codigo_expected AND f.TIPO_FUN = 'N'
WHERE f.CODIGO_FUN IS NULL;

-- ==============================================
-- INSERIR TIPOS FALTANDO (se necessário)
-- ==============================================
-- Exemplo de INSERT (ajustar conforme estrutura da tabela):
-- INSERT INTO funarpen (CODIGO_FUN, DESCRICAO_FUN, VALOR_FUN, TIPO_FUN)
-- VALUES 
--     (459, 'ESCRITURA', 8.00, 'N'),
--     (452, 'PROCURACAO', 8.00, 'N'),
--     (454, 'CERTIDAO', 1.00, 'N'),
--     ...;
