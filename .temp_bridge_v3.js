
/** [Seprocom] Bridge de Navegação Inteligente (Context Aware) **/
(function(){
    console.log("[Seprocom] Ativando Bridge com Detecção de Sistema");

    window.sprURLOpenFrame = function(form, compName, url) {
        // 1. Detectar o Sistema (TTB, PRT, etc)
        var sys = "";
        try {
            var urlParams = new URLSearchParams(window.location.search);
            sys = urlParams.get('sys') || "";
        } catch(e) {}

        // 2. Normalizar a URL
        var target = url;
        if (!url.startsWith("/") && !url.startsWith("http")) {
            target = "/notas/" + url;
        }

        // 3. Injetar o SYS se não existir na URL alvo
        if (sys && target.indexOf("sys=") === -1) {
            target += (target.indexOf("?") === -1 ? "?" : "&") + "sys=" + sys;
        }

        console.log("[sprURLOpenFrame] Navegando:", target);

        var win = window.top;
        var name = compName || "Moldura";
        var comp = null;

        try {
            // Tenta via Controller do Webrun (SmartClient)
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

            // Fallback para DOM direto
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
    
    // Auto-carregamento do motor de graficos
    var s = document.createElement("script");
    s.src = "/notas/html/assets/sprGraficoEstoque.js";
    document.head.appendChild(s);
})();
