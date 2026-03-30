// ============================================
// FLOATING TOTALIZER - OTIMIZADO (FASE 2)
// ============================================

window.floatingTotalizer = (function() {
    
    var elemento = null;
    var estaVisivel = false;
    
    /**
     * Inicializar
     */
    function inicializar() {
        elemento = document.getElementById('floatingTotalizer');
        if (!elemento) {
            console.warn('⚠️ [Floating Totalizer] Elemento não encontrado');
            return;
        }
        
        configurarEventos();
        console.log('✅ [Floating Totalizer] Inicializado');
    }
    
    /**
     * Configurar eventos
     */
    function configurarEventos() {
        // Botão fechar
        var btnFechar = elemento.querySelector('[data-action="fechar"]');
        if (btnFechar) {
            btnFechar.addEventListener('click', esconder);
        }
        
        // Event Bus para atualizações
        if (window.eventBus) {
            window.eventBus.on('selos:solicitados:alterados', function(selos) {
                atualizarComSelos(selos);
            });
            
            window.eventBus.on('selos:solicitados:limpos', function() {
                limpar();
            });
        }
    }
    
    /**
     * Atualizar com selos
     */
    function atualizarComSelos(selos) {
        if (!elemento) return;
        
        var total = 0;
        var tipos = ['TP1', 'TP3', 'TP4', 'TPD', 'TPI'];
        var tiposComQuantidade = [];
        
        // Calcular total
        for (var i = 0; i < tipos.length; i++) {
            var tipo = tipos[i];
            var quantidade = selos[tipo] || 0;
            
            if (quantidade > 0) {
                total += quantidade;
                tiposComQuantidade.push({ tipo: tipo, quantidade: quantidade });
            }
        }
        
        // Atualizar UI
        atualizarUI(total, tiposComQuantidade);
        
        // Mostrar/ocultar
        if (total > 0) {
            mostrar();
        } else {
            esconder();
        }
    }
    
    /**
     * Atualizar UI
     */
    function atualizarUI(total, tiposComQuantidade) {
        var totalElement = elemento.querySelector('.total-value');
        var detalhesElement = elemento.querySelector('.total-details');
        var listaElement = elemento.querySelector('.selos-lista');
        
        if (totalElement) totalElement.textContent = total;
        
        if (detalhesElement) {
            detalhesElement.textContent = tiposComQuantidade.length + ' tipo(s)';
        }
        
        if (listaElement) {
            listaElement.innerHTML = '';
            
            tiposComQuantidade.forEach(function(item) {
                var itemElement = document.createElement('div');
                itemElement.className = 'selo-item';
                itemElement.innerHTML = '<span class="tipo">' + item.tipo + '</span>' +
                                      '<span class="quantidade">' + item.quantidade + '</span>';
                listaElement.appendChild(itemElement);
            });
        }
    }
    
    function mostrar() {
        if (elemento && !estaVisivel) {
            elemento.classList.add('visivel');
            estaVisivel = true;
        }
    }
    
    function esconder() {
        if (elemento && estaVisivel) {
            elemento.classList.remove('visivel');
            estaVisivel = false;
        }
    }
    
    function limpar() {
        if (elemento) {
            var totalElement = elemento.querySelector('.total-value');
            var detalhesElement = elemento.querySelector('.total-details');
            var listaElement = elemento.querySelector('.selos-lista');
            
            if (totalElement) totalElement.textContent = '0';
            if (detalhesElement) detalhesElement.textContent = 'Nenhum selo';
            if (listaElement) listaElement.innerHTML = '';
            
            esconder();
        }
    }
    
    return {
        inicializar: inicializar,
        atualizar: atualizarComSelos,
        mostrar: mostrar,
        esconder: esconder,
        limpar: limpar,
        estaVisivel: function() { return estaVisivel; }
    };
})();

console.log('✅ floating-totalizer.js OTIMIZADO');