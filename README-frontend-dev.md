Frontend dev server (BrowserSync)

Objetivo:
- Servir a pasta `src/main/webapp` localmente e recarregar o navegador automaticamente quando CSS/HTML/JS mudarem.

Instalação (Windows PowerShell):

1. Certifique-se de ter Node.js e npm instalados: `node -v` e `npm -v`.
2. Na raiz do projeto (`c:\Selador`) execute:

```powershell
npm install
```

Executar o servidor de desenvolvimento:

```powershell
npm run dev
```

- O BrowserSync abrirá uma aba apontando para `views/selador.html` e recarregará automaticamente ao salvar alterações em `src/main/webapp/assets/css`, `src/main/webapp/views` ou `src/main/webapp/assets/js`.

Observações:
- Se preferir não instalar dependências, pode usar `npx browser-sync ...` com o mesmo comando do script.
- Se você roda a app por servlet/container (Tomcat), mantenha esse fluxo; o BrowserSync serve estático — para backend dinâmico use `browser-sync` proxy (posso ajudar a configurar se quiser).
