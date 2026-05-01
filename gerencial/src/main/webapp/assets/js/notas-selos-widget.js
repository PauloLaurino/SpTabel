/**
 * Widget Flutuante - Selagem de Atos Tabelionato de Notas
 * 
 * Incluir este script no formulário principal do Maker Studio:
 * <script src="assets/js/notas-selos-widget.js"></script>
 * 
 * O ícone aparecerá no canto inferior direito da tela.
 */

(function () {
    'use strict';

    console.log('💬 [WIDGET NOTAS] Iniciando widget de selagem de atos...');
    console.log('💬 [WIDGET NOTAS] URL atual:', window.location.href);

    // Configurações
    const CONFIG = {
        urlPagina: 'views/notas_selos_funarpen.html',
        icone: 'fa-file-signature',
        titulo: 'Selagem Notas',
        posicao: 'bottom-right'
    };

    console.log('💬 [WIDGET NOTAS] Configurações:', CONFIG);

    // Verificar se já foi inicializado
    if (window.notasSelosWidgetInicializado) {
        console.log('ℹ️ [WIDGET NOTAS] Widget já foi inicializado anteriormente');
        return;
    }
    window.notasSelosWidgetInicializado = true;
    console.log('✅ [WIDGET NOTAS] Flag de inicialização definida');

    // Criar estilos
    console.log('💬 [WIDGET NOTAS] Criando estilos CSS...');
    function criarEstilos() {
        const estilos = document.createElement('style');
        estilos.textContent = `
            /* Widget Flutuante - Selagem Notas */
            .notas-selos-widget {
                position: fixed;
                bottom: 20px;
                right: 20px;
                z-index: 99999;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }

            .notas-selos-botao {
                width: 60px;
                height: 60px;
                border-radius: 50%;
                background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
                color: white;
                border: none;
                cursor: pointer;
                box-shadow: 0 4px 15px rgba(30, 60, 114, 0.4);
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 24px;
                transition: all 0.3s ease;
                position: relative;
            }

            .notas-selos-botao:hover {
                transform: scale(1.1);
                box-shadow: 0 6px 20px rgba(30, 60, 114, 0.6);
            }

            .notas-selos-botao i {
                pointer-events: none;
            }

            /* Tooltip */
            .notas-selos-tooltip {
                position: absolute;
                bottom: 70px;
                right: 0;
                background: #333;
                color: white;
                padding: 8px 12px;
                border-radius: 6px;
                font-size: 13px;
                white-space: nowrap;
                opacity: 0;
                visibility: hidden;
                transition: all 0.3s ease;
            }

            .notas-selos-tooltip::after {
                content: '';
                position: absolute;
                bottom: -6px;
                right: 20px;
                width: 12px;
                height: 12px;
                background: #333;
                transform: rotate(45deg);
            }

            .notas-selos-botao:hover + .notas-selos-tooltip {
                opacity: 1;
                visibility: visible;
            }

            /* Modal/Frame */
            .notas-selos-modal {
                display: none;
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0, 0, 0, 0.5);
                z-index: 999999;
                align-items: center;
                justify-content: center;
            }

            .notas-selos-modal.ativo {
                display: flex;
            }

            .notas-selos-frame-container {
                width: 90%;
                height: 90%;
                max-width: 1200px;
                max-height: 800px;
                background: white;
                border-radius: 10px;
                box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
                display: flex;
                flex-direction: column;
                overflow: hidden;
            }

            .notas-selos-frame-header {
                background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
                color: white;
                padding: 15px 20px;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            .notas-selos-frame-header h3 {
                margin: 0;
                font-size: 16px;
                font-weight: 600;
            }

            .notas-selos-fechar {
                background: none;
                border: none;
                color: white;
                font-size: 20px;
                cursor: pointer;
                padding: 5px 10px;
                border-radius: 4px;
                transition: background 0.3s;
            }

            .notas-selos-fechar:hover {
                background: rgba(255, 255, 255, 0.2);
            }

            .notas-selos-frame {
                flex: 1;
                width: 100%;
                border: none;
            }
        `;
        document.head.appendChild(estilos);
    }

    // Criar widget
    function criarWidget() {
        // Container principal
        const container = document.createElement('div');
        container.className = 'notas-selos-widget';

        // Botão
        const botao = document.createElement('button');
        botao.className = 'notas-selos-botao';
        botao.title = CONFIG.titulo;
        botao.innerHTML = `<i class="fas ${CONFIG.icone}"></i>`;

        // Tooltip
        const tooltip = document.createElement('div');
        tooltip.className = 'notas-selos-tooltip';
        tooltip.textContent = CONFIG.titulo;

        // Evento de clique
        botao.addEventListener('click', abrirModal);

        // Montar
        container.appendChild(botao);
        container.appendChild(tooltip);

        // Adicionar ao body
        document.body.appendChild(container);
    }

    // Criar modal
    function criarModal() {
        const modal = document.createElement('div');
        modal.className = 'notas-selos-modal';
        modal.id = 'notasSelosModal';

        modal.innerHTML = `
            <div class="notas-selos-frame-container">
                <div class="notas-selos-frame-header">
                    <h3><i class="fas ${CONFIG.icone}"></i> ${CONFIG.titulo} - FUNARPEN V11.12 Plus</h3>
                    <button class="notas-selos-fechar" onclick="fecharNotasSelosModal()">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
                <iframe class="notas-selos-frame" src="${CONFIG.urlPagina}"></iframe>
            </div>
        `;

        // Evento de clique fora para fechar
        modal.addEventListener('click', function (e) {
            if (e.target === modal) {
                fecharModal();
            }
        });

        document.body.appendChild(modal);

        // Função global para fechar
        window.fecharNotasSelosModal = fecharModal;
    }

    // Abrir modal
    function abrirModal() {
        const modal = document.getElementById('notasSelosModal');

        // Verificar se o modal já está aberto
        if (modal && modal.classList.contains('ativo')) {
            console.log('ℹ️ Modal de Selagem Notas já está aberto');
            return;
        }

        if (modal) {
            modal.classList.add('ativo');
        } else {
            criarModal();
            document.getElementById('notasSelosModal').classList.add('ativo');
        }
    }

    // Fechar modal
    function fecharModal() {
        const modal = document.getElementById('notasSelosModal');
        if (modal) {
            modal.classList.remove('ativo');
        }
    }

    // Inicializar
    function inicializar() {
        // Verificar se o DOM está pronto
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', function () {
                criarEstilos();
                criarWidget();
            });
        } else {
            criarEstilos();
            criarWidget();
        }
    }

    // Iniciar
    inicializar();

    console.log('✅ Widget de Selagem Notas inicializado com sucesso!');

})();
