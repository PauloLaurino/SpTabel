// ============================================
// CUSTAS APP - Aplicação de Cálculo de Custas Cartoriais
// ============================================

function CustasApp() {
    console.log('🛠️ Criando instância CustasApp...');

    this.config = {
        apiBaseUrl: '/sptabel/api',
        timeout: 30000
    };

    this.elementos = {};
    this.dados = {
        atos: [],
        encargos: [],
        protocolos: [],
        imoveis: [],
        imoveisSelecionados: [],
        resultadoCalculo: null
    };

    // Verificar API
    if (window.custasApi) {
        this.api = window.custasApi;
    } else if (window.CustasApiClient) {
        this.api = new window.CustasApiClient();
    } else {
        console.warn('⚠️ API de custas não encontrada');
        this.api = this.criarAPIFallback();
    }

    console.log('✅ CustasApp instanciada');
}

CustasApp.prototype.inicializar = function () {
    console.log('🚀 Inicializando CustasApp...');

    this.initDOM();
    this.initEventListeners();
    this.carregarDadosIniciais();
    this.esconderLoadingInicial();
    this.initMascaraMoeda();

    // Emitir evento de inicialização
    if (window.eventBus) {
        window.eventBus.emit('custas:app:inicializada', {
            timestamp: new Date().toISOString()
        });
    }

    console.log('🎉 CustasApp completamente inicializado');
};

CustasApp.prototype.initDOM = function () {
    console.log('🏗️ Inicializando DOM...');

    // Elementos do formulário
    this.elementos = {
        // Seleção de ato
        tipoAtoSelect: document.getElementById('tipoAtoSelect'),
        atoInfo: document.getElementById('atoInfo'),
        atoDescricao: document.getElementById('atoDescricao'),
        atoModulo: document.getElementById('atoModulo'),
        atoValorBase: document.getElementById('atoValorBase'),
        atoVrcMaximo: document.getElementById('atoVrcMaximo'),

        // Módulo e Protocolo
        moduloSelect: document.getElementById('moduloSelect'),
        protocoloSelect: document.getElementById('protocoloSelect'),

        // Base de cálculo
        baseCalculo: document.getElementById('baseCalculo'),

        // Imóveis
        imovelSearch: document.getElementById('imovelSearch'),
        imoveisTableBody: document.getElementById('imoveisTableBody'),
        selectAllImoveis: document.getElementById('selectAllImoveis'),
        selectedImoveisCount: document.getElementById('selectedImoveisCount'),

        // Botões
        btnCalcular: document.getElementById('btnCalcular'),
        btnLimpar: document.getElementById('btnLimpar'),
        btnImprimir: document.getElementById('btnImprimir'),
        btnSalvar: document.getElementById('btnSalvar'),

        // Resultado
        resultadoSection: document.getElementById('resultadoSection'),
        resultTableBody: document.getElementById('resultTableBody'),
        totalGeral: document.getElementById('totalGeral'),

        // Elementos de detalhamento
        resEmolumentos: document.getElementById('resEmolumentos'),
        resFUNDEP: document.getElementById('resFUNDEP'),
        resISS: document.getElementById('resISS'),
        resFUNREJUS: document.getElementById('resFUNREJUS'),
        resSelos: document.getElementById('resSelos'),
        resDistribuicao: document.getElementById('resDistribuicao'),
        resTotal: document.getElementById('resTotal')
    };

    console.log('✅ DOM inicializado, elementos capturados');
    return this.elementos;
};

CustasApp.prototype.initMascaraMoeda = function () {
    var input = this.elementos.baseCalculo;
    if (!input) return;

    var self = this;

    input.addEventListener('input', function (e) {
        var value = e.target.value.replace(/\D/g, '');
        if (value === '') {
            e.target.value = '';
            return;
        }
        value = parseInt(value) / 100;
        e.target.value = self.formatarMoeda(value).replace('R$ ', '');
    });

    input.addEventListener('blur', function (e) {
        var value = e.target.value.replace(/\D/g, '');
        if (value === '') {
            e.target.value = '';
            return;
        }
        value = parseInt(value) / 100;
        e.target.value = self.formatarMoeda(value).replace('R$ ', '');
    });
};

CustasApp.prototype.initEventListeners = function () {
    console.log('🎮 Inicializando event listeners...');

    var self = this;

    // Módulo - carregar tipos de ato
    if (this.elementos.moduloSelect) {
        this.elementos.moduloSelect.addEventListener('change', function () {
            self.carregarAtosPorModulo(this.value);
        });
    }

    // Tipo de ato
    if (this.elementos.tipoAtoSelect) {
        this.elementos.tipoAtoSelect.addEventListener('change', function () {
            self.onTipoAtoChange(this.value);
        });
    }

    // Protocolo - carregar imóveis
    if (this.elementos.protocoloSelect) {
        this.elementos.protocoloSelect.addEventListener('change', function () {
            self.onProtocoloChange(this.value);
        });
    }

    // Busca de imóveis
    if (this.elementos.imovelSearch) {
        this.elementos.imovelSearch.addEventListener('input', function () {
            self.filtrarImoveis(this.value);
        });
    }

    // Selecionar todos os imóveis
    if (this.elementos.selectAllImoveis) {
        this.elementos.selectAllImoveis.addEventListener('change', function () {
            self.selecionarTodosImoveis(this.checked);
        });
    }

    // Base de cálculo alterada
    if (this.elementos.baseCalculo) {
        this.elementos.baseCalculo.addEventListener('input', function () {
            self.verificarCalculoDisponivel();
        });
    }

    // Botões
    if (this.elementos.btnCalcular) {
        this.elementos.btnCalcular.addEventListener('click', function () {
            self.calcularCustas();
        });
    }

    if (this.elementos.btnLimpar) {
        this.elementos.btnLimpar.addEventListener('click', function () {
            self.limparFormulario();
        });
    }

    if (this.elementos.btnImprimir) {
        this.elementos.btnImprimir.addEventListener('click', function () {
            self.imprimirResultado();
        });
    }

    if (this.elementos.btnSalvar) {
        this.elementos.btnSalvar.addEventListener('click', function () {
            self.salvarCalculo();
        });
    }

    console.log('✅ Event listeners inicializados');
};

CustasApp.prototype.carregarDadosIniciais = function () {
    console.log('📦 Carregando dados iniciais...');

    var self = this;

    // Carregar módulos (valores fixos)
    this.dados.modulos = [
        { valor: 'N', descricao: 'N - Tabelionato de Notas' },
        { valor: 'R', descricao: 'R - Registro' },
        { valor: 'O', descricao: 'O - Outros' }
    ];

    // Carregar protocolos
    this.api.listarProtocolos()
        .then(function (response) {
            if (response.success) {
                self.dados.protocolos = response.protocolos || [];
                self.preencherSelectProtocolos(self.dados.protocolos);
                console.log('✅ Protocolos carregados: ' + self.dados.protocolos.length);
            }
        })
        .catch(function (error) {
            console.error('❌ Erro ao carregar protocolos:', error);
            // Continuar mesmo se protocolos falharem
            self.carregarImoveisMock();
        });

    // Carregar atos do módulo N por padrão
    this.carregarAtosPorModulo('N');

    // Carregar encargos
    this.api.listarEncargos()
        .then(function (response) {
            if (response.success) {
                self.dados.encargos = response.encargos || [];
                console.log('✅ Encargos carregados: ' + self.dados.encargos.length);
            }
        })
        .catch(function (error) {
            console.error('❌ Erro ao carregar encargos:', error);
        });
};

CustasApp.prototype.preencherSelectProtocolos = function (protocolos) {
    var select = this.elementos.protocoloSelect;
    if (!select) return;

    // Limpar opções existentes (exceto a primeira)
    while (select.options.length > 1) {
        select.remove(1);
    }

    // Adicionar opções
    for (var i = 0; i < protocolos.length; i++) {
        var protocolo = protocolos[i];
        var option = document.createElement('option');
        option.value = protocolo.codigo_pro;
        option.textContent = protocolo.codigo_pro + ' - ' + protocolo.nome_apres_pro;
        select.appendChild(option);
    }

    console.log('✅ Select de protocolos preenchido com ' + protocolos.length + ' opções');
};

CustasApp.prototype.preencherSelectAtos = function (atos) {
    var select = this.elementos.tipoAtoSelect;
    if (!select) return;

    // Limpar opções existentes (exceto a primeira)
    while (select.options.length > 1) {
        select.remove(1);
    }

    // Adicionar opções
    for (var i = 0; i < atos.length; i++) {
        var ato = atos[i];
        var option = document.createElement('option');
        option.value = ato.codigo;
        option.textContent = ato.codigo + ' - ' + ato.descricao;
        option.dataset.tipoAto = ato.tipoAto;
        option.dataset.descricao = ato.descricao;
        option.dataset.modulo = ato.modulo;
        option.dataset.valor = ato.valor;
        option.dataset.valorReferencia = ato.valorReferencia;
        option.dataset.agregado = ato.agregado;
        select.appendChild(option);
    }

    console.log('✅ Select de atos preenchido com ' + atos.length + ' opções');
};

CustasApp.prototype.carregarAtosPorModulo = function (modulo) {
    console.log('📋 Carregando atos para módulo: ' + modulo);

    var self = this;

    if (!modulo) {
        // Limpar select de atos
        var select = this.elementos.tipoAtoSelect;
        while (select && select.options.length > 1) {
            select.remove(1);
        }
        return;
    }

    this.api.listarAtosPorModulo(modulo)
        .then(function (response) {
            if (response.success) {
                self.dados.atos = response.atos || [];
                self.preencherSelectAtos(self.dados.atos);
            }
        })
        .catch(function (error) {
            console.error('❌ Erro ao carregar atos por módulo:', error);
        });
};

CustasApp.prototype.onProtocoloChange = function (protocolo) {
    console.log('📋 Protocolo selecionado: ' + protocolo);

    if (protocolo) {
        // Carregar dados do protocolo (tipo de ato, valores)
        this.carregarDadosProtocolo(protocolo);
        // Carregar imóveis do protocolo
        this.carregarImoveisPorProtocolo(protocolo);
    } else {
        // Carregar imóveis mock quando não há protocolo
        this.carregarImoveisMock();
    }
};

/**
 * Carrega os dados de um protocolo específico
 */
CustasApp.prototype.carregarDadosProtocolo = function (protocolo) {
    console.log('📋 Carregando dados do protocolo: ' + protocolo);

    var self = this;
    var apiBaseUrl = this.config.apiBaseUrl;

    fetch(apiBaseUrl + '/custas/protocolo/' + protocolo)
        .then(function (response) {
            return response.json();
        })
        .then(function (data) {
            if (data.success && data.protocolo) {
                var p = data.protocolo;
                console.log('✅ Dados do protocolo carregados:', p);

                // Armazenar dados do protocolo
                self.dados.protocoloData = p;

                // Preencher automaticamente o tipo de ato se disponível
                if (p.CODTIPOATO_PRO) {
                    var tipoAtoSelect = self.elementos.tipoAtoSelect;
                    if (tipoAtoSelect) {
                        // Procurar o tipo de ato no select
                        for (var i = 0; i < tipoAtoSelect.options.length; i++) {
                            if (tipoAtoSelect.options[i].value == p.CODTIPOATO_PRO) {
                                tipoAtoSelect.selectedIndex = i;
                                self.onTipoAtoChange(p.CODTIPOATO_PRO);
                                break;
                            }
                        }
                    }
                }

                // Preencher valor declarado (VLRDECLARADO) se disponível
                if (p.VLRDECLAR_PRO) {
                    var baseCalculo = self.elementos.baseCalculo;
                    if (baseCalculo) {
                        baseCalculo.value = self.formatarMoeda(p.VLRDECLAR_PRO.toString());
                    }
                }

                // Preencher valor avaliado (VLRAVALIADO) como alternativa
                if (p.VLRAVALIADO_PRO && !p.VLRDECLAR_PRO) {
                    var baseCalculo = self.elementos.baseCalculo;
                    if (baseCalculo) {
                        baseCalculo.value = self.formatarMoeda(p.VLRAVALIADO_PRO.toString());
                    }
                }

                // Preencher informações do protocolo na UI
                self.mostrarInfoProtocolo(p);
            }
        })
        .catch(function (error) {
            console.error('❌ Erro ao carregar dados do protocolo:', error);
        });
};

/**
 * Mostra informações do protocolo na UI
 */
CustasApp.prototype.mostrarInfoProtocolo = function (p) {
    // Você pode adicionar campos na UI para mostrar informações do protocolo
    console.log('📋 Informações do protocolo:', {
        'servico': p.SERVICO_PRO,
        'tipoAto': p.CODTIPOATO_PRO,
        'livro': p.LIVRO_PRO,
        'folha': p.FOLHA_PRO,
        'letra': p.LETRA_PRO,
        'valorDeclarado': p.VLRDECLAR_PRO,
        'valorAvaliado': p.VLRAVALIADO_PRO,
        'quantidadePartes': p.quantidade_partes,
        'quantidadeImoveis': p.quantidade_imoveis
    });
};

CustasApp.prototype.carregarImoveisPorProtocolo = function (protocolo) {
    console.log('📦 Carregando imóveis para protocolo: ' + protocolo);

    var self = this;

    this.api.listarImoveisPorProtocolo(protocolo)
        .then(function (response) {
            if (response.success) {
                var imoveis = response.imoveis || [];
                self.dados.imoveis = imoveis;
                self.renderizarTabelaImoveis(imoveis);
                console.log('✅ Imóveis carregados: ' + imoveis.length);
            }
        })
        .catch(function (error) {
            console.error('❌ Erro ao carregar imóveis por protocolo:', error);
            // Fallback para mock
            self.carregarImoveisMock();
        });
};

CustasApp.prototype.onTipoAtoChange = function (codigo) {
    console.log('📋 Tipo de ato selecionado: ' + codigo);

    var select = this.elementos.tipoAtoSelect;
    var option = select.options[select.selectedIndex];

    if (option && option.value) {
        // Mostrar informações do ato
        this.elementos.atoInfo.style.display = 'block';
        this.elementos.atoDescricao.textContent = option.dataset.descricao || '';
        this.elementos.atoModulo.textContent = this.getModuloLabel(option.dataset.modulo || '');
        this.elementos.atoValorBase.textContent = this.formatarMoeda(option.dataset.valor || '0');
        this.elementos.atoVrcMaximo.textContent = this.formatarMoeda(option.dataset.valorReferencia || '0');
    } else {
        this.elementos.atoInfo.style.display = 'none';
    }

    this.verificarCalculoDisponivel();
};

CustasApp.prototype.verificarCalculoDisponivel = function () {
    var tipoAtoSelecionado = this.elementos.tipoAtoSelect.value;
    var baseCalculo = this.elementos.baseCalculo.value;

    // Remover caracteres não numéricos para validar
    var valorBase = parseFloat(baseCalculo.replace(/\./g, '').replace(',', '.')) || 0;

    var habilitado = tipoAtoSelecionado && valorBase > 0;

    if (this.elementos.btnCalcular) {
        this.elementos.btnCalcular.disabled = !habilitado;
    }

    return habilitado;
};

CustasApp.prototype.calcularCustas = function () {
    console.log('🧮 Calculando custas...');

    var self = this;

    var tipoAtoSelect = this.elementos.tipoAtoSelect;
    var tipoAto = tipoAtoSelect.value;
    var baseCalculo = this.elementos.baseCalculo.value;
    // Remover formatação
    baseCalculo = parseFloat(baseCalculo.replace(/\./g, '').replace(',', '.')) || 0;

    var dados = {
        tipoAto: tipoAto,
        baseCalculo: baseCalculo,
        modulo: this.elementos.moduloSelect.value,
        protocolo: this.elementos.protocoloSelect.value,
        imoveis: this.dados.imoveisSelecionados
    };

    this.api.calcularCustas(dados)
        .then(function (response) {
            console.log('✅ Cálculo concluído:', response);

            if (response.success) {
                self.exibirResultado(response.detalhes);
                self.dados.resultadoCalculo = response;
            } else {
                alert('Erro no cálculo: ' + response.mensagem);
            }
        })
        .catch(function (error) {
            console.error('❌ Erro ao calcular custas:', error);
            alert('Erro ao calcular custas: ' + error.message);
        });
};

CustasApp.prototype.exibirResultado = function (detalhes) {
    console.log('📊 Exibindo resultado...', detalhes);

    this.elementos.resultadoSection.style.display = 'block';

    // Preencher detalhamento
    this.elementos.resEmolumentos.textContent = this.formatarMoeda(detalhes.emolumentos);
    this.elementos.resFUNDEP.textContent = this.formatarMoeda(detalhes.fundep);
    this.elementos.resISS.textContent = this.formatarMoeda(detalhes.iss);
    this.elementos.resFUNREJUS.textContent = this.formatarMoeda(detalhes.funrejus);
    this.elementos.resSelos.textContent = this.formatarMoeda(detalhes.selos);
    this.elementos.resDistribuicao.textContent = this.formatarMoeda(detalhes.distribuicao);
    this.elementos.resTotal.textContent = this.formatarMoeda(detalhes.total);
    this.elementos.totalGeral.textContent = this.formatarMoeda(detalhes.total);

    // Preencher tabela de atos
    var tbody = this.elementos.resultTableBody;
    tbody.innerHTML = '';

    if (detalhes.atos && detalhes.atos.length > 0) {
        for (var i = 0; i < detalhes.atos.length; i++) {
            var ato = detalhes.atos[i];
            var row = document.createElement('tr');
            row.innerHTML =
                '<td>' + ato.codigo + '</td>' +
                '<td>' + ato.descricao + '</td>' +
                '<td class="text-right">' + this.formatarMoeda(ato.valor) + '</td>';
            tbody.appendChild(row);
        }
    }

    // Scroll até o resultado
    this.elementos.resultadoSection.scrollIntoView({ behavior: 'smooth' });
};

CustasApp.prototype.limparFormulario = function () {
    console.log('🔄 Limpando formulário...');

    // Limpar seleção de ato
    this.elementos.tipoAtoSelect.selectedIndex = 0;
    this.elementos.atoInfo.style.display = 'none';

    // Limpar módulo
    this.elementos.moduloSelect.selectedIndex = 0;

    // Limpar protocolo
    this.elementos.protocoloSelect.selectedIndex = 0;

    // Limpar base de cálculo
    this.elementos.baseCalculo.value = '';

    // Limpar seleção de imóveis
    this.dados.imoveisSelecionados = [];
    this.atualizarCountImoveis();
    var checkboxes = this.elementos.imoveisTableBody.querySelectorAll('input[type="checkbox"]');
    for (var i = 0; i < checkboxes.length; i++) {
        checkboxes[i].checked = false;
    }
    this.elementos.selectAllImoveis.checked = false;

    // Ocultar resultado
    this.elementos.resultadoSection.style.display = 'none';
    this.dados.resultadoCalculo = null;

    // Desabilitar botão calcular
    this.elementos.btnCalcular.disabled = true;

    // Recarregar atos do módulo padrão
    this.carregarAtosPorModulo('N');
};

CustasApp.prototype.imprimirResultado = function () {
    window.print();
};

CustasApp.prototype.salvarCalculo = function () {
    console.log('💾 Salvando cálculo...');

    // TODO: Implementar salvamento
    alert('Funcionalidade de salvamento em desenvolvimento');
};

// ============================================
// Métodos auxiliares para imóveis
// ============================================

CustasApp.prototype.carregarImoveisMock = function () {
    // Mock de imóveis para teste
    var imoveisMock = [
        { id: 1, matricula: '1141', registro: '03', descricao: 'Apartamento Centro', valorItbi: 480995.00 },
        { id: 2, matricula: '2231', registro: '10', descricao: 'Casa Alto do Amparo', valorItbi: 113437.50 },
        { id: 3, matricula: '12154', registro: '03', descricao: 'Terreno Av. Donato', valorItbi: 247831.60 }
    ];

    this.dados.imoveis = imoveisMock;
    this.renderizarTabelaImoveis(imoveisMock);
};

CustasApp.prototype.renderizarTabelaImoveis = function (imoveis) {
    var tbody = this.elementos.imoveisTableBody;
    tbody.innerHTML = '';

    for (var i = 0; i < imoveis.length; i++) {
        var imovel = imoveis[i];
        var row = document.createElement('tr');
        var valor = imovel.valorItbi || imovel.VALOR_ITBI || 0;
        row.innerHTML =
            '<td><input type="checkbox" data-id="' + (imovel.id || i) + '" data-valor="' + valor + '"></td>' +
            '<td>' + (imovel.matricula || imovel.MATRIC_IMOP || '') + '</td>' +
            '<td>' + (imovel.registro || '') + '</td>' +
            '<td>' + (imovel.descricao || imovel.DESCR_IMOP || '') + '</td>' +
            '<td>' + this.formatarMoeda(valor) + '</td>';
        tbody.appendChild(row);

        // Adicionar evento de click na checkbox
        var checkbox = row.querySelector('input[type="checkbox"]');
        checkbox.addEventListener('change', this.onImovelCheckboxChange.bind(this));
    }
};

CustasApp.prototype.onImovelCheckboxChange = function (event) {
    var checkbox = event.target;
    var id = parseInt(checkbox.dataset.id);
    var valor = parseFloat(checkbox.dataset.valor);

    if (checkbox.checked) {
        if (this.dados.imoveisSelecionados.indexOf(id) === -1) {
            this.dados.imoveisSelecionados.push(id);
        }
    } else {
        var index = this.dados.imoveisSelecionados.indexOf(id);
        if (index > -1) {
            this.dados.imoveisSelecionados.splice(index, 1);
        }
    }

    this.atualizarCountImoveis();
    this.verificarCalculoDisponivel();
};

CustasApp.prototype.selecionarTodosImoveis = function (checked) {
    var checkboxes = this.elementos.imoveisTableBody.querySelectorAll('input[type="checkbox"]');

    this.dados.imoveisSelecionados = [];

    for (var i = 0; i < checkboxes.length; i++) {
        checkboxes[i].checked = checked;
        if (checked) {
            var id = parseInt(checkboxes[i].dataset.id);
            this.dados.imoveisSelecionados.push(id);
        }
    }

    this.atualizarCountImoveis();
    this.verificarCalculoDisponivel();
};

CustasApp.prototype.filtrarImoveis = function (search) {
    var term = search.toLowerCase();
    var rows = this.elementos.imoveisTableBody.querySelectorAll('tr');

    for (var i = 0; i < rows.length; i++) {
        var text = rows[i].textContent.toLowerCase();
        rows[i].style.display = text.indexOf(term) > -1 ? '' : 'none';
    }
};

CustasApp.prototype.atualizarCountImoveis = function () {
    var count = this.dados.imoveisSelecionados.length;
    this.elementos.selectedImoveisCount.textContent = count;
};

// ============================================
// Utilitários
// ============================================

CustasApp.prototype.formatarMoeda = function (valor) {
    if (valor === null || valor === undefined) return 'R$ 0,00';
    var num = parseFloat(valor);
    return 'R$ ' + num.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
};

CustasApp.prototype.getModuloLabel = function (modulo) {
    var labels = {
        'N': 'Tabelionato de Notas',
        'R': 'Registro',
        'O': 'Outros',
        'C': 'Custas/Encargos'
    };
    return labels[modulo] || modulo;
};

CustasApp.prototype.esconderLoadingInicial = function () {
    var loadingScreen = document.getElementById('loadingScreen');
    var appContainer = document.getElementById('appContainer');

    if (loadingScreen) {
        loadingScreen.style.display = 'none';
    }

    if (appContainer) {
        appContainer.style.display = 'block';
    }
};

CustasApp.prototype.criarAPIFallback = function () {
    return {
        listarModulos: function () {
            return Promise.reject(new Error('API não disponível'));
        },
        listarTiposAtos: function () {
            return Promise.reject(new Error('API não disponível'));
        },
        listarAtosPorModulo: function () {
            return Promise.reject(new Error('API não disponível'));
        },
        listarProtocolos: function () {
            return Promise.reject(new Error('API não disponível'));
        },
        listarImoveisPorProtocolo: function () {
            return Promise.reject(new Error('API não disponível'));
        },
        listarEncargos: function () {
            return Promise.reject(new Error('API não disponível'));
        },
        calcularCustas: function () {
            return Promise.reject(new Error('API não disponível'));
        }
    };
};

// ============================================
// Inicialização
// ============================================

if (typeof window !== 'undefined') {
    window.CustasApp = CustasApp;
    console.log('✅ CustasApp disponível globalmente');
}

// Auto-inicialização
window.inicializarCustasApp = function () {
    if (window.custasApp && window.custasApp instanceof CustasApp) {
        console.log('🔄 Aplicação de custas já inicializada');
        return window.custasApp;
    }

    try {
        window.custasApp = new CustasApp();
        window.custasApp.inicializar();
        return window.custasApp;
    } catch (error) {
        console.error('❌ Erro ao criar instância CustasApp:', error);
        return null;
    }
};

// Inicializar quando DOM estiver pronto
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function () {
        setTimeout(window.inicializarCustasApp, 300);
    });
} else {
    setTimeout(window.inicializarCustasApp, 300);
}

console.log('✅ custas-app.js - Aplicação de Cálculo de Custas Cartoriais');
