/* menu-fixes.js
   Mantém ícones visíveis quando a sidebar (#Menu) está retraída
   e fornece submenu flutuante ao clicar em itens com dropdown enquanto recolhido.
*/
(function(){
  'use strict';

  function injectCss(){
    if (document.getElementById('menu-collapsed-fixes')) return;
    const s = document.createElement('style'); s.id = 'menu-collapsed-fixes';
    s.textContent = `
    /* Garantir visibilidade dos ícones quando sidebar está recolhida */
    div#Menu.collapsed .menu-icon, div#Menu.collapsed .menu-icon .fas, div#Menu.collapsed .menu-icon i {
      display: inline-flex !important;
      color: inherit !important;
      opacity: 1 !important;
      visibility: visible !important;
    }
    /* Permitir foco/tab nos ícones recolhidos */
    div#Menu.collapsed .menu-item-text { pointer-events: none; }
    /* Floating submenu base */
    .webrun-floating-submenu { position: absolute; min-width: 180px; background:#fff; border:1px solid rgba(0,0,0,0.08); box-shadow:0 6px 20px rgba(0,0,0,0.12); z-index:2147483000; border-radius:6px; overflow:hidden; }
    .webrun-floating-submenu .dropdown-item{ white-space:nowrap; }
    `;
    document.head.appendChild(s);
  }

  function removeFloating(){
    const old = document.querySelector('.webrun-floating-submenu');
    if (old) old.parentNode.removeChild(old);
  }

  function showFloating(dropdown, anchor){
    removeFloating();
    const clone = dropdown.cloneNode(true);
    const wrap = document.createElement('div'); wrap.className = 'webrun-floating-submenu';
    wrap.appendChild(clone);
    document.body.appendChild(wrap);
    // position near anchor
    const r = anchor.getBoundingClientRect();
    // prefer right side of sidebar (anchors inside sidebar are left aligned)
    const left = r.right + 8; // gap
    const top = Math.max(8, r.top - 4);
    wrap.style.left = left + 'px';
    wrap.style.top = top + 'px';

    // close on outside click or scroll/resize
    function onDocClick(e){ if (!wrap.contains(e.target) && !anchor.contains(e.target)) removeFloating(); }
    function onScroll(){ removeFloating(); }
    setTimeout(()=>{ document.addEventListener('click', onDocClick); window.addEventListener('scroll', onScroll, true); window.addEventListener('resize', onScroll); }, 0);

    // delegate clicks inside floating to behave like native dropdown items
    wrap.addEventListener('click', function(ev){
      const a = ev.target.closest('a,button');
      if (!a) return;
      ev.preventDefault();
      // dispatch same click on original menu if exists
      const href = a.getAttribute('href');
      if (href && href !== '#') {
        window.location.href = href;
        return;
      }
      // if item has data-action attributes, try to find original and trigger
      const text = (a.textContent||'').trim();
      // try to find matching item in original dropdown and trigger click
      const originals = dropdown.querySelectorAll('a,button');
      for (const oa of originals){ if ((oa.textContent||'').trim() === text){ try{ oa.click(); }catch(e){} break; } }
      removeFloating();
    });
  }

  function initMenuFixes(){
    injectCss();
    // observe menu for collapsed state and attach click handler
    const rootObserver = new MutationObserver(function(){
      const menu = document.querySelector('#Menu');
      if (!menu) return;
      // attach delegated click
      if (!menu.dataset.funarpenFix){
        menu.dataset.funarpenFix = '1';
        menu.addEventListener('click', function(e){
          const target = e.target.closest('.dropdown-toggle, .has-submenu, .list-group-item');
          if (!target) return;
          const menuEl = document.querySelector('#Menu');
          if (menuEl && menuEl.classList.contains('collapsed')){
            // try to find dropdown menu inside this item
            const item = target.closest('.list-group-item');
            if (!item) return;
            const dropdown = item.querySelector('.dropdown-menu');
            if (dropdown){ e.preventDefault(); showFloating(dropdown, item); }
          }
        }, true);
      }
    });
    rootObserver.observe(document.documentElement, { childList:true, subtree:true });

    // initial run
    setTimeout(()=>{ const menu = document.querySelector('#Menu'); if (menu && !menu.dataset.funarpenFix){ initMenuFixes(); } }, 500);
  }

  // start when DOM ready
  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', initMenuFixes); else initMenuFixes();

})();
