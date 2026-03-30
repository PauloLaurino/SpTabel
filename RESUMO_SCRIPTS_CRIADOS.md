# Resumo - Scripts de Sanitização Criados

## Data de Criação
28/03/2026

## Objetivo
Criar scripts PowerShell para sanitização de selados.JSON e geração de selados.JSON12, conforme as regras de negócio do Tabelionato de Notas.

## Arquivos Criados

### Scripts PowerShell

1. **sanitizar_selados_completo.ps1**
   - Sanitização de um selo específico
   - Consulta banco de dados
   - Extrai solicitante de fin_reccab
   - Extrai signatários de ListaPropriedades
   - Gera arquivo selados.JSON12

2. **sanitizar_lote_selados.ps1**
   - Sanitização em lote de selados
   - Processa período 2026-03-01 até 2026-03-20
   - Gera arquivo JSON12 para cada selo

3. **sanitizar_e_enviar.ps1**
   - Sanitiza um selo e envia para o FUNARPEN
   - Usa mTLS com certificado PFX
   - Atualiza status no banco de dados

4. **sanitizar_lote_e_enviar.ps1**
   - Sanitização em lote e envio para o FUNARPEN
   - Processa período 2026-03-01 até 2026-03-20
   - Envia cada selo para o FUNARPEN
   - Atualiza status no banco de dados

5. **verificar_status.ps1**
   - Verifica status dos selos
   - Exibe total de selos, sucessos, pendentes e erros
   - Lista os primeiros 10 selos pendentes

6. **executar_sanitizacao_completa.ps1**
   - Executa sanitização completa
   - Verifica status inicial
   - Executa sanitização em lote
   - Verifica status final

### Arquivos SQL

1. **consultar_selos_pendentes.sql**
   - Consulta selos pendentes de sanitização
   - Período: 2026-03-01 até 2026-03-20

2. **verificar_status_selos.sql**
   - Verifica status geral dos selos
   - Período: 2026-03-01 até 2026-03-20

### Arquivos de Documentação

1. **README_SANITIZACAO.md**
   - Documentação de sanitização
   - Regras de sanitização
   - Estrutura JSON esperada
   - Troubleshooting

2. **README_SCRIPTS_SANITIZACAO.md**
   - Documentação dos scripts
   - Parâmetros de cada script
   - Funcionalidades
   - Regras de sanitização
   - Troubleshooting

3. **selados_exemplo_sanitizado.JSON12**
   - Arquivo JSON de exemplo
   - Estrutura esperada após sanitização

## Regras de Sanitização Implementadas

### 1. Conexão ao Banco de Dados
- IP: 100.102.13.23
- Porta: 3306
- Database: sptabel
- Usuário: root
- Senha: k15720

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

### 6. Estrutura de Verbas
Manter a estrutura e valores originais do objeto `verbas`:
- emolumentos
- vrcExt
- funrejus
- iss
- fundep
- funarpen
- distribuidor
- valorAdicional

## Como Usar

### Para sanitizar um selo específico:
```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\sanitizar_selados_completo.ps1
```

### Para sanitizar em lote:
```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\sanitizar_lote_selados.ps1
```

### Para sanitizar e enviar para o FUNARPEN:
```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\sanitizar_e_enviar.ps1
```

### Para sanitizar em lote e enviar para o FUNARPEN:
```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\sanitizar_lote_e_enviar.ps1
```

### Para verificar status:
```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\verificar_status.ps1
```

### Para executar sanitização completa:
```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\executar_sanitizacao_completa.ps1
```

## Próximos Passos

1. Executar os scripts para processar os selos pendentes
2. Verificar se os arquivos JSON12 foram gerados corretamente
3. Enviar os selos para o FUNARPEN
4. Monitorar o status de recepção
5. Ajustar os scripts conforme necessário

## Referências

- [`README_MANUAL.md`](README_MANUAL.md) - Manual técnico do JSON
- [`README_NOTAS.md`](README_NOTAS.md) - Documentação do projeto NOTAS
- [`SeloJsonSanitizerNotas.java`](src/main/java/com/selador/util/SeloJsonSanitizerNotas.java) - Classe Java de sanitização
- [`README_SANITIZACAO.md`](README_SANITIZACAO.md) - Documentação de sanitização
- [`README_SCRIPTS_SANITIZACAO.md`](README_SCRIPTS_SANITIZACAO.md) - Documentação dos scripts
