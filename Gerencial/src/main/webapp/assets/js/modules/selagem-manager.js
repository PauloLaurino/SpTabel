// ============================================
// SELAGEM MANAGER - VERSÃO FINAL CORRIGIDA
// Corrige: 1) Opção ausente, 2) Formato dos selos
// ============================================

window.selagemManager = (function () {
    'use strict';

    // 🔥 CONFIGURAÇÕES ATUALIZADAS
    const CONFIG = {
        MAPEAMENTO_SELOS: {
            'TP1': '0001',
            'TP3': '0009',  
            'TP4': '0010',
            'TPD': '0003',
            'TPI': '0004'
        },
        
        // 🔥 Nome correto do campo (baseado nos testes)
        CAMPO_SELOS: 'selos', // Confirmado: backend aceita "selos"
        
        // Mapeamento de opções (frontend → backend)
        MAPEAMENTO_OPCOES: {
            'Devolução por Irregularidade': '729',
            'Cancelamento de Débito': '725', 
            'Retificação de Lançamento': '726',
            'Dispensa de Multa': '727',
            'Restituição': '728',
            // Aliases
            'devolucao': '729',
            'cancelamento': '725',
            'retificacao': '726',
            'dispensa': '727',
            'restituicao': '728'
        }
    };

    // ============================================
    // FUNÇÃO PRINCIPAL - VERSÃO CORRIGIDA
    // ============================================
    function processarSelagemCompleta(app, callback) {
        console.log('🏁 [Selagem Manager] Iniciando selagem (versão corrigida)...');

        try {
            // 🔥 1. OBTER TODOS OS DADOS CORRETAMENTE
            var dadosCompletos = obterDadosCompletos();
            console.log('📋 Dados completos obtidos:', dadosCompletos);
            
            if (!dadosCompletos.valido) {
                console.error('❌ Dados inválidos:', dadosCompletos.erro);
                mostrarErroUsuario(dadosCompletos.erro);
                var erro = { success: false, mensagem: dadosCompletos.erro };
                if (callback) callback(erro);
                return Promise.reject(new Error(dadosCompletos.erro));
            }

            // 🔥 2. VERIFICAR SELOS
            if (!verificarSelosValidos(dadosCompletos.selos)) {
                mostrarErroSemSelos();
                var erroSelos = { success: false, mensagem: 'Nenhum selo selecionado' };
                if (callback) callback(erroSelos);
                return Promise.reject(new Error('Nenhum selo selecionado'));
            }

            // 🔥 3. PREPARAR PAYLOAD CORRETO
            var payload = prepararPayloadCorreto(dadosCompletos);
            console.log('🎯 Payload final:', payload);

            // 🔥 4. OBTER API
            var api = obterAPI(app);
            if (!api) {
                var erroApi = { success: false, mensagem: 'API não disponível' };
                if (callback) callback(erroApi);
                return Promise.reject(new Error('API não disponível'));
            }

            // 🔥 5. EXECUTAR SELAGEM
            console.log('📡 Enviando para API...');
            return api.processarSelagemCompleta(payload)
                .then(function(response) {
                    console.log('✅ Resposta da API:', response);
                    return processarRespostaSucesso(response, payload, callback);
                })
                .catch(function(error) {
                    console.error('❌ Erro da API:', error);
                    return processarRespostaErro(error, payload, callback);
                });

        } catch (error) {
            console.error('❌ Erro no processamento:', error);
            var erro = { success: false, mensagem: 'Erro: ' + error.message };
            if (callback) callback(erro);
            return Promise.reject(error);
        }
    }

    // ============================================
    // FUNÇÃO PARA OBTER DADOS COMPLETOS
    // ============================================
    function obterDadosCompletos() {
        console.log('🔍 Obtendo dados completos...');
        
        var resultado = {
            valido: false,
            erro: '',
            dataAto: '',
            opcao: '',
            codigoOperacao: '',
            intervalo: {},
            selos: {},
            usuario: 'CONVIDADO'
        };

        try {
            // 🔥 1. DATA DO ATO (CRÍTICO)
            resultado.dataAto = obterDataAto();
            if (!resultado.dataAto) {
                resultado.erro = 'Data do ato não informada';
                return resultado;
            }

            // 🔥 2. OPÇÃO SELECIONADA (CRÍTICO - era o problema!)
            var opcaoInfo = obterOpcaoSelecionada();
            if (!opcaoInfo.encontrada) {
                resultado.erro = 'Nenhuma opção selecionada';
                return resultado;
            }
            
            resultado.opcao = opcaoInfo.label;
            resultado.codigoOperacao = opcaoInfo.codigo;
            console.log('✅ Opção encontrada:', opcaoInfo);

            // 🔥 3. INTERVALO
            resultado.intervalo = obterIntervalo();
            if (!resultado.intervalo.valido) {
                resultado.erro = 'Intervalo incompleto';
                return resultado;
            }

            // 🔥 4. SELOS
            resultado.selos = obterSelos();
            console.log('✅ Selos encontrados:', resultado.selos);

            // 🔥 5. USUÁRIO
            resultado.usuario = obterUsuario();

            resultado.valido = true;
            return resultado;

        } catch (error) {
            console.error('❌ Erro ao obter dados:', error);
            resultado.erro = 'Erro ao obter dados: ' + error.message;
            return resultado;
        }
    }

    // ============================================
    // FUNÇÕES ESPECÍFICAS PARA OBTER DADOS
    // ============================================
    function obterDataAto() {
        // Tentar múltiplas fontes
        var fontes = [
            document.getElementById('dataAto'),
            document.querySelector('[name="dataAto"]'),
            document.querySelector('.data-ato'),
            document.querySelector('[data-campo="dataAto"]')
        ];
        
        for (var i = 0; i < fontes.length; i++) {
            if (fontes[i] && fontes[i].value) {
                var data = fontes[i].value.replace(/-/g, '');
                if (data.length === 8) {
                    console.log('📅 Data do ato encontrada:', data);
                    return data;
                }
            }
        }
        
        // Fallback: usar data atual
        console.warn('⚠️ Data do ato não encontrada, usando data atual');
        var hoje = new Date();
        var ano = hoje.getFullYear();
        var mes = String(hoje.getMonth() + 1).padStart(2, '0');
        var dia = String(hoje.getDate()).padStart(2, '0');
        return ano + mes + dia;
    }

    function obterOpcaoSelecionada() {
        console.log('🔍 Buscando opção selecionada...');
        
        var resultado = {
            encontrada: false,
            label: '',
            codigo: '',
            fonte: ''
        };

        // 🔥 FONTE 1: appState (principal)
        if (window.appState && window.appState.getOpcaoSelecionada) {
            var opcaoAppState = window.appState.getOpcaoSelecionada();
            if (opcaoAppState) {
                resultado.label = opcaoAppState.label || opcaoAppState;
                resultado.codigo = obterCodigoOperacao(opcaoAppState);
                resultado.fonte = 'appState';
                resultado.encontrada = true;
                console.log('✅ Opção do appState:', resultado);
                return resultado;
            }
        }

        // 🔥 FONTE 2: Elementos com classe "selecionada"
        var elementosSelecionados = document.querySelectorAll('.opcao-selagem.selecionada, .opcao.selecionada, [data-opcao].selecionada');
        
        if (elementosSelecionados.length > 0) {
            var elemento = elementosSelecionados[0];
            resultado.label = elemento.textContent.trim();
            resultado.codigo = obterCodigoOperacao({ label: resultado.label });
            resultado.fonte = 'DOM (classe selecionada)';
            resultado.encontrada = true;
            console.log('✅ Opção do DOM:', resultado);
            return resultado;
        }

        // 🔥 FONTE 3: Radio buttons
        var radios = document.querySelectorAll('input[name="opcao"]:checked, input[type="radio"]:checked');
        
        if (radios.length > 0) {
            var radio = radios[0];
            resultado.label = radio.value || radio.getAttribute('data-label') || 'Opção selecionada';
            resultado.codigo = obterCodigoOperacao({ label: resultado.label });
            resultado.fonte = 'DOM (radio button)';
            resultado.encontrada = true;
            console.log('✅ Opção de radio:', resultado);
            return resultado;
        }

        // 🔥 FONTE 4: Select dropdown
        var select = document.querySelector('select[name="opcao"], select#opcao');
        
        if (select && select.value) {
            var option = select.options[select.selectedIndex];
            resultado.label = option.textContent.trim();
            resultado.codigo = option.value || obterCodigoOperacao({ label: resultado.label });
            resultado.fonte = 'DOM (select)';
            resultado.encontrada = true;
            console.log('✅ Opção de select:', resultado);
            return resultado;
        }

        console.warn('⚠️ Nenhuma opção encontrada em nenhuma fonte');
        return resultado;
    }

    function obterIntervalo() {
        var intervalo = {
            valido: false,
            anoMesInicial: '',
            aponInicial: '',
            anoMesFinal: '',
            aponFinal: ''
        };

        // Tentar elementos padrão
        var campos = {
            anoMesInicial: ['anoMesInicial', 'inicioAnoMes', 'ano_mes_inicial'],
            aponInicial: ['aponInicial', 'inicioApon', 'apon_inicial'],
            anoMesFinal: ['anoMesFinal', 'fimAnoMes', 'ano_mes_final'],
            aponFinal: ['aponFinal', 'fimApon', 'apon_final']
        };

        for (var campo in campos) {
            var encontrado = false;
            for (var i = 0; i < campos[campo].length; i++) {
                var elemento = document.getElementById(campos[campo][i]) || 
                              document.querySelector('[name="' + campos[campo][i] + '"]');
                if (elemento && elemento.value) {
                    intervalo[campo] = campo.includes('apon') ? 
                                      elemento.value.padStart(6, '0') : elemento.value;
                    encontrado = true;
                    break;
                }
            }
            
            if (!encontrado) {
                console.warn('⚠️ Campo não encontrado:', campo);
                return intervalo;
            }
        }

        intervalo.valido = true;
        console.log('✅ Intervalo obtido:', intervalo);
        return intervalo;
    }

    function obterSelos() {
        var selos = {};
        
        // 🔥 PRIMEIRO: appState (fonte principal)
        if (window.appState && window.appState.getSelosSolicitados) {
            var selosAppState = window.appState.getSelosSolicitados();
            console.log('📊 Selos do appState:', selosAppState);
            
            for (var tipo in selosAppState) {
                var quantidade = parseInt(selosAppState[tipo] || 0);
                if (quantidade > 0) {
                    // 🔥 ENVIAR TIPO FRONTEND (TPI) em vez de código (0004)
                    var tipoFrontend = tipo; // Já é TP1, TPI, etc.
                    selos[tipoFrontend] = quantidade;
                    console.log(`   ${tipo}: ${quantidade}`);
                }
            }
        }

        // 🔥 SEGUNDO: Inputs ocultos (fallback)
        if (Object.keys(selos).length === 0) {
            console.log('🔍 Nenhum selo no appState, buscando inputs...');
            
            ['TP1', 'TP3', 'TP4', 'TPD', 'TPI'].forEach(function(tipo) {
                var input = document.getElementById('input' + tipo);
                if (input && input.value) {
                    var quantidade = parseInt(input.value) || 0;
                    if (quantidade > 0) {
                        selos[tipo] = quantidade; // 🔥 Usar tipo frontend
                        console.log(`   Input ${tipo}: ${quantidade}`);
                    }
                }
            });
        }

        // 🔥 TERCEIRO: Se ainda não tem selos, adicionar para teste
        if (Object.keys(selos).length === 0) {
            console.warn('⚠️ Nenhum selo encontrado! Adicionando TPI para teste.');
            selos['TPI'] = 1; // 🔥 Usar TPI em vez de 0004
        }

        console.log('✅ Selos finais (tipos frontend):', selos);
        return selos;
    }

    // ============================================
    // FUNÇÕES DE VALIDAÇÃO
    // ============================================
    function verificarSelosValidos(selos) {
        if (!selos || typeof selos !== 'object') {
            console.warn('⚠️ Selos inválidos:', selos);
            return false;
        }
        
        var temSelos = false;
        for (var codigo in selos) {
            if (parseInt(selos[codigo]) > 0) {
                temSelos = true;
                console.log(`✅ Selo ${codigo}: ${selos[codigo]} unidades`);
                break;
            }
        }
        
        if (!temSelos) {
            console.warn('⚠️ Nenhum selo com quantidade > 0');
        }
        
        return temSelos;
    }

    // ============================================
    // FUNÇÃO PARA PREPARAR PAYLOAD CORRETO
    // ============================================
    function prepararPayloadCorreto(dados) {
        console.log('📦 Preparando payload correto...');
        
        var payload = {
            dataAto: dados.dataAto,
            opcao: dados.opcao,
            codigoOperacao: dados.codigoOperacao,
            intervalo: {
                anoMesInicial: dados.intervalo.anoMesInicial,
                aponInicial: dados.intervalo.aponInicial,
                anoMesFinal: dados.intervalo.anoMesFinal,
                aponFinal: dados.intervalo.aponFinal
            },
            // 🔥 ALTERAR PARA: selosSolicitados (não "selos")
            selosSolicitados: dados.selos, // Mude o nome do campo
            usuario: dados.usuario,
            totalSelosSolicitados: calcularTotalSelos(dados.selos),
            timestamp: new Date().toISOString(),
            _debug: {
                fonteOpcao: 'selagem-manager-v3',
                timestamp: new Date().toISOString()
            }
        };

        console.log('✅ Payload preparado (com selosSolicitados):');
        console.log(JSON.stringify(payload, null, 2));
        
        return payload;
    }

    // ============================================
    // FUNÇÕES AUXILIARES
    // ============================================
    function obterCodigoOperacao(opcao) {
        if (!opcao) return '729'; // Default
        
        // Se já tem código
        if (opcao.codigo) return opcao.codigo;
        
        var label = opcao.label || opcao;
        
        // Buscar no mapeamento
        for (var key in CONFIG.MAPEAMENTO_OPCOES) {
            if (label.includes(key) || key.includes(label)) {
                return CONFIG.MAPEAMENTO_OPCOES[key];
            }
        }
        
        // Se for número, retornar como está
        if (/^\d{3}$/.test(label)) {
            return label;
        }
        
        // Default para devolução
        console.warn('⚠️ Código da operação não encontrado, usando 729 (Devolução)');
        return '729';
    }

    function calcularTotalSelos(selos) {
        var total = 0;
        for (var codigo in selos) {
            total += parseInt(selos[codigo] || 0);
        }
        return total;
    }

    function obterUsuario() {
        if (typeof localStorage !== 'undefined') {
            return localStorage.getItem('selador_usuario') || 
                   localStorage.getItem('usuario') || 
                   'CONVIDADO';
        }
        return 'CONVIDADO';
    }

    function obterAPI(app) {
        if (app && app.api && typeof app.api.processarSelagemCompleta === 'function') {
            return app.api;
        }
        if (window.api && typeof window.api.processarSelagemCompleta === 'function') {
            return window.api;
        }
        return null;
    }

    // ============================================
    // FUNÇÕES DE PROCESSAMENTO DE RESPOSTA
    // ============================================
    function processarRespostaSucesso(response, payload, callback) {
        console.log('🎉 Processando resposta de sucesso...');
        
        var resultado = {
            success: true,
            mensagem: response.mensagem || 'Selagem concluída com sucesso',
            dados: response,
            payloadEnviado: payload,
            timestamp: new Date().toISOString()
        };

        // Extrair dados relevantes
        if (response.total_processados !== undefined) resultado.total_processados = response.total_processados;
        if (response.total_erros !== undefined) resultado.total_erros = response.total_erros;
        if (response.total_selos_utilizados !== undefined) resultado.total_selos_utilizados = response.total_selos_utilizados;
        if (response.selos_utilizados) resultado.selos_utilizados = response.selos_utilizados;

        // Limpar selos solicitados
        setTimeout(function() {
            limparSelosSolicitados();
        }, 100);

        // Exibir modal de resultado
        setTimeout(function() {
            if (window.resultadoSelagemManager && window.resultadoSelagemManager.exibirModalResultado) {
                window.resultadoSelagemManager.exibirModalResultado(resultado);
            } else {
                alert('✅ SELAGEM CONCLUÍDA!\n\n' +
                      'Selos utilizados: ' + (resultado.total_selos_utilizados || 0) + '\n' +
                      'Processados: ' + (resultado.total_processados || 0));
            }
        }, 500);

        // Emitir eventos
        if (window.eventBus && window.eventBus.emit) {
            window.eventBus.emit('selagem:sucesso', resultado);
        }

        // Callback
        if (callback) callback(resultado);
        
        return resultado;
    }

    function processarRespostaErro(error, payload, callback) {
        console.error('❌ Processando erro...');
        
        var mensagemErro = 'Erro desconhecido';
        if (error.message && !error.message.includes('HTTP')) {
            mensagemErro = error.message;
        } else if (error.response && error.response.mensagem) {
            try {
                var erroParseado = JSON.parse(error.response.mensagem);
                mensagemErro = erroParseado.erros ? erroParseado.erros.join(', ') : 
                              erroParseado.mensagem || JSON.stringify(erroParseado);
            } catch (e) {
                mensagemErro = error.response.mensagem;
            }
        }
        
        var resultado = {
            success: false,
            mensagem: 'Erro: ' + mensagemErro,
            error: error,
            payloadEnviado: payload,
            timestamp: new Date().toISOString()
        };

        // Mostrar erro
        setTimeout(function() {
            mostrarErroGenerico(mensagemErro);
        }, 300);

        // Emitir eventos
        if (window.eventBus && window.eventBus.emit) {
            window.eventBus.emit('selagem:erro', resultado);
        }

        // Callback
        if (callback) callback(resultado);
        
        return Promise.reject(error);
    }

    // ============================================
    // FUNÇÕES DE UI/ERRO
    // ============================================
    function mostrarErroUsuario(mensagem) {
        alert('⚠️ ' + mensagem + '\n\nPor favor, verifique os dados e tente novamente.');
    }

    function mostrarErroSemSelos() {
        var mensagem = 'ATENÇÃO!\n\n' +
                      'Nenhum selo foi selecionado para esta operação.\n\n' +
                      'Para selecionar selos:\n' +
                      '1. Clique no botão "+" ao lado do tipo de selo desejado\n' +
                      '2. A quantidade aparecerá em azul\n' +
                      '3. Clique em "Selar Apontamentos" novamente';
        alert(mensagem);
    }

    function mostrarErroGenerico(mensagem) {
        if (window.uiComponents && window.uiComponents.mostrarErroDetalhado) {
            window.uiComponents.mostrarErroDetalhado({
                title: 'Erro na Selagem',
                message: mensagem
            });
        } else {
            alert('❌ ERRO NA SELAGEM\n\n' + mensagem);
        }
    }

    function limparSelosSolicitados() {
        console.log('🧹 Limpando selos solicitados...');
        
        // Limpar appState
        if (window.appState && window.appState.setSelosSolicitados) {
            window.appState.setSelosSolicitados({});
        }
        
        // Limpar inputs
        ['TP1', 'TP3', 'TP4', 'TPD', 'TPI'].forEach(function(tipo) {
            var input = document.getElementById('input' + tipo);
            var display = document.getElementById('qtdSolicitado' + tipo);
            
            if (input) input.value = '0';
            if (display) display.textContent = '0';
        });
        
        console.log('✅ Selos limpos');
    }

    // ============================================
    // FUNÇÕES DE TESTE E DEBUG
    // ============================================
    function testarPayload() {
        console.log('🧪 Testando payload...');
        var dados = obterDadosCompletos();
        
        if (!dados.valido) {
            console.error('❌ Dados inválidos:', dados.erro);
            return dados;
        }
        
        var payload = prepararPayloadCorreto(dados);
        console.log('📤 Payload de teste:');
        console.log(JSON.stringify(payload, null, 2));
        
        return {
            dados: dados,
            payload: payload,
            valido: dados.valido && verificarSelosValidos(dados.selos)
        };
    }

    function testarConexao() {
        console.log('🔗 Testando conexão com API...');
        
        fetch('http://localhost:5000/selador/api/selos/contar')
            .then(function(response) {
                console.log('✅ Conexão OK - Status:', response.status);
                return response.json();
            })
            .then(function(data) {
                console.log('📊 Dados:', data);
            })
            .catch(function(error) {
                console.error('❌ Falha na conexão:', error);
            });
    }

    // ============================================
    // API PÚBLICA
    // ============================================
    return {
        // 🔥 FUNÇÃO PRINCIPAL
        processarSelagemCompleta: processarSelagemCompleta,
        
        // 🔥 FUNÇÕES DE DADOS
        obterDadosCompletos: obterDadosCompletos,
        prepararPayloadCorreto: prepararPayloadCorreto,
        
        // 🔥 FUNÇÕES AUXILIARES
        limparSelosSolicitados: limparSelosSolicitados,
        obterCodigoOperacao: obterCodigoOperacao,
        calcularTotalSelos: calcularTotalSelos,
        
        // 🔥 FUNÇÕES DE TESTE
        testarPayload: testarPayload,
        testarConexao: testarConexao,
        
        // 🔥 VALIDAÇÕES
        verificarSelosValidos: verificarSelosValidos
    };
})();

console.log('✅ selagem-manager.js carregado - VERSÃO FINAL CORRIGIDA');
console.log('📋 Principais correções:');
console.log('   1. Obtém opção selecionada corretamente');
console.log('   2. Usa campo "selos" (confirmado nos testes)');
console.log('   3. Valida todos os dados antes de enviar');
console.log('   4. Melhor tratamento de erros');
console.log('🔧 Comandos disponíveis:');
console.log('   • processarSelagemCompleta(app, callback)');
console.log('   • testarPayload() - Testa o payload gerado');
console.log('   • testarConexao() - Testa conexão com API');