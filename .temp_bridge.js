
/** [Seprocom] Link Bridge to Notas Context **/
(function(){
    console.log("[Seprocom] Ativando Bridge para Contexto /notas/");
    
    var loadScript = function(url){
        var s = document.createElement("script");
        s.src = url;
        s.type = "text/javascript";
        document.head.appendChild(s);
    };

    // Carrega o motor de graficos do contexto /notas/
    loadScript("/notas/html/assets/sprGraficoEstoque.js");

    window.sprURLOpenFrame = function(form, compName, url) {
        // Normaliza a URL para apontar para o contexto /notas/
        var target = url;
        if (url.indexOf("/notas/") === -1 && url.indexOf("http") === -1) {
            target = "/notas/" + url;
        }
        
        console.log("[sprURLOpenFrame] Abrindo via Notas:", target);
        
        var win = window.top;
        var name = compName || "Moldura";
        var comp = null;

        try {
            if (typeof controller !== "undefined" && controller.getElementById) {
                comp = controller.getElementById(name, form);
            }
            if (!comp && win.controller && win.controller.getElementById) {
                comp = win.controller.getElementById(name, form);
            }

            if (comp && comp.openUrlInFrame) {
                comp.openUrlInFrame(target, true, false);
                return;
            }

            var el = document.getElementById(name) || document.getElementById(name + "_iFrame") || win.document.getElementById(name);
            if (el) {
                if (el.tagName === "IFRAME") {
                    el.src = target;
                } else {
                    var ifr = el.querySelector("iframe");
                    if (ifr) ifr.src = target;
                }
            }
        } catch(e) {
            console.error("[sprURLOpenFrame] Erro:", e);
        }
    };

    window.sprURLFrameOpen = window.sprURLOpenFrame;
})();
