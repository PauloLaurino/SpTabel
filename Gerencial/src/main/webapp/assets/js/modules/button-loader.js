// ============================================
// BUTTON LOADER - REFATORADO (FASE 2)
// Integrado com Event Bus e app-state
// ============================================

window.buttonLoader = (function() {
    'use strict';
    
    var estados = {};
    var eventosConfigurados = false;
    
    /**
     * Inicializar módulo
     */
    function inicializar() {
        console.log('🔄 [Button Loader] Inicializando...');
        
        prepararBotoes();
        configurarEventBus();
        
        return true;
    }
    
    /**
     * Configurar Event Bus listeners
     */
    function configurarEventBus() {
        // VERIFICAÇÃO ADICIONAR ANTES
        if (!window.eventBus || typeof window.eventBus.on !== 'function') {
            console.warn('⚠️ [Button Loader] Event Bus não disponível');
            return;
        }
        
        // Verificar se os eventos existem
        if (!window.eventBus.EVENTOS) {
            console.warn('⚠️ [Button Loader] EVENTOS não definidos');
            return;
        }
        
        var EB = window.eventBus.EVENTOS;
        
        // AGORA CONFIGURAR (certifique-se que callback é função)
        if (typeof onButtonClick === 'function') {
            window.eventBus.on(EB.BOTAO_CLICADO, onButtonClick);
        }
    }
    
    /**
     * Preparar botões
     */
    function prepararBotoes() {
        var botaoIds = [
            'btnSolicitarSelos',
            'btnSolicitarSelosEsquerdo',
            'btnSelarApontamentos',
            'btnSelarApontamentosEsquerdo'
        ];
        
        botaoIds.forEach(function(id) {
            var botao = document.getElementById(id);
            if (botao) {
                estados[id] = {
                    loading: false,
                    textoOriginal: botao.innerHTML.trim(),
                    elemento: botao
                };
            }
        });
    }
    
    /**
     * Mostrar loading para Solicitar Selos
     */
    function mostrarLoadingSolicitar() {
        return mostrarLoading('btnSolicitarSelos', 'solicitar');
    }
    
    /**
     * Mostrar loading para Selar
     */
    function mostrarLoadingSelar() {
        return mostrarLoading('btnSelarApontamentos', 'selar');
    }
    
    /**
     * Mostrar loading genérico
     */
    function mostrarLoading(botaoId, tipo) {
        var estado = estados[botaoId];
        if (!estado || !estado.elemento) return false;
        
        if (estado.loading) return true;
        
        estado.loading = true;
        estado.elemento.disabled = true;
        
        var conteudo = '';
        
        if (tipo === 'solicitar') {
            conteudo = '<i class="fas fa-spinner fa-spin"></i> Solicitando...';
        } else if (tipo === 'selar') {
            conteudo = '<i class="fas fa-spinner fa-spin"></i> Selando...';
        }
        
        estado.elemento.innerHTML = conteudo;
        estado.elemento.classList.add('btn-loading');
        
        // Notificar via Event Bus
        if (window.eventBus) {
            window.eventBus.emit('botao:loading:iniciado', {
                botaoId: botaoId,
                tipo: tipo
            });
        }
        
        console.log('🌀 [Button Loader] Loading mostrado:', botaoId);
        return true;
    }
    
    /**
     * Esconder loading
     */
    function esconderLoading(botaoId) {
        var estado = estados[botaoId];
        if (!estado || !estado.elemento) return false;
        
        estado.loading = false;
        estado.elemento.disabled = false;
        estado.elemento.innerHTML = estado.textoOriginal;
        estado.elemento.classList.remove('btn-loading');
        
        // Notificar
        if (window.eventBus) {
            window.eventBus.emit('botao:loading:finalizado', {
                botaoId: botaoId
            });
        }
        
        console.log('✅ [Button Loader] Loading escondido:', botaoId);
        return true;
    }
    
    function esconderLoadingSolicitar() {
        return esconderLoading('btnSolicitarSelos');
    }
    
    function esconderLoadingSelar() {
        return esconderLoading('btnSelarApontamentos');
    }
    
    function esconderTodosLoadings() {
        Object.keys(estados).forEach(function(botaoId) {
            esconderLoading(botaoId);
        });
    }
    
    /**
     * Verificar estado
     */
    function estaCarregando(botaoId) {
        return estados[botaoId] ? estados[botaoId].loading : false;
    }
    
    return {
        inicializar: inicializar,
        
        mostrarLoading: mostrarLoading,
        mostrarLoadingSolicitar: mostrarLoadingSolicitar,
        mostrarLoadingSelar: mostrarLoadingSelar,
        
        esconderLoading: esconderLoading,
        esconderLoadingSolicitar: esconderLoadingSolicitar,
        esconderLoadingSelar: esconderLoadingSelar,
        esconderTodosLoadings: esconderTodosLoadings,
        
        estaCarregando: estaCarregando,
        
        prepararBotoes: prepararBotoes
    };
})();

// Auto-inicialização
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function() {
        setTimeout(function() {
            if (window.buttonLoader && window.buttonLoader.inicializar) {
                window.buttonLoader.inicializar();
            }
        }, 100);
    });
} else {
    setTimeout(function() {
        if (window.buttonLoader && window.buttonLoader.inicializar) {
            window.buttonLoader.inicializar();
        }
    }, 100);
}

console.log('✅ button-loader.js REFATORADO - Nova arquitetura');