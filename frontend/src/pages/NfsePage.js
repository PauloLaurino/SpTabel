/**
 * Página principal de NFS-e
 * Com abas: Emitir Nota, Consultar NFS-e, Cancelar NFS-e, Listar Notas
 * 
 * @author Seprocom
 */

import React, { useState, useEffect } from 'react';
import nfseApi from '../services/nfseApi';
import './NfsePage.css';

const NfsePage = () => {
  const [abaAtiva, setAbaAtiva] = useState('emitir');
  const [notas, setNotas] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [pagina, setPagina] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [notaSelecionada, setNotaSelecionada] = useState(null);
  
  // Filtros
  const [filtroDataInicio, setFiltroDataInicio] = useState('');
  const [filtroDataFim, setFiltroDataFim] = useState('');
  const [filtroTermo, setFiltroTermo] = useState('');
  const [filtroSituacao, setFiltroSituacao] = useState('');
  
  // Formulário de emissão
  const [formNota, setFormNota] = useState({
    tomadorCnpjCpf: '',
    tomadorNome: '',
    tomadorEndereco: '',
    tomadorBairro: '',
    tomadorCidade: '',
    tomadorUf: '',
    tomadorCep: '',
    tomadorEmail: '',
    tomadorTelefone: '',
    valorServico: '',
    discriminacao: '',
    codigoServico: '',
    aliquota: '',
  });
  
  // Formulário de cancelamento
  const [motivoCancelamento, setMotivoCancelamento] = useState('');
  
  // Consulta NFS-e
  const [numeroConsulta, setNumeroConsulta] = useState('');
  const [resultadoConsulta, setResultadoConsulta] = useState(null);

  useEffect(() => {
    carregarNotas();
  }, [pagina, filtroSituacao]);

  const carregarNotas = async () => {
    setLoading(true);
    setError(null);
    try {
      let dados;
      if (filtroTermo) {
        dados = await nfseApi.buscarNotas(filtroTermo, pagina);
      } else if (filtroDataInicio && filtroDataFim) {
        dados = await nfseApi.buscarNotasPorPeriodo(filtroDataInicio, filtroDataFim);
      } else {
        dados = await nfseApi.listarNotas(pagina);
      }
      setNotas(dados.content || []);
      setTotalPaginas(dados.totalPages || 0);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmitNota = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const notaData = {
        ...formNota,
        valorServico: parseFloat(formNota.valorServico) || 0,
        aliquota: parseFloat(formNota.aliquota) || 0,
        dataEmissao: new Date().toISOString().split('T')[0],
        situacao: 'P',
      };
      const novaNota = await nfseApi.criarNota(notaData);
      setNotas([novaNota, ...notas]);
      setFormNota({
        tomadorCnpjCpf: '',
        tomadorNome: '',
        tomadorEndereco: '',
        tomadorBairro: '',
        tomadorCidade: '',
        tomadorUf: '',
        tomadorCep: '',
        tomadorEmail: '',
        tomadorTelefone: '',
        valorServico: '',
        discriminacao: '',
        codigoServico: '',
        aliquota: '',
      });
      alert('Nota criada com sucesso!');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleEmitirNota = async (id) => {
    if (!confirm('Deseja emitir esta NFS-e?')) return;
    
    setLoading(true);
    setError(null);
    try {
      const resultado = await nfseApi.emitirNota(id);
      alert(resultado.mensagem || 'NFS-e emitida com sucesso!');
      carregarNotas();
    } catch (err) {
      setError(err.message);
      alert('Erro ao emitir: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleCancelarNota = async (id) => {
    if (!motivoCancelamento) {
      alert('Informe o motivo do cancelamento');
      return;
    }
    
    if (!confirm('Confirma o cancelamento desta NFS-e?')) return;
    
    setLoading(true);
    setError(null);
    try {
      const resultado = await nfseApi.cancelarNota(id, motivoCancelamento);
      alert(resultado.mensagem || 'NFS-e cancelada com sucesso!');
      setMotivoCancelamento('');
      setNotaSelecionada(null);
      carregarNotas();
    } catch (err) {
      setError(err.message);
      alert('Erro ao cancelar: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleExcluirNota = async (id) => {
    if (!confirm('Confirma a exclusão desta nota?')) return;
    
    setLoading(true);
    setError(null);
    try {
      await nfseApi.excluirNota(id);
      alert('Nota excluída com sucesso!');
      carregarNotas();
    } catch (err) {
      setError(err.message);
      alert('Erro ao excluir: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleConsultarNfse = async () => {
    if (!numeroConsulta) {
      alert('Informe o número da NFS-e');
      return;
    }
    
    setLoading(true);
    setError(null);
    try {
      const resultado = await nfseApi.consultarNfse(numeroConsulta);
      setResultadoConsulta(resultado);
    } catch (err) {
      setError(err.message);
      setResultadoConsulta(null);
    } finally {
      setLoading(false);
    }
  };

  const getSituacaoLabel = (situacao) => {
    const labels = {
      'P': 'Pendente',
      'E': 'Emitida',
      'C': 'Cancelada',
      'X': 'Erro',
    };
    return labels[situacao] || situacao;
  };

  const getSituacaoClass = (situacao) => {
    const classes = {
      'P': 'situacao-pendente',
      'E': 'situacao-emitida',
      'C': 'situacao-cancelada',
      'X': 'situacao-erro',
    };
    return classes[situacao] || '';
  };

  const renderAbaEmitir = () => (
    <div className="aba-content">
      <h2>Emitir Nova NFS-e</h2>
      <form onSubmit={handleSubmitNota} className="nfse-form">
        <div className="form-section">
          <h3>Dados do Tomador</h3>
          <div className="form-row">
            <div className="form-group">
              <label>CPF/CNPJ:</label>
              <input
                type="text"
                value={formNota.tomadorCnpjCpf}
                onChange={(e) => setFormNota({...formNota, tomadorCnpjCpf: e.target.value})}
                placeholder="CPF ou CNPJ"
                required
              />
            </div>
            <div className="form-group">
              <label>Nome/Razão Social:</label>
              <input
                type="text"
                value={formNota.tomadorNome}
                onChange={(e) => setFormNota({...formNota, tomadorNome: e.target.value})}
                required
              />
            </div>
          </div>
          <div className="form-row">
            <div className="form-group full-width">
              <label>Endereço:</label>
              <input
                type="text"
                value={formNota.tomadorEndereco}
                onChange={(e) => setFormNota({...formNota, tomadorEndereco: e.target.value})}
              />
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Bairro:</label>
              <input
                type="text"
                value={formNota.tomadorBairro}
                onChange={(e) => setFormNota({...formNota, tomadorBairro: e.target.value})}
              />
            </div>
            <div className="form-group">
              <label>Cidade:</label>
              <input
                type="text"
                value={formNota.tomadorCidade}
                onChange={(e) => setFormNota({...formNota, tomadorCidade: e.target.value})}
              />
            </div>
            <div className="form-group">
              <label>UF:</label>
              <input
                type="text"
                value={formNota.tomadorUf}
                onChange={(e) => setFormNota({...formNota, tomadorUf: e.target.value})}
                maxLength={2}
              />
            </div>
            <div className="form-group">
              <label>CEP:</label>
              <input
                type="text"
                value={formNota.tomadorCep}
                onChange={(e) => setFormNota({...formNota, tomadorCep: e.target.value})}
              />
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Email:</label>
              <input
                type="email"
                value={formNota.tomadorEmail}
                onChange={(e) => setFormNota({...formNota, tomadorEmail: e.target.value})}
              />
            </div>
            <div className="form-group">
              <label>Telefone:</label>
              <input
                type="text"
                value={formNota.tomadorTelefone}
                onChange={(e) => setFormNota({...formNota, tomadorTelefone: e.target.value})}
              />
            </div>
          </div>
        </div>
        
        <div className="form-section">
          <h3>Dados do Serviço</h3>
          <div className="form-row">
            <div className="form-group">
              <label>Valor do Serviço:</label>
              <input
                type="number"
                step="0.01"
                value={formNota.valorServico}
                onChange={(e) => setFormNota({...formNota, valorServico: e.target.value})}
                required
              />
            </div>
            <div className="form-group">
              <label>Código do Serviço:</label>
              <input
                type="text"
                value={formNota.codigoServico}
                onChange={(e) => setFormNota({...formNota, codigoServico: e.target.value})}
                placeholder="Código municipal"
              />
            </div>
            <div className="form-group">
              <label>Alíquota (%):</label>
              <input
                type="number"
                step="0.01"
                value={formNota.aliquota}
                onChange={(e) => setFormNota({...formNota, aliquota: e.target.value})}
              />
            </div>
          </div>
          <div className="form-row">
            <div className="form-group full-width">
              <label>Discriminação do Serviço:</label>
              <textarea
                value={formNota.discriminacao}
                onChange={(e) => setFormNota({...formNota, discriminacao: e.target.value})}
                rows={4}
                required
              />
            </div>
          </div>
        </div>
        
        <div className="form-actions">
          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? 'Salvando...' : 'Salvar Nota'}
          </button>
        </div>
        
        {error && <div className="error-message">{error}</div>}
      </form>
    </div>
  );

  const renderAbaConsultar = () => (
    <div className="aba-content">
      <h2>Consultar NFS-e</h2>
      <div className="consulta-form">
        <div className="form-row">
          <div className="form-group">
            <label>Número da NFS-e:</label>
            <input
              type="text"
              value={numeroConsulta}
              onChange={(e) => setNumeroConsulta(e.target.value)}
              placeholder="Número da nota"
            />
          </div>
          <button onClick={handleConsultarNfse} className="btn-primary" disabled={loading}>
            {loading ? 'Consultando...' : 'Consultar'}
          </button>
        </div>
        
        {resultadoConsulta && (
          <div className="resultado-consulta">
            <h3>Resultado da Consulta</h3>
            <div className="resultado-info">
              <p><strong>Status:</strong> {resultadoConsulta.sucesso ? 'Sucesso' : 'Erro'}</p>
              <p><strong>Mensagem:</strong> {resultadoConsulta.mensagem}</p>
              {resultadoConsulta.numeroNfse && (
                <p><strong>Número NFS-e:</strong> {resultadoConsulta.numeroNfse}</p>
              )}
              {resultadoConsulta.chaveNfse && (
                <p><strong>Chave:</strong> {resultadoConsulta.chaveNfse}</p>
              )}
              {resultadoConsulta.linkConsulta && (
                <p><strong>Link:</strong> <a href={resultadoConsulta.linkConsulta} target="_blank" rel="noopener noreferrer">{resultadoConsulta.linkConsulta}</a></p>
              )}
            </div>
          </div>
        )}
        
        {error && <div className="error-message">{error}</div>}
      </div>
    </div>
  );

  const renderAbaCancelar = () => (
    <div className="aba-content">
      <h2>Cancelar NFS-e</h2>
      {notaSelecionada ? (
        <div className="cancelamento-form">
          <div className="nota-selecionada">
            <h3>Nota Selecionada</h3>
            <p><strong>Número:</strong> {notaSelecionada.numeroNota || notaSelecionada.id}</p>
            <p><strong>Tomador:</strong> {notaSelecionada.tomadorNome}</p>
            <p><strong>Valor:</strong> R$ {notaSelecionada.valorServico?.toFixed(2)}</p>
            <p><strong>Situação:</strong> {getSituacaoLabel(notaSelecionada.situacao)}</p>
          </div>
          
          <div className="form-group full-width">
            <label>Motivo do Cancelamento:</label>
            <textarea
              value={motivoCancelamento}
              onChange={(e) => setMotivoCancelamento(e.target.value)}
              rows={4}
              placeholder="Informe o motivo do cancelamento"
            />
          </div>
          
          <div className="form-actions">
            <button onClick={() => handleCancelarNota(notaSelecionada.id)} className="btn-danger" disabled={loading}>
              {loading ? 'Cancelando...' : 'Confirmar Cancelamento'}
            </button>
            <button onClick={() => { setNotaSelecionada(null); setMotivoCancelamento(''); }} className="btn-secondary">
              Cancelar
            </button>
          </div>
        </div>
      ) : (
        <div className="selecionar-nota">
          <p>Selecione uma nota para cancelar na aba "Listar Notas"</p>
        </div>
      )}
    </div>
  );

  const renderAbaListar = () => (
    <div className="aba-content">
      <h2>Listar Notas Fiscais</h2>
      
      <div className="filtros">
        <div className="form-row">
          <div className="form-group">
            <label>Data Início:</label>
            <input
              type="date"
              value={filtroDataInicio}
              onChange={(e) => setFiltroDataInicio(e.target.value)}
            />
          </div>
          <div className="form-group">
            <label>Data Fim:</label>
            <input
              type="date"
              value={filtroDataFim}
              onChange={(e) => setFiltroDataFim(e.target.value)}
            />
          </div>
          <div className="form-group">
            <label>Situação:</label>
            <select value={filtroSituacao} onChange={(e) => setFiltroSituacao(e.target.value)}>
              <option value="">Todas</option>
              <option value="P">Pendente</option>
              <option value="E">Emitida</option>
              <option value="C">Cancelada</option>
              <option value="X">Erro</option>
            </select>
          </div>
          <button onClick={carregarNotas} className="btn-secondary">
            Filtrar
          </button>
        </div>
        
        <div className="form-row">
          <div className="form-group full-width">
            <label>Buscar:</label>
            <input
              type="text"
              value={filtroTermo}
              onChange={(e) => setFiltroTermo(e.target.value)}
              placeholder="Buscar por nome, CPF/CNPJ..."
            />
          </div>
          <button onClick={() => { setPagina(0); carregarNotas(); }} className="btn-secondary">
            Buscar
          </button>
        </div>
      </div>

      {loading && <div className="loading">Carregando...</div>}
      
      {error && <div className="error-message">{error}</div>}

      <div className="tabela-container">
        <table className="nfse-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Número</th>
              <th>Data Emissão</th>
              <th>Tomador</th>
              <th>CNPJ/CPF</th>
              <th>Valor</th>
              <th>Situação</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {notas.map((nota) => (
              <tr key={nota.id}>
                <td>{nota.id}</td>
                <td>{nota.numeroNota || '-'}</td>
                <td>{nota.dataEmissao ? new Date(nota.dataEmissao).toLocaleDateString() : '-'}</td>
                <td>{nota.tomadorNome || '-'}</td>
                <td>{nota.tomadorCnpjCpf || '-'}</td>
                <td>R$ {nota.valorServico?.toFixed(2) || '0,00'}</td>
                <td>
                  <span className={`situacao-badge ${getSituacaoClass(nota.situacao)}`}>
                    {getSituacaoLabel(nota.situacao)}
                  </span>
                </td>
                <td className="acoes">
                  {nota.situacao === 'P' && (
                    <button onClick={() => handleEmitirNota(nota.id)} className="btn-small btn-primary">
                      Emitir
                    </button>
                  )}
                  {nota.situacao === 'E' && (
                    <button onClick={() => setNotaSelecionada(nota)} className="btn-small btn-danger">
                      Cancelar
                    </button>
                  )}
                  {nota.situacao === 'P' && (
                    <button onClick={() => handleExcluirNota(nota.id)} className="btn-small btn-danger">
                      Excluir
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        
        {notas.length === 0 && !loading && (
          <div className="sem-registros">Nenhuma nota encontrada</div>
        )}
      </div>

      {totalPaginas > 1 && (
        <div className="paginacao">
          <button onClick={() => setPagina(Math.max(0, pagina - 1))} disabled={pagina === 0}>
            Anterior
          </button>
          <span>Página {pagina + 1} de {totalPaginas}</span>
          <button onClick={() => setPagina(Math.min(totalPaginas - 1, pagina + 1))} disabled={pagina >= totalPaginas - 1}>
            Próxima
          </button>
        </div>
      )}
    </div>
  );

  return (
    <div className="nfse-page">
      <div className="nfse-header">
        <h1>NFS-e - Nota Fiscal de Serviços Eletrônica</h1>
      </div>
      
      <div className="nfse-tabs">
        <button 
          className={`tab-button ${abaAtiva === 'emitir' ? 'active' : ''}`}
          onClick={() => setAbaAtiva('emitir')}
        >
          Emitir Nota
        </button>
        <button 
          className={`tab-button ${abaAtiva === 'consultar' ? 'active' : ''}`}
          onClick={() => setAbaAtiva('consultar')}
        >
          Consultar NFS-e
        </button>
        <button 
          className={`tab-button ${abaAtiva === 'cancelar' ? 'active' : ''}`}
          onClick={() => setAbaAtiva('cancelar')}
        >
          Cancelar NFS-e
        </button>
        <button 
          className={`tab-button ${abaAtiva === 'listar' ? 'active' : ''}`}
          onClick={() => setAbaAtiva('listar')}
        >
          Listar Notas
        </button>
      </div>
      
      <div className="nfse-content">
        {abaAtiva === 'emitir' && renderAbaEmitir()}
        {abaAtiva === 'consultar' && renderAbaConsultar()}
        {abaAtiva === 'cancelar' && renderAbaCancelar()}
        {abaAtiva === 'listar' && renderAbaListar()}
      </div>
    </div>
  );
};

export default NfsePage;