(function () {
  // define a função pública que o loader chama
  console.log('📊 [GRAFICO] Iniciando script sprGraficoEstoque...');
  console.log('📊 [GRAFICO] URL:', window.location.href);
  window.sprGraficoEstoque = window.sprGraficoEstoque || function () {
    console.log('📊 [GRAFICO] Função sprGraficoEstoque executada');
    try {
      // cria root se não existir
      var rootId = 'spr-grafico-estoque-root';
      var root = document.getElementById(rootId);
      console.log('📊 [GRAFICO] Root element:', root ? 'ENCONTRADO' : 'NÃO ENCONTRADO');
      if (!root) {
        // tenta inserir numa sidebar conhecida, senão no body no topo
        var sidebar = document.querySelector('.fp-container, .fp-sidebar, #sidebar, .sidebar, .menu');
        console.log('📊 [GRAFICO] Sidebar:', sidebar ? 'ENCONTRADA' : 'NÃO ENCONTRADA');
        root = document.createElement('div');
        root.id = rootId;
        root.style.margin = '8px 0';
        if (sidebar) sidebar.appendChild(root); else document.body.insertBefore(root, document.body.firstChild);
        console.log('📊 [GRAFICO] Root criado e adicionado ao DOM');
      }

      // estilos mínimos
      if (!document.getElementById('spr-grafico-estoque-styles')) {
        console.log('📊 [GRAFICO] Criando estilos CSS...');
        var st = document.createElement('style'); st.id = 'spr-grafico-estoque-styles';
        st.innerHTML = '
          .spr - graf - card{ background: #fff; border: 1px solid #e6e6e6; padding: 12px; border - radius: 10px; box - shadow: 0 1px 3px rgba(0, 0, 0, 0.04); margin - bottom: 8px; font - family: Arial, sans - serif }
.spr - graf - title{ font - size: 11px; color:#616889; font - weight: 700; text - transform: uppercase; letter - spacing: 1px; margin - bottom: 8px }
.spr - bar{ height: 14px; background: #e6eef9; border - radius: 8px; overflow: hidden; margin: 3.6px 0 }
.spr - bar - fill{ height: 100 %; background:#2563eb; border - radius: 8px }
.spr - bar - label{ display: flex; justify - content: space - between; align - items: center; font - size: 12px; color:#111218; margin - bottom: 3.6px }
.spr - cert - valid{ color:#059669; font - weight: 700 }
.spr - cert - invalid{ color: #b91c1c; font - weight: 800 }
.spr - cert - warning{ margin - top: 6px; padding: 6px 8px; border - radius: 8px; border: 1px solid #fca5a5; background: #fff5f5; color: #b91c1c; font - weight: 700; display: inline - block; font - size: 13px }
.spr - cert - box{ border - left: 4px solid #2563eb; padding: 8px 10px; border - radius: 6px; box - shadow: 0 6px 18px rgba(37, 99, 235, 0.06); background: #fff }
        @keyframes sprBlink{ 50 % { opacity: 0.1 } }.spr - blink{ animation:sprBlink 1s linear infinite }
        ';
        document.head.appendChild(st);
        console.log('📊 [GRAFICO] Estilos CSS adicionados');
      }

      // limpa root
      root.innerHTML = '';

      // --- gráfico simples (consome endpoint de estoque) ---
      var grafCard = document.createElement('div'); grafCard.className = 'spr-graf-card';
      var title = document.createElement('div'); title.className = 'spr-graf-title'; title.textContent = 'Selos (estoque)';
      grafCard.appendChild(title);
      var content = document.createElement('div'); content.id = 'spr-graf-content';
      grafCard.appendChild(content);
      root.appendChild(grafCard);

      function renderChart(data) {
        console.log('📊 [GRAFICO] Renderizando gráfico com dados:', data);
        content.innerHTML = '';
        try {
          var total = 0; for (var k in data) { total += Number(data[k] || 0); }
          var keys = Object.keys(data).slice(0, 6);
          if (keys.length === 0) { content.innerHTML = '<div style="color:#616889">Sem dados de estoque</div>'; return; }
          keys.forEach(function (k) {
            var v = Number(data[k] || 0);
            var labelRow = document.createElement('div'); labelRow.className = 'spr-bar-label';
            var lbl = document.createElement('div'); lbl.textContent = k; lbl.style.fontSize = '12px';
            var val = document.createElement('div'); val.textContent = v; val.style.fontWeight = '700';
            labelRow.appendChild(lbl); labelRow.appendChild(val);
            var bar = document.createElement('div'); bar.className = 'spr-bar';
            var fill = document.createElement('div'); fill.className = 'spr-bar-fill';
            var pct = total > 0 ? Math.max(2, Math.round((v / total) * 100)) : 0;
            fill.style.width = pct + '%';
            bar.appendChild(fill);
            content.appendChild(labelRow);
            content.appendChild(bar);
          });
        } catch (e) { content.innerHTML = '<div style="color:#b91c1c">Erro ao renderizar gráfico</div>'; console.error('[GRAFICO] Erro:', e); }
      }

      console.log('📊 [GRAFICO] Buscando dados do endpoint de estoque...');
      fetch('/funarpen/maker/api/funarpen/selos/cards/estoque')
        .then(function (r) {
          console.log('📊 [GRAFICO] Response do endpoint:', r.status, r.statusText);
          if (!r.ok) throw new Error('no data'); return r.json();
        })
        .then(function (j) {
          console.log('📊 [GRAFICO] Dados recebidos:', j);
          renderChart(j.estoque || j);
        })
        .catch(function (e) {
          console.error('📊 [GRAFICO] Erro ao buscar dados:', e);
          renderChart({});
        });

      // --- card do certificado ---
      console.log('📊 [GRAFICO] Criando card do certificado...');
      var certCard = document.createElement('div'); certCard.className = 'spr-graf-card';
      certCard.id = 'spr-cert-card';
      root.appendChild(certCard);
      certCard.innerHTML = '<div class="spr-graf-title">Certificado</div><div id="spr-cert-body">Carregando...</div>';

      function renderCert(info) {
        console.log('📊 [GRAFICO] Validando certificado:', info);
        var body = document.getElementById('spr-cert-body'); if (!body) return;
        body.innerHTML = '';
        if (!info) { body.innerHTML = '<div style="color:#616889">Informação indisponível</div>'; return; }
        if (info.valid !== true || info.matchesDoc === false || info.matchesName === false) {
          var card = document.createElement('div'); card.className = 'spr-cert-box';
          var p = document.createElement('div'); p.className = 'spr-cert-invalid'; p.textContent = 'Certificado inválido — verifique!'; card.appendChild(p);
          var msg = document.createElement('div'); msg.style.color = '#616889'; msg.style.fontSize = '12px'; msg.textContent = info.message || (info.subject || ''); card.appendChild(msg);
          body.appendChild(card);
          console.warn('📊 [GRAFICO] Certificado INVÁLIDO:', info);
          return;
        }
        var cardOk = document.createElement('div'); cardOk.className = 'spr-cert-box';
        var ok = document.createElement('div'); ok.className = 'spr-cert-valid'; ok.textContent = 'Certificado Válido!'; cardOk.appendChild(ok);
        if (info.notAfter) {
          var d = new Date(info.notAfter);
          var dias = Math.ceil((d - new Date()) / (1000 * 60 * 60 * 24));
          var expira = document.createElement('div'); expira.style.fontSize = '12px'; expira.style.color = '#616889';
          expira.textContent = 'Expira em: ' + d.toLocaleDateString('pt-BR') + ' (' + dias + ' dias)';
          cardOk.appendChild(expira);
          if (dias < 30) {
            var warn = document.createElement('div'); warn.className = 'spr-cert-warning'; warn.textContent = 'Expira em breve!';
            cardOk.appendChild(warn);
          }
        }
        body.appendChild(cardOk);
        console.log('📊 [GRAFICO] Certificado VÁLIDO:', info);
      }

      console.log('📊 [GRAFICO] Buscando dados do certificado...');
      fetch('/funarpen/maker/api/funarpen/certificado')
        .then(function (r) {
          console.log('📊 [GRAFICO] Response certificado:', r.status, r.statusText);
          if (!r.ok) throw new Error('no data'); return r.json();
        })
        .then(function (j) {
          console.log('📊 [GRAFICO] Dados do certificado recebidos:', j);
          renderCert(j);
        })
        .catch(function (e) {
          console.error('📊 [GRAFICO] Erro ao buscar certificado:', e);
          renderCert(null);
        });

    } catch (e) { console.error('📊 [GRAFICO] Erro geral:', e); }
  };
})();
