(function(){
  // child-side snippet for WR-FrameBridge
  function computeHeight() {
    try {
      var app = document.getElementById('app');
      if (app) {
        return Math.max(app.scrollHeight || 0, app.offsetHeight || 0, Math.ceil(app.getBoundingClientRect().height) || 0);
      }
      var de = document.documentElement || {};
      var bd = document.body || {};
      var h = Math.max(de.scrollHeight || 0, bd.scrollHeight || 0, de.offsetHeight || 0, bd.offsetHeight || 0, de.clientHeight || 0, bd.clientHeight || 0);
      // as a last resort, compute max bottom of top-level children
      try {
        var children = (bd && bd.children) || [];
        var max = h;
        for (var i = 0; i < children.length; i++) {
          try { var r = children[i].getBoundingClientRect(); if (r && r.bottom > max) max = Math.ceil(r.bottom); } catch(e){}
        }
        if (max > h) h = max;
      } catch(e){}
      return h;
    } catch (e) { return 0; }
  }

  var __wr_last_sent = 0;
  function sendResize() {
    try {
      var height = computeHeight();
      if (!height) return;
      // send only if changed significantly (10px) to avoid thrash
      if (Math.abs(height - (__wr_last_sent || 0)) < 10) return;
      __wr_last_sent = height;
      var url = (window.location && (window.location.pathname + window.location.search)) || window.location.href || '';
      parent.postMessage({ type: 'WR_FRAME_RESIZE', height: height, url: url }, '*');
    } catch (e) { }
  }

  // initial and on load/resize
  if (document.readyState === 'complete') setTimeout(sendResize, 50); else window.addEventListener('load', function(){ setTimeout(sendResize,50); });
  window.addEventListener('resize', function(){ setTimeout(sendResize,50); });

  // MutationObserver to detect dynamic changes (React, async content)
  try {
    var mo = new MutationObserver(function(){ setTimeout(sendResize,120); });
    mo.observe(document.documentElement || document.body, { childList: true, subtree: true, attributes: true });
  } catch(e) {}

  // expose manual trigger
  try { window.WR_FRAME_BRIDGE = window.WR_FRAME_BRIDGE || {}; window.WR_FRAME_BRIDGE.sendResize = sendResize; } catch(e) {}
})();
