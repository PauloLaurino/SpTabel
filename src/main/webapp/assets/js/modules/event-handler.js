// ============================================
// EVENT HANDLER - Wrapper de compatibilidade (FASE 2)
// Mantém API antiga enquanto módulos são migrados
// ============================================

window.eventHandler = (function () {
    'use strict';

    console.log('⚠️ event-handler.js carregado (wrapper de compatibilidade)');

    function configurar(app) {
        console.warn('⚠️ eventHandler.configurar() é obsoleto - Use controllers especializados');

        // Para compatibilidade, inicializar controllers se necessário
        if (window.buttonController && window.buttonController.inicializar) {
            window.buttonController.inicializar(app);
        }

        if (window.inputController && window.inputController.inicializar) {
            window.inputController.inicializar(app);
        }

        return true;
    }

    // Funções mantidas para compatibilidade
    function verificarCamposPreenchidos(app) {
        if (window.buttonController && window.buttonController.verificarEstadoBotoes) {
            return window.buttonController.verificarEstadoBotoes();
        }
        return {};
    }

    function limparSelosSolicitados(app) {
        if (window.selosManager && window.selosManager.limparSelosSolicitados) {
            window.selosManager.limparSelosSolicitados(app);
        }
    }

    // Exportar funções de compatibilidade
    return {
        configurar: configurar,
        verificarCamposPreenchidos: verificarCamposPreenchidos,
        limparSelosSolicitados: limparSelosSolicitados,

        // Mensagem de depreciação
        _deprecated: 'Este módulo será removido na próxima versão. Use controllers especializados.'
    };
})();

console.log('✅ event-handler.js (compatibilidade) carregado');