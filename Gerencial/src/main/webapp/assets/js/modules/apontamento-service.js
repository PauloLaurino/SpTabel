// ============================================
// APONTAMENTO SERVICE - Serviço para gestão de apontamentos
// Versão ES5 - MIGRADA para FASE 1 e 2
// ============================================

window.apontamentoService = (function () {
    'use strict';

    // Cache para buscas de intervalo
    var cacheBusca = {
        dados: null,
        chave: null,
        timestamp: null,
        validoPor: 2 * 60 * 1000 // 2 minutos
    };

    // Cache global (correção do erro)
    var cache = {};

    // Debouncing para buscas
    var timeoutDebounce = null;
    var DEBOUNCE_DELAY = 500;

    // ============================================
    // INICIALIZAÇÃO
    // ============================================

    function inicializar() {
        console.log('🔍 Apontamento Service inicializando...');

        // Configurar Event Bus
        configurarEventBus();

        // Inicializar cache (agora a variável está declarada)
        cache = {};

        console.log('✅ Apontamento Service inicializado');
        return true;
    }

    function configurarEventBus() {
        if (!window.eventBus) {
            console.warn('⚠️ Event Bus não disponível');
            return;
        }

        var EB = window.eventBus.EVENTOS;

        // ✅ ADICIONAR LISTENER PARA MUDANÇA DE OPÇÃO
        window.eventBus.on(EB.OPCAO_SELECIONADA, function (opcao) {
            console.log('🎯 Opção alterada, verificando se deve buscar intervalo:', opcao.label);

            // Verificar se já temos data preenchida
            if (window.domManager) {
                var elementos = window.domManager.getElementos();
                if (elementos && elementos.dataAto && elementos.dataAto.value) {
                    console.log('📅 Data já preenchida, buscando novo intervalo...');

                    // Aguardar um momento para garantir limpeza
                    setTimeout(function () {
                        buscarIntervalo(window.app || { elementos: elementos });
                    }, 500);
                }
            }
        });

        window.eventBus.on(EB.APP_ERRO, function (erro) {
            if (erro.modulo === 'apontamento') {
                console.error('❌ Erro no módulo de apontamentos:', erro);
            }
        });
    }

    // ============================================
    // BUSCA DE INTERVALO
    // ============================================

    function buscarIntervalo(app) {
        console.log('🚀 Buscando intervalo de apontamentos...');

        // Validar pré-requisitos
        var validacao = validarDadosParaBusca();
        if (!validacao.valida) {
            console.warn('⚠️ ' + validacao.mensagem);

            if (window.uiComponents) {
                window.uiComponents.mostrarMensagem(validacao.mensagem, 'warning');
            }

            // Emitir evento de erro
            if (window.eventBus) {
                window.eventBus.emit(window.eventBus.EVENTOS.INTERVALO_LIMPO, {
                    motivo: validacao.mensagem,
                    timestamp: new Date().toISOString()
                });
            }

            return Promise.reject(new Error(validacao.mensagem));
        }

        // Preparar dados para busca
        var dadosBusca = validacao.dados; // ✅ ESTA VARIÁVEL EXISTE AQUI
        var chaveBusca = criarChaveBusca(dadosBusca);

        // 🔥 VERIFICAR CACHE CORRETAMENTE
        if (cacheValido(chaveBusca, dadosBusca)) { // ✅ PASSANDO dadosBusca
            console.log('💾 Usando intervalo do cache');
            processarIntervaloDoCache();
            return Promise.resolve(cacheBusca.dados);
        }

        // Prevenir múltiplas buscas simultâneas
        if (window.appState && window.appState.isBuscandoIntervalo()) {
            console.log('⏳ Busca já em andamento, ignorando nova requisição');
            return Promise.resolve(null);
        }

        // Marcar como buscando
        if (window.appState) {
            window.appState.setBuscandoIntervalo(true);
        }

        // Emitir evento de início
        if (window.eventBus) {
            window.eventBus.emit(window.eventBus.EVENTOS.LOADING_INICIADO, {
                acao: 'buscar_intervalo'
            });
        }

        // Mostrar indicador de busca
        mostrarIndicadorBusca('Buscando apontamentos para o Ato selecionado');

        console.log('🔍 Executando busca:', dadosBusca);

        // Obter API
        var api = obterAPI(app);

        if (!api || typeof api.buscarIntervaloApontamentos !== 'function') {
            var erro = 'API não disponível para busca de intervalo';
            return tratarErroBusca(erro, null);
        }

        // Executar busca
        return api.buscarIntervaloApontamentos(
            dadosBusca.data,
            dadosBusca.codigoOperacao,
            dadosBusca.descricaoOperacao
        )
            .then(function (response) {
                return processarRespostaBusca(response, chaveBusca, dadosBusca);
            })
            .catch(function (error) {
                return tratarErroBusca(error, dadosBusca);
            })
            .finally(function () {
                // Finalizar busca
                if (window.appState) {
                    window.appState.setBuscandoIntervalo(false);
                }

                esconderIndicadorBusca();

                // Emitir evento de conclusão
                if (window.eventBus) {
                    window.eventBus.emit(window.eventBus.EVENTOS.LOADING_FINALIZADO, {
                        acao: 'buscar_intervalo'
                    });
                }
            });
    }

    function validarDadosParaBusca() {
        var resultado = {
            valida: false,
            mensagem: '',
            dados: {}
        };

        // Validar opção selecionada
        var opcaoSelecionada = window.appState ? window.appState.getOpcaoSelecionada() : null;
        if (!opcaoSelecionada) {
            resultado.mensagem = 'Selecione uma opção antes de buscar';
            return resultado;
        }

        // Validar data do ato
        var dataAtoElement = document.getElementById('dataAto');
        if (!dataAtoElement || !dataAtoElement.value) {
            resultado.mensagem = 'Informe a data do ato primeiro';
            return resultado;
        }

        // Validar data usando validationController se disponível
        if (window.validationController) {
            var validacaoData = window.validationController.validarDataAto(dataAtoElement.value);
            if (!validacaoData.valido) {
                resultado.mensagem = validacaoData.mensagem;
                return resultado;
            }
        }

        resultado.valida = true;
        resultado.mensagem = 'Dados válidos para busca';
        resultado.dados = {
            data: dataAtoElement.value,
            codigoOperacao: opcaoSelecionada.codigo,
            descricaoOperacao: opcaoSelecionada.descricao || opcaoSelecionada.label || ''
        };

        return resultado;
    }

    function criarChaveBusca(dadosBusca) {
        return dadosBusca.data + '|' + dadosBusca.codigoOperacao + '|' + dadosBusca.descricaoOperacao;
    }

    function cacheValido(chave, dadosBusca) {
        if (!dadosBusca) {
            console.warn('⚠️ cacheValido: dadosBusca não fornecido');
            return false;
        }

        // ✅ 1. VERIFICAR CACHE MANAGER (se disponível)
        if (window.cacheManager) {
            // Criar chave única com data + opção
            var chaveCompleta = dadosBusca.data + '|' + dadosBusca.codigoOperacao;

            var cacheData = window.cacheManager.obterCacheBusca(
                chaveCompleta,
                dadosBusca
            );

            if (cacheData) {
                // ✅ VERIFICAR SE DATA DO CACHE É IGUAL À ATUAL
                var dataAtual = document.getElementById('dataAto') ?
                    document.getElementById('dataAto').value : '';

                if (cacheData.data === dataAtual) {
                    console.log('💾 Cache hit válido (mesma data/opção)');
                    cacheBusca.dados = cacheData;
                    cacheBusca.chave = chave;
                    cacheBusca.timestamp = Date.now();
                    return true;
                } else {
                    console.log('🗑️ Cache inválido (data diferente)');
                    window.cacheManager.invalidarCacheBusca(chaveCompleta);
                    return false;
                }
            }
        }

        // ✅ 2. VERIFICAR CACHE LEGADO
        if (!cacheBusca.dados || cacheBusca.chave !== chave || !cacheBusca.timestamp) {
            return false;
        }

        // ✅ 3. VERIFICAR SE DATA MUDOU (COMPARAÇÃO DIRETA)
        var dataAtual = document.getElementById('dataAto') ?
            document.getElementById('dataAto').value : '';

        if (cacheBusca.dados.data && cacheBusca.dados.data !== dataAtual) {
            console.log('🗑️ Cache legado inválido (data diferente)');
            cacheBusca.dados = null;
            cacheBusca.chave = null;
            return false;
        }

        // ✅ 4. VERIFICAR TEMPO DE EXPIRAÇÃO
        var agora = Date.now();
        var idadeCache = agora - cacheBusca.timestamp;

        return idadeCache < cacheBusca.validoPor;
    }

    function atualizarCache(dados, chave, dadosBusca) {
        // ✅ ADICIONAR DATA ATUAL NOS DADOS DO CACHE
        dados.dataAtualCache = new Date().toISOString();
        dados.dataAtoOrigem = document.getElementById('dataAto') ?
            document.getElementById('dataAto').value : '';
        dados.codigoOpcaoOrigem = dadosBusca.codigoOperacao;

        cacheBusca.dados = dados;
        cacheBusca.chave = chave;
        cacheBusca.timestamp = Date.now();

        // 🔥 ARMazenar no Cache Manager com chave específica
        if (window.cacheManager && dadosBusca) {
            var chaveCache = dadosBusca.data + '|' + dadosBusca.codigoOperacao;
            window.cacheManager.cacheBusca(chaveCache, dadosBusca, dados);
        }

        console.log('💾 Cache atualizado com metadados:', {
            data: dados.dataAtoOrigem,
            opcao: dados.codigoOpcaoOrigem,
            timestamp: dados.dataAtualCache
        });
    }

    function verificarEstadoCache() {
        var dataAtual = document.getElementById('dataAto') ?
            document.getElementById('dataAto').value : '';
        var opcaoAtual = window.appState ?
            window.appState.getOpcaoSelecionada() : null;

        return {
            cacheExiste: !!cacheBusca.dados,
            dataCache: cacheBusca.dados ? cacheBusca.dados.dataAtoOrigem : null,
            dataAtual: dataAtual,
            opcaoCache: cacheBusca.dados ? cacheBusca.dados.codigoOpcaoOrigem : null,
            opcaoAtual: opcaoAtual ? opcaoAtual.codigo : null,
            valido: cacheBusca.dados &&
                cacheBusca.dados.dataAtoOrigem === dataAtual &&
                cacheBusca.dados.codigoOpcaoOrigem === (opcaoAtual ? opcaoAtual.codigo : null),
            idade: cacheBusca.timestamp ?
                Math.floor((Date.now() - cacheBusca.timestamp) / 1000) + 's' : 'N/A'
        };
    }

    function processarIntervaloDoCache() {
        if (!cacheBusca.dados) return;

        // Preencher campos
        popularCamposIntervalo(cacheBusca.dados);

        // Emitir evento
        if (window.eventBus) {
            window.eventBus.emit(window.eventBus.EVENTOS.INTERVALO_PREENCHIDO, {
                dados: cacheBusca.dados,
                source: 'cache',
                timestamp: new Date().toISOString()
            });
        }
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

    // ============================================
    // PROCESSAMENTO DE RESPOSTAS
    // ============================================

    function processarRespostaBusca(response, chaveBusca, dadosBusca) {
        var dados = extrairDadosResposta(response);

        // ✅ ADICIONAR: Verificar se dados são válidos antes de cache
        if (!dados || !verificarDadosValidos(dados)) {
            console.warn('⚠️ Nenhum apontamento encontrado - NÃO CACHEAR');

            // ❌ NÃO ATUALIZAR CACHE com "nada encontrado"
            // ❌ cacheBusca.dados = null; // Manter null

            // Limpar campos
            limparCamposIntervalo();

            return { success: false, mensagem: 'Nenhum apontamento encontrado' };
        }

        // Atualizar cache
        atualizarCache(dados, chaveBusca, dadosBusca);

        // Atualizar estado global
        atualizarEstadoComIntervalo(dados);

        // Popular campos na interface
        popularCamposIntervalo(dados);

        // Mostrar mensagem de sucesso
        var mensagemSucesso = 'Apontamentos encontrados com sucesso!';
        if (window.uiComponents) {
            window.uiComponents.mostrarMensagem(mensagemSucesso, 'success');
        }

        // Emitir evento
        if (window.eventBus) {
            window.eventBus.emit(window.eventBus.EVENTOS.APONTAMENTOS_BUSCADOS, {
                success: true,
                dados: dados,
                dadosBusca: dadosBusca,
                timestamp: new Date().toISOString()
            });

            window.eventBus.emit(window.eventBus.EVENTOS.INTERVALO_PREENCHIDO, {
                dados: dados,
                source: 'api',
                timestamp: new Date().toISOString()
            });
        }

        console.log('✅ Intervalo processado com sucesso');
        return { success: true, dados: dados, mensagem: mensagemSucesso };
    }

    function extrairDadosResposta(response) {
        if (!response) return null;

        var dadosParaProcessar = null;

        // Se resposta tem estrutura de sucesso
        if (response.success === true || response.sucesso === true) {
            dadosParaProcessar = response.data || response.dados || response;
        } else if (response.primeiro && response.ultimo) {
            // Se já tem estrutura direta
            dadosParaProcessar = response;
        } else if (typeof response === 'object') {
            // Tentar outras estruturas
            dadosParaProcessar = response;
        }

        // VERIFICAÇÃO: Não aceitar apontamentos com encontrado = false
        if (dadosParaProcessar &&
            dadosParaProcessar.primeiro &&
            dadosParaProcessar.primeiro.encontrado === false) {
            console.log('⚠️ Primeiro apontamento tem encontrado = false');
            return null;
        }

        if (dadosParaProcessar &&
            dadosParaProcessar.ultimo &&
            dadosParaProcessar.ultimo.encontrado === false) {
            console.log('⚠️ Último apontamento tem encontrado = false');
            return null;
        }

        return dadosParaProcessar;
    }

    function verificarDadosValidos(dados) {
        if (!dados) return false;

        // ✅ VERIFICAÇÃO MAIS RESTRITIVA
        var temPrimeiroValido = dados.primeiro &&
            (dados.primeiro.numapo1 || dados.primeiro.anoMes) &&
            (dados.primeiro.numapo2 || dados.primeiro.apontamento || dados.primeiro.numero);

        var temUltimoValido = dados.ultimo &&
            (dados.ultimo.numapo1 || dados.ultimo.anoMes) &&
            (dados.ultimo.numapo2 || dados.ultimo.apontamento || dados.ultimo.numero);

        if (!temPrimeiroValido || !temUltimoValido) {
            console.log('❌ Dados inválidos para cache:', {
                primeiroValido: temPrimeiroValido,
                ultimoValido: temUltimoValido,
                primeiro: dados.primeiro,
                ultimo: dados.ultimo
            });
            return false;
        }

        return true;
    }

    function criarMensagemNenhumApontamento() {
        var opcaoSelecionada = window.appState ? window.appState.getOpcaoSelecionada() : null;
        var rawOpcao = opcaoSelecionada ?
            (opcaoSelecionada.label || opcaoSelecionada.descricao || opcaoSelecionada.codigo) :
            'selecionada';

        var mapped = mapOpcaoParaMensagem(rawOpcao);
        var artigo = mapped.gender === 'f' ? 'Nenhuma' : 'Nenhum';
        var particulaEncontrado = mapped.gender === 'f' ? 'encontrada' : 'encontrado';

        return artigo + ' ' + mapped.label + ' ' + particulaEncontrado + ' na data selecionada';
    }

    function mapOpcaoParaMensagem(raw) {
        var text = (raw || '').toString().toLowerCase();

        // Mapeamento de opções para gênero gramatical
        var mapeamento = [
            // APONTAMENTO (masculino)
            {
                test: function (t) { return t.indexOf('apontamento') !== -1 && t.indexOf('digitaliz') === -1; },
                label: 'Apontamento', gender: 'm'
            },

            // INTIMAÇÃO/DIGITALIZAÇÃO (feminino)
            {
                test: function (t) { return t.indexOf('intima') !== -1 || t.indexOf('digitaliz') !== -1; },
                label: 'Intimação/Edital', gender: 'f'
            },

            // RETIRADA/BAIXA (feminino)
            {
                test: function (t) { return t.indexOf('retirada') !== -1 && t.indexOf('pagamento') === -1; },
                label: 'Retirada', gender: 'f'
            },

            // PAGAMENTO/BAIXA (masculino)
            {
                test: function (t) { return t.indexOf('pagamento') !== -1; },
                label: 'Pagamento', gender: 'm'
            },

            // INSTRUMENTO DIFERIDO (masculino)
            {
                test: function (t) { return t.indexOf('instrumento diferido') !== -1; },
                label: 'Protesto', gender: 'm'
            },

            // PROTESTO COM CUSTAS ANTECIPADAS (masculino)
            {
                test: function (t) { return t.indexOf('protesto com custas antecipadas') !== -1; },
                label: 'Protesto com Custas Pagas', gender: 'm'
            },

            // DEVOLUÇÃO (feminino)
            {
                test: function (t) { return t.indexOf('devolu') !== -1 || t.indexOf('devolução') !== -1 || t.indexOf('devolucao') !== -1; },
                label: 'Devolução', gender: 'f'
            },

            // SUSTAÇÃO DO PROTESTO (feminino)
            {
                test: function (t) { return t.indexOf('susta') !== -1 && t.indexOf('revog') === -1 && t.indexOf('definitiva') === -1; },
                label: 'Sustação', gender: 'f'
            },

            // REVOGAÇÃO DE SUSTAÇÃO (feminino)
            {
                test: function (t) { return t.indexOf('revog') !== -1 && t.indexOf('susta') !== -1; },
                label: 'Sustação Revogada', gender: 'f'
            },

            // SUSTAÇÃO DEFINITIVA (feminino)
            {
                test: function (t) { return t.indexOf('sustação definitiva') !== -1 || t.indexOf('sustacao definitiva') !== -1 || t.indexOf('definitiva') !== -1; },
                label: 'Sustação Definitiva', gender: 'f'
            },

            // SUSPENSÃO DE PROTESTO (feminino)
            {
                test: function (t) { return t.indexOf('suspens') !== -1 && t.indexOf('revog') === -1; },
                label: 'Suspensão', gender: 'f'
            },

            // REVOGAÇÃO DE SUSPENSO (feminino)
            {
                test: function (t) { return t.indexOf('revog') !== -1 && t.indexOf('suspens') !== -1 && t.indexOf('segunda') === -1; },
                label: 'Suspensão Revogada', gender: 'f'
            },

            // SEGUNDA REVOGAÇÃO (feminino)
            {
                test: function (t) { return t.indexOf('segunda revog') !== -1; },
                label: 'Segunda Revogação', gender: 'f'
            }
        ];

        for (var i = 0; i < mapeamento.length; i++) {
            try {
                if (mapeamento[i].test(text)) {
                    return { label: mapeamento[i].label, gender: mapeamento[i].gender };
                }
            } catch (e) { /* ignore */ }
        }

        // Fallback
        var fallback = (raw || '').toString().trim();
        if (fallback === '') fallback = 'opção selecionada';
        fallback = fallback.charAt(0).toUpperCase() + fallback.slice(1);

        // Determinar gênero pelo sufixo
        var lastWord = fallback.split(' ').pop().toLowerCase();
        var gender = 'm'; // padrão masculino

        if (lastWord.endsWith('ção') || lastWord.endsWith('são') ||
            lastWord.endsWith('dade') || lastWord.endsWith('gem') ||
            lastWord === 'sustação' || lastWord === 'suspensão' ||
            lastWord === 'devolução' || lastWord === 'intimação' ||
            lastWord === 'retirada' || lastWord === 'digitalização') {
            gender = 'f';
        }

        return { label: fallback, gender: gender };
    }

    function tratarErroBusca(error, dadosBusca) {
        console.error('❌ Erro na busca de intervalo:', error);

        var mensagem = 'Erro ao buscar apontamentos';
        if (error && error.message) {
            mensagem += ': ' + error.message;
        }

        if (window.uiComponents) {
            window.uiComponents.mostrarMensagem(mensagem, 'error');
        }

        // Emitir evento de erro
        if (window.eventBus) {
            window.eventBus.emitirErro('apontamento', error, dadosBusca);

            window.eventBus.emit(window.eventBus.EVENTOS.APONTAMENTOS_BUSCADOS, {
                success: false,
                mensagem: mensagem,
                error: error,
                dadosBusca: dadosBusca,
                timestamp: new Date().toISOString()
            });
        }

        return { success: false, mensagem: mensagem, error: error };
    }

    // ============================================
    // ATUALIZAÇÃO DE ESTADO E INTERFACE
    // ============================================

    function atualizarEstadoComIntervalo(dadosIntervalo) {
        if (!window.appState) return;

        console.log('📝 Atualizando estado com intervalo:', dadosIntervalo);

        // ✅ USAR API DISPONÍVEL
        if (typeof window.appState.atualizarEstado === 'function') {
            window.appState.atualizarEstado({
                dadosPrimeiroApontamento: dadosIntervalo.primeiro,
                dadosUltimoApontamento: dadosIntervalo.ultimo,
                ultimaBusca: dadosIntervalo.timestamp || new Date().toISOString()
            });
        }
        // ✅ USAR MÉTODOS INDIVIDUAIS SE EXISTIREM
        else {
            if (typeof window.appState.setDadosPrimeiroApontamento === 'function') {
                window.appState.setDadosPrimeiroApontamento(dadosIntervalo.primeiro);
            }
            if (typeof window.appState.setDadosUltimoApontamento === 'function') {
                window.appState.setDadosUltimoApontamento(dadosIntervalo.ultimo);
            }
            if (typeof window.appState.setUltimaBusca === 'function') {
                window.appState.setUltimaBusca(dadosIntervalo.timestamp || new Date().toISOString());
            }
        }

        console.log('✅ Estado atualizado com intervalo');
    }

    function popularCamposIntervalo(dados) {
        console.log('🔥 Popular campos de intervalo com:', dados);

        // ✅ SOLUÇÃO: Usar dados do parâmetro, não do appState
        var primeiro = dados.primeiro || {};
        var ultimo = dados.ultimo || {};

        // Elementos DOM
        var anoMesInicial = document.getElementById('anoMesInicial');
        var aponInicial = document.getElementById('aponInicial');
        var anoMesFinal = document.getElementById('anoMesFinal');
        var aponFinal = document.getElementById('aponFinal');

        // 🔥 FUNÇÃO: Formatar para display humano (sem zeros à esquerda)
        function formatarDisplayHumano(valor) {
            if (!valor) return '';

            // Remover zeros à esquerda
            var numero = parseInt(valor);
            if (isNaN(numero)) return valor;

            return String(numero);
        }

        // Preencher campos COM OS DADOS DO PARÂMETRO
        if (primeiro && anoMesInicial) {
            // Tentar diferentes propriedades possíveis
            var anoMes = primeiro.numapo1 || primeiro.anoMes || '';
            anoMesInicial.value = anoMes;
            console.log('✅ Preenchido anoMesInicial:', anoMes);
        }

        if (primeiro && aponInicial) {
            var apon = primeiro.numapo2 || primeiro.apontamento || primeiro.numero || '';
            // 🔥 FORMATAR PARA DISPLAY HUMANO (sem zeros)
            aponInicial.value = formatarDisplayHumano(apon);
            console.log('✅ Preenchido aponInicial (display humano):', aponInicial.value);
        }

        if (ultimo && anoMesFinal) {
            var anoMesF = ultimo.numapo1 || ultimo.anoMes || '';
            anoMesFinal.value = anoMesF;
            console.log('✅ Preenchido anoMesFinal:', anoMesF);
        }

        if (ultimo && aponFinal) {
            var aponF = ultimo.numapo2 || ultimo.apontamento || ultimo.numero || '';
            // 🔥 FORMATAR PARA DISPLAY HUMANO (sem zeros)
            aponFinal.value = formatarDisplayHumano(aponF);
            console.log('✅ Preenchido aponFinal (display humano):', aponFinal.value);
        }

        // DEBUG: Mostrar estrutura completa dos dados
        console.log('🔍 Estrutura completa dos dados:', {
            dadosCompletos: dados,
            primeiro: primeiro,
            ultimo: ultimo,
            primeiroKeys: primeiro ? Object.keys(primeiro) : [],
            ultimoKeys: ultimo ? Object.keys(ultimo) : []
        });

        // Verificar e mostrar campos preenchidos
        verificarCamposIntervaloPreenchidos();

        // Log dos valores finais
        console.log('🔥 VALORES FINAIS NOS CAMPOS:', {
            anoMesInicial: anoMesInicial ? anoMesInicial.value : 'N/A',
            aponInicial: aponInicial ? aponInicial.value : 'N/A',
            anoMesFinal: anoMesFinal ? anoMesFinal.value : 'N/A',
            aponFinal: aponFinal ? aponFinal.value : 'N/A'
        });

        // Notificar button controller
        setTimeout(function () {
            if (window.buttonController && window.buttonController.verificarEstadoBotoes) {
                window.buttonController.verificarEstadoBotoes();
            }
        }, 100);
    }

    function verificarCamposIntervaloPreenchidos() {
        var valores = obterValoresIntervalo();
        var preenchido = valores.anoMesInicial || valores.aponInicial ||
            valores.anoMesFinal || valores.aponFinal;

        // Mostrar/ocultar seção de opção selecionada
        var opcaoSelecionadaSection = document.getElementById('opcaoSelecionadaSection');
        if (opcaoSelecionadaSection) {
            opcaoSelecionadaSection.style.display = preenchido ? 'block' : 'none';
            console.log(preenchido ?
                '✅ Seção de opção selecionada exibida (campos preenchidos)' :
                'ℹ️ Seção de opção selecionada oculta (campos vazios)');
        }

        return preenchido;
    }

    // ============================================
    // GESTÃO DE CAMPOS
    // ============================================

    function limparCamposIntervalo() {
        console.log('🧹 Limpando campos de intervalo...');

        var campos = ['anoMesInicial', 'aponInicial', 'anoMesFinal', 'aponFinal'];

        for (var i = 0; i < campos.length; i++) {
            var campo = document.getElementById(campos[i]);
            if (campo) {
                campo.value = '';
                campo.placeholder = '';
                campo.classList.remove('campo-invalido');
                campo.title = '';
            }
        }

        // Limpar estado interno
        if (window.appState) {
            window.appState.setDadosPrimeiroApontamento(null);
            window.appState.setDadosUltimoApontamento(null);
            window.appState.setUltimaBusca(null);
        }

        // Limpar cache
        cacheBusca.dados = null;
        cacheBusca.chave = null;

        // Ocultar seção de opção selecionada
        var opcaoSelecionadaSection = document.getElementById('opcaoSelecionadaSection');
        if (opcaoSelecionadaSection) {
            opcaoSelecionadaSection.style.display = 'none';
        }

        // Notificar button controller
        setTimeout(function () {
            if (window.buttonController && window.buttonController.verificarEstadoBotoes) {
                window.buttonController.verificarEstadoBotoes();
            }
        }, 100);

        console.log('✅ Campos de intervalo limpos');
    }

    function obterValoresIntervalo() {
        return {
            anoMesInicial: document.getElementById('anoMesInicial') ?
                document.getElementById('anoMesInicial').value : '',
            aponInicial: document.getElementById('aponInicial') ?
                document.getElementById('aponInicial').value : '',
            anoMesFinal: document.getElementById('anoMesFinal') ?
                document.getElementById('anoMesFinal').value : '',
            aponFinal: document.getElementById('aponFinal') ?
                document.getElementById('aponFinal').value : ''
        };
    }

    function validarIntervalo() {
        var valores = obterValoresIntervalo();

        if (window.validationController) {
            return window.validationController.validarIntervaloCompleto(valores);
        }

        // Fallback básico
        var resultado = {
            valido: false,
            mensagem: '',
            intervalo: valores
        };

        // Verificar se campos iniciais estão preenchidos
        if (!valores.anoMesInicial && !valores.aponInicial) {
            resultado.mensagem = 'Informe o intervalo inicial';
            return resultado;
        }

        // Validar formato ano/mês (se informado)
        if (valores.anoMesInicial && !/^\d{6}$/.test(valores.anoMesInicial)) {
            resultado.mensagem = 'Ano/Mês inicial inválido. Use formato AAAAMM';
            return resultado;
        }

        // Validar apontamento (se informado)
        if (valores.aponInicial && !/^\d{1,6}$/.test(valores.aponInicial)) {
            resultado.mensagem = 'Apontamento inicial inválido';
            return resultado;
        }

        resultado.valido = true;
        resultado.mensagem = 'Intervalo válido';
        return resultado;
    }

    // ============================================
    // FUNÇÕES DE FORMATAÇÃO
    // ============================================

    function formatarCampoAnoMes(elemento) {
        if (!elemento) return;

        if (window.validationController) {
            var resultado = window.validationController.formatarEValidarAnoMes(elemento.value, elemento);
            elemento.value = resultado.valor;
        } else {
            // Fallback básico
            var valor = elemento.value.replace(/\D/g, '');
            if (valor.length > 6) {
                valor = valor.substring(0, 6);
            }
            elemento.value = valor;
        }
    }

    function formatarCampoApon(elemento) {
        if (!elemento) return;

        if (window.validationController) {
            var resultado = window.validationController.formatarEValidarApon(elemento.value, elemento);
            elemento.value = resultado.valor;
        } else {
            // Fallback básico
            var valor = elemento.value.replace(/\D/g, '');
            if (valor.length > 6) {
                valor = valor.substring(0, 6);
            }
            elemento.value = valor;
        }
    }

    // ============================================
    // BUSCA AUTOMÁTICA
    // ============================================

    function buscarIntervaloAutomatico() {
        // Esta função é chamada automaticamente quando condições são atendidas
        console.log('🤖 Busca automática de intervalo...');

        // Usar debouncing para evitar buscas excessivas
        if (timeoutDebounce) {
            clearTimeout(timeoutDebounce);
        }

        timeoutDebounce = setTimeout(function () {
            buscarIntervalo(window.app || null);
            timeoutDebounce = null;
        }, DEBOUNCE_DELAY);
    }

    function forcarNovaBusca(app) {
        console.log('🔄 Forçando nova busca (ignorar cache)...');

        // Limpar cache
        cacheBusca.dados = null;
        cacheBusca.chave = null;

        // Buscar novamente
        buscarIntervalo(app);
    }

    // ============================================
    // INDICADORES VISUAIS
    // ============================================

    function mostrarIndicadorBusca(texto) {
        var searchIndicator = document.getElementById('searchIndicator');
        if (searchIndicator) {
            if (texto) {
                searchIndicator.childNodes[0].nodeValue = texto;
            }
            searchIndicator.style.display = 'inline-block';
        }
    }

    function esconderIndicadorBusca() {
        var searchIndicator = document.getElementById('searchIndicator');
        if (searchIndicator) {
            searchIndicator.style.display = 'none';
        }
    }

    // ============================================
    // VALIDAÇÕES PARA SELAGEM
    // ============================================

    function verificarPodeSelar(app) {
        var resultado = {
            pode: false,
            mensagem: '',
            dados: null
        };

        // Usar validationController se disponível
        if (window.validationController) {
            return window.validationController.validarParaSelagem(app);
        }

        // Fallback: validação básica
        var opcaoSelecionada = window.appState ? window.appState.getOpcaoSelecionada() : null;
        if (!opcaoSelecionada) {
            resultado.mensagem = 'Selecione uma opção antes de selar';
            return resultado;
        }

        var dataAto = document.getElementById('dataAto');
        if (!dataAto || !dataAto.value) {
            resultado.mensagem = 'Informe a data do ato';
            return resultado;
        }

        var validacaoIntervalo = validarIntervalo();
        if (!validacaoIntervalo.valido) {
            resultado.mensagem = validacaoIntervalo.mensagem;
            return resultado;
        }

        var selosSolicitados = window.appState ? window.appState.getSelosSolicitados() : {};
        var totalSelos = 0;
        var tipos = ['TP1', 'TP3', 'TP4', 'TPD', 'TPI'];

        for (var i = 0; i < tipos.length; i++) {
            totalSelos += parseInt(selosSolicitados[tipos[i]] || 0);
        }

        if (totalSelos === 0) {
            resultado.mensagem = 'Solicite os selos necessários antes de selar';
            return resultado;
        }

        resultado.pode = true;
        resultado.mensagem = 'Pronto para selagem';
        resultado.dados = {
            dataAto: dataAto.value,
            opcao: opcaoSelecionada,
            intervalo: validacaoIntervalo.intervalo,
            selosSolicitados: selosSolicitados
        };

        return resultado;
    }

    function obterDadosParaSelagem(app) {
        var dados = {
            dataAto: document.getElementById('dataAto') ? document.getElementById('dataAto').value : '',
            opcao: window.appState ? window.appState.getOpcaoSelecionada() : null,
            intervalo: obterValoresIntervalo(),
            usuario: window.appState ? window.appState.getUsuario() : 'CONVIDADO',
            selosSolicitados: window.appState ? window.appState.getSelosSolicitados() : {}
        };

        return dados;
    }

    // ============================================
    // COMPATIBILIDADE COM API ANTIGA
    // ============================================

    function configurarEventosCampos(app) {
        console.warn('⚠️ configurarEventosCampos() é obsoleto - Use input-controller.js');

        // Esta função agora é responsabilidade do input-controller
        if (window.inputController && window.inputController.inicializar) {
            window.inputController.inicializar(app);
        }
    }

    // ============================================
    // API PÚBLICA - NOVA
    // ============================================

    return {
        // Inicialização
        inicializar: inicializar,

        // Busca de intervalo
        buscarIntervalo: buscarIntervalo,
        forcarNovaBusca: forcarNovaBusca,
        buscarIntervaloAutomatico: buscarIntervaloAutomatico,

        // Gestão de campos
        limparCamposIntervalo: limparCamposIntervalo,
        verificarCamposIntervaloPreenchidos: verificarCamposIntervaloPreenchidos,
        obterValoresIntervalo: obterValoresIntervalo,
        configurarEventosCampos: configurarEventosCampos, // Compatibilidade

        // Formatação
        formatarCampoAnoMes: formatarCampoAnoMes,
        formatarCampoApon: formatarCampoApon,

        // Validações
        validarIntervalo: validarIntervalo,
        verificarPodeSelar: verificarPodeSelar,

        // Dados para selagem
        obterDadosParaSelagem: obterDadosParaSelagem,

        // Busca avançada (compatibilidade)
        buscarApontamentos: function (app, filtros, callback) {
            console.warn('⚠️ buscarApontamentos() não implementado na versão migrada');
            if (typeof callback === 'function') {
                callback({ success: false, message: 'Função não implementada' });
            }
            return Promise.reject(new Error('Função não implementada'));
        },

        // Utilitários
        obterEstatisticas: function () {
            return {
                cacheValido: cacheValido(cacheBusca.chave),
                ultimaBusca: cacheBusca.timestamp ? new Date(cacheBusca.timestamp).toISOString() : null,
                timestamp: new Date().toISOString()
            };
        },

        // Para debug
        _getCache: function () { return cacheBusca; },
        _clearCache: function () {
            cacheBusca.dados = null;
            cacheBusca.chave = null;
            cacheBusca.timestamp = null;
            console.log('🧹 Cache de busca limpo');
        }
    };
})();

// Auto-inicialização
if (typeof window !== 'undefined') {
    setTimeout(function () {
        if (window.apontamentoService && window.apontamentoService.inicializar) {
            window.apontamentoService.inicializar();
        }
    }, 1000);
}

console.log('✅ apontamento-service.js MIGRADO - Usa appState e eventBus');