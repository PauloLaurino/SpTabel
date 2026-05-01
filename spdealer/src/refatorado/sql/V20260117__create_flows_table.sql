-- V20260117__create_flows_table.sql
-- Cria tabela `flows` para persistir flows criados/edits via FlowEditor
-- Ajuste conforme o dialeto do banco (MariaDB/MySQL)

CREATE TABLE IF NOT EXISTS flows (
  id VARCHAR(128) NOT NULL PRIMARY KEY,
  name VARCHAR(255) DEFAULT NULL,
  description TEXT,
  params JSON DEFAULT NULL,
  visual_config JSON DEFAULT NULL,
  data_config JSON DEFAULT NULL,
  json LONGTEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
