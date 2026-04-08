/**
 * Componente NFS-e para integração no frontend existente
 * 
 * @author Seprocom
 */

class NfseComponent {
  constructor(containerSelector, options = {}) {
    this.container = document.querySelector(containerSelector);
    this.options = {
      apiBaseUrl: options.apiBaseUrl || 'http://localhost:8080/gerencial/api/nfse',
      onError: options.onError || console.error,
      onSuccess: options.onSuccess || console.log,
      ...options
    };
    
    this.state = {
      abaAtiva: 'emitir',
      notas: [],
      loading: false,
      error: null,
      pagina: 0,
      totalPaginas: 0,
      notaSelecionada: null,
      filtroDataInicio: '',
      filtroDataFim: '',
      filtroTermo: '',
      filtroSituacao: '',
      formNota: this.getFormNotaDefault(),
      motivoCancelamento: '',
      numeroConsulta: '',
      resultadoConsulta: null
    };
    
    this.init();
  }
  
  getFormNotaDefault() {
    return {
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
      aliquota: ''
    };
  }
  
  getHeaders() {
    const token = localStorage.getItem('nfse_token');
    return {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      ...(token ? { 'Authorization': `Bearer ${token}` } : {})
    };
  }
  
  async handleResponse(response) {
    if (!response.ok) {
      const error = await response.json().catch(() => ({ mensagem: 'Erro desconhecido' }));
      throw new Error(error.mensagem || `HTTP ${response.status}`);
    }
    return response.json();
  }
  
  async apiCall(endpoint, options = {}) {
    const url = `${this.options.apiBaseUrl}${endpoint}`;
    const response = await fetch(url, {
      ...options,
      headers: { ...this.getHeaders(), ...options.headers }
    });
    return this.handleResponse(response);
  }
  
  init() {
    if (!this.container) {
      console.error('Container não encontrado');
      return;
    }
    
    this.render();
    this.bindEvents();
    this.carregarNotas();
  }
  
  render() {
    this.container.innerHTML = `
      <div class="nfse-component">
        <div class="nfse-header">
          <h1>NFS-e - Nota Fiscal de Serviços Eletrônica</h1>
        </div>
        
        <div class="nfse-tabs">
          <button class="tab-button ${this.state.abaAtiva === 'emitir' ? 'active' : ''}" data-aba="emitir">
            Emitir Nota
          </button>
          <button class="tab-button ${this.state.abaAtiva === 'consultar' ? 'active' : ''}" data-aba="consultar">
            Consultar NFS-e
          </button>
          <button class="tab-button ${this.state.abaAtiva === 'cancelar' ? 'active' : ''}" data-aba="cancelar">
            Cancelar NFS-e
          </button>
          <button class="tab-button ${this.state.abaAtiva === 'listar' ? 'active' : ''}" data-aba="listar">
            Listar Notas
          </button>
        </div>
        
        <div class="nfse-content">
          ${this.renderAbaContent()}
        </div>
      </div>
    `;
    
    this.bindEvents();
  }
  
  renderAbaContent() {
    switch (this.state.abaAtiva) {
      case 'emitir':
        return this.renderAbaEmitir();
      case 'consultar':
        return this.renderAbaConsultar();
      case 'cancelar':
        return this.renderAbaCancelar();
      case 'listar':
        return this.renderAbaListar();
      default:
        return '';
    }
  }
  
  renderAbaEmitir() {
    const { formNota, loading } = this.state;
    
    return `
      <div class="aba-content">
        <h2>Emitir Nova NFS-e</h2>
        <form class="nfse-form" id="nfse-form">
          <div class="form-section">
            <h3>Dados do Tomador</h3>
            <div class="form-row">
              <div class="form-group">
                <label>CPF/CNPJ:</label>
                <input type="text" name="tomadorCnpjCpf" value="${formNota.tomadorCnpjCpf}" required />
              </div>
              <div class="form-group">
                <label>Nome/Razão Social:</label>
                <input type="text" name="tomadorNome" value="${formNota.tomadorNome}" required />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group full-width">
                <label>Endereço:</label>
                <input type="text" name="tomadorEndereco" value="${formNota.tomadorEndereco}" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>Bairro:</label>
                <input type="text" name="tomadorBairro" value="${formNota.tomadorBairro}" />
              </div>
              <div class="form-group">
                <label>Cidade:</label>
                <input type="text" name="tomadorCidade" value="${formNota.tomadorCidade}" />
              </div>
              <div class="form-group">
                <label>UF:</label>
                <input type="text" name="tomadorUf" value="${formNota.tomadorUf}" maxLength="2" />
              </div>
              <div class="form-group">
                <label>CEP:</label>
                <input type="text" name="tomadorCep" value="${formNota.tomadorCep}" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>Email:</label>
                <input type="email" name="tomadorEmail" value="${formNota.tomadorEmail}" />
              </div>
              <div class="form-group">
                <label>Telefone:</label>
                <input type="text" name="tomadorTelefone" value="${formNota.tomadorTelefone}" />
              </div>
            </div>
          </div>
          
          <div class="form-section">
            <h3>Dados do Serviço</h3>
            <div class="form-row">
              <div class="form-group">
                <label>Valor do Serviço:</label>
                <input type="number" step="0.01" name="valorServico" value="${formNota.valorServico}" required />
              </div>
              <div class="form-group">
                <label>Código do Serviço:</label>
                <input type="text" name="codigoServico" value="${formNota.codigoServico}" />
              </div>
              <div class="form-group">
                <label>Alíquota (%):</label>
                <input type="number" step="0.01" name="aliquota" value="${formNota.aliquota}" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group full-width">
                <label>Discriminação do Serviço:</label>
                <textarea name="discriminacao" rows="4" required>${formNota.discriminacao}</textarea>
              </div>
            </div>
          </div>
          
          <div class="form-actions">
            <button type="submit" class="btn-primary" ${loading ? 'disabled' : ''}>
              ${loading ? 'Salvando...' : 'Salvar Nota'}
            </button>
          </div>
          
          <div class="error-message" id="emitir-error" style="display: none;"></div>
        </form>
      </div>
    `;
  }
  
  renderAbaConsultar() {
    const { numeroConsulta, resultadoConsulta, loading } = this.state;
    
    return `
      <div class="aba-content">
        <h2>Consultar NFS-e</h2>
        <div class="consulta-form">
          <div class="form-row">
            <div class="form-group">
              <label>Número da NFS-e:</label>
              <input type="text" id="numero-consulta" value="${numeroConsulta}" placeholder="Número da nota" />
            </div>
            <button class="btn-primary" id="btn-consultar" ${loading ? 'disabled' : ''}>
              ${loading ? 'Consultando...' : 'Consultar'}
            </button>
          </div>
          
          ${resultadoConsulta ? `
            <div class="resultado-consulta">
              <h3>Resultado da Consulta</h3>
              <div class="resultado-info">
                <p><strong>Status:</strong> ${resultadoConsulta.sucesso ? 'Sucesso' : 'Erro'}</p>
                <p><strong>Mensagem:</strong> ${resultadoConsulta.mensagem}</p>
                ${resultadoConsulta.numeroNfse ? `<p><strong>Número NFS-e:</strong> ${resultadoConsulta.numeroNfse}</p>` : ''}
                ${resultadoConsulta.chaveNfse ? `<p><strong>Chave:</strong> ${resultadoConsulta.chaveNfse}</p>` : ''}
                ${resultadoConsulta.linkConsulta ? `<p><strong>Link:</strong> <a href="${resultadoConsulta.linkConsulta}" target="_blank">${resultadoConsulta.linkConsulta}</a></p>` : ''}
              </div>
            </div>
          ` : ''}
          
          <div class="error-message" id="consultar-error" style="display: none;"></div>
        </div>
      </div>
    `;
  }
  
  renderAbaCancelar() {
    const { notaSelecionada, motivoCancelamento, loading } = this.state;
    
    if (!notaSelecionada) {
      return `
        <div class="aba-content">
          <h2>Cancelar NFS-e</h2>
          <div class="selecionar-nota">
            <p>Selecione uma nota para cancelar na aba "Listar Notas"</p>
          </div>
        </div>
      `;
    }
    
    return `
      <div class="aba-content">
        <h2>Cancelar NFS-e</h2>
        <div class="cancelamento-form">
          <div class="nota-selecionada">
            <h3>Nota Selecionada</h3>
            <p><strong>Número:</strong> ${notaSelecionada.numeroNota || notaSelecionada.id}</p>
            <p><strong>Tomador:</strong> ${notaSelecionada.tomadorNome}</p>
            <p><strong>Valor:</strong> R$ ${(notaSelecionada.valorServico || 0).toFixed(2)}</p>
            <p><strong>Situação:</strong> ${this.getSituacaoLabel(notaSelecionada.situacao)}</p>
          </div>
          
          <div class="form-group full-width">
            <label>Motivo do Cancelamento:</label>
            <textarea id="motivo-cancelamento" rows="4" placeholder="Informe o motivo do cancelamento">${motivoCancelamento}</textarea>
          </div>
          
          <div class="form-actions">
            <button class="btn-danger" id="btn-confirmar-cancelar" ${loading ? 'disabled' : ''}>
              ${loading ? 'Cancelando...' : 'Confirmar Cancelamento'}
            </button>
            <button class="btn-secondary" id="btn-cancelar-selecao">Cancelar</button>
          </div>
          
          <div class="error-message" id="cancelar-error" style="display: none;"></div>
        </div>
      </div>
    `;
  }
  
  renderAbaListar() {
    const { notas, loading, pagina, totalPaginas, filtroDataInicio, filtroDataFim, filtroSituacao, filtroTermo } = this.state;
    
    return `
      <div class="aba-content">
        <h2>Listar Notas Fiscais</h2>
        
        <div class="filtros">
          <div class="form-row">
            <div class="form-group">
              <label>Data Início:</label>
              <input type="date" id="filtro-data-inicio" value="${filtroDataInicio}" />
            </div>
            <div class="form-group">
              <label>Data Fim:</label>
              <input type="date" id="filtro-data-fim" value="${filtroDataFim}" />
            </div>
            <div class="form-group">
              <label>Situação:</label>
              <select id="filtro-situacao">
                <option value="">Todas</option>
                <option value="P" ${filtroSituacao === 'P' ? 'selected' : ''}>Pendente</option>
                <option value="E" ${filtroSituacao === 'E' ? 'selected' : ''}>Emitida</option>
                <option value="C" ${filtroSituacao === 'C' ? 'selected' : ''}>Cancelada</option>
                <option value="X" ${filtroSituacao === 'X' ? 'selected' : ''}>Erro</option>
              </select>
            </div>
            <button class="btn-secondary" id="btn-filtrar">Filtrar</button>
          </div>
          
          <div class="form-row">
            <div class="form-group full-width">
              <label>Buscar:</label>
              <input type="text" id="filtro-termo" value="${filtroTermo}" placeholder="Buscar por nome, CPF/CNPJ..." />
            </div>
            <button class="btn-secondary" id="btn-buscar">Buscar</button>
          </div>
        </div>

        ${loading ? '<div class="loading">Carregando...</div>' : ''}
        
        <div class="error-message" id="listar-error" style="display: none;"></div>

        <div class="tabela-container">
          <table class="nfse-table">
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
              ${notas.map(nota => `
                <tr>
                  <td>${nota.id}</td>
                  <td>${nota.numeroNota || '-'}</td>
                  <td>${nota.dataEmissao ? new Date(nota.dataEmissao).toLocaleDateString() : '-'}</td>
                  <td>${nota.tomadorNome || '-'}</td>
                  <td>${nota.tomadorCnpjCpf || '-'}</td>
                  <td>R$ ${(nota.valorServico || 0).toFixed(2)}</td>
                  <td>
                    <span class="situacao-badge ${this.getSituacaoClass(nota.situacao)}">
                      ${this.getSituacaoLabel(nota.situacao)}
                    </span>
                  </td>
                  <td class="acoes">
                    ${nota.situacao === 'P' ? `<button class="btn-small btn-primary btn-emitir" data-id="${nota.id}">Emitir</button>` : ''}
                    ${nota.situacao === 'E' ? `<button class="btn-small btn-danger btn-selecionar-cancelar" data-id="${nota.id}">Cancelar</button>` : ''}
                    ${nota.situacao === 'P' ? `<button class="btn-small btn-danger btn-excluir" data-id="${nota.id}">Excluir</button>` : ''}
                  </td>
                </tr>
              `).join('')}
            </tbody>
          </table>
          
          ${notas.length === 0 && !loading ? '<div class="sem-registros">Nenhuma nota encontrada</div>' : ''}
        </div>

        ${totalPaginas > 1 ? `
          <div class="paginacao">
            <button id="btn-pagina-anterior" ${pagina === 0 ? 'disabled' : ''}>Anterior</button>
            <span>Página ${pagina + 1} de ${totalPaginas}</span>
            <button id="btn-pagina-proxima" ${pagina >= totalPaginas - 1 ? 'disabled' : ''}>Próxima</button>
          </div>
        ` : ''}
      </div>
    `;
  }
  
  getSituacaoLabel(situacao) {
    const labels = { 'P': 'Pendente', 'E': 'Emitida', 'C': 'Cancelada', 'X': 'Erro' };
    return labels[situacao] || situacao;
  }
  
  getSituacaoClass(situacao) {
    const classes = { 'P': 'situacao-pendente', 'E': 'situacao-emitida', 'C': 'situacao-cancelada', 'X': 'situacao-erro' };
    return classes[situacao] || '';
  }
  
  bindEvents() {
    // Tabs
    this.container.querySelectorAll('.tab-button').forEach(btn => {
      btn.addEventListener('click', (e) => {
        this.setState({ abaAtiva: e.target.dataset.aba });
      });
    });
    
    // Formulário de emissão
    const form = this.container.querySelector('#nfse-form');
    if (form) {
      form.addEventListener('submit', (e) => this.handleSubmitNota(e));
      form.querySelectorAll('input, textarea').forEach(input => {
        input.addEventListener('change', (e) => {
          this.setState({
            formNota: { ...this.state.formNota, [e.target.name]: e.target.value }
          });
        });
      });
    }
    
    // Consulta
    const btnConsultar = this.container.querySelector('#btn-consultar');
    if (btnConsultar) {
      btnConsultar.addEventListener('click', () => this.handleConsultar());
    }
    const inputConsulta = this.container.querySelector('#numero-consulta');
    if (inputConsulta) {
      inputConsulta.addEventListener('change', (e) => {
        this.setState({ numeroConsulta: e.target.value });
      });
    }
    
    // Cancelar
    const btnConfirmarCancelar = this.container.querySelector('#btn-confirmar-cancelar');
    if (btnConfirmarCancelar) {
      btnConfirmarCancelar.addEventListener('click', () => this.handleCancelarNota());
    }
    const btnCancelarSelecao = this.container.querySelector('#btn-cancelar-selecao');
    if (btnCancelarSelecao) {
      btnCancelarSelecao.addEventListener('click', () => {
        this.setState({ notaSelecionada: null, motivoCancelamento: '' });
      });
    }
    const motivoInput = this.container.querySelector('#motivo-cancelamento');
    if (motivoInput) {
      motivoInput.addEventListener('change', (e) => {
        this.setState({ motivoCancelamento: e.target.value });
      });
    }
    
    // Filtros
    const btnFiltrar = this.container.querySelector('#btn-filtrar');
    if (btnFiltrar) {
      btnFiltrar.addEventListener('click', () => this.carregarNotas());
    }
    const btnBuscar = this.container.querySelector('#btn-buscar');
    if (btnBuscar) {
      btnBuscar.addEventListener('click', () => { this.setState({ pagina: 0 }); this.carregarNotas(); });
    }
    ['filtro-data-inicio', 'filtro-data-fim', 'filtro-situacao', 'filtro-termo'].forEach(id => {
      const input = this.container.querySelector(`#${id}`);
      if (input) {
        input.addEventListener('change', (e) => {
          const key = id.replace('filtro-', '').replace('-', '');
          const stateKey = id.replace('filtro-', '').replace(/-([a-z])/g, (g) => g[1].toUpperCase());
          this.setState({ [stateKey]: e.target.value });
        });
      }
    });
    
    // Ações da tabela
    this.container.querySelectorAll('.btn-emitir').forEach(btn => {
      btn.addEventListener('click', (e) => this.handleEmitirNota(parseInt(e.target.dataset.id)));
    });
    this.container.querySelectorAll('.btn-selecionar-cancelar').forEach(btn => {
      btn.addEventListener('click', (e) => this.selecionarParaCancelar(parseInt(e.target.dataset.id)));
    });
    this.container.querySelectorAll('.btn-excluir').forEach(btn => {
      btn.addEventListener('click', (e) => this.handleExcluirNota(parseInt(e.target.dataset.id)));
    });
    
    // Paginação
    const btnPagAnt = this.container.querySelector('#btn-pagina-anterior');
    if (btnPagAnt) {
      btnPagAnt.addEventListener('click', () => this.setState({ pagina: Math.max(0, this.state.pagina - 1) }, () => this.carregarNotas()));
    }
    const btnPagProx = this.container.querySelector('#btn-pagina-proxima');
    if (btnPagProx) {
      btnPagProx.addEventListener('click', () => this.setState({ pagina: Math.min(this.state.totalPaginas - 1, this.state.pagina + 1) }, () => this.carregarNotas()));
    }
  }
  
  setState(newState) {
    this.state = { ...this.state, ...newState };
    this.render();
  }
  
  async carregarNotas() {
    this.setState({ loading: true, error: null });
    try {
      let dados;
      const { pagina, filtroTermo, filtroDataInicio, filtroDataFim } = this.state;
      
      if (filtroTermo) {
        dados = await this.apiCall(`/notas/busca?termo=${encodeURIComponent(filtroTermo)}&pagina=${pagina}`);
      } else if (filtroDataInicio && filtroDataFim) {
        dados = await this.apiCall(`/notas/periodo?dataInicio=${filtroDataInicio}&dataFim=${filtroDataFim}`);
      } else {
        dados = await this.apiCall(`/notas?pagina=${pagina}`);
      }
      
      this.setState({
        notas: dados.content || [],
        totalPaginas: dados.totalPages || 0,
        loading: false
      });
    } catch (err) {
      this.setState({ error: err.message, loading: false });
      this.mostrarErro('listar-error', err.message);
    }
  }
  
  async handleSubmitNota(e) {
    e.preventDefault();
    this.setState({ loading: true, error: null });
    
    try {
      const notaData = {
        ...this.state.formNota,
        valorServico: parseFloat(this.state.formNota.valorServico) || 0,
        aliquota: parseFloat(this.state.formNota.aliquota) || 0,
        dataEmissao: new Date().toISOString().split('T')[0],
        situacao: 'P'
      };
      
      await this.apiCall('/notas', {
        method: 'POST',
        body: JSON.stringify(notaData)
      });
      
      alert('Nota criada com sucesso!');
      this.setState({ formNota: this.getFormNotaDefault() });
      this.carregarNotas();
    } catch (err) {
      this.mostrarErro('emitir-error', err.message);
    } finally {
      this.setState({ loading: false });
    }
  }
  
  async handleEmitirNota(id) {
    if (!confirm('Deseja emitir esta NFS-e?')) return;
    
    this.setState({ loading: true, error: null });
    try {
      const resultado = await this.apiCall(`/notas/${id}/emitir`, { method: 'POST' });
      alert(resultado.mensagem || 'NFS-e emitida com sucesso!');
      this.carregarNotas();
    } catch (err) {
      alert('Erro ao emitir: ' + err.message);
    } finally {
      this.setState({ loading: false });
    }
  }
  
  async handleCancelarNota() {
    const { notaSelecionada, motivoCancelamento } = this.state;
    
    if (!motivoCancelamento) {
      alert('Informe o motivo do cancelamento');
      return;
    }
    
    if (!confirm('Confirma o cancelamento desta NFS-e?')) return;
    
    this.setState({ loading: true, error: null });
    try {
      const resultado = await this.apiCall(`/notas/${notaSelecionada.id}/cancelar`, {
        method: 'POST',
        body: JSON.stringify({ motivo: motivoCancelamento })
      });
      alert(resultado.mensagem || 'NFS-e cancelada com sucesso!');
      this.setState({ notaSelecionada: null, motivoCancelamento: '' });
      this.carregarNotas();
    } catch (err) {
      this.mostrarErro('cancelar-error', err.message);
    } finally {
      this.setState({ loading: false });
    }
  }
  
  async handleExcluirNota(id) {
    if (!confirm('Confirma a exclusão desta nota?')) return;
    
    this.setState({ loading: true, error: null });
    try {
      await this.apiCall(`/notas/${id}`, { method: 'DELETE' });
      alert('Nota excluída com sucesso!');
      this.carregarNotas();
    } catch (err) {
      alert('Erro ao excluir: ' + err.message);
    } finally {
      this.setState({ loading: false });
    }
  }
  
  async handleConsultar() {
    const { numeroConsulta } = this.state;
    if (!numeroConsulta) {
      alert('Informe o número da NFS-e');
      return;
    }
    
    this.setState({ loading: true, error: null, resultadoConsulta: null });
    try {
      const resultado = await this.apiCall(`/nfse/consultar?numero=${encodeURIComponent(numeroConsulta)}`);
      this.setState({ resultadoConsulta: resultado, loading: false });
    } catch (err) {
      this.mostrarErro('consultar-error', err.message);
      this.setState({ loading: false });
    }
  }
  
  selecionarParaCancelar(id) {
    const nota = this.state.notas.find(n => n.id === id);
    if (nota) {
      this.setState({ notaSelecionada: nota, abaAtiva: 'cancelar' });
    }
  }
  
  mostrarErro(elementId, mensagem) {
    const el = this.container.querySelector(`#${elementId}`);
    if (el) {
      el.textContent = mensagem;
      el.style.display = 'block';
    }
  }
  
  // Métodos de integração com o container
  mount(selector) {
    const container = document.querySelector(selector);
    if (container) {
      this.container = container;
      this.init();
    }
    return this;
  }
  
  unmount() {
    if (this.container) {
      this.container.innerHTML = '';
    }
  }
  
  updateProps(newProps) {
    this.options = { ...this.options, ...newProps };
  }
}

// Exportar para uso global
window.NfseComponent = NfseComponent;