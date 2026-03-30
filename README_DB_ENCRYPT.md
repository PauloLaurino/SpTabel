Gerar `ENC(...)` para `db.properties` (Guia rápido)

1) Requisitos
- Maven instalado no ambiente do desenvolvedor OU projeto construído para executar a classe `com.monitor.funarpen.util.spr.Spr`.
- `master.key` que será usado em produção (escolha/forneça um valor seguro por cliente).

2) Usando Maven (recomendado no dev)
Execute no diretório do projeto (onde o `pom.xml` está):

```powershell
mvn org.codehaus.mojo:exec-maven-plugin:3.0.0:java -Dexec.mainClass=com.monitor.funarpen.util.spr.Spr -Dexec.args="encrypt MASTERKEY senha_plain"
```
Substitua `MASTERKEY` pela chave (p.ex. `abc123...`) e `senha_plain` pela senha do banco. A saída terá o cipher — copie o valor retornado e coloque em `db.properties` como:

```
DB_PASS=ENC(<CIPHER_AQUI>)
```

3) Sem Maven (opcional)
Se você tiver o projeto compilado e todas as dependências em `target/dependency`, pode executar com `java -cp` apontando para os jars; exemplo genérico:

```powershell
java -cp "target/classes;target/lib/*" com.monitor.funarpen.util.spr.Spr encrypt MASTERKEY senha_plain
```

4) Transferir para o servidor
- No servidor, crie `C:\ProgramData\spr\Notas\master.key` com o mesmo `MASTERKEY` usado para gerar o cipher.
- Coloque `db.properties` em `C:\ProgramData\spr\Notas\db.properties` com `DB_PASS=ENC(...)`.
- Ajuste permissões (ex.: `icacls`) para que o usuário do Tomcat leia ambos os arquivos.

5) Testes
- Reinicie Tomcat e acesse o endpoint debug (no servidor):

```powershell
curl "http://localhost:PORT/CONTEXT/maker/api/parametros?debug=true&diag=1"
```
- Verifique `configExists`, `masterExists` e `decryptAttempt` no JSON retornado.

Se quiser, eu posso gerar um pequeno script PowerShell que chama Maven para você (no dev) e faz o patch automático de `db.properties.example` com o `ENC(...)` resultante.