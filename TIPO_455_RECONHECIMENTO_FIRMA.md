# Tipo 455 – RECONHECIMENTO DE FIRMA

## Descrição
O Reconhecimento de Firma é o ato de autenticar a assinatura de uma pessoa em documento apresentado ao tabelionato. É obrigatório o reconhecimento quando a lei ou as partes exigem certeza e autenticidade do ato.

## Vínculos de Selos
01 (um) SELO DE FISCALIZAÇÃO (TN1) – por reconhecimento de firma (por signatário);
Referência: Tabela XI Item I

Obrigatoriamente os selos precisam utilizar o mesmo número de IDAP para que esse relacionamento apresente na leitura do QRCode e na consulta do selo.

## Mapeamento de Tipos de Ato
Os códigos de tipo de ato 402, 403 e 404 são convertidos para 455 (Reconhecimento de Firma) no JSON sanitizado:

| codigoTipoAto original | propriedades.tipo | Descrição |
|------------------------|-------------------|------------|
| 403 | 1 | Com Valor Declarado |
| 402 | 2 | Sem Valor Declarado |
| 404 | 3 | Sinal Público |

## Estrutura do JSON Sanitizado

```json
{
  "ambiente": "prod",
  "documentoResponsavel": "09851329657",
  "codigoEmpresa": "eyJhbGciOiJIUzUxMiJ9...",
  "codigoOficio": 181196,
  "selo": {
    "seloDigital": "SFTN1.XXXXXXXX.XXXXXXXX.XXXXq",
    "codigoPedido": 944874,
    "tipoGratuidade": 0,
    "codigoTipoAto": 455,
    "tipoEmissaoAto": 1,
    "idap": "0000000000000045305R00000000000000000000",
    "versao": 11.12,
    "dataSeloEmitido": "04/03/2026",
    "dataAtoPraticado": "04/03/2026",
    "seloRetificado": "",
    "propriedades": {
      "tipo": 1,
      "solicitanteAto": {
        "nomeRazao": "NOME DO SOLICITANTE",
        "tipoDocumento": 1,
        "numeroDocumento": "12345678900"
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
          "nomeRazao": "NOME COMPLETO DO SIGNATÁRIO",
          "documentoTipo": 1,
          "documentoNumero": "12345678910"
        }
      ]
    },
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
}
```

## Propriedades do Ato

### propriedades.tipo
| Valor | Descrição |
|-------|-----------|
| 1 | Com Valor Declarado |
| 2 | Sem Valor Declarado |
| 3 | Sinal Público |

### solicitanteAto
| Propriedade | Tipo | Descrição |
|-------------|------|-----------|
| nomeRazao | string | Nome completo ou Razão Social do solicitante |
| tipoDocumento | int | Tipo de Documento (1-CPF, 2-CNPJ, 3-CNH, 4-RG, 5-Passaporte, 6-CTPS, 7-Título Eleitor, 8-Certificado Reserva, 9-Doc Estrangeiro, 10-Doc Profissional, 11-Outro, 12-Não Informado) |
| numeroDocumento | string | Número do documento |

### reconhecimento
| Propriedade | Tipo | Descrição |
|-------------|------|-----------|
| especie | int | 1 - Por Verdadeira ou Autêntica, 2 - Por Semelhança |
| quantidadePaginasAto | int | Quantidade de Páginas no ato (null - não temos essa informação registrada) |
| quantidadePartesEnvolvidasAto | int | Quantidade de Partes envolvidas no ato |
| data | string | Data do reconhecimento (DD/MM/AAAA) |
| descricao | string | Descrição do ato (null - não temos essa informação registrada) |

### signatarios
| Propriedade | Tipo | Descrição |
|-------------|------|-----------|
| nomeRazao | string | Nome completo ou Razão social do Signatário |
| documentoTipo | int | Tipo de Documento |
| documentoNumero | string | Número do documento |

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
| 10 | Documento de Identificação Profissional (Ex.CRM / OAB e CREA) |
| 11 | Outro |
| 12 | Não Informado |

## Fontes de Dados

| Dado | Tabela | Campo | Origem |
|------|--------|-------|--------|
| documentoResponsavel | parametros | DOC_PAR | Valor fixo do sistema |
| solicitanteAto | fin_reccab | nomecli_rec, cpfcli_rec | Busca por num_rec (IDAP posições 11-20) |
| signatarios | ListaPropriedades | envolvidos.nome_razao, emvolvidos.CPF_CNPJ | Extraído do JSON original |
| verbas | selos | emolumentos, vrcExt, funrejus, iss, fundep, funarpen, distribuidor, valorAdicional | Mantido do JSON original |

## Cálculo do num_rec para busca do solicitante
O num_rec é extraído do IDAP (posições 11 a 20):
- IDAP: "0000000000000045305R00000000000000000000"
- num_rec: "0000045305" (45305)

Exemplo de consulta: para buscar o solicitanteAto
```sql
SELECT nomecli_rec, cpfcli_rec 
FROM fin_reccab 
WHERE num_rec = 45305
```

## Regras de Sanitização

1. **codigoTipoAto**: Sempre converter para 455
2. **propriedades.tipo**: Mapear conforme tabela de conversão (403→1, 402→2, 404→3)
3. **codigoPedido**: Remover ponto decimal (ex: 94487.4 → 944874)
4. **documentoResponsavel**: Buscar em parametros.DOC_PAR
5. **solicitanteAto**: Buscar em fin_reccab usando num_rec derivado do IDAP
6. **signatarios**: Extrair de ListaPropriedades do JSON original
7. **quantidadePartesEnvolvidasAto**: Contar signatários no array
8. **verbas**: Manter valores originais do JSON

## Exemplo de Dados Reais

### Dados do Banco
- IDAP: 0000000000000045305R00000000000000000000
- num_rec: 45305
- solicitante: GILSON DA SILVEIRA PRINS
- CPF: 89751817900

### Dados do JSON Original (signatários)
- Nome: MARIA DE LURDES BRONOSKI OCHINSKI
- CPF: 05496182956

### JSON Sanitizado Resultado
```json
{
  "ambiente": "prod",
  "documentoResponsavel": "09851329657",
  "codigoEmpresa": "eyJhbGciOiJIUzUxMiJ9...",
  "codigoOficio": 181196,
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
    "seloRetificado": "",
    "propriedades": {
      "tipo": 2,
      "solicitanteAto": {
        "nomeRazao": "GILSON DA SILVEIRA PRINS",
        "tipoDocumento": 1,
        "numeroDocumento": "89751817900"
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
      ]
    },
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
}
```
