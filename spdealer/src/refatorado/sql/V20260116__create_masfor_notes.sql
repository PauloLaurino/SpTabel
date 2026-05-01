-- Migration script to create masfor related documentation (do not run in prod without review)
-- V20260116__create_masfor_notes.sql

-- Table `masfor` is expected to already exist. This script documents expected columns and example insert.
-- Use in homologation only. Follow project rules: include id_fil and vlrsal_* where applicable.

/* Example structure (if table does not exist):
CREATE TABLE IF NOT EXISTS masfor (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  codigo VARCHAR(50) NOT NULL,
  descricao VARCHAR(200) NOT NULL,
  ativo TINYINT(1) DEFAULT 1,
  id_fil INT NOT NULL,
  criado_em DATETIME,
  atualizado_em DATETIME
);
*/

-- Example insert for homologation user
INSERT INTO masfor (codigo, descricao, ativo, id_fil, criado_em)
VALUES ('TIPO1', 'Tipo de Fornecedor Exemplo', 1, 1, NOW());
