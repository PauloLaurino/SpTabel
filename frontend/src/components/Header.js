/**
 * Header Component - Segue o padrão visual do Selador
 * Sistema de Cálculo de Custas
 */

class Header {
    constructor(props = {}) {
        this.props = {
            systemName: props.systemName || 'SISTEMA CUSTAS',
            systemDescription: props.systemDescription || 'Cálculo de Emolumentos',
            version: props.version || '1.0.0',
            userName: props.userName || 'Usuário',
            userRole: props.userRole || 'Tabelião',
            isOnline: props.isOnline !== undefined ? props.isOnline : true,
            isDatabaseConnected: props.isDatabaseConnected !== undefined ? props.isDatabaseConnected : true,
            currentTime: props.currentTime || new Date().toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })
        };
        
        this.element = null;
        this.timeInterval = null;
    }

    render() {
        const { systemName, systemDescription, version, userName, userRole, isOnline, isDatabaseConnected, currentTime } = this.props;
        
        return `
            <header class="app-header">
                <!-- Header Esquerdo - Logo e Versão -->
                <div class="header-left">
                    <div class="logo-container">
                        <i class="logo-icon fa-solid fa-stamp"></i>
                        <div class="logo-text">
                            <span class="system-name">${systemName}</span>
                            <span class="system-description">${systemDescription}</span>
                        </div>
                    </div>
                    <div class="version-container">
                        <span class="version-badge">v${version}</span>
                    </div>
                </div>

                <!-- Header Centro - Status -->
                <div class="header-center">
                    <div class="status-container">
                        <div class="status-item ${isOnline ? 'online' : ''}">
                            <i class="status-icon fa-solid fa-circle"></i>
                            <span class="status-text">${isOnline ? 'Online' : 'Offline'}</span>
                        </div>
                        <span class="status-separator">|</span>
                        <div class="status-item db ${isDatabaseConnected ? '' : ''}">
                            <i class="status-icon fa-solid fa-database"></i>
                            <span class="status-text">${isDatabaseConnected ? 'DB' : 'DB Offline'}</span>
                        </div>
                        <span class="status-separator">|</span>
                        <div class="status-item time">
                            <i class="status-icon fa-solid fa-clock"></i>
                            <span class="status-text" id="header-time">${currentTime}</span>
                        </div>
                    </div>
                </div>

                <!-- Header Direito - Usuário e Ações -->
                <div class="header-right">
                    <div class="user-container">
                        <i class="user-avatar fa-solid fa-user-circle"></i>
                        <div class="user-details">
                            <span class="user-name">${userName}</span>
                            <span class="user-role">${userRole}</span>
                        </div>
                    </div>
                    <div class="header-actions">
                        <button class="btn-header" title="Configurações" onclick="window.location.href='#config'">
                            <i class="fa-solid fa-gear"></i>
                        </button>
                        <button class="btn-header btn-danger" title="Sair" onclick="window.location.href='#logout'">
                            <i class="fa-solid fa-right-from-bracket"></i>
                        </button>
                    </div>
                </div>
            </header>
        `;
    }

    mount(container) {
        if (typeof container === 'string') {
            container = document.querySelector(container);
        }
        
        if (!container) {
            console.error('Header: Container não encontrado');
            return;
        }
        
        container.innerHTML = this.render();
        this.element = container.querySelector('.app-header');
        
        // Iniciar atualização do relógio
        this.startTimeUpdate();
    }

    startTimeUpdate() {
        if (this.timeInterval) {
            clearInterval(this.timeInterval);
        }
        
        this.timeInterval = setInterval(() => {
            const timeElement = document.getElementById('header-time');
            if (timeElement) {
                timeElement.textContent = new Date().toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
            }
        }, 1000);
    }

    updateProps(newProps) {
        this.props = { ...this.props, ...newProps };
        if (this.element) {
            this.element.innerHTML = this.render();
        }
    }

    destroy() {
        if (this.timeInterval) {
            clearInterval(this.timeInterval);
        }
        if (this.element) {
            this.element.remove();
        }
    }
}

// Exportar para uso global
window.Header = Header;
