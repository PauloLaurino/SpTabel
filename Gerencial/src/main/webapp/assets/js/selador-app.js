// ============================================
// SELADOR APP - CLASSE PRINCIPAL (REFATORADA - FASE 2)
// ============================================

function SeladorApp() {
    console.log('🛠️ Criando instância SeladorApp (FASE 2)...');
    
    this.config = {
        apiBaseUrl: '/selador/api',
        timeout: 30000,
        mapeamentoSelos: {
            'TP1': '0001',
            'TPD': '0003',
            'TPI': '0004',
            'TP3': '0009',
            'TP4': '0010'
        }
    };
    
    this.elementos = {};
    this.controllers = {};
    
    // Verificar API
    if (window.api) {
        this.api = window.api;
    } else if (window.ApiClient) {
        this.api = new window.ApiClient();
    } else {
        console.warn('⚠️ API não encontrada, criando básica');
        this.api = this.criarAPIBasica();
    }
    
    console.log('✅ SeladorApp instanciada');
}

SeladorApp.prototype.inicializar = function() {
    console.log('🚀 Inicializando Selador (FASE 2)...');
    
    this.verificarIntegracaoMaker();
    this.initDOM();
    this.inicializarControllers();
    this.carregarDadosIniciais();
    
    this.esconderLoadingInicial();
    
    // Emitir evento de inicialização
    if (window.eventBus) {
        window.eventBus.emit(window.eventBus.EVENTOS.APP_INICIALIZADA, {
            timestamp: new Date().toISOString(),
            versao: '1.26.1'
        });
    }
    
    console.log('🎉 Selador completamente inicializado (FASE 2)');
};

SeladorApp.prototype.initDOM = function() {
    console.log('🏗️ Inicializando DOM...');
    
    // Usar domManager se disponível
    if (window.domManager && window.domManager.inicializar) { // ✅ CORRIGIDO
        this.elementos = window.domManager.inicializar();
        console.log('✅ DOM inicializado via domManager');
    } else {
        // Fallback manual
        this.elementos = {};
        console.warn('⚠️ domManager não disponível, usando fallback');
    }
    
    return this.elementos;
};

SeladorApp.prototype.inicializarControllers = function() {
    console.log('🎮 Inicializando controllers...');
    
    // App Controller (orquestração principal)
    if (window.appController && window.appController.inicializar) {
        this.controllers.app = window.appController;
        window.appController.inicializar(this);
        console.log('✅ App Controller inicializado');
    }
    
    // Button Controller (gestão de botões)
    if (window.buttonController && window.buttonController.inicializar) {
        this.controllers.button = window.buttonController;
        window.buttonController.inicializar(this);
        console.log('✅ Button Controller inicializado');
    }
    
    // Input Controller (gestão de inputs)
    if (window.inputController && window.inputController.inicializar) {
        this.controllers.input = window.inputController;
        window.inputController.inicializar(this);
        console.log('✅ Input Controller inicializado');
    }
    
    // Validation Controller (não precisa de inicialização)
    if (window.validationController) {
        this.controllers.validation = window.validationController;
        console.log('✅ Validation Controller disponível');
    }
};

SeladorApp.prototype.carregarDadosIniciais = function() {
    console.log('📦 Carregando dados iniciais...');
    
    // ✅ USAR MÉTODO CORRETO
    if (window.selosManager) {
        // Tentar novo método primeiro
        if (typeof window.selosManager.carregarSelosDisponiveis === 'function') {
            window.selosManager.carregarSelosDisponiveis(this);
        }
        // Fallback para método antigo
        else if (typeof window.selosManager.carregar === 'function') {
            window.selosManager.carregar(this);
        }
    }
    
    if (window.opcoesManager) {
        window.opcoesManager.carregar(this);
    }
    
    if (window.uiComponents) {
        window.uiComponents.iniciarAtualizacaoHora(this);
    }
    
    console.log('✅ Dados iniciais carregados');
};

SeladorApp.prototype.verificarIntegracaoMaker = function() {
    try {
        var urlParams = new URLSearchParams(window.location.search);
        if (urlParams.get('origem') === 'maker' || urlParams.get('embedded') === 'true') {
            // Atualizar estado global
            if (window.appState) {
                window.appState.setUsuario(urlParams.get('usuario') || 'OPERADOR_MAKER');
                
                // Marcar como integração Maker
                window.appState.atualizarEstado({
                    isMakerIntegration: true
                });
            }
            
            document.body.classList.add('embedded-maker');
            console.log('🔗 Modo Maker ativado');
        }
    } catch (e) {
        console.warn('⚠️ Não foi possível verificar integração Maker:', e);
    }
};

SeladorApp.prototype.esconderLoadingInicial = function() {
    var loadingScreen = document.getElementById('loadingScreen');
    var appContainer = document.getElementById('appContainer');
    
    if (loadingScreen) {
        loadingScreen.style.display = 'none';
        loadingScreen.classList.add('hidden');
    }
    
    if (appContainer) {
        appContainer.style.display = 'flex';
        appContainer.classList.remove('hidden');
    }
};

SeladorApp.prototype.criarAPIBasica = function() {
    return {
        contarSelos: function() { return Promise.reject(new Error('API não disponível')); },
        buscarIntervaloApontamentos: function() { return Promise.reject(new Error('API não disponível')); },
        calcularSelosNecessarios: function() { return Promise.reject(new Error('API não disponível')); },
        processarSelagemCompleta: function() { return Promise.reject(new Error('API não disponível')); }
    };
};

// Exposição global
if (typeof window !== 'undefined') {
    window.SeladorApp = SeladorApp;
    console.log('✅ SeladorApp disponível globalmente (FASE 2)');
}

// Auto-inicialização
window.inicializarSeladorApp = function() {
    if (window.app && window.app instanceof SeladorApp) {
        console.log('🔄 Aplicação já inicializada');
        return window.app;
    }
    
    try {
        window.app = new SeladorApp();
        window.app.inicializar();
        return window.app;
    } catch (error) {
        console.error('❌ Erro ao criar instância SeladorApp:', error);
        return null;
    }
};

// Inicializar quando DOM estiver pronto
// Inicializar quando DOM estiver pronto
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function() {
        setTimeout(window.inicializarSeladorApp, 300);
    });
} else {
    setTimeout(window.inicializarSeladorApp, 300);
}

// ============================================
// GARANTIR window.app EXISTE - PATCH DE SEGURANÇA
// ============================================

// Garantir que window.app existe mesmo se a inicialização falhar
if (typeof window !== 'undefined' && !window.app) {
    console.log('🔧 Criando window.app de segurança...');
    
    // Criar app básico com métodos essenciais
    window.app = {
        elementos: {},
        api: null,
        controllers: {},
        config: {},
        
        // Método para obter elementos
        getElement: function(id) {
            return this.elementos[id] || document.getElementById(id);
        },
        
        // Inicialização básica
        inicializarBasica: function() {
            console.log('🔄 Inicializando app básico...');
            
            // Coletar elementos essenciais
            var elementosIds = [
                'dataAto', 'anoMesInicial', 'aponInicial', 
                'anoMesFinal', 'aponFinal', 'opcoesSelect',
                'btnSolicitarSelos', 'btnSelarApontamentos'
            ];
            
            elementosIds.forEach(function(id) {
                var elemento = document.getElementById(id);
                if (elemento) {
                    window.app.elementos[id] = elemento;
                }
            });
            
            // Configurar API básica
            if (window.api) {
                window.app.api = window.api;
            } else if (window.ApiClient) {
                window.app.api = new window.ApiClient();
            }
            
            console.log('✅ App básico inicializado:', {
                elementos: Object.keys(window.app.elementos),
                temAPI: !!window.app.api
            });
        }
    };
    
    // Auto-inicialização básica quando DOM estiver pronto
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function() {
            setTimeout(function() {
                if (window.app && window.app.inicializarBasica) {
                    window.app.inicializarBasica();
                }
            }, 500);
        });
    } else {
        setTimeout(function() {
            if (window.app && window.app.inicializarBasica) {
                window.app.inicializarBasica();
            }
        }, 500);
    }
}

console.log('✅ SeladorApp FASE 2 - window.app garantido');