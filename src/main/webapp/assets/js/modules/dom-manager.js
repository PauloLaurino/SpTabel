// ============================================
// DOM MANAGER - Otimizado para FASE 3
// Gerenciamento de DOM com caching, debouncing e otimizações
// ============================================

window.domManager = (function () {
    'use strict';

    // Cache de elementos DOM
    var elementCache = {
        byId: {},
        bySelector: {},
        byClass: {}
    };

    // Configurações de performance
    var config = {
        cacheTTL: 5 * 60 * 1000, // 5 minutos
        maxCacheSize: 500,
        autoClearInterval: 2 * 60 * 1000, // 2 minutos
        useMutationObserver: true,
        debounceRender: true,
        renderDelay: 16 // ~60fps
    };

    // Observador de mutações DOM
    var mutationObserver = null;

    // Elementos do sistema
    var elementos = {};

    // ============================================
    // INICIALIZAÇÃO
    // ============================================

    function inicializar() {
        console.log('🏗️ DOM Manager inicializando (FASE 3)...');

        // Carregar elementos críticos
        carregarElementosCriticos();

        // Configurar observador de mutações
        configurarMutationObserver();

        // Configurar limpeza automática de cache
        configurarLimpezaAutomatica();

        // Configurar Event Bus
        configurarEventBus();

        console.log('✅ DOM Manager inicializado com', Object.keys(elementos).length, 'elementos');
        return elementos;
    }

    function configurarEventBus() {
        if (!window.eventBus) return;

        // Invalidar cache quando DOM mudar significativamente
        window.eventBus.on('ui:render', function () {
            setTimeout(function () {
                invalidarCacheSeletor();
            }, 1000);
        });

        // Limpar cache quando necessário
        window.eventBus.on('cache:limpar', function () {
            limparCache();
        });
    }

    function configurarLimpezaAutomatica() {
        setInterval(function () {
            limparCacheExpirado();
        }, config.autoClearInterval);
    }

    function configurarMutationObserver() {
        if (!config.useMutationObserver || !window.MutationObserver) return;

        mutationObserver = new MutationObserver(function (mutations) {
            handleDOMChanges(mutations);
        });

        // Observar body para mudanças estruturais
        mutationObserver.observe(document.body, {
            childList: true,
            subtree: true,
            attributes: false,
            characterData: false
        });

        console.log('👁️ Mutation Observer configurado');
    }

    // ============================================
    // CARREGAMENTO DE ELEMENTOS OTIMIZADO
    // ============================================

    function carregarElementosCriticos() {
        var startTime = performance.now();

        // Elementos essenciais do sistema
        var elementosCriticos = [
            // Painéis
            'painelOpcoes', 'painelData', 'painelSelos', 'painelAcoes',

            // Inputs
            'dataAto', 'anoMesInicial', 'aponInicial', 'anoMesFinal', 'aponFinal',

            // Selos
            'inputTP1', 'inputTP3', 'inputTP4', 'inputTPD', 'inputTPI',
            'qtdDisponivelTP1', 'qtdDisponivelTP3', 'qtdDisponivelTP4', 'qtdDisponivelTPD', 'qtdDisponivelTPI',
            'qtdSolicitadoTP1', 'qtdSolicitadoTP3', 'qtdSolicitadoTP4', 'qtdSolicitadoTPD', 'qtdSolicitadoTPI',

            // Botões
            'btnCalcularSelos', 'btnSelar', 'btnLimpar', 'btnBuscar',
            'btnConfig', 'btnAjuda',

            // Status e mensagens
            'statusBar', 'loadingIndicator', 'searchIndicator',
            'toastContainer', 'modalContainer',

            // Totalizadores
            'totalSolicitado', 'totalDisponivel', 'floatingTotalizer',

            // Modais
            'modalConfirmacao', 'modalErro', 'modalSucesso'
        ];

        // Carregar com cache
        elementosCriticos.forEach(function (id) {
            elementos[id] = getElementByIdCached(id);
        });

        // Carregar elementos por classe (com cache)
        carregarElementosPorClasse();

        var endTime = performance.now();
        console.log('⚡ Elementos críticos carregados em', (endTime - startTime).toFixed(2), 'ms');
    }

    function carregarElementosPorClasse() {
        // Classes frequentemente usadas
        var classesImportantes = [
            'selo-item', 'btn-acao', 'campo-input', 'totalizador',
            'painel-secao', 'card-info', 'status-indicator'
        ];

        classesImportantes.forEach(function (className) {
            var key = 'class_' + className;
            elementos[key] = getElementsByClassNameCached(className);
        });
    }

    // ============================================
    // GETTERS OTIMIZADOS COM CACHE
    // ============================================

    function getElementos() {
        return Object.assign({}, elementos);
    }

    function getElementById(id, forceRefresh) {
        return getElementByIdCached(id, forceRefresh);
    }

    function getElementByIdCached(id, forceRefresh) {
        if (!forceRefresh && elementCache.byId[id]) {
            var cached = elementCache.byId[id];

            // Verificar se ainda está no DOM
            if (isElementInDOM(cached.element)) {
                return cached.element;
            } else {
                // Remover do cache se não estiver mais no DOM
                delete elementCache.byId[id];
            }
        }

        var element = document.getElementById(id);
        if (element) {
            elementCache.byId[id] = {
                element: element,
                timestamp: Date.now()
            };

            // Limitar tamanho do cache
            if (Object.keys(elementCache.byId).length > config.maxCacheSize) {
                removerElementoMaisAntigo('byId');
            }
        }

        return element;
    }

    function getElementsByClassName(className, forceRefresh) {
        return getElementsByClassNameCached(className, forceRefresh);
    }

    function getElementsByClassNameCached(className, forceRefresh) {
        var cacheKey = 'class_' + className;

        if (!forceRefresh && elementCache.byClass[cacheKey]) {
            var cached = elementCache.byClass[cacheKey];

            // Verificar se ainda há elementos no DOM
            if (cached.elements.length > 0 && isElementInDOM(cached.elements[0])) {
                return cached.elements;
            } else {
                delete elementCache.byClass[cacheKey];
            }
        }

        var elements = Array.from(document.getElementsByClassName(className));
        if (elements.length > 0) {
            elementCache.byClass[cacheKey] = {
                elements: elements,
                timestamp: Date.now()
            };
        }

        return elements;
    }

    function querySelector(selector, forceRefresh) {
        return querySelectorCached(selector, forceRefresh);
    }

    function querySelectorCached(selector, forceRefresh) {
        if (!forceRefresh && elementCache.bySelector[selector]) {
            var cached = elementCache.bySelector[selector];

            if (isElementInDOM(cached.element)) {
                return cached.element;
            } else {
                delete elementCache.bySelector[selector];
            }
        }

        var element = document.querySelector(selector);
        if (element) {
            elementCache.bySelector[selector] = {
                element: element,
                timestamp: Date.now()
            };

            if (Object.keys(elementCache.bySelector).length > config.maxCacheSize) {
                removerElementoMaisAntigo('bySelector');
            }
        }

        return element;
    }

    function querySelectorAll(selector, forceRefresh) {
        var cacheKey = 'all_' + selector;

        if (!forceRefresh && elementCache.bySelector[cacheKey]) {
            var cached = elementCache.bySelector[cacheKey];

            if (cached.elements.length > 0 && isElementInDOM(cached.elements[0])) {
                return cached.elements;
            } else {
                delete elementCache.bySelector[cacheKey];
            }
        }

        var elements = Array.from(document.querySelectorAll(selector));
        if (elements.length > 0) {
            elementCache.bySelector[cacheKey] = {
                elements: elements,
                timestamp: Date.now()
            };
        }

        return elements;
    }

    // ============================================
    // MANIPULAÇÃO DE DOM OTIMIZADA
    // ============================================

    function atualizarElemento(id, atualizacoes) {
        var elemento = getElementById(id);
        if (!elemento) return false;

        var mudancas = false;

        // Atualizar texto
        if (atualizacoes.texto !== undefined) {
            elemento.textContent = atualizacoes.texto;
            mudancas = true;
        }

        // Atualizar HTML
        if (atualizacoes.html !== undefined) {
            elemento.innerHTML = atualizacoes.html;
            mudancas = true;
        }

        // Atualizar valor
        if (atualizacoes.valor !== undefined && elemento.value !== undefined) {
            elemento.value = atualizacoes.valor;
            mudancas = true;
        }

        // Atualizar classes
        if (atualizacoes.classes) {
            atualizarClasses(elemento, atualizacoes.classes);
            mudancas = true;
        }

        // Atualizar atributos
        if (atualizacoes.atributos) {
            for (var attr in atualizacoes.atributos) {
                if (atualizacoes.atributos.hasOwnProperty(attr)) {
                    elemento.setAttribute(attr, atualizacoes.atributos[attr]);
                }
            }
            mudancas = true;
        }

        // Atualizar estilos
        if (atualizacoes.estilos) {
            Object.assign(elemento.style, atualizacoes.estilos);
            mudancas = true;
        }

        // Invalidar cache se houve mudanças
        if (mudancas) {
            invalidarCacheElemento(id);
        }

        return mudancas;
    }

    function atualizarClasses(elemento, classes) {
        if (classes.adicionar) {
            var toAdd = Array.isArray(classes.adicionar) ? classes.adicionar : [classes.adicionar];
            toAdd.forEach(function (className) {
                elemento.classList.add(className);
            });
        }

        if (classes.remover) {
            var toRemove = Array.isArray(classes.remover) ? classes.remover : [classes.remover];
            toRemove.forEach(function (className) {
                elemento.classList.remove(className);
            });
        }

        if (classes.toggle) {
            elemento.classList.toggle(classes.toggle);
        }

        if (classes.set) {
            elemento.className = classes.set;
        }
    }

    function mostrarElemento(id, display) {
        var elemento = getElementById(id);
        if (!elemento) return false;

        elemento.style.display = display || 'block';
        return true;
    }

    function ocultarElemento(id) {
        var elemento = getElementById(id);
        if (!elemento) return false;

        elemento.style.display = 'none';
        return true;
    }

    function toggleElemento(id, display) {
        var elemento = getElementById(id);
        if (!elemento) return false;

        if (elemento.style.display === 'none') {
            elemento.style.display = display || 'block';
        } else {
            elemento.style.display = 'none';
        }

        return true;
    }

    function adicionarEvento(id, evento, callback, options) {
        var elemento = getElementById(id);
        if (!elemento) return null;

        // Aplicar debouncing para eventos frequentes
        if (options && options.debounce && window.debounceUtils) {
            callback = window.debounceUtils.debounce(callback, options.debounce, 'dom_' + id + '_' + evento);
        }

        elemento.addEventListener(evento, callback);

        // Retornar função para remover evento
        return function () {
            elemento.removeEventListener(evento, callback);
        };
    }

    // ============================================
    // RENDERIZAÇÃO OTIMIZADA
    // ============================================

    function renderizarTemplate(templateId, dados, containerId) {
        var template = getElementById(templateId);
        if (!template) {
            console.error('❌ Template não encontrado:', templateId);
            return null;
        }

        var container = containerId ? getElementById(containerId) : null;
        if (containerId && !container) {
            console.error('❌ Container não encontrado:', containerId);
            return null;
        }

        // Renderizar template
        var html = template.innerHTML;

        // Substituir variáveis (simples)
        for (var key in dados) {
            if (dados.hasOwnProperty(key)) {
                var placeholder = '{{' + key + '}}';
                html = html.replace(new RegExp(placeholder, 'g'), dados[key]);
            }
        }

        // Inserir no DOM com debouncing
        if (config.debounceRender) {
            clearTimeout(renderizarTemplate.debounceTimer);

            renderizarTemplate.debounceTimer = setTimeout(function () {
                if (container) {
                    container.innerHTML = html;
                }

                // Notificar renderização
                if (window.eventBus) {
                    window.eventBus.emit('ui:render', {
                        template: templateId,
                        container: containerId
                    });
                }
            }, config.renderDelay);
        } else {
            if (container) {
                container.innerHTML = html;
            }
        }

        return html;
    }

    // ============================================
    // GESTÃO DE CACHE
    // ============================================

    function isElementInDOM(element) {
        return element && document.body.contains(element);
    }

    function removerElementoMaisAntigo(cacheType) {
        if (!elementCache[cacheType] || Object.keys(elementCache[cacheType]).length === 0) {
            return;
        }

        var oldestKey = null;
        var oldestTime = Infinity;

        for (var key in elementCache[cacheType]) {
            if (elementCache[cacheType].hasOwnProperty(key)) {
                var timestamp = elementCache[cacheType][key].timestamp;
                if (timestamp < oldestTime) {
                    oldestTime = timestamp;
                    oldestKey = key;
                }
            }
        }

        if (oldestKey) {
            delete elementCache[cacheType][oldestKey];

            if (window.debugMode) {
                console.log('🗑️ Removido do cache DOM:', cacheType, oldestKey);
            }
        }
    }

    function invalidarCacheElemento(id) {
        if (elementCache.byId[id]) {
            delete elementCache.byId[id];
        }

        // Invalidar caches relacionados
        for (var key in elementCache.bySelector) {
            if (key.includes(id)) {
                delete elementCache.bySelector[key];
            }
        }
    }

    function invalidarCacheSeletor(selector) {
        if (selector && elementCache.bySelector[selector]) {
            delete elementCache.bySelector[selector];
        } else {
            // Invalidar todos os seletores
            elementCache.bySelector = {};
        }
    }

    function limparCacheExpirado() {
        var now = Date.now();
        var removed = 0;

        // Limpar byId
        for (var id in elementCache.byId) {
            if (elementCache.byId.hasOwnProperty(id)) {
                var cached = elementCache.byId[id];
                if ((now - cached.timestamp) > config.cacheTTL) {
                    delete elementCache.byId[id];
                    removed++;
                }
            }
        }

        // Limpar byClass
        for (var className in elementCache.byClass) {
            if (elementCache.byClass.hasOwnProperty(className)) {
                var cached = elementCache.byClass[className];
                if ((now - cached.timestamp) > config.cacheTTL) {
                    delete elementCache.byClass[className];
                    removed++;
                }
            }
        }

        // Limpar bySelector
        for (var selector in elementCache.bySelector) {
            if (elementCache.bySelector.hasOwnProperty(selector)) {
                var cached = elementCache.bySelector[selector];
                if ((now - cached.timestamp) > config.cacheTTL) {
                    delete elementCache.bySelector[selector];
                    removed++;
                }
            }
        }

        if (removed > 0 && window.debugMode) {
            console.log('🧹 DOM Cache - Removidos', removed, 'itens expirados');
        }
    }

    function limparCache() {
        var count = Object.keys(elementCache.byId).length +
            Object.keys(elementCache.byClass).length +
            Object.keys(elementCache.bySelector).length;

        elementCache = {
            byId: {},
            bySelector: {},
            byClass: {}
        };

        console.log('🧹 DOM Cache limpo:', count, 'itens');
    }

    // ============================================
    // HANDLERS DE MUDANÇAS DOM
    // ============================================

    function handleDOMChanges(mutations) {
        var significantChange = false;

        mutations.forEach(function (mutation) {
            // Verificar se é uma mudança significativa
            if (mutation.type === 'childList' && mutation.addedNodes.length > 0) {
                significantChange = true;

                // Invalidar caches relacionados
                mutation.addedNodes.forEach(function (node) {
                    if (node.nodeType === 1) { // Element node
                        invalidateCachesForElement(node);
                    }
                });
            }
        });

        if (significantChange && window.eventBus) {
            window.eventBus.emit('dom:changed', { timestamp: new Date().toISOString() });
        }
    }

    function invalidateCachesForElement(element) {
        // Invalidar cache por ID
        if (element.id && elementCache.byId[element.id]) {
            delete elementCache.byId[element.id];
        }

        // Invalidar cache por classe
        if (element.className) {
            var classes = element.className.split(' ');
            classes.forEach(function (className) {
                var cacheKey = 'class_' + className;
                if (elementCache.byClass[cacheKey]) {
                    delete elementCache.byClass[cacheKey];
                }
            });
        }
    }

    // ============================================
    // UTILITÁRIOS ESPECÍFICOS DO SISTEMA
    // ============================================


    // ============================================
    // CONFIGURAÇÃO DE CAMPOS INICIAIS
    // ============================================

    function configurarCamposIniciaisDOM() {
        console.log('⚙️ DOM Manager: Configurando valores iniciais dos campos...');

        try {
            // 1. Data atual
            var hoje = new Date();
            var dataFormatada = hoje.toISOString().split('T')[0]; // YYYY-MM-DD

            var dataInput = getElementById('dataAto');
            if (dataInput) {
                // Limpar valores anteriores
                dataInput.value = '';
                dataInput.removeAttribute('value');
                dataInput.removeAttribute('data-default');

                // Definir data atual
                dataInput.value = dataFormatada;
                dataInput.placeholder = "DD/MM/AAAA";

                console.log('✅ Data configurada:', dataFormatada);
            }

            // 2. Campos de intervalo - LIMPAR COMPLETAMENTE
            var camposIntervalo = [
                { id: 'anoMesInicial', tipo: 'anoMes' },
                { id: 'aponInicial', tipo: 'apon' },
                { id: 'anoMesFinal', tipo: 'anoMes' },
                { id: 'aponFinal', tipo: 'apon' }
            ];

            camposIntervalo.forEach(function (campoInfo) {
                var campo = getElementById(campoInfo.id);
                if (campo) {
                    // Limpar completamente
                    campo.value = '';
                    campo.placeholder = '';

                    // Remover atributos
                    campo.removeAttribute('value');
                    campo.removeAttribute('data-value');
                    campo.removeAttribute('data-default');

                    // Remover classes de validação
                    campo.classList.remove('valid', 'invalid', 'error', 'warning');

                    console.log('✅ Campo limpo:', campoInfo.id);
                }
            });

            // 3. Limpar qualquer estado de busca
            var searchIndicator = getElementById('searchIndicator');
            if (searchIndicator) {
                ocultarElemento('searchIndicator');
            }

            // 4. Limpar opção selecionada
            var opcaoSection = getElementById('opcaoSelecionadaSection');
            if (opcaoSection) {
                opcaoSection.style.display = 'none';
            }

            // 5. Invalidar cache destes elementos
            invalidarCacheElemento('dataAto');
            camposIntervalo.forEach(function (campo) {
                invalidarCacheElemento(campo.id);
            });

            return true;

        } catch (error) {
            console.error('❌ Erro ao configurar campos no DOM Manager:', error);
            return false;
        }
    }

    function limparSelosSolicitadosUI() {
        var tipos = ['TP1', 'TP3', 'TP4', 'TPD', 'TPI'];

        tipos.forEach(function (tipo) {
            // Limpar inputs
            var input = getElementById('input' + tipo);
            if (input) input.value = '0';

            // Limpar displays
            var display = getElementById('qtdSolicitado' + tipo);
            if (display) display.textContent = '0';

            // Remover classes de destaque
            var item = getElementById('seloItem' + tipo);
            if (item) {
                item.classList.remove('com-solicitacao');
            }
        });

        // Atualizar totalizador
        var totalizador = getElementById('totalSolicitado');
        if (totalizador) totalizador.textContent = '0';

        // Ocultar floating totalizer
        var floating = getElementById('floatingTotalizer');
        if (floating) floating.style.display = 'none';
    }

    function mostrarLoading(acao) {
        var loading = getElementById('loadingIndicator');
        if (!loading) return;

        var texto = getElementById('loadingText');
        if (texto && acao) {
            texto.textContent = acao + '...';
        }

        loading.style.display = 'flex';
    }

    function esconderLoading() {
        var loading = getElementById('loadingIndicator');
        if (loading) {
            loading.style.display = 'none';
        }
    }

    // ============================================
    // API PÚBLICA
    // ============================================

    return {
        // Inicialização
        inicializar: inicializar,
        getElementos: getElementos,

        // Getters otimizados
        getElementById: getElementById,
        getElementsByClassName: getElementsByClassName,
        querySelector: querySelector,
        querySelectorAll: querySelectorAll,

        // Manipulação DOM
        atualizarElemento: atualizarElemento,
        mostrarElemento: mostrarElemento,
        ocultarElemento: ocultarElemento,
        toggleElemento: toggleElemento,
        adicionarEvento: adicionarEvento,

        // Renderização
        renderizarTemplate: renderizarTemplate,

        // Cache management
        limparCache: limparCache,
        invalidarCacheElemento: invalidarCacheElemento,
        invalidarCacheSeletor: invalidarCacheSeletor,

        // Utilitários específicos
        configurarCamposIniciaisDOM: configurarCamposIniciaisDOM,
        limparSelosSolicitadosUI: limparSelosSolicitadosUI,
        mostrarLoading: mostrarLoading,
        esconderLoading: esconderLoading,

        // Configuração
        setConfig: function (newConfig) {
            Object.assign(config, newConfig);
        },

        // Estatísticas
        getStats: function () {
            return {
                cache: {
                    byId: Object.keys(elementCache.byId).length,
                    byClass: Object.keys(elementCache.byClass).length,
                    bySelector: Object.keys(elementCache.bySelector).length
                },
                elementos: Object.keys(elementos).length,
                config: config,
                timestamp: new Date().toISOString()
            };
        },

        // Debug
        _debug: function () {
            return {
                elementCache: elementCache,
                elementos: elementos,
                config: config
            };
        }
    };
})();

// ============================================
// EXECUÇÃO FINAL - GARANTIR CAMPOS CONFIGURADOS
// ============================================

// Aguardar DOM completamente carregado e configurar campos
function garantirCamposConfigurados() {
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function () {
            setTimeout(function () {
                console.log('🎬 Configurando campos após DOM carregado...');
                configurarCamposIniciais();
            }, 200);
        });
    } else {
        // DOM já carregado
        setTimeout(function () {
            console.log('🎬 Configurando campos (DOM já carregado)...');
            configurarCamposIniciais();
        }, 300);
    }
}

// Executar após tudo carregado
setTimeout(garantirCamposConfigurados, 1000);

// Também configurar quando app for inicializada
if (window.eventBus) {
    window.eventBus.on(window.eventBus.EVENTOS.APP_INICIALIZADA, function () {
        console.log('🔄 Reconfigurando campos após inicialização da app...');
        setTimeout(configurarCamposIniciais, 500);
    });
}

// Auto-inicialização
if (typeof window !== 'undefined') {
    setTimeout(function () {
        if (window.domManager && window.domManager.inicializar) {
            window.domManager.inicializar();
            console.log('✅ domManager.js OTIMIZADO - FASE 3');
        }
    }, 200);
}