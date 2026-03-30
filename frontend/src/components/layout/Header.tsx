import React from 'react';

/**
 * Header Component - Segue o padrão visual do Selador
 */
interface HeaderProps {
  systemName?: string;
  systemDescription?: string;
  version?: string;
  userName?: string;
  userRole?: string;
  isOnline?: boolean;
  isDatabaseConnected?: boolean;
  currentTime?: string;
}

export const Header: React.FC<HeaderProps> = ({
  systemName = 'SISTEMA CUSTAS',
  systemDescription = 'Cálculo de Emolumentos',
  version = '1.0.0',
  userName = 'Usuário',
  userRole = 'Tabelião',
  isOnline = true,
  isDatabaseConnected = true,
  currentTime = new Date().toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' }),
}) => {
  return (
    <header className="app-header">
      {/* Header Esquerdo - Logo e Versão */}
      <div className="header-left">
        <div className="logo-container">
          <i className="logo-icon fa-solid fa-stamp"></i>
          <div className="logo-text">
            <span className="system-name">{systemName}</span>
            <span className="system-description">{systemDescription}</span>
          </div>
        </div>
        <div className="version-container">
          <span className="version-badge">v{version}</span>
        </div>
      </div>

      {/* Header Centro - Status */}
      <div className="header-center">
        <div className="status-container">
          <div className={`status-item ${isOnline ? 'online' : ''}`}>
            <i className="status-icon fa-solid fa-circle"></i>
            <span className="status-text">{isOnline ? 'Online' : 'Offline'}</span>
          </div>
          <span className="status-separator">|</span>
          <div className={`status-item db ${isDatabaseConnected ? '' : ''}`}>
            <i className="status-icon fa-solid fa-database"></i>
            <span className="status-text">{isDatabaseConnected ? 'DB' : 'DB Offline'}</span>
          </div>
          <span className="status-separator">|</span>
          <div className="status-item time">
            <i className="status-icon fa-solid fa-clock"></i>
            <span className="status-text">{currentTime}</span>
          </div>
        </div>
      </div>

      {/* Header Direito - Usuário e Ações */}
      <div className="header-right">
        <div className="user-container">
          <i className="user-avatar fa-solid fa-user-circle"></i>
          <div className="user-details">
            <span className="user-name">{userName}</span>
            <span className="user-role">{userRole}</span>
          </div>
        </div>
        <div className="header-actions">
          <button className="btn-header" title="Configurações">
            <i className="fa-solid fa-gear"></i>
          </button>
          <button className="btn-header btn-danger" title="Sair">
            <i className="fa-solid fa-right-from-bracket"></i>
          </button>
        </div>
      </div>
    </header>
  );
};
