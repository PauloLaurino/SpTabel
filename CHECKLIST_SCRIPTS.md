# Checklist - Scripts de Sanitização

## Data de Verificação
28/03/2026

## Scripts PowerShell

- [x] sanitizar_selados_completo.ps1
- [x] sanitizar_lote_selados.ps1
- [x] sanitizar_e_enviar.ps1
- [x] sanitizar_lote_e_enviar.ps1
- [x] verificar_status.ps1
- [x] executar_sanitizacao_completa.ps1

## Arquivos SQL

- [x] consultar_selos_pendentes.sql
- [x] verificar_status_selos.sql

## Arquivos de Documentação

- [x] README_SANITIZACAO.md
- [x] README_SCRIPTS_SANITIZACAO.md
- [x] RESUMO_SCRIPTS_CRIADOS.md
- [x] selados_exemplo_sanitizado.JSON12

## Regras de Sanitização

- [x] Conexão ao banco de dados (IP: 100.102.13.23, porta: 3306, database: sptabel)
- [x] Chave de recibo (extrair num_rec do IDAP posição 11 com 10 caracteres)
- [x] Sanitização do campo documentoResponsavel (substituir "0" por DOC_PAR)
- [x] Mapeamento das propriedades do ato (tipo 455)
- [x] Mapeamento dos signatários (array)
- [x] Estrutura de verbas (manter valores originais)

## Funcionalidades

- [x] Consulta ao banco de dados
- [x] Extração de solicitante de fin_reccab
- [x] Extração de signatários de ListaPropriedades
- [x] Construção de JSON sanitizado
- [x] Geração de arquivo selados.JSON12
- [x] Envio para o FUNARPEN via mTLS
- [x] Atualização de status no banco de dados
- [x] Verificação de status dos selos

## Próximos Passos

- [ ] Executar scripts para processar selos pendentes
- [ ] Verificar se os arquivos JSON12 foram gerados corretamente
- [ ] Enviar selos para o FUNARPEN
- [ ] Monitorar status de recepção
- [ ] Ajustar scripts conforme necessário

## Referências

- [`README_MANUAL.md`](README_MANUAL.md) - Manual técnico do JSON
- [`README_NOTAS.md`](README_NOTAS.md) - Documentação do projeto NOTAS
- [`SeloJsonSanitizerNotas.java`](src/main/java/com/selador/util/SeloJsonSanitizerNotas.java) - Classe Java de sanitização
- [`README_SANITIZACAO.md`](README_SANITIZACAO.md) - Documentação de sanitização
- [`README_SCRIPTS_SANITIZACAO.md`](README_SCRIPTS_SANITIZACAO.md) - Documentação dos scripts
- [`RESUMO_SCRIPTS_CRIADOS.md`](RESUMO_SCRIPTS_CRIADOS.md) - Resumo dos scripts criados
