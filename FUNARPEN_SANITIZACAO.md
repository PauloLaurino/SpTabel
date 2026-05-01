# Plano de Conclusão - Sanitização FUNARPEN V11.12

## Situação Atual (30/03/2026)

### ✅ Concluído

- Backup GitHub: https://github.com/PauloLaurino/SpTabel
- Conexão forçada para banco sptabel
- Endpoint de sanitização em lote
- Sanitização de 226+ selos para versão 11.12
- Tipos de ato suportados: 401-459 (maioria)

### ❌ Pendente

- Registros com JSON12 em formato V10 (sem objeto "selo") não estão sendo convertidos
- Alguns IDs específicos falha: 48302, 48303, 48457, 48518, 48519, 48530

---

## Tarefas para Conclusão

### Task 1: debug e Correção Conversão V10→V11

**Problema**: Registros com JSON na estrutura antiga (sem objeto "selo") não estão sendo convertidos.

**JSON Atual (V11)**:

```json
{
  "numeroTipoAto": 430,
  "seloDigital": "SFTN2.PJpKN.Rjauu-yrUDM.1122q",
  "versao": "11",
  "ListaVerbas": [...],
  "ListaPropriedades": [...]
}
```

**Esperado (V11)**:

```json
{
  "ambiente": "prod",
  "documentoResponsavel": "...",
  "codigoOficio": 181122,
  "selo": {
    "seloDigital": "...",
    "codigoTipoAto": 430,
    "versao": "11.12",
    "verbas": {...},
    "propriedades": {...}
  }
}
```

**Solução**: O conversor `converterV11ParaV11.12` precisa ser Called quando JSON12 não tem objeto "selo".

### Task 2: Teste de endpoints

**Verificar após correção**:

```
GET http://100.102.13.23:8049/notas/maker/api/notas/selos/sanitizar/SFTN2.PJpKN.Rjauu-yrUDM.1122q
```

Esperado: success=true, JSON12 atualizado

### Task 3: Sanitização em Lote

**Executar após Tasks 1-2**:

```
GET http://100.102.13.23:8049/notas/maker/api/notas/selos/sanitizar/lote?force=true
```

Esperado: >1000 registros processados, todos com versão 11.12

### Task 4: Validação Final

**Verificar no banco**:

```sql
SELECT ID, NUMTIPATO, JSON_EXTRACT(JSON12, '$.selo.versao') AS VERSAO
FROM selados
WHERE JSON12 IS NOT NULL
ORDER BY ID DESC
LIMIT 20;
```

Esperado: Todos com "11.12"

---

## Código-Chave para debug

### Localização

- **SeloJsonSanitizerNotas.java:838-877** - Verificação de JSON12 existente
- **SeloJsonSanitizerNotas.java:905-925** - Atualização de versão 11 → 11.12

### Log para debug

O código tem logging em:

- `[SeloJsonSanitizerNotas] JSON12 válido para selo`
- `[SeloJsonSanitizerNotas] Atualizando versão` para 11.12
- `[SeloJsonSanitizerNotas] JSON12 é V11 (sem objeto 'selo')`

### Variáveis Importantes

- `forceRevalidate` - força re-processamento
- `needsConversion` - indica se precisa converter V11→V11.12

---

## Referência - Estruturas JSON

### Header (obrigatório)

```json
{
  "ambiente": "prod",
  "documentoResponsavel": "12345678900",
  "codigoEmpresa": "1",
  "codigoOficio": 181122
}
```

### objeto "selo" (obrigatório)

```json
{
  "seloDigital": "SFTN1.xxx",
  "codigoPedido": 94487,
  "codigoTipoAto": 455,
  "tipoEmissaoAto": 1,
  "idap": "",
  "versao": "11.12",
  "dataSeloEmitido": "30/03/2026",
  "dataAtoPraticado": "30/03/2026",
  "seloRetificado": null,
  "tipoGratuidade": 0,
  "verbas": {...},
  "propriedades": {...}
}
```

### verbas (obrigatório)

```json
{
  "emolumentos": 24.14,
  "vrcExt": 0,
  "funrejus": 3.01,
  "iss": 0.36,
  "fundep": 0.6,
  "funarpen": 1.0,
  "distribuidor": 0,
  "valorAdicional": 0
}
```

---

## Cronograma Proposto

| Data  | Task                    | Responsável |
| ----- | ----------------------- | ----------- |
| 30/03 | Task 1: Debug conversão | Kilo        |
| 30/03 | Task 2: Teste endpoint  | Kilo        |
| 30/03 | Task 3: Lote            | Paulo       |
| 31/03 | Task 4: Validação       | Paulo       |

---

## Contato Suporte FUNARPEN

- Email: suporte@funarpen.com.br
- Telefone: (41) 3222-4000
