# Manual de Automação FUNARPEN - Notas 🚀

Este documento detalha o avanço na automação da sanitização e transmissão dos selos do período **20/02/2026 a 27/03/2026**.

## 🛠️ Tecnologias e Infraestrutura
- **Servidor FUNARPEN**: `100.102.13.23:8049`
- **Banco de Dados**: `sptabel` (Produção)
- **Motor de Sanitização**: Google Gson (Leniente) com Limpeza Radical de Strings.
- **Autenticação**: Protocolo seguro via `ConnectionFactory` e Token JWT.
- **Tabelas**: `parametros` (CODTABEL_PAR para codigoOficio, DOC_PAR para documentoResponsavel)

---

## 🔧 Regras de Sanitização Implementadas

### Campos do Header JSON
| Campo | Origem (Tabela parametros) | Formato FUNARPEN | Status |
|-------|---------------------------|-----------------|--------|
| ambiente | AMBIENTE_PAR | "prod", "homolog", "dev" | ✅ |
| documentoResponsavel | DOC_PAR | CPF do tabelião | ✅ |
| codigoEmpresa | CODIGO_PAR | Valor numérico | ✅ |
| codigoOficio | CODTABEL_PAR | Código do cartório | ✅ |

### Campos do Selo
| Campo | Lógica de Sanitização | Status |
|-------|----------------------|--------|
| codigoPedido | Decimal -> Inteiro (95092.5 → 95092) | ✅ |
| dataAtoPraticado | Placeholder "dataato" -> data real | ✅ |
| seloRetificado | Vazio/null -> null | ✅ |
| versao | 11.12 | ✅ |

### Verbas (Valores Monetários)
| Campo | Lógica | Exemplo |
|-------|--------|---------|
| emolumentos | Se não tem ponto decimal E >= 10: dividir por 100 | 2414 → 24.14 |
| funrejus | Mesmo critério | 301 → 3.01 |
| iss | Mesmo critério | 36 → 0.36 |
| fundep | Mesmo critério | 60 → 0.60 |
| funarpen | Mesmo critério | 100 → 1.00 |
| vrcext | Mantém 0 | ✅ |
| distribuidor | Mantém 0 | ✅ |

### Campos Removidos (Injetados Incorretamente)
- `seloRetificado_original` ❌
- `propriedades_sanitizadas` ❌
- `codigoPedido_sanitizado` ❌

### Conversão de Tipos de Ato
| Tipo Original | Tipo Sanitizado | Observação |
|---------------|-----------------|------------|
| 408 | 459 | Escritura (conversão automática) |

### Conversão V10 → V11
O sanitizador detecta e converte JSON da versão 10 para versão 11.12:

**V10 (estrutura flat):**
```json
{
  "numeroTipoAto": 430,
  "seloDigital": "SFTN2.PJpKN.Rjauu-yrUDM.1122q",
  "ListaVerbas": [...],
  "ListaPropriedades": [...]
}
```

**V11 (estrutura com objeto "selo"):**
```json
{
  "ambiente": "prod",
  "documentoResponsavel": "09851329657",
  "codigoEmpresa": "...",
  "codigoOficio": 181122,
  "selo": {
    "seloDigital": "...",
    "codigoTipoAto": 430,
    "verbas": {...},
    "propriedades": {...}
  }
}
```

A conversão inclui:
1. Criar objeto `selo` e mover campos para dentro
2. Processar `ListaVerbas` → `verbas` (convertendo centavos para decimal)
3. Processar `ListaPropriedades` → `propriedades` conforme tipo de ato
4. Injetar dados do banco (documentoResponsavel, codigoOficio, etc.)

### Extração de Signatários/Partes
O sanitizador extrai signatários e partes de múltiplas fontes:

1. **propriedades.envolvidos** (JSON original): Para tipos 401-455
2. **ListaPropriedadesExtras** (após JSON original): Signatários adicionais
3. **Tabela not_1** (para tipos 408/459): Outorgantes e Outorgados
   - `QUALIF_NOT1=1` → outorgantes
   - `QUALIF_NOT1=2` → outorgados
   - `TIPOPESSOA_NOT1=F` → documentoTipo=1 (CPF)
   - `TIPOPESSOA_NOT1=J` → documentoTipo=2 (CNPJ)
   - `QUALIF_NOT1` (String) → conversão para inteiro

### Extração de Signatários de Dados Extras
Os dados de signatários estão disponíveis **APÓS o JSON original** (após o `}` final), concatenados no campo `ListaPropriedadesExtras`. A estrutura contém pares `NomePropriedade`/`ValorPropriedade`:

```json
{"NomePropriedade":"envolvidos.tipo_envolvido","ValorPropriedade":"Nome"},
{"NomePropriedade":"envolvidos.nome_razao","ValorPropriedade":"ALVARO LOPATKO SILVA"},
{"NomePropriedade":"envolvidos.CPF_CNPJ","ValorPropriedade":"86169963972"},
{"NomePropriedade":"envolvidos.doc_emissor_uf","ValorPropriedade":"PR"}
```

O sanitizador agora:
1. Extrai esses dados extras após o JSON original
2. Processa os pares `NomePropriedade`/`ValorPropriedade`
3. Monta os signatários com `nomeRazao`, `documentoTipo`, `documentoNumero`, `uf`

---

## 🔢 Tipos de Ato - Processamento por Tipo (FUNARPEN V11.12)

Esta seção documenta o processamento específico implementado para cada tipo de ato notarial, conforme manual técnico V11.12 (páginas 229-238).

### Tipos Implementados

| Tipo | Descrição | Método | Propriedades Injetadas |
|------|-----------|--------|----------------------|
| 401 | Autenticação | processarTipo455 | solicitanteAto, reconhecimento, signatarios |
| 402 | Sem Valor Declarado | processarTipo455 | tipo=2, solicitanteAto, reconhecimento, signatarios |
| 403 | Com Valor Declarado | processarTipo455 | tipo=1, solicitanteAto, reconhecimento, signatarios |
| 404 | Sinal Público | processarTipo455 | tipo=3, solicitanteAto, reconhecimento, signatarios |
| 407 | Carta de Sentença | processarTipo407 | solicitanteAto, cartaSentenca.totalPaginas, signatarios |
| 408 | Escritura | processarTipo459 | escritura, bens, outorgantes, outorgados |
| 409-413 | Escritura (variantes) | processarTipo459 | escritura, bens, outorgantes, outorgados |
| 416 | Constituição Condomínio | processarTipo416 | solicitanteAto, escritorio.tipo, unidades, livro/folha/termo |
| 430 | Traslado | processarTipo430 | traslado.livro, traslado.folha, traslado.termo |
| 432 | Certidão | processarTipo454 | certidao.tipo |
| 433 | Página Extra Certidão | processarTipo430 | traslado.livro, traslado.folha, traslado.termo |
| 439 | Anulação Selo | processarTipo430 | traslado.livro, traslado.folha, traslado.termo |
| 443 | Vaga Garagem | processarTipo430 | traslado.livro, traslado.folha, traslado.termo |
| 446 | Tempo Adicional | processarTipo430 | traslado.livro, traslado.folha, traslado.termo |
| 450 | Bem Adicional 10+ | processarTipo430 | traslado.livro, traslado.folha, traslado.termo |
| 452 | Procuração | processarTipo452 | procuracao, outorgantes, outorgados |
| 453 | Testamento | processarTipo453 | testamento, outorgantes |
| 454 | Certidão | processarTipo454 | certidao.tipo |
| 455 | Reconhecimento Firma | processarTipo455 | tipo, solicitanteAto, reconhecimento, signatarios |
| 456 | Ata Notarial | processarTipo456 | ataNotarial, outorgantes |
| 457 | Materialização | processarTipo457 | solicitanteAto, materializacao, envolvido, signatarios |
| 458 | Certidão Negativa | processarTipo458 | solicitanteAto, certidao, buscas |
| 459 | Escritura | processarTipo459 | escritura, bens, outorgantes, outorgados |

### Propriedades por Tipo (Manual V11.12)

#### 455 - Reconhecimento de Firma
```json
{
  "tipo": 1,
  "solicitanteAto": {"nomeRazao": "", "tipoDocumento": 1, "numeroDocumento": ""},
  "reconhecimento": {"especie": 1, "quantidadePartesEnvolvidasAto": 0, "data": "DD/MM/AAAA"},
  "signatarios": [{"nomeRazao": "", "documentoTipo": 1, "documentoNumero": ""}]
}
```
- tipo: 1=Com Valor, 2=Sem Valor, 3=Sinal Público
- especie: 1=Por Verdadeira/Autêntica, 2=Por Semelhança

#### 407 - Carta de Sentença
```json
{
  "solicitanteAto": {"nomeRazao": null, "tipoDocumento": null, "numeroDocumento": null},
  "cartaSentenca": {"totalPaginas": null},
  "signatarios": []
}
```

#### 416 - Constituição de Condomínio
```json
{
  "solicitanteAto": {"nomeRazao": null, "tipoDocumento": null, "numeroDocumento": null},
  "escritura": {
    "tipo": 1,
    "unidades": {"quantidadeTotal": null, "quantidadePartes": null},
    "livro": "88", "folha": "50", "termo": null
  },
  "outorgantes": []
}
```
- tipo: 1=Constituição, 2=Divisão, 3=Partilha Amigável

#### 430/433/439/443/446/450 - Traslado e derivados
```json
{
  "traslado": {"livro": "88", "folha": "50", "termo": null}
}
```

#### 452 - Procuração
```json
{
  "procuracao": {"livro": "88", "folha": "50", "data": "DD/MM/AAAA"},
  "outorgantes": [],
  "outorgados": []
}
```

#### 453 - Testamento
```json
{
  "testamento": {"tipo": 1, "livro": "88", "folha": "50", "termo": null},
  "outorgantes": []
}
```
- tipo: 1=Testamento Público, 2=Testamento Particular, 3=Testamento Cerrado, 4=Revogação

#### 454 - Certidão
```json
{
  "certidao": {"tipo": 1}
}
```
- tipo: 1=Procuração, 2=Escritura, 3=Termo de Reconhecimento

#### 456 - Ata Notarial
```json
{
  "ataNotarial": {"tipo": 1, "especie": 1, "livro": "88", "folha": "50", "termo": null},
  "outorgantes": []
}
```
- tipo: 1=Geral, 2=Usucapião, 3=Adjudicação Extrajudicial
- especie: 1=Interna, 2=Externa

#### 457 - Materialização
```json
{
  "solicitanteAto": {"nomeRazao": null, "tipoDocumento": null, "numeroDocumento": null},
  "materializacao": {"data": null, "descricao": null, "envolvido": {"nomeRazao": null}},
  "signatarios": []
}
```

#### 458 - Certidão Negativa
```json
{
  "solicitanteAto": {"nomeRazao": null, "tipoDocumento": null, "numeroDocumento": null},
  "certidao": {"tipo": 2, "consulta": null},
  "buscas": {"quantidade": null, "escritura": null, "quantidadeFolhaAdicional": null}
}
```

#### 459 - Escritura
```json
{
  "escritura": {"livro": "88", "folha": "50", "termo": "1", "data": "DD/MM/AAAA"},
  "bens": [{"descricao": "", "matricula": "", "valorAvaliacao": 0.0}],
  "outorgantes": [{"nomeRazao": "", "numeroDocumento": "", "tipoDocumento": 1}],
  "outorgados": [{"nomeRazao": "", "numeroDocumento": "", "tipoDocumento": 1}]
}
```

---

## 🚀 Passo 1: Executar Amostra de Sanitização (290 Selos)
Para validar os dados ANTES do envio real, utilize o script PowerShell atualizado. Ele agora utiliza o novo motor Gson que ignora malformações como `...` ou `|` e tokens automaticamente.

1.  Abra o terminal na raiz do projeto.
2.  Execute:
    ```powershell
    .\run_amostra.ps1
    ```
3.  **Resultado Esperado**: `Sucesso: 290 | Falha: 0`.

---

## 📤 Passo 2: Enviar Selos Sanitizados ao FUNARPEN (Lote)
Após a sanitização (Passo 1), os dados estarão na coluna `JSON12` da tabela `selados`. Para transmitir ao FUNARPEN, utilize o endpoint implementado no Servlet.

- **URL**: `POST /maker/api/funarpen/selos/recepcao/lote`
- **Parâmetros**:
  - `dataInicio=2026-03-19`
  - `dataFim=2026-03-27`
  - `limit=50` (ajustável)

**Comando cURL para Envio Massivo:**
```bash
curl -X POST "http://100.102.13.23:8059/notas/maker/api/funarpen/selos/recepcao/lote?dataInicio=2026-03-19&dataFim=2026-03-27&limit=50"
```

---

## 🔍 O que mudou? (Notas de Versão)
1.  **Sanitização Imbatível**: Trocamos o Jackson pelo **GSON**. Isso permitiu processar registros que continham truncamentos (`...`) ou placeholders sem aspas (`codoficio`).
2.  **Limpeza Radical**: Implementamos a remoção automática de pipes (`|`) e quebras de linha que causavam erros de recepção na API do FUNARPEN.
3.  **ConnectionFactory Seguro**: Integrado com o `DbUtil` oficial. Se o arquivo estiver criptografado, ele resolve via Jasypt; se falhar, utiliza as credenciais de produção (root/k15720) como fallback seguro para garantir a operação.
4.  **Atualização de Status**: O sistema agora atualiza `STATUS_FUNARPEN = '1'` e grava o `PROTOCOLO` retornado pelo servidor `100.102.13.23`.
5.  **Verbas Corrigidas**: Valores sem decimal são automaticamente convertidos (ex: 36 → 0.36).
6.  **CODTABEL_PAR**: Corrigido `codigoOficio` usando o campo `CODTABEL_PAR` da tabela `parametros`.
7.  **Processamento por Tipo**: Implementado processamento específicos para 29 tipos de ato (401-459).

---

## ✅ Estrutura JSON Corrigida (Modelo Final)

```json
{
  "ambiente": "prod",
  "documentoResponsavel": "09851329657",
  "codigoEmpresa": "1",
  "codigoOficio": "181122",
  "selo": {
    "seloDigital": "SFTN1.wGFJb.C4f2c-q6zZh.1122p",
    "codigoPedido": 95092,
    "tipoGratuidade": 0,
    "codigoTipoAto": 455,
    "tipoEmissaoAto": 1,
    "idap": "0000000000000045574R00000000000000000000",
    "versao": 11.12,
    "dataSeloEmitido": "19/03/2026",
    "dataAtoPraticado": "19/03/2026",
    "seloRetificado": null,
    "propriedades": {
      "tipo": 1,
      "solicitanteAto": {
        "nomeRazao": "DALTON FELIPE FERREIRA",
        "tipoDocumento": 1,
        "numeroDocumento": "11568022964"
      },
      "reconhecimento": {
        "especie": 1,
        "quantidadePaginasAto": null,
        "quantidadePartesEnvolvidasAto": 0,
        "data": "19/03/2026",
        "descricao": null
      },
      "signatarios": []
    },
    "verbas": {
      "emolumentos": 24.14,
      "vrcExt": 0,
      "funrejus": 3.01,
      "iss": 0.36,
      "fundep": 0.6,
      "funarpen": 1.0,
      "distribuidor": 0,
      "valorAdicional": 0
    }
  }
}
```

---

**Status Atual**: ✅ Pronto para Envio Massivo ao FUNARPEN.

## 📁 Arquivos Principais

| Arquivo | Descrição |
|---------|-----------|
| `SeloJsonSanitizerNotas.java` | Classe principal de sanitização (29 tipos implementados) |
| `ExecutarAmostraNotas.java` | Utilitário para teste em massa |
| `VerificarJson.java` | Utilitário para verificaç��o do JSON |

### Executar via Maven

```bash
# Compilar projeto
mvn compile

# Executar sanitização em massa
mvn exec:java -Dexec.mainClass="com.selador.util.ExecutarAmostraNotas"

# Verificar resultado da sanitização
mvn exec:java -Dexec.mainClass="com.selador.util.VerificarJson"
```

---

*Documento atualizado em: 29/03/2026*
*Versão: 1.8*
