// ============================================
// APP STATE - Otimizado para FASE 3
// Gerenciamento de estado com cache, persistência e otimizações
// ============================================

window.appState = (function () {
    'use strict';

    // Estado inicial otimizado
    var estado = {
        // Dados do usuário
        usuario: 'CONVIDADO',
        permissao: 'BASICA',

        // Opção selecionada
        opcaoSelecionada: null,

        // Selos
        selosDisponiveis: {},
        selosSolicitados: {},
        selosZerados: [],

        // Apontamentos
        dadosPrimeiroApontamento: null,
        dadosUltimoApontamento: null,

        // Estado da aplicação
        buscandoIntervalo: false,
        calculandoSelos: false,
        selando: false,

        // Cache e timestamps
        ultimaBusca: null,
        ultimoCalculo: null,
        ultimaSelagem: null,

        // Configurações
        config: {
            autoBuscar: true,
            autoCalcular: false,
            mostrarAlertas: true,
            modoCompacto: false
        },

        // Performance
        metrics: {
            stateUpdates: 0,
            lastUpdate: null,
            averageUpdateTime: 0
        }
    };

    // Cache de estados anteriores (para undo/redo ou debug)
    var stateHistory = {
        past: [],
        future: [],
        maxSize: 10,
        enabled: true
    };

    // Listeners otimizados com debouncing
    var listeners = {
        all: [],
        specific: {}
    };

    // Cache de seletores DOM frequentes
    var domCache = {};

    // ============================================
    // INICIALIZAÇÃO
    // ============================================

    function inicializar(config) {
        console.log('📊 App State inicializando (FASE 3)...');

        // Carregar estado persistente se disponível
        carregarEstadoPersistente();

        // Configurar limpeza automática de cache
        configurarLimpezaAutomatica();

        // Configurar Event Bus
        configurarEventBus();

        console.log('✅ App State inicializado com otimizações');
        return estado;
    }

    function configurarEventBus() {
        if (!window.eventBus) return;

        var EB = window.eventBus.EVENTOS;

        // Atualizar estado baseado em eventos
        window.eventBus.on(EB.SELAGEM_CONCLUIDA, function (resultado) {
            if (resultado.success) {
                atualizarEstado({
                    ultimaSelagem: new Date().toISOString(),
                    selando: false
                });
            }
        });

        // Sincronizar selos zerados
        window.eventBus.on(EB.SELOS_ZERADOS_DETECTADOS, function (data) {
            if (data.selosZerados) {
                setSelosZerados(data.selosZerados);
            }
        });
    }

    function configurarLimpezaAutomatica() {
        // Limpar cache DOM a cada 5 minutos
        setInterval(function () {
            limparCacheDOM();
        }, 5 * 60 * 1000);

        // Limpar histórico antigo a cada hora
        setInterval(function () {
            limparHistóricoAntigo();
        }, 60 * 60 * 1000);
    }

    // ============================================
    // GERENCIAMENTO DE ESTADO OTIMIZADO
    // ============================================

    /**
     * Atualizar estado com otimizações de performance
     * @param {Object} updates - Atualizações
     * @param {Object} options - Opções
     */
    function atualizarEstado(updates, options) {
        if (!updates || typeof updates !== 'object') {
            console.warn('⚠️ atualizarEstado: updates deve ser objeto');
            return false;
        }

        options = options || {};
        var startTime = performance.now();

        // Verificar se há mudanças reais
        var hasChanges = false;
        for (var key in updates) {
            if (updates.hasOwnProperty(key) && !isEqual(estado[key], updates[key])) {
                hasChanges = true;
                break;
            }
        }

        if (!hasChanges && !options.force) {
            // console.log('ℹ️ Nenhuma mudança no estado, ignorando');
            return false;
        }

        // Salvar no histórico
        if (stateHistory.enabled && !options.skipHistory) {
            saveToHistory();
        }

        // Aplicar atualizações
        var oldState = JSON.parse(JSON.stringify(estado));

        for (var key in updates) {
            if (updates.hasOwnProperty(key)) {
                // Atualização profunda para objetos
                if (typeof updates[key] === 'object' && updates[key] !== null &&
                    typeof estado[key] === 'object' && estado[key] !== null) {

                    estado[key] = Object.assign({}, estado[key], updates[key]);
                } else {
                    estado[key] = updates[key];
                }
            }
        }

        // Atualizar métricas
        var endTime = performance.now();
        var updateTime = endTime - startTime;

        estado.metrics.stateUpdates++;
        estado.metrics.lastUpdate = new Date().toISOString();
        estado.metrics.averageUpdateTime =
            ((estado.metrics.averageUpdateTime * (estado.metrics.stateUpdates - 1)) + updateTime) /
            estado.metrics.stateUpdates;

        // Notificar listeners com debouncing
        notifyListeners(oldState, updates, options);

        // Persistir se necessário
        if (!options.skipPersist && updates.config) {
            persistirEstado();
        }

        // Log de performance em desenvolvimento
        if (window.debugMode && updateTime > 10) {
            console.log('⏱️ Estado atualizado em', updateTime.toFixed(2), 'ms');
        }

        return true;
    }

    /**
     * Notificar listeners otimizado
     */
    function notifyListeners(oldState, updates, options) {
        if (listeners.all.length === 0 && Object.keys(listeners.specific).length === 0) {
            return;
        }

        // Debounce para múltiplas atualizações rápidas
        clearTimeout(notifyListeners.debounceTimer);

        notifyListeners.debounceTimer = setTimeout(function () {
            // Notificar listeners gerais
            for (var i = 0; i < listeners.all.length; i++) {
                try {
                    listeners.all[i](estado, updates, oldState);
                } catch (error) {
                    console.error('❌ Erro no listener geral:', error);
                    removeListener(listeners.all[i]);
                }
            }

            // Notificar listeners específicos
            for (var key in updates) {
                if (updates.hasOwnProperty(key) && listeners.specific[key]) {
                    var specificListeners = listeners.specific[key].slice();

                    for (var j = 0; j < specificListeners.length; j++) {
                        try {
                            specificListeners[j](estado[key], updates[key], key);
                        } catch (error) {
                            console.error('❌ Erro no listener específico [' + key + ']:', error);
                            removeSpecificListener(key, specificListeners[j]);
                        }
                    }
                }
            }
        }, options.debounce || 10); // Debounce padrão de 10ms
    }

    // ============================================
    // GETTERS OTIMIZADOS COM CACHE
    // ============================================

    function getEstado() {
        return Object.assign({}, estado);
    }

    function getUsuario() {
        return estado.usuario;
    }

    function getOpcaoSelecionada() {
        return estado.opcaoSelecionada;
    }

    function getSelosDisponiveis() {
        return Object.assign({}, estado.selosDisponiveis);
    }

    function getSelosSolicitados() {
        return Object.assign({}, estado.selosSolicitados);
    }

    function getSelosZerados() {
        return estado.selosZerados.slice();
    }

    function getDadosPrimeiroApontamento() {
        return estado.dadosPrimeiroApontamento ?
            Object.assign({}, estado.dadosPrimeiroApontamento) : null;
    }

    function getDadosUltimoApontamento() {
        return estado.dadosUltimoApontamento ?
            Object.assign({}, estado.dadosUltimoApontamento) : null;
    }

    function getConfig() {
        return Object.assign({}, estado.config);
    }

    function getMetrics() {
        return Object.assign({}, estado.metrics);
    }

    // ============================================
    // SETTERS OTIMIZADOS
    // ============================================

    function setUsuario(usuario) {
        return atualizarEstado({ usuario: usuario });
    }

    function setOpcaoSelecionada(opcao) {
        return atualizarEstado({ opcaoSelecionada: opcao });
    }

    function setSelosDisponiveis(selos) {
        return atualizarEstado({ selosDisponiveis: selos });
    }

    function setSelosSolicitados(selos) {
        return atualizarEstado({ selosSolicitados: selos });
    }

    function setSelosZerados(selosZerados) {
        return atualizarEstado({ selosZerados: selosZerados });
    }

    function setDadosPrimeiroApontamento(dados) {
        return atualizarEstado({ dadosPrimeiroApontamento: dados });
    }

    function setDadosUltimoApontamento(dados) {
        return atualizarEstado({ dadosUltimoApontamento: dados });
    }

    function setBuscandoIntervalo(buscando) {
        return atualizarEstado({ buscandoIntervalo: buscando });
    }

    function setCalculandoSelos(calculando) {
        return atualizarEstado({ calculandoSelos: calculando });
    }

    function setUltimaBusca(timestamp) {
        return atualizarEstado({ ultimaBusca: timestamp });
    }

    function setConfig(configUpdates) {
        return atualizarEstado({
            config: Object.assign({}, estado.config, configUpdates)
        });
    }

    // ============================================
    // CHECKERS OTIMIZADOS
    // ============================================

    function isBuscandoIntervalo() {
        return estado.buscandoIntervalo === true;
    }

    function isCalculandoSelos() {
        return estado.calculandoSelos === true;
    }

    function isSelando() {
        return estado.selando === true;
    }

    function hasOpcaoSelecionada() {
        return estado.opcaoSelecionada !== null;
    }

    function hasIntervaloPreenchido() {
        return estado.dadosPrimeiroApontamento !== null &&
            estado.dadosUltimoApontamento !== null;
    }

    function hasSelosSolicitados() {
        var selos = estado.selosSolicitados;
        return selos.TP1 > 0 || selos.TP3 > 0 || selos.TP4 > 0 ||
            selos.TPD > 0 || selos.TPI > 0;
    }

    // ============================================
    // LISTENERS OTIMIZADOS
    // ============================================

    function addListener(callback) {
        if (typeof callback !== 'function') return;

        listeners.all.push(callback);

        // Retornar função para remover
        return function () {
            removeListener(callback);
        };
    }

    function removeListener(callback) {
        var index = listeners.all.indexOf(callback);
        if (index !== -1) {
            listeners.all.splice(index, 1);
        }
    }

    function addSpecificListener(key, callback) {
        if (!key || typeof callback !== 'function') return;

        if (!listeners.specific[key]) {
            listeners.specific[key] = [];
        }

        listeners.specific[key].push(callback);

        // Retornar função para remover
        return function () {
            removeSpecificListener(key, callback);
        };
    }

    function removeSpecificListener(key, callback) {
        if (listeners.specific[key]) {
            var index = listeners.specific[key].indexOf(callback);
            if (index !== -1) {
                listeners.specific[key].splice(index, 1);
            }

            // Limpar array vazio
            if (listeners.specific[key].length === 0) {
                delete listeners.specific[key];
            }
        }
    }

    // ============================================
    // CACHE E PERFORMANCE
    // ============================================

    /**
     * Cache de elementos DOM frequentes
     */
    function getElementoDOM(id, forceRefresh) {
        if (!forceRefresh && domCache[id]) {
            return domCache[id];
        }

        var elemento = document.getElementById(id);
        if (elemento) {
            domCache[id] = elemento;
        }

        return elemento;
    }

    function limparCacheDOM() {
        var count = Object.keys(domCache).length;
        domCache = {};

        if (window.debugMode && count > 0) {
            console.log('🧹 DOM Cache limpo:', count, 'elementos');
        }
    }

    /**
     * Histórico de estados
     */
    function saveToHistory() {
        if (!stateHistory.enabled) return;

        // Limitar tamanho do histórico
        if (stateHistory.past.length >= stateHistory.maxSize) {
            stateHistory.past.shift();
        }

        stateHistory.past.push(JSON.parse(JSON.stringify(estado)));
        stateHistory.future = []; // Limpar futuro ao salvar novo estado
    }

    function undo() {
        if (stateHistory.past.length === 0) return false;

        // Salvar estado atual no futuro
        stateHistory.future.unshift(JSON.parse(JSON.stringify(estado)));

        // Restaurar estado anterior
        var previousState = stateHistory.past.pop();
        estado = previousState;

        // Notificar listeners
        notifyListeners(estado, estado, { force: true });

        return true;
    }

    function redo() {
        if (stateHistory.future.length === 0) return false;

        // Salvar estado atual no passado
        stateHistory.past.push(JSON.parse(JSON.stringify(estado)));

        // Restaurar estado futuro
        var nextState = stateHistory.future.shift();
        estado = nextState;

        // Notificar listeners
        notifyListeners(estado, estado, { force: true });

        return true;
    }

    function limparHistóricoAntigo() {
        // Manter apenas os últimos estados
        var maxHistory = 5;
        if (stateHistory.past.length > maxHistory) {
            stateHistory.past = stateHistory.past.slice(-maxHistory);
        }
        if (stateHistory.future.length > maxHistory) {
            stateHistory.future = stateHistory.future.slice(-maxHistory);
        }
    }

    // ============================================
    // PERSISTÊNCIA
    // ============================================

    function persistirEstado() {
        try {
            var estadoParaPersistir = {
                usuario: estado.usuario,
                config: estado.config,
                ultimaSelagem: estado.ultimaSelagem,
                timestamp: new Date().toISOString()
            };

            localStorage.setItem('selador_app_state', JSON.stringify(estadoParaPersistir));

            if (window.debugMode) {
                console.log('💾 Estado persistido');
            }
        } catch (error) {
            console.warn('⚠️ Não foi possível persistir estado:', error);
        }
    }

    function carregarEstadoPersistente() {
        try {
            var persistido = localStorage.getItem('selador_app_state');
            if (persistido) {
                var estadoPersistido = JSON.parse(persistido);

                // Aplicar configurações persistidas
                if (estadoPersistido.config) {
                    estado.config = Object.assign({}, estado.config, estadoPersistido.config);
                }

                if (estadoPersistido.usuario && estadoPersistido.usuario !== 'CONVIDADO') {
                    estado.usuario = estadoPersistido.usuario;
                }

                console.log('📂 Estado persistente carregado');
            }
        } catch (error) {
            console.warn('⚠️ Não foi possível carregar estado persistente:', error);
            localStorage.removeItem('selador_app_state');
        }
    }

    function limparPersistencia() {
        try {
            localStorage.removeItem('selador_app_state');
            console.log('🧹 Persistência limpa');
        } catch (error) {
            console.warn('⚠️ Erro ao limpar persistência:', error);
        }
    }

    // ============================================
    // UTILITÁRIOS
    // ============================================

    function isEqual(obj1, obj2) {
        if (obj1 === obj2) return true;
        if (typeof obj1 !== typeof obj2) return false;

        if (typeof obj1 !== 'object' || obj1 === null || obj2 === null) {
            return obj1 === obj2;
        }

        var keys1 = Object.keys(obj1);
        var keys2 = Object.keys(obj2);

        if (keys1.length !== keys2.length) return false;

        for (var i = 0; i < keys1.length; i++) {
            var key = keys1[i];
            if (!isEqual(obj1[key], obj2[key])) {
                return false;
            }
        }

        return true;
    }

    function getRelatorio() {
        return {
            estado: {
                usuario: estado.usuario,
                opcaoSelecionada: estado.opcaoSelecionada ? 'Sim' : 'Não',
                selosDisponiveis: Object.keys(estado.selosDisponiveis).length,
                selosSolicitados: Object.keys(estado.selosSolicitados).length,
                hasIntervalo: hasIntervaloPreenchido() ? 'Sim' : 'Não'
            },
            metrics: estado.metrics,
            listeners: {
                all: listeners.all.length,
                specific: Object.keys(listeners.specific).length
            },
            cache: {
                dom: Object.keys(domCache).length,
                history: {
                    past: stateHistory.past.length,
                    future: stateHistory.future.length
                }
            },
            timestamp: new Date().toISOString()
        };
    }

    // ============================================
    // API PÚBLICA
    // ============================================

    return {
        // Inicialização
        inicializar: inicializar,

        // Gerenciamento de estado
        atualizarEstado: atualizarEstado,
        getEstado: getEstado,

        // Getters
        getUsuario: getUsuario,
        getOpcaoSelecionada: getOpcaoSelecionada,
        getSelosDisponiveis: getSelosDisponiveis,
        getSelosSolicitados: getSelosSolicitados,
        getSelosZerados: getSelosZerados,
        getDadosPrimeiroApontamento: getDadosPrimeiroApontamento,
        getDadosUltimoApontamento: getDadosUltimoApontamento,
        getConfig: getConfig,
        getMetrics: getMetrics,

        // Setters
        setUsuario: setUsuario,
        setOpcaoSelecionada: setOpcaoSelecionada,
        setSelosDisponiveis: setSelosDisponiveis,
        setSelosSolicitados: setSelosSolicitados,
        setSelosZerados: setSelosZerados,
        setDadosPrimeiroApontamento: setDadosPrimeiroApontamento,
        setDadosUltimoApontamento: setDadosUltimoApontamento,
        setBuscandoIntervalo: setBuscandoIntervalo,
        setCalculandoSelos: setCalculandoSelos,
        setUltimaBusca: setUltimaBusca,
        setConfig: setConfig,

        // Checkers
        isBuscandoIntervalo: isBuscandoIntervalo,
        isCalculandoSelos: isCalculandoSelos,
        isSelando: isSelando,
        hasOpcaoSelecionada: hasOpcaoSelecionada,
        hasIntervaloPreenchido: hasIntervaloPreenchido,
        hasSelosSolicitados: hasSelosSolicitados,

        // Listeners
        addListener: addListener,
        removeListener: removeListener,
        addSpecificListener: addSpecificListener,
        removeSpecificListener: removeSpecificListener,

        // Cache e performance
        getElementoDOM: getElementoDOM,
        limparCacheDOM: limparCacheDOM,
        undo: undo,
        redo: redo,

        // Persistência
        persistirEstado: persistirEstado,
        carregarEstadoPersistente: carregarEstadoPersistente,
        limparPersistencia: limparPersistencia,

        // Utilitários
        getRelatorio: getRelatorio,

        // Debug
        _debug: function () {
            return {
                estado: estado,
                listeners: listeners,
                cache: domCache,
                history: stateHistory
            };
        },
        _reset: function () {
            estado = {
                usuario: 'CONVIDADO',
                opcaoSelecionada: null,
                selosDisponiveis: {},
                selosSolicitados: {},
                selosZerados: [],
                dadosPrimeiroApontamento: null,
                dadosUltimoApontamento: null,
                buscandoIntervalo: false,
                calculandoSelos: false,
                selando: false,
                ultimaBusca: null,
                ultimoCalculo: null,
                ultimaSelagem: null,
                config: {
                    autoBuscar: true,
                    autoCalcular: false,
                    mostrarAlertas: true,
                    modoCompacto: false
                },
                metrics: {
                    stateUpdates: 0,
                    lastUpdate: null,
                    averageUpdateTime: 0
                }
            };

            listeners = { all: [], specific: {} };
            domCache = {};
            stateHistory = { past: [], future: [], maxSize: 10, enabled: true };

            console.log('🧹 App State resetado');
        }
    };
})();

// Auto-inicialização
if (typeof window !== 'undefined') {
    setTimeout(function () {
        if (window.appState && window.appState.inicializar) {
            window.appState.inicializar();
            console.log('✅ appState.js OTIMIZADO - FASE 3');
        }
    }, 100);
}