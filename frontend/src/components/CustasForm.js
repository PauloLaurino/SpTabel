/**
 * CustasForm - Componente principal de cálculo de custas cartoriais
 * Sistema de Cálculo de Custas - Tabelionato de Notas
 */
class CustasForm {
    constructor() {
        this.state = {
            // Seleções
            modulo: '',
            tipoAto: '',
            protocolo: '',
            
            // Dados do imóvel
            valorImovel: '',
            imoveisSelecionados: [],
            
            // Resultados
            resultados: null,
            loading: false,
            error: null
        };
        
        // Dados carregados
        this.modulos = [];
        this.tiposAto = [];
        this.protocolos = [];
        this.imoveisDisponiveis = [];
        
        this.init();
    }
    
    /**
     * Inicializa o componente
     */
    init() {
        this.render();
        this.bindEvents();
        this.carregarModulos();
    }
    
    /**
     * Renderiza o componente
     */
    render() {
        const container = document.getElementById('custas-form-container');
        if (!container) return;
        
        container.innerHTML = `
            <div class="custas-form">
                <div class="form-section">
                    <h2 class="section-title">📋 Cálculo de Custas Cartoriais</h2>
                    
                    <!-- Módulo -->
                    <div class="form-group">
                        <label for="modulo-select" class="form-label">
                            <i class="fas fa-layer-group"></i> Módulo
                        </label>
                        <select id="modulo-select" class="form-select" required>
                            <option value="">Selecione o Módulo...</option>
                        </select>
                    </div>
                    
                    <!-- Tipo de Ato -->
                    <div class="form-group">
                        <label for="tipo-ato-select" class="form-label">
                            <i class="fas fa-file-signature"></i> Tipo de Ato
                        </label>
                        <select id="tipo-ato-select" class="form-select" required disabled>
                            <option value="">Selecione o Tipo de Ato...</option>
                        </select>
                    </div>
                    
                    <!-- Protocolo (Opcional) -->
                    <div class="form-group">
                        <label for="protocolo-select" class="form-label">
                            <i class="fas fa-file-alt"></i> Protocolo (Opcional)
                        </label>
                        <select id="protocolo-select" class="form-select">
                            <option value="">Selecione o Protocolo...</option>
                        </select>
                    </div>
                    
                    <!-- Valor do Imóvel / ITCD / Base de Cálculo -->
                    <div class="form-group">
                        <label for="valor-imovel" class="form-label">
                            <i class="fas fa-dollar-sign"></i> Valor do Imóvel / ITCD / Base de Cálculo (R$)
                        </label>
                        <input 
                            type="text" 
                            id="valor-imovel" 
                            class="form-input"
                            placeholder="0,00"
                            required
                        >
                        <small class="form-hint">Digite o valor em Reais (ex: 530.154,25)</small>
                    </div>
                    
                    <!-- Botões de Ação -->
                    <div class="form-actions">
                        <button id="btn-buscar-imoveis" class="btn btn-secondary" disabled>
                            <i class="fas fa-search"></i> Buscar Imóveis
                        </button>
                        <button id="btn-calcular" class="btn btn-primary" disabled>
                            <i class="fas fa-calculator"></i> Calcular Custas
                        </button>
                        <button id="btn-limpar" class="btn btn-outline">
                            <i class="fas fa-eraser"></i> Limpar
                        </button>
                    </div>
                </div>
                
                <!-- Seção de Imóveis (Opcional) -->
                <div id="imoveis-section" class="form-section hidden">
                    <h3 class="section-subtitle">🏠 Imóveis Selecionados</h3>
                    <div id="imoveis-lista" class="imoveis-lista"></div>
                    
                    <!-- Formulário Manual de Imóvel -->
                    <div id="imovel-manual-form" class="imovel-manual-form hidden">
                        <h4 class="section-subtitle">✏️ Adicionar Imóvel Manualmente</h4>
                        <div class="form-grid">
                            <div class="form-group">
                                <label for="imovel-matricula" class="form-label">Matrícula</label>
                                <input type="text" id="imovel-matricula" class="form-input" placeholder="12345">
                            </div>
                            <div class="form-group">
                                <label for="imovel-endereco" class="form-label">Endereço</label>
                                <input type="text" id="imovel-endereco" class="form-input" placeholder="Rua, número">
                            </div>
                            <div class="form-group">
                                <label for="imovel-bairro" class="form-label">Bairro</label>
                                <input type="text" id="imovel-bairro" class="form-input" placeholder="Centro">
                            </div>
                            <div class="form-group">
                                <label for="imovel-cidade" class="form-label">Cidade</label>
                                <input type="text" id="imovel-cidade" class="form-input" placeholder="Maringá">
                            </div>
                        </div>
                        <button id="btn-adicionar-imovel" class="btn btn-success">
                            <i class="fas fa-plus"></i> Adicionar Imóvel
                        </button>
                    </div>
                    
                    <button id="btn-adicionar-manual" class="btn btn-outline btn-small">
                        <i class="fas fa-plus-circle"></i> Adicionar Imóvel Manualmente
                    </button>
                </div>
                
                <!-- Seção de Resultados -->
                <div id="resultados-section" class="form-section hidden">
                    <h3 class="section-subtitle">💰 Resultado do Cálculo</h3>
                    <div id="resultados-container"></div>
                </div>
                
                <!-- Mensagens de Erro -->
                <div id="error-message" class="error-message hidden"></div>
            </div>
        `;
    }
    
    /**
     * Vincula eventos aos elementos
     */
    bindEvents() {
        // Módulo
        document.getElementById('modulo-select').addEventListener('change', (e) => {
            this.state.modulo = e.target.value;
            this.carregarTiposAto(e.target.value);
        });
        
        // Tipo de Ato
        document.getElementById('tipo-ato-select').addEventListener('change', (e) => {
            this.state.tipoAto = e.target.value;
            this.validarFormulario();
        });
        
        // Protocolo
        document.getElementById('protocolo-select').addEventListener('change', (e) => {
            this.state.protocolo = e.target.value;
            if (e.target.value) {
                this.carregarImoveisPorProtocolo(e.target.value);
            } else {
                this.limparImoveis();
            }
        });
        
        // Valor do Imóvel - Formatação
        document.getElementById('valor-imovel').addEventListener('input', (e) => {
            this.formatarValorMonetario(e.target);
            this.state.valorImovel = e.target.value;
            this.validarFormulario();
        });
        
        // Buscar Imóveis
        document.getElementById('btn-buscar-imoveis').addEventListener('click', () => {
            this.buscarImoveis();
        });
        
        // Calcular Custas
        document.getElementById('btn-calcular').addEventListener('click', () => {
            this.calcularCustas();
        });
        
        // Limpar
        document.getElementById('btn-limpar').addEventListener('click', () => {
            this.limparFormulario();
        });
        
        // Adicionar Imóvel Manualmente
        document.getElementById('btn-adicionar-manual').addEventListener('click', () => {
            this.toggleFormularioManual();
        });
        
        // Adicionar Imóvel
        document.getElementById('btn-adicionar-imovel').addEventListener('click', () => {
            this.adicionarImovelManual();
        });
    }
    
    /**
     * Carrega a lista de módulos
     */
    async carregarModulos() {
        try {
            const response = await fetch('/api/custas/modulos');
            const data = await response.json();
            
            if (data.success) {
                this.modulos = data.modulos;
                this.preencherSelectModulos();
            }
        } catch (error) {
            this.mostrarErro('Erro ao carregar módulos: ' + error.message);
        }
    }
    
    /**
     * Preenche o select de módulos
     */
    preencherSelectModulos() {
        const select = document.getElementById('modulo-select');
        select.innerHTML = '<option value="">Selecione o Módulo...</option>';
        
        this.modulos.forEach(modulo => {
            const option = document.createElement('option');
            option.value = modulo.codigo;
            option.textContent = modulo.descricao;
            select.appendChild(option);
        });
    }
    
    /**
     * Carrega os tipos de ato por módulo
     */
    async carregarTiposAto(modulo) {
        const select = document.getElementById('tipo-ato-select');
        select.innerHTML = '<option value="">Selecione o Tipo de Ato...</option>';
        select.disabled = true;
        
        if (!modulo) return;
        
        try {
            const response = await fetch(`/api/custas/tipos-ato?modulo=${modulo}`);
            const data = await response.json();
            
            if (data.success) {
                this.tiposAto = data.tipos;
                this.preencherSelectTiposAto();
                select.disabled = false;
            }
        } catch (error) {
            this.mostrarErro('Erro ao carregar tipos de ato: ' + error.message);
        }
    }
    
    /**
     * Preenche o select de tipos de ato
     */
    preencherSelectTiposAto() {
        const select = document.getElementById('tipo-ato-select');
        select.innerHTML = '<option value="">Selecione o Tipo de Ato...</option>';
        
        this.tiposAto.forEach(tipo => {
            const option = document.createElement('option');
            option.value = tipo.codigo;
            option.textContent = tipo.descricao;
            select.appendChild(option);
        });
    }
    
    /**
     * Carrega a lista de protocolos
     */
    async carregarProtocolos() {
        try {
            const response = await fetch('/api/custas/protocolos');
            const data = await response.json();
            
            if (data.success) {
                this.protocolos = data.protocolos;
                this.preencherSelectProtocolos();
            }
        } catch (error) {
            this.mostrarErro('Erro ao carregar protocolos: ' + error.message);
        }
    }
    
    /**
     * Preenche o select de protocolos
     */
    preencherSelectProtocolos() {
        const select = document.getElementById('protocolo-select');
        select.innerHTML = '<option value="">Selecione o Protocolo...</option>';
        
        this.protocolos.forEach(protocolo => {
            const option = document.createElement('option');
            option.value = protocolo.codigo_pro;
            option.textContent = protocolo.nome_apres_pro;
            select.appendChild(option);
        });
    }
    
    /**
     * Carrega imóveis por protocolo
     */
    async carregarImoveisPorProtocolo(protocolo) {
        try {
            const response = await fetch(`/api/custas/imoveis-protocolo?protocolo=${protocolo}`);
            const data = await response.json();
            
            if (data.success) {
                this.imoveisDisponiveis = data.imoveis;
                this.renderizarImoveisDisponiveis();
            }
        } catch (error) {
            this.mostrarErro('Erro ao carregar imóveis: ' + error.message);
        }
    }
    
    /**
     * Renderiza a lista de imóveis disponíveis
     */
    renderizarImoveisDisponiveis() {
        const container = document.getElementById('imoveis-lista');
        container.innerHTML = '';
        
        if (this.imoveisDisponiveis.length === 0) {
            container.innerHTML = '<p class="empty-message">Nenhum imóvel encontrado para este protocolo.</p>';
            return;
        }
        
        this.imoveisDisponiveis.forEach(imovel => {
            const div = document.createElement('div');
            div.className = 'imovel-item';
            div.innerHTML = `
                <input type="checkbox" id="imovel-${imovel.matricula}" value="${imovel.matricula}">
                <label for="imovel-${imovel.matricula}">
                    <strong>Matrícula:</strong> ${imovel.matricula}<br>
                    <strong>Endereço:</strong> ${imovel.endereco || 'N/A'}<br>
                    <strong>Bairro:</strong> ${imovel.bairro || 'N/A'}<br>
                    <strong>Cidade:</strong> ${imovel.cidade || 'N/A'}
                </label>
            `;
            container.appendChild(div);
        });
        
        // Habilitar botão de buscar imóveis
        document.getElementById('btn-buscar-imoveis').disabled = false;
    }
    
    /**
     * Busca imóveis por termo
     */
    async buscarImoveis() {
        const termo = document.getElementById('valor-imovel').value;
        
        try {
            const response = await fetch(`/api/custas/imoveis?termo=${encodeURIComponent(termo)}`);
            const data = await response.json();
            
            if (data.success) {
                this.imoveisDisponiveis = data.imoveis;
                this.renderizarImoveisDisponiveis();
                document.getElementById('imoveis-section').classList.remove('hidden');
            }
        } catch (error) {
            this.mostrarErro('Erro ao buscar imóveis: ' + error.message);
        }
    }
    
    /**
     * Formata valor monetário
     */
    formatarValorMonetario(input) {
        let valor = input.value.replace(/\D/g, '');
        valor = (parseInt(valor) / 100).toFixed(2);
        
        if (isNaN(valor)) {
            input.value = '';
            return;
        }
        
        // Formatar como R$ 530.154,25
        const partes = valor.split('.');
        const inteiro = partes[0];
        const decimal = partes[1] || '00';
        
        // Adicionar separador de milhar
        const inteiroFormatado = inteiro.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
        
        input.value = `${inteiroFormatado},${decimal}`;
    }
    
    /**
     * Valida o formulário
     */
    validarFormulario() {
        const modulo = document.getElementById('modulo-select').value;
        const tipoAto = document.getElementById('tipo-ato-select').value;
        const valorImovel = document.getElementById('valor-imovel').value;
        
        const btnCalcular = document.getElementById('btn-calcular');
        
        if (modulo && tipoAto && valorImovel) {
            btnCalcular.disabled = false;
        } else {
            btnCalcular.disabled = true;
        }
    }
    
    /**
     * Calcula as custas
     */
    async calcularCustas() {
        this.state.loading = true;
        this.mostrarLoading();
        
        const payload = {
            tipoAto: this.state.tipoAto,
            baseCalculo: this.parseValorMonetario(this.state.valorImovel),
            imoveis: this.state.imoveisSelecionados
        };
        
        try {
            const response = await fetch('/api/custas/calcular', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            });
            
            const data = await response.json();
            
            if (data.success) {
                this.state.resultados = data.detalhes;
                this.renderizarResultados();
            } else {
                this.mostrarErro(data.mensagem);
            }
        } catch (error) {
            this.mostrarErro('Erro ao calcular custas: ' + error.message);
        } finally {
            this.state.loading = false;
            this.esconderLoading();
        }
    }
    
    /**
     * Parse valor monetário para número
     */
    parseValorMonetario(valor) {
        if (!valor) return 0;
        // Remove pontos de milhar e substitui vírgula por ponto
        return parseFloat(valor.replace(/\./g, '').replace(',', '.'));
    }
    
    /**
     * Renderiza os resultados
     */
    renderizarResultados() {
        const container = document.getElementById('resultados-container');
        const resultados = this.state.resultados;
        
        container.innerHTML = `
            <div class="resultados-grid">
                <div class="resultado-item">
                    <span class="resultado-label">Emolumentos</span>
                    <span class="resultado-valor">${this.formatarMoeda(resultados.emolumentos)}</span>
                </div>
                <div class="resultado-item">
                    <span class="resultado-label">FUNDEP (5%)</span>
                    <span class="resultado-valor">${this.formatarMoeda(resultados.fundep)}</span>
                </div>
                <div class="resultado-item">
                    <span class="resultado-label">ISS (5%)</span>
                    <span class="resultado-valor">${this.formatarMoeda(resultados.iss)}</span>
                </div>
                <div class="resultado-item">
                    <span class="resultado-label">FUNREJUS</span>
                    <span class="resultado-valor">${this.formatarMoeda(resultados.funrejus)}</span>
                </div>
                <div class="resultado-item">
                    <span class="resultado-label">SELO</span>
                    <span class="resultado-valor">${this.formatarMoeda(resultados.selos)}</span>
                </div>
                <div class="resultado-item">
                    <span class="resultado-label">Distribuição</span>
                    <span class="resultado-valor">${this.formatarMoeda(resultados.distribuicao)}</span>
                </div>
                <div class="resultado-item resultado-total">
                    <span class="resultado-label">TOTAL</span>
                    <span class="resultado-valor">${this.formatarMoeda(resultados.total)}</span>
                </div>
            </div>
        `;
        
        document.getElementById('resultados-section').classList.remove('hidden');
    }
    
    /**
     * Formata valor como moeda
     */
    formatarMoeda(valor) {
        return new Intl.NumberFormat('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        }).format(valor);
    }
    
    /**
     * Mostra mensagem de erro
     */
    mostrarErro(mensagem) {
        const errorDiv = document.getElementById('error-message');
        errorDiv.textContent = mensagem;
        errorDiv.classList.remove('hidden');
        
        setTimeout(() => {
            errorDiv.classList.add('hidden');
        }, 5000);
    }
    
    /**
     * Mostra loading
     */
    mostrarLoading() {
        const btnCalcular = document.getElementById('btn-calcular');
        btnCalcular.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Calculando...';
        btnCalcular.disabled = true;
    }
    
    /**
     * Esconde loading
     */
    esconderLoading() {
        const btnCalcular = document.getElementById('btn-calcular');
        btnCalcular.innerHTML = '<i class="fas fa-calculator"></i> Calcular Custas';
        this.validarFormulario();
    }
    
    /**
     * Limpa o formulário
     */
    limparFormulario() {
        this.state = {
            modulo: '',
            tipoAto: '',
            protocolo: '',
            valorImovel: '',
            imoveisSelecionados: [],
            resultados: null,
            loading: false,
            error: null
        };
        
        document.getElementById('modulo-select').value = '';
        document.getElementById('tipo-ato-select').value = '';
        document.getElementById('tipo-ato-select').disabled = true;
        document.getElementById('protocolo-select').value = '';
        document.getElementById('valor-imovel').value = '';
        document.getElementById('imoveis-lista').innerHTML = '';
        document.getElementById('resultados-container').innerHTML = '';
        document.getElementById('imoveis-section').classList.add('hidden');
        document.getElementById('resultados-section').classList.add('hidden');
        document.getElementById('imovel-manual-form').classList.add('hidden');
        document.getElementById('btn-buscar-imoveis').disabled = true;
        document.getElementById('btn-calcular').disabled = true;
    }
    
    /**
     * Limpa a lista de imóveis
     */
    limparImoveis() {
        this.imoveisDisponiveis = [];
        document.getElementById('imoveis-lista').innerHTML = '';
        document.getElementById('imoveis-section').classList.add('hidden');
        document.getElementById('btn-buscar-imoveis').disabled = true;
    }
    
    /**
     * Toggle formulário manual
     */
    toggleFormularioManual() {
        const form = document.getElementById('imovel-manual-form');
        form.classList.toggle('hidden');
    }
    
    /**
     * Adiciona imóvel manualmente
     */
    adicionarImovelManual() {
        const matricula = document.getElementById('imovel-matricula').value;
        const endereco = document.getElementById('imovel-endereco').value;
        const bairro = document.getElementById('imovel-bairro').value;
        const cidade = document.getElementById('imovel-cidade').value;
        
        if (!matricula) {
            this.mostrarErro('Informe a matrícula do imóvel');
            return;
        }
        
        const imovel = {
            matricula,
            endereco,
            bairro,
            cidade,
            manual: true
        };
        
        this.state.imoveisSelecionados.push(imovel);
        this.renderizarImovelAdicionado(imovel);
        
        // Limpar formulário
        document.getElementById('imovel-matricula').value = '';
        document.getElementById('imovel-endereco').value = '';
        document.getElementById('imovel-bairro').value = '';
        document.getElementById('imovel-cidade').value = '';
        
        this.mostrarErro('Imóvel adicionado com sucesso!');
    }
    
    /**
     * Renderiza imóvel adicionado
     */
    renderizarImovelAdicionado(imovel) {
        const container = document.getElementById('imoveis-lista');
        
        const div = document.createElement('div');
        div.className = 'imovel-item imovel-manual';
        div.innerHTML = `
            <input type="checkbox" checked disabled>
            <label>
                <strong>Matrícula:</strong> ${imovel.matricula}<br>
                <strong>Endereço:</strong> ${imovel.endereco || 'N/A'}<br>
                <strong>Bairro:</strong> ${imovel.bairro || 'N/A'}<br>
                <strong>Cidade:</strong> ${imovel.cidade || 'N/A'}
                <span class="badge-manual">Manual</span>
            </label>
        `;
        container.appendChild(div);
    }
}

// Inicializar quando o DOM estiver pronto
document.addEventListener('DOMContentLoaded', () => {
    new CustasForm();
});
