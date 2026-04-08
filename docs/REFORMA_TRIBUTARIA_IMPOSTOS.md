# Reforma Tributária - Impostos sobre Serviços (NFSe)

Este documento detalha as mudanças trazidas pela Reforma Tributária (EC 132/2023) e como o sistema de NFS-e deve lidar com os novos impostos e transições.

## Cronograma de Implementação

| Fase | Período | Descrição |
|------|---------|-----------|
| **Fase 1** | 2025-2026 | CBS/IBS começam a substituir gradualmente o ISS |
| **Fase 2** | 2026-2028 | Transição com ISS + CBS + IBS simultâneos |
| **Fase 3** | 2028-2033 | CBS/IBS se tornam principais |
| **Fase 4** | 2033+ | Substituição completa do ISS por CBS+IBS |

## Impostos Atuais (2024)

### ISS (Imposto Sobre Serviços)
- **Abrangência**: Municipal
- **Alíquota**: 2% a 5% (varia por município)
- **Base de cálculo**: Valor do serviço
- **Responsável**: Município do local do serviço

### PIS/COFINS
- **Abrangência**: Federal
- **PIS**: 1,65%
- **COFINS**: 7,6%
- **Regime**: Não cumulativo (pode descontar créditos)

### CSLL (Contribuição Social sobre Lucro Líquido)
- **Alíquota**: 9% (Lucro Presumido/Real)

### IRPJ (Imposto de Renda Pessoa Jurídica)
- **Alíquota**: 15%
- **Adicional**: 2,5% sobre valor que exceder R$ 20.000/mês

## Impostos Novos (Pós-Reforma)

### CBS (Contribuição sobre Bens e Serviços)
- **Alíquota**: 10% (IVA federal)
- **Substitui**: PIS + COFINS
- **Cumulatividade**: Não cumulativo com desconto de créditos
- **Destinação**: União Federal

### IBS (Imposto sobre Bens e Serviços)
- **Alíquota**: 10% (IVA estadual/municipal)
- **Substitui**: ISS
- **Cumulatividade**: Não cumulativo
- **Destinação**: Estados + Municípios (via fundo de participação)

### CPP (Contribuição Patronal)
- **Alíquota**: 12% (a definir regulamentação)
- **Substitui**: Contribuições patronais (INSS, etc.)
- **Destinação**: Seguridade Social

## Cálculo dos Impostos

### Modelo Atual (ISS Simples)
```
valor_servico = 1000,00
aliquota_iss = 5%
valor_iss = 1000,00 * 0,05 = 50,00
```

### Modelo de Transição (ISS + CBS + IBS)
```
valor_servico = 1000,00

-- ISS (mantido até 2033)
aliquota_iss = 5%
valor_iss = 1000,00 * 0,05 = 50,00

-- CBS (federal)
aliquota_cbs = 10%
valor_cbs = 1000,00 * 0,10 = 100,00

-- IBS (estadual/municipal)
aliquota_ibs = 10%
valor_ibs = 1000,00 * 0,10 = 100,00

Total Impostos = 250,00
```

### Modelo Novo (CBS + IBS + CPP)
```
valor_servico = 1000,00

-- CBS (federal)
valor_cbs = 1000,00 * 0,10 = 100,00

-- IBS (estadual/municipal)
valor_ibs = 1000,00 * 0,10 = 100,00

-- CPP (patronal - nova)
valor_cpp = 1000,00 * 0,12 = 120,00

Total Impostos = 320,00
```

## Tratamento no Sistema

### Parâmetros por Município

O sistema deve permitir parametrização por código IBGE:

```sql
-- Exemplo: São Paulo (3550308)
INSERT INTO nfse_impostos_municipio 
  (municipio_id, iss_aliquota, cbs_ativo, ibs_ativo, cpp_ativo, fase_transicao)
VALUES 
  (3550308, 0.05, true, true, false, 'TRANSICAO');

-- Exemplo: Pato Branco (4118200)
INSERT INTO nfse_impostos_municipio 
  (municipio_id, iss_aliquota, cbs_ativo, ibs_ativo, cpp_ativo, fase_transicao)
VALUES 
  (4118200, 0.05, false, false, false, 'NAO_INICIADA');
```

### Cálculo Automático

O sistema deve:

1. **Identificar o município** pelo código IBGE do serviço
2. **Verificar a fase de transição** do município
3. **Selecionar os impostos ativos** baseado na fase
4. **Calcular cada imposto** com sua alíquota específica
5. **Exibir os valores** de forma clara para o usuário

### Códigos de Serviço (LC 116/2003)

Cada código de serviço pode ter regras específicas:

| Código | Serviço | ISS | CBS | IBS | PIS | COFINS |
|--------|---------|-----|-----|-----|-----|--------|
| 0101 | Construção civil | ✓ | - | - | - | - |
| 0601 | Engenharia | ✓ | - | - | - | - |
| 0701 | Auditoria | ✓ | ✓ | ✓ | ✓ | ✓ |
| 1401 | Consultoria | ✓ | ✓ | ✓ | ✓ | ✓ |

## Tabela de Parâmetros Recomendada

### Estrutura nfse_impostos

| Campo | Descrição | Exemplo |
|-------|-----------|---------|
| codigo | Código do imposto | ISS, CBS, IBS |
| nome | Nome completo | Imposto sobre Serviços |
| tipo_imposto | Categoria | ISS, CBS, IBS, PIS, COFINS |
| aliquota | Percentual | 0.05 (5%) |
| data_inicio | Início vigência | 2026-04-01 |
| fase | Fase transição | TRANSICAO, PLENA |

## Funcionalidades do Sistema

1. **Configuração por Município**: Permitir definir alíquotas por IBGE
2. **Fase de Transição**: Controlar quais impostos estão ativos
3. **Cálculo Automático**: Calcular todos os impostos aplicáveis
4. **Demonstrativo**: Mostrar breakdown detalhado dos valores
5. **Relatórios**: Gerar relatórios por período com diferentes impostos
6. **Alertas**: Notificar quando houver mudanças de fase

## Referências

- EC 132/2023 - Reforma Tributária
- LC 116/2003 - ISS
- Lei Complementar 161/2024 - regulamentação CBS/IBS
- Regulamento do IBS (a definir)