(function () {
  if (window.__WR_FRAME_BRIDGE__) return;
  window.__WR_FRAME_BRIDGE__ = true;

  // Optional: allowlist of parent origins. If empty, accept from any origin.
  // You can set window.__WR_FRAME_BRIDGE_ALLOWED_ORIGINS = ['https://meuhost'] before loading.
  var allowed = (window.__WR_FRAME_BRIDGE_ALLOWED_ORIGINS || []);

  function originAllowed(origin) {
    if (!allowed || !allowed.length) return true;
    try {
      for (var i = 0; i < allowed.length; i++) {
        if (origin.indexOf(allowed[i]) === 0) return true;
      }
    } catch (e) {}
    return false;
  }

  // debounce / cache to avoid applying the same height repeatedly
  var __wr_lastApplied = new WeakMap(); // iframeEl -> {height: number, time: ms}
  var __wr_debounceMs = 500;

  window.addEventListener('message', function (event) {
    try {
      if (!event.data || event.data.type !== 'WR_FRAME_RESIZE') return;
      try { console.debug('WR_FRAME_RESIZE received', event.data, 'origin=', event.origin, 'source=', event.source); } catch(e){}
      if (!originAllowed(event.origin)) return;

      var height = parseInt(event.data.height, 10);
      if (!height || height < 50) return;
      var iframes = document.getElementsByTagName('iframe');

      // 1) try direct match by contentWindow
      for (var i = 0; i < iframes.length; i++) {
        try {
          try { console.debug('checking iframe', i, iframes[i].getAttribute && iframes[i].getAttribute('src')); } catch(e){}
          if (iframes[i].contentWindow === event.source) {
            try { console.debug('matched by contentWindow -> iframe', i); } catch(e){}
            applyHeight(iframes[i], height);
            return;
          }
        } catch (e) { }
      }

      // 2) fallback: try to walk up the source window's parent chain to find a frame element that lives in this document
      try {
        var win = event.source;
        var depth = 0;
        while (win && win !== window && depth < 20) {
          try {
            var fe = win.frameElement;
            try { console.debug('walking parent chain depth', depth, 'frameElement=', fe); } catch(e){}
            if (fe && fe.ownerDocument === document) {
              try { console.debug('matched by frameElement at depth', depth); } catch(e){}
              applyHeight(fe, height);
              return;
            }
          } catch (e) { }
          try { win = win.parent; } catch(e){ break; }
          depth++;
        }
      } catch (e) { }

      // 3) fallback: if child provided url, try match by iframe.src contains url
      try {
        var msgUrl = event.data && event.data.url;
        if (msgUrl) {
          for (var j = 0; j < iframes.length; j++) {
            try {
              var src = iframes[j].getAttribute('src') || iframes[j].src || '';
                try { console.debug('matching by src', j, 'iframe.src=', src, 'msgUrl=', msgUrl); } catch(e){}
                if (src.indexOf(msgUrl) !== -1 || msgUrl.indexOf(src) !== -1) {
                  try { console.debug('matched by src -> iframe', j); } catch(e){}
                  applyHeight(iframes[j], height);
                  return;
                }
            } catch(e) {}
          }
        }
      } catch(e) {}

      // 4) fallback: try common Maker wrapper patterns (openform/form.jsp) or first iframe
      try {
        var patterns = ['openform', 'form.jsp', 'formID', 'open.do'];
        for (var p = 0; p < patterns.length; p++) {
          var pat = patterns[p];
          for (var k = 0; k < iframes.length; k++) {
            try {
              var s2 = iframes[k].getAttribute('src') || iframes[k].src || '';
              if (s2 && s2.indexOf(pat) !== -1) {
                try { console.debug('fallback matched pattern', pat, '-> iframe', k); } catch(e){}
                applyHeight(iframes[k], height);
                return;
              }
            } catch(e){}
          }
        }
        // final fallback: apply to first VISIBLE iframe on the page, otherwise first iframe
        try {
          for (var v = 0; v < iframes.length; v++) {
            try {
              var el = iframes[v];
              var rect = el.getBoundingClientRect && el.getBoundingClientRect();
              var cs = window.getComputedStyle ? window.getComputedStyle(el) : null;
              var visible = rect && rect.width > 0 && rect.height > 0 && el.offsetParent !== null;
              if (cs) visible = visible && cs.display !== 'none' && cs.visibility !== 'hidden' && cs.opacity !== '0';
              if (visible) {
                try { console.debug('fallback applying to visible iframe', v); } catch(e){}
                applyHeight(el, height);
                return;
              }
            } catch(e){}
          }
          // no visible iframe found, fall back to first iframe
          if (iframes && iframes.length) {
            try { console.debug('fallback applying to first iframe (no visible found)'); } catch(e){}
            applyHeight(iframes[0], height);
            return;
          }
        } catch(e) { try { console.warn('final fallback failed', e); } catch(_){} }
      } catch(e) { try { console.warn('fallback matching failed', e); } catch(_){} }

      // nothing matched after fallbacks
    } catch (e) {
      // safe guard
      try { console.warn('WR_FRAME_BRIDGE error', e); } catch (err) {}
    }
  });

  function applyHeight(iframeEl, height) {
    try {
      // debounce: skip if same height applied recently
      try {
        var prev = __wr_lastApplied.get(iframeEl);
        var now = Date.now();
        if (prev && prev.height === height && (now - prev.time) < __wr_debounceMs) {
          try { console.debug('applyHeight skipped (debounced) for', iframeEl); } catch(e){}
          return;
        }
        __wr_lastApplied.set(iframeEl, { height: height, time: now });
      } catch(e){}

      // compute new height and apply both height and minHeight to avoid layout collapsing
      var newH = (height + 20) + 'px';
      try { iframeEl.style.height = newH; } catch(e){}
      try { iframeEl.style.minHeight = newH; } catch(e){}
      try { iframeEl.style.border = '0'; } catch(e){}

      // also attempt to ensure parent containers allow the frame to grow
      try {
        var p = iframeEl.parentElement;
        for (var pi = 0; pi < 3 && p; pi++, p = p.parentElement) {
          try { if (p.style && (!p.style.minHeight || parseInt(p.style.minHeight,10) < height)) p.style.minHeight = newH; } catch(e){}
        }
      } catch(e){}

      try {
        var ev = new CustomEvent('wr-frame-resized', { detail: { iframe: iframeEl, height: height } });
        iframeEl.dispatchEvent && iframeEl.dispatchEvent(ev);
      } catch (e) {}
    } catch (e) { try { console.warn('applyHeight failed', e); } catch(_){} }
  }
})();
