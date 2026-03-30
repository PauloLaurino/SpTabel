/**
 * SealTableResultados Component - Tabela de resultados de custas
 * Mostra: Emolumentos, FUNDEP, ISS, FUNREJUS, SELO, TOTAL
 */

class SealTableResultados {
    constructor(props = {}) {
        this.props = {
            resultado: props.resultado || null,
            onPrint: props.onPrint || null,
            onExport: props.onExport || null
        };
        
        this.element = null;
    }

    render() {
        const { resultado } = this.props;
        
        if (!resultado) {
            return `
                <div class="card seal-table">
                    <h2 class="card-title">Resultados do Cálculo</h2>
                    <div class="empty-state">
                        <i class="fa-solid fa-calculator"></i>
                        <p>Selecione um ato e imóveis para calcular as custas</p>
                    </div>
                </div>
            `;
        }
        
        return `
            <div class="card seal-table">
                <h2 class="card-title">Resultados do Cálculo</h2>
                <div class="table-container">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Descrição</th>
                                <th>Valor</th>
                                <th>Percentual</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${this.renderEmolumentos(resultado)}
                            ${this.renderFundep(resultado)}
                            ${this.renderIss(resultado)}
                            ${this.renderFunrejus(resultado)}
                            ${this.renderSelo(resultado)}
                            ${this.renderDistribuicao(resultado)}
                            ${this.renderTotal(resultado)}
                        </tbody>
                    </table>
                </div>
                <div class="actions-container">
                    <button class="btn-action btn-print" onclick="window.sealTableResultados.handlePrint()">
                        <i class="fa-solid fa-print"></i>
                        Imprimir
                    </button>
                    <button class="btn-action btn-export" onclick="window.sealTableResultados.handleExport()">
                        <i class="fa-solid fa-file-export"></i>
                        Exportar
                    </button>
                </div>
            </div>
        `;
    }

    renderEmolumentos(resultado) {
        return `
            <tr class="row-emolumentos">
                <td class="table-cell">
                    <i class="fa-solid fa-file-signature"></i>
                    Emolumentos
                </td>
                <td class="table-cell valor-cell">${this.formatCurrency(resultado.emolumento || 0)}</td>
                <td class="table-cell">-</td>
            </tr>
        `;
    }

    renderFundep(resultado) {
        return `
            <tr class="row-fundep">
                <td class="table-cell">
                    <i class="fa-solid fa-graduation-cap"></i>
                    FUNDEP (5%)
                </td>
                <td class="table-cell valor-cell">${this.formatCurrency(resultado.fundep || 0)}</td>
                <td class="table-cell">5%</td>
            </tr>
        `;
    }

    renderIss(resultado) {
        return `
            <tr class="row-iss">
                <td class="table-cell">
                    <i class="fa-solid fa-building"></i>
                    ISS (5%)
                </td>
                <td class="table-cell valor-cell">${this.formatCurrency(resultado.iss || 0)}</td>
                <td class="table-cell">5%</td>
            </tr>
        `;
    }

    renderFunrejus(resultado) {
        return `
            <tr class="row-funrejus">
                <td class="table-cell">
                    <i class="fa-solid fa-gavel"></i>
                    FUNREJUS (0,25% + 0,002%)
                </td>
                <td class="table-cell valor-cell">${this.formatCurrency(resultado.funrejus || 0)}</td>
                <td class="table-cell">0,252%</td>
            </tr>
        `;
    }

    renderSelo(resultado) {
        return `
            <tr class="row-selo">
                <td class="table-cell">
                    <i class="fa-solid fa-stamp"></i>
                    SELO (${resultado.tipoSelo || 'TN1'})
                </td>
                <td class="table-cell valor-cell">${this.formatCurrency(resultado.selo || 0)}</td>
                <td class="table-cell">-</td>
            </tr>
        `;
    }

    renderDistribuicao(resultado) {
        return `
            <tr class="row-distribuicao">
                <td class="table-cell">
                    <i class="fa-solid fa-share-nodes"></i>
                    Distribuição
                </td>
                <td class="table-cell valor-cell">${this.formatCurrency(resultado.distribuicao || 0)}</td>
                <td class="table-cell">-</td>
            </tr>
        `;
    }

    renderTotal(resultado) {
        return `
            <tr class="row-total">
                <td class="table-cell">
                    <strong>TOTAL</strong>
                </td>
                <td class="table-cell valor-cell total-cell">
                    <strong>${this.formatCurrency(resultado.total || 0)}</strong>
                </td>
                <td class="table-cell">-</td>
            </tr>
        `;
    }

    formatCurrency(value) {
        return new Intl.NumberFormat('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        }).format(value);
    }

    handlePrint() {
        if (this.props.onPrint) {
            this.props.onPrint();
        } else {
            window.print();
        }
    }

    handleExport() {
        if (this.props.onExport) {
            this.props.onExport();
        } else {
            alert('Função de exportação não implementada');
        }
    }

    mount(container) {
        if (typeof container === 'string') {
            container = document.querySelector(container);
        }
        
        if (!container) {
            console.error('SealTableResultados: Container não encontrado');
            return;
        }
        
        container.innerHTML = this.render();
        this.element = container.querySelector('.seal-table');
    }

    updateProps(newProps) {
        this.props = { ...this.props, ...newProps };
        if (this.element) {
            this.element.innerHTML = this.render();
        }
    }

    destroy() {
        if (this.element) {
            this.element.remove();
        }
    }
}

// Exportar para uso global
window.SealTableResultados = SealTableResultados;
