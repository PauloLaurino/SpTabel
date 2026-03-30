# TODO - Integração do módulo compartilhado de Selos

Lista de tarefas para extrair e compartilhar funcionalidades comuns (selo eletrônico) entre os projetos `protesto` e `notas`.

- [ ] Inventariar funcionalidades comuns
- [ ] Definir API/contrato do módulo compartilhado
- [ ] Criar repositório/módulo compartilhado (`seprocom-selos`)
- [ ] Extrair código: validação JSON, modelos DTO, criptografia, cliente HTTP
- [ ] Publicar artefato ou configurar composite build (maven/nexus ou includeBuild)
- [ ] Atualizar `protesto` e `notas` para usar o módulo
- [ ] Remover duplicação e validar com testes/CI

Observações:
- Preferência: publicar artefato em repositório Maven interno (produção). Usar `includeBuild` durante desenvolvimento.
- Externalizar configurações sensíveis (keystore, URLs, credenciais) via `application.properties` ou variáveis de ambiente.
- Versionamento semântico (SemVer) para o módulo compartilhado.

Autor: Copilot (assistente automático)
Data: 20-02-2026
