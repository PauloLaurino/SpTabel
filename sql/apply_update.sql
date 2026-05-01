UPDATE fr_regras_funcoes SET FUN_CONTEUDO_CLIENTE = '/**
 * Abre uma URL dentro de uma moldura (iframe) sem abrir nova aba.<br/>
 * <br/>
 * 1º (esq): referência ao formulário (opcional).<br/>
 * 2º (meio): component (nome/id da moldura) — ex.: Moldura.<br/>
 * 3º (dir): url — string da página a abrir (ex.: /notas/html/selos_utilizados.html).<br/>
 * <br/>
 * Estratégias (em ordem):<br/>
 *   1. controller.getElementById + openUrlInFrame (API Maker)<br/>
 *   2. isc.Canvas.getById → getHandle → querySelector(''iframe'') (SmartClient API)<br/>
 *   3. getElementById por padrões de nome/id derivados do componenteName<br/>
 *   4. Fallback: qualquer iframe em branco que não seja container de formulário<br/>
 */
(function(){
  window.sprURLOpenFrame = function (form, componentName, url) {
    if (!componentName || componentName.trim() === '''') {
       console.warn(''[sprURLOpenFrame] Nome do componente nao informado. Tentando fallback para \"Moldura\"'');
       componentName = ''Moldura'';
    }
    console.log(''[sprURLOpenFrame] Solicitado:'', componentName, ''URL:'', url);
    try {
      if (!url) return null;

      // ---------------------------------------------------------------
      // Estratégia 1: controller API do Maker (openUrlInFrame)
      // ---------------------------------------------------------------
      var comp = null;
      if (typeof controller !== ''undefined'' && controller && typeof controller.getElementById === ''function'') {
        comp = controller.getElementById(componentName, form);
        if (comp && typeof comp.openUrlInFrame === ''function'') {
          console.log(''[sprURLOpenFrame] Chamando openUrlInFrame no controller'');
          comp.openUrlInFrame(url, true, false);
        }
      }

      // ---------------------------------------------------------------
      // Estratégias 2, 3 e 4: localizar o <iframe> real e setar src
      // ---------------------------------------------------------------
      function findRealIframe(name) {

        // 2: SmartClient API — isc.Canvas.getById
        // O Maker Studio renderiza a "Moldura URL" como widget ISC; o iframe
        // fica dentro do handle do componente, não tem ID derivado do nome.
        try {
          if (typeof isc !== ''undefined'' && isc.Canvas) {
            var iscComp = isc.Canvas.getById(name);
            if (iscComp) {
              var handle = iscComp.getHandle ? iscComp.getHandle() : null;
              if (handle) {
                var childIframes = handle.querySelectorAll(''iframe'');
                if (childIframes.length > 0) {
                  console.log(''[sprURLOpenFrame] iframe via isc.Canvas.getById:'', name, childIframes[0].id);
                  return childIframes[0];
                }
              }
              if (typeof iscComp.getIFrameHandle === ''function'') {
                var h = iscComp.getIFrameHandle();
                if (h) { console.log(''[sprURLOpenFrame] iframe via getIFrameHandle''); return h; }
              }
            }
          }
        } catch(iscErr) {
          console.warn(''[sprURLOpenFrame] isc.Canvas fallback ignorado:'', iscErr.message);
        }

        // 3: busca por padrões de ID/name nos documentos acessíveis
        var docs = [document];
        try { if (window.top    && window.top.document)    docs.push(window.top.document);    } catch(e){}
        try { if (window.parent && window.parent.document) docs.push(window.parent.document); } catch(e){}

        var patterns = [
          name,
          name + ''_iFrame'',
          name + ''_frame'',
          name.toLowerCase(),
          name.toLowerCase() + ''_iframe''
        ];

        for (var d = 0; d < docs.length; d++) {
          var doc = docs[d];

          // busca por ID exato
          for (var p = 0; p < patterns.length; p++) {
            var el = doc.getElementById(patterns[p]);
            if (el && el.tagName) {
              var tag = el.tagName.toLowerCase();
              if (tag === ''iframe'') return el;
              if (tag === ''div'') {
                console.log(''[sprURLOpenFrame] Alvo e uma DIV. Verificando/Injetando iframe...'');
                var existing = el.querySelector(''iframe'');
                if (existing) return existing;
                
                var newIframe = doc.createElement(''iframe'');
                newIframe.id = patterns[p] + ''_gen_iframe'';
                newIframe.style.width = ''100%'';
                newIframe.style.height = ''100%'';
                newIframe.style.border = ''none'';
                newIframe.setAttribute(''frameborder'', ''0'');
                el.style.overflow = ''hidden'';
                el.appendChild(newIframe);
                return newIframe;
              }
            }
          }

          // busca por id/name parcial
          var iframes = doc.getElementsByTagName(''iframe'');
          for (var i = 0; i < iframes.length; i++) {
            var iid = (iframes[i].id   || '''').toLowerCase();
            var inm = (iframes[i].name || '''').toLowerCase();
            var ln  = name.toLowerCase();
            if (iid === ln || inm === ln || iid.indexOf(ln) !== -1 || inm.indexOf(ln) !== -1) {
              return iframes[i];
            }
          }

          // 4: Fallback — qualquer iframe em branco que não seja container de formulário Maker
          for (var j = 0; j < iframes.length; j++) {
            var isrc = (iframes[j].src || '''').toLowerCase();
            var jid  = (iframes[j].id  || '''').toLowerCase();
            if ((isrc === ''about:blank'' || isrc === '''') &&
                jid.indexOf(''urlframe'')     === -1 &&
                jid.indexOf(''wfriframe'')    === -1 &&
                jid.indexOf(''mainsystem'')   === -1 &&
                jid.indexOf(''system_frame'') === -1) {
              console.log(''[sprURLOpenFrame] iframe blank (fallback):'', iframes[j].id);
              return iframes[j];
            }
          }
        }
        return null;
      }

      // Aguarda o SmartClient finalizar a renderização e então seta src
      var attempts    = 0;
      var maxAttempts = 15;   // 15 × 200ms = 3s
      function tryLoadInRealIframe() {
        var iframe = findRealIframe(componentName);
        if (iframe) {
          console.log(''[sprURLOpenFrame] iframe real encontrado:'', iframe.id || iframe.name);
          iframe.src = url;
          return;
        }
        if (++attempts < maxAttempts) {
          setTimeout(tryLoadInRealIframe, 200);
        } else {
          console.warn(''[sprURLOpenFrame] iframe real nao encontrado apos '' + maxAttempts + '' tentativas.'');
        }
      }
      setTimeout(tryLoadInRealIframe, 100);

      return comp;
    } catch (e) {
      console.error(''[sprURLOpenFrame] Erro critico:'', e);
      return null;
    }
  };
})();
' WHERE FUN_NOME = 'Abrir URL em uma Moldura';
