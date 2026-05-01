// Normaliza requisições que contenham '/funarpen/funarpen/' para '/funarpen/'
// Evita 404s quando bundles são construídos com duplicação do contexto.
(function () {
  try{
    if(!window.__funarpen_fetch_normalized__){
      var _origFetch = window.fetch && window.fetch.bind(window);
      if(_origFetch){
        window.fetch = function(input, init){
          try{
            var url = (typeof input === 'string') ? input : (input && input.url ? input.url : '');
            if(url && url.indexOf('/funarpen/funarpen/') !== -1){
              var normalized = url.replace(/\/funarpen\/funarpen\//g, '/funarpen/');
              if(typeof input === 'string') return _origFetch(normalized, init);
              try{ return _origFetch(new Request(normalized, input)); }catch(e){ return _origFetch(normalized, init); }
            }
          }catch(e){}
          return _origFetch(input, init);
        };
      }
      window.__funarpen_fetch_normalized__ = true;
    }
  }catch(e){}

  (function () {

  // Polyfill mínimo para isEqual (evita ReferenceError quando bibliotecas ausentes)
  try {
    if (typeof window !== 'undefined' && typeof window.isEqual === 'undefined') {
      window.isEqual = function(a, b) {
        try { if (a === b) return true; } catch(e) {}
        try { return JSON.stringify(a) === JSON.stringify(b); } catch(e) { return false; }
      };
    }
  } catch(e) {}

  if (typeof React === "undefined") {
    console.error("React não carregado. Verifique a ordem dos scripts.");
    return;
  }

  const { useState, useEffect } = React;

  window.AppHeader = function AppHeader(props) {

    const [time, setTime] = useState("");

    useEffect(() => {
      const t = setInterval(() => {
        setTime(new Date().toLocaleTimeString());
      }, 1000);
      return () => clearInterval(t);
    }, []);

    return React.createElement(
      "header",
      { className: "app-header" },

      /* LOGO */
      React.createElement(
        "div",
        { className: "logo-container" },
        React.createElement("i", { className: "fas fa-stamp logo-icon" }),
        React.createElement(
          "div",
          { className: "logo-text" },
          React.createElement("h1", null, props.title || "Sistema"),
          props.subtitle
            ? React.createElement("p", null, props.subtitle)
            : null
        ),
        props.version
          ? React.createElement("span", { className: "version-badge" }, props.version)
          : null
      ),

      /* STATUS */
      React.createElement(
        "div",
        { className: "status-container" },
        React.createElement("div", { className: "online" },
          React.createElement("i", { className: "fas fa-circle status-icon" }),
          " Online"
        ),
        props.database
          ? React.createElement("div", { className: "db" },
              React.createElement("i", { className: "fas fa-database status-icon" }),
              " ", props.database
            )
          : null,
        React.createElement("div", { className: "time" },
          React.createElement("i", { className: "fas fa-clock status-icon" }),
          " ", time
        )
      ),

      /* USUÁRIO */
      React.createElement(
        "div",
        { className: "user-container" },
        React.createElement("i", { className: "fas fa-user-circle user-avatar" }),
        React.createElement(
          "div",
          null,
          React.createElement("div", { className: "user-name" }, props.userName || ""),
          React.createElement("div", { className: "user-role" }, props.userLogin || "")
        ),
        React.createElement(
          "button",
          {
            className: "btn-header btn-danger",
            title: "Sair",
            onClick: props.onLogout || function () {
              window.location.href = "/webrun5/logout";
            }
          },
          React.createElement("i", { className: "fas fa-sign-out-alt" })
        )
      )
    );
  };

})();
