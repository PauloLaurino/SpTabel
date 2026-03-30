# Funarpen Transitário - Documentação de Conclusões

## Objetivo
Documentar as conclusões da análise do fluxo de envio de selos para a API Funarpen.

---

## 1. Arquitetura Atual

### Servidores

| Servidor | IP | Porta | Banco |
|----------|-----|-------|-------|
| Notas | 100.75.153.127 | 8059 | MariaDB (localhost) |

### URL da API Funarpen
- **Endpoint**: `http://100.75.153.127:8059/funarpen/maker/api/funarpen/selos/recepcao`
- **API Externa**: `https://v11plus.funarpen.com.br` (produção)

---

## 2. Fluxo de Envio de Selos

### Processo Atual (Python)

```
Script Python (local)
    ↓
1. Lê JSON do banco 100.75.153.127 (tabela selados, campo JSON)
2. Sanitiza o JSON (lógica em Python)
3. Envia JSON PRONTO para → http://100.75.153.127:8059/funarpen/...
    ↓
Servidor (funarpen.war - versão 12/03)
    ↓
Repassa JSON para API Externa Funarpen
    ↓
API Funarpen retorna protocolo
```

### Ponto Importante
- O **funarpen.war** no servidor apenas **repassa** o JSON pronto para a API externa
- Não faz sanitização - o script local que faz isso

---

## 3. Projeto Notas (C:\Desenvolvimento\Seprocom\Notas)

### Estado Atual
- **Compilação**: ✅ BUILD SUCCESS
- **WAR Gerado**: `target/notas.war`
- **Deploy**: ❌ NÃO deployado no servidor

### Arquivos Modificados
- `SeloJsonSanitizerNotas.java` - melhorias para sanitização completamente o JSON
- `DbUtil.java` - caminhos Linux
- `FunarpenParametrosServlet.java` - consulta tabela parametros
- `database.properties` - configurações de banco

### Problema
O projeto Notas **não está implantado** no servidor. As correções feitas ainda não foram usadas em produção.

---

## 4. Como os Selos Foram Enviados


### Lições Aprendidas
- Sempre entender o fluxo completo antes de liberar para produção
- Não assumir que o Java está funcionando apenas porque compilou
- Verificar se o WAR está realmente implantado

---

## 5. Selos Pendentes

### Encontrados sempre com selados.DATAENVIO > parametros.DTVERSAO_FUNARPEN
- selados.STATUS != 'SUCESSO'
- Todos do tipo 402,403 e 404 

### Próximos Passos
1. Validar que todos foram processados com sucesso
2. Só depois implementar as melhorias em SeloJsonSanitizerNotas.java

---

## 6. Tipos de Ato (TIPO 455)

| Código | Descrição |
|--------|-----------|
| 402 | Sem Valor Declarado |
| 403 | Com Valor Declarado |
| 404 | Sinal Público |

---

## 7. Configurações de Banco

### Tabela: parametros
- `AMBIENTE_PAR` - Ambiente P (prod) D (Desenvolvimento)  (ambiente)
- `CODTABEL_PAR` - (codigoOficio)
- `DOC_PAR` - Documento do responsável (documentoResponsavel)
- `TOKENFUNARPEN_PAR` - Token de autenticação (codigoEmpresa)
- `CAMINHOCERTIFICADO_PAR` - Caminho do certificado
- `DTVERSAO_FUNARPEN` - Processar selos só apartir da data registada
   apenas DATAENVIO > DTVERSAO_PARAMETRO
  
  Exemplo 
  {
  "ambiente": "prod",
  "documentoResponsavel": "09851329657",
  "codigoEmpresa": "eyJhbGciOiJIUzUxMiJ9.eyJkb2N1bWVudG8iOiIxMDc1MjI0MTAwMDE0MCJ9.G1SilnGGFWQVPIfFBPS9-oQcYz6aXu3ndnjtvza2cvOcwWBuuY5pLIABrTwWnC3PH4hlOqiqbxX9O7Cl35g2vg",
  "codigoOficio": 181122,   
  "selo": { ...
  
### Tabela: selados
- `SELO` - Código do selo
- `JSON` - Dados do ato
- `STATUS` - Status do envio
- `RETORNO` - Protocolo retornado
- `NUMTIPATO` - Tipo do ato

---

## 8. URLs de Referência

### Endpoints do Servidor
- Recepção: `/maker/api/funarpen/selos/recepcao`
- Reenviar: `/maker/api/funarpen/selos/reenviar-lote`
- Sanitizar/Reenviar: `/maker/api/funarpen/selos/sanitizar-reenviar`
- Consultar Protocolo: `/maker/api/funarpen/selos/protocolo`

---

## 9. Pendências

- [ ] Validar todos os selos do período
- [ ] Implementar melhorias em SeloJsonSanitizerNotas.java
- [ ] Testar e validar fluxo completo

---

*Documento criado em: 2026-03-19*
