# README - Projeto NOTAS (SPTABEL)

Este documento descreve como testar e implantar localmente o projeto NOTAS (sptabel), onde encontrar os parâmetros de configuração e como usar os scripts de automação (`run_local_tests.ps1`, `run_curl.ps1`, `deploy.ps1`).

Resumo rápido

- Os parâmetros de execução (ambiente, contexto, caminho do certificado, senha e token) ficam na tabela `parametros` do banco (base padrão para NOTAS: `sptabel`).
- Scripts relevantes:
  - `deploy.ps1` — publica o WAR no Tomcat/Webrun (já existente).
  - `run_local_tests.ps1` — automação build → deploy → espera expansão do WAR (novo).
  - `run_curl.ps1` — executa um teste mTLS usando `curl` e salva resposta/trace/summary (novo).

Onde buscar os parâmetros

- Query rápida (exemplo):

```bash
mysql -u root -pk15720 sptabel -e "select AMBIENTE_PAR, DTVERSAO_FUNARPEN, CAMINHOCERTIFICADO_PAR, SENHACERTIFICADO_PAR, TOKENFUNARPEN_PAR FROM parametros LIMIT 1"
```

- Campos:
  - `AMBIENTE_PAR`: ambiente do deploy — `D` = Desenvolvimento, `P` = Produção.
  - `DTVERSAO_FUNARPEN`: data para sanitização e envio de selos da versão '112' selos.versao apenas essa versão deve ser processada na tabela selados
  - `CAMINHOCERTIFICADO_PAR`: caminho absoluto do arquivo PFX a ser usado para mTLS.
  - `SENHACERTIFICADO_PAR`: senha do PFX.
  - `TOKENFUNARPEN_PAR`: token empresarial usado no header `Authorization` e, quando aplicável, no campo `codigoEmpresa` do payload.

Diferenças entre ambientes (D vs P)

- Ambiente D (Desenvolvimento):
  - Contexto típico: `webrun`/`Maker Studio` local em Windows.
  - Exemplo de destino (padrão do `deploy.ps1` em dev para NOTAS):
    `C:\Program Files (x86)\Softwell Solutions\Maker Studio\Webrun Studio\tomcat\webapps\notas.war`
  - Certificado PFX geralmente em um caminho local (ex.: `C:\spd\funarpen\certificado.pfx`).

- Ambiente P (Produção):
  - Contexto típico: Tomcat Linux com contexto `funarpen`.
  - Exemplo de destino: `/usr/local/tomcat8/webapps/funarpen_notas.war` (conforme regras no projeto).
  - Em produção, verifique permissões, CA e política de mTLS (certificados válidos, acesso ao PFX no servidor).

Uso dos scripts

- `run_local_tests.ps1` (integração build→deploy→test)
  - Path: `C:\Desenvolvimento\Seprocom\Notas\run_local_tests.ps1`
  - Parâmetros importantes:
    - `-DBUser`, `-DBPass`, `-DBName`, `-DBHost`, `-DBPort` — se fornecidos, o script lerá a tabela `parametros` e ajustará `TOKEN`, `Cert` e `Senha` automaticamente.
    - `-SkipMavenBuild` — pula `mvn package` se já compilado.
  - Exemplo (com leitura de parâmetros do DB):
    ```powershell
    powershell -NoProfile -ExecutionPolicy Bypass -File .\run_local_tests.ps1 \
      -DBUser root -DBPass k15720 -DBName sptabel -DBHost localhost -RequestFile request.json
    ```

- `run_curl.ps1` (teste mTLS e coleta de logs)
  - Path: `C:\Desenvolvimento\Seprocom\Notas\run_curl.ps1`
  - Pode ler `parametros` do DB se passar `-DBUser`/`-DBPass`.
  - Parâmetros principais:
    - `-RequestFile` — arquivo JSON a enviar (padrão `request.json`).
    - `-Url` — URL do endpoint (se não informado, usa `https://dev-v11plus.funarpen.com.br/selos/recepcao`).
    - `-Cert`, `-Senha`, `-Token` — sobrescrevem valores lidos do DB.
  - Saídas geradas no diretório do script:
    - `resposta_notas_curl.txt` — corpo da resposta (se houver).
    - `resposta_notas_curl_trace.txt` — saída verbose do `curl` (stderr).
    - `run_curl_notas_summary.txt` — resumo com token parcialmente mascarado e status do curl.
  - Exemplo (com DB):
    ```powershell
    powershell -NoProfile -ExecutionPolicy Bypass -File .\run_curl.ps1 -DBUser root -DBPass k15720 -RequestFile request.json
    ```

Observações de segurança

- Nunca comite PFX ou tokens no repositório.
- Ao gerar relatórios de resumo, o script mascara parcialmente o token por padrão.
- Em produção, execute `run_curl.ps1` diretamente no servidor que possui o PFX e acesso à rede Funarpen (evite expor o PFX fora do host).

Troubleshooting rápido

- Erro `ERRO 24 - Token da empresa inválido`: verifique se o header `Authorization: Bearer <token>` corresponde ao valor esperado em `TOKENFUNARPEN_PAR` e, se necessário, alinhe o campo `codigoEmpresa` no `request.json` com esse token/identificador.
- Problemas TLS / conexão fechada: certifique-se que o PFX contém chave privada (HasPrivateKey = True) e que o `curl` no Windows está usando `--cert-type P12 --cert "path.pfx:senha"`.
- Se `run_local_tests.ps1` reportar que não encontrou o WAR expandido, aumente `WaitForExpandSeconds` ou verifique logs do Tomcat.

Checklist antes de testar em Produção

1. Validar `CONTEXTO_PAR` e `AMBIENTE_PAR` na tabela `parametros`.
2. Confirmar caminho do PFX e permissões no host de teste/produção.
3. Confirmar `TOKENFUNARPEN_PAR` e, se aplicável, atualize `request.json` para usar o mesmo `codigoEmpresa`.
4. Em produção, testar `curl` localmente no servidor de produção antes de automatizar (evita problemas de firewall/CA).
5. `DTVERSAO_FUNARPEN` Só processar registros que selados.DATAENVIO >= `parametros.DTVERSAO_FUNARPEN'

Se quiser, eu atualizo esse README com exemplos reais (com valores mascarados) ou adiciono um `README_QUICKSTART.md` com um passo-a-passo simplificado para novos engenheiros. Deseja que eu adicione isso?

## Exemplos mascarados

Use estes exemplos como referência — todos os valores sensíveis estão truncados/mascarados para segurança.

- Consulta à tabela `parametros` (exemplo de comando e saída mascarada):

```bash
mysql -u root -p****** sptabel -e "select AMBIENTE_PAR, DTVERSAO_FUNARPEN, CAMINHOCERTIFICADO_PAR, SENHACERTIFICADO_PAR, TOKENFUNARPEN_PAR FROM parametros LIMIT 1"
# saída (mascarada):
# D	 sptabel	 C:\spd\funarpen\certificado.pfx	 ******	 eyJhbGciOiJIUzUxMiJ9.G1Si...5g
```

- Executando `run_local_tests.ps1` (exemplo mascarado):

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\run_local_tests.ps1 \
  -DBUser root -DBPass "k****0" -DBName sptabel -DBHost localhost -RequestFile request.json
```

- Executando `run_curl.ps1` com token e PFX mascarados:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\run_curl.ps1 \
  -RequestFile request.json \
  AMBIENTE_PAR ='D'  -Url "https://dev-v11plus.funarpen.com.br/selos/recepcao" \
  AMBIENTE_PAR ='P'  -Url "https://v11plus.funarpen.com.br/selos/recepcao" \
  -Cert "C:\\spd\\funarpen\\certificado.pfx" -Senha "6*****75" \
  -Token "eyJhbGciOiJIUzUxMiJ9.eyJ...G1Si...5g"
```

- Arquivos gerados (padrão):
  - `resposta_notas_curl.txt` — corpo da resposta
  - `resposta_notas_curl_trace.txt` — trace verbose do curl
  - `run_curl_notas_summary.txt` — resumo com token mascarado

Mantenha valores reais (PFX, senha, token) fora do repositório e forneça-os apenas no host onde os testes serão executados.

# Projeto TTB (Notas) — Diretrizes Técnicas

Objetivo

- Criar novas funções para o Sistema de Gestão do Tabelionato de Notas (Maker 5 / Softwell).

Regras gerais

- Reaproveitar módulos, `pom.xml`, `node` e configuração do projeto `Protesto`.
- Estrutura de deploy: gerar WAR `notas.war` e disponibilizar em `webapps` do Tomcat.

Instalação / deploy

- `install_spr.ps1`: ajustar contexto/paths para este projeto:
  - O `WAR` deve ser instalado como `/webapps/notas.war` (atualize `$WarLocalPath` / `$RemoteTomcatHome` conforme necessário).
  - Exemplo: copie o WAR para `C:\Program Files (x86)\Apache Software Foundation\Tomcat 9.0\webapps\notas.war` e reinicie o Tomcat.

Acesso ao banco e criptografia

- `DbUtil.java` (mesma lógica do `Protesto`):
  - Ler `C:\ProgramData\spr\Notas\db.properties` e `C:\ProgramData\spr\master.key`.
  - Suportar valores em texto e `ENC(...)` (Jasypt). Se `ENC(...)` presente, tentar decifrar usando `master.key`.
  - Recomenda-se usar `ENC(...)` para `DB_PASS` em produção; o desenvolvedor pode gerar com o utilitário `Spr` (ex.: via Maven).
- Comando exemplo para gerar cipher (developer machine):

```
mvn org.codehaus.mojo:exec-maven-plugin:3.0.0:java -Dexec.mainClass=com.monitor.funarpen.util.spr.Spr -Dexec.args="encrypt MASTERKEY senha_plain"
```

- No servidor, garanta que o usuário do Tomcat tem permissão de leitura em:
  - `C:\ProgramData\spr\master.key`
  - `C:\ProgramData\spr\notas\db.properties`
  - Exemplo de `icacls` para aplicar permissões (executar como Administrador):

```
icacls "C:\ProgramData\spr\master.key" /inheritance:r
icacls "C:\ProgramData\spr\master.key" /grant:r "SYSTEM:F" "Administradores:F"
icacls "C:\ProgramData\spr\Notas\db.properties" /inheritance:r
icacls "C:\ProgramData\spr\Notas\db.properties" /grant:r "SYSTEM:F" "Administradores:F"
```

`web.xml` / `ParametrosServlet`

- Pode usar `@WebServlet` (anotações) ou mapeamento explícito no `WEB-INF/web.xml` do WAR.
- Se o Tomcat estiver configurado para não escanear anotações, adicione o mapeamento no `WEB-INF/web.xml` do WAR:

```xml
<servlet>
  <servlet-name>ParametrosServlet</servlet-name>
  <servlet-class>com.monitor.funarpen.web.servlets.ParametrosServlet</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>ParametrosServlet</servlet-name>
  <url-pattern>/maker/api/parametros</url-pattern>
</servlet-mapping>
```

- Alternativamente, o administrador pode inserir a configuração em `TOMCAT_HOME/conf/context.xml` ou `web.xml` global, mas preferir o `WEB-INF/web.xml` do aplicativo para isolamento.

Menu e contexto

- Reaproveitar a pasta `Menu` do `Protesto` e ajustar as siglas/contexto:
  - Trocar sigla do sistema para `TTB` onde aplicável.
  - Ajustar links e caminhos para usar o contexto `sptabel` (ex.: `http://host:PORT/sptabel/...`).

Integração de formulários (sprURLOpenFrame)

- Reutilizar a função `sprURLOpenFrame` para abrir formulários em abas/iframes integrados ao menu nativo do Maker.
- Garanta que o script/SQL que atualiza a função (`sql/update_sprURLOpenFrame_1350.sql`) seja aplicado no ambiente Maker para que os novos formulários sejam abertos corretamente.

Gerencial / Pro-Max

- Rotinas comuns ao projeto `Notas` e `Protesto` foram centralizadas em `C:\Desenvolvimento\Seprocom\Gerencial`.
- URLs oficiais de acesso:
  - NFS-e: `/gerencial/nfse/index.html`
  - Serviço de Assinatura: `/gerencial/servico-assinatura/index.html`
  - Selos Utilizados (SPA): `/gerencial/frontend/index.html#/selos`
  - Monitor de Selos (SPA): `/gerencial/frontend/index.html#/pedidos`
- Gráficos e sidebar devem carregar automaticamente de `/gerencial/html/assets/`.
- Backend Pro-Max: leitura de pastas, conversão inicial para PDF/A, multi-tenant via `sys` e seleção automática de certificado.
- Frontend Premium: dark mode, explorador de pastas, toggles de assinatura/PDF-A/junção e monitoramento por arquivo.

Dependências e build

- Manter os mesmos módulos e dependências do `Protesto` (copiar `pom.xml`, `package.json` e `node_modules`/build scripts se necessário).
- Ajustar `artifactId`/nome do WAR para `notas` no `pom.xml` (se desejar gerar `notas.war`).

Testes e verificação

1. Gerar WAR (mvn package) e garantir que `WEB-INF/web.xml` e `WEB-INF/classes/com/monitor/funarpen/web/servlets/ParametrosServlet.class` estejam presentes no WAR.
2. Copiar `notas.war` para `TOMCAT_HOME/webapps` e reiniciar Tomcat.
3. Verificar arquivos de configuração em `C:\ProgramData\spr\Notas` e permissões.
4. Testar endpoint de parâmetros localmente no servidor:

```
curl "http://localhost:PORT/sptabel/maker/api/parametros"
curl "http://localhost:PORT/sptabel/maker/api/parametros?debug=true&diag=1"
```

5. Conferir logs do Tomcat (`TOMCAT_HOME/logs`) se houver erros (ClassNotFound, SQLException, problemas de decrypt).

Observações finais

- Preferência por JNDI/DataSource em produção: considere adaptar `DbUtil` para primeiro tentar lookup JNDI (`java:comp/env/jdbc/spr`) e depois fallback para `C:\ProgramData\spr`.
- Documentar no repositório `Notas` quaisquer diferenças específicas de negócio (campos/colunas da tabela `parametros`) e manter sincronizado com `Protesto` quando compartilhar utilitários.

Próximos passos sugeridos

- Atualizar `install_spr.ps1` para usar `/webapps/notas.war` e gerar payload compatível.
- Validar `DbUtil` com `ENC(...)` e `master.key` em ambiente de teste.
- Atualizar `Menu` e aplicar `sprURLOpenFrame` na base Maker do cliente.

---

README gerado automaticamente; posso ajustar mais se quiser detalhes de comandos ou templates (ex.: `context.xml` para JNDI, exemplo de `db.properties` com `ENC(...)`, ou script de deploy específico).
