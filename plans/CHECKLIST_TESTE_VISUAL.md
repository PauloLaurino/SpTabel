# Checklist para Teste Visual do Módulo FUNARPEN

## 1. Variáveis de Ambiente
- [ ] Defina as variáveis obrigatórias do sistema:
  - CODTABEL_PAR
  - CNPJ_PAR
  - AMBIENTE_PAR
  - CONTEXTO_PAR
  - TOKENFUNARPEN_PAR
- [ ] Defina as variáveis de conexão com o banco MariaDB:
  - DB_URL=jdbc:mariadb://localhost:3306/spprot?useSSL=false&serverTimezone=UTC
  - DB_USER=root
  - DB_PASS=k15720

## 2. Banco de Dados
- [ ] MariaDB 10.8.8 rodando e acessível
- [ ] Base de dados `spprot` criada
- [ ] Tabelas `selos`, `selados`, `log_selos`, `log_selos_api` criadas conforme modelo

## 3. Backend Java (Tomcat/Webrun 5)
- [ ] Código Java (servlets/controllers/services) publicado em webapps/webrun5
- [ ] Endpoints funcionando:
  - POST /webrun5/maker/api/funarpen/selos/importar
  - GET  /webrun5/maker/api/funarpen/selos/monitor
  - GET  /webrun5/relatorios/selos/baixa/pdf?id_log=123

## 4. Frontend (Maker.Commons)
- [ ] Arquivo html/monitor_selos_funarpen.html copiado para Maker.Commons/html
- [ ] Dependências React (react.production.min.js, react-dom.production.min.js) em Maker.Commons/components/react
- [ ] Outras dependências (css, imagens) se necessário

## 5. Deploy
- [ ] Execute o script `deploy.ps1` para copiar arquivos estáticos para Maker.Commons
- [ ] Reinicie o Tomcat/Webrun 5 se necessário

## 6. Teste Visual
- [ ] Acesse o menu do Maker e abra o formulário React
- [ ] Teste importação de selos e monitoramento
- [ ] Teste download do PDF de auditoria

---
Se algum passo falhar, revise logs do Tomcat/Webrun 5 e do banco MariaDB.
