# Instruções de Uso - Scripts de Sanitização

## Pré-requisitos

1. **PowerShell 5.1 ou superior**
2. **MySQL Client instalado** (mysql.exe)
3. **Acesso ao banco de dados MariaDB** (IP: 100.102.13.23, porta: 3306)
4. **Credenciais do banco** (usuário: root, senha: k15720)

## Como Executar

### 1. Verificar Status dos Selos

Para verificar o status geral dos selos no período:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\verificar_status.ps1
```

**Saída esperada:**
```
========================================
  Verificação de Status dos Selos
========================================

[1/2] Consultando status geral...
   Período: 2026-03-01 até 2026-03-20
   Total de selados: 100
   Recepcionados com sucesso: 80
   Pendentes: 20
   Erros: 0

[2/2] Consultando selos pendentes...
   Total de selos pendentes (limitado a 10): 10

   Lista de selos pendentes:
   1. SFTN1.cG7Rb.Mwfwe-KRsZM.1122q (Status: PENDENTE, Data: 2026-03-04)
   2. SFTN1.cG7Rb.Mwfwe-KRsZM.1123q (Status: PENDENTE, Data: 2026-03-05)
   ...

========================================
  Verificação concluída!
========================================
```

### 2. Sanitizar um Selo Específico

Para sanitizar um selo específico:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\sanitizar_selados_completo.ps1 -Selo "SFTN1.cG7Rb.Mwfwe-KRsZM.1122q"
```

**Saída esperada:**
```
========================================
  Sanitização de selados.JSON
========================================

[1/6] Consultando dados do selo: SFTN1.cG7Rb.Mwfwe-KRsZM.1122q
   Selo encontrado: SFTN1.cG7Rb.Mwfwe-KRsZM.1122q
   IDAP: 0000000000000045305R00000000000000000000
   DOC_PAR: 09851329657
   Status atual: PENDENTE

[2/6] Consultando fin_reccab para solicitante...
   num_rec extraído do IDAP: 0000045305
   Solicitante encontrado: GILSON DA SILVEIRA PRINS
   CPF: 89751817900

[3/6] Processando JSON original...
   Código Tipo Ato: 403
   Tipo de consulta: 1
   Signatários encontrados: 1

[4/6] Construindo JSON sanitizado...

[5/6] Salvando arquivo selados.JSON12...
   Arquivo selados.JSON12 salvo com sucesso.

[6/6] Verificando status de recepção...
   Status atual: PENDENTE
   Selo ainda não foi recepcionado. Status: PENDENTE

========================================
  Sanitização concluída com sucesso!
========================================

Resumo:
  Selo: SFTN1.cG7Rb.Mwfwe-KRsZM.1122q
  Tipo Ato: 455 (Reconhecimento de Firma)
  Solicitante: GILSON DA SILVEIRA PRINS
  Signatários: 1
  Arquivo gerado: selados.JSON12
```

### 3. Sanitizar em Lote

Para sanitizar todos os selos pendentes no período:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\sanitizar_lote_selados.ps1
```

**Saída esperada:**
```
========================================
  Sanitização em Lote de selados.JSON
========================================

[1/4] Consultando selos pendentes...
   Período: 2026-03-01 até 2026-03-20
   Total de selos pendentes: 20

[2/4] Processando selos...
   Processando 1/20 : SFTN1.cG7Rb.Mwfwe-KRsZM.1122q
      OK - Arquivo gerado: selados_SFTN1.cG7Rb.Mwfwe-KRsZM.1122q.JSON12
   Processando 2/20 : SFTN1.cG7Rb.Mwfwe-KRsZM.1123q
      OK - Arquivo gerado: selados_SFTN1.cG7Rb.Mwfwe-KRsZM.1123q.JSON12
   ...

[3/4] Resumo do processamento:
   Total de selos: 20
   Sucessos: 20
   Erros: 0

[4/4] Verificando status de recepção...
   Total de selados no período: 100
   Recepcionados com sucesso: 80
   Pendentes: 20

========================================
  Processamento em lote concluído!
========================================
```

### 4. Sanitizar e Enviar para o FUNARPEN

Para sanitizar um selo e enviar para o FUNARPEN:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\sanitizar_e_enviar.ps1 -Selo "SFTN1.cG7Rb.Mwfwe-KRsZM.1122q"
```

**Saída esperada:**
```
========================================
  Sanitização e Envio para FUNARPEN
========================================

[1/7] Consultando dados do selo: SFTN1.cG7Rb.Mwfwe-KRsZM.1122q
   Selo encontrado: SFTN1.cG7Rb.Mwfwe-KRsZM.1122q
   IDAP: 0000000000000045305R00000000000000000000
   DOC_PAR: 09851329657
   Status atual: PENDENTE

[2/7] Consultando fin_reccab para solicitante...
   num_rec extraído do IDAP: 0000045305
   Solicitante encontrado: GILSON DA SILVEIRA PRINS
   CPF: 89751817900

[3/7] Processando JSON original...
   Código Tipo Ato: 403
   Tipo de consulta: 1
   Signatários encontrados: 1

[4/7] Construindo JSON sanitizado...

[5/7] Salvando arquivo selados.JSON12...
   Arquivo selados.JSON12 salvo com sucesso.

[6/7] Enviando para o FUNARPEN...
   Resposta do FUNARPEN:
   {
     "status": "success",
     "message": "Selo recepcionado com sucesso"
   }
   Status atualizado para SUCESSO no banco de dados.

[7/7] Verificando status final...
   Status final: SUCESSO

========================================
  Processo concluído!
========================================

Resumo:
  Selo: SFTN1.cG7Rb.Mwfwe-KRsZM.1122q
  Tipo Ato: 455 (Reconhecimento de Firma)
  Solicitante: GILSON DA SILVEIRA PRINS
  Signatários: 1
  Arquivo gerado: selados.JSON12
  Status final: SUCESSO
```

### 5. Sanitizar em Lote e Enviar para o FUNARPEN

Para sanitizar todos os selos pendentes e enviar para o FUNARPEN:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\sanitizar_lote_e_enviar.ps1
```

**Saída esperada:**
```
========================================
  Sanitização em Lote e Envio FUNARPEN
========================================

[1/6] Consultando parâmetros...
   Ambiente: prod
   Token: eyJhbGciOiJIUzUxMiJ9...

[2/6] Consultando selos pendentes...
   Período: 2026-03-01 até 2026-03-20
   Total de selos pendentes: 20

[3/6] Processando selos...
   Processando 1/20 : SFTN1.cG7Rb.Mwfwe-KRsZM.1122q
      OK - Enviado e atualizado
   Processando 2/20 : SFTN1.cG7Rb.Mwfwe-KRsZM.1123q
      OK - Enviado e atualizado
   ...

[4/6] Resumo do processamento:
   Total de selos: 20
   Sucessos: 20
   Erros: 0

[5/6] Verificando status geral...
   Total de selados no período: 100
   Recepcionados com sucesso: 100
   Pendentes: 0

[6/6] Processamento concluído!

========================================
  Processamento em lote concluído!
========================================
```

### 6. Executar Sanitização Completa

Para executar sanitização completa (verificação + sanitização + verificação):

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\executar_sanitizacao_completa.ps1
```

**Saída esperada:**
```
========================================
  Execução de Sanitização Completa
========================================

[1/4] Verificando status inicial...
   Período: 2026-03-01 até 2026-03-20
   Total de selados: 100
   Recepcionados com sucesso: 80
   Pendentes: 20

[2/4] Executando sanitização em lote...
   ... (saída do sanitizar_lote_selados.ps1)

[3/4] Verificando status após sanitização...
   Período: 2026-03-01 até 2026-03-20
   Total de selados: 100
   Recepcionados com sucesso: 100
   Pendentes: 0

[4/4] Resumo final:
   Processo de sanitização concluído.
   Verifique os arquivos JSON12 gerados.

========================================
  Execução concluída!
========================================
```

## Troubleshooting

### Erro: "mysql não é reconhecido como comando"
- Instale o MySQL Client ou adicione ao PATH
- Baixe em: https://dev.mysql.com/downloads/mysql/

### Erro: "Acesso negado ao banco de dados"
- Verifique se o IP 100.102.13.23 está acessível
- Confirme se a porta 3306 está aberta
- Valide as credenciais (root/k15720)

### Erro: "Selo não encontrado"
- Confirme se o selo existe na tabela `selos`
- Verifique se o selo está vinculado a um registro em `selados`

### Erro: "Solicitante não encontrado"
- Verifique se o num_rec extraído do IDAP existe em `fin_reccab`
- Confirme se os campos `nomecli_rec` e `cpfcli_rec` estão preenchidos

### Erro: "Signatários não encontrados"
- Verifique se `ListaPropriedades` contém campos de envolvidos
- Confirme se os campos de nome e documento estão preenchidos corretamente

### Erro: "Erro ao enviar para o FUNARPEN"
- Verifique se o token está válido
- Confirme se o certificado PFX está configurado corretamente
- Valide se a URL do endpoint está acessível

## Referências

- [`README_SANITIZACAO.md`](README_SANITIZACAO.md) - Documentação de sanitização
- [`README_SCRIPTS_SANITIZACAO.md`](README_SCRIPTS_SANITIZACAO.md) - Documentação dos scripts
- [`RESUMO_SCRIPTS_CRIADOS.md`](RESUMO_SCRIPTS_CRIADOS.md) - Resumo dos scripts criados
- [`CHECKLIST_SCRIPTS.md`](CHECKLIST_SCRIPTS.md) - Checklist dos scripts
