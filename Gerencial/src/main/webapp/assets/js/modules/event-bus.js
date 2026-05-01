// ============================================
// EVENT BUS - Otimizado para FASE 3
// Sistema de pub/sub com debouncing, throttling e cache
// ============================================

window.eventBus = (function () {
    'use strict';

    // Sistema de eventos
    var events = {};

    // Cache de eventos recentes para evitar duplicação
    var eventCache = {
        recent: {},
        lastEmitted: {},
        debouncedEvents: {},
        throttledEvents: {}
    };

    // Configurações de performance
    var config = {
        maxCacheSize: 100,
        cacheTTL: 5000, // 5 segundos
        debounceDefaults: {
            'ui:render': 16,     // ~60fps
            'input:change': 100, // Inputs
            'scroll': 50,        // Scroll
            'resize': 250        // Resize
        },
        throttleDefaults: {
            'mousemove': 100,
            'scroll': 100,
            'resize': 200
        },
        maxListenersPerEvent: 20
    };

    // Limpar cache periodicamente
    setInterval(function () {
        cleanExpiredCache();
    }, 30000);

    // ============================================
    // FUNÇÕES PRINCIPAIS OTIMIZADAS
    // ============================================

    /**
     * Registrar listener para evento
     * @param {string} event - Nome do evento
     * @param {Function} callback - Função callback
     * @param {Object} options - Opções (debounce, throttle, once)
     */
    function on(event, callback, options) {
        if (!event || typeof callback !== 'function') {
            console.warn('⚠️ eventBus.on() - Parâmetros inválidos');
            return function () { }; // Retorna função vazia para unsubscribe
        }

        // Verificar limite de listeners
        if (!events[event]) {
            events[event] = [];
        }

        if (events[event].length >= config.maxListenersPerEvent) {
            console.warn('⚠️ Limite de listeners atingido para:', event);
            // Remover o listener mais antigo
            events[event].shift();
        }

        // Criar wrapper otimizado
        var wrapper = createListenerWrapper(callback, options, event);

        events[event].push({
            callback: wrapper,
            original: callback,
            options: options || {},
            id: generateId()
        });

        // Retornar função para unsubscribe
        return function () {
            off(event, callback);
        };
    }

    /**
     * Criar wrapper otimizado para listener
     */
    function createListenerWrapper(callback, options, eventName) {
        if (!options) return callback;

        var wrappedCallback = callback;

        // Aplicar debouncing
        if (options.debounce) {
            var delay = typeof options.debounce === 'number'
                ? options.debounce
                : (config.debounceDefaults[eventName] || 100);

            wrappedCallback = createDebouncedFunction(wrappedCallback, delay, eventName);
        }

        // Aplicar throttling
        if (options.throttle) {
            var limit = typeof options.throttle === 'number'
                ? options.throttle
                : (config.throttleDefaults[eventName] || 100);

            wrappedCallback = createThrottledFunction(wrappedCallback, limit, eventName);
        }

        // Executar apenas uma vez
        if (options.once) {
            var executed = false;
            wrappedCallback = function () {
                if (!executed) {
                    executed = true;
                    callback.apply(this, arguments);
                }
            };
        }

        return wrappedCallback;
    }

    /**
     * Emitir evento com cache e otimizações
     * @param {string} event - Nome do evento
     * @param {*} data - Dados do evento
     * @param {Object} options - Opções (skipCache, force)
     */
    function emit(event, data, options) {
        if (!event) return;

        options = options || {};

        // Verificar cache para evitar eventos duplicados
        if (!options.force && shouldSkipEvent(event, data)) {
            return;
        }

        // Atualizar cache
        updateEventCache(event, data);

        // Executar listeners
        if (events[event]) {
            // Usar cópia para evitar problemas durante execução
            var listeners = events[event].slice();

            // Executar assincronamente para não bloquear
            setTimeout(function () {
                for (var i = 0; i < listeners.length; i++) {
                    try {
                        listeners[i].callback(data);
                    } catch (error) {
                        console.error('❌ Erro no listener de', event, ':', error);
                        handleListenerError(error, event, listeners[i]);
                    }
                }
            }, 0);
        }

        // Log apenas em desenvolvimento
        if (window.debugMode) {
            console.log('📢 eventBus.emit:', event, data);
        }
    }

    /**
     * Remover listener
     */
    function off(event, callback) {
        if (!events[event]) return;

        if (callback) {
            // Remover listener específico
            events[event] = events[event].filter(function (listener) {
                return listener.original !== callback;
            });
        } else {
            // Remover todos os listeners do evento
            events[event] = [];
        }

        // Remover evento se não tem mais listeners
        if (events[event].length === 0) {
            delete events[event];
        }
    }

    /**
     * Emitir evento apenas uma vez
     */
    function once(event, callback, options) {
        var newOptions = Object.assign({}, options, { once: true });
        return on(event, callback, newOptions);
    }

    /**
     * Emitir erro de forma estruturada
     */
    function emitirErro(modulo, erro, dadosAdicionais) {
        var erroEvento = {
            modulo: modulo,
            erro: erro,
            mensagem: erro ? erro.message : 'Erro desconhecido',
            dados: dadosAdicionais || {},
            timestamp: new Date().toISOString(),
            stack: erro ? erro.stack : null
        };

        emit(EVENTOS.APP_ERRO, erroEvento);

        // Log para desenvolvedor
        console.group('🔴 ERRO DO SISTEMA');
        console.error('Módulo:', modulo);
        console.error('Erro:', erro);
        console.error('Dados:', dadosAdicionais);
        console.groupEnd();
    }

    // ============================================
    // FUNÇÕES DE PERFORMANCE (FASE 3)
    // ============================================

    /**
     * Verificar se deve pular evento (cache)
     */
    function shouldSkipEvent(event, data) {
        var cacheKey = event + '_' + JSON.stringify(data);
        var now = Date.now();

        // Verificar se é evento recente idêntico
        if (eventCache.recent[cacheKey] &&
            (now - eventCache.recent[cacheKey].timestamp) < config.cacheTTL) {
            return true;
        }

        // Verificar se é evento idêntico ao último emitido
        if (eventCache.lastEmitted[event]) {
            var lastData = eventCache.lastEmitted[event].data;
            var lastTime = eventCache.lastEmitted[event].timestamp;

            if (JSON.stringify(lastData) === JSON.stringify(data) &&
                (now - lastTime) < 100) { // 100ms threshold
                return true;
            }
        }

        return false;
    }

    /**
     * Atualizar cache de eventos
     */
    function updateEventCache(event, data) {
        var cacheKey = event + '_' + JSON.stringify(data);
        var now = Date.now();

        // Limitar tamanho do cache
        if (Object.keys(eventCache.recent).length >= config.maxCacheSize) {
            var oldestKey = Object.keys(eventCache.recent)[0];
            delete eventCache.recent[oldestKey];
        }

        // Atualizar caches
        eventCache.recent[cacheKey] = {
            timestamp: now,
            data: data
        };

        eventCache.lastEmitted[event] = {
            timestamp: now,
            data: data
        };
    }

    /**
     * Criar função debounced
     */
    function createDebouncedFunction(fn, delay, eventName) {
        var timeoutId;

        return function () {
            var context = this;
            var args = arguments;

            clearTimeout(timeoutId);

            timeoutId = setTimeout(function () {
                fn.apply(context, args);
            }, delay);
        };
    }

    /**
     * Criar função throttled
     */
    function createThrottledFunction(fn, limit, eventName) {
        var lastRun;
        var timeoutId;

        return function () {
            var context = this;
            var args = arguments;
            var now = Date.now();

            if (!lastRun || (now - lastRun) >= limit) {
                fn.apply(context, args);
                lastRun = now;
            } else {
                clearTimeout(timeoutId);
                timeoutId = setTimeout(function () {
                    fn.apply(context, args);
                    lastRun = Date.now();
                }, limit - (now - lastRun));
            }
        };
    }

    /**
     * Limpar cache expirado
     */
    function cleanExpiredCache() {
        var now = Date.now();
        var removed = 0;

        for (var key in eventCache.recent) {
            if (eventCache.recent.hasOwnProperty(key)) {
                if ((now - eventCache.recent[key].timestamp) > config.cacheTTL) {
                    delete eventCache.recent[key];
                    removed++;
                }
            }
        }

        if (removed > 0 && window.debugMode) {
            console.log('🧹 eventBus - Cache limpo:', removed, 'itens');
        }
    }

    /**
     * Tratar erro em listener
     */
    function handleListenerError(error, event, listener) {
        // Remover listener problemático
        off(event, listener.original);

        // Notificar sobre o erro
        emitirErro('eventBus', new Error('Listener com erro removido: ' + event), {
            event: event,
            listenerId: listener.id,
            error: error.message
        });
    }

    // ============================================
    // UTILITÁRIOS
    // ============================================

    function generateId() {
        return Math.random().toString(36).substr(2, 9);
    }

    function getStats() {
        var totalListeners = 0;
        var eventsCount = {};

        for (var event in events) {
            if (events.hasOwnProperty(event)) {
                var count = events[event].length;
                eventsCount[event] = count;
                totalListeners += count;
            }
        }

        return {
            totalEvents: Object.keys(events).length,
            totalListeners: totalListeners,
            eventsCount: eventsCount,
            cacheSize: Object.keys(eventCache.recent).length,
            timestamp: new Date().toISOString()
        };
    }

    function clearAll() {
        events = {};
        eventCache = {
            recent: {},
            lastEmitted: {},
            debouncedEvents: {},
            throttledEvents: {}
        };
        console.log('🧹 eventBus - Todos os listeners e cache limpos');
    }

    // ============================================
    // CONSTANTES DE EVENTOS
    // ============================================

    var EVENTOS = {
        // Sistema
        APP_INICIALIZADA: 'app:inicializada',
        APP_ERRO: 'app:erro',
        LOADING_INICIADO: 'loading:iniciado',
        LOADING_FINALIZADO: 'loading:finalizado',

        // Opções
        OPCAO_SELECIONADA: 'opcao:selecionada',
        OPCOES_CARREGADAS: 'opcoes:carregadas',

        // Data
        DATA_ALTERADA: 'data:alterada',

        // Intervalo
        INTERVALO_PREENCHIDO: 'intervalo:preenchido',
        INTERVALO_LIMPO: 'intervalo:limpo',
        APONTAMENTOS_BUSCADOS: 'apontamentos:buscados',

        // Selos
        SELOS_CARREGADOS: 'selos:carregados',
        SELOS_ZERADOS_DETECTADOS: 'selos:zerados:detectados',
        CALCULO_INICIADO: 'calculo:iniciado',
        CALCULO_CONCLUIDO: 'calculo:concluido',

        // Selagem
        SELAGEM_CONCLUIDA: 'selagem:concluida',
        SELAGEM_FALHOU: 'selagem:falhou',

        // UI
        UI_COMPONENT_READY: 'ui:component:ready',
        TOAST_SHOW: 'toast:show',
        TOAST_HIDE: 'toast:hide',

        // Performance
        PERFORMANCE_METRICS: 'performance:metrics',
        CACHE_UPDATED: 'cache:updated'
    };

    // ============================================
    // API PÚBLICA
    // ============================================

    return {
        // Métodos principais
        on: on,
        off: off,
        emit: emit,
        once: once,
        emitirErro: emitirErro,

        // Métodos de performance
        getStats: getStats,
        clearAll: clearAll,

        // Constantes
        EVENTOS: EVENTOS,

        // Configuração
        setConfig: function (newConfig) {
            Object.assign(config, newConfig);
        },

        // Debug
        _debug: function () {
            return {
                events: events,
                cache: eventCache,
                config: config
            };
        }
    };
})();

// Auto-configuração
if (typeof window !== 'undefined') {
    setTimeout(function () {
        console.log('✅ eventBus.js OTIMIZADO - FASE 3');

        // Configurar debouncing automático para eventos comuns
        if (window.eventBus && window.eventBus.setConfig) {
            window.eventBus.setConfig({
                debounceDefaults: {
                    'resize': 250,
                    'scroll': 100,
                    'input:change': 150
                }
            });
        }
    }, 100);
}