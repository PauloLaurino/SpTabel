// ============================================
// CONFIGURAÇÃO DE CARREGAMENTO DINÂMICO - FASE 3.3
// ============================================

var modulosParaLazyLoading = {
    // Módulos essenciais (carregam imediatamente)
    essenciais: [
        'helpers',
        'appState',
        'eventBus',
        'domManager',
        'validationController',
        'ApiClient'
    ],

    // Módulos secundários (carregam após DOM)
    secundarios: [
        'uiComponents',
        'buttonLoader',
        'inputController',
        'buttonController',
        'validationController'
    ],

    // Módulos de negócio (carregam após secundários)
    negocios: [
        'selosManager',
        'apontamentoService',
        'opcoesManager',
        'selagemManager'
    ],

    // Módulos opcionais (carregam sob demanda)
    opcionais: [
        'floatingTotalizer',
        'makerIntegration',
        'performanceMonitor'
    ]
};

var modulosCarregados = {};

// ============================================
// GARANTIR window.app EXISTE - PRIMEIRO!
// ============================================

(function () {
    'use strict';

    // Se já existe window.app, não fazer nada
    if (window.app && window.app.elementos) {
        console.log('✅ window.app já existe');
        return;
    }

    console.log('🔧 Garantindo window.app existe...');

    // Criar app básico se não existir
    if (!window.app) {
        window.app = {
            elementos: {},
            api: null,
            config: {},

            // Método para compatibilidade
            getApp: function () {
                return window.app;
            }
        };

        console.log('🆕 window.app básico criado');
    }

    // Coletar elementos se necessário
    if (!window.app.elementos || Object.keys(window.app.elementos).length === 0) {
        window.app.elementos = {};

        ['dataAto', 'anoMesInicial', 'aponInicial', 'anoMesFinal', 'aponFinal'].forEach(function (id) {
            var el = document.getElementById(id);
            if (el) window.app.elementos[id] = el;
        });

        console.log('📋 Elementos adicionados ao window.app:', Object.keys(window.app.elementos));
    }

    // Garantir API
    if (!window.app.api) {
        if (window.api) {
            window.app.api = window.api;
        } else if (window.ApiClient) {
            window.app.api = new window.ApiClient();
        }

        console.log('🔌 API configurada:', !!window.app.api);
    }
})();

// ============================================
// PATCH DE COMPATIBILIDADE - init.js
// ============================================

function aplicarPatchCompatibilidade() {
    console.log('🔧 Aplicando patch de compatibilidade...');

    // 1. Patch para selos-manager (adicionar métodos faltantes)
    if (window.selosManager) {
        // Adicionar método processarRespostaSelagemParaAtualizacao se não existir
        if (typeof window.selosManager.processarRespostaSelagemParaAtualizacao !== 'function') {
            window.selosManager.processarRespostaSelagemParaAtualizacao = function (response) {
                console.log('🔄 Processando resposta de selagem para atualização de estoque...');

                if (!response || !response.success) {
                    console.warn('⚠️ Selagem não foi bem-sucedida, não atualizando selos');
                    return null;
                }

                // Extrair selos utilizados
                var selosUtilizados = response.selos_utilizados || response.dados?.selos_utilizados || {};

                // Obter selos disponíveis atuais
                var selosDisponiveisAtuais = {};
                if (window.appState) {
                    selosDisponiveisAtuais = window.appState.getSelosDisponiveis() || {};
                }

                // Subtrair selos utilizados
                var tipos = ['TP1', 'TP3', 'TP4', 'TPD', 'TPI'];
                var novosSelosDisponiveis = {};
                var mapeamentoReverso = {
                    '0001': 'TP1',
                    '0003': 'TPD',
                    '0004': 'TPI',
                    '0009': 'TP3',
                    '0010': 'TP4'
                };

                tipos.forEach(function (tipo) {
                    var disponivelAtual = parseInt(selosDisponiveisAtuais[tipo] || 0);
                    var codigoBanco = null;

                    // Encontrar código correspondente
                    for (var codigo in mapeamentoReverso) {
                        if (mapeamentoReverso[codigo] === tipo) {
                            codigoBanco = codigo;
                            break;
                        }
                    }

                    var utilizado = codigoBanco ? parseInt(selosUtilizados[codigoBanco] || 0) : 0;
                    var novoDisponivel = Math.max(0, disponivelAtual - utilizado);

                    novosSelosDisponiveis[tipo] = novoDisponivel;

                    if (utilizado > 0) {
                        console.log('📊 ' + tipo + ': ' + disponivelAtual + ' - ' + utilizado + ' = ' + novoDisponivel);
                    }
                });

                // Atualizar no estado
                if (window.appState && window.appState.setSelosDisponiveis) {
                    window.appState.setSelosDisponiveis(novosSelosDisponiveis);
                    console.log('✅ Selos disponíveis atualizados após selagem');
                }

                return novosSelosDisponiveis;
            };
        }
    }

    console.log('✅ Patch de compatibilidade aplicado');
}

// Executar imediatamente
aplicarPatchCompatibilidade();

// ============================================
// INICIALIZADOR DA APLICAÇÃO - Atualizado para FASE 1
// ============================================

console.log('🚀 Sistema Selador - Inicializando (FASE 1)...');

// Configurar tratamento global de erros
window.addEventListener('error', function (event) {
    console.error('❌ ERRO GLOBAL:', event.error);

    // Emitir pelo Event Bus se disponível
    if (window.eventBus && window.eventBus.emitirErro) {
        window.eventBus.emitirErro('sistema', event.error, {
            url: window.location.href,
            userAgent: navigator.userAgent
        });
    }
});

// Verificar módulos essenciais
function verificarModulosEssenciais() {
    var faltantes = [];

    for (var i = 0; i < modulosParaLazyLoading.essenciais.length; i++) {
        var modulo = modulosParaLazyLoading.essenciais[i];
        if (!window[modulo]) {
            faltantes.push(modulo);
        } else {
            modulosCarregados[modulo] = true;
        }
    }

    if (faltantes.length > 0) {
        console.error('❌ Módulos críticos faltantes:', faltantes);
        return false;
    }

    console.log('✅ Módulos essenciais carregados');
    return true;
}

// ============================================
// PATCH DE SEGURANÇA PARA window.app
// ============================================

function garantirAppGlobal() {
    console.log('🔧 Garantindo window.app global...');

    // Se não existe window.app, criar básico
    if (!window.app) {
        console.warn('⚠️ window.app não definido, criando básico...');

        window.app = {
            elementos: {},
            api: window.api || null,
            state: {},

            // Método para botão-controller.js
            getApp: function () {
                return window.app;
            }
        };

        // Tentar preencher elementos
        var idsEssenciais = ['dataAto', 'anoMesInicial', 'aponInicial', 'anoMesFinal', 'aponFinal'];
        idsEssenciais.forEach(function (id) {
            var el = document.getElementById(id);
            if (el) window.app.elementos[id] = el;
        });

        console.log('🔄 window.app básico criado:', window.app);
    }

    // Verificar se tem método getApp para compatibilidade
    if (window.app && typeof window.app.getApp !== 'function') {
        window.app.getApp = function () {
            return window.app;
        };
    }

    return window.app;
}

// Chamar antes de qualquer inicialização
garantirAppGlobal();

// ============================================
// ADAPTADOR DE COMPATIBILIDADE - init.js
// Adicione ANTES da inicialização
// ============================================

function criarAdaptadorAppState() {
    if (!window.appState) return;

    console.log('🔧 Criando adaptador de compatibilidade para appState...');

    // 1. Adicionar método setUltimaBusca que falta
    if (typeof window.appState.setUltimaBusca !== 'function') {
        window.appState.setUltimaBusca = function (valor) {
            // Usar atualizarEstado se existir
            if (typeof window.appState.atualizarEstado === 'function') {
                window.appState.atualizarEstado({ ultimaBusca: valor });
            }
            // Ou atualizar direto no estado
            else if (window.appState._estado) {
                window.appState._estado.ultimaBusca = valor;
            }
            console.log('📝 setUltimaBusca adaptado:', valor);
        };
    }

    // 2. Adicionar método setBuscandoIntervalo
    if (typeof window.appState.setBuscandoIntervalo !== 'function') {
        window.appState.setBuscandoIntervalo = function (valor) {
            if (typeof window.appState.atualizarEstado === 'function') {
                window.appState.atualizarEstado({ buscandoIntervalo: valor });
            }
            else if (window.appState._estado) {
                window.appState._estado.buscandoIntervalo = valor;
            }
            console.log('🔍 setBuscandoIntervalo adaptado:', valor);
        };
    }

    // 3. Adicionar método isBuscandoIntervalo
    if (typeof window.appState.isBuscandoIntervalo !== 'function') {
        window.appState.isBuscandoIntervalo = function () {
            if (window.appState._estado) {
                return window.appState._estado.buscandoIntervalo || false;
            }
            return false;
        };
    }

    // 4. Adaptador para selos-manager
    if (window.selosManager) {
        // Se carregar existe mas é obsoleto, criar carregarSelosDisponiveis
        if (typeof window.selosManager.carregar === 'function' &&
            typeof window.selosManager.carregarSelosDisponiveis !== 'function') {

            window.selosManager.carregarSelosDisponiveis = function (app) {
                console.log('🔄 Usando adaptador: carregarSelosDisponiveis -> carregar');
                return window.selosManager.carregar(app);
            };
        }
    }

    console.log('✅ Adaptador de compatibilidade criado');
}

// Chamar antes de tudo
criarAdaptadorAppState();


// ============================================
// CONFIGURAÇÃO INICIAL DE CAMPOS
// ============================================

function configurarCamposIniciais() {
    console.log('📅 Configurando campos iniciais...');

    try {
        // 1. Configurar data atual no formato YYYY-MM-DD
        var hoje = new Date();
        var ano = hoje.getFullYear();
        var mes = String(hoje.getMonth() + 1).padStart(2, '0');
        var dia = String(hoje.getDate()).padStart(2, '0');
        var dataAtual = ano + '-' + mes + '-' + dia;

        console.log('📆 Data atual:', dataAtual);

        // 2. Configurar campo de data
        var dataInput = document.getElementById('dataAto');
        if (dataInput) {
            // Limpar qualquer valor prévio
            dataInput.removeAttribute('value');
            dataInput.removeAttribute('data-default');
            dataInput.removeAttribute('data-value');

            // Definir data atual
            dataInput.value = dataAtual;

            // Configurar placeholder
            dataInput.placeholder = "DD/MM/AAAA";

            // Atualizar no appState se disponível
            if (window.appState && window.appState.setDataAto) {
                window.appState.setDataAto(dataAtual);
            }

            console.log('✅ Campo data configurado:', dataAtual);
        } else {
            console.warn('⚠️ Campo dataAto não encontrado');
        }

        // 3. Limpar campos de intervalo
        var camposIntervalo = [
            { id: 'anoMesInicial', placeholder: 'AAAAMM' },
            { id: 'aponInicial', placeholder: '' },
            { id: 'anoMesFinal', placeholder: 'AAAAMM' },
            { id: 'aponFinal', placeholder: '' }
        ];

        camposIntervalo.forEach(function (campoInfo) {
            var campo = document.getElementById(campoInfo.id);
            if (campo) {
                // Remover valores prévios
                campo.value = '';
                campo.removeAttribute('value');
                campo.removeAttribute('data-value');
                campo.removeAttribute('data-default');

                // Configurar placeholder (se especificado)
                if (campoInfo.placeholder) {
                    campo.placeholder = campoInfo.placeholder;
                } else {
                    campo.placeholder = '';
                }

                // Limpar qualquer classe de erro/validação
                campo.classList.remove('invalid', 'error', 'valid');

                console.log('✅ Campo limpo:', campoInfo.id);
            } else {
                console.warn('⚠️ Campo não encontrado:', campoInfo.id);
            }
        });

        // 4. Limpar qualquer display de busca
        var searchIndicator = document.getElementById('searchIndicator');
        if (searchIndicator) {
            searchIndicator.style.display = 'none';
        }

        // 5. Limpar opção selecionada na UI
        var opcaoSelecionadaSection = document.getElementById('opcaoSelecionadaSection');
        if (opcaoSelecionadaSection) {
            opcaoSelecionadaSection.style.display = 'none';
        }

        var opcaoSelecionadaTexto = document.getElementById('opcaoSelecionadaTexto');
        if (opcaoSelecionadaTexto) {
            opcaoSelecionadaTexto.textContent = 'Nenhuma opção selecionada';
        }

        var opcaoDetalhes = document.getElementById('opcaoDetalhes');
        if (opcaoDetalhes) {
            opcaoDetalhes.textContent = 'Selecione uma opção na lista';
        }

        console.log('✅ Campos iniciais configurados com sucesso');

        // 6. Emitir evento de campos configurados
        if (window.eventBus) {
            window.eventBus.emit('app:campos:configurados', {
                timestamp: new Date().toISOString(),
                dataAto: dataAtual
            });
        }

        return true;

    } catch (error) {
        console.error('❌ Erro ao configurar campos iniciais:', error);
        return false;
    }
}

// ============================================
// CONFIGURAÇÃO DO DOM MANAGER PARA CAMPOS INICIAIS
// ============================================

function configurarDomManagerParaCampos() {
    if (!window.domManager) {
        console.warn('⚠️ domManager não disponível, criando função básica');
        return;
    }

    // Extender domManager com função para configurar campos
    if (typeof window.domManager.configurarCamposIniciais !== 'function') {
        window.domManager.configurarCamposIniciais = function () {
            console.log('🏗️ DOM Manager: Configurando campos iniciais...');

            var elementos = window.domManager.getElementos();

            // Data atual
            var hoje = new Date();
            var dataFormatada = hoje.toISOString().split('T')[0];

            if (elementos.dataAto) {
                elementos.dataAto.value = dataFormatada;
                elementos.dataAto.placeholder = "DD/MM/AAAA";
            }

            // Campos de intervalo
            var camposIds = ['anoMesInicial', 'aponInicial', 'anoMesFinal', 'aponFinal'];
            camposIds.forEach(function (id) {
                var campo = elementos[id];
                if (campo) {
                    campo.value = '';
                    campo.placeholder = '';
                }
            });

            return true;
        };

        console.log('✅ Função configurarCamposIniciais adicionada ao DOM Manager');
    }
}

// Função principal de inicialização
function inicializarAplicacao() {
    console.log('🎯 Iniciando aplicação...');

    // 🔥 ADICIONAR: Configurar campos iniciais IMEDIATAMENTE
    console.log('📋 Configurando campos de formulário...');
    configurarCamposIniciais();
    configurarDomManagerParaCampos();

    // Verificar módulos essenciais
    if (!verificarModulosEssenciais()) {
        mostrarErroCarregamento('Módulos essenciais não carregados');
        return;
    }

    // Configurar Event Bus para debug (opcional)
    if (window.eventBus) {
        // Listener para erros do sistema
        window.eventBus.on(window.eventBus.EVENTOS.APP_ERRO, function (erro) {
            console.error('🔴 ERRO VIA EVENT BUS:', erro);
        });

        // Listener para inicialização
        window.eventBus.on(window.eventBus.EVENTOS.APP_INICIALIZADA, function () {
            console.log('✅ Aplicação inicializada via Event Bus');
        });
    }

    // Inicializar aplicação
    if (typeof window.inicializarSeladorApp === 'function') {
        setTimeout(function () {
            var app = window.inicializarSeladorApp();

            // Emitir evento de inicialização
            if (app && window.eventBus) {
                window.eventBus.emit(window.eventBus.EVENTOS.APP_INICIALIZADA, {
                    timestamp: new Date().toISOString(),
                    usuario: window.appState ? window.appState.getUsuario() : 'CONVIDADO'
                });
            }
        }, 100);
    } else {
        console.error('❌ Função inicializarSeladorApp não encontrada');
        mostrarErroCarregamento('Erro na inicialização');
    }
}

// Mostrar erro de carregamento
function mostrarErroCarregamento(mensagem) {
    var loadingScreen = document.getElementById('loadingScreen');
    if (loadingScreen) {
        loadingScreen.innerHTML = '\
            <div class="loading-container">\
                <div class="error-icon">\
                    <i class="fas fa-exclamation-triangle"></i>\
                </div>\
                <div class="loading-content">\
                    <h2>Erro ao Carregar</h2>\
                    <p class="loading-text">' + mensagem + '</p>\
                    <div class="loading-actions">\
                        <button onclick="location.reload()" class="btn-refresh">\
                            <i class="fas fa-redo"></i> Tentar Novamente\
                        </button>\
                    </div>\
                </div>\
            </div>\
        ';
    }
}

/**
 * Carregar módulos por categoria
 */
function carregarModulosPorCategoria(categoria, callback) {
    console.log('📦 Carregando módulos:', categoria);

    var modulos = modulosParaLazyLoading[categoria];
    if (!modulos || modulos.length === 0) {
        if (callback) callback();
        return;
    }

    var carregados = 0;
    var total = modulos.length;

    modulos.forEach(function (modulo) {
        // Se já está carregado, contar como pronto
        if (modulosCarregados[modulo]) {
            carregados++;
            verificarConclusao();
            return;
        }

        // Tentar detectar módulo já carregado
        setTimeout(function () {
            if (window[modulo]) {
                modulosCarregados[modulo] = true;
                carregados++;
                verificarConclusao();
            } else {
                // Se não estiver disponível, marcar como não necessário
                console.log('⚠️ Módulo não encontrado:', modulo);
                carregados++;
                verificarConclusao();
            }
        }, 100);
    });

    function verificarConclusao() {
        if (carregados >= total) {
            console.log('✅ Categoria carregada:', categoria, carregados + '/' + total);
            if (callback) callback();
        }
    }
}

/**
 * Carregar módulo sob demanda
 */
function carregarModuloSobDemanda(nomeModulo, callback) {
    if (modulosCarregados[nomeModulo]) {
        if (callback) callback(window[nomeModulo]);
        return;
    }

    console.log('🔄 Carregando módulo sob demanda:', nomeModulo);

    // Implementar carregamento dinâmico se necessário
    // Por enquanto, apenas verificar se está disponível
    var intervalo = setInterval(function () {
        if (window[nomeModulo]) {
            clearInterval(intervalo);
            modulosCarregados[nomeModulo] = true;
            console.log('✅ Módulo carregado:', nomeModulo);
            if (callback) callback(window[nomeModulo]);
        }
    }, 100);

    // Timeout após 5 segundos
    setTimeout(function () {
        clearInterval(intervalo);
        if (!modulosCarregados[nomeModulo]) {
            console.error('❌ Timeout carregando módulo:', nomeModulo);
            if (callback) callback(null);
        }
    }, 5000);
}

/**
 * Inicialização progressiva
 */
function inicializacaoProgressiva() {
    console.log('🚀 Inicialização progressiva iniciada');

    // 1. Verificar módulos essenciais
    if (!verificarModulosEssenciais()) {
        mostrarErroCarregamento('Módulos essenciais não carregados');
        return;
    }

    // 2. Carregar módulos secundários
    carregarModulosPorCategoria('secundarios', function () {
        // 3. Carregar módulos de negócio
        carregarModulosPorCategoria('negocios', function () {
            // 4. Inicializar aplicação
            setTimeout(inicializarAplicacao, 300);

            // 5. Carregar módulos opcionais em background
            setTimeout(function () {
                carregarModulosPorCategoria('opcionais', function () {
                    console.log('🎉 Todos os módulos carregados');

                    // Notificar via Event Bus
                    if (window.eventBus) {
                        window.eventBus.emit('app:modulos:carregados', {
                            timestamp: new Date().toISOString(),
                            totalModulos: Object.keys(modulosCarregados).length
                        });
                    }
                });
            }, 2000);
        });
    });
}

// No final do init.js, após inicializarAplicacao()
setTimeout(function () {
    // Inicializar button controller simplificado
    if (window.buttonControllerSimplificado && window.buttonControllerSimplificado.inicializar) {
        window.buttonControllerSimplificado.inicializar();

        // Configurar eventos após 500ms
        setTimeout(function () {
            if (window.buttonControllerSimplificado.configurarEventosBotoes) {
                window.buttonControllerSimplificado.configurarEventosBotoes();
            }
        }, 500);
    }
}, 1500);

// ============================================
// 💣 SOLUÇÃO DE FORÇA BRUTA CONTRA PLACEHOLDERS
// ============================================

function eliminarPlaceholdersForcaBruta() {
    console.log('💥 INICIANDO ELIMINAÇÃO FORÇADA DE PLACEHOLDERS...');

    // Lista dos campos problemáticos
    var camposAlvo = [
        'anoMesInicial',
        'aponInicial',
        'anoMesFinal',
        'aponFinal'
    ];

    // Executar 3 vezes com intervalos (por segurança)
    for (var execucao = 1; execucao <= 3; execucao++) {
        setTimeout(function (exec) {
            console.log('🎯 Execução ' + exec + ' de eliminação...');

            camposAlvo.forEach(function (id) {
                var campo = document.getElementById(id);
                if (!campo) {
                    console.warn('⚠️ Campo não encontrado:', id);
                    return;
                }

                // 1. ZERAR TUDO
                campo.value = '';
                campo.defaultValue = '';
                campo.placeholder = '';

                // 2. REMOVER ATRIBUTOS
                campo.removeAttribute('placeholder');
                campo.removeAttribute('data-placeholder');
                campo.removeAttribute('aria-placeholder');

                // 3. DESTRUIR SETTER DO PLACEHOLDER
                try {
                    Object.defineProperty(campo, 'placeholder', {
                        value: '',
                        writable: false,
                        configurable: true
                    });
                } catch (e) { }

                // 4. SOBRESCREVER INNERHTML (nuclear)
                var tempValue = campo.value;
                campo.outerHTML = campo.outerHTML;

                // Re-obter o elemento
                campo = document.getElementById(id);
                if (campo) {
                    campo.value = tempValue;

                    // 5. ADICIONAR EVENTO PARA BLOQUEAR FUTUROS PLACEHOLDERS
                    campo.addEventListener('DOMAttrModified', function (e) {
                        if (e.attrName === 'placeholder') {
                            this.removeAttribute('placeholder');
                        }
                    });

                    console.log('✅ Campo ' + id + ' neutralizado');
                }
            });

            // Verificar resultado
            console.log('📊 Estado após execução ' + exec + ':');
            camposAlvo.forEach(function (id) {
                var campo = document.getElementById(id);
                if (campo) {
                    console.log('  • ' + id + ': placeholder="' + campo.placeholder + '"');
                }
            });

        }, 300 * execucao, execucao);
    }

    // Última verificação após 2 segundos
    setTimeout(function () {
        console.log('🔍 VERIFICAÇÃO FINAL:');
        var algumPlaceholder = false;

        camposAlvo.forEach(function (id) {
            var campo = document.getElementById(id);
            if (campo && campo.placeholder && campo.placeholder !== '') {
                console.error('❌ ' + id + ' AINDA TEM PLACEHOLDER: "' + campo.placeholder + '"');
                algumPlaceholder = true;
            }
        });

        if (!algumPlaceholder) {
            console.log('🎉 TODOS OS PLACEHOLDERS FORAM ELIMINADOS!');
        } else {
            console.error('💀 ALGUNS PLACEHOLDERS RESISTIRAM - TENTANDO MÉTODO EXTREMO...');
            tentarMetodoExtremo();
        }
    }, 2000);
}

// ============================================
// 🚨 MÉTODO EXTREMO (se o anterior falhar)
// ============================================

function tentarMetodoExtremo() {
    console.log('🚨 INICIANDO MÉTODO EXTREMO...');

    var campos = [
        { id: 'anoMesInicial', maxlength: 6, pattern: '\\d{6}', title: 'Formato: AAAAMM' },
        { id: 'aponInicial', maxlength: 6, pattern: '\\d{6}', title: '1 a 999999' },
        { id: 'anoMesFinal', maxlength: 6, pattern: '\\d{6}', title: 'Formato: AAAAMM' },
        { id: 'aponFinal', maxlength: 6, pattern: '\\d{6}', title: '1 a 999999' }
    ];

    campos.forEach(function (campoInfo) {
        var campoAntigo = document.getElementById(campoInfo.id);
        if (!campoAntigo) return;

        var parent = campoAntigo.parentNode;
        var value = campoAntigo.value;

        // CRIAR NOVO ELEMENTO DO ZERO
        var novoCampo = document.createElement('input');
        novoCampo.type = 'text';
        novoCampo.id = campoInfo.id;
        novoCampo.className = campoAntigo.className;
        novoCampo.maxLength = campoInfo.maxlength;
        novoCampo.pattern = campoInfo.pattern;
        novoCampo.title = campoInfo.title;
        novoCampo.value = value;

        // REMOVER COMPLETAMENTE O ANTIGO
        parent.removeChild(campoAntigo);
        parent.appendChild(novoCampo);

        console.log('🔄 Campo ' + campoInfo.id + ' recriado do zero');
    });

    console.log('✅ Método extremo concluído');
}

// ============================================
// DEBUG DE SELAGEM - Adicionar ao init.js
// ============================================

function debugSelagem() {
    console.group('🔍 DEBUG DE SELAGEM');

    // Verificar estado atual
    if (window.appState) {
        console.log('📊 Estado do appState:');
        console.log('  • Opção selecionada:', window.appState.getOpcaoSelecionada());
        console.log('  • Selos solicitados:', window.appState.getSelosSolicitados());
        console.log('  • Usuário:', window.appState.getUsuario());
    }

    // Verificar campos do formulário
    var campos = ['dataAto', 'anoMesInicial', 'aponInicial', 'anoMesFinal', 'aponFinal'];
    campos.forEach(function (id) {
        var campo = document.getElementById(id);
        if (campo) {
            console.log('  • ' + id + ':', campo.value);
        }
    });

    // Verificar mapeamento de selos
    if (window.selosManager) {
        var mapeamento = window.selosManager.obterMapeamentoSelos ?
            window.selosManager.obterMapeamentoSelos() :
            'Método não disponível';
        console.log('  • Mapeamento de selos:', mapeamento);
    }

    console.groupEnd();
}

// Expor para debug no console
window.debugSelagem = debugSelagem;

// ============================================
// EXECUTAR IMEDIATAMENTE
// ============================================

// Executar quando DOM estiver pronto
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function () {
        setTimeout(eliminarPlaceholdersForcaBruta, 100);
    });
} else {
    setTimeout(eliminarPlaceholdersForcaBruta, 100);
}

// Executar também quando app for inicializada
if (window.eventBus) {
    window.eventBus.on(window.eventBus.EVENTOS.APP_INICIALIZADA, function () {
        setTimeout(eliminarPlaceholdersForcaBruta, 300);
    });
}

// Executar um pouco depois também (por garantia)
setTimeout(eliminarPlaceholdersForcaBruta, 2000);

// Aguardar DOM e módulos
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function () {
        // Dar tempo para módulos carregarem
        setTimeout(inicializarAplicacao, 100);
    });
} else {
    setTimeout(inicializarAplicacao, 100);
}