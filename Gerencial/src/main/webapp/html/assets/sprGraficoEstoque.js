/**
 * Script Central de Integracao Funarpen (Gerencial)
 * Fonte unica de verdade - NAO duplicar logica.
 */
(function () {
  if (window._spr_widget_loaded) return;
  window._spr_widget_loaded = true;

  'use strict';

  var WIDGET_ID = 'spr-grafico-estoque-root';
  var STYLE_ID  = 'spr-grafico-estoque-styles';

  if (window.console) console.log('[Seprocom] Script de Integracao Ativo');

  // =====================================================================
  // 1. PONTE DE NAVEGACAO - sprURLOpenFrame
  /**
   * Versao Evoluida do sprURLOpenFrame (Baseada no Maker 5 + Melhorias de Contexto e Layout)
   * 
   * 1. Detecta e ajusta o contexto da aplicacao automaticamente (/gerencial, /notas, etc.).
   * 2. Propaga automaticamente os parametros 'sys' e 'database' (Notas/Protesto).
   * 3. Gerencia o redimensionamento dinamico da moldura.
   * 4. Garante compatibilidade com diferentes versoes do SmartClient/Webrun.
   */
  // =====================================================================
  window.sprURLOpenFrame = function (form, componentName, url) {
    if (!componentName || componentName.trim() === '') {
       if (window.console) console.warn('[sprURLOpenFrame] Nome do componente nao informado. Tentando fallback para "Moldura"');
       componentName = 'Moldura';
    }
    if (window.console) console.log('[sprURLOpenFrame] Solicitado:', componentName, 'URL:', url);
    try {
      if (!url) return null;

      // 1: Estrategia via controller do Maker
      var comp = null;
      var win = window.top || window;
      if (typeof win.controller !== 'undefined' && win.controller && typeof win.controller.getElementById === 'function') {
        comp = win.controller.getElementById(componentName, form);
        if (comp && typeof comp.openUrlInFrame === 'function') {
          if (window.console) console.log('[sprURLOpenFrame] Chamando openUrlInFrame no controller');
          comp.openUrlInFrame(url, true, false);
        }
      }

      // 2: Estrategia via SmartClient (isc) e busca no DOM
      function findRealIframe(name) {
        try {
          var isc = win.isc;
          if (typeof isc !== 'undefined' && isc.Canvas) {
            var iscComp = isc.Canvas.getById(name) || isc.Canvas.getCanvas(name);
            if (iscComp) {
              var handle = iscComp.getHandle ? iscComp.getHandle() : null;
              if (handle) {
                var childIframes = handle.querySelectorAll('iframe');
                if (childIframes.length > 0) return childIframes[0];
              }
              if (typeof iscComp.getIFrameHandle === 'function') return iscComp.getIFrameHandle();
            }
          }
        } catch(e) {}

        var docs = [document];
        try { if (win.document) docs.push(win.document); } catch(e){}
        try { if (window.parent && window.parent.document) docs.push(window.parent.document); } catch(e){}

        var patterns = [name, name + '_iFrame', name + '_frame', name.toLowerCase()];
        for (var d = 0; d < docs.length; d++) {
          var doc = docs[d];
          for (var p = 0; p < patterns.length; p++) {
            var el = doc.getElementById(patterns[p]);
            if (el && el.tagName) {
              var tag = el.tagName.toLowerCase();
              if (tag === 'iframe') return el;
              if (tag === 'div' || tag === 'span') {
                if (window.console) console.log('[sprURLOpenFrame] Alvo e uma DIV. Verificando/Injetando iframe...');
                var existing = el.querySelector('iframe');
                if (existing) return existing;
                var newIframe = doc.createElement('iframe');
                newIframe.id = patterns[p] + '_gen_iframe';
                newIframe.style.width = '100%'; newIframe.style.height = '100%';
                newIframe.style.border = 'none'; newIframe.setAttribute('frameborder', '0');
                el.style.overflow = 'hidden'; el.appendChild(newIframe);
                return newIframe;
              }
            }
          }
        }
        return null;
      }

      var attempts = 0;
      function tryLoad() {
        var iframe = findRealIframe(componentName);
        if (iframe) { 
           if (window.console) console.log('[sprURLOpenFrame] iframe real encontrado:', iframe.id || iframe.name);
           iframe.src = url; 
           return; 
        }
        if (++attempts < 15) setTimeout(tryLoad, 200);
      }
      setTimeout(tryLoad, 100);

      return comp;
    } catch (e) {
      if (window.console) console.error('[sprURLOpenFrame] Erro:', e);
      return null;
    }
  };

  // Alias para compatibilidade com chamadas antigas
  window.sprURLFrameOpen = window.sprURLOpenFrame;
  window.sprURLframeOpen = window.sprURLOpenFrame;

  // =====================================================================
  // AUTO-PROPAGACAO: Injeta sprURLOpenFrame em TODOS os iframes
  // =====================================================================
  var _sprFn = window.sprURLOpenFrame;

  function _injectIntoFrame(frameWin) {
    try {
      if (typeof frameWin.sprURLOpenFrame !== 'function') {
        frameWin.sprURLOpenFrame = _sprFn;
        frameWin.sprURLFrameOpen = _sprFn;
        frameWin.sprURLframeOpen = _sprFn;
      }
    } catch (e) { /* cross-origin */ }
  }

  function _propagateAll(win) {
    try {
      var iframes = win.document.getElementsByTagName('iframe');
      for (var i = 0; i < iframes.length; i++) {
        try {
          var fw = iframes[i].contentWindow;
          if (fw) {
            _injectIntoFrame(fw);
            _propagateAll(fw);
          }
        } catch (e) {}
      }
    } catch (e) {}
  }

  setInterval(function () { _propagateAll(window.top || window); }, 1000);
  _propagateAll(window.top || window);

  // =====================================================================
  // 2. DASHBOARD SIDEBAR - sprGraficoEstoque
  // =====================================================================
  window.sprGraficoEstoque = function () {
    var doc = resolveMenuDoc();
    if (doc && !doc.getElementById(WIDGET_ID)) {
      buildWidget(doc);
    }
  };

  function resolveMenuDoc() {
    var candidates = [window];
    try { if (window.parent && window.parent !== window) candidates.push(window.parent); } catch (e) {}
    try { if (window.top    && window.top    !== window) candidates.push(window.top);    } catch (e) {}
    for (var i = 0; i < candidates.length; i++) {
      try {
        var d = candidates[i].document;
        if (d.getElementById('MenuPrincipal') || d.getElementById('Menu') || d.getElementById('Sidebar')) return d;
      } catch (e) {}
    }
    return null;
  }

  function ensureStyles(doc) {
    if (doc.getElementById(STYLE_ID)) return;
    var st = doc.createElement('style');
    st.id = STYLE_ID;
    st.textContent = [
      '.spr-graf-card{background:#2d2d2d;border:1px solid #444;padding:8px 10px;border-radius:6px;margin-bottom:6px;font-family:Arial,sans-serif;color:#fff;box-shadow:0 2px 4px rgba(0,0,0,0.2)}',
      '.spr-graf-title{font-size:11px;color:#aaa;font-weight:700;text-transform:uppercase;margin-bottom:6px;letter-spacing:0.5px}',
      '.spr-bar{height:8px;background:#444;border-radius:4px;overflow:hidden;margin:2px 0 5px}',
      '.spr-bar-fill{height:100%;background:#3b82f6;border-radius:4px;transition:width .6s ease}',
      '.spr-bar-label{display:flex;justify-content:space-between;font-size:11px;font-weight:bold;color:#eee}',
      '.spr-cert-valid{color:#4ade80;font-weight:700;font-size:12px}',
      '.spr-cert-invalid{color:#f87171;font-weight:700;font-size:12px}',
      '.spr-cert-box{border-left:3px solid #3b82f6;padding:6px 8px;border-radius:4px;background:rgba(59,130,246,0.1)}'
    ].join('');
    (doc.head || doc.documentElement).appendChild(st);
  }

  function buildWidget(doc) {
    ensureStyles(doc);
    var menu = doc.getElementById('MenuPrincipal') || doc.getElementById('Menu') || doc.getElementById('Sidebar');
    if (!menu) return;

    var root = doc.createElement('div');
    root.id = WIDGET_ID;
    root.style.cssText = 'position:relative;display:block;margin:20px 10px 10px;width:calc(100% - 20px);z-index:100;padding:0;clear:both;box-sizing:border-box';
    menu.appendChild(root);

    renderCards(doc, root);
  }

  function renderCards(doc, root) {
    var grafCard = doc.createElement('div');
    grafCard.className = 'spr-graf-card';
    grafCard.innerHTML = '<div class="spr-graf-title">Estoque de Selos</div><div id="spr-graf-content"><div style="color:#888;font-size:11px">Carregando...</div></div>';
    root.appendChild(grafCard);

    var certCard = doc.createElement('div');
    certCard.className = 'spr-graf-card';
    certCard.innerHTML = '<div class="spr-graf-title">Certificado Digital</div><div id="spr-cert-body" style="color:#888;font-size:11px">Carregando...</div>';
    root.appendChild(certCard);

    var resolveApi = function (paths, callback) {
      var tryNext = function (index) {
        if (index >= paths.length) return;
        fetch(paths[index])
          .then(function (r) {
            if (!r.ok) throw new Error(r.status);
            return r.json();
          })
          .then(function (data) { callback(data); })
          .catch(function () { tryNext(index + 1); });
      };
      tryNext(0);
    };

    var update = function () {
      var params = new URLSearchParams(window.location.search);
      var sys = params.get('sys') || 'TTB';

      resolveApi([
        '/notas/FunarpenSelosServlet?action=estoque&sys=' + sys,
        '/notas/maker/api/notas/selos/cards/estoque?sys=' + sys,
        '/sptabel/maker/api/notas/selos/cards/estoque?sys=' + sys
      ], function (d) {
        var content = doc.getElementById('spr-graf-content');
        if (content && (d.estoque || Array.isArray(d))) {
          paintBars(doc, content, d.estoque || d);
        }
      });

      resolveApi([
        '/notas/maker/api/funarpen/certificado?sys=' + sys,
        '/sptabel/maker/api/funarpen/certificado?sys=' + sys
      ], function (d) {
        var body = doc.getElementById('spr-cert-body');
        if (body) paintCert(doc, body, d);
      });
    };

    update();
    setInterval(update, 60000);
  }

  function paintBars(doc, container, data) {
    container.innerHTML = '';
    var items = Array.isArray(data) ? data : [];
    items.slice(0, 7).forEach(function (item) {
      var k   = item.tiposelo || item.label || 'Selo';
      var v   = item.total || item.valor || 0;
      var pct = Math.min(100, Math.round((v / 500) * 100));
      var cor = v >= 100 ? '#16a34a' : v >= 50 ? '#f59e0b' : '#ef4444';
      container.innerHTML +=
        '<div class="spr-bar-label"><span>' + k + '</span><span>' + v + '</span></div>' +
        '<div class="spr-bar"><div class="spr-bar-fill" style="background:' + cor + ';width:' + pct + '%"></div></div>';
    });
  }

  function paintCert(doc, body, info) {
    body.innerHTML = '';
    var box = doc.createElement('div');
    box.className = 'spr-cert-box';
    if (info && info.valid) {
      box.innerHTML = '<div class="spr-cert-valid">Certificado Valido</div>';
      if (info.notAfter) {
        var d = new Date(info.notAfter);
        box.innerHTML += '<div style="color:#fff;font-size:10px;margin-top:3px">Vence: ' + d.toLocaleDateString('pt-BR') + '</div>';
      }
    } else {
      box.style.borderColor = '#ef4444';
      box.style.background = 'rgba(239,68,68,0.1)';
      box.innerHTML = '<div class="spr-cert-invalid">Certificado Invalido</div>';
    }
    body.appendChild(box);
  }

  if (document.readyState === 'complete') {
    window.sprGraficoEstoque();
  } else {
    window.addEventListener('load', function () {
      setTimeout(window.sprGraficoEstoque, 1000);
    });
  }
})();
