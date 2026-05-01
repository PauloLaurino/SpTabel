/**
 * sprURLFrameOpen.js - Ponte de Navegação Evolved
 * Consolidado para Maker Studio e Maker 5.
 */
(function() {
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
})();
