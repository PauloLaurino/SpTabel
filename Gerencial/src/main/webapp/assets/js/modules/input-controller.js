// ============================================
// INPUT CONTROLLER - Atualizado para FASE 3.1
// ============================================

window.inputController = (function () {
    'use strict';

    var app = null;
    var elementos = {};

    // 🔥 REMOVER declarações antigas de debouncing
    // var timeoutBusca = null; // ❌ REMOVER ESTA LINHA
    // var DEBOUNCE_DELAY = 500; // ❌ REMOVER ESTA LINHA

    // 🔥 ADICIONAR referência aos utils
    var debounceUtils = null;

    // ============================================
    // INICIALIZAÇÃO
    // ============================================

    function inicializar(instanciaApp) {
        console.log('⌨️ Input Controller inicializando...');

        app = instanciaApp;
        elementos = app.elementos;

        // 🔥 INICIALIZAR debounceUtils
        if (window.debounceUtils) {
            debounceUtils = window.debounceUtils;
            console.log('⚡ Debounce utils carregado');
        }

        if (!elementos) {
            console.error('❌ Elementos não disponíveis');
            return false;
        }

        // Configurar Event Bus
        configurarEventBus();

        // 🔥 USAR debounceUtils para configurar inputs
        configurarInputsComDebounce();

        console.log('✅ Input Controller inicializado com debouncing');
        return true;
    }

    function configurarEventBus() {
        if (!window.eventBus) {
            console.warn('⚠️ Event Bus não disponível');
            return;
        }

        var EB = window.eventBus.EVENTOS;

        // Escutar eventos relevantes
        window.eventBus.on(EB.OPCAO_SELECIONADA, function () {
            // Quando opção muda, configurar busca automática na data
            configurarBuscaAutomaticaData();
        });
    }

    // ============================================
    // CONFIGURAÇÃO DE INPUTS
    // ============================================

    function configurarInputsData() {
        // Data do Ato
        if (elementos.dataAto) {
            // Configurar data padrão
            configurarDataPadrao();

            // Configurar evento de change
            elementos.dataAto.addEventListener('change', function (e) {
                console.log('📅 Data do ato alterada:', this.value);

                // Validar data
                if (window.validationController) {
                    var validacao = window.validationController.validarDataAto(this.value);

                    if (!validacao.valido) {
                        this.classList.add('campo-invalido');
                        this.title = validacao.mensagem;

                        if (window.uiComponents) {
                            window.uiComponents.mostrarMensagem(validacao.mensagem, 'warning');
                        }
                        return;
                    } else {
                        this.classList.remove('campo-invalido');
                        this.title = '';
                    }
                }

                // Buscar intervalo automaticamente se tiver opção selecionada
                if (window.appState && window.appState.getOpcaoSelecionada()) {
                    buscarIntervaloComDebounce();
                }

                // Notificar alteração
                if (window.eventBus) {
                    window.eventBus.emit('data:alterada', {
                        data: this.value,
                        timestamp: new Date().toISOString()
                    });
                }
            });

            // Configurar busca automática
            configurarBuscaAutomaticaData();
        }
    }

    function configurarDataPadrao() {
        if (!elementos.dataAto || elementos.dataAto.value) return;

        var hoje = new Date();
        var dataFormatada = hoje.toISOString().split('T')[0];
        elementos.dataAto.value = dataFormatada;

        console.log('📅 Data padrão configurada:', dataFormatada);
    }

    function configurarBuscaAutomaticaData() {
        // Esta função configura a busca automática quando a data muda
        // e há uma opção selecionada
    }

    function buscarIntervaloComDebounce() {
        // 🔥 USAR debounceUtils se disponível
        if (debounceUtils && debounceUtils.debounceBusca) {
            console.log('⏰ Debouncing via debounceUtils');
            var debouncedBusca = debounceUtils.debounceBusca(function () {
                if (window.apontamentoService && window.apontamentoService.buscarIntervalo) {
                    window.apontamentoService.buscarIntervalo(app);
                }
            });
            debouncedBusca();
        } else {
            // 🔥 FALLBACK para compatibilidade
            console.warn('⚠️ debounceUtils não disponível, usando fallback');

            // Limpar timeout anterior
            if (timeoutBusca) {
                clearTimeout(timeoutBusca);
            }

            // Configurar novo timeout
            timeoutBusca = setTimeout(function () {
                if (window.apontamentoService && window.apontamentoService.buscarIntervalo) {
                    window.apontamentoService.buscarIntervalo(app);
                }
                timeoutBusca = null;
            }, 500); // 🔥 USAR VALOR PADRÃO

            console.log('⏰ Debouncing fallback: 500ms');
        }
    }

    function configurarInputsComDebounce() {
        console.log('⚡ Configurando inputs com debouncing...');

        // Configurar debouncing específico para cada input

        // 1. Data do Ato - debounce mais longo para buscas automáticas
        if (elementos.dataAto && debounceUtils) {
            var debouncedDataChange = debounceUtils.debounce(function () {
                console.log('📅 Data alterada com debouncing:', elementos.dataAto.value);

                // Validação
                if (window.validationController) {
                    var validacao = window.validationController.validarDataAto(elementos.dataAto.value);
                    if (!validacao.valido) {
                        elementos.dataAto.classList.add('campo-invalido');
                        elementos.dataAto.title = validacao.mensagem;
                        return;
                    } else {
                        elementos.dataAto.classList.remove('campo-invalido');
                        elementos.dataAto.title = '';
                    }
                }

                // Buscar intervalo se tiver opção
                if (window.appState && window.appState.getOpcaoSelecionada()) {
                    buscarIntervaloComDebounce();
                }

                // Notificar via Event Bus
                if (window.eventBus) {
                    window.eventBus.emit('data:alterada', {
                        data: elementos.dataAto.value,
                        timestamp: new Date().toISOString()
                    });
                }
            }, 500, 'data_ato_change');

            elementos.dataAto.addEventListener('change', debouncedDataChange);
        }

        // 2. Campos de intervalo - debounce médio
        var inputsIntervalo = [
            { id: 'anoMesInicial', tipo: 'anoMes' },
            { id: 'aponInicial', tipo: 'apon' },
            { id: 'anoMesFinal', tipo: 'anoMes' },
            { id: 'aponFinal', tipo: 'apon' }
        ];

        for (var i = 0; i < inputsIntervalo.length; i++) {
            var config = inputsIntervalo[i];
            var input = elementos[config.id];

            if (!input) continue;

            // 🔥 Configurar debouncing automático usando debounceUtils
            if (debounceUtils) {
                var debouncedIntervaloInput = debounceUtils.debounceInput(
                    config.id,
                    function (e) {
                        var inputAtual = e.target;
                        var tipo = obterTipoInput(inputAtual.id);

                        if (!tipo) return;

                        // Formatar e validar
                        if (tipo === 'anoMes') {
                            formatarEValidarAnoMesInput(inputAtual);
                        } else if (tipo === 'apon') {
                            formatarEValidarAponInput(inputAtual);
                        }

                        // Verificar botões com debounce
                        setTimeout(function () {
                            if (window.buttonController) {
                                window.buttonController.verificarEstadoBotoes();
                            }
                        }, 50);
                    }
                );

                // Configurar eventos
                input.addEventListener('input', debouncedIntervaloInput);
            }

            // Configurar outros eventos normalmente
            input.addEventListener('blur', function (e) {
                var inputAtual = e.target;
                var tipo = obterTipoInput(inputAtual.id);

                if (!tipo) return;

                // Validar no blur
                if (tipo === 'anoMes') {
                    validarCampoNoBlur(inputAtual, 'anoMes');
                } else if (tipo === 'apon') {
                    validarCampoNoBlur(inputAtual, 'apon');
                }
            });

            input.addEventListener('focus', function (e) {
                var inputAtual = e.target;
                inputAtual.classList.remove('campo-invalido');
                inputAtual.title = '';

                // Selecionar texto
                setTimeout(function () {
                    inputAtual.select();
                }, 100);
            });
        }

        // 3. Inputs de selos - debounce curto para resposta rápida
        configurarInputsSelosComDebounce();

        console.log('⚡ Inputs configurados com debouncing');
    }

    function configurarInputsSelosComDebounce() {
        var tiposSelos = ['TP1', 'TP3', 'TP4', 'TPD', 'TPI'];

        for (var i = 0; i < tiposSelos.length; i++) {
            var tipo = tiposSelos[i];
            var inputId = 'input' + tipo;
            var input = elementos[inputId];

            if (!input) continue;

            // 🔥 Debounce curto para inputs de selos
            if (debounceUtils) {
                var debouncedSeloInput = debounceUtils.debounceInput(
                    inputId,
                    function (e) {
                        var inputAtual = e.target;
                        var tipoInput = inputAtual.getAttribute('data-tipo') ||
                            inputAtual.id.replace('input', '');

                        // Formatar: apenas números, máximo 4 dígitos
                        var valor = inputAtual.value.replace(/[^0-9]/g, '');
                        if (valor.length > 4) {
                            valor = valor.substring(0, 4);
                        }
                        inputAtual.value = valor;

                        // Atualizar display visual
                        atualizarDisplaySelo(tipoInput, valor);

                        // Atualizar estado global
                        atualizarSeloNoEstado(tipoInput, valor);

                        // Verificar botões
                        setTimeout(function () {
                            if (window.buttonController) {
                                window.buttonController.verificarEstadoBotoes();
                            }
                        }, 50);
                    },
                    100 // 🔥 Debounce curto para resposta rápida
                );

                input.addEventListener('input', debouncedSeloInput);
            }

            // Eventos de blur e focus normais
            input.addEventListener('blur', function (e) {
                var inputAtual = e.target;
                var tipoInput = inputAtual.getAttribute('data-tipo') ||
                    inputAtual.id.replace('input', '');
                var valor = parseInt(inputAtual.value) || 0;

                // Validar limites
                if (valor < 0) {
                    inputAtual.value = '0';
                    atualizarDisplaySelo(tipoInput, '0');
                    atualizarSeloNoEstado(tipoInput, '0');
                } else if (valor > 9999) {
                    inputAtual.value = '9999';
                    atualizarDisplaySelo(tipoInput, '9999');
                    atualizarSeloNoEstado(tipoInput, '9999');
                }
            });

            input.addEventListener('focus', function (e) {
                var inputAtual = e.target;
                setTimeout(function () {
                    inputAtual.select();
                }, 100);
            });
        }
    }

    // ============================================
    // CONFIGURAÇÃO DE INPUTS DE INTERVALO
    // ============================================

    function configurarInputsIntervalo() {
        var inputsIntervalo = [
            { id: 'anoMesInicial', tipo: 'anoMes' },
            { id: 'aponInicial', tipo: 'apon' },
            { id: 'anoMesFinal', tipo: 'anoMes' },
            { id: 'aponFinal', tipo: 'apon' }
        ];

        for (var i = 0; i < inputsIntervalo.length; i++) {
            var config = inputsIntervalo[i];
            var input = elementos[config.id];

            if (!input) continue;

            // Configurar evento de input
            input.addEventListener('input', function (e) {
                var inputAtual = e.target;
                var tipo = obterTipoInput(inputAtual.id);

                if (!tipo) return;

                // Formatar e validar
                if (tipo === 'anoMes') {
                    formatarEValidarAnoMesInput(inputAtual);
                } else if (tipo === 'apon') {
                    formatarEValidarAponInput(inputAtual);
                }

                // Verificar estado dos botões após um delay
                setTimeout(function () {
                    if (window.buttonController) {
                        window.buttonController.verificarEstadoBotoes();
                    }
                }, 50);
            });

            // Configurar evento de blur
            input.addEventListener('blur', function (e) {
                var inputAtual = e.target;
                var tipo = obterTipoInput(inputAtual.id);

                if (!tipo) return;

                // Validar no blur
                if (tipo === 'anoMes') {
                    validarCampoNoBlur(inputAtual, 'anoMes');
                } else if (tipo === 'apon') {
                    validarCampoNoBlur(inputAtual, 'apon');
                }
            });

            // Configurar evento de focus
            input.addEventListener('focus', function (e) {
                var inputAtual = e.target;
                inputAtual.classList.remove('campo-invalido');
                inputAtual.title = '';

                // Selecionar todo o texto
                setTimeout(function () {
                    inputAtual.select();
                }, 100);
            });
        }
    }

    function obterTipoInput(id) {
        if (id.includes('anoMes')) return 'anoMes';
        if (id.includes('apon')) return 'apon';
        return null;
    }

    function formatarEValidarAnoMesInput(input) {
        if (!window.validationController) return;

        var resultado = window.validationController.formatarEValidarAnoMes(input.value, input);
        input.value = resultado.valor;

        return resultado;
    }

    function formatarEValidarAponInput(input) {
        if (!window.validationController) return;

        var resultado = window.validationController.formatarEValidarApon(input.value, input);
        input.value = resultado.valor;

        return resultado;
    }

    function validarCampoNoBlur(input, tipo) {
        if (!window.validationController) return;

        var validacao;

        if (tipo === 'anoMes') {
            validacao = window.validationController.validarCampoAnoMes(input.value);
        } else if (tipo === 'apon') {
            validacao = window.validationController.validarCampoApon(input.value);
        } else {
            return;
        }

        if (!validacao.valido && input.value) {
            input.classList.add('campo-invalido');
            input.title = validacao.mensagem;

            // Mostrar mensagem apenas se for um erro significativo
            if (input.value.length === (tipo === 'anoMes' ? 6 : 6)) {
                if (window.uiComponents) {
                    window.uiComponents.mostrarMensagem(validacao.mensagem, 'warning');
                }
            }
        }
    }

    // ============================================
    // CONFIGURAÇÃO DE INPUTS DE SELOS
    // ============================================

    function configurarInputsSelos() {
        var tiposSelos = ['TP1', 'TP3', 'TP4', 'TPD', 'TPI'];

        for (var i = 0; i < tiposSelos.length; i++) {
            var tipo = tiposSelos[i];
            var inputId = 'input' + tipo;
            var input = elementos[inputId];

            if (!input) continue;

            // Configurar evento de input
            input.addEventListener('input', function (e) {
                var inputAtual = e.target;
                var tipoInput = inputAtual.getAttribute('data-tipo') ||
                    inputAtual.id.replace('input', '');

                // Formatar: apenas números, máximo 4 dígitos
                var valor = inputAtual.value.replace(/[^0-9]/g, '');
                if (valor.length > 4) {
                    valor = valor.substring(0, 4);
                }
                inputAtual.value = valor;

                // Atualizar display visual
                atualizarDisplaySelo(tipoInput, valor);

                // Atualizar estado global
                atualizarSeloNoEstado(tipoInput, valor);

                // Verificar estado dos botões
                setTimeout(function () {
                    if (window.buttonController) {
                        window.buttonController.verificarEstadoBotoes();
                    }
                }, 50);
            });

            // Configurar evento de blur
            input.addEventListener('blur', function (e) {
                var inputAtual = e.target;
                var tipoInput = inputAtual.getAttribute('data-tipo') ||
                    inputAtual.id.replace('input', '');
                var valor = parseInt(inputAtual.value) || 0;

                // Validar limites
                if (valor < 0) {
                    inputAtual.value = '0';
                    atualizarDisplaySelo(tipoInput, '0');
                    atualizarSeloNoEstado(tipoInput, '0');
                } else if (valor > 9999) {
                    inputAtual.value = '9999';
                    atualizarDisplaySelo(tipoInput, '9999');
                    atualizarSeloNoEstado(tipoInput, '9999');
                }
            });

            // Configurar evento de focus
            input.addEventListener('focus', function (e) {
                var inputAtual = e.target;
                setTimeout(function () {
                    inputAtual.select();
                }, 100);
            });
        }
    }

    function atualizarDisplaySelo(tipo, quantidade) {
        var displayId = 'qtdSolicitado' + tipo;
        var display = document.getElementById(displayId);

        if (!display) return;

        display.textContent = quantidade;

        var qtdNum = parseInt(quantidade) || 0;
        if (qtdNum > 0) {
            display.classList.add('solicitado-destaque', 'com-quantidade');

            var itemElement = document.getElementById('seloItem' + tipo);
            if (itemElement) {
                itemElement.classList.add('com-solicitacao');
            }
        } else {
            display.classList.remove('solicitado-destaque', 'com-quantidade');

            var itemElement = document.getElementById('seloItem' + tipo);
            if (itemElement) {
                itemElement.classList.remove('com-solicitacao');
            }
        }
    }

    function atualizarSeloNoEstado(tipo, quantidade) {
        if (!window.appState) return;

        var selosAtuais = window.appState.getSelosSolicitados();
        var novosSelos = Object.assign({}, selosAtuais);

        novosSelos[tipo] = parseInt(quantidade) || 0;

        // Atualizar estado global
        window.appState.setSelosSolicitados(novosSelos);
    }

    // ============================================
    // FUNÇÕES AUXILIARES
    // ============================================

    function limparInputsIntervalo() {
        var campos = ['anoMesInicial', 'aponInicial', 'anoMesFinal', 'aponFinal'];

        for (var i = 0; i < campos.length; i++) {
            var campo = elementos[campos[i]];
            if (campo) {
                campo.value = '';
                campo.classList.remove('campo-invalido');
                campo.title = '';
            }
        }

        console.log('🧹 Inputs de intervalo limpos');
    }

    function obterValoresIntervalo() {
        return {
            anoMesInicial: elementos.anoMesInicial ? elementos.anoMesInicial.value : '',
            aponInicial: elementos.aponInicial ? elementos.aponInicial.value : '',
            anoMesFinal: elementos.anoMesFinal ? elementos.anoMesFinal.value : '',
            aponFinal: elementos.aponFinal ? elementos.aponFinal.value : ''
        };
    }

    // ============================================
    // FORMATADORES DE APONTAMENTOS
    // ============================================

    function formatarApontamentoSeisDigitos(valor) {
        if (!valor || valor.trim() === '') return '';

        // Remover zeros à esquerda para display
        var numero = parseInt(valor);
        if (isNaN(numero)) return valor;

        return String(numero); // Mostrar sem zeros à esquerda
    }

    function validarApontamentoSeisDigitos(valor) {
        if (!valor || valor.trim() === '') return false;

        var numero = parseInt(valor);
        if (isNaN(numero)) return false;

        // Validar se está entre 1 e 999999
        return numero >= 1 && numero <= 999999;
    }

    function converterParaSeisDigitos(valor) {
        if (!valor || valor.trim() === '') return '000000';

        var numero = parseInt(valor);
        if (isNaN(numero)) return '000000';

        return String(numero).padStart(6, '0');
    }

    // ============================================
    // API PÚBLICA
    // ============================================

    return {
        inicializar: inicializar,

        // Controle de inputs
        limparInputsIntervalo: limparInputsIntervalo,
        obterValoresIntervalo: obterValoresIntervalo,

        // Formatação
        formatarEValidarAnoMesInput: formatarEValidarAnoMesInput,
        formatarEValidarAponInput: formatarEValidarAponInput,

        // Para outros módulos
        getApp: function () { return app; },
        getElementos: function () { return elementos; }
    };
})();

console.log('✅ input-controller.js carregado - Eventos de inputs e formatação');