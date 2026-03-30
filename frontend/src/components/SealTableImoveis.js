/**
 * SealTableImoveis Component - Tabela de imóveis registrados para o ato
 * Tabela: protimoveis LEFT JOIN ato.protoc_livro_ato = protimoveis.CODIGO_PROI
 */

class SealTableImoveis {
    constructor(props = {}) {
        this.props = {
            imoveis: props.imoveis || [],
            onRemove: props.onRemove || null,
            onEdit: props.onEdit || null
        };
        
        this.element = null;
    }

    render() {
        const { imoveis } = this.props;
        
        if (imoveis.length === 0) {
            return `
                <div class="card seal-table">
                    <h2 class="card-title">Imóveis Registrados</h2>
                    <div class="empty-state">
                        <i class="fa-solid fa-home"></i>
                        <p>Nenhum imóvel registrado para este ato</p>
                    </div>
                </div>
            `;
        }
        
        return `
            <div class="card seal-table">
                <h2 class="card-title">Imóveis Registrados (${imoveis.length})</h2>
                <div class="table-container">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Matrícula</th>
                                <th>Localização</th>
                                <th>Área (m²)</th>
                                <th>Valor</th>
                                <th>Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${imoveis.map(imovel => this.renderRow(imovel)).join('')}
                        </tbody>
                    </table>
                </div>
            </div>
        `;
    }

    renderRow(imovel) {
        return `
            <tr class="table-row" data-id="${imovel.id}">
                <td class="table-cell">${imovel.matricula || 'N/A'}</td>
                <td class="table-cell">${imovel.localizacao || 'N/A'}</td>
                <td class="table-cell">${imovel.area || 'N/A'}</td>
                <td class="table-cell valor-cell">${this.formatCurrency(imovel.valor || 0)}</td>
                <td class="table-cell actions-cell">
                    <button class="btn-action btn-edit" onclick="window.sealTableImoveis.handleEdit(${imovel.id})" title="Editar">
                        <i class="fa-solid fa-pen"></i>
                    </button>
                    <button class="btn-action btn-delete" onclick="window.sealTableImoveis.handleRemove(${imovel.id})" title="Remover">
                        <i class="fa-solid fa-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    }

    formatCurrency(value) {
        return new Intl.NumberFormat('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        }).format(value);
    }

    handleEdit(imovelId) {
        if (this.props.onEdit) {
            this.props.onEdit(imovelId);
        }
    }

    handleRemove(imovelId) {
        if (this.props.onRemove) {
            this.props.onRemove(imovelId);
        }
    }

    mount(container) {
        if (typeof container === 'string') {
            container = document.querySelector(container);
        }
        
        if (!container) {
            console.error('SealTableImoveis: Container não encontrado');
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
window.SealTableImoveis = SealTableImoveis;
