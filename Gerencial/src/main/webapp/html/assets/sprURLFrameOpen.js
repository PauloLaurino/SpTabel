/**
 * sprURLFrameOpen.js - Versao Golden Resiliente
 * Focada em compatibilidade direta com o contexto do frame.
 */
(function() {
  window.sprURLOpenFrame = function (form, componentName, url) {
    var name = componentName || 'Moldura';
    if (window.console) console.log('[sprURLOpenFrame] Solicitado:', name, 'URL:', url);
    
    try {
      if (!url) return null;

      // 1. Tenta o Controller LOCAL primeiro (Mais confiavel no Maker)
      var comp = null;
      if (typeof controller !== 'undefined' && controller && typeof controller.getElementById === 'function') {
        comp = controller.getElementById(name, form);
        if (comp && typeof comp.openUrlInFrame === 'function') {
          if (window.console) console.log('[sprURLOpenFrame] Carregando via controller local');
          comp.openUrlInFrame(url, true, false);
          return comp;
        }
      }

      // 2. Fallback para Controller no TOP (se o local falhar)
      var win = window.top || window;
      if (win !== window && win.controller && typeof win.controller.getElementById === 'function') {
        comp = win.controller.getElementById(name, form);
        if (comp && typeof comp.openUrlInFrame === 'function') {
          if (window.console) console.log('[sprURLOpenFrame] Carregando via controller TOP');
          comp.openUrlInFrame(url, true, false);
          return comp;
        }
      }

      // 3. Busca Direta no DOM/SmartClient (Estrategia Golden)
      function findAndLoad(targetName) {
        // Tenta SmartClient isc
        try {
          var isc = window.isc || (window.top && window.top.isc);
          if (isc && isc.Canvas) {
            var iscComp = isc.Canvas.getById(targetName) || isc.Canvas.getCanvas(targetName);
            if (iscComp) {
              if (typeof iscComp.getIFrameHandle === 'function') {
                var h = iscComp.getIFrameHandle();
                if (h) { h.src = url; return true; }
              }
              var handle = iscComp.getHandle ? iscComp.getHandle() : null;
              if (handle) {
                var iframe = handle.querySelector('iframe');
                if (iframe) { iframe.src = url; return true; }
              }
            }
          }
        } catch(e) {}

        // Tenta DOM patterns
        var patterns = [targetName, targetName + '_iFrame', targetName.toLowerCase()];
        for (var p = 0; p < patterns.length; p++) {
          var el = document.getElementById(patterns[p]) || (window.top && window.top.document.getElementById(patterns[p]));
          if (el) {
            if (el.tagName && el.tagName.toLowerCase() === 'iframe') {
              el.src = url; return true;
            }
            var childIframe = el.querySelector && el.querySelector('iframe');
            if (childIframe) {
              childIframe.src = url; return true;
            }
          }
        }
        return false;
      }

      var attempts = 0;
      function retry() {
        if (findAndLoad(name)) {
          if (window.console) console.log('[sprURLOpenFrame] Carregado via DOM/isc');
          return;
        }
        if (++attempts < 15) setTimeout(retry, 250);
        else if (window.console) console.warn('[sprURLOpenFrame] Alvo nao encontrado:', name);
      }
      retry();

    } catch (e) {
      if (window.console) console.error('[sprURLOpenFrame] Erro critico:', e);
    }
  };

  // Garante os dois nomes
  window.sprURLFrameOpen = window.sprURLOpenFrame;
  window.sprURLframeOpen = window.sprURLOpenFrame;

  // Propaga para o top para garantir visibilidade global
  try { window.top.sprURLOpenFrame = window.sprURLOpenFrame; } catch(e){}
  try { window.top.sprURLFrameOpen = window.sprURLOpenFrame; } catch(e){}
})();
