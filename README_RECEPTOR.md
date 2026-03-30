API Receptor - Selos

Objetivo:
- Documentar endpoints para recepção de selos e consulta de protocolos.

Arquivos gerados:
- api/selos_api.yaml: OpenAPI 3.1.0 para `/selos/recepcao`, `/selos/recepcao/lote`, `/protocolo`.

Próximos passos:
- Implementar endpoints no backend Java, com validação de certificado (mutual TLS).
- Testar end-to-end com cliente que envia certificado.
- Garantir que `codigoEmpresa` e `documentoResponsavel` sejam validados.
