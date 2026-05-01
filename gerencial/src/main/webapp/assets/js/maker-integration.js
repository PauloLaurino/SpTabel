/**
 * INTEGRAÇÃO COM MAKER 5 - SISTEMA SELADOR - VERSÃO ES5
 * Funções que serão chamadas pelo formulário Selador do Maker 5
 * Compatível com Java 8 + Tomcat + Navegadores antigos
 */

// ============================================================================
// DEPENDÊNCIAS - Verificar se helpers está disponível
// ============================================================================
if (!window.helpers) {
    window.helpers = {
        converterDataParaYYYYMMDD: function(dataISO) {
            try {
                return dataISO.replace(/-/g, '');
            } catch (e) {
                console.warn('⚠️ Erro ao converter data:', e);
                return null;
            }
        }
    };
}

// ============================================================================
// FUNÇÃO PRINCIPAL - Chamada pelo Maker 5 via "Executar Javascript"
// ============================================================================

/**
 * sprChamaSelador - Função principal de integração
 * @param {string} dataAtoString - Data no formato AAAAMMDD (obrigatório)
 * @param {string} tipoOperacao - Código do tipo de operação (opcional)
 * @param {object} opcoes - Opções adicionais (opcional)
 * @returns {Window|null} Referência à janela aberta ou null
 */
function sprChamaSelador(dataAtoString, tipoOperacao, opcoes) {
    console.group('🔗 [MAKER INTEGRATION] sprChamaSelador chamada');
    console.log('📅 Data recebida:', dataAtoString);
    console.log('⚙️ Tipo operacao:', tipoOperacao);
    console.log('⚙️ Opcoes:', opcoes);
    
    // Valores padrão para ES5
    tipoOperacao = tipoOperacao || '';
    opcoes = opcoes || {};
    
    try {
        // ============================================
        // 1. VALIDAÇÃO DOS PARÂMETROS
        // ============================================
        if (!dataAtoString || typeof dataAtoString !== 'string') {
            var erro = '❌ Parâmetro dataAtoString é obrigatório e deve ser string';
            console.error(erro);
            alert(erro);
            return null;
        }
        
        if (dataAtoString.length !== 8) {
            var erro = '❌ dataAtoString deve ter 8 dígitos no formato AAAAMMDD';
            console.error(erro);
            alert(erro);
            return null;
        }
        
        // Validar se é uma data válida
        var ano = dataAtoString.substring(0, 4);
        var mes = dataAtoString.substring(4, 6);
        var dia = dataAtoString.substring(6, 8);
        
        var data = new Date(ano + '-' + mes + '-' + dia);
        if (isNaN(data.getTime())) {
            var erro = '❌ dataAtoString não é uma data válida';
            console.error(erro);
            alert(erro);
            return null;
        }
        
        // ============================================
        // 2. CONFIGURAÇÃO DA URL DO SELADOR
        // ============================================
        // IMPORTANTE: Ajustar conforme sua instalação
        var baseUrl;
        
        if (opcoes.baseUrl) {
            // URL personalizada nas opções
            baseUrl = opcoes.baseUrl;
        } else if (window.location.origin.indexOf('localhost') !== -1) {
            // Desenvolvimento local
            baseUrl = 'http://localhost:5000/selador';
        } else {
            // Produção - mesma origem do Maker
            baseUrl = window.location.origin + '/selador';
        }
        
        var seladorPath = '/views/selador.html';
        var urlCompleta = baseUrl + seladorPath;
        
        // ============================================
        // 3. PREPARAR PARÂMETROS DE CONSULTA
        // ============================================
        var params = new URLSearchParams();
        params.append('data', dataAtoString);
        params.append('origem', 'maker');
        params.append('timestamp', Date.now().toString());
        
        if (tipoOperacao && tipoOperacao.trim() !== '') {
            params.append('tipo', tipoOperacao.trim());
        }
        
        // Adicionar opções extras
        if (opcoes.filtros) {
            for (var key in opcoes.filtros) {
                if (opcoes.filtros.hasOwnProperty(key)) {
                    var value = opcoes.filtros[key];
                    if (value !== null && value !== undefined) {
                        params.append(key, value.toString());
                    }
                }
            }
        }
        
        var urlFinal = urlCompleta + '?' + params.toString();
        console.log('🌐 URL do Selador:', urlFinal);
        
        // ============================================
        // 4. ABRIR JANELA DO SELADOR
        // ============================================
        var configJanela = {
            width: opcoes.largura || 1400,
            height: opcoes.altura || 800,
            left: opcoes.esquerda || 100,
            top: opcoes.top || 100,
            resizable: opcoes.redimensionavel !== false,
            scrollbars: opcoes.barrasRolagem !== false,
            toolbar: opcoes.barraFerramentas || false,
            menubar: opcoes.barraMenu || false,
            location: opcoes.barraEndereco || false,
            status: opcoes.barraStatus || true
        };
        
        // Converter configuração para string de features
        var featuresArray = [];
        for (var key in configJanela) {
            if (configJanela.hasOwnProperty(key)) {
                var value = configJanela[key];
                if (typeof value === 'boolean') {
                    if (value) featuresArray.push(key);
                } else {
                    featuresArray.push(key + '=' + value);
                }
            }
        }
        
        var features = featuresArray.join(',');
        console.log('🪟 Configuração da janela:', configJanela);
        
        // Abrir janela
        var janelaSelador = window.open(urlFinal, 'SistemaSelador_MakerIntegration', features);
        
        if (!janelaSelador) {
            var erro = '⚠️ Popup bloqueado pelo navegador!\nPor favor, permita popups para este site.';
            console.error(erro);
            alert(erro);
            return null;
        }
        
        // ============================================
        // 5. CONFIGURAR COMUNICAÇÃO (OPCIONAL)
        // ============================================
        window.addEventListener('message', function(event) {
            // Verificar origem para segurança
            try {
                var urlBase = new URL(baseUrl);
                if (event.origin !== urlBase.origin) {
                    console.warn('⚠️ Mensagem de origem não confiável:', event.origin);
                    return;
                }
            } catch (e) {
                console.warn('⚠️ Não foi possível verificar origem:', e);
            }
            
            console.log('📨 Mensagem recebida do Selador:', event.data);
            
            // Exemplo de tratamento de mensagens
            if (event.data && event.data.type === 'selagemConcluida') {
                console.log('✅ Selagem concluída no Selador:', event.data.resultado);
            }
            
            if (event.data && event.data.type === 'fecharJanela') {
                console.log('🚪 Fechando janela do Selador por solicitação');
                if (janelaSelador) janelaSelador.close();
            }
        });
        
        // ============================================
        // 6. LOG DE SUCESSO E RETORNO
        // ============================================
        console.log('✅ Selador aberto com sucesso');
        console.groupEnd();
        
        // Retornar referência para possível controle posterior
        return janelaSelador;
        
    } catch (error) {
        console.error('❌ Erro crítico na integração:', error);
        console.groupEnd();
        
        alert('❌ Erro ao abrir Sistema Selador:\n' + error.message);
        return null;
    }
}

// ============================================================================
// FUNÇÃO DE CALLBACK - Para o Selador fechar e retornar ao Maker
// ============================================================================

/**
 * sprFechaSelador - Callback para fechar o Selador e retornar ao Maker
 * @param {object} resultado - Resultado da operação no Selador (opcional)
 */
function sprFechaSelador(resultado) {
    console.group('🔗 [MAKER INTEGRATION] sprFechaSelador chamada');
    
    // Valor padrão para ES5
    resultado = resultado || {};
    
    console.log('📊 Resultado recebido:', resultado);
    
    try {
        // Opcional: Enviar dados de volta para o Maker
        if (resultado && Object.keys(resultado).length > 0) {
            // Exemplo: Mostrar resumo para o usuário
            if (resultado.selados && resultado.selados > 0) {
                alert('✅ Selagem concluída!\n' + resultado.selados + ' apontamento(s) selado(s) com sucesso.');
            }
            
            // Opcional: Atualizar algo no formulário do Maker
            if (typeof window.atualizarFormularioMaker === 'function') {
                window.atualizarFormularioMaker(resultado);
            }
        }
        
        // Fechar a janela do Selador
        window.close();
        
        console.log('✅ Selador fechado e retornado ao Maker');
        
    } catch (error) {
        console.error('❌ Erro ao fechar Selador:', error);
        // Tentar fechar mesmo com erro
        try { window.close(); } catch (e) {}
    } finally {
        console.groupEnd();
    }
}

// ============================================================================
// FUNÇÕES AUXILIARES PARA O MAKER
// ============================================================================

/**
 * Validar se o Selador está disponível
 * @returns {Promise} Promise que resolve para true/false
 */
function sprVerificarDisponibilidadeSelador() {
    return new Promise(function(resolve) {
        try {
            fetch('/selador/api/config/health', {
                method: 'GET',
                headers: { 'Accept': 'application/json' }
            })
            .then(function(response) {
                return response.json();
            })
            .then(function(dados) {
                resolve(dados.success === true);
            })
            .catch(function(error) {
                console.warn('⚠️ Selador não disponível:', error);
                resolve(false);
            });
        } catch (error) {
            console.warn('⚠️ Erro ao verificar disponibilidade:', error);
            resolve(false);
        }
    });
}

/**
 * Obter estatísticas rápidas do Selador
 * @returns {Promise} Promise com estatísticas
 */
function sprObterEstatisticasSelador() {
    return new Promise(function(resolve) {
        try {
            Promise.all([
                fetch('/selador/api/config/health').then(function(r) { return r.json(); }),
                fetch('/selador/api/selos/contar').then(function(r) { return r.json(); })
            ])
            .then(function(results) {
                var status = results[0];
                var selos = results[1];
                
                resolve({
                    online: status.success,
                    selosDisponiveis: (selos.data && selos.data.disponiveis) || 0,
                    versao: (status.data && status.data.versao) || '1.0.0',
                    timestamp: new Date().toISOString()
                });
            })
            .catch(function(error) {
                console.error('❌ Erro ao obter estatísticas:', error);
                resolve({ online: false, erro: error.message });
            });
        } catch (error) {
            console.error('❌ Erro ao obter estatísticas:', error);
            resolve({ online: false, erro: error.message });
        }
    });
}

// ============================================================================
// DOCUMENTAÇÃO DE USO PARA O MAKER
// ============================================================================
/*
EXEMPLOS DE USO NO MAKER 5:

1. CHAMADA BÁSICA:
   Executar Javascript: sprChamaSelador('20240115', '01');

2. CHAMADA COM OPÇÕES:
   Executar Javascript: 
   sprChamaSelador('20240115', '01', {
       largura: 1200,
       altura: 700,
       filtros: { status: '01' }
   });

3. VERIFICAR DISPONIBILIDADE ANTES:
   Executar Javascript:
   sprVerificarDisponibilidadeSelador().then(function(disponivel) {
       if (disponivel) {
           sprChamaSelador('20240115', '01');
       } else {
           alert('Sistema Selador indisponível');
       }
   });

4. NO FORMULÁRIO DO MAKER:
   - Criar botão "Abrir Selador"
   - Na ação do botão: Executar Javascript
   - Código: sprChamaSelador(dataCampo, tipoCampo);
*/

// ============================================================================
// EXPORTAÇÃO DAS FUNÇÕES PARA O AMBIENTE GLOBAL (MAKER)
// ============================================================================

if (typeof window !== 'undefined') {
    // Exportar função principal
    window.sprChamaSelador = sprChamaSelador;
    window.sprFechaSelador = sprFechaSelador;
    
    // Exportar funções auxiliares
    window.sprVerificarDisponibilidadeSelador = sprVerificarDisponibilidadeSelador;
    window.sprObterEstatisticasSelador = sprObterEstatisticasSelador;
    
    console.log('✅ Integração Maker 5 carregada e pronta para uso');
    
    // Exemplo de uso automático se detectar parâmetros na URL
    if (window.location.search.indexOf('testeMaker=1') !== -1) {
        console.log('🧪 Teste automático da integração');
        setTimeout(function() {
            sprChamaSelador('20240115', '01', { largura: 1200, altura: 700 });
        }, 1000);
    }
}