// ============================================
// CACHE MANAGER - Sistema avançado de cache para FASE 3
// ============================================

window.cacheManager = (function() {
    'use strict';
    
    var caches = {
        // Cache de API
        api: {
            data: {},
            config: {
                defaultTTL: 5 * 60 * 1000, // 5 minutos
                maxSize: 100,
                cleanupInterval: 60 * 1000 // Limpar a cada minuto
            }
        },
        
        // Cache de buscas
        busca: {
            data: {},
            config: {
                defaultTTL: 2 * 60 * 1000, // 2 minutos
                maxSize: 50
            }
        },
        
        // Cache de cálculos
        calculo: {
            data: {},
            config: {
                defaultTTL: 10 * 60 * 1000, // 10 minutos
                maxSize: 30
            }
        },
        
        // Cache de UI
        ui: {
            data: {},
            config: {
                defaultTTL: 30 * 60 * 1000, // 30 minutos
                maxSize: 1000
            }
        }
    };
    
    // ============================================
    // INICIALIZAÇÃO
    // ============================================
    
    function inicializar() {
        console.log('💾 Cache Manager inicializando...');
        
        // Configurar limpeza automática
        configurarLimpezaAutomatica();
        
        // Configurar Event Bus
        configurarEventBus();
        
        console.log('✅ Cache Manager inicializado');
    }
    
    function configurarEventBus() {
        if (!window.eventBus) return;
        
        var EB = window.eventBus.EVENTOS;
        
        // Limpar cache quando selagem for concluída
        window.eventBus.on(EB.SELAGEM_CONCLUIDA, function() {
            invalidarCache('calculo');
            invalidarCache('busca');
            console.log('🧹 Cache invalidado após selagem');
        });
        
        // Limpar cache quando dados mudarem
        window.eventBus.on(EB.OPCAO_SELECIONADA, function() {
            invalidarCache('busca');
            console.log('🧹 Cache de busca invalidado (opção alterada)');
        });
        
        // Limpar cache quando usuário mudar
        window.eventBus.on(EB.USUARIO_ALTERADO, function() {
            invalidarTodos();
            console.log('🧹 Todos os caches invalidados (usuário alterado)');
        });
    }
    
    function configurarLimpezaAutomatica() {
        setInterval(function() {
            limparExpirados();
        }, caches.api.config.cleanupInterval);
    }
    
    // ============================================
    // GERENCIAMENTO DE CACHE
    // ============================================
    
    /**
     * Obter item do cache
     * @param {string} categoria - api, busca, calculo, ui
     * @param {string} chave - Chave do cache
     * @returns {*} Dados em cache ou null
     */
    function obter(categoria, chave) {
        if (!caches[categoria] || !caches[categoria].data[chave]) {
            return null;
        }
        
        var item = caches[categoria].data[chave];
        
        // Verificar se expirou
        if (Date.now() > item.expiraEm) {
            remover(categoria, chave);
            return null;
        }
        
        // Atualizar último acesso
        item.ultimoAcesso = Date.now();
        
        return item.dados;
    }
    
    /**
     * Armazenar item no cache
     * @param {string} categoria - api, busca, calculo, ui
     * @param {string} chave - Chave do cache
     * @param {*} dados - Dados para cache
     * @param {number} ttl - TTL em ms (opcional)
     */
    function armazenar(categoria, chave, dados, ttl) {
        if (!caches[categoria]) {
            console.warn('⚠️ Categoria de cache não encontrada:', categoria);
            return false;
        }
        
        if (!ttl) {
            ttl = caches[categoria].config.defaultTTL;
        }
        
        // Verificar limite de tamanho
        if (Object.keys(caches[categoria].data).length >= caches[categoria].config.maxSize) {
            removerMaisAntigo(categoria);
        }
        
        caches[categoria].data[chave] = {
            dados: dados,
            criadoEm: Date.now(),
            expiraEm: Date.now() + ttl,
            ultimoAcesso: Date.now(),
            acessos: 1
        };
        
        return true;
    }
    
    /**
     * Remover item do cache
     */
    function remover(categoria, chave) {
        if (caches[categoria] && caches[categoria].data[chave]) {
            delete caches[categoria].data[chave];
            return true;
        }
        return false;
    }
    
    /**
     * Invalidar cache completo de uma categoria
     */
    function invalidarCache(categoria) {
        if (caches[categoria]) {
            caches[categoria].data = {};
            console.log('🧹 Cache invalidado:', categoria);
            return true;
        }
        return false;
    }
    
    /**
     * Invalidar todos os caches
     */
    function invalidarTodos() {
        for (var categoria in caches) {
            if (caches.hasOwnProperty(categoria)) {
                caches[categoria].data = {};
            }
        }
        console.log('🧹 Todos os caches invalidados');
    }
    
    /**
     * Remover item mais antigo
     */
    function removerMaisAntigo(categoria) {
        if (!caches[categoria] || Object.keys(caches[categoria].data).length === 0) {
            return;
        }
        
        var chaveMaisAntiga = null;
        var menorTimestamp = Infinity;
        
        for (var chave in caches[categoria].data) {
            if (caches[categoria].data.hasOwnProperty(chave)) {
                var item = caches[categoria].data[chave];
                if (item.ultimoAcesso < menorTimestamp) {
                    menorTimestamp = item.ultimoAcesso;
                    chaveMaisAntiga = chave;
                }
            }
        }
        
        if (chaveMaisAntiga) {
            delete caches[categoria].data[chaveMaisAntiga];
            console.log('🗑️ Removido do cache:', categoria, chaveMaisAntiga);
        }
    }
    
    /**
     * Limpar itens expirados
     */
    function limparExpirados() {
        var agora = Date.now();
        var removidos = 0;
        
        for (var categoria in caches) {
            if (caches.hasOwnProperty(categoria)) {
                for (var chave in caches[categoria].data) {
                    if (caches[categoria].data.hasOwnProperty(chave)) {
                        var item = caches[categoria].data[chave];
                        if (agora > item.expiraEm) {
                            delete caches[categoria].data[chave];
                            removidos++;
                        }
                    }
                }
            }
        }
        
        if (removidos > 0) {
            console.log('🧹 Removidos', removidos, 'itens expirados do cache');
        }
    }
    
    // ============================================
    // CACHE ESPECÍFICO PARA API
    // ============================================
    
    /**
     * Cache para chamadas API
     */
    function cacheAPI(endpoint, params, dados) {
        var chave = criarChaveAPI(endpoint, params);
        return armazenar('api', chave, dados);
    }
    
    function obterCacheAPI(endpoint, params) {
        var chave = criarChaveAPI(endpoint, params);
        return obter('api', chave);
    }
    
    function criarChaveAPI(endpoint, params) {
        var chave = endpoint;
        if (params) {
            chave += '?' + JSON.stringify(params);
        }
        return chave;
    }
    
    // ============================================
    // CACHE ESPECÍFICO PARA BUSCAS
    // ============================================
    
    /**
     * Cache para buscas de intervalo
     */
    function cacheBusca(data, codigoOperacao, dados) {
        var chave = 'busca_' + data + '_' + codigoOperacao;
        return armazenar('busca', chave, dados);
    }
    
    function obterCacheBusca(data, codigoOperacao) {
        var chave = 'busca_' + data + '_' + codigoOperacao;
        return obter('busca', chave);
    }
    
    // ============================================
    // CACHE ESPECÍFICO PARA CÁLCULOS
    // ============================================
    
    /**
     * Cache para cálculos de selos
     */
    function cacheCalculo(dados, resultado) {
        var chave = 'calculo_' + JSON.stringify(dados);
        return armazenar('calculo', chave, resultado);
    }
    
    function obterCacheCalculo(dados) {
        var chave = 'calculo_' + JSON.stringify(dados);
        return obter('calculo', chave);
    }
    
    // ============================================
    // ESTATÍSTICAS E MONITORAMENTO
    // ============================================
    
    function obterEstatisticas() {
        var stats = {
            totalItens: 0,
            porCategoria: {},
            taxaAcerto: {},
            memoriaEstimada: 0
        };
        
        for (var categoria in caches) {
            if (caches.hasOwnProperty(categoria)) {
                var itens = Object.keys(caches[categoria].data).length;
                stats.porCategoria[categoria] = itens;
                stats.totalItens += itens;
            }
        }
        
        // Estimar uso de memória (aproximado)
        stats.memoriaEstimada = stats.totalItens * 2; // KB aproximado
        
        return stats;
    }
    
    function gerarRelatorio() {
        var stats = obterEstatisticas();
        var relatorio = [
            '📊 RELATÓRIO DO CACHE MANAGER',
            '==============================',
            'Total de itens em cache: ' + stats.totalItens,
            'Memória estimada: ' + stats.memoriaEstimada + ' KB',
            '',
            'Por categoria:'
        ];
        
        for (var categoria in stats.porCategoria) {
            relatorio.push('  • ' + categoria + ': ' + stats.porCategoria[categoria] + ' itens');
        }
        
        relatorio.push('', 'Última limpeza: ' + new Date().toLocaleTimeString());
        
        return relatorio.join('\n');
    }
    
    // ============================================
    // API PÚBLICA
    // ============================================
    
    return {
        // Inicialização
        inicializar: inicializar,
        
        // Gerenciamento geral
        obter: obter,
        armazenar: armazenar,
        remover: remover,
        invalidarCache: invalidarCache,
        invalidarTodos: invalidarTodos,
        limparExpirados: limparExpirados,
        
        // Cache específico
        cacheAPI: cacheAPI,
        obterCacheAPI: obterCacheAPI,
        cacheBusca: cacheBusca,
        obterCacheBusca: obterCacheBusca,
        cacheCalculo: cacheCalculo,
        obterCacheCalculo: obterCacheCalculo,
        
        // Monitoramento
        obterEstatisticas: obterEstatisticas,
        gerarRelatorio: gerarRelatorio,
        
        // Para debug
        _getCaches: function() { return caches; },
        _clearAll: function() {
            invalidarTodos();
            return true;
        }
    };
})();

console.log('✅ cache-manager.js criado - FASE 3.2');