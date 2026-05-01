# Arquitetura de Integração Maker-React (Funarpen)

> [!IMPORTANT]
> **ESTE DOCUMENTO É A ÚNICA FONTE DE VERDADE PARA A INTEGRAÇÃO.**
> **NÃO ALTERAR A ESTRUTURA DO WEBRUN (main.jsp, etc.).**

## 1. Mecanismo de Injeção (Loader)
A injeção do dashboard e das pontes de navegação ocorre via Maker Studio, no evento **AO ENTRAR** do Sidebar do template principal.

**Script de Injeção (Nativo do Maker):**
```javascript
(function () {
  try {
    var urlParams = new URLSearchParams(window.location.search);
    var sys = urlParams.get('sys') || '';
    var contextPath = '/gerencial'; 
    var assetPath = contextPath + '/html/assets/sprGraficoEstoque.js?sys=' + sys + '&v=' + Date.now(); 
    var attr = 'data-spr-loader';

    function inject(doc) {
      if (doc.querySelector('script[' + attr + ']')) return;
      var s = doc.createElement('script');
      s.setAttribute(attr, assetPath);
      s.src = assetPath;
      s.async = true;
      doc.head.appendChild(s);
    }

    inject(document);
    if (window.top !== window) inject(window.top.document);

    function tryCall(attempt) {
      var targetWin = (typeof window.sprGraficoEstoque === 'function') ? window : 
                     (window.parent && typeof window.parent.sprGraficoEstoque === 'function') ? window.parent :
                     (window.top && typeof window.top.sprGraficoEstoque === 'function') ? window.top : null;
      if (targetWin) { targetWin.sprGraficoEstoque(); return; }
      if (attempt < 40) setTimeout(function () { tryCall(attempt + 1); }, 250);
    }
    setTimeout(function () { tryCall(0); }, 500);
  } catch (e) { console.error('Erro no loader:', e); }
})();
```

## 2. Ponte de Navegação (sprURLOpenFrame)
Responsável por renderizar Micro-frontends React dentro de Molduras Maker.
- **Localização**: Definida dentro de `/gerencial/html/assets/sprGraficoEstoque.js`.
- **Inteligência**: Detecta automaticamente contextPath, herda `sys` e `database`, e gerencia o redimensionamento dinâmico da Moldura via `ResizeObserver`.

## 3. Rotas React Padronizadas
- **Selos Utilizados**: `/gerencial/frontend/index.html#/selos`
- **Monitor FUNARPEN**: `/gerencial/frontend/index.html#/pedidos`
- **Monitor NFS-e**: `/gerencial/frontend/index.html#/nfse`
- **Assinador Digital**: `/gerencial/servico-assinatura/index.html`

## 4. Estrutura de Arquivos Centralizada (Gerencial)
- **Script Central**: `C:\Desenvolvimento\Seprocom\Gerencial\src\main\webapp\html\assets\sprGraficoEstoque.js`
- **URL de Acesso**: `http://localhost:8049/gerencial/html/assets/sprGraficoEstoque.js`

## 5. Fluxo de Dados (API)
O script central no Gerencial consome dados do projeto **Notas** via:
- **Servlet**: `/notas/FunarpenSelosServlet?action=estoque&sys=TTB`
- **Certificado**: Parte do mesmo payload JSON retornado pelo Servlet.
