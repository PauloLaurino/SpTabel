-- ===============================================================
-- Script para adicionar parâmetros de impostos na tabela parametros
-- REFORMA TRIBUTÁRIA (EC 132/2023)
-- COMPATÍVEL COM MariaDB 10.8+
-- 
-- Autor: Seprocom
-- Data: 2026-04-01
-- ===============================================================

-- ===============================================================
-- CBS (Contribuição sobre Bens e Serviços) - Federal -取代PIS/COFINS
-- ===============================================================
ALTER TABLE parametros ADD COLUMN CBS_PAR decimal(5,4) default 0.10;
ALTER TABLE parametros ADD COLUMN CBS_ATIVO_PAR boolean default false;
ALTER TABLE parametros ADD COLUMN CBS_DEDUZIR_PAR decimal(15,2) default 0;

-- ===============================================================
-- IBS (Imposto sobre Bens e Serviços) - Estadual/Municipal -取代ISS
-- ===============================================================
ALTER TABLE parametros ADD COLUMN IBS_PAR decimal(5,4) default 0.10;
ALTER TABLE parametros ADD COLUMN IBS_ATIVO_PAR boolean default false;
ALTER TABLE parametros ADD COLUMN IBS_DEDUZIR_PAR decimal(15,2) default 0;

-- ===============================================================
-- PIS (Programa de Integração Social) - Federal
-- ===============================================================
ALTER TABLE parametros ADD COLUMN PIS_PAR decimal(5,4) default 0.0165;

-- ===============================================================
-- COFINS (Contribuição para Financiamento da Seguridade Social) - Federal
-- ===============================================================
ALTER TABLE parametros ADD COLUMN COFINS_PAR decimal(5,4) default 0.076;

-- ===============================================================
-- CSLL (Contribuição Social sobre Lucro Líquido) - Federal
-- ===============================================================
ALTER TABLE parametros ADD COLUMN CSLL_PAR decimal(5,4) default 0.09;

-- ===============================================================
-- IRPJ (Imposto de Renda de Pessoa Jurídica) - Federal
-- ===============================================================
ALTER TABLE parametros ADD COLUMN IRPJ_PAR decimal(5,4) default 0.15;
ALTER TABLE parametros ADD COLUMN IRPJ_ADIC_PAR decimal(5,4) default 0.025;

-- ===============================================================
-- CPP (Contribuição Patronal) - Federal - Nova após Reforma
-- ===============================================================
ALTER TABLE parametros ADD COLUMN CPP_PAR decimal(5,4) default 0.12;
ALTER TABLE parametros ADD COLUMN CPP_ATIVO_PAR boolean default false;

-- ===============================================================
-- Controle de fase da Reforma Tributária
-- ===============================================================
ALTER TABLE parametros ADD COLUMN FASE_REFORMA_PAR varchar(20) default 'NAO_INICIADA';

-- ===============================================================
-- Verificar colunas adicionadas
-- ===============================================================

-- SELECT column_name, data_type, column_default 
-- FROM information_schema.columns 
-- WHERE table_schema = database() 
-- AND table_name = 'parametros' 
-- AND column_name LIKE '%_PAR'
-- ORDER BY ordinal_position;

-- ===============================================================
-- Atualizar dados de exemplo (opcional)
-- ===============================================================

-- UPDATE parametros SET 
--   CBS_PAR = 0.10,
--   IBS_PAR = 0.10,
--   PIS_PAR = 0.0165,
--   COFINS_PAR = 0.076,
--   CSLL_PAR = 0.09,
--   IRPJ_PAR = 0.15,
--   IRPJ_ADIC_PAR = 0.025,
--   CPP_PAR = 0.12,
--   FASE_REFORMA_PAR = 'TRANSICAO',
--   CBS_ATIVO_PAR = true,
--   IBS_ATIVO_PAR = true,
--   CPP_ATIVO_PAR = false
-- WHERE CODIGO_PAR = 1;