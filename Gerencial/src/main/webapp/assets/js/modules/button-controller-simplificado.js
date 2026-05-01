// ============================================
// BUTTON CONTROLLER - VERSÃO SIMPLIFICADA E FUNCIONAL
// ============================================

window.buttonControllerSimplificado = (function() {
    'use strict';
    
    var estado = {
        verificarAtivo: false,
        intervaloTimer: null,
        opcaoAtual: null
    };
    
    // ============================================
    // INICIALIZAÇÃO
    // ============================================
    
    function inicializar() {
        console.log('🎛️ Button Controller Simplificado inicializando...');
        
        // Configurar listeners dos inputs
        configurarListenersInputs();
        
        // Configurar eventos dos botões
        configurarEventosBotoes();
        
        // Configurar Event Bus
        configurarEventBus();
        
        // Iniciar verificação periódica
        iniciarVerificacaoPeriodica();
        
        // Verificar estado inicial
        verificarEstadoBotoes();
        
        console.log('✅ Button Controller Simplificado pronto');
        return true;
    }
    
    // ============================================
    // CONFIGURAÇÃO DE LISTENERS
    // ============================================
    
    function configurarListenersInputs() {
        console.log('🔧 Configurando listeners dos campos...');
        
        // IDs dos campos de intervalo
        var camposIntervalo = [
            'anoMesInicial',
            'aponInicial', 
            'anoMesFinal',
            'aponFinal'
        ];
        
        // Adicionar listener para cada campo
        camposIntervalo.forEach(function(id) {
            var campo = document.getElementById(id);
            if (campo) {
                campo.addEventListener('input', function() {
                    setTimeout(verificarEstadoBotoes, 100);
                });
                
                campo.addEventListener('change', function() {
                    setTimeout(verificarEstadoBotoes, 150);
                });
            }
        });
        
        // Listener para selos solicitados (inputs ocultos)
        var tiposSelos = ['TP1', 'TP3', 'TP4', 'TPD', 'TPI'];
        tiposSelos.forEach(function(tipo) {
            var inputId = 'input' + tipo;
            var input = document.getElementById(inputId);
            
            if (input) {
                input.addEventListener('input', function() {
                    setTimeout(verificarEstadoBotoes, 100);
                });
            }
        });
        
        console.log('👂 Listeners configurados');
    }
    
    // ============================================
    // CONFIGURAÇÃO DE EVENT BUS
    // ============================================
    
    function configurarEventBus() {
        if (!window.eventBus) {
            console.warn('⚠️ Event Bus não disponível para button controller');
            return;
        }
        
        var EB = window.eventBus.EVENTOS;
        
        if (!EB) {
            console.warn('⚠️ EVENTOS não definido no Event Bus');
            return;
        }
        
        // Listener para mudança de opção
        window.eventBus.on(EB.OPCAO_SELECIONADA, function(opcao) {
            console.log('🔄 Button Controller: Opção alterada detectada');
            estado.opcaoAtual = opcao;
            
            // Forçar verificação dos botões após mudança de opção
            setTimeout(function() {
                verificarEstadoBotoes();
            }, 50);
        });
        
        // Listener para cálculo concluído
        window.eventBus.on(EB.CALCULO_CONCLUIDO, function(resultado) {
            setTimeout(function() {
                verificarEstadoBotoes();
            }, 100);
        });
        
        // Listener para selagem concluída
        window.eventBus.on(EB.SELAGEM_CONCLUIDA, function(resultado) {
            setTimeout(function() {
                verificarEstadoBotoes();
            }, 100);
        });
        
        console.log('🎧 Event Bus configurado para Button Controller');
    }
    
    // ============================================
    // VERIFICAÇÃO DOS ESTADOS DOS BOTÕES
    // ============================================
    
    function verificarEstadoBotoes() {
        if (estado.verificarAtivo) return;
        estado.verificarAtivo = true;
        
        // Verificar botão SOLICITAR SELOS
        verificarBotaoSolicitar();
        
        // Verificar botão SELAR ATOS  
        verificarBotaoSelar();
        
        estado.verificarAtivo = false;
    }
    
    function verificarBotaoSolicitar() {
        var botao = document.getElementById('btnSolicitarSelos');
        if (!botao) return;
        
        // Verificar se os 4 campos de intervalo estão preenchidos
        var camposPreenchidos = verificarCamposIntervaloPreenchidos();
        
        // Atualizar aparência do botão
        atualizarAparenciaBotao(botao, camposPreenchidos, 'solicitar');
        
        console.log('📊 Estado botão Solicitar:', {
            habilitado: camposPreenchidos,
            campos: obterValoresCamposIntervalo()
        });
    }
    
    function verificarBotaoSelar() {
        var botao = document.getElementById('btnSelarApontamentos');
        if (!botao) return;
        
        var temSelosSolicitados = verificarSelosSolicitados();
        var totalSelos = calcularTotalSelosSolicitados();
        
        // ✅ CONTROLAR VISIBILIDADE E ESTADO
        if (temSelosSolicitados && totalSelos > 0) {
            // MOSTRAR e HABILITAR
            botao.classList.add('visivel', 'btn-roxo');
            botao.classList.remove('btn-cinza', 'desaparecendo');
            botao.classList.add('aparecendo');
            botao.disabled = false;
            botao.title = 'Clique para selar os apontamentos';
        } else {
            // OCULTAR e DESABILITAR
            botao.classList.remove('visivel', 'btn-roxo', 'aparecendo');
            botao.classList.add('btn-cinza', 'desaparecendo');
            botao.disabled = true;
            botao.title = 'Solicite selos primeiro para habilitar';
            
            // Após animação, remover completamente
            setTimeout(function() {
                if (!temSelosSolicitados) {
                    botao.classList.remove('visivel');
                }
            }, 300);
        }
        
        console.log('📊 Botão Selar:', {
            visivel: temSelosSolicitados,
            habilitado: temSelosSolicitados,
            totalSelos: totalSelos,
            classes: botao.className
        });
    }
    
    // ============================================
    // FUNÇÕES DE VERIFICAÇÃO
    // ============================================
    
    function verificarCamposIntervaloPreenchidos() {
        var campos = [
            document.getElementById('anoMesInicial'),
            document.getElementById('aponInicial'),
            document.getElementById('anoMesFinal'),
            document.getElementById('aponFinal')
        ];
        
        // Verificar se todos existem e têm valor
        for (var i = 0; i < campos.length; i++) {
            if (!campos[i] || !campos[i].value || campos[i].value.trim() === '') {
                return false;
            }
        }
        
        return true;
    }
    
    function obterValoresCamposIntervalo() {
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
    
    function verificarSelosSolicitados() {
        var total = calcularTotalSelosSolicitados();
        return total > 0;
    }
    
    function calcularTotalSelosSolicitados() {
        var tipos = ['TP1', 'TP3', 'TP4', 'TPD', 'TPI'];
        var total = 0;
        
        tipos.forEach(function(tipo) {
            var input = document.getElementById('input' + tipo);
            if (input && input.value) {
                var valor = parseInt(input.value) || 0;
                total += valor;
            }
        });
        
        return total;
    }
    
    // ============================================
    // ATUALIZAÇÃO DE APARÊNCIA
    // ============================================
    
    function atualizarAparenciaBotao(botao, habilitado, tipo) {
        if (!botao) return;
        
        // Limpar classes anteriores
        botao.classList.remove('btn-roxo', 'btn-cinza');
        
        if (habilitado) {
            // BOTÃO HABILITADO (ROXO)
            botao.classList.add('btn-roxo');
            botao.disabled = false;
            botao.style.opacity = '1';
            botao.style.cursor = 'pointer';
            
            if (tipo === 'solicitar') {
                botao.title = 'Clique para calcular os selos necessários';
            } else {
                botao.title = 'Clique para selar os apontamentos';
            }
        } else {
            // BOTÃO DESABILITADO (CINZA)
            botao.classList.add('btn-cinza');
            botao.disabled = true;
            botao.style.opacity = '0.7';
            botao.style.cursor = 'not-allowed';
            
            if (tipo === 'solicitar') {
                botao.title = 'Preencha todos os campos de intervalo para habilitar';
            } else {
                botao.title = 'Solicite selos primeiro para habilitar';
            }
        }
    }
    
    // ============================================
    // CONFIGURAÇÃO DE EVENTOS DOS BOTÕES
    // ============================================
    
    function configurarEventosBotoes() {
        console.log('🔧 Configurando eventos dos botões...');
        
        // Botão Solicitar Selos
        var btnSolicitar = document.getElementById('btnSolicitarSelos');
        if (btnSolicitar) {
            // Remover eventos antigos
            var novoBtn = btnSolicitar.cloneNode(true);
            btnSolicitar.parentNode.replaceChild(novoBtn, btnSolicitar);
            btnSolicitar = novoBtn;
            
            // Adicionar novo evento
            btnSolicitar.addEventListener('click', function(e) {
                e.preventDefault();
                
                if (this.disabled) {
                    console.log('⛔ Botão Solicitar desabilitado - clique ignorado');
                    return;
                }
                
                console.log('🛒 Botão Solicitar Selos clicado');
                executarSolicitacaoSelos();
            });
        }
        
        // Botão Selar Atos
        var btnSelar = document.getElementById('btnSelarApontamentos');
        if (btnSelar) {
            // Remover eventos antigos
            var novoBtn = btnSelar.cloneNode(true);
            btnSelar.parentNode.replaceChild(novoBtn, btnSelar);
            btnSelar = novoBtn;
            
            // Adicionar novo evento
            btnSelar.addEventListener('click', function(e) {
                e.preventDefault();
                
                if (this.disabled) {
                    console.log('⛔ Botão Selar desabilitado - clique ignorado');
                    return;
                }
                
                console.log('🏁 Botão Selar Atos clicado');
                executarSelagem();
            });
        }
        
        console.log('✅ Eventos dos botões configurados');
    }
    
    // ============================================
    // EXECUÇÃO DAS AÇÕES
    // ============================================
    
    function executarSolicitacaoSelos() {
        console.log('🧮 Iniciando solicitação de selos...');
        
        // Usar o módulo existente se disponível
        if (window.selosManager && window.selosManager.calcularSelosNecessarios) {
            // Obter app básico
            var app = window.app || { elementos: {} };
            
            // Mostrar loading no botão
            mostrarLoadingBotao('btnSolicitarSelos', 'solicitando');
            
            // Executar cálculo
            window.selosManager.calcularSelosNecessarios(app, function(resultado) {
                console.log('📊 Resultado cálculo:', resultado);
                
                // Esconder loading
                esconderLoadingBotao('btnSolicitarSelos');
                
                // Verificar estado dos botões novamente
                setTimeout(verificarEstadoBotoes, 100);
                
                // Mostrar mensagem
                if (window.uiComponents) {
                    if (resultado.success) {
                        window.uiComponents.mostrarMensagem(
                            'Selos calculados com sucesso! Total: ' + 
                            (resultado.total || 0) + ' selos', 
                            'success'
                        );
                    } else {
                        window.uiComponents.mostrarMensagem(
                            'Erro ao calcular selos: ' + (resultado.mensagem || 'Erro desconhecido'),
                            'error'
                        );
                    }
                }
            });
        } else {
            console.error('❌ Módulo de selos não disponível');
            
            // Simulação para teste
            if (window.uiComponents) {
                window.uiComponents.mostrarMensagem(
                    'Simulação: Selos calculados com sucesso!',
                    'success'
                );
            }
        }
    }
    
    function executarSelagem() {
        console.log('🏁 Iniciando selagem...');
        
        // Usar módulo existente se disponível
        if (window.selagemManager && window.selagemManager.processarSelagemCompleta) {
            // Obter app básico
            var app = window.app || { elementos: {} };
            
            // Mostrar loading no botão
            mostrarLoadingBotao('btnSelarApontamentos', 'selando');
            
            // Executar selagem
            window.selagemManager.processarSelagemCompleta(app, function(resultado) {
                console.log('📊 Resultado selagem:', resultado);
                
                // Esconder loading
                esconderLoadingBotao('btnSelarApontamentos');
                
                // CRÍTICO: Limpar selos solicitados após selagem
                limparSelosSolicitados();
                
                // Verificar estado dos botões
                setTimeout(verificarEstadoBotoes, 100);
                
                // Mostrar mensagem
                if (window.uiComponents) {
                    if (resultado.success) {
                        window.uiComponents.mostrarMensagem(
                            'Selagem concluída com sucesso!',
                            'success'
                        );
                    } else {
                        window.uiComponents.mostrarMensagem(
                            'Erro na selagem: ' + (resultado.mensagem || 'Erro desconhecido'),
                            'error'
                        );
                    }
                }
            });
        } else {
            console.error('❌ Módulo de selagem não disponível');
            
            // Simulação para teste
            if (window.uiComponents) {
                window.uiComponents.mostrarMensagem(
                    'Simulação: Selagem concluída com sucesso!',
                    'success'
                );
                
                // Simular limpeza dos selos solicitados
                setTimeout(function() {
                    limparSelosSolicitados();
                    verificarEstadoBotoes();
                }, 500);
            }
        }
    }
    
    function limparSelosSolicitados() {
        console.log('🧹 Limpando selos solicitados...');
        
        var tipos = ['TP1', 'TP3', 'TP4', 'TPD', 'TPI'];
        
        tipos.forEach(function(tipo) {
            // Input oculto
            var input = document.getElementById('input' + tipo);
            if (input) {
                input.value = '0';
            }
            
            // Display visível
            var display = document.getElementById('qtdSolicitado' + tipo);
            if (display) {
                display.textContent = '0';
                display.classList.remove('solicitado-destaque');
            }
        });
        
        // Ocultar floating totalizer se existir
        if (window.floatingTotalizer && window.floatingTotalizer.esconder) {
            window.floatingTotalizer.esconder();
        }
        
        console.log('✅ Selos solicitados limpos');
    }
    
    // ============================================
    // LOADING DOS BOTÕES
    // ============================================
    
    function mostrarLoadingBotao(botaoId, tipo) {
        var botao = document.getElementById(botaoId);
        if (!botao) return;
        
        botao.disabled = true;
        
        var textoOriginal = botao.innerHTML;
        botao.setAttribute('data-texto-original', textoOriginal);
        
        if (tipo === 'solicitando') {
            botao.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Solicitando<span class="loading-dots"></span>';
        } else if (tipo === 'selando') {
            botao.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Selando<span class="loading-dots"></span>';
        }
        
        // Adicionar animação dos pontos se não existir
        setTimeout(function() {
            var dotsContainer = botao.querySelector('.loading-dots');
            if (dotsContainer && !dotsContainer.hasChildNodes()) {
                dotsContainer.innerHTML = '<span class="dot"></span><span class="dot"></span><span class="dot"></span>';
            }
        }, 10);
        
        console.log('🌀 Loading mostrado para:', botaoId);
    }
    
    function esconderLoadingBotao(botaoId) {
        var botao = document.getElementById(botaoId);
        if (!botao) return;
        
        var textoOriginal = botao.getAttribute('data-texto-original');
        if (textoOriginal) {
            botao.innerHTML = textoOriginal;
        }
        
        // Re-verificar estado do botão após 100ms
        setTimeout(function() {
            verificarEstadoBotoes();
        }, 100);
        
        console.log('✅ Loading escondido para:', botaoId);
    }
    
    // ============================================
    // VERIFICAÇÃO PERIÓDICA
    // ============================================
    
    function iniciarVerificacaoPeriodica() {
        // Verificar a cada 2 segundos (para garantir sincronia)
        estado.intervaloTimer = setInterval(function() {
            verificarEstadoBotoes();
        }, 2000);
        
        console.log('⏱️ Verificação periódica iniciada');
    }
    
    // ============================================
    // LIMPEZA DE EVENTOS
    // ============================================
    
    function limparEventos() {
        if (estado.intervaloTimer) {
            clearInterval(estado.intervaloTimer);
            estado.intervaloTimer = null;
        }
        
        // Remover listeners do Event Bus se existirem
        if (window.eventBus && window.eventBus.off) {
            var EB = window.eventBus.EVENTOS;
            if (EB) {
                window.eventBus.off(EB.OPCAO_SELECIONADA);
                window.eventBus.off(EB.CALCULO_CONCLUIDO);
                window.eventBus.off(EB.SELAGEM_CONCLUIDA);
            }
        }
        
        console.log('🧹 Eventos do Button Controller limpos');
    }
    
    // ============================================
    // API PÚBLICA
    // ============================================
    
    return {
        inicializar: inicializar,
        verificarEstadoBotoes: verificarEstadoBotoes,
        configurarEventosBotoes: configurarEventosBotoes,
        limparSelosSolicitados: limparSelosSolicitados,
        limparEventos: limparEventos,
        
        // Para testes/debug
        _testarCampos: function() {
            return {
                intervaloPreenchido: verificarCamposIntervaloPreenchidos(),
                selosSolicitados: verificarSelosSolicitados(),
                totalSelos: calcularTotalSelosSolicitados(),
                valoresCampos: obterValoresCamposIntervalo(),
                opcaoAtual: estado.opcaoAtual
            };
        },
        
        // Estado interno
        _getEstado: function() { return estado; }
    };
})();

// Auto-inicialização corrigida
(function() {
    'use strict';
    
    function inicializarButtonController() {
        if (window.buttonControllerSimplificado && window.buttonControllerSimplificado.inicializar) {
            try {
                console.log('🔄 Inicializando Button Controller Simplificado...');
                var resultado = window.buttonControllerSimplificado.inicializar();
                if (resultado) {
                    console.log('✅ button-controller-simplificado.js inicializado com sucesso');
                } else {
                    console.warn('⚠️ Button Controller Simplificado inicializado com erro');
                }
            } catch (error) {
                console.error('❌ Erro ao inicializar Button Controller:', error);
            }
        } else {
            console.warn('⚠️ Button Controller Simplificado não disponível, tentando novamente em 500ms');
            setTimeout(inicializarButtonController, 500);
        }
    }
    
    // Esperar um pouco mais para garantir que todos os módulos estão carregados
    setTimeout(inicializarButtonController, 1000);
})();