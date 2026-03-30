-- =================================================================
-- Script para substituir placeholder |dataato| nos registros versão 112
-- Data: 2026-03-15
-- Objetivo: Substituir |dataato| pela data do ato (dataAtoPraticado)
-- =================================================================

-- Verificar exemplo de JSON para entender a estrutura
SELECT 
    sel.ID,
    sel.SELO,
    SUBSTRING(sel.JSON, LOCATE('dataAtoPraticado', sel.JSON), 50) as dataato_exemplo
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112' 
  AND sel.JSON LIKE '%|dataato|%'
LIMIT 3;

-- =================================================================
-- Estratégia: Usar JSON_EXTRACT para extrair a data do JSON
-- O formato da data no JSON é: "dataAtoPraticado":"2026-02-27T14:30:00"
-- Precisamos converter para DD/MM/YYYY
-- =================================================================

-- Verificar o formato atual da data extraída
SELECT 
    sel.ID,
    sel.SELO,
    JSON_UNQUOTE(JSON_EXTRACT(sel.JSON, '$.dataAtoPraticado')) as dataato_extraida
FROM selados sel
INNER JOIN selos s ON sel.SELO = s.selo_sel
WHERE s.versao = '112' 
  AND sel.JSON LIKE '%|dataato|%'
LIMIT 5;

-- =================================================================
-- Criar procedure para atualizar os registros
-- =================================================================
DROP PROCEDURE IF EXISTS atualiza_dataato_ver112;

DELIMITER //

CREATE PROCEDURE atualiza_dataato_ver112()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_id INT;
    DECLARE v_json TEXT;
    DECLARE v_dataato TEXT;
    DECLARE v_dataato_formatada VARCHAR(10);
    DECLARE cur CURSOR FOR 
        SELECT sel.ID, sel.JSON
        FROM selados sel
        INNER JOIN selos s ON sel.SELO = s.selo_sel
        WHERE s.versao = '112' AND sel.JSON LIKE '%|dataato|%';
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO v_id, v_json;
        IF done THEN
            LEAVE read_loop;
        END IF;

        -- Extrair dataAtoPraticado do JSON
        SET v_dataato = JSON_UNQUOTE(JSON_EXTRACT(v_json, '$.dataAtoPraticado'));
        
        -- Se dataAtoPraticado não existir, tentar dataAto
        IF v_dataato IS NULL OR v_dataato = 'null' THEN
            SET v_dataato = JSON_UNQUOTE(JSON_EXTRACT(v_json, '$.dataAto'));
        END IF;

        -- Se encontrou data, converter para DD/MM/YYYY
        IF v_dataato IS NOT NULL AND v_dataato != 'null' AND v_dataato != '' THEN
            -- O formato é YYYY-MM-DDTHH:MM:SS, extrair apenas a data
            SET v_dataato_formatada = SUBSTRING(v_dataato, 9, 2);
            SET v_dataato_formatada = CONCAT(v_dataato_formatada, '/');
            SET v_dataato_formatada = CONCAT(v_dataato_formatada, SUBSTRING(v_dataato, 6, 2));
            SET v_dataato_formatada = CONCAT(v_dataato_formatada, '/');
            SET v_dataato_formatada = CONCAT(v_dataato_formatada, SUBSTRING(v_dataato, 1, 4));
            
            -- Atualizar o registro
            UPDATE selados 
            SET JSON = REPLACE(JSON, '|dataato|', v_dataato_formatada)
            WHERE ID = v_id;
        END IF;
    END LOOP;
    CLOSE cur;
    
    SELECT 'Registros atualizados com sucesso' as resultado;
END //

DELIMITER ;

-- Executar a procedure
CALL atualiza_dataato_ver112();

-- Verificar resultado
SELECT 
    s.versao,
    COUNT(*) as total,
    SUM(CASE WHEN sel.JSON LIKE '%|dataato|%' THEN 1 ELSE 0 END) as com_dataato_placeholder
FROM selados sel 
INNER JOIN selos s ON sel.SELO = s.selo_sel 
WHERE s.versao = '112'
GROUP BY s.versao;

-- Limpar procedure
DROP PROCEDURE IF EXISTS atualiza_dataato_ver112;
