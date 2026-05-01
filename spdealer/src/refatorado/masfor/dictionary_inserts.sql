-- Inserções de dicionário para o formulário masfor (homologação)
-- Ajuste conforme esquema real de `dictionary_tables` e `dictionary_columns` do projeto

-- Exemplo: tabela
INSERT INTO dictionary_tables (table_name, label, application) VALUES ('masfor', 'Tipo de Fornecedores', 'SPDEALER');

-- Exemplo: colunas (nome_da_coluna, label, tipo, tamanho, obrigatorio)
INSERT INTO dictionary_columns (table_name, column_name, label, data_type, length, required)
VALUES
('masfor','codigo','Código','VARCHAR',30,1),
('masfor','descricao','Descrição','VARCHAR',200,1),
('masfor','ativo','Ativo','CHAR',1,1),
('masfor','id_fil','Filial','INT',11,1);
