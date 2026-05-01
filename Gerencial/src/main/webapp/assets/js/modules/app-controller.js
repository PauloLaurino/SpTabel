// ============================================
// APP CONTROLLER - Orquestração principal da aplicação
// Versão ES5 - REFATORADA da FASE 2
// ============================================

window.appController = (function() {
    'use strict';
    
    var app = null;
    var elementos = {};
    
    // ============================================
    // INICIALIZAÇÃO
    // ============================================
    
    function inicializar(instanciaApp) {
        console.log('🎮 App Controller inicializando...');
        
        app = instanciaApp;
        elementos = app.elementos;
        
        if (!elementos || Object.keys(elementos).length === 0) {
            console.error('❌ Elementos DOM não encontrados');
            return false;
        }
        
        // Configurar Event Bus para eventos globais
        configurarEventBus();
        
        // Configurar handlers principais
        configurarHandlersEventBus();
        
        console.log('✅ App Controller inicializado');
        return true;
    }
    
    // ============================================
    // CONFIGURAÇÃO DO EVENT BUS
    // ============================================
    
    function configurarEventBus() {
        if (!window.eventBus || typeof window.eventBus.on !== 'function') {
            console.warn('⚠️ [App Controller] Event Bus não disponível');
            return;
        }
        
        if (!window.eventBus.EVENTOS) {
            console.warn('⚠️ [App Controller] EVENTOS não definidos');
            return;
        }
        
        var EB = window.eventBus.EVENTOS;
        
        // Configurar listeners com verificações
        if (typeof handleOpcaoSelecionada === 'function') {
            window.eventBus.on(EB.OPCAO_SELECIONADA, handleOpcaoSelecionada);
        }
        
        if (typeof handleIntervaloPreenchido === 'function') {
            window.eventBus.on(EB.INTERVALO_PREENCHIDO, handleIntervaloPreenchido);
        }
    }

    // ============================================
    // CONFIGURAÇÃO DE HANDLERS DO EVENT BUS
    // ============================================

    function configurarHandlersEventBus() {
        console.log('🔧 Configurando handlers do Event Bus...');
        
        if (!window.eventBus || typeof window.eventBus.on !== 'function') {
            console.warn('⚠️ Event Bus não disponível para handlers');
            return;
        }
        
        var EB = window.eventBus.EVENTOS;
        
        // 1. Handler para quando selos são carregados
        if (typeof handleSelosCarregados === 'function') {
            window.eventBus.on(EB.SELOS_CARREGADOS, function(dados) {
                console.log('✅ App Controller: Selos carregados', dados.total || 0, 'selos');
                handleSelosCarregados(dados);
            });
        }
        
        // 2. Handler para quando selos são solicitados
        if (typeof handleSelosSolicitados === 'function') {
            window.eventBus.on(EB.SELOS_SOLICITADOS, function(selos) {
                console.log('✅ App Controller: Selos solicitados atualizados');
                handleSelosSolicitados(selos);
            });
        }
        
        // 3. Handler para resultado de cálculo
        if (typeof handleResultadoCalculo === 'function') {
            window.eventBus.on(EB.CALCULO_CONCLUIDO, function(resultado) {
                console.log('✅ App Controller: Resultado cálculo recebido');
                handleResultadoCalculo(resultado);
            });
        }
        
        // 4. Handler para resultado de selagem
        if (typeof handleResultadoSelagem === 'function') {
            window.eventBus.on(EB.SELAGEM_CONCLUIDA, function(resultado) {
                console.log('✅ App Controller: Resultado selagem recebido');
                handleResultadoSelagem(resultado);
            });
        }
        
        // 5. Handler para erros
        window.eventBus.on(EB.APP_ERRO, function(erro) {
            console.error('🔴 App Controller: Erro recebido via Event Bus', erro);
            mostrarErroSistema(erro);
        });
        
        console.log('✅ Handlers do Event Bus configurados');
    }    

    // ============================================
    // FUNÇÕES HANDLER PARA EVENT BUS
    // ============================================

    function handleOpcaoSelecionada(opcao) {
        console.log('🎯 App Controller: Opção selecionada:', opcao ? opcao.label : 'N/A');
        tratarOpcaoSelecionada(opcao);
    }

    function handleIntervaloPreenchido(dados) {
        console.log('📊 App Controller: Intervalo preenchido:', dados ? 'Sim' : 'Não');
        // Pode adicionar lógica adicional aqui se necessário
    }

    function handleSelosCarregados(dados) {
        console.log('🏷️ App Controller: Selos carregados - Total:', dados.total || 0);
        
        // Atualizar UI com selos disponíveis
        if (elementos.currentStatus) {
            elementos.currentStatus.textContent = dados.total + ' selos disponíveis';
        }
    }

    function handleSelosSolicitados(selos) {
        console.log('🛒 App Controller: Selos solicitados atualizados');
        atualizarUIComSelosSolicitados(selos);
    }

    function handleResultadoCalculo(resultado) {
        console.log('🧮 App Controller: Resultado cálculo processado');
        tratarResultadoCalculo(resultado);
    }

    function handleResultadoSelagem(resultado) {
        console.log('🏁 App Controller: Resultado selagem processado');
        
        if (resultado.success) {
            // Mostrar sucesso
            if (window.uiComponents) {
                var mensagem = 'Selagem concluída com sucesso!\n' +
                            'Selados: ' + (resultado.selados || 0) + ' apontamento(s)';
                
                window.uiComponents.mostrarMensagem(mensagem, 'success');
            }
            
            // Recarregar selos disponíveis após 1 segundo
            setTimeout(function() {
                if (window.selosManager && window.selosManager.carregarSelosDisponiveis) {
                    window.selosManager.carregarSelosDisponiveis(app);
                }
            }, 1000);
        }
    }
    
    // ============================================
    // CONFIGURAÇÃO DE BOTÕES PRINCIPAIS
    // ============================================

    function configurarBotoesPrincipais() {
        console.log('🔧 Configurando botões principais (App Controller)...');
        
        // Referência aos botões principais
        var botoes = {
            btnRefresh: elementos.btnRefresh,
            btnLogout: elementos.btnLogout,
            btnHelp: elementos.btnHelp,
            btnFechar: elementos.btnFechar
        };
        
        // Configurar botão Refresh
        if (botoes.btnRefresh) {
            botoes.btnRefresh.addEventListener('click', function(e) {
                e.preventDefault();
                console.log('🔄 Botão Refresh clicado');
                atualizarDadosCompletos();
            });
        }
        
        // Configurar botão Logout
        if (botoes.btnLogout) {
            botoes.btnLogout.addEventListener('click', function(e) {
                e.preventDefault();
                console.log('👋 Botão Logout clicado');
                fazerLogout();
            });
        }
        
        // Configurar botão Help
        if (botoes.btnHelp) {
            botoes.btnHelp.addEventListener('click', function(e) {
                e.preventDefault();
                console.log('❓ Botão Help clicado');
                mostrarAjuda();
            });
        }
        
        // Configurar botão Fechar
        if (botoes.btnFechar) {
            botoes.btnFechar.addEventListener('click', function(e) {
                e.preventDefault();
                console.log('🚪 Botão Fechar clicado');
                fecharAplicacao();
            });
        }
        
        console.log('✅ Botões principais configurados');
    }

    // ============================================
    // CONFIGURAÇÃO DE HANDLERS PRINCIPAIS
    // ============================================

    function configurarHandlersPrincipais() {
        console.log('🔧 Configurando handlers principais...');
        
        // Navegação
        configurarNavegacao();
        
        // Integração Maker
        configurarIntegracaoMaker();
        
        // Configurar botões principais (se ainda responsabilidade do app-controller)
        configurarBotoesPrincipais();
        
        console.log('✅ Handlers principais configurados');
    }
    
    // ============================================
    // FUNÇÕES DE ORQUESTRAÇÃO
    // ============================================
    
    function atualizarDadosCompletos() {
        console.log('🔄 Atualizando dados completos...');
        
        // Emitir evento de loading
        if (window.eventBus) {
            window.eventBus.emit(window.eventBus.EVENTOS.LOADING_INICIADO, {
                acao: 'atualizar_dados'
            });
        }
        
        // Atualizar selos
        if (window.selosManager && window.selosManager.carregar) {
            window.selosManager.carregar(app)
                .then(function() {
                    console.log('✅ Selos atualizados');
                })
                .catch(function(erro) {
                    console.error('❌ Erro ao atualizar selos:', erro);
                });
        }
        
        // Atualizar intervalo se houver opção selecionada
        setTimeout(function() {
            if (window.appState && window.appState.getOpcaoSelecionada()) {
                if (window.apontamentoService && window.apontamentoService.forcarNovaBusca) {
                    window.apontamentoService.forcarNovaBusca(app);
                }
            }
            
            // Finalizar loading
            if (window.eventBus) {
                setTimeout(function() {
                    window.eventBus.emit(window.eventBus.EVENTOS.LOADING_FINALIZADO, {
                        acao: 'atualizar_dados'
                    });
                    
                    // Mensagem de sucesso
                    if (window.uiComponents) {
                        window.uiComponents.mostrarMensagem('Dados atualizados!', 'success');
                    }
                }, 1000);
            }
        }, 500);
    }
    
    function fazerLogout() {
        console.log('👋 Realizando logout...');
        
        // Limpar estado
        if (window.appState) {
            window.appState.setUsuario('CONVIDADO');
            window.appState.setOpcaoSelecionada(null);
            window.appState.setSelosSolicitados({});
        }
        
        // Limpar interface
        if (elementos.currentUser) {
            elementos.currentUser.textContent = 'CONVIDADO';
        }
        
        // Limpar campos
        if (window.apontamentoService && window.apontamentoService.limparCamposIntervalo) {
            window.apontamentoService.limparCamposIntervalo(app);
        }
        
        // Limpar selos solicitados
        if (window.selosManager && window.selosManager.limparSelosSolicitados) {
            window.selosManager.limparSelosSolicitados(app);
        }
        
        // Mensagem
        if (window.uiComponents) {
            window.uiComponents.mostrarMensagem('Logout realizado com sucesso', 'success');
        }
        
        console.log('✅ Logout realizado');
    }
    
    function fecharAplicacao() {
        console.log('🚪 Fechando aplicação...');
        
        // Verificar se está no modo Maker
        var isMakerMode = window.appState && window.appState.getEstado().isMakerIntegration;
        
        if (isMakerMode) {
            // Se está no Maker, usar callback
            if (window.sprFechaSelador) {
                var resultado = window.appState ? window.appState.getEstado().resultadoSelagem || {} : {};
                window.sprFechaSelador(resultado);
            } else if (window.parent && window.parent !== window) {
                // Tentar fechar via postMessage
                window.parent.postMessage({ 
                    type: 'selador:fechar',
                    action: 'close'
                }, '*');
            }
        } else {
            // Standalone - confirmar
            if (window.uiComponents && window.uiComponents.mostrarConfirmacao) {
                window.uiComponents.mostrarConfirmacao(
                    'Deseja realmente fechar o Sistema Selador?',
                    function() {
                        window.close();
                    },
                    function() {
                        console.log('❌ Fechar cancelado');
                    }
                );
            } else {
                if (confirm('Deseja realmente fechar o Sistema Selador?')) {
                    window.close();
                }
            }
        }
    }
    
    function mostrarAjuda() {
        var mensagem = 'Sistema Selador v1.26.1\n\n' +
                      '📋 Funcionalidades:\n' +
                      '• Selecione uma opção de selagem\n' +
                      '• Informe a data do ato\n' +
                      '• O sistema buscará o intervalo automaticamente\n' +
                      '• Solicite os selos necessários\n' +
                      '• Execute a selagem\n\n' +
                      '🛠️ Atalhos:\n' +
                      '• Ctrl+R: Atualizar dados\n' +
                      '• Ctrl+S: Solicitar selos (quando habilitado)\n' +
                      '• Ctrl+Enter: Selar apontamentos (quando habilitado)\n' +
                      '• ESC: Fechar modais\n\n' +
                      '📞 Suporte: Contacte o administrador do sistema.';
        
        if (window.uiComponents && window.uiComponents.mostrarConfirmacao) {
            window.uiComponents.mostrarConfirmacao(
                mensagem,
                function() {
                    console.log('ℹ️ Ajuda visualizada');
                },
                null,
                'Ajuda do Sistema'
            );
        } else {
            alert(mensagem);
        }
    }
    
    // ============================================
    // FUNÇÕES DE TRATAMENTO DE EVENTOS
    // ============================================
    
    function tratarOpcaoSelecionada(opcao) {
        if (!opcao) return;
        
        console.log('🎯 Tratando opção selecionada:', opcao.label);
        
        // Limpar selos solicitados automaticamente
        if (window.selosManager && window.selosManager.limparSelosSolicitados) {
            window.selosManager.limparSelosSolicitados(app);
        }
        
        // Buscar intervalo se tiver data
        if (elementos.dataAto && elementos.dataAto.value) {
            setTimeout(function() {
                if (window.apontamentoService && window.apontamentoService.buscarIntervalo) {
                    window.apontamentoService.buscarIntervalo(app);
                }
            }, 100);
        }
    }
    
    function atualizarUIComSelosSolicitados(selos) {
        // Atualizar floating totalizer
        if (window.floatingTotalizer && window.floatingTotalizer.atualizar) {
            window.floatingTotalizer.atualizar(app, selos);
        }
        
        // Atualizar totalizador principal
        if (window.uiComponents && window.uiComponents.atualizarTotalizadorSelos) {
            window.uiComponents.atualizarTotalizadorSelos(app, selos);
        }
    }
    
    function tratarResultadoBuscaApontamentos(resultado) {
        if (!resultado || !resultado.success) {
            console.warn('⚠️ Busca de apontamentos sem sucesso');
            return;
        }
        
        // Atualizar estado dos botões
        if (window.buttonController) {
            window.buttonController.verificarEstadoBotoes();
        }
    }
    
    function tratarResultadoCalculo(resultado) {
        if (!resultado || !resultado.success) {
            console.warn('⚠️ Cálculo sem sucesso');
            return;
        }
        
        console.log('✅ Cálculo processado com sucesso');
        
        // Mostrar mensagem
        if (resultado.mensagem && window.uiComponents) {
            window.uiComponents.mostrarMensagem(resultado.mensagem, 'success');
        }
    }
    
    function mostrarErroSistema(erro) {
        console.error('🔴 ERRO DO SISTEMA DETECTADO:', erro);
        
        // Mostrar erro para o usuário
        if (window.uiComponents && window.uiComponents.mostrarErroDetalhado) {
            window.uiComponents.mostrarErroDetalhado(erro);
        } else if (window.uiComponents && window.uiComponents.mostrarMensagem) {
            window.uiComponents.mostrarMensagem(
                'Erro no sistema: ' + (erro.erro || 'Erro desconhecido'),
                'error'
            );
        }
    }
    
    function configurarNavegacao() {
        // Configurar atalhos de teclado
        document.addEventListener('keydown', function(event) {
            // Ctrl + R para refresh
            if ((event.ctrlKey || event.metaKey) && event.key === 'r') {
                event.preventDefault();
                console.log('🔁 Atalho Ctrl+R pressionado');
                elementos.btnRefresh.click();
            }
            
            // Escape para fechar modais
            if (event.key === 'Escape') {
                var modais = document.querySelectorAll('.modal-overlay[style*="display: flex"]');
                if (modais.length > 0) {
                    event.preventDefault();
                    for (var i = 0; i < modais.length; i++) {
                        modais[i].style.display = 'none';
                    }
                }
            }
        });
    }
    
    function configurarIntegracaoMaker() {
        // Verificar se estamos em modo Maker
        try {
            var urlParams = new URLSearchParams(window.location.search);
            if (urlParams.get('origem') === 'maker' || urlParams.get('embedded') === 'true') {
                console.log('🔗 Modo Maker detectado - configurando integração');
                
                // Ajustar UI para modo embedded se necessário
                document.body.classList.add('embedded-mode');
                
                // Configurar comunicação com parent
                if (window.parent && window.parent !== window) {
                    window.addEventListener('message', function(event) {
                        console.log('📨 Mensagem recebida do parent:', event.data);
                        
                        if (event.data && event.data.type === 'selador:comando') {
                            tratarComandoMaker(event.data);
                        }
                    });
                }
            }
        } catch (e) {
            console.warn('⚠️ Erro ao configurar integração Maker:', e);
        }
    }
    
    function tratarComandoMaker(comando) {
        console.log('🎯 Comando Maker recebido:', comando);
        
        switch (comando.acao) {
            case 'fechar':
                fecharAplicacao();
                break;
            case 'atualizar':
                atualizarDadosCompletos();
                break;
            case 'logout':
                fazerLogout();
                break;
            default:
                console.warn('⚠️ Comando Maker desconhecido:', comando.acao);
        }
    }
    
    // ============================================
    // API PÚBLICA
    // ============================================
    
    return {
        inicializar: inicializar,
        
        // Funções principais
        atualizarDadosCompletos: atualizarDadosCompletos,
        fazerLogout: fazerLogout,
        fecharAplicacao: fecharAplicacao,
        mostrarAjuda: mostrarAjuda,
        
        // Funções de tratamento
        tratarOpcaoSelecionada: tratarOpcaoSelecionada,
        tratarResultadoBuscaApontamentos: tratarResultadoBuscaApontamentos,
        tratarResultadoCalculo: tratarResultadoCalculo,
        
        // Para outros módulos
        getApp: function() { return app; },
        getElementos: function() { return elementos; }
    };
})();

console.log('✅ app-controller.js carregado - Orquestração principal');