(function () {
  if (window._spr_widget_loaded) return;
  window._spr_widget_loaded = true;

  // Não renderizar se estiver dentro de uma página que já é um dashboard
  if (window.location.pathname.indexOf('selos_utilizados.html') !== -1) {
    console.log('[Estoque] Dashboard detectado. Ignorando injeção do widget lateral.');
    return;
  }
  'use strict';

  var WIDGET_ID   = 'spr-grafico-estoque-root';
  var STYLE_ID    = 'spr-grafico-estoque-styles';

  function resolveMenuDoc() {
    var candidates = [window];
    try { if (window.parent && window.parent !== window) candidates.push(window.parent); } catch (e) {}
    try { if (window.top   && window.top   !== window && window.top !== window.parent) candidates.push(window.top); } catch (e) {}
    for (var i = 0; i < candidates.length; i++) {
      try {
        var d = candidates[i].document;
        if (d.getElementById('MenuPrincipal') || d.getElementById('Menu') || d.getElementById('Sidebar')) return d;
      } catch (e) {}
    }
    return null;
  }

  function widgetExists() {
    var wins = [window];
    try { if (window.parent && window.parent !== window) wins.push(window.parent); } catch (e) {}
    try { if (window.top   && window.top   !== window) wins.push(window.top); } catch (e) {}
    for (var i = 0; i < wins.length; i++) {
      try { if (wins[i].document.getElementById(WIDGET_ID)) return true; } catch (e) {}
    }
    return false;
  }

  function ensureStyles(doc) {
    if (doc.getElementById(STYLE_ID)) return;
    var st = doc.createElement('style');
    st.id = STYLE_ID;
    st.textContent = [
      '.spr-graf-card{background:#2d2d2d;border:1px solid #444;padding:8px 10px;border-radius:6px;box-shadow:0 1px 4px rgba(0,0,0,0.3);margin-bottom:6px;font-family:Arial,sans-serif;font-size:12px}',
      '.spr-graf-title{font-size:11px;color:#fff;font-weight:700;text-transform:uppercase;letter-spacing:.8px;margin-bottom:6px}',
      '.spr-bar{height:8px;background:#444;border-radius:4px;overflow:hidden;margin:2px 0 5px}',
      '.spr-bar-fill{height:100%;background:#3b82f6;border-radius:4px;transition:width .6s ease}',
      '.spr-bar-label{display:flex;justify-content:space-between;font-size:11px;color:#fff !important;font-weight:bold}',
      '.spr-cert-valid{color:#4ade80;font-weight:700;font-size:12px}',
      '.spr-cert-invalid{color:#f87171;font-weight:700;font-size:12px}',
      '.spr-cert-box{border-left:3px solid #3b82f6;padding:6px 8px;border-radius:4px;background:#1e2a3a}',
      '.spr-cert-warn{margin-top:4px;padding:4px 6px;border-radius:4px;border:1px solid #7f1d1d;background:#3b0d0d;color:#fca5a5;font-weight:700;font-size:11px}'
    ].join('');
    (doc.head || doc.documentElement).appendChild(st);
  }

  function buildWidget(doc) {
    if (doc.getElementById(WIDGET_ID)) return;
    ensureStyles(doc);
    var menu = doc.getElementById('MenuPrincipal') || doc.getElementById('Menu');
    var root = doc.createElement('div');
    root.id  = WIDGET_ID;
    root.style.cssText = [
      menu ? 'position:relative' : 'position:fixed',
      menu ? 'display:block' : 'left:5px',
      menu ? 'margin: 20px 10px 10px' : 'bottom: 5px',
      'width:' + (menu ? 'calc(100% - 20px)' : '241px'),
      'z-index:9999;padding:12px;border-radius:8px;box-sizing:border-box;background:rgba(255,255,255,0.05);border:1px solid rgba(255,255,255,0.1);clear:both'
    ].join(';');
    var parent = menu || doc.body;
    parent.appendChild(root);
    renderGrafico(doc, root);
  }

  function renderGrafico(doc, root) {
    var grafCard = doc.createElement('div');
    grafCard.className = 'spr-graf-card';
    var title = doc.createElement('div');
    title.className = 'spr-graf-title';
    title.textContent = 'Estoque de Selos';
    grafCard.appendChild(title);
    var content = doc.createElement('div');
    content.id = 'spr-graf-content';
    grafCard.appendChild(content);
    root.appendChild(grafCard);

    var update = function() {
        var urlParams = new URLSearchParams(window.location.search);
        var sys = urlParams.get('sys') || 'TTB';
        var database = urlParams.get('database') || urlParams.get('db') || '';
        
        var apiUrl = '/notas/FunarpenSelosServlet?action=estoque&sys=' + sys + '&database=' + database;
        
        fetch(apiUrl)
          .then(function (res) { return res.json(); })
          .then(function (data) {
            if (data && data.estoque) paintBars(doc, content, data.estoque);
            if (data && data.certificado) {
              var cb = doc.getElementById('spr-cert-body');
              if (cb) paintCert(doc, cb, data.certificado);
            }
          })
          .catch(function (err) { console.error('Error fetching data:', err); });
    };
    update();
    setInterval(update, 60000);
    renderCertificado(doc, root);
  }

  function paintBars(doc, container, data) {
    container.innerHTML = '';
    var stats = {};
    if (Array.isArray(data)) {
        data.forEach(function(i) { stats[i.tiposelo || i.label || 'Selo'] = i.total || 0; });
    } else { stats = data || {}; }
    var keys = Object.keys(stats).slice(0, 7);
    var total = 0;
    keys.forEach(function (k) { total += Number(stats[k] || 0); });
    keys.forEach(function (k) {
      var v = Number(stats[k] || 0);
      var pct = total > 0 ? Math.round((v / (total * 1.2)) * 100) : 0;
      var lbl = doc.createElement('div'); lbl.className = 'spr-bar-label';
      lbl.innerHTML = '<span>' + k + '</span><span style="font-weight:700">' + v + '</span>';
      var bar = doc.createElement('div'); bar.className = 'spr-bar';
      var fill = doc.createElement('div'); fill.className = 'spr-bar-fill';
      fill.style.background = v >= 100 ? '#16a34a' : v >= 50 ? '#f59e0b' : '#ef4444';
      fill.style.width = '0%';
      bar.appendChild(fill);
      container.appendChild(lbl);
      container.appendChild(bar);
      setTimeout(function () { fill.style.width = Math.min(100, pct) + '%'; }, 50);
    });
  }

  function renderCertificado(doc, root) {
    var certCard = doc.createElement('div');
    certCard.className = 'spr-graf-card';
    certCard.innerHTML = '<div class="spr-graf-title">Certificado Digital</div><div id="spr-cert-body" style="color:#888;font-size:11px">Aguardando dados...</div>';
    root.appendChild(certCard);
  }

  function paintCert(doc, body, info) {
    body.innerHTML = '';
    var box = doc.createElement('div'); box.className = 'spr-cert-box';
    if (info.valid) {
      box.innerHTML = '<div class="spr-cert-valid">✔ Certificado Válido</div>';
      if (info.notAfter) {
        var d = new Date(info.notAfter);
        box.innerHTML += '<div style="color:#fff;font-size:10px;margin-top:3px">Vence: ' + d.toLocaleDateString('pt-BR') + '</div>';
      }
    } else {
      box.style.borderColor = '#ef4444';
      box.innerHTML = '<div class="spr-cert-invalid">✖ Certificado Inválido</div><div style="color:#666;font-size:10px">'+(info.message || '')+'</div>';
    }
    body.appendChild(box);
  }

  // --- NAVEGAÇÃO HÍBRIDA EVOLUÍDA (PONTE MAKER-REACT) ---
  window.sprURLOpenFrame = function (form, componentName, url) {
    console.log('🚀 [Navegação] sprURLOpenFrame iniciada:', componentName, url);
    try {
      var pathParts = window.location.pathname.split('/').filter(Boolean);
      var appContext = pathParts.length > 0 ? '/' + pathParts[0] : '';
      if (url && url.startsWith('/notas') && appContext && appContext !== '/notas') url = appContext + url;
      
      var parentParams = new URLSearchParams(window.location.search);
      var sys = parentParams.get('sys');
      var database = parentParams.get('database') || parentParams.get('db');
      if (sys && url && url.indexOf('sys=') === -1) url += (url.indexOf('?') === -1 ? '?' : '&') + 'sys=' + encodeURIComponent(sys);
      if (database && url && url.indexOf('database=') === -1) url += (url.indexOf('?') === -1 ? '?' : '&') + 'database=' + encodeURIComponent(database);

      var iframe = null;
      if (typeof controller !== 'undefined' && controller && typeof controller.getElementById === 'function') {
        iframe = controller.getElementById(componentName, form);
      }
      if (!iframe) {
        iframe = document.getElementById(componentName) || document.getElementById(componentName + '_iFrame');
      }
      if (!iframe) {
        if (arguments.length === 2 && typeof form === 'string') {
          var mainFrame = document.getElementById('mainsystem');
          if (mainFrame) { mainFrame.src = form; return mainFrame; }
        }
        return null;
      }
      if (typeof iframe.openUrlInFrame === 'function') iframe.openUrlInFrame(url, true, false);
      else iframe.src = url;

      iframe.style.width = "100%";
      iframe.style.boxSizing = "border-box";
      iframe.style.border = "0";

      var applySizing = function() {
        var p = iframe.parentElement;
        var container = null;
        for (var i = 0; i < 6 && p; i++, p = p.parentElement) {
          if (!p) break;
          if ((p.id || "").toLowerCase().indexOf("moldura") !== -1) { container = p; break; }
        }
        if (!container) container = iframe.parentElement || document.body;
        var h = container.clientHeight || 500;
        iframe.style.height = h + "px";
      };
      applySizing();
      window.addEventListener("resize", applySizing);
      return iframe;
    } catch (e) { console.error('Erro sprURLOpenFrame:', e); return null; }
  };

  window.sprURLframeOpen = window.sprURLOpenFrame;
  window.sprURLFrameOpen = window.sprURLOpenFrame;

  (function tryImmediate() {
    var d = resolveMenuDoc();
    if (d && !widgetExists()) buildWidget(d);
  })();
})();
