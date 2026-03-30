# Serviço de Selagem - Tabelionato de Notas (FUNARPEN V11.12 Plus)

## Visão Geral

Este documento descreve o processo de implementação do serviço de selagem para o Tabelionato de Notas, integrando com a API do FUNARPEN versão V11.12 "Plus".

### Diferença Fundamental: Notas vs Protesto

| Aspecto | Tabelionato de Protesto (Selador) | Tabelionato de Notas |
|---------|-----------------------------------|---------------------|
| **Momento da Selagem** | Quando o documento é utilizado (protestado) | Na lavratura do ato (escrituração) |
| **Fluxo** | Selagem posterior ao ato | Selagem no momento da lavratura |
| **Retorno** | Calcula selos necessários | Retorna processo completo de selagem |

### Objetivo

Gerar o JSON de recepção de selos para o Tabelionato de Notas, tratando atos principais e seus respectivos selos acessórios/vinculados, retornando todo o processo de selagem concluído.

---

## 1. Estrutura Base (Seguir mesmo padrão do Selador)

### 1.1 Pacotes

```
src/main/java/com/selador/
├── dao/
│   ├── ProtocoloDAO.java       # NOVO - Consulta protocolo (sptabel)
│   ├── ParteDAO.java           # NOVO - Consulta partes (not_1)
│   ├── VerbasDAO.java          # NOVO - Consulta verbas (fin_recitem)
│   ├── ImovelDAO.java          # NOVO - Consulta imóveis (protimoveis)
│   └── AcessorioDAO.java       # NOVO - Consulta atos acessórios
├── service/
│   └── SelagemNotasService.java  # NOVO - Serviço principal de Notas
├── util/
│   └── FunarpenJsonGeneratorNotas.java  # Adaptado para Notas
└── web/
    └── servlets/
        └── SelagemNotasServlet.java  # NOVO - Servlet de integração
```

### 1.2 Models (Reutilizar existentes)

```java
// Reutilizar do Selador existente:
- Selo.java       ✅ Já existe
- Selado.java     ✅ Já existe
- SerCustas.java  ✅ Já existe
```

### 1.3 Enums (Criar novos para Notas)

```java
// NOVO: Tipos de Selo para Notas (TN = Tabelionato de Notas)
public enum TipoSeloNotas {
    TN1("1", "TN1", "Selo Digital", new BigDecimal("1.00")),
    TN2("2", "TN2", "Selo Fiscalização", new BigDecimal("8.00")),
    TN3("3", "TN3", "Selo Fiscalização", new BigDecimal("5.00")),
    TN5("5", "TN5", "Selo Apostilamento", new BigDecimal("1.00"));
    
    // ...
}

// NOVO: Tipos de Ato para Notas
public enum TipoAtoNotas {
    // Códigos FUNARPEN para Tabelionato de Notas
    AUTENTICACAO(401, "TN1", "Autenticação"),
    TRANSFERENCIA_VEICULO(406, "TN1", "Transferência de Veículo"),
    CARTA_SENTENCA(407, "TN2", "Carta de Sentença"),
    CONSTITUICAO_CONDOMINIO(416, "TN2", "Constituição de Condomínio"),
    UNIDADE_ADICIONAL(417, "TN1", "Unidade Adicional"),
    BEM_ADICIONAL(418, "TN2", "Bem Adicional (até 9ª)"),
    PAGINA_EXTRA_ATA(421, "TN1", "Página Extra - Ata"),
    PARTE_EXTRA_PROCURACAO(424, "TN3", "Parte Extra - Procuração"),
    PUBLICA_FORMA(428, "TN1", "Pública Forma"),
    PUBLICA_FORMA_PAGINA(429, "TN1", "Página Extra - Pública Forma"),
    TRASLADO(430, "TN2", "Traslado"),
    CERTIDAO_PAGINA(433, "TN3", "Página Extra - Certidão"),
    BUSCAS(436, "TN3", "Buscas (10 anos)"),
    APOSTILAMENTO(437, "TN5", "Apostilamento"),
    VAGA_GARAGEM(443, "TN2", "Vaga de Garagem"),
    SESSAO_CONCILIACAO(445, "TN2", "Sessão de Conciliação"),
    TEMPO_ADICIONAL(446, "TN2", "Tempo Adicional"),
    BEM_ADICIONAL_10(450, "1", "Bem Adicional (10ª+)"),
    PROCURACAO(452, "TN2", "Procuração"),
    TESTAMENTO(453, "TN2", "Testamento"),
    CERTIDAO(454, "TN1", "Certidão"),
    RECONHECIMENTO_FIRMA(455, "TN1", "Reconhecimento de Firma"),
    ATA_NOTARIAL(456, "TN2", "Ata Notarial"),
    MATERIALIZACAO(457, "TN1", "Materialização"),
    CERTIDAO_NEGATIVA(458, "TN1", "Certidão Negativa"),
    ESCRITURA(459, "TN2", "Escritura");
    
    private final int codigo;
    private final String tipoSelo;
    private final String descricao;
    
    // ...
}
```

---

## 2. Regras de Negócio

### 2.1 Cálculo do Selo Principal

| Emolumentos | Selo Utilizado | Valor |
|-------------|----------------|-------|
| > R$ 32,00 | TN2 | R$ 8,00 |
| R$ 4,01 a R$ 32,00 | TN1 | R$ 1,00 |
| ≤ R$ 4,00 | TN1 | R$ 1,00 |
| Apostilamento (Tipo 437) | TN5 | R$ 1,00 |

### 2.2 Tipos de Selo por Natureza

| Tipo | Nome | Valor |
|------|------|-------|
| TN1 | Digital | R$ 1,00 |
| TN2 | Fiscalização | R$ 8,00 |
| TN3 | Fiscalização | R$ 5,00 |
| TN5 | Apostilamento | R$ 1,00 |

### 2.3 Lógica de Selos Acessórios (Vínculos)

#### Páginas Extras
- **Tipo 433**: Certidões
- **Tipo 421**: Atas Notariais
- **Tipo 429**: Pública Forma

#### Partes Excedentes
- **Tipo 424**: Cada outorgante/outorgado que exceder o limite em Procurações

#### Bens/Imóveis Adicionais
- **Tipo 418**: Até a 9ª unidade (Escrituras 459, Atas 456, Inventários 412)
- **Tipo 450**: A partir da 10ª unidade

#### Vagas de Garagem
- **Tipo 443**: Quando possuem matrícula autônoma

### 2.4 Cálculo de Verbas

```java
// Funrejus: 0,2% sobre o valor base
funrejus = valorBase.multiply(new BigDecimal("0.002"));

// VRCEXT: Converter total de VRCs pelo indexador de 2024
vrcext = totalVRC.multiply(new BigDecimal("0.277"));

// ISS: Conforme alíquotas da serventia
iss = valorBase.multiply(aliquotaServentia);
```

### 2.5 Geração do IDAP

O IDAP deve ter 20 caracteres:
```
AAAAMMDD + Natureza (TN) + LIVRO + FOLHA + Sequencial (5 dígitos) + numtipoato
```

Exemplo: `00000000012345678901TN000345012300000459`

---

## 3. Mapeamento de Tipos de Ato

### Tabela de Códigos de Ato

| Código | Tipo de Ato | Selo Principal | Selos Vinculados |
|--------|-------------|----------------|------------------|
| 401 | Autenticação | TN1 | - |
| 406 | Transferência de Veículo | TN1 | - |
| 407 | Carta de Sentença | TN2 | - |
| 412 | Inventário | TN2 | 430, 418, 450, 443 |
| 416 | Constituição de Condomínio | TN2 | 430, 417 |
| 417 | Unidade Adicional (Condomínio) | TN1 | 430, 416 |
| 418 | Unidade/Bem Adicional | TN2 | 416, 452, 453, 456, 459 |
| 421 | Página Extra (Ata) | TN1 | 430, 456 |
| 424 | Parte Extra (Procuração) | TN3 | 430, 452 |
| 428 | Pública Forma | TN1 | 429 |
| 429 | Página Extra (Pública Forma) | TN1 | 428 |
| 430 | Traslado | TN2 | 452, 453, 456, 459 |
| 433 | Página Extra (Certidão) | TN3 | 454 |
| 436 | Buscas (10 anos) | TN3 | 454 |
| 437 | Apostilamento | TN5 | - |
| 443 | Vaga de Garagem | TN2 | 416, 452, 453, 456, 459 |
| 445 | Sessão de Conciliação | TN2 | 446 |
| 446 | Tempo Adicional (Conciliação) | TN2 | 445 |
| 450 | Unidade/Bem Extra (10+) | TN1 | 416, 452, 453, 456, 459 |
| 452 | Procuração | TN2 | 424, 430, 418, 450, 443 |
| 453 | Testamento | TN2 | 430 |
| 454 | Certidão | TN1 | 436, 433 |
| 455 | Reconhecimento de Firma | TN1 | 455 |
| 456 | Ata Notarial | TN2 | 421, 430, 418, 450, 443 |
| 457 | Materialização | TN1 | 436, 433 |
| 458 | Certidão Negativa | TN1 | - |
| 459 | Escritura | TN2 | 430, 418, 450, 443 |

---

## 4. Consultas SQL do Sistema (Base sptabel)

### 4.1 Dados do Protocolo

```sql
SELECT
    protocolo.CODIGO_PRO AS codigo,
    protocolo.SERVICO_PRO AS servico,
    protocolo.CODTIPOATO_PRO AS tipoAto,
    protocolo.LIVRO_PRO AS livro,
    protocolo.FOLHA_PRO AS folha,
    protocolo.LETRA_PRO AS letra,
    protocolo.VLRDECLAR_PRO AS valorDeclarado,
    protocolo.VLRAVALIADO_PRO AS valorAvaliado,
    protocolo.EMOLUMENTO_PRO AS emolumentos,
    protocolo.NUMREC_PRO AS numRec,
    protocolo.PROTOC_SEQ_LIV AS numReq,
    protocolo.SELOLIVRO_PRO AS seloLivro,
    protocolo.SELOTRANSL_PRO AS selloTraslado,
    protocolo.SELOCERT_PRO AS seloCertidao
FROM protocolo
WHERE protocolo.CODIGO_PRO = :PROTOCOLO
```

### 4.2 Quantidade de Partes

```sql
SELECT Count(protpartes.CODCLI_PRO) AS qtde
FROM protpartes
WHERE protpartes.CODIGO_PRO = :PROTOCOLO
```

### 4.3 Primeira Parte (Solicitante)

```sql
SELECT
    cliente.NUMERO_DOC_CLI AS numeroDoc,
    cliente.NOME_CLI AS nome,
    cliente.CODIGO_CLI AS codigo,
    cliente.NUMDOC1_CLI AS numeroDoc1,
    cliente.ORGAOEMISSDOC1_CLI AS orgaoEmissor,
    cliente.UFORGAOEMISSDOC1_CLI AS ufOrgao
FROM protpartes
INNER JOIN cliente 
    ON (protpartes.TIPO_PRO = cliente.TIPO_CLI) 
    AND (protpartes.CODCLI_PRO = cliente.CODIGO_CLI)
WHERE protpartes.CODIGO_PRO = :PROTOCOLO
LIMIT 1 OFFSET 0
```

### 4.4 Todas as Partes

```sql
SELECT
    cliente.NUMERO_DOC_CLI AS numeroDoc,
    cliente.NOME_CLI AS nome,
    cliente.CODIGO_CLI AS codigo,
    cliente.NUMDOC1_CLI AS numeroDoc1,
    cliente.ORGAOEMISSDOC1_CLI AS orgaoEmissor,
    cliente.UFORGAOEMISSDOC1_CLI AS ufOrgao
FROM protpartes
INNER JOIN cliente 
    ON (protpartes.TIPO_PRO = cliente.TIPO_CLI) 
    AND (protpartes.CODCLI_PRO = cliente.CODIGO_CLI)
WHERE protpartes.CODIGO_PRO = :PROTOCOLO
```

### 4.5 Tipo de Ato

```sql
SELECT
    codtipoato.NUMEROTIPO AS numeroTipo,
    codtipoato.CODSELO AS codSelo
FROM codtipoato
WHERE (codtipoato.COD_COD = :COD) AND (codtipoato.TIPO_COD = 'N')
```

### 4.6 Verbas (fin_recitem)

```sql
SELECT
    (SELECT fin_recitem.valor_rec FROM fin_recitem
     WHERE (fin_recitem.num_rec = :NUMREC) AND (fin_recitem.codigo_cus = 901) LIMIT 1) As FADEP,
    (SELECT Sum(fin_recitem.qtde_rec * fin_recitem.valor_rec) FROM fin_recitem
     WHERE (fin_recitem.num_rec = :NUMREC) AND 
           (fin_recitem.codigo_cus = 903 OR fin_recitem.codigo_cus = 908 OR fin_recitem.codigo_cus = 909 OR fin_recitem.codigo_cus = 910 OR fin_recitem.codigo_cus = 911)) As FUNARPEN,
    (SELECT fin_recitem.valor_rec FROM fin_recitem
     WHERE (fin_recitem.num_rec = :NUMREC) AND (fin_recitem.codigo_cus = 904) LIMIT 1) As ISS,
    (SELECT fin_recitem.valor_rec FROM fin_recitem
     WHERE (fin_recitem.num_rec = :NUMREC) AND (fin_recitem.codigo_cus = 905) LIMIT 1) As DISTRIB,
    (SELECT fin_recitem.valor_rec FROM fin_recitem
     WHERE (fin_recitem.num_rec = :NUMREC) AND (fin_recitem.codigo_cus = 907) LIMIT 1) As FUNREJUS,
    (SELECT Sum(fin_recitem.valor_rec)
     FROM fin_recitem INNER JOIN ser_custas ON fin_recitem.codigo_cus = ser_custas.CODIGO_CUS
     WHERE (fin_recitem.num_rec = :NUMREC) AND (ser_custas.SISTEMA_CUS = 'N') AND (ser_custas.BASE_CUS = 'S')) As EMOLUMENTOS
```

### 4.7 Selos Disponíveis

```sql
SELECT
    Count(selos.selo_sel) As DISPONIVEL,
    selos.versao
FROM selos
WHERE (selos.tiposelo_sel = :CODSELO) AND (selos.tipo_sel = 0 OR selos.tipo_sel Is Null)
GROUP BY selos.versao, selos.tipo_sel
LIMIT 1
```

### 4.8 Selar Estoque (com LOCK)

```sql
SELECT
    selos.selo_sel,
    selos.numpedido,
    selos.pedido_sel,
    selos.ID_sel,
    selos.versao
FROM selos
WHERE (selos.tiposelo_sel = :CODSELO) AND (selos.tipo_sel = 0 OR selos.tipo_sel Is Null)
LIMIT 1
FOR UPDATE
```

### 4.9 Atos Acessórios (fin_recitem)

```sql
SELECT
    ser_custas.TIPOATO_CUS,
    codtipoato.CODSELO
FROM ser_custas
INNER JOIN codtipoato ON ser_custas.TIPOATO_CUS = codtipoato.NUMEROTIPO
WHERE (ser_custas.SISTEMA_CUS = 'N') AND (ser_custas.CODIGO_CUS = :CODCUS)
```

### 4.10 Imóveis do Protocolo

```sql
SELECT
    protimoveis.CODIGO_IMO,
    protimoveis.ENDERECO_IMO,
    protimoveis.MATRICULA_IMO,
    protimoveis.VALOR_IMO,
    protimoveis.AREA_IMO,
    protimoveis.TIPO_IMO
FROM protimoveis
WHERE protimoveis.CODPROT_IMO = :PROTOCOLO
```

---

## 5. Fluxo de Execução do Serviço

### 5.1 Fluxo Principal (Na Lavratura)

```
1. Receber número do protocolo (CODIGO_PRO)
         ↓
2. Consultar dados do protocolo (SERVICO_PRO, CODTIPOATO, LIVRO, FOLHA, LETRA)
         ↓
3. Consultar quantidade de partes
         ↓
4. Consultar primeira parte (solicitante)
         ↓
5. Consultar tipo de ato (NUMEROTIPO, CODSELO)
         ↓
6. Buscar verbas (fin_recitem)
         ↓
7. Buscar imóveis vinculados (protimoveis)
         ↓
8. Verificar estoque de selos disponíveis
         ↓
9. Calcular selo principal (TN1, TN2, TN5)
         ↓
10. Calcular selos acessórios (páginas extras, partes extras, bens extras)
         ↓
11. Para cada tipo de selo necessário:
    11.1 Reservar selo no estoque (SELECT FOR UPDATE)
    11.2 Gerar IDAP único
    11.3 Gerar JSON para FUNARPEN
    11.4 Enviar para API FUNARPEN
    11.5 Gravar na tabela selados
    11.6 Atualizar tabela selos (marcar como utilizado)
         ↓
12. Atualizar protocolo (SELOLIVRO_PRO, SELOTRANSL_PRO, etc.)
         ↓
13. Retornar resultado completo (sucesso + lista de selos utilizados)
```

### 5.2 Classe Principal de Serviço

```java
/**
 * Serviço de selagem para Tabelionato de Notas
 * Segue o padrão do SelagemService existente, adaptado para Notas
 */
public class SelagemNotasService {

    /**
     * Processa selagem completa para um protocolo do Tabelionato de Notas
     * 
     * @param numeroProtocolo Número do protocolo (CODIGO_PRO)
     * @return Mapa com resultado da selagem
     */
    public Map<String, Object> processarSelagemNotas(Long numeroProtocolo) {
        // 1. Buscar dados do protocolo
        // 2. Consultar partes
        // 3. Buscar verbas
        // 4. Calcular selos necessários
        // 5. Reservar e utilizar selos
        // 6. Enviar para FUNARPEN
        // 7. Atualizar protocolo
        // 8. Retornar resultado
    }
}
```

---

## 6. Geração de JSON (FUNARPEN V11.12 Plus)

### 6.1 Estrutura JSON Base - Modelo Corrigido

A estrutura JSON deve seguir rigorosamente este formato para ser aceita pela API FUNARPEN V11.12 Plus:

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

### 6.2 Regras de Sanitização

| Campo | Origem (parametros) | Validação | Status |
|-------|---------------------|-----------|--------|
| ambiente | AMBIENTE_PAR | P→prod, H→homolog, D→dev | ✅ |
| documentoResponsavel | DOC_PAR | CPF do tabelião | ✅ |
| codigoEmpresa | CODIGO_PAR | Valor numérico ou string | ✅ |
| codigoOficio | CODTABEL_PAR | Código do cartório | ✅ |

### 6.3 Correções Aplicadas pelo Sanitizer

| Problema | Antes | Depois | Método |
|----------|-------|--------|--------|
| Ambiente | "P" | "prod" | Mapear P/H/D |
| documentoResponsavel | null/"0" | DOC_PAR | Preencher do banco |
| codigoPedido decimal | 95092.5 | 95092 | Remover ponto |
| dataAtoPraticado | "dataato" | dataSeloEmitido | Fallback |
| Verbas sem decimal | 36 | 0.36 | Dividir por 100 |
| Campos injetados | seloRetificado_original | REMOVIDO | Limpar |

### 6.4 Lógica de Conversão de Verbas

```java
// Valores sem ponto decimal E >= 10 são convertidos (centavos -> reais)
// Exemplo: 36 (36 centavos = R$0.36) -> 0.36
if (!valorStr.contains(".") && valor >= 10) {
    valorDecimal = valor / 100.0;
}
```

| Verba | Valor Original | Valor Correto |
|-------|---------------|---------------|
| emolumentos | 2414 | 24.14 |
| funrejus | 301 | 3.01 |
| iss | 36 | 0.36 |
| fundep | 60 | 0.60 |
| funarpen | 100 | 1.00 |

---

## 6.5 Processamento por Tipo de Ato (FUNARPEN V11.12)

Esta seção documenta o processamento específico implementado para cada tipo de ato notarial, conforme manual técnico V11.12 (páginas 229-238).

### Métodos Implementados em SeloJsonSanitizerNotas.java

| Tipo | Descrição | Método | Propriedades Injetadas |
|------|-----------|--------|----------------------|
| 401 | Autenticação | processarTipo455 | solicitanteAto, reconhecimento, signatarios |
| 402 | Sem Valor Declarado | processarTipo455 | tipo=2, solicitanteAto, reconhecimento, signatarios |
| 403 | Com Valor Declarado | processarTipo455 | tipo=1, solicitanteAto, reconhecimento, signatarios |
| 404 | Sinal Público | processarTipo455 | tipo=3, solicitanteAto, reconhecimento, signatarios |
| 407 | Carta de Sentença | processarTipo407 | solicitanteAto, cartaSentenca.totalPaginas, signatarios |
| 409-413 | Escritura (variantes) | processarTipo459 | escritura, bens, outorgantes, outorgados |
| 416 | Constituição Condomínio | processarTipo416 | solicitanteAto, escritorio.tipo, unidades, livro/folha/termo |
| 430 | Traslado | processarTipo430 | traslado.livro, traslado.folha, traslado.termo |
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

### Propriedades por Tipo (Manual V11.12 - páginas 229-238)

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

## 7. Checklist de Implementação

### Fase 1: Estrutura Base

- [ ] 1.1 - Criar enum `TipoSeloNotas` (TN1, TN2, TN3, TN5)
- [ ] 1.2 - Criar enum `TipoAtoNotas` (todos os códigos de 401 a 459)
- [ ] 1.3 - Criar DAO `ProtocoloDAO` para consultar protocolo
- [ ] 1.4 - Criar DAO `ParteDAO` para consultar partes
- [ ] 1.5 - Criar DAO `VerbasDAO` para consultar verbas
- [ ] 1.6 - Criar DAO `ImovelDAO` para consultar imóveis

### Fase 2: Consultas e Repositórios

- [ ] 2.1 - Implementar consulta de dados do protocolo
- [ ] 2.2 - Implementar consulta de quantidade de partes
- [ ] 2.3 - Implementar consulta de verbas
- [ ] 2.4 - Implementar consulta de selos disponíveis (adaptar do SeloDAO)
- [ ] 2.5 - Implementar reserva de selo no estoque

### Fase 3: Serviços de Negócio

- [ ] 3.1 - Criar `IdapGeneratorService` para gerar IDAP (20 caracteres)
- [ ] 3.2 - Criar `SeloPrincipalCalculator` para calcular selo principal
- [ ] 3.3 - Criar `CalculadoraAcessoriosNotas` para calcular selos acessórios
- [ ] 3.4 - Criar `VerbasCalculator` para calcular verbas
- [ ] 3.5 - Implementar lógica de vínculos (páginas, partes, bens)

### Fase 4: Serviço Principal

- [ ] 4.1 - Criar `SelagemNotasService` (serviço principal)
- [ ] 4.2 - Implementar método `processarSelagemNotas(Long protocolo)`
- [ ] 4.3 - Implementar validação de estoque
- [ ] 4.4 - Implementar geração de JSON
- [ ] 4.5 - Implementar envio para API FUNARPEN

### Fase 5: Persistência

- [ ] 5.1 - Reutilizar `SeladoDAO` para gravar selados
- [ ] 5.2 - Reutilizar/adaptar update no protocolo
- [ ] 5.3 - Adaptar update no estoque de selos
- [ ] 5.4 - Implementar transaction management

### Fase 6: Propriedades Específicas por Tipo de Ato

- [ ] 6.1 - Implementar propriedades para Escritura (459)
- [ ] 6.2 - Implementar propriedades para Procuração (452)
- [ ] 6.3 - Implementar propriedades para Ata Notarial (456)
- [ ] 6.4 - Implementar propriedades para Certidão (454)
- [ ] 6.5 - Implementar propriedades para Testamentos (453)
- [ ] 6.6 - Implementar propriedades para Traslado (430)
- [ ] 6.7 - Implementar propriedades para Apostilamento (437)
- [ ] 6.8 - Implementar propriedades para Reconhecimento de Firma (455)
- [ ] 6.9 - Implementar propriedades para Autenticação (401)
- [ ] 6.10 - Implementar propriedades para Pública Forma (428)
- [ ] 6.11 - Implementar propriedades para Carta de Sentença (407)
- [ ] 6.12 - Implementar propriedades para Constituição de Condomínio (416)

### Fase 7: Integração com Maker Studio

- [ ] 7.1 - Criar função nativa para chamar o serviço
- [ ] 7.2 - Implementar passagem de parâmetros (numero protocolo)
- [ ] 7.3 - Implementar retorno de resultado

### Fase 8: Testes

- [x] 8.1 - ✅ Sanitização implementada em `SeloJsonSanitizerNotas.java`
- [x] 8.2 - ✅ Teste de massa (290 selos) concluído com sucesso
- [x] 8.3 - ✅ Validação de estrutura JSON conforme manual FUNARPEN

### Fase 9: Sanitização (IMPLEMENTADA)

- [x] 9.1 - ✅ `SeloJsonSanitizerNotas.java` - classe principal de sanitização
- [x] 9.2 - ✅ Correção de ambiente (P→prod, H→homolog, D→dev)
- [x] 9.3 - ✅ Preenchimento de documentoResponsavel (DOC_PAR)
- [x] 9.4 - ✅ Preenchimento de codigoOficio (CODTABEL_PAR)
- [x] 9.5 - ✅ Correção de codigoPedido (decimal→inteiro)
- [x] 9.6 - ✅ Correção de dataAtoPraticado (placeholder→data real)
- [x] 9.7 - ✅ Correção de verbas (centavos→reais)
- [x] 9.8 - ✅ Remoção de campos injetados incorretamente
- [x] 9.9 - ✅ `ExecutarAmostraNotas.java` - utilitário de teste em massa

---

## 8. Tabela de Custas (ser_custas)

### Atualização Necessária

A tabela `ser_custas` deve ser atualizada para incluir a classificação de tipos de ato por serventia:

```sql
-- Adicionar colunas para classificação
ALTER TABLE ser_custas ADD COLUMN CLASSIFICACAO_ATO VARCHAR(50);
ALTER TABLE ser_custas ADD COLUMN TIPO_ACESSORIO VARCHAR(50);

-- Exemplos de classificação:
-- 459|ESCRITURA|PRINCIPAL
-- 418|ESCRITURA|BEM_ADICIONAL
-- 430|ESCRITURA|TRASLADO
-- 443|ESCRITURA|VAGA_GARAGEM
-- 452|PROCURACAO|PRINCIPAL
-- 424|PROCURACAO|PARTE_ADICIONAL
-- 456|ATA|PRINCIPAL
-- 421|ATA|PAGINA_ADICIONAL
-- 454|CERTIDAO|PRINCIPAL
-- 433|CERTIDAO|PAGINA_ADICIONAL
```

---

## 9. Observações Importantes

### 9.1 Encoding

- O JSON gerado deve ser **UTF-8**
- Campos não preenchidos devem ser enviados como `null`

### 9.2 Formato do IDAP

```
AAAA = Ano
MM   = Mês
DD   = Dia
TN   = Natureza (Tabelionato de Notas)
NNNNNNNN = Sequencial (8 dígitos)
```

### 9.3 Tratamento de Nulos

- Todos os campos opcionais devem ser verificados antes do envio
- Não enviar strings vazias - usar `null`

### 9.4 Cálculo de VRC

- Indexador 2024: R$ 0,277 por VRC
- Aplicar em todos os atos que possuem VRC

### 9.5 Certidões

- Para certidões, o protocolo deve ser concatenado com "C"
- Exemplo: Protocolo 345 → "C345"

### 9.6 Diferença na Geração do IDAP

O IDAP no Tabelionato de Notas deve seguir o formato especificado na task:
```
12 ZEROS + (PROTOCOLO,8,0) + N + (LIVRO,6,0) + LETRA + (FOLHA,3,0) + 9 ZEROS = 40 caracteres
```

Exemplo: `000000002024000001N000001A001000000`

---

## 10. Referências

- **API FUNARPEN**: Versão V11.12 "Plus"
- **Tabela de Emolumentos**: Tabela XI da Lei Estadual
- **Código de Normas**: Art. 457, 743-A, 730-A
- **Instruções Normativas**: 028/2020 - CGJ, 62/2017 - CNJ
- **Projeto Selador**: Estrutura base adaptada do projeto existente

---

## 11. Arquivos de Referência do Selador

| Arquivo | Descrição |
|---------|-----------|
| `SelagemService.java` | Service principal de selagem |
| `SeloService.java` | Service de gerenciamento de selos |
| `SeloDAO.java` | DAO para operações na tabela selos |
| `SeladoDAO.java` | DAO para tabela selados |
| `FunarpenJsonGenerator.java` | Gerador de JSON para FUNARPEN |
| `TipoSelo.java` | Enum de tipos de selo |
| `TipoOperacao.java` | Enum de operações |
| `Selo.java` | Model do selo |
| `Selado.java` | Model do registro de selagem |

---

## 12. Tabelas de Verificação do Banco de Dados

### 12.1 Scripts de Verificação Criados

Para verificar e atualizar as tabelas do banco de dados, execute os seguintes scripts SQL:

| Script | Descrição |
|--------|------------|
| [`sql/ddls/funarpen_verificacao.sql`](sql/ddls/funarpen_verificacao.sql) | Verifica tipos de ato na tabela `funarpen` |
| [`sql/ddls/codtipoato_verificacao.sql`](sql/ddls/codtipoato_verificacao.sql) | Verifica mapeamento na tabela `codtipoato` |

### 12.2 Tabela `funarpen`

Tabela principal que contém os tipos de ato e valores do FUNARPEN.

**Estrutura esperada:**
```sql
CODIGO_FUN     - Código do tipo de ato
DESCRICAO_FUN - Descrição do tipo
VALOR_FUN     - Valor do selo
TIPO_FUN      - Tipo: 'N' = Notas
```

**Códigos esperados para V11.12 Plus (Tabelionato de Notas):**

| Código | Descrição | Tipo Selo |
|--------|-----------|-----------|
| 401 | Autenticação | TN1 |
| 406 | Transferência de Veículo | TN1 |
| 407 | Carta de Sentença | TN2 |
| 412 | Inventário | TN2 |
| 416 | Constituição de Condomínio | TN2 |
| 417 | Unidade Adicional | TN1 |
| 418 | Bem Adicional (até 9ª) | TN2 |
| 421 | Página Extra - Ata | TN1 |
| 424 | Parte Extra - Procuração | TN3 |
| 428 | Pública Forma | TN1 |
| 429 | Página Extra - Pública Forma | TN1 |
| 430 | Traslado | TN2 |
| 433 | Página Extra - Certidão | TN3 |
| 436 | Buscas (10 anos) | TN3 |
| 437 | Apostilamento | TN5 |
| 443 | Vaga de Garagem | TN2 |
| 445 | Sessão de Conciliação | TN2 |
| 446 | Tempo Adicional | TN2 |
| 450 | Bem Adicional (10ª+) | TN1 |
| 452 | Procuração | TN2 |
| 453 | testamento | TN2 |
| 454 | Certidão | TN1 |
| 455 | Reconhecimento de Firma | TN1 |
| 456 | Ata Notarial | TN2 |
| 457 | Materialização | TN1 |
| 458 | Certidão Negativa | TN1 |
| 459 | Escritura | TN2 |

### 12.3 Tabela `codtipoato`

Tabela de mapeamento entre códigos internos e tipos de ato.

**Estrutura esperada:**
```sql
COD_COD        - Código interno
NUMEROTIPO    - Número do tipo de ato
CODSELO       - Código do selo (1=TN1, 2=TN2, 3=TN3, 5=TN5)
TIPO_COD      - Tipo: 'N' = Notas
DESCRICAO_COD - Descrição do tipo de ato
```

### 12.4 Como Executar a Verificação

1. Execute o script `sql/ddls/funarpen_verificacao.sql` no banco de dados `sptabel`
2. Execute o script `sql/ddls/codtipoato_verificacao.sql` no banco de dados `sptabel`
3. Verifique os resultados para identificar tipos faltando
4. Insira os tipos faltando conforme os exemplos nos scripts

---

*Documento gerado em: 2024*
*Versão: 1.1*
