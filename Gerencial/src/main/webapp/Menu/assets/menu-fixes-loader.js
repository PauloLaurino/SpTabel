(function(){
  'use strict';
  function ts(){return new Date().toISOString();}
  try{
    console.info('[menu-fixes-loader] init', ts());
    var topWin = window.top;
    if(!topWin){ console.warn('[menu-fixes-loader] no window.top available'); return; }
    var topDoc = topWin.document;
    if(!topDoc){ console.warn('[menu-fixes-loader] window.top.document not accessible'); return; }

    if(topDoc.getElementById('menu-fixes-script')){ console.info('[menu-fixes-loader] already injected: #menu-fixes-script'); }

    // detect base context (e.g. /webrun5) from top location and build candidates dynamically
    var basePrefix = '';
    try{
      var path = (topWin.location && topWin.location.pathname) || '';
      var firstSeg = (path || '').split('/').filter(function(s){ return s && s.length; })[0];
      if(firstSeg){ basePrefix = '/' + firstSeg; }
    }catch(e){ /* ignore cross-origin issues */ }

    var candidates = [
      // prefer dynamic base without /Menu prefix (use /assets)
      basePrefix + '/assets/menu-fixes.js',
      basePrefix + '/funarpen/html/assets/menu-fixes.js',
      // fallbacks that avoid leading /Menu/
      '/webrun5/assets/menu-fixes.js',
      '/assets/menu-fixes.js',
      '/funarpen/html/assets/menu-fixes.js'
    ];

    function tryLoad(index){
      if(index>=candidates.length){ console.warn('[menu-fixes-loader] all candidates failed'); return; }
      var src = candidates[index];
      console.info('[menu-fixes-loader] trying', src);
      try{
        var s = topDoc.createElement('script');
        s.id = 'menu-fixes-script';
        s.src = src;
        s.async = true;
        s.onload = function(){ console.info('[menu-fixes-loader] loaded from', src, ts()); ensureObserver(); };
        s.onerror = function(){ console.warn('[menu-fixes-loader] failed to load', src); try{ s.remove(); }catch(e){} tryLoad(index+1); };
        (topDoc.head || topDoc.documentElement).appendChild(s);
      }catch(e){ console.error('[menu-fixes-loader] error creating script element for', src, e); tryLoad(index+1); }
    }

    // quick inline CSS to make effect immediate and to help debugging
    if(!topDoc.getElementById('menu-fixes-inline-css')){
      var css = [
        '/* injected by menu-fixes-loader */',
        'div#Menu.collapsed .menu-icon{display:inline-flex !important;visibility:visible !important;opacity:1 !important}',
        '#NavLogo,#Navbar,#UserInfo{overflow:visible !important}',
        '.webrun-main-form #Navbar .dropdown-menu{z-index:99999 !important}'
      ].join('\n');
      try{
        var st = topDoc.createElement('style'); st.id = 'menu-fixes-inline-css'; st.appendChild(topDoc.createTextNode(css));
        (topDoc.head || topDoc.documentElement).appendChild(st);
        console.info('[menu-fixes-loader] inline CSS injected', ts());
      }catch(e){ console.warn('[menu-fixes-loader] failed to inject inline CSS', e); }
    } else {
      console.info('[menu-fixes-loader] inline CSS already present');
    }

    function ensureObserver(){
      try{
        var root = topDoc.documentElement;
        if(!root){ console.warn('[menu-fixes-loader] no root to observe'); return; }
        // observe additions to detect Menu rendering
        var mo = new topWin.MutationObserver(function(muts){
          for(var m of muts){
            if(m.addedNodes && m.addedNodes.length){
              for(var n of m.addedNodes){
                try{
                  if(n.nodeType===1){
                    if(n.querySelector && n.querySelector('#Menu')){
                      console.info('[menu-fixes-loader] detected #Menu added to DOM', ts());
                      // try re-loading script if not present
                      if(!topDoc.getElementById('menu-fixes-script')){
                        console.info('[menu-fixes-loader] script missing after render, attempting load again');
                        tryLoad(0);
                      }
                      return;
                    }
                  }
                }catch(ee){}
              }
            }
          }
        });
        mo.observe(root, { childList:true, subtree:true });
        // auto-disconnect after 30s
        setTimeout(function(){ try{ mo.disconnect(); console.info('[menu-fixes-loader] observer disconnected (timeout)', ts()); }catch(e){} }, 30000);
        console.info('[menu-fixes-loader] observer installed', ts());
      }catch(e){ console.warn('[menu-fixes-loader] ensureObserver error', e); }
        // listen for funarpen child ready messages to mitigate loader loops
        try{
          topWin.addEventListener('message', function(ev){
            try{
              var m = ev && ev.data;
              if(!m || !m.type) return;
              if(m.type === 'funarpen-child-ready'){
                console.info('[menu-fixes-loader] received funarpen-child-ready', m);
                try{
                  var iframe = topDoc.getElementById('funarpen-monitor-iframe');
                  if(iframe){ iframe.style.visibility = 'visible'; iframe.style.opacity = '1'; }
                  // try to remove known loading overlays if present
                  var ov = topDoc.getElementById('__funarpen_loading_overlay__'); if(ov && ov.parentNode) ov.parentNode.removeChild(ov);
                }catch(e){}
              }
              if(m.type === 'funarpen-child-error'){
                console.warn('[menu-fixes-loader] child reported error', m);
              }
            }catch(e){}
          });
        }catch(e){ console.warn('[menu-fixes-loader] cannot install message listener', e); }
    }

    tryLoad(0);
  }catch(err){ console.error('[menu-fixes-loader] fatal', err); }
})();
