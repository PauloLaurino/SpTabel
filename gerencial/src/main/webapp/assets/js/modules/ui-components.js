// ============================================
// UI COMPONENTS - REFATORADO (FASE 2)
// Usa: window.eventBus para comunicação
// ============================================

window.uiComponents = (function() {
    
    var estado = {
        mensagensAtivas: 0,
        loadingAtivo: false
    };
    
    /**
     * Mostrar mensagem toast
     */
    function mostrarMensagem(mensagem, tipo, duracao) {
        tipo = tipo || 'info';
        duracao = duracao || 3000;
        
        // Notificar via Event Bus
        if (window.eventBus) {
            window.eventBus.emit(window.eventBus.EVENTOS.MENSAGEM_EXIBIDA, {
                tipo: tipo,
                mensagem: mensagem,
                duracao: duracao
            });
        }
        
        console.log('💬 [UI] Mensagem [' + tipo + ']:', mensagem);
        
        // Criar elemento (mesma lógica anterior)
        var mensagemDiv = document.createElement('div');
        mensagemDiv.className = 'ui-mensagem mensagem-' + tipo;
        
        // ... (restante da implementação original mantida)
        mensagemDiv.style.cssText = '\
            position: fixed;\
            top: 20px;\
            right: 20px;\
            padding: 12px 20px;\
            border-radius: 4px;\
            font-weight: 500;\
            z-index: 9999;\
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);\
            animation: slideIn 0.3s ease;\
            max-width: 300px;\
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;\
            font-size: 14px;\
            line-height: 1.4;\
        ';
        
        var cores = {
            'success': { bg: '#d4edda', text: '#155724', border: '#c3e6cb', icon: 'check-circle' },
            'error': { bg: '#f8d7da', text: '#721c24', border: '#f5c6cb', icon: 'exclamation-circle' },
            'warning': { bg: '#fff3cd', text: '#856404', border: '#ffeeba', icon: 'exclamation-triangle' },
            'info': { bg: '#d1ecf1', text: '#0c5460', border: '#bee5eb', icon: 'info-circle' }
        };
        
        var cor = cores[tipo] || cores.info;
        mensagemDiv.style.backgroundColor = cor.bg;
        mensagemDiv.style.color = cor.text;
        mensagemDiv.style.border = '1px solid ' + cor.border;
        
        var iconClass = 'fas fa-' + cor.icon;
        mensagemDiv.innerHTML = '\
            <i class="' + iconClass + '" style="margin-right: 8px;"></i>\
            ' + mensagem + '\
        ';
        
        document.body.appendChild(mensagemDiv);
        estado.mensagensAtivas++;
        
        // Adicionar animação se não existir
        if (!document.querySelector('#ui-animacoes')) {
            var style = document.createElement('style');
            style.id = 'ui-animacoes';
            style.textContent = '\
                @keyframes slideIn {\
                    from { transform: translateX(100px); opacity: 0; }\
                    to { transform: translateX(0); opacity: 1; }\
                }\
                @keyframes slideOut {\
                    from { transform: translateX(0); opacity: 1; }\
                    to { transform: translateX(100px); opacity: 0; }\
                }\
            ';
            document.head.appendChild(style);
        }
        
        // Remover após duração
        setTimeout(function() {
            mensagemDiv.style.animation = 'slideOut 0.3s ease';
            mensagemDiv.style.opacity = '0';
            
            setTimeout(function() {
                if (mensagemDiv.parentNode) {
                    document.body.removeChild(mensagemDiv);
                    estado.mensagensAtivas--;
                }
            }, 300);
        }, duracao);
        
        return mensagemDiv;
    }
    
    /**
     * Mostrar loading
     */
    function mostrarLoading(mensagem, overlay) {
        var modal = document.getElementById('modalLoading');
        if (!modal) {
            console.warn('⚠️ [UI] Modal de loading não encontrado');
            return null;
        }
        
        // Configurar mensagem
        var messageElement = modal.querySelector('.loading-message');
        if (messageElement && mensagem) {
            messageElement.textContent = mensagem;
        }
        
        // Configurar overlay
        modal.style.backgroundColor = overlay === false ? 'transparent' : 'rgba(0, 0, 0, 0.7)';
        
        // Mostrar
        modal.style.display = 'flex';
        estado.loadingAtivo = true;
        
        // Notificar via Event Bus
        if (window.eventBus) {
            window.eventBus.emit(window.eventBus.EVENTOS.LOADING_INICIADO, {
                mensagem: mensagem,
                overlay: overlay !== false
            });
        }
        
        console.log('⏳ [UI] Loading exibido:', mensagem || 'Processando...');
        return modal;
    }
    
    /**
     * Esconder loading
     */
    function esconderLoading() {
        var modal = document.getElementById('modalLoading');
        if (modal) {
            modal.style.display = 'none';
            estado.loadingAtivo = false;
            
            // Notificar via Event Bus
            if (window.eventBus) {
                window.eventBus.emit(window.eventBus.EVENTOS.LOADING_FINALIZADO);
            }
            
            console.log('✅ [UI] Loading escondido');
        }
    }
    
    /**
     * Mostrar modal de resultado
     * @param {Object} dados - Dados do resultado
     * @param {string} tipo - 'success' ou 'error'
     */
    function mostrarResultado(dados, tipo) {
        tipo = tipo || 'success';
        
        var modal = document.getElementById('modalResultado');
        if (!modal) {
            console.warn('⚠️ Modal de resultado não encontrado');
            mostrarMensagem(dados.mensagem || 'Operação concluída', tipo);
            return;
        }
        
        // Configurar título
        var titulo = modal.querySelector('.modal-title');
        if (titulo) {
            titulo.textContent = tipo === 'success' ? '✅ Operação Concluída' : '❌ Erro na Operação';
            titulo.className = 'modal-title ' + tipo;
        }
        
        // Configurar corpo
        var corpo = modal.querySelector('.modal-body');
        if (corpo) {
            var html = '<div class="resultado-conteudo resultado-' + tipo + '">';
            
            if (tipo === 'success') {
                html += '<div class="resultado-header">';
                html += '<i class="fas fa-check-circle resultado-icon"></i>';
                html += '<h3>Operação Realizada com Sucesso</h3>';
                html += '</div>';
                
                html += '<div class="resultado-detalhes">';
                html += '<div class="detalhe-item">';
                html += '<span class="detalhe-label">Total Processado:</span>';
                html += '<span class="detalhe-valor">' + (dados.total_processados || 0) + '</span>';
                html += '</div>';
                
                if (dados.total_erros) {
                    html += '<div class="detalhe-item">';
                    html += '<span class="detalhe-label">Com Erro:</span>';
                    html += '<span class="detalhe-valor">' + dados.total_erros + '</span>';
                    html += '</div>';
                }
                
                html += '<div class="detalhe-item">';
                html += '<span class="detalhe-label">Data/Hora:</span>';
                html += '<span class="detalhe-valor">' + new Date().toLocaleString('pt-BR') + '</span>';
                html += '</div>';
                
                if (dados.mensagem) {
                    html += '<div class="detalhe-mensagem">';
                    html += '<p>' + dados.mensagem + '</p>';
                    html += '</div>';
                }
                
                html += '</div>';
            } else {
                html += '<div class="resultado-header">';
                html += '<i class="fas fa-exclamation-circle resultado-icon"></i>';
                html += '<h3>Erro no Processamento</h3>';
                html += '</div>';
                
                html += '<div class="resultado-detalhes">';
                html += '<div class="detalhe-item">';
                html += '<span class="detalhe-label">Erro:</span>';
                html += '<span class="detalhe-valor">' + (dados.message || dados.mensagem || 'Erro desconhecido') + '</span>';
                html += '</div>';
                
                if (dados.erro) {
                    html += '<div class="detalhe-item">';
                    html += '<span class="detalhe-label">Detalhes:</span>';
                    html += '<span class="detalhe-valor">' + dados.erro + '</span>';
                    html += '</div>';
                }
                
                html += '</div>';
            }
            
            html += '</div>';
            corpo.innerHTML = html;
        }
        
        // Configurar botões
        var botoes = modal.querySelector('.modal-buttons') || modal.querySelector('.modal-footer');
        if (botoes) {
            botoes.innerHTML = '';
            
            var btnFechar = document.createElement('button');
            btnFechar.className = 'btn-modal ' + (tipo === 'success' ? 'btn-success' : 'btn-error');
            btnFechar.innerHTML = tipo === 'success' ? 
                '<i class="fas fa-check"></i> OK' : 
                '<i class="fas fa-times"></i> Fechar';
            
            btnFechar.onclick = function() {
                modal.style.display = 'none';
            };
            
            botoes.appendChild(btnFechar);
        }
        
        // Mostrar modal
        modal.style.display = 'flex';
        
        // Adicionar estilos se necessário
        adicionarEstilosResultado();
        
        console.log('📊 Modal de resultado exibido:', tipo);
    }
    
    /**
     * Mostrar modal de erro detalhado
     * @param {Object|string} erro - Objeto de erro ou mensagem
     */
    function mostrarErroDetalhado(erro) {
        var mensagem = '';
        var detalhes = '';
        
        if (typeof erro === 'string') {
            mensagem = erro;
        } else if (erro && typeof erro === 'object') {
            mensagem = erro.message || erro.mensagem || 'Erro desconhecido';
            detalhes = JSON.stringify(erro, null, 2);
        }
        
        var modal = document.getElementById('modalErro');
        if (!modal) {
            mostrarMensagem(mensagem, 'error');
            console.error('❌ Erro detalhado:', erro);
            return;
        }
        
        // Configurar corpo
        var corpo = modal.querySelector('.modal-body');
        if (corpo) {
            var html = '<div class="erro-conteudo">';
            html += '<div class="erro-mensagem">';
            html += '<i class="fas fa-exclamation-triangle" style="color: #dc3545; margin-right: 10px;"></i>';
            html += '<strong>' + mensagem + '</strong>';
            html += '</div>';
            
            if (detalhes) {
                html += '<div class="erro-detalhes">';
                html += '<pre style="background: #f8f9fa; padding: 10px; border-radius: 4px; font-size: 12px; overflow: auto; max-height: 200px;">';
                html += detalhes;
                html += '</pre>';
                html += '</div>';
            }
            
            html += '</div>';
            corpo.innerHTML = html;
        }
        
        // Mostrar modal
        modal.style.display = 'flex';
        
        console.error('❌ Modal de erro exibido:', mensagem);
    }
    
    /**
     * Atualizar totalizador de selos
     */
    function atualizarTotalizadorSelos(app, selos) {
        var totalSolicitado = 0;
        var tiposComQuantidade = [];
        
        if (!selos || typeof selos !== 'object') {
            selos = {};
        }
        
        var tipos = ['TP1', 'TP3', 'TP4', 'TPD', 'TPI'];
        for (var i = 0; i < tipos.length; i++) {
            var tipo = tipos[i];
            var quantidade = selos[tipo] || 0;
            
            if (quantidade > 0) {
                totalSolicitado += quantidade;
                tiposComQuantidade.push(quantidade + ' ' + tipo);
            }
        }
        
        var elementos = app.elementos || {};
        var totalContainer = elementos.totalSolicitadoContainer || document.getElementById('totalSolicitadoContainer');
        var totalValor = elementos.totalSolicitadoValor || document.getElementById('totalSolicitadoValor');
        var totalDetalhes = elementos.totalSolicitadoDetalhes || document.getElementById('totalSolicitadoDetalhes');
        
        if (totalContainer && totalValor) {
            if (totalSolicitado > 0) {
                totalContainer.style.display = 'block';
                totalValor.textContent = totalSolicitado;
                
                if (totalDetalhes) {
                    totalDetalhes.textContent = tiposComQuantidade.join(', ') || 'Nenhum selo solicitado';
                }
            } else {
                totalContainer.style.display = 'none';
            }
        }
        
        // Notificar via Event Bus
        if (window.eventBus && totalSolicitado > 0) {
            window.eventBus.emit('totalizador:atualizado', {
                total: totalSolicitado,
                detalhes: tiposComQuantidade
            });
        }
        
        return totalSolicitado;
    }
    
    /**
     * Iniciar atualização de hora
     */
    function iniciarAtualizacaoHora(app) {
        function atualizar() {
            var agora = new Date();
            var horaFormatada = agora.toLocaleTimeString('pt-BR', {
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
            });
            
            var elementos = app.elementos || {};
            var currentTime = elementos.currentTime || document.getElementById('currentTime');
            
            if (currentTime) {
                currentTime.textContent = horaFormatada;
            }
        }
        
        atualizar();
        setInterval(atualizar, 1000);
        
        console.log('🕐 [UI] Atualização de hora iniciada');
    }
    
    /**
     * Aplicar alerta visual em selos zerados
     */
    function aplicarAlertaSelosZerados(selosZerados) {
        if (!selosZerados || selosZerados.length === 0) return;
        
        for (var i = 0; i < selosZerados.length; i++) {
            var selo = selosZerados[i];
            var elemento = document.getElementById('qtdDisponivel' + selo.tipoFrontend);
            
            if (elemento) {
                elemento.classList.add('alerta-zerado');
                elemento.title = 'Selo zerado! Solicite mais selos deste tipo.';
                elemento.style.animation = 'pulse-alerta 2s infinite';
            }
        }
        
        // Garantir animação
        if (!document.querySelector('#ui-animacao-alerta')) {
            var style = document.createElement('style');
            style.id = 'ui-animacao-alerta';
            style.textContent = '\
                @keyframes pulse-alerta {\
                    0% { box-shadow: 0 0 0 0 rgba(220, 53, 69, 0.4); }\
                    70% { box-shadow: 0 0 0 10px rgba(220, 53, 69, 0); }\
                    100% { box-shadow: 0 0 0 0 rgba(220, 53, 69, 0); }\
                }\
            ';
            document.head.appendChild(style);
        }
    }
    
    /**
     * Habilitar/desabilitar botões de ação
     */
    function setEstadoBotoesAcao(habilitar) {
        var botoes = [
            'btnSolicitarSelos',
            'btnSelarApontamentos',
            'btnRefresh',
            'btnFechar'
        ];
        
        for (var i = 0; i < botoes.length; i++) {
            var botao = document.getElementById(botoes[i]);
            if (botao) {
                botao.disabled = !habilitar;
                botao.style.opacity = habilitar ? '1' : '0.5';
                botao.style.cursor = habilitar ? 'pointer' : 'not-allowed';
            }
        }
        
        console.log(habilitar ? '✅ [UI] Botões habilitados' : '⏸️ [UI] Botões desabilitados');
    }
    
    /**
     * Configurar tooltips
     */
    function configurarTooltips() {
        var tooltips = {
            'btnSolicitarSelos': 'Calcular e solicitar selos necessários',
            'btnSelarApontamentos': 'Processar selagem dos apontamentos',
            'btnRefresh': 'Atualizar dados do sistema',
            'btnFechar': 'Fechar o Sistema Selador',
            'btnHelp': 'Ajuda e informações do sistema',
            'btnLogout': 'Sair do sistema'
        };
        
        for (var id in tooltips) {
            var elemento = document.getElementById(id);
            if (elemento && !elemento.title) {
                elemento.title = tooltips[id];
            }
        }
        
        console.log('🔧 [UI] Tooltips configurados');
    }
    
    /**
     * Limpar todas as mensagens
     */
    function limparMensagens() {
        var mensagens = document.querySelectorAll('.ui-mensagem');
        for (var i = 0; i < mensagens.length; i++) {
            if (mensagens[i].parentNode) {
                document.body.removeChild(mensagens[i]);
            }
        }
        
        estado.mensagensAtivas = 0;
        console.log('🧹 [UI] Mensagens limpas');
    }
    
    /**
     * Adicionar estilos para resultado
     */
    function adicionarEstilosResultado() {
        if (document.querySelector('#ui-estilos-resultado')) return;
        
        var style = document.createElement('style');
        style.id = 'ui-estilos-resultado';
        style.textContent = '\
            .resultado-conteudo { padding: 20px; }\
            .resultado-success { border-left: 4px solid #28a745; }\
            .resultado-error { border-left: 4px solid #dc3545; }\
            .resultado-header { display: flex; align-items: center; margin-bottom: 20px; }\
            .resultado-icon { font-size: 24px; margin-right: 12px; }\
            .resultado-success .resultado-icon { color: #28a745; }\
            .resultado-error .resultado-icon { color: #dc3545; }\
            .resultado-header h3 { margin: 0; font-size: 18px; }\
            .resultado-detalhes { background: #f8f9fa; padding: 15px; border-radius: 4px; }\
            .detalhe-item { display: flex; justify-content: space-between; margin-bottom: 10px; }\
            .detalhe-item:last-child { margin-bottom: 0; }\
            .detalhe-label { font-weight: 600; color: #495057; }\
            .detalhe-valor { font-weight: 500; color: #212529; }\
            .detalhe-mensagem { margin-top: 15px; padding-top: 15px; border-top: 1px solid #dee2e6; }\
            .detalhe-mensagem p { margin: 0; color: #6c757d; }\
            .btn-modal { padding: 8px 16px; border: none; border-radius: 4px; cursor: pointer; font-weight: 500; }\
            .btn-success { background: #28a745; color: white; }\
            .btn-error { background: #dc3545; color: white; }\
        ';
        
        document.head.appendChild(style);
    }
    
    // Inicialização automática
    function inicializar() {
        console.log('🎨 [UI Components] Inicializando...');
        configurarTooltips();
    }
    
    // Auto-inicialização
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', inicializar);
    } else {
        setTimeout(inicializar, 100);
    }
    
    return {
        // Mensagens
        mostrarMensagem: mostrarMensagem,
        limparMensagens: limparMensagens,
        
        // Loading
        mostrarLoading: mostrarLoading,
        esconderLoading: esconderLoading,
        
        // Modais
        mostrarResultado: mostrarResultado,
        mostrarErroDetalhado: mostrarErroDetalhado,
        
        // Atualizações
        atualizarTotalizadorSelos: atualizarTotalizadorSelos,
        iniciarAtualizacaoHora: iniciarAtualizacaoHora,
        aplicarAlertaSelosZerados: aplicarAlertaSelosZerados,
        setEstadoBotoesAcao: setEstadoBotoesAcao,
        
        // Configuração
        configurarTooltips: configurarTooltips,
        
        // Estado
        getEstado: function() { return estado; }
    };
})();

console.log('✅ ui-components.js REFATORADO - Nova arquitetura');