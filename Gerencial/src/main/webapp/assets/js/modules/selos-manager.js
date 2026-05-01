// ============================================
// SELOS MANAGER - Gestão de selos do sistema
// Versão ES5 - MIGRADA para FASE 1 e 2
// ============================================

window.selosManager = (function () {
    'use strict';

    // Configuração de mapeamento de selos
    var MAPEAMENTO_SELOS = {
        'TP1': '0001',  // Pago TP1
        'TPD': '0003',  // Difer. TPD
        'TPI': '0004',  // Isento TPI
        'TP3': '0009',  // Pago TP3
        'TP4': '0010'   // Pago TP4
    };

    var MAPEAMENTO_REVERSO = {
        '0001': 'TP1',
        '0003': 'TPD',
        '0004': 'TPI',
        '0009': 'TP3',
        '0010': 'TP4'
    };

    // Cache para selos disponíveis
    var cacheSelos = {
        dados: null,
        timestamp: null,
        validoPor: 5 * 60 * 1000 // 5 minutos
    };

    // ============ VARIÁVEL A SER ADICIONADA ============
    var eventosConfigurados = false;
    // ===================================================

    // ============================================
    // INICIALIZAÇÃO
    // ============================================

    function inicializar() {
        console.log('🏷️ Selos Manager inicializando...');

        // 🔥 GARANTIR QUE O EVENT BUS ESTEJA CONFIGURADO SEMPRE
        configurarEventBus();

        console.log('✅ Selos Manager inicializado');
    }

    function configurarEventBus() {
        if (!window.eventBus) {
            console.warn('⚠️ eventBus não disponível, tentando novamente em 500ms');
            setTimeout(configurarEventBus, 500);
            return;
        }

        var EB = window.eventBus.EVENTOS;

        if (!EB) {
            console.warn('⚠️ EVENTOS não definido no Event Bus');
            return;
        }

        // 🔥 LISTENER PARA OPÇÃO SELECIONADA - Limpar selos quando opção mudar
        window.eventBus.on(EB.OPCAO_SELECIONADA, function (opcao) {
            console.log('🔄 Selos Manager: Opção alterada detectada, limpando selos...', opcao);

            // Limpar selos solicitados
            limparSelosSolicitados();

            // Invalidar cache
            invalidarCacheSelos();

            // Forçar recarga de selos disponíveis
            setTimeout(function () {
                var app = window.app || null;
                carregarSelosDisponiveis(app);
            }, 200);
        });

        // 🔥 LISTENER PARA RESPOSTA DE SELAGEM
        window.eventBus.on(EB.SELAGEM_CONCLUIDA, function (resultado) {
            console.log('🔄 Selagem concluída, processando atualização...');
            processarRespostaSelagemParaAtualizacao(resultado);
        });

        console.log('🎧 Event Bus configurado para Selos Manager');
    }

    // ============================================
    // CARREGAMENTO DE SELOS DISPONÍVEIS
    // ============================================

    function processarRespostaSelagemParaAtualizacao(response) {
        console.log('🔄 Processando resposta de selagem para atualização:', response);

        if (!response || !response.success) {
            console.warn('⚠️ Selagem não foi bem-sucedida, não atualizando selos');
            return null;
        }

        // Obter selos utilizados da resposta
        var selosUtilizados = response.selos_utilizados || response.dados?.selos_utilizados;
        var totalUtilizados = response.total_selos_utilizados || 0;

        if (!selosUtilizados || totalUtilizados === 0) {
            console.warn('⚠️ Nenhum dado de selos utilizados na resposta');
            return null;
        }

        console.log('🧮 Selos utilizados na selagem:', selosUtilizados);

        // Obter selos disponíveis atuais
        var selosDisponiveisAtuais = window.appState ? window.appState.getSelosDisponiveis() : {};

        // Subtrair selos utilizados
        var novosSelosDisponiveis = {};
        var tipos = ['TP1', 'TP3', 'TP4', 'TPD', 'TPI'];
        var houveAlteracao = false;

        tipos.forEach(function (tipo) {
            var disponivelAtual = parseInt(selosDisponiveisAtuais[tipo] || 0);
            var utilizado = parseInt(selosUtilizados[tipo] || 0);
            var novoDisponivel = Math.max(0, disponivelAtual - utilizado);

            novosSelosDisponiveis[tipo] = novoDisponivel;

            if (utilizado > 0) {
                houveAlteracao = true;
                console.log('📊 ' + tipo + ': ' + disponivelAtual + ' - ' + utilizado + ' = ' + novoDisponivel);
            }
        });

        if (!houveAlteracao) {
            console.log('ℹ️ Nenhuma alteração na contagem de selos');
            return novosSelosDisponiveis;
        }

        // Atualizar no appState
        if (window.appState && window.appState.setSelosDisponiveis) {
            window.appState.setSelosDisponiveis(novosSelosDisponiveis);
            console.log('✅ Selos disponíveis atualizados:', novosSelosDisponiveis);
        }

        // Atualizar interface IMEDIATAMENTE
        atualizarInterfaceSelos(novosSelosDisponiveis);

        return novosSelosDisponiveis;
    }

    function carregarSelosDisponiveis(app, forcarRecarga) {
        console.log('📦 Carregando selos disponíveis...' + (forcarRecarga ? ' [FORÇADO]' : ''));

        // 🔥 Se forçar recarga, invalidar cache primeiro
        if (forcarRecarga) {
            invalidarCacheSelos();
            if (window.api && window.api.cache) {
                for (var key in window.api.cache) {
                    if (key.includes('/selos/')) {
                        delete window.api.cache[key];
                    }
                }
            }
        }

        // Verificar cache primeiro
        if (cacheValido()) {
            console.log('💾 Usando selos do cache');
            processarSelosDoCache();
            return Promise.resolve(cacheSelos.dados);
        }

        // Emitir evento de início
        if (window.eventBus) {
            window.eventBus.emit(window.eventBus.EVENTOS.LOADING_INICIADO, {
                acao: 'carregar_selos'
            });
        }

        // Obter API do app ou global
        var api = obterAPI(app);

        if (!api || typeof api.contarSelos !== 'function') {
            var erro = 'API não disponível para carregar selos';
            console.error('❌ ' + erro);

            // Emitir erro
            if (window.eventBus) {
                window.eventBus.emitirErro('selos', new Error(erro));
            }

            return Promise.reject(new Error(erro));
        }

        return api.contarSelos()
            .then(function (response) {
                console.log('✅ Resposta contagem de selos:', response);

                // Processar resposta
                var resultado = processarRespostaSelos(response);

                if (resultado.success && resultado.selos) {
                    // Atualizar cache
                    atualizarCache(resultado.selos);

                    // Atualizar estado global
                    atualizarEstadoGlobal(resultado.selos);

                    // Atualizar interface
                    atualizarInterfaceSelos(resultado.selos);

                    // Verificar selos zerados
                    verificarESinalizarSelosZerados(resultado.selos);

                    // Emitir evento
                    if (window.eventBus) {
                        window.eventBus.emit(window.eventBus.EVENTOS.SELOS_CARREGADOS, {
                            selos: resultado.selos,
                            total: resultado.total,
                            timestamp: new Date().toISOString()
                        });
                    }

                    // Mostrar mensagem
                    if (window.uiComponents && resultado.total > 0) {
                        var detalhes = Object.keys(resultado.selos)
                            .map(function (tipo) {
                                return resultado.selos[tipo] + ' ' + tipo;
                            })
                            .join(', ');

                        window.uiComponents.mostrarMensagem(
                            'Selos disponíveis carregados: ' + resultado.total + ' selos',
                            'success'
                        );
                    }
                } else {
                    // Emitir erro
                    if (window.eventBus) {
                        window.eventBus.emitirErro('selos', new Error(resultado.mensagem), response);
                    }

                    if (window.uiComponents) {
                        window.uiComponents.mostrarMensagem(
                            'Erro ao carregar selos: ' + resultado.mensagem,
                            'error'
                        );
                    }
                }

                return resultado;
            })
            .catch(function (error) {
                console.error('❌ Erro ao carregar selos:', error);

                // Emitir erro
                if (window.eventBus) {
                    window.eventBus.emitirErro('selos', error);
                }

                // Mostrar mensagem de erro
                if (window.uiComponents) {
                    window.uiComponents.mostrarMensagem(
                        'Erro ao carregar selos disponíveis: ' + (error.message || 'Erro desconhecido'),
                        'error'
                    );
                }

                throw error;
            })
            .finally(function () {
                // Emitir evento de conclusão
                if (window.eventBus) {
                    window.eventBus.emit(window.eventBus.EVENTOS.LOADING_FINALIZADO, {
                        acao: 'carregar_selos'
                    });
                }
            });
    }

    function obterAPI(app) {
        // Tentar app.api primeiro, depois api global
        if (app && app.api) {
            return app.api;
        } else if (window.api) {
            return window.api;
        } else if (window.ApiClient) {
            return new window.ApiClient();
        }

        return null;
    }

    function cacheValido() {
        // 🔥 TENTAR CACHE MANAGER PRIMEIRO
        if (window.cacheManager) {
            var cacheKey = 'selos_disponiveis';
            var cached = window.cacheManager.obter('api', cacheKey);
            if (cached) {
                cacheSelos.dados = cached;
                cacheSelos.timestamp = Date.now();
                return true;
            }
        }

        // Fallback para cache antigo
        if (!cacheSelos.dados || !cacheSelos.timestamp) {
            return false;
        }

        var agora = Date.now();
        var idadeCache = agora - cacheSelos.timestamp;

        return idadeCache < cacheSelos.validoPor;
    }

    function atualizarCache(selos) {
        cacheSelos.dados = selos;
        cacheSelos.timestamp = Date.now();

        // 🔥 ARMazenar no Cache Manager
        if (window.cacheManager) {
            window.cacheManager.armazenar('api', 'selos_disponiveis', selos);
        }

        console.log('💾 Cache de selos atualizado');
    }

    function processarSelosDoCache() {
        if (!cacheSelos.dados) return;

        // Atualizar estado global
        atualizarEstadoGlobal(cacheSelos.dados);

        // Atualizar interface
        atualizarInterfaceSelos(cacheSelos.dados);

        // Verificar selos zerados
        verificarESinalizarSelosZerados(cacheSelos.dados);
    }

    // ============================================
    // PROCESSAMENTO DE RESPOSTAS
    // ============================================

    function processarRespostaSelos(response) {
        console.log('🧠 Processando resposta de selos:', response);

        var resultado = {
            success: false,
            mensagem: '',
            total: 0,
            selos: {}
        };

        // VALIDAÇÃO: garantir que é resposta válida
        if (!response || typeof response !== 'object') {
            resultado.mensagem = 'Resposta inválida da API';
            return resultado;
        }

        // SE for a instância do app (erro comum), retornar erro
        if (response.constructor && response.constructor.name === 'SeladorApp') {
            resultado.mensagem = 'Erro interno: objeto incorreto recebido';
            return resultado;
        }

        // CASO 1: Resposta de contagem de selos (GET /selos/contar)
        if (response.contagem && typeof response.contagem === 'object') {
            console.log('✅ CASO 1: Resposta de contagem de selos');

            resultado.success = true;
            resultado.mensagem = response.mensagem || response.message || 'Contagem de selos disponíveis';
            resultado.selos = normalizarSelos(response.contagem);

            // CASO 2: Resposta de cálculo de selos necessários (POST /selos/calcular)
        } else if (response.selos && typeof response.selos === 'object') {
            console.log('✅ CASO 2: Resposta de cálculo de selos necessários');

            resultado.success = response.success === true;
            resultado.mensagem = response.mensagem || response.message || 'Selos calculados com sucesso';
            resultado.selos = normalizarSelos(response.selos);

            // CASO 3: Resposta com success: true e data
        } else if (response.success === true && response.data) {
            console.log('✅ CASO 3: success + data');

            resultado.success = true;
            resultado.mensagem = response.mensagem || response.message || 'Operação concluída';

            // Extrair selos do data
            if (response.data.selos && typeof response.data.selos === 'object') {
                resultado.selos = normalizarSelos(response.data.selos);
            } else if (typeof response.data === 'object') {
                resultado.selos = extrairSelosDeObjeto(response.data);
            }

            // CASO 4: Formato desconhecido
        } else {
            console.log('⚠️ CASO 4: Formato não reconhecido');

            resultado.success = response.success === true;
            resultado.mensagem = response.mensagem || response.message ||
                (resultado.success ? 'Operação concluída' : 'Erro na operação');

            // Tentar extrair selos de qualquer propriedade
            resultado.selos = extrairSelosDeObjeto(response);
        }

        // Calcular total
        resultado.total = calcularTotalSelos(resultado.selos);

        console.log('🎯 Resultado processado:', resultado);
        return resultado;
    }

    function normalizarSelos(selos) {
        var normalizados = {};
        var tiposEsperados = ['TP1', 'TP3', 'TP4', 'TPD', 'TPI'];

        // Primeiro, tentar tipos diretos
        for (var i = 0; i < tiposEsperados.length; i++) {
            var tipo = tiposEsperados[i];
            if (selos[tipo] !== undefined) {
                normalizados[tipo] = parseInt(selos[tipo]) || 0;
            }
        }

        // Se não encontrou tipos diretos, tentar mapeamento
        if (Object.keys(normalizados).length === 0) {
            for (var codigo in selos) {
                if (selos.hasOwnProperty(codigo)) {
                    var tipoFrontend = MAPEAMENTO_REVERSO[codigo];
                    if (tipoFrontend) {
                        normalizados[tipoFrontend] = parseInt(selos[codigo]) || 0;
                    }
                }
            }
        }

        return normalizados;
    }

    function extrairSelosDeObjeto(objeto) {
        var selos = {};
        var tiposEsperados = ['TP1', 'TP3', 'TP4', 'TPD', 'TPI'];

        for (var key in objeto) {
            if (objeto.hasOwnProperty(key)) {
                var valor = objeto[key];

                // Verificar se é um tipo de selo
                if (tiposEsperados.includes(key.toUpperCase())) {
                    if (typeof valor === 'number') {
                        selos[key.toUpperCase()] = valor;
                    } else if (typeof valor === 'string' && /^\d+$/.test(valor)) {
                        selos[key.toUpperCase()] = parseInt(valor);
                    }
                }
            }
        }

        return selos;
    }

    function calcularTotalSelos(selos) {
        var total = 0;

        for (var tipo in selos) {
            if (selos.hasOwnProperty(tipo)) {
                total += parseInt(selos[tipo]) || 0;
            }
        }

        return total;
    }

    // ============================================
    // ATUALIZAÇÃO DE ESTADO E INTERFACE
    // ============================================

    function atualizarEstadoGlobal(selos) {
        if (!window.appState) {
            console.warn('⚠️ appState não disponível');
            return;
        }

        // Atualizar selos disponíveis
        window.appState.setSelosDisponiveis(selos);

        // Verificar selos zerados
        var selosZerados = verificarSelosZerados(selos);
        window.appState.setSelosZerados(selosZerados);

        console.log('📝 Estado global atualizado com selos');
    }

    function atualizarInterfaceSelos(selos) {
        // Atualizar displays visíveis
        var tipos = ['TP1', 'TP3', 'TP4', 'TPD', 'TPI'];

        for (var i = 0; i < tipos.length; i++) {
            var tipo = tipos[i];
            var quantidade = selos[tipo] || 0;

            atualizarDisplaySelo(tipo, quantidade, 'disponivel');
        }

        console.log('🖥️ Interface de selos atualizada');
    }

    function atualizarDisplaySelo(tipo, quantidade, tipoDisplay) {
        var displayId = tipoDisplay === 'disponivel' ? 'qtdDisponivel' + tipo : 'qtdSolicitado' + tipo;
        var display = document.getElementById(displayId);

        if (!display) return;

        display.textContent = quantidade;

        if (tipoDisplay === 'disponivel') {
            if (quantidade === 0) {
                display.classList.add('alerta-zerado');
                display.title = 'Selo zerado! Solicite mais selos deste tipo.';
            } else {
                display.classList.remove('alerta-zerado');
                display.title = '';
            }
        } else {
            if (quantidade > 0) {
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
    }

    function verificarESinalizarSelosZerados(selos) {
        var selosZerados = verificarSelosZerados(selos);

        if (selosZerados.length > 0) {
            // Emitir evento
            if (window.eventBus) {
                window.eventBus.emit(window.eventBus.EVENTOS.SELOS_ZERADOS_DETECTADOS, {
                    selosZerados: selosZerados,
                    timestamp: new Date().toISOString()
                });
            }

            // Mostrar alerta se não foi exibido recentemente
            if (window.appState && !window.appState.getEstado().alertasExibidos) {
                mostrarAlertaSelosZerados(selosZerados);

                // Marcar como exibido
                window.appState.atualizarEstado({
                    alertasExibidos: true
                });
            }
        }

        return selosZerados;
    }

    function verificarSelosZerados(selos) {
        var selosZerados = [];
        var tipos = ['TP1', 'TP3', 'TP4', 'TPD', 'TPI'];

        for (var i = 0; i < tipos.length; i++) {
            var tipo = tipos[i];
            var quantidade = selos[tipo] || 0;

            if (quantidade === 0) {
                selosZerados.push({
                    tipo: tipo,
                    nome: obterNomeSelo(tipo),
                    quantidade: 0
                });
            }
        }

        console.log('🔍 Selos zerados encontrados:', selosZerados.length);
        return selosZerados;
    }

    function mostrarAlertaSelosZerados(selosZerados) {
        if (selosZerados.length === 0) return;

        var mensagem = '';
        if (selosZerados.length === 1) {
            mensagem = '⚠️ Selo ' + selosZerados[0].nome + ' zerado!';
        } else {
            var nomes = selosZerados.map(function (selo) {
                return selo.nome;
            });
            mensagem = '⚠️ Os seguintes selos estão zerados: ' + nomes.join(', ') + '!';
        }

        if (window.uiComponents) {
            window.uiComponents.mostrarMensagem(mensagem, 'warning');
        }
    }

    // ============================================
    // CÁLCULO DE SELOS NECESSÁRIOS
    // ============================================

    function calcularSelosNecessarios(app, callback) {
        console.log('🧮 Iniciando cálculo de selos necessários...');

        // 🔥 DEBUG: Verificar o que está chegando
        console.log('🔍 DEBUG ENTRADA calcularSelosNecessarios:');
        console.log('   • app existe:', !!app);
        console.log('   • app.elementos existe:', app ? !!app.elementos : false);

        if (app && app.elementos) {
            console.log('   • anoMesInicial no app.elementos:', app.elementos.anoMesInicial);
            console.log('   • anoMesInicial valor:', app.elementos.anoMesInicial ? app.elementos.anoMesInicial.value : 'N/A');

            // Verificar também no DOM direto
            var anoMesDom = document.getElementById('anoMesInicial');
            console.log('   • anoMesInicial no DOM:', {
                existe: !!anoMesDom,
                valor: anoMesDom ? anoMesDom.value : 'N/A',
                innerHTML: anoMesDom ? anoMesDom.outerHTML.substring(0, 100) : 'N/A'
            });
        }

        // 1. Validar usando validationController se disponível
        var dados = null;
        if (window.validationController) {
            var validacao = window.validationController.validarParaCalculo(app);

            if (!validacao.podeCalcular) {
                var erro = { success: false, mensagem: validacao.mensagem };

                // Chamar callback com erro
                if (typeof callback === 'function') {
                    callback(erro);
                }

                // Emitir evento de erro
                if (window.eventBus) {
                    window.eventBus.emit(window.eventBus.EVENTOS.CALCULO_CONCLUIDO, erro);
                }

                return Promise.reject(new Error(validacao.mensagem));
            }

            // 🔥 USAR OS DADOS JÁ VALIDADOS E FORMATADOS DA VALIDAÇÃO
            dados = validacao.dados;
            console.log('📊 Dados para cálculo (já validados e formatados):', dados);
        } else {
            // Fallback: preparar dados manualmente
            dados = {
                data: app.elementos.dataAto ? app.elementos.dataAto.value.replace(/-/g, '') : '',
                tipoOperacao: window.appState ? window.appState.getOpcaoSelecionada().label : '',
                anoMesInicial: app.elementos.anoMesInicial ? app.elementos.anoMesInicial.value : '',
                aponInicial: app.elementos.aponInicial ? app.elementos.aponInicial.value : '',
                anoMesFinal: app.elementos.anoMesFinal ? app.elementos.anoMesFinal.value : '',
                aponFinal: app.elementos.aponFinal ? app.elementos.aponFinal.value : ''
            };
            console.log('📊 Dados para cálculo (fallback):', dados);
        }

        // 3. Obter API
        var api = obterAPI(app);
        if (!api || typeof api.calcularSelosNecessarios !== 'function') {
            var erro = 'API não disponível para cálculo de selos';
            console.error('❌ ' + erro);

            var resultadoErro = { success: false, mensagem: erro };

            if (typeof callback === 'function') {
                callback(resultadoErro);
            }

            if (window.eventBus) {
                window.eventBus.emit(window.eventBus.EVENTOS.CALCULO_CONCLUIDO, resultadoErro);
            }

            return Promise.reject(new Error(erro));
        }

        // 4. Executar cálculo
        console.log('📡 Chamando API calcularSelosNecessarios...');
        return api.calcularSelosNecessarios(dados)
            .then(function (response) {
                console.log('✅ Resposta cálculo recebida:', response);

                // Processar resposta
                var resultado = processarRespostaSelos(response);

                // Atualizar selos solicitados se cálculo foi bem-sucedido
                if (resultado.success && resultado.selos && Object.keys(resultado.selos).length > 0) {
                    // Atualizar estado global
                    if (window.appState) {
                        window.appState.setSelosSolicitados(resultado.selos);
                    }

                    // Atualizar interface
                    atualizarSelosSolicitadosUI(resultado.selos);

                    // Verificar conflitos com selos zerados
                    verificarConflitosSelosZerados(resultado.selos);
                }

                // Adicionar dados extras ao resultado
                resultado.dadosEntrada = dados;
                resultado.timestamp = new Date().toISOString();

                // Chamar callback se fornecido
                if (typeof callback === 'function') {
                    console.log('📞 Executando callback com:', resultado);
                    callback(resultado);
                }

                // Emitir evento de conclusão
                if (window.eventBus) {
                    window.eventBus.emit(window.eventBus.EVENTOS.CALCULO_CONCLUIDO, resultado);
                }

                return resultado;
            })
            .catch(function (error) {
                console.error('❌ Erro na API de cálculo:', error);

                var resultadoErro = {
                    success: false,
                    mensagem: 'Erro na API: ' + (error.message || 'Erro desconhecido'),
                    error: error,
                    timestamp: new Date().toISOString()
                };

                // Chamar callback com erro
                if (typeof callback === 'function') {
                    callback(resultadoErro);
                }

                // Emitir evento de erro
                if (window.eventBus) {
                    window.eventBus.emit(window.eventBus.EVENTOS.CALCULO_CONCLUIDO, resultadoErro);
                    window.eventBus.emitirErro('selos', error, dados);
                }

                throw error;
            });
    }

    function atualizarSelosSolicitadosUI(selos) {
        console.log('🔄 Atualizando UI com selos solicitados:', selos);

        var tipos = ['TP1', 'TP3', 'TP4', 'TPD', 'TPI'];
        var totalSolicitado = 0;

        // Atualizar cada tipo de selo
        for (var i = 0; i < tipos.length; i++) {
            var tipo = tipos[i];
            var quantidade = selos[tipo] || 0;

            // Atualizar input oculto
            var input = document.getElementById('input' + tipo);
            if (input) {
                input.value = quantidade;
            }

            // Atualizar display visível
            atualizarDisplaySelo(tipo, quantidade, 'solicitado');

            totalSolicitado += quantidade;
        }

        // Atualizar floating totalizer
        if (window.floatingTotalizer && window.floatingTotalizer.atualizar) {
            // Precisamos de app, mas pode ser obtido via window.app
            var app = window.app || null;
            window.floatingTotalizer.atualizar(app, selos);
        }

        // Atualizar totalizador principal
        if (window.uiComponents && window.uiComponents.atualizarTotalizadorSelos) {
            var app = window.app || { elementos: {} };
            window.uiComponents.atualizarTotalizadorSelos(app, selos);
        }

        console.log('✅ UI atualizada. Total solicitado:', totalSolicitado);
    }

    function verificarConflitosSelosZerados(selosSolicitados) {
        if (!window.appState) return;

        var selosZerados = window.appState.getSelosZerados();
        if (selosZerados.length === 0) return;

        var conflitos = [];

        for (var i = 0; i < selosZerados.length; i++) {
            var seloZerado = selosZerados[i];
            var quantidadeSolicitada = selosSolicitados[seloZerado.tipo] || 0;

            if (quantidadeSolicitada > 0) {
                conflitos.push(seloZerado.nome);
            }
        }

        if (conflitos.length > 0 && window.uiComponents) {
            var mensagem = '⚠️ Atenção! Os seguintes selos solicitados estão zerados: ' +
                conflitos.join(', ') + '. A selagem pode falhar.';

            window.uiComponents.mostrarMensagem(mensagem, 'warning');
        }
    }

    // ============================================
    // GESTÃO DE SELOS SOLICITADOS
    // ============================================

    function limparSelosSolicitados(app) {
        console.log('🧹 Limpando selos solicitados...');

        // Limpar estado global
        if (window.appState) {
            window.appState.setSelosSolicitados({});
        }

        // Limpar interface
        var tipos = ['TP1', 'TP3', 'TP4', 'TPD', 'TPI'];

        for (var i = 0; i < tipos.length; i++) {
            var tipo = tipos[i];

            // Limpar quantidade solicitada na UI
            atualizarDisplaySelo(tipo, 0, 'solicitado');

            // Limpar input oculto
            var input = document.getElementById('input' + tipo);
            if (input) {
                input.value = '0';
            }
        }

        // Ocultar floating totalizer
        if (window.floatingTotalizer && window.floatingTotalizer.esconder) {
            window.floatingTotalizer.esconder();
        }

        // Ocultar totalizador principal
        if (window.uiComponents && window.uiComponents.atualizarTotalizadorSelos) {
            var appParaUI = app || window.app || { elementos: {} };
            window.uiComponents.atualizarTotalizadorSelos(appParaUI, {});
        }

        console.log('✅ Selos solicitados limpos');
    }

    // ============================================
    // UTILITÁRIOS
    // ============================================

    function obterNomeSelo(tipo) {
        var nomes = {
            'TP1': 'Pago TP1',
            'TP3': 'Pago TP3',
            'TP4': 'Pago TP4',
            'TPD': 'Difer. TPD',
            'TPI': 'Isento TPI'
        };
        return nomes[tipo] || tipo;
    }

    function obterMapeamentoSelos() {
        return {
            frontendParaBanco: Object.assign({}, MAPEAMENTO_SELOS),
            bancoParaFrontend: Object.assign({}, MAPEAMENTO_REVERSO)
        };
    }

    // ============================================
    // COMPATIBILIDADE COM API ANTIGA
    // ============================================

    // Função carregar mantida para compatibilidade
    function carregar(app) {
        console.warn('⚠️ selosManager.carregar() é obsoleto - Use carregarSelosDisponiveis()');
        return carregarSelosDisponiveis(app);
    }

    // ============================================
    // CORREÇÃO DE CACHE - ADICIONAR CONTEXTO
    // ============================================

    // Substituir a função cacheValido() existente (linha ~70)
    function cacheValido() {
        if (!cacheSelos.dados || !cacheSelos.timestamp) {
            return false;
        }

        // ✅ NOVO: Verificar idade
        var agora = Date.now();
        var idadeCache = agora - cacheSelos.timestamp;

        // ❌ Cache expirado (> 2 minutos)
        if (idadeCache > 120000) { // 2 minutos
            console.log('🗑️ Cache de selos expirado');
            cacheSelos.dados = null;
            cacheSelos.timestamp = null;
            return false;
        }

        return true;
    }

    // Adicionar função para invalidar cache quando necessário
    function invalidarCacheSelos() {
        cacheSelos.dados = null;
        cacheSelos.timestamp = null;
        console.log('🧹 Cache de selos invalidado');
    }

    // ============================================
    // API PÚBLICA - NOVA
    // ============================================

return {
    // Inicialização
    inicializar: inicializar,

    // Carregamento
    carregarSelosDisponiveis: carregarSelosDisponiveis,
    carregar: carregar, // Compatibilidade

    // Cálculo
    calcularSelosNecessarios: calcularSelosNecessarios,

    // Gestão
    limparSelosSolicitados: limparSelosSolicitados,

    // Processamento
    processarRespostaSelos: processarRespostaSelos,
    processarRespostaSelagemParaAtualizacao: processarRespostaSelagemParaAtualizacao, // NOVO

    // Utilitários
    obterNomeSelo: obterNomeSelo,
    obterMapeamentoSelos: obterMapeamentoSelos,

    // Cache
    invalidarCache: invalidarCacheSelos,
    _getCache: function() { return cacheSelos; },
    _clearCache: function() {
        cacheSelos.dados = null;
        cacheSelos.timestamp = null;
        console.log('🧹 Cache de selos limpo');
    }
};
})();

// Auto-inicialização
if (typeof window !== 'undefined') {
    setTimeout(function () {
        if (window.selosManager && window.selosManager.inicializar) {
            window.selosManager.inicializar();
        }
    }, 1000);
}

console.log('✅ selos-manager.js MIGRADO - Usa appState e eventBus');