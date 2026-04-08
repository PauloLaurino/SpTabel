import React from 'react';
import { FileText } from 'lucide-react';
import './Header.css';

interface HeaderProps {
  systemName?: string;
  systemDescription?: string;
  version?: string;
  userName?: string;
  userRole?: string;
  isOnline?: boolean;
  isDatabaseConnected?: boolean;
}

export const Header: React.FC<HeaderProps> = ({
  systemName = 'NFS-E',
  systemDescription = 'Emissor de Notas Fiscais',
  version = '1.0.0',
  userName = 'Usuário',
  userRole = 'Operador',
  isOnline = true,
  isDatabaseConnected = true,
}) => {
  return (
    <header className="nfse-header">
      <div className="header-left">
        <div className="logo-container">
          <div className="logo-icon">
            <FileText size={24} />
          </div>
          <div className="logo-text">
            <span className="system-name">{systemName}</span>
            <span className="system-description">{systemDescription}</span>
          </div>
        </div>
        <div className="version-container">
          <span className="version-badge">v{version}</span>
        </div>
      </div>

      <div className="header-center">
        <div className="status-container">
          <div className={`status-item ${isOnline ? 'online' : 'offline'}`}>
            <span className="status-dot"></span>
            <span className="status-text">{isOnline ? 'Online' : 'Offline'}</span>
          </div>
          <span className="status-separator">|</span>
          <div className={`status-item ${isDatabaseConnected ? 'online' : 'offline'}`}>
            <span className="status-dot"></span>
            <span className="status-text">
              {isDatabaseConnected ? 'BD Conectado' : 'BD Offline'}
            </span>
          </div>
        </div>
      </div>

      <div className="header-right">
        <div className="user-info">
          <div className="user-avatar">
            <span>{userName.charAt(0).toUpperCase()}</span>
          </div>
          <div className="user-details">
            <span className="user-name">{userName}</span>
            <span className="user-role">{userRole}</span>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;