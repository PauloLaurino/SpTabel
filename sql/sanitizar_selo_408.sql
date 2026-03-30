-- ============================================================
-- Script SQL para sanitizar selos da versão 112
-- Objetivo: Transformar o JSON original para o formato FUNARPEN
-- ============================================================

-- Passo 1: Copiar JSON original para JSON12 (já feito para o selo 408)
-- UPDATE selados SET JSON12 = JSON WHERE SELO = 'SFTN2.PJRKN.Rjauu-LrfDM.1122q';

-- Verificar o estado atual
SELECT 
    s.selo_sel,
    s.dataselo_sel,
    sel.NUMTIPATO,
    LENGTH(sel.JSON) as tamanho_json,
    LENGTH(sel.JSON12) as tamanho_json12,
    JSON_VALID(sel.JSON12) as json12_valido
FROM selos s
INNER JOIN selados sel ON sel.SELO = s.selo_sel
WHERE s.selo_sel = 'SFTN2.PJRKN.Rjauu-LrfDM.1122q';

-- ============================================================
-- O JSON atual tem a seguinte estrutura (formato antigo):
-- {
--   "numeroTipoAto": 408,
--   "seloDigital": "SFTN2.PJRKN.Rjauu-LrfDM.1122q",
--   "ListaVerbas": [
--     {"NomeVerba": "EMOLUMENTOS", "ValorVerba": 559290},
--     ...
--   ],
--   "ListaPropriedades": [
--     {"NomePropriedade": "envolvidos.nome_razao", "ValorPropriedade": "..."},
--     ...
--   ]
-- }
--
-- O formato FUNARPEN espera:
-- {
--   "ambiente": "prod",
--   "documentoResponsavel": "10847240908",
--   "codigoEmpresa": "<TOKEN>",
--   "codigoOficio": 181196,
--   "selo": {
--     "seloDigital": "...",
--     "codigoTipoAto": 408,
--     "verbas": {
--       "emolumentos": 5592.90,
--       ...
--     },
--     "propriedades": {
--       "envolvidos": [...],
--       "detalhes_selo": {...}
--     }
--   }
-- }
-- ============================================================

-- Como o MySQL/MariaDB não tem funções completas para transformar JSON complexas,
-- isso precisa ser feito via aplicação Java ou Stored Procedure.

-- Por enquanto, o JSON12 está copiado do JSON original e é válido (JSON_VALID = 1).
-- A transformação completa precisa ser implementada no sanitizador Java.

-- Verificar quantos selos precisam de sanitização na versão 112
SELECT 
    COUNT(*) as total,
    SUM(CASE WHEN JSON_VALID(JSON12) = 1 THEN 1 ELSE 0 END) as json12_validos,
    SUM(CASE WHEN JSON_VALID(JSON12) != 1 OR JSON_VALID(JSON12) IS NULL THEN 1 ELSE 0 END) as json12_invalidos
FROM selos s
INNER JOIN selados sel ON sel.SELO = s.selo_sel
WHERE s.dataselo_sel >= '2026-02-26';
