// ============================================
// BUTTON CONTROLLER - Gestão de estados de botões
// Versão ES5 - REFATORADA da FASE 2
// ============================================

window.buttonController = (function () {
    'use strict';

    var app = null;
    var elementos = {};

    // Cache de estados dos botões
    var estadosBotoes = {};

    // ============================================
    // INICIALIZAÇÃO
    // ============================================

    function inicializar(instanciaApp) {
        console.log('🎛️ Button Controller inicializando...');
        
        app = instanciaApp;
        
        // Configurar Event Bus
        configurarEventBus();
        
        // 🔥 CONFIGURAR BOTÕES PRINCIPAIS (ADICIONAR ESTA LINHA)
        configurarBotoesPrincipais(app);
        
        // Verificar estado inicial dos botões
        setTimeout(function() {
            verificarEstadoBotoes();
        }, 500);
        
        console.log('✅ Button Controller inicializado');
        return true;
    }

    function inicializarEstadosBotoes() {
        // Botões principais
        var botoesPrincipais = ['btnSolicitarSelos', 'btnSelarApontamentos'];

        for (var i = 0; i < botoesPrincipais.length; i++) {
            var botaoId = botoesPrincipais[i];
            var botao = elementos[botaoId];

            if (botao) {
                estadosBotoes[botaoId] = {
                    id: botaoId,
                    habilitado: false,
                    loading: false,
                    textoOriginal: botao.innerHTML,
                    elemento: botao
                };
            }
        }
    }

    function configurarEventBus() {
        if (!window.eventBus || typeof window.eventBus.on !== 'function') {
            console.warn('⚠️ [Button Controller] Event Bus não disponível');
            return;
        }

        if (!window.eventBus.EVENTOS) {
            console.warn('⚠️ [Button Controller] EVENTOS não definidos');
            return;
        }

        var EB = window.eventBus.EVENTOS;

        // PRIMEIRO LISTENER (linha 83 original)
        if (typeof handleSelosAtualizados === 'function') {
            window.eventBus.on(EB.SELOS_ATUALIZADOS, handleSelosAtualizados);
        }

        // SEGUNDO LISTENER (linha 95 original)
        if (typeof handleEstadoBotoesAlterado === 'function') {
            window.eventBus.on(EB.ESTADO_BOTOES_ALTERADO, handleEstadoBotoesAlterado);
        }
    }

    // ============================================
    // CONFIGURAÇÃO DOS BOTÕES PRINCIPAIS
    // ============================================

    function configurarBotoesPrincipais(app) {
        console.log('🔧 [Button Controller] Configurando botões principais...');

        // 🔥 USAR OS IDs CORRETOS DO SEU HTML
        var botoes = {
            btnSolicitarSelos: window.domManager.getElementById('btnSolicitarSelos'),
            btnSelarApontamentos: window.domManager.getElementById('btnSelarApontamentos')
        };

        // Log dos botões encontrados
        var encontrados = 0;
        for (var id in botoes) {
            if (botoes[id]) {
                encontrados++;
                console.log('✅ [Button Controller] Botão encontrado:', id, botoes[id]);
            } else {
                console.error('❌ [Button Controller] Botão não encontrado:', id);
            }
        }

        console.log('🔍 [Button Controller] Botões encontrados:', encontrados + '/' + Object.keys(botoes).length);

        // 🔥 CONFIGURAR EVENT LISTENERS
        if (botoes.btnSolicitarSelos) {
            console.log('🎯 [Button Controller] Configurando evento para btnSolicitarSelos');

            botoes.btnSolicitarSelos.addEventListener('click', function (e) {
                e.preventDefault();
                console.log('🛒 [Button Controller] Botão Solicitar Selos CLICADO');
                handleSolicitarSelos(app);
            });

            // Adicionar ID para referência
            botoes.btnSolicitarSelos.setAttribute('data-controller', 'button-controller');
        }

        if (botoes.btnSelarApontamentos) {
            console.log('🎯 [Button Controller] Configurando evento para btnSelarApontamentos');

            botoes.btnSelarApontamentos.addEventListener('click', function (e) {
                e.preventDefault();
                console.log('🏁 [Button Controller] Botão Selar Apontamentos CLICADO');
                handleSelarApontamentos(app);
            });

            // Adicionar ID para referência
            botoes.btnSelarApontamentos.setAttribute('data-controller', 'button-controller');
        }

        return botoes;
    }

    // ============================================
    // HANDLERS DOS BOTÕES
    // ============================================

    function handleSolicitarSelos(app) {
        console.log('🛒 [Button Controller] Iniciando solicitação de selos...');

        // 1. Verificar se pode calcular
        var podeCalcular = verificarPodeCalcular(app);

        if (!podeCalcular.valido) {
            console.warn('❌ [Button Controller] Não pode calcular:', podeCalcular.mensagem);
            mostrarMensagemUsuario(podeCalcular.mensagem, 'warning');
            return;
        }

        // 2. Mostrar loading
        mostrarLoading('Calculando selos necessários...');

        // 3. Chamar selos manager
        if (window.selosManager && window.selosManager.calcularSelosNecessarios) {
            console.log('🧮 [Button Controller] Chamando selosManager.calcularSelosNecessarios...');

            window.selosManager.calcularSelosNecessarios(app, function (resultado) {
                console.log('📊 [Button Controller] Resultado do cálculo:', resultado);

                // 4. Esconder loading
                esconderLoading();

                // 5. Processar resultado
                processarResultadoCalculo(resultado);

                // 6. Atualizar estado dos botões
                setTimeout(function () {
                    verificarEstadoBotoes();
                }, 100);
            });
        } else {
            console.error('❌ [Button Controller] selosManager não disponível');
            esconderLoading();
            mostrarMensagemUsuario('Módulo de selos não disponível', 'error');
        }
    }

    function handleSelarApontamentos(app) {
        console.log('🏁 [Button Controller] Iniciando processo de selagem...');

        // 1. Verificar se pode selar
        var podeSelar = verificarPodeSelar(app);

        if (!podeSelar.valido) {
            console.warn('❌ [Button Controller] Não pode selar:', podeSelar.mensagem);
            mostrarMensagemUsuario(podeSelar.mensagem, 'warning');
            return;
        }

        // 2. Obter dados para selagem
        var dadosSelagem = obterDadosParaSelagem(app);

        // 3. Mostrar confirmação
        mostrarConfirmacaoSelagem(dadosSelagem, function (confirmado) {
            if (confirmado) {
                executarSelagem(app, dadosSelagem);
            }
        });
    }

    // ============================================
    // FUNÇÕES AUXILIARES
    // ============================================

    function verificarPodeCalcular(app) {
        console.log('🔍 [Button Controller] Verificando se pode calcular...');

        var resultado = {
            valido: false,
            mensagem: ''
        };

        // 1. Verificar opção selecionada
        if (!window.appState || !window.appState.getOpcaoSelecionada()) {
            resultado.mensagem = 'Selecione uma opção antes de calcular os selos';
            return resultado;
        }

        // 2. Verificar data
        var dataAto = window.domManager.getElementById('dataAto');
        if (!dataAto || !dataAto.value) {
            resultado.mensagem = 'Informe a data do ato';
            return resultado;
        }

        // 3. Verificar intervalo (pelo menos inicial)
        var anoMesInicial = window.domManager.getElementById('anoMesInicial');
        var aponInicial = window.domManager.getElementById('aponInicial');

        if ((!anoMesInicial || !anoMesInicial.value) && (!aponInicial || !aponInicial.value)) {
            resultado.mensagem = 'Informe pelo menos o intervalo inicial (Ano/Mês ou Apontamento)';
            return resultado;
        }

        // Se passou em todas as validações
        resultado.valido = true;
        resultado.mensagem = 'Pronto para calcular selos';

        return resultado;
    }

    function verificarPodeSelar(app) {
        console.log('🔍 [Button Controller] Verificando se pode selar...');

        var resultado = {
            valido: false,
            mensagem: ''
        };

        // 1. Primeiro verificar se pode calcular (reusa a validação)
        var podeCalcular = verificarPodeCalcular(app);
        if (!podeCalcular.valido) {
            resultado.mensagem = podeCalcular.mensagem;
            return resultado;
        }

        // 2. Verificar se tem selos solicitados
        if (window.appState) {
            var selosSolicitados = window.appState.getSelosSolicitados();
            var totalSelos = calcularTotalSelos(selosSolicitados);

            if (totalSelos === 0) {
                resultado.mensagem = 'Solicite os selos necessários antes de selar';
                return resultado;
            }
        }

        // Se passou em todas as validações
        resultado.valido = true;
        resultado.mensagem = 'Pronto para selar';

        return resultado;
    }

    function calcularTotalSelos(selos) {
        if (!selos) return 0;

        var total = 0;
        var tipos = ['TP1', 'TP3', 'TP4', 'TPD', 'TPI'];

        for (var i = 0; i < tipos.length; i++) {
            total += parseInt(selos[tipos[i]] || 0);
        }

        return total;
    }

    function obterDadosParaSelagem(app) {
        var dados = {
            dataAto: '',
            opcao: null,
            intervalo: {},
            usuario: 'CONVIDADO',
            selosSolicitados: {}
        };

        // Obter data
        var dataAtoElement = window.domManager.getElementById('dataAto');
        if (dataAtoElement) {
            dados.dataAto = dataAtoElement.value;
        }

        // Obter opção
        if (window.appState) {
            dados.opcao = window.appState.getOpcaoSelecionada();
            dados.usuario = window.appState.getUsuario();
            dados.selosSolicitados = window.appState.getSelosSolicitados();
        }

        // Obter intervalo
        dados.intervalo = {
            anoMesInicial: window.domManager.getElementById('anoMesInicial') ? window.domManager.getElementById('anoMesInicial').value : '',
            aponInicial: window.domManager.getElementById('aponInicial') ? window.domManager.getElementById('aponInicial').value : '',
            anoMesFinal: window.domManager.getElementById('anoMesFinal') ? window.domManager.getElementById('anoMesFinal').value : '',
            aponFinal: window.domManager.getElementById('aponFinal') ? window.domManager.getElementById('aponFinal').value : ''
        };

        return dados;
    }

    // ============================================
    // FUNÇÕES DE UI (usando ui-components se disponível)
    // ============================================

    function mostrarMensagemUsuario(mensagem, tipo) {
        if (window.uiComponents && window.uiComponents.mostrarMensagem) {
            window.uiComponents.mostrarMensagem(mensagem, tipo);
        } else {
            // Fallback básico
            alert(tipo.toUpperCase() + ': ' + mensagem);
        }
    }

    function mostrarLoading(texto) {
        if (window.uiComponents && window.uiComponents.mostrarLoading) {
            window.uiComponents.mostrarLoading(texto);
        }
    }

    function esconderLoading() {
        if (window.uiComponents && window.uiComponents.esconderLoading) {
            window.uiComponents.esconderLoading();
        }
    }

    function mostrarConfirmacaoSelagem(dados, callback) {
        var mensagem = 'CONFIRMAR SELAGEM\n\n' +
            'Opção: ' + (dados.opcao ? dados.opcao.label : '') + '\n' +
            'Data: ' + dados.dataAto + '\n' +
            'Selos solicitados: ' + calcularTotalSelos(dados.selosSolicitados);

        if (window.uiComponents && window.uiComponents.mostrarModalConfirmacao) {
            window.uiComponents.mostrarModalConfirmacao('Confirmar Selagem', mensagem, callback);
        } else {
            // Fallback básico
            var confirmado = confirm(mensagem + '\n\nDeseja continuar?');
            if (callback && typeof callback === 'function') {
                callback(confirmado);
            }
        }
    }

    function processarResultadoCalculo(resultado) {
        if (resultado.success) {
            var mensagem = '✅ Selos calculados com sucesso!';
            if (resultado.total) {
                mensagem += ' Total: ' + resultado.total + ' selos';
            }
            mostrarMensagemUsuario(mensagem, 'success');

            // Atualizar floating totalizer se disponível
            if (window.floatingTotalizer && window.floatingTotalizer.atualizar) {
                window.floatingTotalizer.atualizar(resultado.selos || {});
            }
        } else {
            mostrarMensagemUsuario('❌ Erro ao calcular selos: ' + (resultado.mensagem || 'Erro desconhecido'), 'error');
        }
    }

    function executarSelagem(app, dados) {
        console.log('🚀 [Button Controller] Executando selagem...');

        mostrarLoading('Processando selagem...');

        if (window.selagemManager && window.selagemManager.executarSelagem) {
            window.selagemManager.executarSelagem(app, dados, function (resultado) {
                console.log('📊 [Button Controller] Resultado da selagem:', resultado);

                esconderLoading();

                if (resultado.success) {
                    var mensagem = '✅ Selagem concluída!\n' +
                        'Selados: ' + (resultado.selados || 0) + ' apontamento(s)';

                    mostrarMensagemUsuario(mensagem, 'success');

                    // Recarregar selos disponíveis
                    setTimeout(function () {
                        if (window.selosManager && window.selosManager.carregarSelosDisponiveis) {
                            window.selosManager.carregarSelosDisponiveis(app);
                        }
                    }, 1000);
                } else {
                    mostrarMensagemUsuario('❌ Erro na selagem: ' + (resultado.mensagem || 'Erro desconhecido'), 'error');
                }

                // Atualizar estado dos botões
                setTimeout(function () {
                    verificarEstadoBotoes();
                }, 100);
            });
        } else {
            console.error('❌ [Button Controller] selagemManager não disponível');
            esconderLoading();
            mostrarMensagemUsuario('Módulo de selagem não disponível', 'error');
        }
    }

    // ============================================
    // CONFIGURAÇÃO DE LISTENERS
    // ============================================

    function configurarListeners() {
        // Botão Solicitar Selos
        if (elementos.btnSolicitarSelos) {
            // Remover listener antigo se existir
            var novoBotao = elementos.btnSolicitarSelos.cloneNode(true);
            elementos.btnSolicitarSelos.parentNode.replaceChild(novoBotao, elementos.btnSolicitarSelos);
            elementos.btnSolicitarSelos = novoBotao;

            // Adicionar novo listener
            elementos.btnSolicitarSelos.addEventListener('click', function (e) {
                e.preventDefault();
                e.stopPropagation();

                if (!podeClicarBotao('btnSolicitarSelos')) {
                    console.log('⛔ Botão desabilitado - clique ignorado');
                    return;
                }

                console.log('🛒 Botão Solicitar Selos clicado');
                executarSolicitacaoSelos();
            });
        }

        // Botão Selar Apontamentos
        if (elementos.btnSelarApontamentos) {
            // Remover listener antigo se existir
            var novoBotao = elementos.btnSelarApontamentos.cloneNode(true);
            elementos.btnSelarApontamentos.parentNode.replaceChild(novoBotao, elementos.btnSelarApontamentos);
            elementos.btnSelarApontamentos = novoBotao;

            // Adicionar novo listener
            elementos.btnSelarApontamentos.addEventListener('click', function (e) {
                e.preventDefault();
                e.stopPropagation();

                if (!podeClicarBotao('btnSelarApontamentos')) {
                    console.log('⛔ Botão desabilitado - clique ignorado');
                    return;
                }

                console.log('🏁 Botão Selar Apontamentos clicado');
                executarSelagem();
            });
        }

        // Configurar tooltips
        configurarTooltips();
    }

    function podeClicarBotao(botaoId) {
        var estado = estadosBotoes[botaoId];
        if (!estado) return false;

        return estado.habilitado && !estado.loading;
    }

    // ============================================
    // VERIFICAÇÃO DE ESTADO DOS BOTÕES
    // ============================================

    function verificarEstadoBotoes() {
        console.log('🔍 Verificando estado dos botões...');

        verificarBotaoSolicitar();
        verificarBotaoSelar();

        // Emitir evento
        if (window.eventBus) {
            window.eventBus.emit(window.eventBus.EVENTOS.BOTOES_ATUALIZADOS, {
                botoes: estadosBotoes,
                timestamp: new Date().toISOString()
            });
        }

        return estadosBotoes;
    }

    function verificarBotaoSolicitar() {
        var botaoId = 'btnSolicitarSelos';
        var estado = estadosBotoes[botaoId];
        if (!estado || !estado.elemento) return;

        // Verificar se todos os campos de intervalo estão preenchidos
        var camposPreenchidos = verificarCamposIntervaloPreenchidos();

        // Atualizar estado
        estado.habilitado = camposPreenchidos;

        // Atualizar UI
        atualizarAparenciaBotao(estado.elemento, camposPreenchidos, 'solicitar');

        console.log('📊 Estado botão Solicitar:', {
            habilitado: camposPreenchidos,
            campos: obterValoresCamposIntervalo()
        });
    }

    function verificarBotaoSelar() {
        var botaoId = 'btnSelarApontamentos';
        var estado = estadosBotoes[botaoId];
        if (!estado || !estado.elemento) return;

        // Verificar se há selos solicitados
        var temSelosSolicitados = false;
        if (window.appState) {
            var selosSolicitados = window.appState.getSelosSolicitados();
            var total = window.appState.calcularTotalSelosSolicitados();
            temSelosSolicitados = total > 0;
        }

        // Atualizar estado
        estado.habilitado = temSelosSolicitados;

        // Atualizar UI
        atualizarAparenciaBotao(estado.elemento, temSelosSolicitados, 'selar');

        console.log('📊 Estado botão Selar:', {
            habilitado: temSelosSolicitados,
            totalSelos: window.appState ? window.appState.calcularTotalSelosSolicitados() : 0
        });
    }

    function verificarCamposIntervaloPreenchidos() {
        if (!elementos) return false;

        var campos = [
            elementos.anoMesInicial,
            elementos.aponInicial,
            elementos.anoMesFinal,
            elementos.aponFinal
        ];

        for (var i = 0; i < campos.length; i++) {
            if (!campos[i] || !campos[i].value || campos[i].value.trim() === '') {
                return false;
            }
        }

        return true;
    }

    function obterValoresCamposIntervalo() {
        return {
            anoMesInicial: elementos.anoMesInicial ? elementos.anoMesInicial.value : '',
            aponInicial: elementos.aponInicial ? elementos.aponInicial.value : '',
            anoMesFinal: elementos.anoMesFinal ? elementos.anoMesFinal.value : '',
            aponFinal: elementos.aponFinal ? elementos.aponFinal.value : ''
        };
    }

    // ============================================
    // ATUALIZAÇÃO DE APARÊNCIA
    // ============================================

    function atualizarAparenciaBotao(botao, habilitado, tipo) {
        if (!botao) return;

        // Remover todas as classes de estado
        botao.classList.remove('btn-habilitado', 'btn-desabilitado', 'btn-roxo', 'btn-cinza');

        // Adicionar classes apropriadas
        if (habilitado) {
            botao.classList.add('btn-habilitado', 'btn-roxo');
            botao.disabled = false;
            botao.style.opacity = '1';
            botao.style.cursor = 'pointer';
            botao.title = tipo === 'solicitar' ?
                'Clique para calcular os selos necessários' :
                'Clique para selar os apontamentos';
        } else {
            botao.classList.add('btn-desabilitado', 'btn-cinza');
            botao.disabled = true;
            botao.style.opacity = '0.6';
            botao.style.cursor = 'not-allowed';
            botao.title = tipo === 'solicitar' ?
                'Preencha todos os campos de intervalo para habilitar' :
                'Solicite selos primeiro para habilitar';
        }
    }

    function configurarTooltips() {
        // Tooltips para botões principais
        var tooltips = {
            'btnSolicitarSelos': 'Calcular selos necessários para o intervalo',
            'btnSelarApontamentos': 'Selar os apontamentos solicitados',
            'btnRefresh': 'Atualizar dados do sistema',
            'btnFechar': 'Fechar o Sistema Selador',
            'btnLogout': 'Sair do sistema',
            'btnHelp': 'Ajuda e informações do sistema'
        };

        for (var id in tooltips) {
            if (elementos[id] && !elementos[id].title) {
                elementos[id].title = tooltips[id];
            }
        }
    }

    // ============================================
    // GESTÃO DE LOADING
    // ============================================

    function mostrarLoadingBotao(botaoId, tipo) {
        var estado = estadosBotoes[botaoId];
        if (!estado || !estado.elemento) return false;

        // Já está em loading
        if (estado.loading) return true;

        estado.loading = true;
        estado.elemento.disabled = true;

        // Usar button-loader se disponível
        if (window.buttonLoader && window.buttonLoader.mostrarLoading) {
            if (tipo === 'solicitando') {
                window.buttonLoader.mostrarLoadingSolicitar(botaoId);
            } else if (tipo === 'selando') {
                window.buttonLoader.mostrarLoadingSelar(botaoId);
            } else {
                window.buttonLoader.mostrarLoading(botaoId, tipo);
            }
        } else {
            // Fallback básico
            var textoLoading = tipo === 'solicitando' ? 'Solicitando...' : 'Selando...';
            estado.elemento.innerHTML = '<i class="fas fa-spinner fa-spin"></i> ' + textoLoading;
        }

        console.log('🌀 Loading mostrado para botão:', botaoId);
        return true;
    }

    function esconderLoadingBotao(botaoId) {
        var estado = estadosBotoes[botaoId];
        if (!estado || !estado.elemento) return false;

        estado.loading = false;

        // Usar button-loader se disponível
        if (window.buttonLoader && window.buttonLoader.esconderLoading) {
            window.buttonLoader.esconderLoading(botaoId);
        } else {
            // Restaurar texto original
            estado.elemento.innerHTML = estado.textoOriginal;
        }

        // Re-verificar estado do botão
        setTimeout(function () {
            verificarEstadoBotoes();
        }, 100);

        console.log('✅ Loading escondido para botão:', botaoId);
        return true;
    }

    // ============================================
    // EXECUÇÃO DE AÇÕES
    // ============================================

    function executarSolicitacaoSelos() {
        console.log('🎯 Executando solicitação de selos...');

        // Emitir evento de início
        if (window.eventBus) {
            window.eventBus.emit(window.eventBus.EVENTOS.CALCULO_INICIADO, {
                timestamp: new Date().toISOString()
            });
        }

        // Validar antes de executar
        if (window.validationController) {
            var validacao = window.validationController.validarParaCalculo(app);

            if (!validacao.podeCalcular) {
                if (window.uiComponents) {
                    window.uiComponents.mostrarMensagem(validacao.mensagem, 'warning');
                }

                // Esconder loading mesmo em caso de erro
                if (window.eventBus) {
                    window.eventBus.emit(window.eventBus.EVENTOS.CALCULO_CONCLUIDO, {
                        success: false,
                        mensagem: validacao.mensagem
                    });
                }
                return;
            }
        }

        // Executar cálculo via selos-manager
        if (window.selosManager && window.selosManager.calcularSelosNecessarios) {
            window.selosManager.calcularSelosNecessarios(app, function (resultado) {
                console.log('📞 Callback cálculo recebido:', resultado);

                // Emitir evento de conclusão
                if (window.eventBus) {
                    window.eventBus.emit(window.eventBus.EVENTOS.CALCULO_CONCLUIDO, resultado);
                }
            });
        } else {
            console.error('❌ selosManager não disponível');

            // Emitir erro
            if (window.eventBus) {
                window.eventBus.emit(window.eventBus.EVENTOS.CALCULO_CONCLUIDO, {
                    success: false,
                    mensagem: 'Módulo de cálculo não disponível'
                });
            }
        }
    }

    function executarSelagem() {
        console.log('🎯 Executando selagem...');

        // Emitir evento de início
        if (window.eventBus) {
            window.eventBus.emit(window.eventBus.EVENTOS.SELAGEM_INICIADA, {
                timestamp: new Date().toISOString()
            });
        }

        // Validar antes de executar
        if (window.validationController) {
            var validacao = window.validationController.validarParaSelagem(app);

            if (!validacao.podeSelar) {
                if (window.uiComponents) {
                    window.uiComponents.mostrarMensagem(validacao.mensagem, 'warning');
                }

                // Esconder loading
                if (window.eventBus) {
                    window.eventBus.emit(window.eventBus.EVENTOS.SELAGEM_CONCLUIDA, {
                        success: false,
                        mensagem: validacao.mensagem
                    });
                }
                return;
            }
        }

        // Mostrar confirmação
        if (window.uiComponents && window.uiComponents.mostrarConfirmacao) {
            window.uiComponents.mostrarConfirmacao(
                'Deseja realmente selar os apontamentos? Esta ação não pode ser desfeita.',
                function () {
                    processarSelagem();
                },
                function () {
                    console.log('❌ Selagem cancelada pelo usuário');

                    // Esconder loading
                    if (window.eventBus) {
                        window.eventBus.emit(window.eventBus.EVENTOS.SELAGEM_CONCLUIDA, {
                            success: false,
                            mensagem: 'Cancelado pelo usuário'
                        });
                    }
                }
            );
        } else {
            // Fallback para confirm nativo
            if (confirm('Deseja realmente selar os apontamentos?')) {
                processarSelagem();
            } else {
                // Esconder loading
                if (window.eventBus) {
                    window.eventBus.emit(window.eventBus.EVENTOS.SELAGEM_CONCLUIDA, {
                        success: false,
                        mensagem: 'Cancelado pelo usuário'
                    });
                }
            }
        }
    }

    function processarSelagem() {
        console.log('🏁 Processando selagem...');

        // DEBUG: Mostrar o que temos disponível
        console.log('🔍 DEBUG - Disponíveis:', {
            windowApp: !!window.app,
            windowAppGetApp: window.app && typeof window.app.getApp,
            thisGetApp: this && typeof this.getApp,
            buttonController: !!window.buttonController,
            buttonControllerGetApp: window.buttonController && typeof window.buttonController.getApp
        });

        // SOLUÇÃO SIMPLES: Usar window.app diretamente
        var app = window.app;

        // Se window.app não existe, criar básico
        if (!app) {
            console.warn('⚠️ window.app não existe, criando básico...');
            app = criarAppBasico();
            window.app = app; // Salvar no global para uso futuro
        }

        // Se app ainda não tem elementos, coletar
        if (!app.elementos || Object.keys(app.elementos).length === 0) {
            console.warn('⚠️ App sem elementos, coletando...');
            coletarElementosBasicos(app);
        }

        // Se app não tem API, tentar obter
        if (!app.api) {
            console.warn('⚠️ App sem API, obtendo...');
            app.api = obterAPI();
        }

        console.log('✅ App preparado:', {
            temElementos: !!app.elementos && Object.keys(app.elementos).length,
            temAPI: !!app.api,
            elementos: app.elementos ? Object.keys(app.elementos) : []
        });

        // Agora usar selagemManager
        if (window.selagemManager && window.selagemManager.processarSelagemCompleta) {
            console.log('🎯 Chamando selagemManager...');

            window.selagemManager.processarSelagemCompleta(app, function (resultado) {
                console.log('📞 Resposta selagem:', resultado);

                // Emitir evento
                if (window.eventBus) {
                    window.eventBus.emit(window.eventBus.EVENTOS.SELAGEM_CONCLUIDA, resultado);
                }

                // Mostrar mensagem
                if (window.uiComponents) {
                    var tipo = resultado.success ? 'success' : 'error';
                    window.uiComponents.mostrarMensagem(resultado.mensagem, tipo);
                }

                // 🔥🔥🔥 CRÍTICO: FORÇAR ATUALIZAÇÃO DO BOTÃO "SELAR" E UI
                setTimeout(function () {
                    // 1. Verificar estado dos botões
                    if (window.buttonController && window.buttonController.verificarEstadoBotoes) {
                        window.buttonController.verificarEstadoBotoes();
                    }

                    // 2. Se selagem bem-sucedida, desabilitar botão "Selar"
                    if (resultado.success) {
                        console.log('🔄 Desabilitando botão Selar após selagem...');

                        var btnSelar = document.getElementById('btnSelarApontamentos');
                        if (btnSelar) {
                            btnSelar.disabled = true;
                            btnSelar.classList.remove('btn-habilitado', 'btn-roxo');
                            btnSelar.classList.add('btn-desabilitado', 'btn-cinza');
                            btnSelar.title = 'Solicite selos primeiro para habilitar';
                            btnSelar.style.opacity = '0.6';
                            btnSelar.style.cursor = 'not-allowed';
                        }

                        // 3. Também limpar visualmente os inputs de selos solicitados
                        var tipos = ['TP1', 'TP3', 'TP4', 'TPD', 'TPI'];
                        tipos.forEach(function (tipo) {
                            var display = document.getElementById('qtdSolicitado' + tipo);
                            if (display) {
                                display.textContent = '0';
                                display.classList.remove('solicitado-destaque', 'com-quantidade');
                            }
                        });

                        // 4. Ocultar totalizador flutuante se existir
                        if (window.floatingTotalizer && window.floatingTotalizer.esconder) {
                            window.floatingTotalizer.esconder();
                        }
                    }

                    // 5. Forçar atualização da UI após 500ms (para garantir)
                    setTimeout(function () {
                        if (window.uiComponents && window.uiComponents.atualizarInterface) {
                            window.uiComponents.atualizarInterface();
                        }
                    }, 500);

                }, 300); // Pequeno delay para a UI processar primeiro
            });

        } else {
            console.error('❌ selagemManager não disponível!');

            if (window.uiComponents) {
                window.uiComponents.mostrarMensagem(
                    'Erro: Módulo de selagem não carregado. Verifique se selagem-manager.js foi incluído.',
                    'error'
                );
            }
        }
    }

    // Funções auxiliares
    function criarAppBasico() {
        return {
            elementos: {},
            api: null,
            config: {}
        };
    }

    function coletarElementosBasicos(app) {
        if (!app.elementos) app.elementos = {};

        var ids = ['dataAto', 'anoMesInicial', 'aponInicial', 'anoMesFinal', 'aponFinal'];

        ids.forEach(function (id) {
            var elemento = document.getElementById(id);
            if (elemento) {
                app.elementos[id] = elemento;
            }
        });

        console.log('📋 Elementos coletados:', Object.keys(app.elementos));
    }

    function obterAPI() {
        if (window.api) return window.api;
        if (window.ApiClient) return new window.ApiClient();

        console.error('❌ Nenhuma API disponível!');
        return {
            processarSelagemCompleta: function () {
                return Promise.reject(new Error('API não disponível'));
            }
        };
    }

    // ============================================
    // API PÚBLICA
    // ============================================

    return {
        inicializar: inicializar,

        // Controle de estado
        verificarEstadoBotoes: verificarEstadoBotoes,
        getEstadoBotao: function (botaoId) { return estadosBotoes[botaoId]; },

        // Controle de loading
        mostrarLoadingBotao: mostrarLoadingBotao,
        esconderLoadingBotao: esconderLoadingBotao,

        // Execução
        executarSolicitacaoSelos: executarSolicitacaoSelos,
        executarSelagem: executarSelagem,

        // Para outros módulos
        getApp: function () { return app; }
    };
})();

console.log('✅ button-controller.js carregado - Gestão de estados de botões');