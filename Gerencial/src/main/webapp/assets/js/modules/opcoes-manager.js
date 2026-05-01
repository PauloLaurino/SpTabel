// ============================================
// OPCOES MANAGER - REFATORADO (FASE 2)
// Usa: window.appState + window.eventBus
// Responsabilidade única: Gerenciar opções do sistema
// ============================================

window.opcoesManager = (function() {
    
    // Lista completa de opções do sistema (imutável)
    const OPCOES_DATA = Object.freeze([
        { id: 'opcao1', label: 'Apontamento', codigo: '701', cor: '', descricao: 'Apontamento padrão' },
        { id: 'opcao2', label: 'Intimação/Digitalização', codigo: '702-738/725-740/703', cor: 'amarelo', descricao: 'Intimação e digitalização' },
        { id: 'opcao3', label: 'Retirada/Baixa', codigo: '705/735-736', cor: 'verde-escuro', descricao: 'Retirada e baixa' },
        { id: 'opcao4', label: 'Pagamento/Baixa', codigo: '704/735-736', cor: 'verde-escuro', descricao: 'Pagamento com baixa' },
        { id: 'opcao5', label: 'Instrumento Diferido', codigo: '739', cor: 'vermelho', descricao: 'Instrumento diferido' },
        { id: 'opcao6', label: 'Protesto com Custas Antecipadas', codigo: '706', cor: 'azul-escuro', descricao: 'Protesto com custas antecipadas' },
        { id: 'opcao7', label: 'Devolução por Irregularidade', codigo: '729', cor: 'roxo', descricao: 'Devolução por irregularidade' },
        { id: 'opcao8', label: 'Sustação do Protesto', codigo: '734', cor: 'azul-ciano', descricao: 'Sustação de protesto' },
        { id: 'opcao9', label: 'Revogação de Sustação', codigo: '734', cor: 'azul-ciano', descricao: 'Revogação de sustação' },
        { id: 'opcao10', label: 'Sustação Definitiva', codigo: '727', cor: 'azul-ciano', descricao: 'Sustação definitiva' },
        { id: 'opcao11', label: 'Suspensão de Protesto', codigo: '733', cor: 'bege', descricao: 'Suspensão de protesto' },
        { id: 'opcao12', label: 'Revogação de Suspenso', codigo: '733', cor: 'bege', descricao: 'Revogação de suspenso' },
        { id: 'opcao13', label: 'Segunda Revogação', codigo: '733', cor: 'bege', descricao: 'Segunda revogação' }
        /*{ id: 'opcao14', label: 'Averbação', codigo: '715', cor: '', descricao: 'Averbação' },
        { id: 'opcao15', label: 'Materialização', codigo: '717', cor: '', descricao: 'Materialização' },
        { id: 'opcao16', label: 'Apostilamento', codigo: '719', cor: '', descricao: 'Apostilamento' }*/
    ]);
    
    // Estado interno do módulo
    var estado = {
        opcoesCarregadas: false,
        eventosConfigurados: false
    };
    
    /**
     * Carregar opções na interface
     * @param {Object} app - Instância da aplicação (para compatibilidade)
     */
    function carregar(app) {
        console.log('🔍 [OpcoesManager] Carregando opções...');
        
        var container = window.domManager.getElementById('opcoesLista'); // ✅ CORRIGIDO
        if (!container) {
            console.error('❌ Container opcoesLista não encontrado');
            return;
        }
        
        container.innerHTML = '';
        
        // Criar elementos para cada opção
        for (var i = 0; i < OPCOES_DATA.length; i++) {
            var opcao = OPCOES_DATA[i];
            criarElementoOpcao(container, opcao);
        }
        
        // Configurar eventos
        configurarEventos();
        
        estado.opcoesCarregadas = true;
        console.log('✅ [OpcoesManager] Opções carregadas:', OPCOES_DATA.length + ' itens');
    }
    
    /**
     * Criar elemento HTML para uma opção
     * @param {HTMLElement} container - Container pai
     * @param {Object} opcao - Dados da opção
     */
    function criarElementoOpcao(container, opcao) {
        var opcaoElement = document.createElement('div');
        var classes = 'opcao-item';
        
        // Adicionar classe de cor se existir
        if (opcao.cor && opcao.cor.trim() !== '') {
            classes += ' opcao-' + opcao.cor;
        }
        
        opcaoElement.className = classes;
        opcaoElement.innerHTML = '\
            <input type="radio" \
                   name="opcaoSelecionada" \
                   id="' + opcao.id + '" \
                   class="opcao-radio" \
                   value="' + opcao.codigo + '">\
            <label for="' + opcao.id + '" class="opcao-label">' + opcao.label + '</label>\
            <span class="opcao-codigo">' + opcao.codigo + '</span>\
        ';
        
        // Configurar eventos
        var radioInput = opcaoElement.querySelector('input[type="radio"]');
        var labelElement = opcaoElement.querySelector('.opcao-label');
        
        // Evento no radio button
        radioInput.addEventListener('change', function() {
            if (this.checked) {
                selecionarOpcao(opcao.id);
            }
        });
        
        // Evento no label
        labelElement.addEventListener('click', function(event) {
            if (radioInput.checked) return;
            radioInput.checked = true;
            selecionarOpcao(opcao.id);
            event.preventDefault();
        });
        
        container.appendChild(opcaoElement);
    }
    
    /**
     * Configurar eventos do módulo
     */
    function configurarEventos() {
        if (estado.eventosConfigurados) return;
        
        // Ouvir eventos do Event Bus
        if (window.eventBus) {
            // Limpar selos quando opção mudar (via Event Bus)
            window.eventBus.on('opcao-selecionada', function(opcao) {
                console.log('🔄 [OpcoesManager] Opção alterada, limpando selos...');
                
                // Emitir evento para limpar selos
                window.eventBus.emit('limpar-selos-solicitados', {
                    motivo: 'mudanca-opcao',
                    opcaoAnterior: window.appState ? window.appState.getOpcaoSelecionada() : null,
                    opcaoNova: opcao
                });
                
                // Limpar intervalo também
                window.eventBus.emit('limpar-intervalo', {
                    motivo: 'mudanca-opcao'
                });
            });
        }
        
        estado.eventosConfigurados = true;
        console.log('🔧 [OpcoesManager] Eventos configurados');
    }
    
    /**
     * Selecionar uma opção
     * @param {string} opcaoId - ID da opção
     */
    function selecionarOpcao(opcaoId) {
        var opcao = obterOpcaoPorId(opcaoId);
        
        if (!opcao) {
            console.warn('⚠️ [OpcoesManager] Opção não encontrada:', opcaoId);
            return;
        }
        
        console.log('📌 [OpcoesManager] Opção selecionada:', opcao.label, 'Código:', opcao.codigo);
        
        // 1. Atualizar estado global
        if (window.appState) {
            window.appState.setOpcaoSelecionada(opcao);
        }
        
        // 2. Atualizar interface visual
        atualizarSelecaoVisual(opcaoId);
        
        // 3. Notificar via Event Bus
        if (window.eventBus) {
            window.eventBus.emit('opcao-selecionada', opcao);
        }
        
        // 4. Mostrar na interface
        if (window.domManager && window.domManager.mostrarOpcaoSelecionada) {
            window.domManager.mostrarOpcaoSelecionada(opcao);
        }
        
        // ✅ 5. BUSCAR INTERVALO AUTOMATICAMENTE se tiver data preenchida
        buscarIntervaloSeDataDisponivel(opcao);
        
        // 🔥 EMITIR EVENTO DE OPÇÃO SELECIONADA
        if (window.eventBus && window.eventBus.EVENTOS) {
            window.eventBus.emit(window.eventBus.EVENTOS.OPCAO_SELECIONADA, {
                codigo: opcao.codigo,
                label: opcao.label,
                descricao: opcao.descricao || '',
                timestamp: new Date().toISOString()
            });
        } else {
            console.warn('⚠️ Event Bus não disponível para emitir OPCAO_SELECIONADA');
        }
        
        // 🔥 FORÇAR LIMPEZA DIRETA TAMBÉM (backup)
        setTimeout(function() {
            if (window.selosManager && window.selosManager.limparSelosSolicitados) {
                window.selosManager.limparSelosSolicitados();
            }
        }, 100);
    }

    /* 
    * Buscar intervalo automaticamente se data do ato estiver disponível
    */
    function buscarIntervaloSeDataDisponivel(opcao) {
        console.log('🔍 [OpcoesManager] Verificando se deve buscar intervalo...');
        
        // Verificar se domManager está disponível
        if (!window.domManager || !window.domManager.getElementos) {
            console.warn('⚠️ domManager não disponível');
            return;
        }
        
        var elementos = window.domManager.getElementos();
        if (!elementos || !elementos.dataAto) {
            console.warn('⚠️ Elementos DOM não disponíveis');
            return;
        }
        
        // Verificar se data está preenchida
        var dataAto = elementos.dataAto.value;
        if (!dataAto || dataAto.trim() === '') {
            console.log('ℹ️ Data do ato não preenchida, aguardando...');
            return;
        }
        
        console.log('📅 Data encontrada, buscando intervalo para opção:', opcao.label);
        
        // Aguardar um momento para garantir que a limpeza aconteceu
        setTimeout(function() {
            // Verificar se apontamentoService está disponível
            if (window.apontamentoService && window.apontamentoService.buscarIntervalo) {
                console.log('🚀 Buscando intervalo automaticamente...');
                
                // Emitir evento de loading
                if (window.eventBus) {
                    window.eventBus.emit('loading:iniciado', {
                        acao: 'buscar_intervalo_opcao',
                        opcao: opcao.label
                    });
                }
                
                // Buscar intervalo
                window.apontamentoService.buscarIntervalo(window.app || { elementos: elementos });
                
            } else {
                console.warn('⚠️ apontamentoService não disponível para busca automática');
                
                // Tentar fallback: usar API diretamente
                buscarIntervaloFallback(dataAto, opcao.codigo);
            }
        }, 300); // Delay para garantir processamento da limpeza
    }

    // ============================================
    // FUNÇÃO FALLBACK PARA BUSCA DE INTERVALO:
    // ============================================

    function buscarIntervaloFallback(dataAto, codigoOpcao) {
        console.log('🔄 Usando fallback para busca de intervalo...');
        
        if (!window.api || typeof window.api.buscarIntervaloApontamentos !== 'function') {
            console.error('❌ API não disponível para fallback');
            return;
        }
        
        // Converter data para formato AAAAMMDD
        var dataFormatada = dataAto.replace(/-/g, '');
        
        // Buscar via API
        window.api.buscarIntervaloApontamentos(dataFormatada, codigoOpcao)
            .then(function(response) {
                console.log('✅ Resposta fallback:', response);
                
                if (response && response.success && response.data) {
                    // Popular campos manualmente
                    popularCamposIntervaloFallback(response.data);
                }
            })
            .catch(function(error) {
                console.error('❌ Erro no fallback:', error);
            });
    }

    // ============================================
    // FUNÇÃO PARA POPULAR CAMPOS (FALLBACK):
    // ============================================

    function popularCamposIntervaloFallback(dadosIntervalo) {
        if (!dadosIntervalo || !window.domManager) return;
        
        var elementos = window.domManager.getElementos();
        if (!elementos) return;
        
        // Popular campos
        if (elementos.anoMesInicial && dadosIntervalo.primeiro && dadosIntervalo.primeiro.anoMes) {
            elementos.anoMesInicial.value = dadosIntervalo.primeiro.anoMes;
        }
        
        if (elementos.aponInicial && dadosIntervalo.primeiro && dadosIntervalo.primeiro.numero) {
            elementos.aponInicial.value = dadosIntervalo.primeiro.numero;
        }
        
        if (elementos.anoMesFinal && dadosIntervalo.ultimo && dadosIntervalo.ultimo.anoMes) {
            elementos.anoMesFinal.value = dadosIntervalo.ultimo.anoMes;
        }
        
        if (elementos.aponFinal && dadosIntervalo.ultimo && dadosIntervalo.ultimo.numero) {
            elementos.aponFinal.value = dadosIntervalo.ultimo.numero;
        }
        
        console.log('✅ Campos populados via fallback');
        
        // Emitir evento de intervalo preenchido
        if (window.eventBus) {
            window.eventBus.emit('intervalo:preenchido', {
                source: 'opcoes-manager-fallback',
                dados: dadosIntervalo,
                timestamp: new Date().toISOString()
            });
        }
    }
    
    /**
     * Atualizar seleção visual
     * @param {string} opcaoId - ID da opção selecionada
     */
    function atualizarSelecaoVisual(opcaoId) {
        // Remover seleção anterior
        var itensSelecionados = document.querySelectorAll('.opcao-item.selecionada');
        for (var i = 0; i < itensSelecionados.length; i++) {
            itensSelecionados[i].classList.remove('selecionada');
        }
        
        // Adicionar seleção nova
        var opcaoElement = document.getElementById(opcaoId);
        if (opcaoElement) {
            opcaoElement.closest('.opcao-item').classList.add('selecionada');
        }
    }
    
    /**
     * Obter opção pelo ID
     * @param {string} opcaoId - ID da opção
     * @returns {Object|null} Dados da opção ou null
     */
    function obterOpcaoPorId(opcaoId) {
        for (var i = 0; i < OPCOES_DATA.length; i++) {
            if (OPCOES_DATA[i].id === opcaoId) {
                return Object.assign({}, OPCOES_DATA[i]); // Retorna cópia
            }
        }
        return null;
    }
    
    /**
     * Obter opção pelo código
     * @param {string} codigo - Código da opção
     * @returns {Object|null} Dados da opção ou null
     */
    function obterOpcaoPorCodigo(codigo) {
        for (var i = 0; i < OPCOES_DATA.length; i++) {
            if (OPCOES_DATA[i].codigo === codigo) {
                return Object.assign({}, OPCOES_DATA[i]); // Retorna cópia
            }
        }
        return null;
    }
    
    /**
     * Obter todas as opções
     * @returns {Array} Cópia da lista de opções
     */
    function obterTodasOpcoes() {
        return OPCOES_DATA.slice();
    }
    
    /**
     * Verificar se uma opção está selecionada
     * @returns {boolean} True se há opção selecionada
     */
    function isOpcaoSelecionada() {
        if (!window.appState) return false;
        return !!window.appState.getOpcaoSelecionada();
    }
    
    /**
     * Obter opção selecionada atual
     * @returns {Object|null} Opção selecionada ou null
     */
    function obterOpcaoSelecionada() {
        if (!window.appState) return null;
        return window.appState.getOpcaoSelecionada();
    }
    
    /**
     * Desselecionar opção atual
     */
    function desselecionarOpcao() {
        if (!window.appState) return;
        
        // Remover seleção visual
        var itensSelecionados = document.querySelectorAll('.opcao-item.selecionada');
        for (var i = 0; i < itensSelecionados.length; i++) {
            itensSelecionados[i].classList.remove('selecionada');
        }
        
        // Limpar estado
        window.appState.setOpcaoSelecionada(null);
        
        // Ocultar seção
        var section = document.getElementById('opcaoSelecionadaSection');
        if (section) section.style.display = 'none';
        
        console.log('🗑️ [OpcoesManager] Opção desselecionada');
    }
    
    /**
     * Selecionar opção por código
     * @param {string} codigo - Código da opção
     * @returns {boolean} True se selecionou com sucesso
     */
    function selecionarOpcaoPorCodigo(codigo) {
        var opcao = obterOpcaoPorCodigo(codigo);
        if (!opcao) {
            console.warn('⚠️ [OpcoesManager] Opção não encontrada para código:', codigo);
            return false;
        }
        
        // Encontrar elemento radio
        var radioElement = document.getElementById(opcao.id);
        if (!radioElement) {
            console.warn('⚠️ [OpcoesManager] Elemento radio não encontrado:', opcao.id);
            return false;
        }
        
        // Simular seleção
        radioElement.checked = true;
        selecionarOpcao(opcao.id);

        // 🔥 GARANTIR QUE O EVENTO SEJA EMITIDO
        if (window.eventBus) {
            window.eventBus.emit(window.eventBus.EVENTOS.OPCAO_SELECIONADA, {
                codigo: opcao.codigo,
                label: opcao.label,
                descricao: opcao.descricao || '',
                element: element
            });
        }
        
        // 🔥 TAMBÉM LIMPAR SELOS SOLICITADOS DIRETAMENTE
        if (window.selosManager && window.selosManager.limparSelosSolicitados) {
            setTimeout(function() {
                window.selosManager.limparSelosSolicitados();
            }, 50);
        }

        return true;
    }
    
    /**
     * Validar se a opção selecionada é válida
     * @returns {Object} Resultado da validação
     */
    function validarOpcaoSelecionada() {
        var resultado = {
            valida: false,
            mensagem: '',
            opcao: null
        };
        
        if (!isOpcaoSelecionada()) {
            resultado.mensagem = 'Nenhuma opção selecionada';
            return resultado;
        }
        
        var opcao = obterOpcaoSelecionada();
        if (!opcao) {
            resultado.mensagem = 'Opção selecionada inválida';
            return resultado;
        }
        
        // Verificar se o código existe na lista
        var opcaoValida = obterOpcaoPorCodigo(opcao.codigo);
        if (!opcaoValida) {
            resultado.mensagem = 'Opção com código inválido: ' + opcao.codigo;
            return resultado;
        }
        
        resultado.valida = true;
        resultado.opcao = opcao;
        resultado.mensagem = 'Opção válida: ' + opcao.label;
        
        return resultado;
    }
    
    /**
     * Obter estatísticas das opções
     * @returns {Object} Estatísticas
     */
    function obterEstatisticas() {
        var opcoesComCor = 0;
        var codigosUnicos = [];
        
        for (var i = 0; i < OPCOES_DATA.length; i++) {
            var opcao = OPCOES_DATA[i];
            
            if (opcao.cor && opcao.cor.trim() !== '') {
                opcoesComCor++;
            }
            
            if (codigosUnicos.indexOf(opcao.codigo) === -1) {
                codigosUnicos.push(opcao.codigo);
            }
        }
        
        return {
            total: OPCOES_DATA.length,
            comCor: opcoesComCor,
            codigosUnicos: codigosUnicos.length,
            codigos: codigosUnicos,
            ultimaAtualizacao: new Date().toISOString()
        };
    }
    
    /**
     * Inicializar módulo (para compatibilidade com nova arquitetura)
     */
    function inicializar() {
        console.log('🎮 [OpcoesManager] Inicializando...');
        
        // Carregar opções se DOM estiver pronto
        if (document.readyState === 'complete' || document.readyState === 'interactive') {
            carregar();
        } else {
            document.addEventListener('DOMContentLoaded', carregar);
        }
        
        return true;
    }
    
    // ============================================
    // EXPORTAÇÃO PÚBLICA
    // ============================================
    
    return {
        // Inicialização
        carregar: carregar,
        inicializar: inicializar,
        
        // Seleção
        selecionarOpcao: selecionarOpcao,
        selecionarOpcaoPorCodigo: selecionarOpcaoPorCodigo,
        desselecionarOpcao: desselecionarOpcao,
        obterOpcaoSelecionada: obterOpcaoSelecionada,
        isOpcaoSelecionada: isOpcaoSelecionada,
        
        // Busca
        obterOpcaoPorId: obterOpcaoPorId,
        obterOpcaoPorCodigo: obterOpcaoPorCodigo,
        obterTodasOpcoes: obterTodasOpcoes,
        
        // Validação
        validarOpcaoSelecionada: validarOpcaoSelecionada,
        
        // Utilitários
        obterEstatisticas: obterEstatisticas
    };
})();

// Auto-inicialização
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function() {
        setTimeout(function() {
            if (window.opcoesManager && window.opcoesManager.inicializar) {
                window.opcoesManager.inicializar();
            }
        }, 100);
    });
} else {
    setTimeout(function() {
        if (window.opcoesManager && window.opcoesManager.inicializar) {
            window.opcoesManager.inicializar();
        }
    }, 100);
}

console.log('✅ opcoes-manager.js REFATORADO - Nova arquitetura');