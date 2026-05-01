/**
 * Chat WhatsApp Widget - Sistema de Protesto
 * Widget injetável que cria um botão flutuante no canto inferior direito
 * e abre um iframe com o chat ao ser clicado.
 * 
 * Uso:
 * <script src="/chat-widget.js" data-chat-url="http://localhost:5173"></script>
 * 
 * Ou via JavaScript:
 * ChatWidget.init({ chatUrl: 'http://localhost:5173', position: 'bottom-right' });
 */
(function(window) {
    'use strict';

    // Configuração padrão
    const DEFAULT_CONFIG = {
        chatUrl: '/chat/', // Chat deployado no Tomcat em webapps/chat/
        // chatUrl: 'http://192.168.10.30:3001', // Servidor Socket.io (desenvolvimento)
        // chatUrl: 'https://seu-dominio.com/chat', // Produção (via nginx)
        position: 'bottom-right',
        buttonText: 'Chat',
        buttonIcon: 'chat',
        zIndex: 9999,
        width: 380,
        height: 600,
        primaryColor: '#25D366',
        secondaryColor: '#128C7E',
        borderRadius: 12,
        showBadge: true,
        autoOpen: false,
        persistState: true
    };

    // Estado do widget
    let state = {
        isOpen: false,
        unreadCount: 0,
        config: { ...DEFAULT_CONFIG }
    };

    // Elementos DOM
    let container = null;
    let button = null;
    let iframe = null;
    let badge = null;

    /**
     * Cria o ícone SVG do chat
     */
    function createChatIcon() {
        return `
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="28" height="28">
                <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 00-3.48-8.413z"/>
            </svg>
        `;
    }

    /**
     * Cria o ícone SVG de fechar
     */
    function createCloseIcon() {
        return `
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="24" height="24">
                <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
            </svg>
        `;
    }

    /**
     * Cria os estilos CSS do widget
     */
    function createStyles() {
        const style = document.createElement('style');
        style.id = 'chat-widget-styles';
        style.textContent = `
            .chat-widget-container {
                position: fixed;
                z-index: ${state.config.zIndex};
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
            }
            
            .chat-widget-button {
                position: fixed;
                bottom: 24px;
                right: 24px;
                width: 60px;
                height: 60px;
                border-radius: 50%;
                background: linear-gradient(135deg, ${state.config.primaryColor} 0%, ${state.config.secondaryColor} 100%);
                border: none;
                cursor: pointer;
                display: flex;
                align-items: center;
                justify-content: center;
                color: white;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15), 0 2px 4px rgba(0, 0, 0, 0.1);
                transition: transform 0.2s ease, box-shadow 0.2s ease;
                z-index: ${state.config.zIndex + 1};
            }
            
            .chat-widget-button:hover {
                transform: scale(1.05);
                box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2), 0 3px 6px rgba(0, 0, 0, 0.15);
            }
            
            .chat-widget-button:active {
                transform: scale(0.95);
            }
            
            .chat-widget-button.open {
                background: #f0f0f0;
                color: #333;
            }
            
            .chat-widget-badge {
                position: absolute;
                top: -4px;
                right: -4px;
                min-width: 20px;
                height: 20px;
                border-radius: 10px;
                background: #ff4757;
                color: white;
                font-size: 11px;
                font-weight: 600;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 0 6px;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
            }
            
            .chat-widget-badge.hidden {
                display: none;
            }
            
            .chat-widget-iframe-container {
                position: fixed;
                bottom: 100px;
                right: 24px;
                width: ${state.config.width}px;
                height: ${state.config.height}px;
                max-height: calc(100vh - 140px);
                border-radius: ${state.config.borderRadius}px;
                overflow: hidden;
                box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2), 0 4px 8px rgba(0, 0, 0, 0.1);
                z-index: ${state.config.zIndex};
                opacity: 0;
                visibility: hidden;
                transform: translateY(20px) scale(0.95);
                transition: opacity 0.3s ease, transform 0.3s ease, visibility 0.3s ease;
            }
            
            .chat-widget-iframe-container.open {
                opacity: 1;
                visibility: visible;
                transform: translateY(0) scale(1);
            }
            
            .chat-widget-iframe {
                width: 100%;
                height: 100%;
                border: none;
                background: white;
            }
            
            .chat-widget-tooltip {
                position: fixed;
                bottom: 40px;
                right: 100px;
                background: white;
                padding: 12px 16px;
                border-radius: 8px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
                font-size: 14px;
                color: #333;
                z-index: ${state.config.zIndex};
                opacity: 0;
                visibility: hidden;
                transform: translateX(10px);
                transition: opacity 0.3s ease, transform 0.3s ease, visibility 0.3s ease;
            }
            
            .chat-widget-tooltip.show {
                opacity: 1;
                visibility: visible;
                transform: translateX(0);
            }
            
            .chat-widget-tooltip::after {
                content: '';
                position: absolute;
                bottom: 12px;
                right: -8px;
                border-width: 8px 0 8px 8px;
                border-style: solid;
                border-color: transparent transparent transparent white;
            }
            
            @media (max-width: 480px) {
                .chat-widget-iframe-container {
                    width: calc(100vw - 32px);
                    height: calc(100vh - 120px);
                    right: 16px;
                    bottom: 100px;
                    max-height: none;
                }
                
                .chat-widget-button {
                    right: 16px;
                    bottom: 16px;
                }
            }
            
            /* Animação de pulse para novas mensagens */
            @keyframes chat-widget-pulse {
                0% {
                    box-shadow: 0 0 0 0 rgba(37, 211, 102, 0.7);
                }
                70% {
                    box-shadow: 0 0 0 15px rgba(37, 211, 102, 0);
                }
                100% {
                    box-shadow: 0 0 0 0 rgba(37, 211, 102, 0);
                }
            }
            
            .chat-widget-button.pulse {
                animation: chat-widget-pulse 2s infinite;
            }
        `;
        document.head.appendChild(style);
    }

    /**
     * Cria o botão flutuante
     */
    function createButton() {
        button = document.createElement('button');
        button.className = 'chat-widget-button';
        button.innerHTML = createChatIcon();
        button.setAttribute('aria-label', 'Abrir chat');
        button.setAttribute('title', 'Chat WhatsApp');
        
        // Badge para contador de mensagens não lidas
        if (state.config.showBadge) {
            badge = document.createElement('span');
            badge.className = 'chat-widget-badge hidden';
            badge.textContent = '0';
            button.appendChild(badge);
        }
        
        button.addEventListener('click', toggleChat);
        document.body.appendChild(button);
        
        // Tooltip de boas-vindas
        showTooltip();
    }

    /**
     * Cria o container do iframe
     */
    function createIframeContainer() {
        container = document.createElement('div');
        container.className = 'chat-widget-iframe-container';
        
        iframe = document.createElement('iframe');
        iframe.className = 'chat-widget-iframe';
        iframe.src = state.config.chatUrl;
        iframe.setAttribute('allow', 'microphone; camera');
        iframe.setAttribute('title', 'Chat WhatsApp');
        
        container.appendChild(iframe);
        document.body.appendChild(container);
    }

    /**
     * Mostra tooltip de boas-vindas
     */
    function showTooltip() {
        const tooltip = document.createElement('div');
        tooltip.className = 'chat-widget-tooltip';
        tooltip.textContent = 'Olá! Precisa de ajuda? Clique aqui para iniciar um chat.';
        document.body.appendChild(tooltip);
        
        // Mostra após 3 segundos
        setTimeout(() => {
            if (!state.isOpen) {
                tooltip.classList.add('show');
            }
        }, 3000);
        
        // Esconde após 8 segundos
        setTimeout(() => {
            tooltip.classList.remove('show');
            setTimeout(() => tooltip.remove(), 300);
        }, 11000);
        
        // Esconde ao abrir o chat
        button.addEventListener('click', () => {
            tooltip.classList.remove('show');
            setTimeout(() => tooltip.remove(), 300);
        }, { once: true });
    }

    /**
     * Alterna o estado do chat (abrir/fechar)
     */
    function toggleChat() {
        state.isOpen = !state.isOpen;
        
        if (state.isOpen) {
            openChat();
        } else {
            closeChat();
        }
        
        // Persiste o estado
        if (state.config.persistState) {
            localStorage.setItem('chat-widget-open', state.isOpen);
        }
    }

    /**
     * Abre o chat
     */
    function openChat() {
        state.isOpen = true;
        container.classList.add('open');
        button.classList.add('open');
        button.innerHTML = createCloseIcon();
        
        // Re-adiciona o badge se existir
        if (badge) {
            button.appendChild(badge);
        }
        
        // Remove animação de pulse
        button.classList.remove('pulse');
        
        // Foca no iframe
        setTimeout(() => iframe.focus(), 100);
    }

    /**
     * Fecha o chat
     */
    function closeChat() {
        state.isOpen = false;
        container.classList.remove('open');
        button.classList.remove('open');
        button.innerHTML = createChatIcon();
        
        // Re-adiciona o badge se existir
        if (badge) {
            button.appendChild(badge);
        }
    }

    /**
     * Atualiza o contador de mensagens não lidas
     */
    function updateBadge(count) {
        state.unreadCount = count;
        
        if (badge) {
            if (count > 0) {
                badge.textContent = count > 99 ? '99+' : count;
                badge.classList.remove('hidden');
                button.classList.add('pulse');
            } else {
                badge.classList.add('hidden');
                button.classList.remove('pulse');
            }
        }
    }

    /**
     * Recebe mensagens do iframe via postMessage
     */
    function setupMessageListener() {
        window.addEventListener('message', (event) => {
            // Verifica a origem da mensagem
            if (!event.origin.startsWith(state.config.chatUrl.replace(/\/$/, ''))) {
                return;
            }
            
            const { type, data } = event.data || {};
            
            switch (type) {
                case 'CHAT_UNREAD_COUNT':
                    updateBadge(data.count);
                    break;
                case 'CHAT_NEW_MESSAGE':
                    if (!state.isOpen) {
                        updateBadge(state.unreadCount + 1);
                    }
                    break;
                case 'CHAT_CLOSE':
                    closeChat();
                    break;
            }
        });
    }

    /**
     * Restaura o estado salvo
     */
    function restoreState() {
        if (state.config.persistState) {
            const wasOpen = localStorage.getItem('chat-widget-open') === 'true';
            if (wasOpen || state.config.autoOpen) {
                setTimeout(() => openChat(), 500);
            }
        }
    }

    /**
     * Inicializa o widget
     */
    function init(config = {}) {
        // Mescla configuração
        state.config = { ...DEFAULT_CONFIG, ...config };
        
        // Verifica se já foi inicializado
        if (document.querySelector('.chat-widget-button')) {
            console.warn('ChatWidget já foi inicializado');
            return;
        }
        
        // Cria os elementos
        createStyles();
        createButton();
        createIframeContainer();
        setupMessageListener();
        restoreState();
        
        console.log('ChatWidget inicializado', state.config);
    }

    /**
     * Destrói o widget
     */
    function destroy() {
        if (button) button.remove();
        if (container) container.remove();
        if (badge) badge.remove();
        
        const styles = document.getElementById('chat-widget-styles');
        if (styles) styles.remove();
        
        state = {
            isOpen: false,
            unreadCount: 0,
            config: { ...DEFAULT_CONFIG }
        };
        
        console.log('ChatWidget destruído');
    }

    /**
     * Atualiza a configuração
     */
    function updateConfig(newConfig) {
        state.config = { ...state.config, ...newConfig };
        
        // Atualiza o src do iframe se a URL mudou
        if (newConfig.chatUrl && iframe) {
            iframe.src = newConfig.chatUrl;
        }
    }

    // API pública
    window.ChatWidget = {
        init,
        destroy,
        open: openChat,
        close: closeChat,
        toggle: toggleChat,
        updateBadge,
        updateConfig,
        getState: () => ({ ...state })
    };

    // Auto-inicialização via atributos data-*
    document.addEventListener('DOMContentLoaded', () => {
        const script = document.querySelector('script[data-chat-url]');
        if (script) {
            const config = {
                chatUrl: script.dataset.chatUrl || DEFAULT_CONFIG.chatUrl,
                position: script.dataset.position || DEFAULT_CONFIG.position,
                primaryColor: script.dataset.primaryColor || DEFAULT_CONFIG.primaryColor,
                secondaryColor: script.dataset.secondaryColor || DEFAULT_CONFIG.secondaryColor,
                width: parseInt(script.dataset.width) || DEFAULT_CONFIG.width,
                height: parseInt(script.dataset.height) || DEFAULT_CONFIG.height,
                autoOpen: script.dataset.autoOpen === 'true',
                showBadge: script.dataset.showBadge !== 'false'
            };
            init(config);
        }
    });

})(window);
