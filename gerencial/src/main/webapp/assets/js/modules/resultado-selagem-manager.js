// ============================================
// RESULTADO SELAGEM MANAGER - VERSÃO FIX
// ============================================

window.resultadoSelagemManager = (function () {
    'use strict';

    // 🔥 CONFIGURAÇÕES
    const CONFIG = {
        IDS_MODAL: {
            MODAL: 'modalResultadoSelagem',
            TITLE: 'modalResultadoTitulo',
            GRID_CONTAINER: 'gridResultadosContainer',
            RESUMO: 'resumoResultado',
            TOTAL_SELOS: 'totalSelosUtilizados',
            TOTAL_APONTAMENTOS: 'totalApontamentosSelados',
            BOTAO_FECHAR: 'btnFecharResultado',
            BOTAO_EXPORTAR: 'btnExportarResultado'
        }
    };

    // 🔥 INSTÂNCIAS DO AG-GRID
    let gridApi = null;

    // ============================================
    // FUNÇÃO PRINCIPAL CORRIGIDA
    // ============================================
    function exibirModalResultado(resultado) {
        console.log('📊 Exibindo resultados da selagem...');

        try {
            // 🔥 1. PROCESSAR DADOS
            var dados = processarDadosResultado(resultado);
            console.log('✅ Dados processados:', dados);

            // 🔥 2. CRIAR MODAL (SEMPRE criar, não verificar se existe)
            criarModal();

            // 🔥 3. ATUALIZAR RESUMO
            atualizarResumo(dados);

            // 🔥 4. INICIALIZAR GRID
            inicializarGrid(dados);

            // 🔥 5. EXIBIR MODAL
            mostrarModal();

            return dados;

        } catch (error) {
            console.error('❌ Erro:', error);
            mostrarResultadoSimples(resultado);
            return null;
        }
    }

    // ============================================
    // 🔥 PROCESSAR DADOS - VERSÃO FIX
    // ============================================
    function processarDadosResultado(resultado) {
        console.log('🔍 Processando dados...');

        // Dados do backend
        var dadosBackend = resultado.dados || resultado;

        var dados = {
            sucesso: resultado.success || dadosBackend.sucesso || false,
            mensagem: resultado.mensagem || dadosBackend.mensagem || '',

            // 🔥 ESTATÍSTICAS
            totalApontamentos: dadosBackend.total_apontamentos || 0,
            totalProcessados: dadosBackend.total_processados || 0,
            totalJaTinhaSelo: dadosBackend.total_ja_tinha_selos || 0,
            totalErros: dadosBackend.total_erros || 0,
            totalSelosUtilizados: dadosBackend.total_selos_utilizados || 0,

            // 🔥 DADOS PARA GRID
            apontamentos: dadosBackend.apontamentos || [],
            rowData: []
        };

        // 🔥 FORMATAR DADOS PARA GRID
        if (dados.apontamentos.length > 0) {
            dados.rowData = dados.apontamentos.map(function (apontamento, index) {
                return {
                    id: index + 1,
                    protocolo: apontamento.protocolo || '',
                    apontamento: (apontamento.numapo1 || '') + '/' + (apontamento.numapo2 || ''),
                    devedor: apontamento.devedor || '',
                    cpfcnpj: formatarCpfCnpj(apontamento.cpfcnpj || ''),
                    status: formatarStatus(apontamento.status || ''),
                    selos: formatarSelos(apontamento.selos || []),
                    observacao: apontamento.observacao || ''
                };
            });
        }

        console.log('📈 Estatísticas:', {
            total: dados.totalApontamentos,
            processados: dados.totalProcessados,
            jaTinha: dados.totalJaTinhaSelo,
            erros: dados.totalErros,
            selos: dados.totalSelosUtilizados,
            registrosGrid: dados.rowData.length
        });

        return dados;
    }

    // ============================================
    // 🔥 CRIAR MODAL - VERSÃO CORRIGIDA
    // ============================================
    function criarModal() {
        console.log('🛠️ Criando modal...');
        
        // 🔥 PRIMEIRO: Remover modal existente se houver
        var modalExistente = document.getElementById(CONFIG.IDS_MODAL.MODAL);
        if (modalExistente) {
            modalExistente.remove();
            console.log('🗑️ Modal anterior removido');
        }

        // 🔥 SEGUNDO: Criar novo modal
        var modalHTML = `
        <div id="${CONFIG.IDS_MODAL.MODAL}" style="
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.5);
            z-index: 9999;
            align-items: center;
            justify-content: center;
        ">
            <div style="
                background: white;
                border-radius: 8px;
                padding: 20px;
                width: 90%;
                max-width: 1200px;
                max-height: 90vh;
                overflow: auto;
                box-shadow: 0 4px 20px rgba(0,0,0,0.2);
            ">
                <div style="
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 20px;
                    border-bottom: 2px solid #007bff;
                    padding-bottom: 10px;
                ">
                    <h3 id="${CONFIG.IDS_MODAL.TITLE}" style="margin: 0; color: #007bff;">
                        <i class="fas fa-check-circle" style="color: #28a745;"></i> Resultado da Selagem
                    </h3>
                    <button id="${CONFIG.IDS_MODAL.BOTAO_FECHAR}" style="
                        background: #6c757d;
                        color: white;
                        border: none;
                        border-radius: 4px;
                        padding: 8px 16px;
                        cursor: pointer;
                        font-size: 14px;
                    ">
                        <i class="fas fa-times"></i> Fechar
                    </button>
                </div>

                <div>
                    <!-- RESUMO -->
                    <div style="margin-bottom: 20px;">
                        <div id="${CONFIG.IDS_MODAL.RESUMO}">
                            Carregando resumo...
                        </div>
                    </div>

                    <!-- GRID -->
                    <div style="margin-top: 20px;">
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
                            <h4 style="margin: 0;">
                                <i class="fas fa-list"></i> Detalhes dos Apontamentos
                            </h4>
                            <div style="font-size: 14px; color: #6c757d;">
                                Total: <span id="${CONFIG.IDS_MODAL.TOTAL_APONTAMENTOS}" style="font-weight: bold;">0</span> | 
                                Selos: <span id="${CONFIG.IDS_MODAL.TOTAL_SELOS}" style="font-weight: bold;">0</span>
                            </div>
                        </div>
                        <div id="${CONFIG.IDS_MODAL.GRID_CONTAINER}" class="ag-theme-alpine" style="height: 400px; width: 100%;"></div>
                    </div>

                    <!-- BOTÕES -->
                    <div style="
                        margin-top: 20px;
                        display: flex;
                        justify-content: flex-end;
                        gap: 10px;
                        border-top: 1px solid #dee2e6;
                        padding-top: 15px;
                    ">
                        <button id="${CONFIG.IDS_MODAL.BOTAO_EXPORTAR}" style="
                            background: #007bff;
                            color: white;
                            border: none;
                            border-radius: 4px;
                            padding: 10px 20px;
                            cursor: pointer;
                            font-size: 14px;
                            display: flex;
                            align-items: center;
                            gap: 5px;
                        ">
                            <i class="fas fa-download"></i> Exportar para Excel
                        </button>
                    </div>
                </div>
            </div>
        </div>
        `;

        document.body.insertAdjacentHTML('beforeend', modalHTML);
        console.log('✅ Modal criado com IDs:', CONFIG.IDS_MODAL);

        // 🔥 CONFIGURAR EVENTOS
        configurarEventosModal();
    }

    function configurarEventosModal() {
        // Botão Fechar
        var btnFechar = document.getElementById(CONFIG.IDS_MODAL.BOTAO_FECHAR);
        if (btnFechar) {
            btnFechar.addEventListener('click', function () {
                esconderModal();
                if (gridApi) {
                    gridApi.destroy();
                    gridApi = null;
                }
            });
        }

        // Botão Exportar
        var btnExportar = document.getElementById(CONFIG.IDS_MODAL.BOTAO_EXPORTAR);
        if (btnExportar) {
            btnExportar.addEventListener('click', function () {
                exportarParaExcel();
            });
        }
    }

    // ============================================
    // 🔥 ATUALIZAR RESUMO - VERSÃO CORRIGIDA
    // ============================================
    function atualizarResumo(dados) {
        var resumoElement = document.getElementById(CONFIG.IDS_MODAL.RESUMO);
        var totalSelosElement = document.getElementById(CONFIG.IDS_MODAL.TOTAL_SELOS);
        var totalApontamentosElement = document.getElementById(CONFIG.IDS_MODAL.TOTAL_APONTAMENTOS);

        if (!resumoElement) {
            console.error('❌ Elemento de resumo não encontrado');
            console.log('🔍 Procurando elemento com ID:', CONFIG.IDS_MODAL.RESUMO);
            console.log('📝 Modal existe?', document.getElementById(CONFIG.IDS_MODAL.MODAL) ? 'Sim' : 'Não');
            return;
        }

        // 🔥 RESUMO HTML
        var resumoHTML = '<div style="font-size: 14px;">';

        if (dados.sucesso) {
            resumoHTML += '<div style="color: #28a745; margin-bottom: 10px; font-weight: bold;">';
            resumoHTML += '<i class="fas fa-check-circle"></i> SELAGEM CONCLUÍDA';
            resumoHTML += '</div>';
        }

        if (dados.mensagem) {
            resumoHTML += '<div style="background: #e7f3ff; padding: 10px; border-radius: 4px; margin-bottom: 15px; color: #004085;">';
            resumoHTML += '<i class="fas fa-info-circle"></i> ' + dados.mensagem;
            resumoHTML += '</div>';
        }

        // 🔥 ESTATÍSTICAS
        resumoHTML += '<div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 10px; margin-top: 10px;">';

        resumoHTML += criarItemResumo('📊 Total analisado', dados.totalApontamentos, '#17a2b8');
        resumoHTML += criarItemResumo('✅ Novo selo aplicado', dados.totalProcessados, '#28a745');
        resumoHTML += criarItemResumo('ℹ️ Já possuíam selo', dados.totalJaTinhaSelo, '#ffc107');
        resumoHTML += criarItemResumo('❌ Com erro', dados.totalErros, '#dc3545');
        resumoHTML += criarItemResumo('🏷️ Selos utilizados', dados.totalSelosUtilizados, '#007bff');

        resumoHTML += '</div>';
        resumoHTML += '</div>';

        resumoElement.innerHTML = resumoHTML;

        // 🔥 ATUALIZAR TOTAIS
        if (totalSelosElement) {
            totalSelosElement.textContent = dados.totalSelosUtilizados;
        }
        if (totalApontamentosElement) {
            totalApontamentosElement.textContent = dados.totalProcessados;
        }

        // 🔥 ATUALIZAR TÍTULO
        var tituloElement = document.getElementById(CONFIG.IDS_MODAL.TITLE);
        if (tituloElement) {
            if (dados.sucesso) {
                tituloElement.innerHTML = '<i class="fas fa-check-circle" style="color: #28a745;"></i> Selagem Concluída';
            } else {
                tituloElement.innerHTML = '<i class="fas fa-exclamation-triangle" style="color: #ffc107;"></i> Selagem com Erros';
            }
        }

        console.log('✅ Resumo atualizado');
    }

    function criarItemResumo(label, valor, cor) {
        return `
        <div style="
            background: ${cor}10;
            border-left: 4px solid ${cor};
            padding: 8px 12px;
            border-radius: 4px;
        ">
            <div style="font-size: 12px; color: #6c757d;">${label}</div>
            <div style="font-size: 18px; font-weight: bold; color: ${cor};">${valor}</div>
        </div>
        `;
    }

    // ============================================
    // 🔥 INICIALIZAR AG-GRID
    // ============================================
    function inicializarGrid(dados) {
        var gridContainer = document.getElementById(CONFIG.IDS_MODAL.GRID_CONTAINER);
        
        if (!gridContainer) {
            console.error('❌ Container do grid não encontrado');
            console.log('🔍 Procurando elemento com ID:', CONFIG.IDS_MODAL.GRID_CONTAINER);
            return;
        }

        // 🔥 LIMPAR CONTEÚDO ANTERIOR
        gridContainer.innerHTML = '';

        // 🔥 CONFIGURAÇÃO SIMPLES DO GRID
        var columnDefs = [
            {
                field: 'id',
                headerName: '#',
                width: 60,
                sortable: true
            },
            {
                field: 'protocolo',
                headerName: 'Protocolo',
                width: 100,
                sortable: true
            },
            {
                field: 'apontamento',
                headerName: 'Apontamento',
                width: 120,
                sortable: true
            },
            {
                field: 'devedor',
                headerName: 'Devedor',
                width: 200,
                sortable: true,
                cellRenderer: function (params) {
                    var texto = params.value || '';
                    if (texto.length > 20) {
                        return texto.substring(0, 20) + '...';
                    }
                    return texto;
                }
            },
            {
                field: 'cpfcnpj',
                headerName: 'CPF/CNPJ',
                width: 150,
                sortable: true
            },
            {
                field: 'status',
                headerName: 'Status',
                width: 150,
                sortable: true
            },
            {
                field: 'selos',
                headerName: 'Selos Aplicados',
                width: 200,
                sortable: true
            }
        ];

        var gridOptions = {
            columnDefs: columnDefs,
            rowData: dados.rowData,
            defaultColDef: {
                resizable: true,
                sortable: true
            },
            domLayout: 'autoHeight',
            rowHeight: 35,
            headerHeight: 35,
            onGridReady: function (params) {
                gridApi = params.api;
                params.api.sizeColumnsToFit();
                console.log('✅ AG Grid pronto com ' + dados.rowData.length + ' registros');
            }
        };

        // 🔥 INICIALIZAR GRID
        new agGrid.createGrid(gridContainer, gridOptions);
        console.log('✅ Grid inicializado');
    }

    // ============================================
    // 🔥 FUNÇÕES DE FORMATAÇÃO
    // ============================================
    function formatarStatus(status) {
        var map = {
            'sucesso': '✅ Novo selo aplicado',
            'ja_possui': 'ℹ️ Já possuía selo',
            'sem_selo_disponivel': '⚠️ Sem selos disponíveis',
            'erro': '❌ Erro'
        };
        return map[status] || status;
    }

    function formatarSelos(selos) {
        if (!selos || !Array.isArray(selos) || selos.length === 0) {
            return '';
        }
        return selos.map(function (selo) {
            if (typeof selo === 'object') {
                return (selo.tipo || 'Selo') + ': ' + (selo.numero || 'N/A');
            }
            return 'Selo: ' + selo;
        }).join(', ');
    }

    function formatarCpfCnpj(cpfcnpj) {
        if (!cpfcnpj) return '';
        var limpo = cpfcnpj.replace(/\D/g, '');
        if (limpo.length === 11) {
            return limpo.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
        } else if (limpo.length === 14) {
            return limpo.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5');
        }
        return cpfcnpj;
    }

    // ============================================
    // 🔥 CONTROLE DO MODAL
    // ============================================
    function mostrarModal() {
        var modal = document.getElementById(CONFIG.IDS_MODAL.MODAL);
        if (modal) {
            modal.style.display = 'flex';
            console.log('✅ Modal exibido');
        } else {
            console.error('❌ Modal não encontrado para exibir');
        }
    }

    function esconderModal() {
        var modal = document.getElementById(CONFIG.IDS_MODAL.MODAL);
        if (modal) {
            modal.style.display = 'none';
            console.log('✅ Modal escondido');
        }
    }

    // ============================================
    // 🔥 EXPORTAÇÃO
    // ============================================
    function exportarParaExcel() {
        if (!gridApi) {
            alert('Grid não disponível para exportação');
            return;
        }

        try {
            gridApi.exportDataAsExcel({
                fileName: 'selagem_resultado_' + new Date().toISOString().slice(0, 10)
            });
            console.log('✅ Exportado para Excel');
        } catch (error) {
            console.error('❌ Erro ao exportar:', error);
            alert('Erro ao exportar: ' + error.message);
        }
    }

    // ============================================
    // 🔥 FALLBACK SIMPLES
    // ============================================
    function mostrarResultadoSimples(resultado) {
        var dadosBackend = resultado.dados || resultado;
        
        var mensagem = 'SELAGEM CONCLUÍDA\n\n';
        mensagem += 'Resultados:\n';
        mensagem += '• Total analisado: ' + (dadosBackend.total_apontamentos || 0) + '\n';
        mensagem += '• Novo selo aplicado: ' + (dadosBackend.total_processados || 0) + '\n';
        mensagem += '• Já possuíam selo: ' + (dadosBackend.total_ja_tinha_selos || 0) + '\n';
        mensagem += '• Com erro: ' + (dadosBackend.total_erros || 0) + '\n';
        mensagem += '• Selos utilizados: ' + (dadosBackend.total_selos_utilizados || 0) + '\n\n';
        
        if (dadosBackend.mensagem) {
            mensagem += 'Mensagem: ' + dadosBackend.mensagem;
        }
        
        alert(mensagem);
    }

    // ============================================
    // API PÚBLICA
    // ============================================
    return {
        exibirModalResultado: exibirModalResultado,
        mostrarResultadoSimples: mostrarResultadoSimples
    };
})();

console.log('✅ resultado-selagem-manager.js carregado - VERSÃO FIX');
console.log('📋 Correções:');
console.log('   1. Modal SEMPRE criado (não verifica se existe)');
console.log('   2. IDs fixos e consistentes');
console.log('   3. AG Grid funcional');
console.log('🔧 Comando: exibirModalResultado(resultado)');