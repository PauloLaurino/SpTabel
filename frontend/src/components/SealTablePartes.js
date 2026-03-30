/**
 * SealTablePartes Component - Tabela de partes (imovpartes)
 * Tabela: imovpartes LEFT JOIN ato.protoc_livro_ato = imovpartes.PROTOCOLO_IMOP
 * AND protimoveis.ID_IMO = imovpartes.ID_IMO (componente SealTable selecionado na busca de imóveis)
 * 
 * QUALIDIC_IMOP:
 * - T (Vendedor) - Compra e Venda
 * - D (Comprador) - Compra e Venda
 * - T (Falecido) - Inventário
 * - D (Herdeiro) - Inventário
 */

class SealTablePartes {
    constructor(props = {}) {
        this.props = {
            partes: props.partes || [],
            tipoAto: props.tipoAto || null, // 'COMPRA_VENDA' ou 'INVENTARIO'
            onRemove: props.onRemove || null,
            onEdit: props.onEdit || null
        };
        
        this.element = null;
    }

    render() {
        const { partes, tipoAto } = this.props;
        
        if (partes.length === 0) {
            return `
                <div class="card seal-table">
                    <h2 class="card-title">Partes do Ato</h2>
                    <div class="empty-state">
                        <i class="fa-solid fa-users"></i>
                        <p>Nenhuma parte registrada</p>
                    </div>
                </div>
            `;
        }
        
        return `
            <div class="card seal-table">
                <h2 class="card-title">Partes do Ato (${partes.length})</h2>
                <div class="table-container">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Nome</th>
                                <th>CPF/CNPJ</th>
                                <th>Qualificação</th>
                                <th>Imóvel</th>
                                <th>Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${partes.map(parte => this.renderRow(parte)).join('')}
                        </tbody>
                    </table>
                </div>
            </div>
        `;
    }

    renderRow(parte) {
        const qualificacao = this.getQualificacao(parte.qualidic_imop);
        const qualificacaoClass = this.getQualificacaoClass(parte.qualidic_imop);
        
        return `
            <tr class="table-row" data-id="${parte.id}">
                <td class="table-cell">${parte.nome || 'N/A'}</td>
                <td class="table-cell">${parte.cpf_cnpj || 'N/A'}</td>
                <td class="table-cell">
                    <span class="qualificacao-badge ${qualificacaoClass}">
                        ${qualificacao}
                    </span>
                </td>
                <td class="table-cell">${parte.imovel_matricula || 'N/A'}</td>
                <td class="table-cell actions-cell">
                    <button class="btn-action btn-edit" onclick="window.sealTablePartes.handleEdit(${parte.id})" title="Editar">
                        <i class="fa-solid fa-pen"></i>
                    </button>
                    <button class="btn-action btn-delete" onclick="window.sealTablePartes.handleRemove(${parte.id})" title="Remover">
                        <i class="fa-solid fa-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    }

    getQualificacao(qualidic) {
        const { tipoAto } = this.props;
        
        if (!tipoAto) return qualidic || 'N/A';
        
        switch (tipoAto.toUpperCase()) {
            case 'COMPRA_VENDA':
                switch (qualidic) {
                    case 'T': return 'Vendedor';
                    case 'D': return 'Comprador';
                    default: return qualidic || 'N/A';
                }
            case 'INVENTARIO':
                switch (qualidic) {
                    case 'T': return 'Falecido';
                    case 'D': return 'Herdeiro';
                    default: return qualidic || 'N/A';
                }
            default:
                return qualidic || 'N/A';
        }
    }

    getQualificacaoClass(qualidic) {
        const { tipoAto } = this.props;
        
        if (!tipoAto) return 'qual-default';
        
        switch (tipoAto.toUpperCase()) {
            case 'COMPRA_VENDA':
                switch (qualidic) {
                    case 'T': return 'qual-vendedor';
                    case 'D': return 'qual-comprador';
                    default: return 'qual-default';
                }
            case 'INVENTARIO':
                switch (qualidic) {
                    case 'T': return 'qual-falecido';
                    case 'D': return 'qual-herdeiro';
                    default: return 'qual-default';
                }
            default:
                return 'qual-default';
        }
    }

    handleEdit(parteId) {
        if (this.props.onEdit) {
            this.props.onEdit(parteId);
        }
    }

    handleRemove(parteId) {
        if (this.props.onRemove) {
            this.props.onRemove(parteId);
        }
    }

    mount(container) {
        if (typeof container === 'string') {
            container = document.querySelector(container);
        }
        
        if (!container) {
            console.error('SealTablePartes: Container não encontrado');
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
window.SealTablePartes = SealTablePartes;
