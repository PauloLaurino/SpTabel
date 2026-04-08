# Funarpen Transitário - Documentação de Conclusões

## Objetivo
Documentar as conclusões da análise do fluxo de envio de selos para a API Funarpen.

---

## 1. Arquitetura Atual

### 1. Conexão ao Banco de Dados
- IP: 100.102.13.23
- Porta: 3306
- Database: sptabel
- Usuário: root
- Senha: k15720

### URL da API Funarpen
- **Endpoint**: `http://100.102.13.23:8049/funarpen/maker/api/funarpen/selos/recepcao`
- **API Externa**: `https://v11plus.funarpen.com.br` (produção)

---

## 2. Fluxo de Envio de Selos

### Processo Atual (Python)

```
Script Python (local)
    ↓
1. Lê JSON do banco 100.102.13.23 (tabela selados, campo JSON)
2. Sanitiza o JSON (lógica em Python)
3. Envia JSON PRONTO para → http://100.102.13.23:8049/funarpen/...
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


---

## 3. Como os Selos Foram Enviados


### Lições Aprendidas
- Sempre entender o fluxo completo antes de liberar para produção
- Não assumir que o Java está funcionando apenas porque compilou
- Verificar se o WAR está realmente implantado

---

## 4. Selos Pendentes

### Encontrados sempre com selados.DATAENVIO > parametros.DTVERSAO_FUNARPEN
- selados.STATUS != 'SUCESSO'
- Todos do tipo 402,403 e 404 

### Próximos Passos
1. Validar que todos foram processados com sucesso
2. Só depois implementar as melhorias em SeloJsonSanitizerNotas.java

---

## 5. Tipos de Ato (TIPO 455)

### 2. Chave de Recibo
- `fin_reccab.num_rec` é derivado do campo `selados.IDAP`
- Extrair posição 11 com 10 caracteres
- Exemplo: se `"idap": "0000000000000045305R00000000000000000000"`, então `fin_reccab.num_rec = "0000045305"`

### 3. Sanitização do Campo `documentoResponsavel`
- Substituir o valor `"0"` no JSON original pelo valor obtido do campo `parametros.DOC_PAR`

### 4. Mapeamento das Propriedades do Ato (Tipo 455)
- `solicitanteAto.nomeRazao`: Preencher com `fin_reccab.nomecli_rec`
- `solicitanteAto.documentoTipo`: Mapear para o tipo de documento válido (1-12). Se não houver informação, usar `12 - Não Informado`
- `solicitanteAto.documentoNumero`: Preencher com `fin_reccab.cpfcli_rec`
- `solicitanteAto.endereco`: Definir como `null`
- `autenticacao.totalPaginas`: Definir como `null`
- `certidao.consulta.tipo`: Determinado pelo `codigoTipoAto`:
  - 403 -> `1 (Com Valor Declarado)`
  - 402 -> `2 (Sem Valor Declarado)`
  - 404 -> `3 (Sinal Público)`
- `reconhecimento.especie`: Definir como `1 (Por Verdadeira ou Autêntica)`
- `reconhecimento.quantidadePaginasAto`: Definir como `null`
- `reconhecimento.quantidadePartesEnvolvidasAto`: Contar o número de objetos no array de `signatarios`
- `reconhecimento.data`: Usar o valor de `dataAtoPraticado`
- `reconhecimento.descricao`: Definir como `null`

### 5. Mapeamento dos Signatários (Array)
Para cada objeto no array `signatarios`:
- `signatarios.nomeRazao`: Extrair de `envolvidos.nome_razao` nas propriedades do selo original
- `signatarios.documentoTipo`: Determinado pelo tipo de documento em `envolvidos.doc_pessoa` (ex: CPF=1, CNPJ=2). Se vazio, inferir pelo formato do número ou usar `12`
- `signatarios.documentoNumero`: Extrair de `envolvidos.CPF_CNPJ` nas propriedades do selo original

### 5. Estrutura de Verbas
Manter a estrutura e valores originais do objeto `verbas`:
- emolumentos
- vrcExt
- funrejus
- iss
- fundep
- funarpen
- distribuidor
- valorAdicional

## Tipos de Documento

| Código | Descrição |
|--------|-----------|
| 1 | CPF |
| 2 | CNPJ |
| 3 | CNH |
| 4 | RG |
| 5 | Passaporte |
| 6 | CTPS |
| 7 | Título de Eleitor |
| 8 | Certificado de Reserva |
| 9 | Documento Estrangeiro |
| 10 | Documento de Identificação Profissional |
| 11 | Outro |
| 12 | Não Informado |

## Estrutura JSON Sanitizada Esperada

```json
{
  "ambiente": "prod",
  "documentoResponsavel": "09851329657",
  "codigoEmpresa": "eyJhbGciOiJIUzUxMiJ9...",
  "codigoOficio": 181122,
  "selo": {
    "seloDigital": "SFTN1.cG7Rb.Mwfwe-KRsZM.1122q",
    "codigoPedido": 944874,
    "tipoGratuidade": 0,
    "codigoTipoAto": 455,
    "tipoEmissaoAto": 1,
    "idap": "0000000000000045305R00000000000000000000",
    "versao": 11.12,
    "dataSeloEmitido": "04/03/2026",
    "dataAtoPraticado": "04/03/2026",
    "seloRetificado": ""
  },
  "solicitanteAto": {
    "nomeRazao": "GILSON DA SILVEIRA PRINS",
    "documentoTipo": 1,
    "documentoNumero": "89751817900",
    "endereco": null
  },
  "autenticacao": {
    "totalPaginas": null
  },
  "certidao": {
    "consulta": {
      "tipo": 1
    }
  },
  "reconhecimento": {
    "especie": 1,
    "quantidadePaginasAto": null,
    "quantidadePartesEnvolvidasAto": 1,
    "data": "04/03/2026",
    "descricao": null
  },
  "signatarios": [
    {
      "nomeRazao": "MARIA DE LURDES BRONOSKI OCHINSKI",
      "documentoTipo": 1,
      "documentoNumero": "05496182956"
    }
  ],
  "verbas": {
    "emolumentos": 24.14,
    "vrcExt": 0,
    "funrejus": 3.01,
    "iss": 0.36,
    "fundep": 0.60,
    "funarpen": 1.00,
    "distribuidor": 0,
    "valorAdicional": 0
  }
}
```


## 6. Configurações de Banco

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
  "seloRetificado":null,
  "selo": { ...
  
### Tabela: selados
- `SELO` - Código do selo
- `JSON` - Dados do ato
- `STATUS` - Status do envio
- `RETORNO` - Protocolo retornado
- `NUMTIPATO` - Tipo do ato

---

## 7. URLs de Referência

### Endpoints do Servidor
- Recepção: `/maker/api/funarpen/selos/recepcao`
- Reenviar: `/maker/api/funarpen/selos/reenviar-lote`
- Sanitizar/Reenviar: `/maker/api/funarpen/selos/sanitizar-reenviar`
- Consultar Protocolo: `/maker/api/funarpen/selos/protocolo`

---

## 8. Pendências

- [ ] Validar todos os selos do período
- [ ] Implementar melhorias em SeloJsonSanitizerNotas.java
- [ ] Testar e validar fluxo completo

---

*Documento criado em: 2026-03-19*
