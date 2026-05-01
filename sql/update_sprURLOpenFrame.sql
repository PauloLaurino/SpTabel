/**
 * Versão Evoluída do sprURLOpenFrame (Baseada no Maker 5 + Melhorias de Contexto e Layout)
 * 
 * 1. Detecta e ajusta o contexto da aplicação automaticamente (/gerencial, /notas, etc.).
 * 2. Propaga automaticamente os parâmetros 'sys' e 'database' (Notas/Protesto).
 * 3. Gerencia o redimensionamento dinâmico da moldura.
 * 4. Garante compatibilidade com diferentes versões do SmartClient/Webrun.
 */
(function(){
  window.sprURLOpenFrame = function (form, componentName, url) {
    try {
      // --- 1. DETECÇÃO E AJUSTE DE CONTEXTO DA APLICAÇÃO ---
      var pathParts = window.location.pathname.split('/').filter(Boolean);
      var appContext = pathParts.length > 0 ? '/' + pathParts[0] : '';
      
      // Ajusta URL se começa com /notas e estamos em contexto diferente
      if (url && url.startsWith('/notas') && appContext && appContext !== '/notas') {
        url = appContext + url;
      }

      // --- 2. HERANÇA DE CONTEXTO ---
      var parentParams = new URLSearchParams(window.location.search);
      var sys = parentParams.get('sys');
      var database = parentParams.get('database') || parentParams.get('db');
      if (sys && url && url.indexOf('sys=') === -1) {
          url += (url.indexOf('?') === -1 ? '?' : '&') + 'sys=' + encodeURIComponent(sys);
      }
      if (database && url && url.indexOf('database=') === -1 && url.indexOf('db=') === -1) {
          url += (url.indexOf('?') === -1 ? '?' : '&') + 'database=' + encodeURIComponent(database);
      }

      // --- 3. LOCALIZAÇÃO DO COMPONENTE ---
      var iframe = null;
      if (typeof controller !== 'undefined' && controller && typeof controller.getElementById === 'function') {
        iframe = controller.getElementById(componentName, form);
      }

      // Fallback robusto se o controller falhar
      if (!iframe) {
        console.warn('[sprURLOpenFrame] Controller falhou. Tentando busca manual por ID:', componentName);
        iframe = document.getElementById(componentName) || document.getElementById(componentName + '_iFrame');
      }

      if (!iframe) return null;

      // --- 3. CARREGAMENTO DA URL ---
      if (typeof iframe.openUrlInFrame === 'function') {
        iframe.openUrlInFrame(url, true, false);
      } else {
        iframe.src = url;
      }

      // --- 4. ESTILIZAÇÃO E REDIMENSIONAMENTO (Lógica Maker 5) ---
      iframe.style.width = "100%";
      iframe.style.boxSizing = "border-box";
      iframe.style.border = "0";

      function applySizing() {
        try {
          var p = iframe.parentElement;
          var container = null;
          // Busca o container da moldura (até 6 níveis acima)
          for (var i = 0; i < 6 && p; i++, p = p.parentElement) {
            if (!p) break;
            var id = (p.id || "").toLowerCase();
            var cls = (p.className || "").toLowerCase();
            if (id.indexOf("moldura") !== -1 || cls.indexOf("moldura") !== -1 ||
                (p.getAttribute && p.getAttribute("data-moldura") !== null)) {
              container = p;
              break;
            }
          }
          if (!container) container = iframe.parentElement || document.body;

          var containerHeight = container.clientHeight || container.offsetHeight || 0;
          if (containerHeight && containerHeight > 120) {
            iframe.style.height = containerHeight + "px";
          } else {
            var vh = Math.max(window.innerHeight - 40, 300);
            iframe.style.height = vh + "px";
          }
          if (!iframe.style.minHeight) iframe.style.minHeight = "300px";
        } catch (e) { console.error('[sprURLOpenFrame] Erro no applySizing:', e); }
      }

      applySizing();

      // --- 5. OBSERVAÇÃO DE MUDANÇAS DE TAMANHO ---
      if (window.ResizeObserver) {
        try {
          var ro = new ResizeObserver(function () { applySizing(); });
          ro.observe(iframe.parentElement || document.body);
        } catch (e) { window.addEventListener("resize", applySizing); }
      } else {
        window.addEventListener("resize", applySizing);
      }

      return iframe;
    } catch (e) {
      console.error('[sprURLOpenFrame] Erro critico:', e);
      return null;
    }
  };
})();
