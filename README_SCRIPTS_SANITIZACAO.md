# README - Scripts de Sanitização de Selados

## Visão Geral

Este documento descreve os scripts PowerShell criados para sanitização de selados.JSON e geração de selados.JSON12, conforme as regras de negócio do Tabelionato de Notas.

## Scripts Disponíveis

### 1. `sanitizar_selados_completo.ps1`

Script para sanitização de um selo específico.

**Parâmetros:**
- `-Selo`: Número do selo digital (padrão: "SFTN1.cG7Rb.Mwfwe-KRsZM.1122q")
- `-DBHost`: IP do banco de dados (padrão: "100.102.13.23")
- `-DBPort`: Porta do banco de dados (padrão: "3306")
- `-DBName`: Nome do banco de dados (padrão: "sptabel")
- `-DBUser`: Usuário do banco de dados (padrão: "root")
- `-DBPass`: Senha do banco de dados (padrão: "k15720")

**Uso:**
```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\sanitizar_selados_completo.ps1
```

**Funcionalidades:**
- Conecta ao MariaDB e consulta dados do selo
- Extrai num_rec do IDAP (posição 11 com 10 caracteres)
- Consulta fin_reccab para obter solicitante
- Consulta parametros para obter DOC_PAR
- Extrai signatários de ListaPropriedades
- Constrói JSON sanitizado conforme regras de negócio
- Gera arquivo selados.JSON12

---

### 2. `sanitizar_lote_selados.ps1`

Script para sanitização em lote de selados.

**Parâmetros:**
- `-DBHost`: IP do banco de dados (padrão: "100.102.13.23")
- `-DBPort`: Porta do banco de dados (padrão: "3306")
- `-DBName`: Nome do banco de dados (padrão: "sptabel")
- `-DBUser`: Usuário do banco de dados (padrão: "root")
- `-DBPass`: Senha do banco de dados (padrão: "k15720")
- `-DataInicio`: Data de início do período (padrão: "2026-03-01")
- `-DataFim`: Data de fim do período (padrão: "2026-03-20")

**Uso:**
```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\sanitizar_lote_selados.ps1
```

**Funcionalidades:**
- Consulta selos pendentes no período especificado
- Processa cada selo individualmente
- Gera arquivo JSON12 para cada selo
- Exibe resumo do processamento

---

### 3. `sanitizar_e_enviar.ps1`

Script para sanitizar um selo e enviar para o FUNARPEN.

**Parâmetros:**
- `-Selo`: Número do selo digital (padrão: "SFTN1.cG7Rb.Mwfwe-KRsZM.1122q")
- `-DBHost`: IP do banco de dados (padrão: "100.102.13.23")
- `-DBPort`: Porta do banco de dados (padrão: "3306")
- `-DBName`: Nome do banco de dados (padrão: "sptabel")
- `-DBUser`: Usuário do banco de dados (padrão: "root")
- `-DBPass`: Senha do banco de dados (padrão: "k15720")
- `-Url`: URL do endpoint (padrão: "https://dev-v11plus.funarpen.com.br/selos/recepcao")

**Uso:**
```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\sanitizar_e_enviar.ps1
```

**Funcionalidades:**
- Consulta dados do selo e parâmetros
- Extrai solicitante de fin_reccab
- Extrai signatários de ListaPropriedades
- Constrói JSON sanitizado
- Gera arquivo selados.JSON12
- Envia para o FUNARPEN via mTLS
- Atualiza status no banco de dados

---

### 4. `sanitizar_lote_e_enviar.ps1`

Script para sanitização em lote e envio para o FUNARPEN.

**Parâmetros:**
- `-DBHost`: IP do banco de dados (padrão: "100.102.13.23")
- `-DBPort`: Porta do banco de dados (padrão: "3306")
- `-DBName`: Nome do banco de dados (padrão: "sptabel")
- `-DBUser`: Usuário do banco de dados (padrão: "root")
- `-DBPass`: Senha do banco de dados (padrão: "k15720")
- `-DataInicio`: Data de início do período (padrão: "2026-03-01")
- `-DataFim`: Data de fim do período (padrão: "2026-03-20")
- `-Url`: URL do endpoint (padrão: "https://dev-v11plus.funarpen.com.br/selos/recepcao")

**Uso:**
```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\sanitizar_lote_e_enviar.ps1
```

**Funcionalidades:**
- Consulta parâmetros do banco
- Consulta selos pendentes no período
- Processa cada selo individualmente
- Gera arquivo JSON12 para cada selo
- Envia cada selo para o FUNARPEN
- Atualiza status no banco de dados
- Exibe resumo do processamento

---

### 5. `verificar_status.ps1`

Script para verificar status dos selos.

**Parâmetros:**
- `-DBHost`: IP do banco de dados (padrão: "100.102.13.23")
- `-DBPort`: Porta do banco de dados (padrão: "3306")
- `-DBName`: Nome do banco de dados (padrão: "sptabel")
- `-DBUser`: Usuário do banco de dados (padrão: "root")
- `-DBPass`: Senha do banco de dados (padrão: "k15720")
- `-DataInicio`: Data de início do período (padrão: "2026-03-01")
- `-DataFim`: Data de fim do período (padrão: "2026-03-20")

**Uso:**
```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\verificar_status.ps1
```

**Funcionalidades:**
- Consulta status geral dos selos
- Exibe total de selos, sucessos, pendentes e erros
- Lista os primeiros 10 selos pendentes

---

### 6. `executar_sanitizacao_completa.ps1`

Script para executar sanitização completa.

**Parâmetros:**
- `-DBHost`: IP do banco de dados (padrão: "100.102.13.23")
- `-DBPort`: Porta do banco de dados (padrão: "3306")
- `-DBName`: Nome do banco de dados (padrão: "sptabel")
- `-DBUser`: Usuário do banco de dados (padrão: "root")
- `-DBPass`: Senha do banco de dados (padrão: "k15720")
- `-DataInicio`: Data de início do período (padrão: "2026-03-01")
- `-DataFim`: Data de fim do período (padrão: "2026-03-20")

**Uso:**
```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\executar_sanitizacao_completa.ps1
```

**Funcionalidades:**
- Verifica status inicial
- Executa sanitização em lote
- Verifica status final
- Exibe resumo do processamento

---

## Arquivos SQL

### 1. `consultar_selos_pendentes.sql`

Script SQL para consultar selos pendentes de sanitização.

**Uso:**
```bash
mysql -h 100.102.13.23 -P 3306 -u root -pk15720 sptabel < consultar_selos_pendentes.sql
```

### 2. `verificar_status_selos.sql`

Script SQL para verificar status geral dos selos.

**Uso:**
```bash
mysql -h 100.102.13.23 -P 3306 -u root -pk15720 sptabel < verificar_status_selos.sql
```

---

## Arquivos de Exemplo

### 1. `selados_exemplo_sanitizado.JSON12`

Arquivo JSON de exemplo mostrando a estrutura esperada após sanitização.

---

## Regras de Sanitização

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

---

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

---

## Troubleshooting

### Erro de conexão ao banco
- Verifique se o IP 100.102.13.23 está acessível
- Confirme se a porta 3306 está aberta
- Valide as credenciais (root/k15720)

### Selo não encontrado
- Confirme se o selo existe na tabela `selos`
- Verifique se o selo está vinculado a um registro em `selados`

### Solicitante não encontrado
- Verifique se o num_rec extraído do IDAP existe em `fin_reccab`
- Confirme se os campos `nomecli_rec` e `cpfcli_rec` estão preenchidos

### Signatários não encontrados
- Verifique se `ListaPropriedades` contém campos de envolvidos
- Confirme se os campos de nome e documento estão preenchidos corretamente

### Erro ao enviar para o FUNARPEN
- Verifique se o token está válido
- Confirme se o certificado PFX está configurado corretamente
- Valide se a URL do endpoint está acessível

---

## Referências

- [`README_MANUAL.md`](README_MANUAL.md) - Manual técnico do JSON
- [`README_NOTAS.md`](README_NOTAS.md) - Documentação do projeto NOTAS
- [`SeloJsonSanitizerNotas.java`](src/main/java/com/selador/util/SeloJsonSanitizerNotas.java) - Classe Java de sanitização
- [`README_SANITIZACAO.md`](README_SANITIZACAO.md) - Documentação de sanitização
