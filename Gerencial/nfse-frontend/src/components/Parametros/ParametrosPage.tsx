import { useState, useEffect } from 'react';
import { Save, RotateCcw, History, Loader2, AlertCircle } from 'lucide-react';
import { parametrizeApi } from '../../services/parametrosApi';
import { ParametrosDTO, LogParametros } from '../../types/parametros';
import './ParametrosPage.css';

type TabType = 'cadastro' | 'gerencial' | 'impostos' | 'funarpen' | 'historico';

export const ParametrosPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<TabType>('cadastro');
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  
  const [parametros, setParametros] = useState<ParametrosDTO | null>(null);
  const [parametrosOriginais, setParametrosOriginais] = useState<ParametrosDTO | null>(null);
  const [historico, setHistorico] = useState<LogParametros[]>([]);
  const [paginaHistorico, setPaginaHistorico] = useState(0);
  
  // Carregar parâmetros ao iniciar
  useEffect(() => {
    carregarParametros();
  }, []);
  
  const carregarParametros = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await parametrizeApi.buscarParametros();
      setParametros(data);
      setParametrosOriginais(JSON.parse(JSON.stringify(data)));
    } catch (err) {
      setError('Erro ao carregar parâmetros');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };
  
  const carregarHistorico = async (pagina: number = 0) => {
    try {
      const response = await parametrizeApi.buscarHistorico(pagina, 10);
      setHistorico(response.content);
      setPaginaHistorico(pagina);
    } catch (err) {
      console.error('Erro ao carregar histórico', err);
    }
  };
  
  const handleSalvar = async () => {
    if (!parametros) return;
    
    setSaving(true);
    setError(null);
    setSuccess(null);
    
    try {
      await parametrizeApi.salvarParametros(parametros);
      setSuccess('Parâmetros salvos com sucesso!');
      setParametrosOriginais(JSON.parse(JSON.stringify(parametros)));
      
      // Recarregar para ver as alterações
      await carregarParametros();
    } catch (err) {
      setError('Erro ao salvar parâmetros');
      console.error(err);
    } finally {
      setSaving(false);
    }
  };
  
  const handleReset = () => {
    if (parametrosOriginais) {
      setParametros(JSON.parse(JSON.stringify(parametrosOriginais)));
      setSuccess(null);
      setError(null);
    }
  };
  
  const hasChanges = JSON.stringify(parametros) !== JSON.stringify(parametrosOriginais);
  
  const tabs = [
    { id: 'cadastro' as TabType, label: 'Cadastro' },
    { id: 'gerencial' as TabType, label: 'Gerencial' },
    { id: 'impostos' as TabType, label: 'Impostos (CCN)' },
    { id: 'funarpen' as TabType, label: 'Funarpen' },
    { id: 'historico' as TabType, label: 'Histórico', icon: History },
  ];
  
  const formatDateTime = (date?: string): string => {
    if (!date) return '-';
    return new Date(date).toLocaleString('pt-BR');
  };
  
  const renderCampo = (nome: string, label: string, tipo: 'text' | 'number' | 'date' | 'checkbox' | 'select', options?: string[]) => {
    const valor = parametros ? (parametros as any)[nome] : '';
    
    if (tipo === 'checkbox') {
      return (
        <div className="form-group-checkbox" key={nome}>
          <label>
            <input
              type="checkbox"
              checked={valor || false}
              onChange={(e) => setParametros({ ...parametros!, [nome]: e.target.checked })}
            />
            {label}
          </label>
        </div>
      );
    }
    
    if (tipo === 'select' && options) {
      return (
        <div className="form-group" key={nome}>
          <label htmlFor={nome}>{label}</label>
          <select
            id={nome}
            value={valor || ''}
            onChange={(e) => setParametros({ ...parametros!, [nome]: e.target.value })}
          >
            <option value="">Selecione...</option>
            {options.map(opt => (
              <option key={opt} value={opt}>{opt}</option>
            ))}
          </select>
        </div>
      );
    }
    
    return (
      <div className="form-group" key={nome}>
        <label htmlFor={nome}>{label}</label>
        <input
          type={tipo}
          id={nome}
          value={valor || ''}
          onChange={(e) => {
            const value = tipo === 'number' ? parseFloat(e.target.value) || 0 : e.target.value;
            setParametros({ ...parametros!, [nome]: value });
          }}
          step={tipo === 'number' ? '0.0001' : undefined}
        />
      </div>
    );
  };
  
  const renderTabCadastro = () => (
    <div className="tab-content">
      <h3>Dados do Cartório</h3>
      <div className="form-grid">
        {renderCampo('codigoPar', 'Código', 'number')}
        {renderCampo('docPar', 'Documento', 'text')}
        {renderCampo('nomeTabeliao', 'Nome do Tabelião', 'text')}
        {renderCampo('codigoTabeliao', 'Código Tabelião', 'text')}
        {renderCampo('codigoCartorio', 'Código Cartório', 'text')}
        {renderCampo('nomeCartorio', 'Nome do Cartório', 'text')}
        {renderCampo('cnpjCartorio', 'CNPJ', 'text')}
        {renderCampo('inscricaoEstadual', 'Inscrição Estadual', 'text')}
        {renderCampo('enderecoCartorio', 'Endereço', 'text')}
        {renderCampo('telefoneCartorio', 'Telefone', 'text')}
        {renderCampo('emailCartorio', 'Email', 'text')}
        {renderCampo('ativa', 'Ativo', 'select', ['S', 'N'])}
      </div>
    </div>
  );
  
  const renderTabGerencial = () => (
    <div className="tab-content">
      <h3>Configurações Gerenciais</h3>
      <div className="form-grid">
        {renderCampo('dtVersaoFunarpen', 'Data Versão Funarpen', 'date')}
        {renderCampo('obsFunarpen', 'Observações Funarpen', 'text')}
      </div>
    </div>
  );
  
  const renderTabImpostos = () => (
    <div className="tab-content">
      <h3>Impostos - Reforma Tributária (CCN)</h3>
      <div className="form-grid">
        <div className="form-section">
          <h4>CBS - Contribuição sobre Bens e Serviços</h4>
          {renderCampo('cbsPar', 'Alíquota CBS (%)', 'number')}
          {renderCampo('cbsAtivoPar', 'CBS Ativo', 'checkbox')}
          {renderCampo('cbsDeduzirPar', 'Valor Dedução CBS', 'number')}
        </div>
        
        <div className="form-section">
          <h4>IBS - Imposto sobre Bens e Serviços</h4>
          {renderCampo('ibsPar', 'Alíquota IBS (%)', 'number')}
          {renderCampo('ibsAtivoPar', 'IBS Ativo', 'checkbox')}
          {renderCampo('ibsDeduzirPar', 'Valor Dedução IBS', 'number')}
        </div>
        
        <div className="form-section">
          <h4>PIS/COFINS/CSLL/IRPJ</h4>
          {renderCampo('pisPar', 'Alíquota PIS (%)', 'number')}
          {renderCampo('cofinsPar', 'Alíquota COFINS (%)', 'number')}
          {renderCampo('csllPar', 'Alíquota CSLL (%)', 'number')}
          {renderCampo('irpjPar', 'Alíquota IRPJ (%)', 'number')}
          {renderCampo('irpjAdicPar', 'Alíquota IRPJ Adicional (%)', 'number')}
        </div>
        
        <div className="form-section">
          <h4>CPP - Contribuição Patronal</h4>
          {renderCampo('cppPar', 'Alíquota CPP (%)', 'number')}
          {renderCampo('cppAtivoPar', 'CPP Ativo', 'checkbox')}
        </div>
      </div>
    </div>
  );
  
  const renderTabFunarpen = () => (
    <div className="tab-content">
      <h3>Configurações Funarpen</h3>
      <div className="form-grid">
        {renderCampo('faseReformaPar', 'Fase Reforma Tributária', 'select', 
          ['NAO_INICIADA', 'TRANSICAO', 'CONVERGENCIA', 'PLENA'])}
        {renderCampo('cbsAtivoPar', 'CBS Ativo', 'checkbox')}
        {renderCampo('ibsAtivoPar', 'IBS Ativo', 'checkbox')}
        {renderCampo('cppAtivoPar', 'CPP Ativo', 'checkbox')}
      </div>
    </div>
  );
  
  const renderTabHistorico = () => (
    <div className="tab-content">
      <h3>Histórico de Alterações</h3>
      {historico.length === 0 ? (
        <p className="no-data">Nenhum registro encontrado</p>
      ) : (
        <table className="historico-table">
          <thead>
            <tr>
              <th>Data</th>
              <th>Campo</th>
              <th>Valor Anterior</th>
              <th>Novo Valor</th>
              <th>Usuário</th>
              <th>IP</th>
            </tr>
          </thead>
          <tbody>
            {historico.map((item, index) => (
              <tr key={index}>
                <td>{formatDateTime(item.dataAlteracao)}</td>
                <td>{item.nomeCampo}</td>
                <td>{item.valorAnterior || '-'}</td>
                <td>{item.valorNovo || '-'}</td>
                <td>{item.usuarioAlteracao}</td>
                <td>{item.ipOrigem}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      <button 
        className="btn btn-secondary"
        onClick={() => carregarHistorico(paginaHistorico + 1)}
      >
        Carregar mais
      </button>
    </div>
  );
  
  if (loading) {
    return (
      <div className="parametros-page loading">
        <Loader2 className="spin" size={48} />
        <p>Carregando parâmetros...</p>
      </div>
    );
  }
  
  return (
    <div className="parametros-page">
      <div className="parametros-header">
        <h2>Parâmetros do Sistema</h2>
        <div className="header-actions">
          {hasChanges && (
            <button className="btn btn-outline" onClick={handleReset}>
              <RotateCcw size={16} />
              Restaurar
            </button>
          )}
          <button 
            className="btn btn-primary" 
            onClick={handleSalvar}
            disabled={saving || !hasChanges}
          >
            {saving ? <Loader2 className="spin" size={16} /> : <Save size={16} />}
            Salvar
          </button>
        </div>
      </div>
      
      {error && (
        <div className="alert alert-error">
          <AlertCircle size={16} />
          {error}
        </div>
      )}
      
      {success && (
        <div className="alert alert-success">
          {success}
        </div>
      )}
      
      <div className="tabs">
        {tabs.map(tab => (
          <button
            key={tab.id}
            className={`tab ${activeTab === tab.id ? 'active' : ''}`}
            onClick={() => {
              setActiveTab(tab.id);
              if (tab.id === 'historico') {
                carregarHistorico(0);
              }
            }}
          >
            {tab.icon && <tab.icon size={16} />}
            {tab.label}
          </button>
        ))}
      </div>
      
      <div className="tab-panel">
        {activeTab === 'cadastro' && renderTabCadastro()}
        {activeTab === 'gerencial' && renderTabGerencial()}
        {activeTab === 'impostos' && renderTabImpostos()}
        {activeTab === 'funarpen' && renderTabFunarpen()}
        {activeTab === 'historico' && renderTabHistorico()}
      </div>
    </div>
  );
};

export default ParametrosPage;