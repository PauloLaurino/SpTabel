/**
 * ParametrosPage - CRUD de Parâmetros do Sistema
 * Divide os campos em abas conforme especificação:
 * #Cadastro (coluna 1-22), #Gerencial (23-44), #CCN (46-50), 
 * #FUNARPEN (51-63), Arquivos (64-72), #Boleto (73-86), #NFS2 (87-90), #Cabeçalho (campo 45)
 */

class ParametrosPage {
    constructor(props = {}) {
        this.apiUrl = '/api/nfse/parametros';
        this.activeTab = 'cadastro';
        this.parametros = null;
        this.parametrosOriginais = null;
        this.historico = [];
        this.isLoading = false;
        this.isSaving = false;
        this.hasChanges = false;
        
        this.tabs = [
            { id: 'cadastro', label: 'Cadastro', range: '1-22' },
            { id: 'gerencial', label: 'Gerencial', range: '23-44' },
            { id: 'CCN', label: 'CCN (Impostos)', range: '46-50' },
            { id: 'funarpen', label: 'FUNARPEN', range: '51-63' },
            { id: 'arquivos', label: 'Arquivos', range: '64-72' },
            { id: 'boleto', label: 'Boleto', range: '73-86' },
            { id: 'nfse', label: 'NFSe', range: '87-90' },
            { id: 'cabecalho', label: 'Cabeçalho', range: '45' },
            { id: 'historico', label: 'Histórico', range: '' }
        ];
    }

    async init() {
        await this.carregarParametros();
    }

    async carregarParametros() {
        this.setLoading(true);
        try {
            const response = await fetch(this.apiUrl);
            if (!response.ok) throw new Error('Erro ao carregar parâmetros');
            this.parametros = await response.json();
            this.parametrosOriginais = JSON.parse(JSON.stringify(this.parametros));
            this.hasChanges = false;
            this.render();
        } catch (error) {
            console.error('Erro:', error);
            this.showError('Erro ao carregar parâmetros do sistema');
        } finally {
            this.setLoading(false);
        }
    }

    async salvarParametros() {
        if (!this.hasChanges) return;
        
        this.setSaving(true);
        try {
            const response = await fetch(this.apiUrl, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(this.parametros)
            });
            
            if (!response.ok) throw new Error('Erro ao salvar');
            
            this.parametrosOriginais = JSON.parse(JSON.stringify(this.parametros));
            this.hasChanges = false;
            this.showSuccess('Parâmetros salvos com sucesso!');
            
            // Recarregar para confirmar
            await this.carregarParametros();
        } catch (error) {
            console.error('Erro:', error);
            this.showError('Erro ao salvar parâmetros');
        } finally {
            this.setSaving(false);
        }
    }

    async carregarHistorico(pagina = 0) {
        try {
            const response = await fetch(`${this.apiUrl}/historico?pagina=${pagina}&tamanho=20`);
            const data = await response.json();
            this.historico = data.content || [];
            this.renderHistorico();
        } catch (error) {
            console.error('Erro ao carregar histórico', error);
        }
    }

    setActiveTab(tabId) {
        this.activeTab = tabId;
        if (tabId === 'historico') {
            this.carregarHistorico(0);
        }
        this.render();
    }

    updateField(field, value) {
        this.parametros[field] = value;
        this.hasChanges = JSON.stringify(this.parametros) !== JSON.stringify(this.parametrosOriginais);
        this.render();
    }

    resetChanges() {
        this.parametros = JSON.parse(JSON.stringify(this.parametrosOriginais));
        this.hasChanges = false;
        this.render();
    }

    setLoading(loading) {
        this.isLoading = loading;
    }

    setSaving(saving) {
        this.isSaving = saving;
    }

    showError(message) {
        const alert = document.getElementById('parametros-alert');
        if (alert) {
            alert.className = 'alert alert-error';
            alert.textContent = message;
            alert.style.display = 'flex';
            setTimeout(() => alert.style.display = 'none', 5000);
        }
    }

    showSuccess(message) {
        const alert = document.getElementById('parametros-alert');
        if (alert) {
            alert.className = 'alert alert-success';
            alert.textContent = message;
            alert.style.display = 'flex';
            setTimeout(() => alert.style.display = 'none', 5000);
        }
    }

    formatDate(dateStr) {
        if (!dateStr) return '-';
        return new Date(dateStr).toLocaleDateString('pt-BR');
    }

    formatDateTime(dateStr) {
        if (!dateStr) return '-';
        return new Date(dateStr).toLocaleString('pt-BR');
    }

    render() {
        const container = document.getElementById('parametros-page');
        if (!container) return;

        container.innerHTML = `
            <div class="parametros-wrapper">
                <div class="parametros-header">
                    <h2>Parâmetros do Sistema</h2>
                    <div class="header-actions">
                        ${this.hasChanges ? `
                            <button class="btn btn-outline" onclick="window.parametrosPage.resetChanges()">
                                <i class="fas fa-rotate-left"></i> Restaurar
                            </button>
                        ` : ''}
                        <button class="btn btn-primary" 
                                onclick="window.parametrosPage.salvarParametros()"
                                ${!this.hasChanges || this.isSaving ? 'disabled' : ''}>
                            ${this.isSaving ? '<i class="fas fa-spinner fa-spin"></i> Salvando...' : '<i class="fas fa-save"></i> Salvar'}
                        </button>
                    </div>
                </div>

                <div id="parametros-alert" class="alert" style="display: none;"></div>

                ${this.isLoading ? `
                    <div class="loading-container">
                        <i class="fas fa-spinner fa-spin fa-2x"></i>
                        <p>Carregando parâmetros...</p>
                    </div>
                ` : this.renderTabs()}
            </div>
        `;
    }

    renderTabs() {
        return `
            <div class="tabs">
                ${this.tabs.map(tab => `
                    <button class="tab ${this.activeTab === tab.id ? 'active' : ''}" 
                            onclick="window.parametrosPage.setActiveTab('${tab.id}')">
                        ${tab.label}
                        ${tab.range ? `<span class="tab-range">${tab.range}</span>` : ''}
                    </button>
                `).join('')}
            </div>
            <div class="tab-content">
                ${this.getTabContent()}
            </div>
        `;
    }

    getTabContent() {
        switch (this.activeTab) {
            case 'cadastro': return this.renderCadastro();
            case 'gerencial': return this.renderGerencial();
            case 'CCN': return this.renderCCN();
            case 'funarpen': return this.renderFunarpen();
            case 'arquivos': return this.renderArquivos();
            case 'boleto': return this.renderBoleto();
            case 'nfse': return this.renderNFSe();
            case 'cabecalho': return this.renderCabecalho();
            case 'historico': return this.renderHistorico();
            default: return '';
        }
    }

    renderField(name, label, type = 'text', options = null) {
        const value = this.parametros ? this.parametros[name] : '';
        
        if (type === 'checkbox') {
            return `
                <div class="form-group">
                    <label class="checkbox-label">
                        <input type="checkbox" ${value ? 'checked' : ''} 
                               onchange="window.parametrosPage.updateField('${name}', this.checked)">
                        ${label}
                    </label>
                </div>
            `;
        }
        
        if (type === 'select' && options) {
            return `
                <div class="form-group">
                    <label for="${name}">${label}</label>
                    <select id="${name}" onchange="window.parametrosPage.updateField('${name}', this.value)">
                        <option value="">Selecione...</option>
                        ${options.map(opt => `<option value="${opt}" ${value === opt ? 'selected' : ''}>${opt}</option>`).join('')}
                    </select>
                </div>
            `;
        }
        
        return `
            <div class="form-group">
                <label for="${name}">${label}</label>
                <input type="${type}" id="${name}" value="${value || ''}" 
                       onchange="window.parametrosPage.updateField('${name}', this.value)"
                       step="${type === 'number' ? '0.0001' : ''}">
            </div>
        `;
    }

    renderCadastro() {
        const fields = [
            ['codigoPar', 'Código'],
            ['docPar', 'Documento'],
            ['nomeTabeliao', 'Nome Tabelião'],
            ['codigoTabeliao', 'Código Tabelião'],
            ['codigoCartorio', 'Código Cartório'],
            ['nomeCartorio', 'Nome Cartório'],
            ['cnpjCartorio', 'CNPJ'],
            ['inscricaoEstadual', 'Inscrição Estadual'],
            ['enderecoCartorio', 'Endereço'],
            ['bairroCartorio', 'Bairro'],
            ['cidadeCartorio', 'Cidade'],
            ['ufCartorio', 'UF'],
            ['cepCartorio', 'CEP'],
            ['telefoneCartorio', 'Telefone'],
            ['faxCartorio', 'Fax'],
            ['emailCartorio', 'Email'],
            ['siteCartorio', 'Site'],
            ['ativa', 'Ativo', 'select', ['S', 'N']],
            ['codigoIBGE', 'Código IBGE'],
            ['codigoReceitaFederal', 'Código Receita Federal'],
            ['codigoJuntaComercial', 'Código Junta Comercial'],
            ['dataAberturaCartorio', 'Data Abertura', 'date'],
            ['tabeliaoSubstituto', 'Tabelião Substituto'],
            ['委书Cartorio', 'Escritura', 'number']
        ];
        
        return `
            <div class="tab-header"><h3>Cadastro (Campos 1-22)</h3></div>
            <div class="form-grid">
                ${fields.map(([name, label, type, options]) => this.renderField(name, label, type, options)).join('')}
            </div>
        `;
    }

    renderGerencial() {
        const fields = [
            ['tabeliaoResponsavel', 'Tabelião Responsável'],
            ['codigoOrgaoJG', 'Código Órgão JG'],
            ['codigoCartorioJF', 'Código Cartório JF'],
            ['numeroCarnle', 'Número Carnle'],
            ['tabelaCustas', 'Tabela Custas'],
            ['tabelaEmolumentos', 'Tabela Emolumentos'],
            ['atoPratico', 'Ato Prático'],
            ['atoServentia', 'Ato Serventia'],
            ['atoRegistro', 'Ato Registro'],
            ['atoProtesto', 'Ato Protesto'],
            ['atoUnique', 'Ato Unique'],
            ['serieSelos', 'Série Selos'],
            ['serieCertidao', 'Série Certidão'],
            ['seriePapel', 'Série Papel'],
            ['tabeliaoAuxiliar', 'Tabelião Auxiliar'],
            ['substitutoLegal', 'Substituto Legal'],
            ['escreventeAutorizado', 'Escriventante Autorizado'],
            ['codigoTipoServentia', 'Código Tipo Serventia'],
            ['codigoMunicipio', 'Código Município'],
            ['codigoPais', 'Código País'],
            ['indicadorAtividade', 'Indicador Atividade'],
            ['serieAto', 'Série Ato'],
            ['naturezaJuridica', 'Natureza Jurídica']
        ];
        
        return `
            <div class="tab-header"><h3>Gerencial (Campos 23-44)</h3></div>
            <div class="form-grid">
                ${fields.map(([name, label, type, options]) => this.renderField(name, label, type, options)).join('')}
            </div>
        `;
    }

    renderCCN() {
        const fields = [
            ['issPar', 'ISS (%)', 'number'],
            ['cbsPar', 'CBS (%)', 'number'],
            ['ibsPar', 'IBS (%)', 'number'],
            ['pisPar', 'PIS (%)', 'number'],
            ['cofinsPar', 'COFINS (%)', 'number'],
            ['csllPar', 'CSLL (%)', 'number'],
            ['irpjPar', 'IRPJ (%)', 'number'],
            ['irpjAdicPar', 'IRPJ Adicional (%)', 'number'],
            ['cppPar', 'CPP (%)', 'number']
        ];
        
        return `
            <div class="tab-header"><h3>CCN - Impostos (Campos 46-50)</h3></div>
            <div class="form-section">
                <h4>Reforma Tributária</h4>
                <div class="form-grid">
                    ${fields.map(([name, label, type]) => this.renderField(name, label, type)).join('')}
                </div>
            </div>
        `;
    }

    renderFunarpen() {
        const fields = [
            ['dtVersaoFunarpen', 'Data Versão Funarpen', 'date'],
            ['obsFunarpen', 'Observações Funarpen'],
            ['faseReformaPar', 'Fase Reforma', 'select', ['NAO_INICIADA', 'TRANSICAO', 'CONVERGENCIA', 'PLENA']],
            ['cbsAtivoPar', 'CBS Ativo', 'checkbox'],
            ['ibsAtivoPar', 'IBS Ativo', 'checkbox'],
            ['cppAtivoPar', 'CPP Ativo', 'checkbox'],
            ['cbsDeduzirPar', 'CBS Dedução', 'number'],
            ['ibsDeduzirPar', 'IBS Dedução', 'number'],
            ['tipoSelagem', 'Tipo Selagem'],
            ['modeloCertidao', 'Modelo Certidão'],
            ['codigoTabela', 'Código Tabela'],
            ['codigoLivro', 'Código Livro'],
            ['dataInicioPeriodo', 'Data Início Período', 'date']
        ];
        
        return `
            <div class="tab-header"><h3>Funarpen (Campos 51-63)</h3></div>
            <div class="form-grid">
                ${fields.map(([name, label, type, options]) => this.renderField(name, label, type, options)).join('')}
            </div>
        `;
    }

    renderArquivos() {
        const fields = [
            ['diretorioDocs', 'Diretório Documentos'],
            ['diretorioRelatorios', 'Diretório Relatórios'],
            ['diretorioImportacao', 'Diretório Importação'],
            ['diretorioExportacao', 'Diretório Exportação'],
            ['prefixoArquivo', 'Prefixo Arquivo'],
            ['formatoArquivo', 'Formato Arquivo'],
            ['separadorCSV', 'Separador CSV'],
            ['encodingArquivo', 'Encoding Arquivo'],
            ['nomeArquivoRetorno', 'Nome Arquivo Retorno']
        ];
        
        return `
            <div class="tab-header"><h3>Arquivos (Campos 64-72)</h3></div>
            <div class="form-grid">
                ${fields.map(([name, label, type, options]) => this.renderField(name, label, type, options)).join('')}
            </div>
        `;
    }

    renderBoleto() {
        const fields = [
            ['bancoBoleto', 'Banco'],
            ['agenciaBoleto', 'Agência'],
            ['contaBoleto', 'Conta'],
            ['convenioBoleto', 'Convênio'],
            ['carteiraBoleto', 'Carteira'],
            ['codigoCedente', 'Código Cedente'],
            ['nossoNumeroInicial', 'Número Inicial'],
            ['digitoVerificador', 'Dígito Verificador'],
            ['instrucaoBoleto', 'Instrução'],
            ['mensagemBoleto', 'Mensagem'],
            ['localPagamento', 'Local Pagamento'],
            ['codigoBarras', 'Código Barras'],
            ['tipoCobranca', 'Tipo Cobrança'],
            ['vencimentoAutomatico', 'Vencimento Automático', 'checkbox']
        ];
        
        return `
            <div class="tab-header"><h3>Boleto (Campos 73-86)</h3></div>
            <div class="form-grid">
                ${fields.map(([name, label, type, options]) => this.renderField(name, label, type, options)).join('')}
            </div>
        `;
    }

    renderNFSe() {
        const fields = [
            ['nfseMunicipio', 'Município NFSe'],
            ['nfseAmbiente', 'Ambiente', 'select', ['PRODUÇÃO', 'HOMOLOGAÇÃO']],
            ['nfseUrl', 'URL NFSe'],
            ['nfseToken', 'Token NFSe']
        ];
        
        return `
            <div class="tab-header"><h3>NFSe (Campos 87-90)</h3></div>
            <div class="form-grid">
                ${fields.map(([name, label, type, options]) => this.renderField(name, label, type, options)).join('')}
            </div>
        `;
    }

    renderCabecalho() {
        return `
            <div class="tab-header"><h3>Cabeçalho (Campo 45)</h3></div>
            <div class="form-grid">
                ${this.renderField('cabecalhoPersonalizado', 'Cabeçalho Personalizado')}
            </div>
        `;
    }

    renderHistorico() {
        if (this.historico.length === 0) {
            return `
                <div class="tab-header"><h3>Histórico de Alterações</h3></div>
                <p class="no-data">Nenhum registro encontrado</p>
            `;
        }
        
        return `
            <div class="tab-header"><h3>Histórico de Alterações</h3></div>
            <div class="table-container">
                <table class="data-table">
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
                        ${this.historico.map(item => `
                            <tr>
                                <td>${this.formatDateTime(item.dataAlteracao)}</td>
                                <td>${item.nomeCampo}</td>
                                <td>${item.valorAnterior || '-'}</td>
                                <td>${item.valorNovo || '-'}</td>
                                <td>${item.usuarioAlteracao}</td>
                                <td>${item.ipOrigem}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
            <button class="btn btn-secondary" onclick="window.parametrosPage.carregarHistorico(1)">
                Carregar mais
            </button>
        `;
    }

    mount(container) {
        if (typeof container === 'string') {
            container = document.querySelector(container);
        }
        
        if (!container) {
            console.error('ParametrosPage: Container não encontrado');
            return;
        }

        container.innerHTML = '<div id="parametros-page"></div>';
        window.parametrosPage = this;
        this.init();
    }
}

// Exportar para uso global
window.ParametrosPage = ParametrosPage;