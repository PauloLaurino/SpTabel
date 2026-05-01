import React, { useState } from 'react';
import { FileText, Search, XCircle, List, Plus, Send, Loader2, AlertCircle, CheckCircle } from 'lucide-react';
import { nfseApi } from '../../services/nfseApi';
import { NotasCabDTO, NfseResponseDTO } from '../../types/nfse';
import './NfseTabs.css';

type TabType = 'emitir' | 'consultar' | 'cancelar' | 'listar';

export const NfseTabs: React.FC = () => {
  const [activeTab, setActiveTab] = useState<TabType>('listar');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  
  // Filters state
  const [dataInicio, setDataInicio] = useState('');
  const [dataFim, setDataFim] = useState('');
  const [termoBusca, setTermoBusca] = useState('');
  const [numeroNota, setNumeroNota] = useState('');
  const [cnpjTomador, setCnpjTomador] = useState('');
  
  // Data state
  const [notas, setNotas] = useState<NotasCabDTO[]>([]);
  const [notaSelecionada, setNotaSelecionada] = useState<NotasCabDTO | null>(null);
  const [notasSelecionadas, setNotasSelecionadas] = useState<Set<number>>(new Set());
  
  // Emit form state
  const [emitForm, setEmitForm] = useState({
    tomadorNome: '',
    tomadorCnpjCpf: '',
    tomadorEmail: '',
    valorServico: 0,
    discriminacao: '',
    codigoServico: '',
  });

  const tabs = [
    { id: 'emitir' as TabType, label: 'Emitir Nota', icon: Plus },
    { id: 'consultar' as TabType, label: 'Consultar NFS-e', icon: Search },
    { id: 'cancelar' as TabType, label: 'Cancelar NFS-e', icon: XCircle },
    { id: 'listar' as TabType, label: 'Listar Notas', icon: List },
  ];

  const formatCurrency = (value?: number): string => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(value || 0);
  };

  const formatDate = (date?: string): string => {
    if (!date) return '-';
    return new Date(date).toLocaleDateString('pt-BR');
  };

  const getSituacaoBadge = (situacao?: string): string => {
    switch (situacao) {
      case 'P': return 'Pendente';
      case 'E': return 'Emitida';
      case 'C': return 'Cancelada';
      case 'X': return 'Erro';
      default: return 'Desconhecido';
    }
  };

  const getSituacaoClass = (situacao?: string): string => {
    switch (situacao) {
      case 'P': return 'badge-pending';
      case 'E': return 'badge-success';
      case 'C': return 'badge-error';
      case 'X': return 'badge-warning';
      default: return 'badge-pending';
    }
  };

  const handleBuscarNotas = async () => {
    if (!dataInicio || !dataFim) {
      setError('Por favor, selecione o período de busca.');
      return;
    }
    
    setLoading(true);
    setError(null);
    setSuccess(null);
    
    try {
      const result = await nfseApi.buscarPorPeriodo({
        dataInicio,
        dataFim,
      });
      setNotas(result);
    } catch (err) {
      setError('Erro ao buscar notas. Tente novamente.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleBuscarGlobal = async () => {
    if (!termoBusca) {
      setError('Por favor, insira um termo de busca.');
      return;
    }
    
    setLoading(true);
    setError(null);
    
    try {
      const result = await nfseApi.buscarGlobal({
        termo: termoBusca,
        pagina: 0,
        tamanho: 20,
      });
      setNotas(result.content);
    } catch (err) {
      setError('Erro ao buscar notas. Tente novamente.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleEmitirNota = async () => {
    if (!emitForm.tomadorNome || !emitForm.valorServico) {
      setError('Preencha os campos obrigatórios: Nome do Tomador e Valor do Serviço.');
      return;
    }
    
    setLoading(true);
    setError(null);
    setSuccess(null);
    
    try {
      const novaNota = await nfseApi.criarNota({
        tomadorNome: emitForm.tomadorNome,
        tomadorCnpjCpf: emitForm.tomadorCnpjCpf,
        tomadorEmail: emitForm.tomadorEmail,
        valorServico: emitForm.valorServico,
        discriminacao: emitForm.discriminacao,
        codigoServico: emitForm.codigoServico,
        situacao: 'P',
      });
      
      setSuccess(`Nota criada com sucesso! ID: ${novaNota.id}`);
      setNotas((prev) => [novaNota, ...prev]);
      setEmitForm({
        tomadorNome: '',
        tomadorCnpjCpf: '',
        tomadorEmail: '',
        valorServico: 0,
        discriminacao: '',
        codigoServico: '',
      });
    } catch (err) {
      setError('Erro ao criar nota. Tente novamente.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleEmitirNfse = async (id: number) => {
    setLoading(true);
    setError(null);
    setSuccess(null);
    
    try {
      const result: NfseResponseDTO = await nfseApi.emitirNota(id);
      if (result.sucesso) {
        setSuccess(`NFS-e emitida com sucesso! Número: ${result.numeroNfse}`);
        handleBuscarNotas();
      } else {
        setError(result.mensagem || 'Erro ao emitir NFS-e');
      }
    } catch (err) {
      setError('Erro ao emitir NFS-e. Tente novamente.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCancelarNfse = async () => {
    if (!notaSelecionada?.id) {
      setError('Selecione uma nota para cancelar.');
      return;
    }
    
    const motivo = prompt('Digite o motivo do cancelamento:');
    if (!motivo) return;
    
    setLoading(true);
    setError(null);
    setSuccess(null);
    
    try {
      const result: NfseResponseDTO = await nfseApi.cancelarNota(notaSelecionada.id, motivo);
      if (result.sucesso) {
        setSuccess('NFS-e cancelada com sucesso!');
        handleBuscarNotas();
      } else {
        setError(result.mensagem || 'Erro ao cancelar NFS-e');
      }
    } catch (err) {
      setError('Erro ao cancelar NFS-e. Tente novamente.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleConsultarNfse = async () => {
    if (!numeroNota) {
      setError('Informe o número da NFS-e para consultar.');
      return;
    }
    
    setLoading(true);
    setError(null);
    setSuccess(null);
    
    try {
      const result: NfseResponseDTO = await nfseApi.consultarNfsePorNumero(numeroNota, cnpjTomador || undefined);
      if (result.sucesso) {
        setSuccess(`NFS-e encontrada! Status: ${result.mensagem}`);
      } else {
        setError(result.mensagem || 'NFS-e não encontrada');
      }
    } catch (err) {
      setError('Erro ao consultar NFS-e. Tente novamente.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const toggleNotaSelecao = (id: number) => {
    const newSelection = new Set(notasSelecionadas);
    if (newSelection.has(id)) {
      newSelection.delete(id);
    } else {
      newSelection.add(id);
    }
    setNotasSelecionadas(newSelection);
  };

  const renderTabContent = () => {
    switch (activeTab) {
      case 'emitir':
        return (
          <div className="tab-content animate-fade-in">
            <div className="form-section">
              <h3>Dados do Tomador</h3>
              <div className="form-grid">
                <div className="form-group">
                  <label>Nome/Razão Social *</label>
                  <input
                    type="text"
                    value={emitForm.tomadorNome}
                    onChange={(e) => setEmitForm({ ...emitForm, tomadorNome: e.target.value })}
                    placeholder="Nome completo ou razão social"
                  />
                </div>
                <div className="form-group">
                  <label>CNPJ/CPF</label>
                  <input
                    type="text"
                    value={emitForm.tomadorCnpjCpf}
                    onChange={(e) => setEmitForm({ ...emitForm, tomadorCnpjCpf: e.target.value })}
                    placeholder="XX.XXX.XXX/XXXX-XX ou XXX.XXX.XXX-XX"
                  />
                </div>
                <div className="form-group">
                  <label>E-mail</label>
                  <input
                    type="email"
                    value={emitForm.tomadorEmail}
                    onChange={(e) => setEmitForm({ ...emitForm, tomadorEmail: e.target.value })}
                    placeholder="email@exemplo.com"
                  />
                </div>
              </div>
            </div>
            
            <div className="form-section">
              <h3>Dados do Serviço</h3>
              <div className="form-grid">
                <div className="form-group">
                  <label>Valor do Serviço *</label>
                  <input
                    type="number"
                    step="0.01"
                    value={emitForm.valorServico || ''}
                    onChange={(e) => setEmitForm({ ...emitForm, valorServico: parseFloat(e.target.value) || 0 })}
                    placeholder="0,00"
                  />
                </div>
                <div className="form-group">
                  <label>Código do Serviço</label>
                  <input
                    type="text"
                    value={emitForm.codigoServico}
                    onChange={(e) => setEmitForm({ ...emitForm, codigoServico: e.target.value })}
                    placeholder="Código municipal"
                  />
                </div>
                <div className="form-group full-width">
                  <label>Discriminação do Serviço</label>
                  <textarea
                    value={emitForm.discriminacao}
                    onChange={(e) => setEmitForm({ ...emitForm, discriminacao: e.target.value })}
                    placeholder="Descrição detalhada dos serviços prestados"
                    rows={4}
                  />
                </div>
              </div>
            </div>
            
            <div className="form-actions">
              <button className="btn-primary" onClick={handleEmitirNota} disabled={loading}>
                {loading ? <Loader2 className="animate-spin" size={18} /> : <Plus size={18} />}
                Criar Nota
              </button>
            </div>
          </div>
        );
        
      case 'consultar':
        return (
          <div className="tab-content animate-fade-in">
            <div className="form-section">
              <h3>Consulta de NFS-e</h3>
              <div className="form-grid">
                <div className="form-group">
                  <label>Número da NFS-e *</label>
                  <input
                    type="text"
                    value={numeroNota}
                    onChange={(e) => setNumeroNota(e.target.value)}
                    placeholder="Número da nota fiscal"
                  />
                </div>
                <div className="form-group">
                  <label>CNPJ do Tomador (opcional)</label>
                  <input
                    type="text"
                    value={cnpjTomador}
                    onChange={(e) => setCnpjTomador(e.target.value)}
                    placeholder="CNPJ do tomador"
                  />
                </div>
              </div>
            </div>
            
            <div className="form-actions">
              <button className="btn-primary" onClick={handleConsultarNfse} disabled={loading}>
                {loading ? <Loader2 className="animate-spin" size={18} /> : <Search size={18} />}
                Consultar
              </button>
            </div>
          </div>
        );
        
      case 'cancelar':
        return (
          <div className="tab-content animate-fade-in">
            <div className="form-section">
              <h3>Cancelamento de NFS-e</h3>
              <p className="form-description">
                Selecione uma nota da lista abaixo para cancelar.
              </p>
              
              {notaSelecionada && (
                <div className="selected-note">
                  <h4>Nota Selecionada</h4>
                  <div className="note-details">
                    <p><strong>Número:</strong> {notaSelecionada.numeroNota || notaSelecionada.id}</p>
                    <p><strong>Tomador:</strong> {notaSelecionada.tomadorNome}</p>
                    <p><strong>Valor:</strong> {formatCurrency(notaSelecionada.valorTotal)}</p>
                    <p><strong>Situação:</strong> <span className={`badge ${getSituacaoClass(notaSelecionada.situacao)}`}>{getSituacaoBadge(notaSelecionada.situacao)}</span></p>
                  </div>
                </div>
              )}
              
              <div className="form-actions">
                <button 
                  className="btn-danger" 
                  onClick={handleCancelarNfse} 
                  disabled={loading || !notaSelecionada}
                >
                  {loading ? <Loader2 className="animate-spin" size={18} /> : <XCircle size={18} />}
                  Cancelar NFS-e
                </button>
              </div>
            </div>
          </div>
        );
        
      case 'listar':
      default:
        return (
          <div className="tab-content animate-fade-in">
            <div className="filter-section">
              <h3>Filtros de Busca</h3>
              <div className="filter-grid">
                <div className="filter-group">
                  <label>Período</label>
                  <div className="date-range">
                    <input
                      type="date"
                      value={dataInicio}
                      onChange={(e) => setDataInicio(e.target.value)}
                      placeholder="Data início"
                    />
                    <span>até</span>
                    <input
                      type="date"
                      value={dataFim}
                      onChange={(e) => setDataFim(e.target.value)}
                      placeholder="Data fim"
                    />
                  </div>
                </div>
                <div className="filter-group">
                  <label>Busca Global</label>
                  <div className="search-input">
                    <input
                      type="text"
                      value={termoBusca}
                      onChange={(e) => setTermoBusca(e.target.value)}
                      placeholder="Buscar por tomador, número, etc."
                    />
                    <button className="btn-secondary" onClick={handleBuscarGlobal}>
                      <Search size={16} />
                    </button>
                  </div>
                </div>
                <div className="filter-actions">
                  <button className="btn-primary" onClick={handleBuscarNotas}>
                    <Search size={16} />
                    Buscar
                  </button>
                </div>
              </div>
            </div>
            
            <div className="results-section">
              <div className="results-header">
                <h3>Resultados ({notas.length})</h3>
                {notasSelecionadas.size > 0 && (
                  <button className="btn-primary">
                    <Send size={16} />
                    Emitir {notasSelecionadas.size} Selecionadas
                  </button>
                )}
              </div>
              
              {loading ? (
                <div className="loading-state">
                  <Loader2 className="animate-spin" size={32} />
                  <p>Carregando notas...</p>
                </div>
              ) : notas.length === 0 ? (
                <div className="empty-state">
                  <FileText size={48} strokeWidth={1} />
                  <p>Nenhuma nota encontrada no período informado.</p>
                </div>
              ) : (
                <div className="table-container">
                  <table className="table">
                    <thead>
                      <tr>
                        <th style={{ width: '40px' }}></th>
                        <th>Número</th>
                        <th>Data Emissão</th>
                        <th>Tomador</th>
                        <th>Valor</th>
                        <th>Situação</th>
                        <th>Ações</th>
                      </tr>
                    </thead>
                    <tbody>
                      {notas.map((nota) => (
                        <tr 
                          key={nota.id}
                          className={notaSelecionada?.id === nota.id ? 'selected' : ''}
                          onClick={() => setNotaSelecionada(nota)}
                        >
                          <td>
                            <input
                              type="checkbox"
                              checked={notasSelecionadas.has(nota.id || 0)}
                              onChange={() => toggleNotaSelecao(nota.id || 0)}
                              onClick={(e) => e.stopPropagation()}
                            />
                          </td>
                          <td className="font-mono">{nota.numeroNota || nota.id}</td>
                          <td>{formatDate(nota.dataEmissao)}</td>
                          <td>{nota.tomadorNome || '-'}</td>
                          <td className="text-right">{formatCurrency(nota.valorTotal)}</td>
                          <td>
                            <span className={`badge ${getSituacaoClass(nota.situacao)}`}>
                              {getSituacaoBadge(nota.situacao)}
                            </span>
                          </td>
                          <td>
                            <div className="action-buttons">
                              {nota.situacao === 'P' && (
                                <button
                                  className="btn-ghost btn-sm"
                                  onClick={(e) => {
                                    e.stopPropagation();
                                    handleEmitirNfse(nota.id!);
                                  }}
                                  title="Emitir NFS-e"
                                >
                                  <Send size={14} />
                                </button>
                              )}
                              {nota.situacao === 'E' && (
                                <button
                                  className="btn-ghost btn-sm"
                                  onClick={(e) => {
                                    e.stopPropagation();
                                    setNotaSelecionada(nota);
                                    setActiveTab('cancelar');
                                  }}
                                  title="Cancelar"
                                >
                                  <XCircle size={14} />
                                </button>
                              )}
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>
        );
    }
  };

  return (
    <div className="nfse-tabs-container">
      <div className="tabs-header">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            className={`tab-button ${activeTab === tab.id ? 'active' : ''}`}
            onClick={() => setActiveTab(tab.id)}
          >
            <tab.icon size={18} />
            <span>{tab.label}</span>
          </button>
        ))}
      </div>
      
      <div className="tabs-content">
        {(error || success) && (
          <div className={`alert ${error ? 'alert-error' : 'alert-success'}`}>
            {error ? <AlertCircle size={18} /> : <CheckCircle size={18} />}
            <span>{error || success}</span>
            <button className="alert-close" onClick={() => { setError(null); setSuccess(null); }}>
              ×
            </button>
          </div>
        )}
        
        {renderTabContent()}
      </div>
    </div>
  );
};

export default NfseTabs;