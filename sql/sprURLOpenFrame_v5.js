(function(){
  window.sprURLOpenFrame = function (form, componentName, url) {
    try {
      // Detect application context from current path
      var pathParts = window.location.pathname.split('/').filter(Boolean);
      var appContext = pathParts.length > 0 ? '/' + pathParts[0] : '';
      
      // Adjust URL if it starts with /notas and we're in a different context
      if (url && url.startsWith('/notas') && appContext && appContext !== '/notas') {
        url = appContext + url;
      }
      
      var parentParams = new URLSearchParams(window.location.search);
      var sys = parentParams.get("sys");
      var database = parentParams.get("database") || parentParams.get("db");
      if (sys && url && url.indexOf("sys=") === -1) {
          url += (url.indexOf("?") === -1 ? "?" : "&") + "sys=" + encodeURIComponent(sys);
      }
      if (database && url && url.indexOf("database=") === -1 && url.indexOf("db=") === -1) {
          url += (url.indexOf("?") === -1 ? "?" : "&") + "database=" + encodeURIComponent(database);
      }
      var iframe = null;
      if (typeof controller !== "undefined" && controller && typeof controller.getElementById === "function") {
        iframe = controller.getElementById(componentName, form);
      }
      if (!iframe) {
        iframe = document.getElementById(componentName) || document.getElementById(componentName + "_iFrame");
      }
      if (!iframe) return null;
      if (typeof iframe.openUrlInFrame === "function") {
        iframe.openUrlInFrame(url, true, false);
      } else {
        iframe.src = url;
      }
      iframe.style.width = "100%";
      iframe.style.boxSizing = "border-box";
      iframe.style.border = "0";
      function applySizing() {
        try {
          var p = iframe.parentElement;
          var container = null;
          for (var i = 0; i < 6 && p; i++, p = p.parentElement) {
            if (!p) break;
            var id = (p.id || "").toLowerCase();
            var cls = (p.className || "").toLowerCase();
            if (id.indexOf("moldura") !== -1 || cls.indexOf("moldura") !== -1 || (p.getAttribute && p.getAttribute("data-moldura") !== null)) {
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
        } catch (e) {}
      }
      applySizing();
      if (window.ResizeObserver) {
        try {
          var ro = new ResizeObserver(function () { applySizing(); });
          ro.observe(iframe.parentElement || document.body);
        } catch (e) { window.addEventListener("resize", applySizing); }
      } else {
        window.addEventListener("resize", applySizing);
      }
      return iframe;
    } catch (e) { return null; }
  };
})();
