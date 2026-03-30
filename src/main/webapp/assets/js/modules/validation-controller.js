// ============================================
// VALIDATION CONTROLLER - Validações específicas do domínio
// Versão ES5 - REFATORADA da FASE 2
// ============================================

window.validationController = (function () {
    'use strict';

    // ============================================
    // VALIDAÇÕES DE CAMPOS
    // ============================================

    function validarCampoAnoMes(valor) {
        if (!valor || typeof valor !== 'string') {
            return { valido: false, mensagem: 'Campo obrigatório' };
        }

        if (valor.length !== 6) {
            return { valido: false, mensagem: 'Deve ter 6 dígitos (AAAAMM)' };
        }

        if (!/^\d{6}$/.test(valor)) {
            return { valido: false, mensagem: 'Apenas números são permitidos' };
        }

        var ano = parseInt(valor.substring(0, 4));
        var mes = parseInt(valor.substring(4, 6));

        if (ano < 1900 || ano > 2100) {
            return { valido: false, mensagem: 'Ano deve estar entre 1900 e 2100' };
        }

        if (mes < 1 || mes > 12) {
            return { valido: false, mensagem: 'Mês deve estar entre 01 e 12' };
        }

        return { valido: true, mensagem: '' };
    }

    function validarCampoApon(valor) {
        if (!valor || typeof valor !== 'string') {
            return { valido: false, mensagem: 'Campo obrigatório' };
        }

        if (!/^\d{1,6}$/.test(valor)) {
            return { valido: false, mensagem: 'Apenas números (1-6 dígitos)' };
        }

        var num = parseInt(valor);
        if (num < 1 || num > 999999) {
            return { valido: false, mensagem: 'Número deve estar entre 1 e 999999' };
        }

        return { valido: true, mensagem: '' };
    }

    function validarDataAto(valor) {
        if (!valor || typeof valor !== 'string') {
            return { valido: false, mensagem: 'Data obrigatória' };
        }

        try {
            var data = new Date(valor);
            if (isNaN(data.getTime())) {
                return { valido: false, mensagem: 'Data inválida' };
            }

            var hoje = new Date();
            if (data > hoje) {
                return { valido: false, mensagem: 'Data não pode ser futura' };
            }

            return { valido: true, mensagem: '' };
        } catch (e) {
            return { valido: false, mensagem: 'Erro ao validar data' };
        }
    }

    function validarSeloQuantidade(valor) {
        if (valor === '' || valor === null || valor === undefined) {
            return { valido: false, mensagem: 'Quantidade obrigatória' };
        }

        var qtd = parseInt(valor);
        if (isNaN(qtd)) {
            return { valido: false, mensagem: 'Deve ser um número' };
        }

        if (qtd < 0) {
            return { valido: false, mensagem: 'Não pode ser negativo' };
        }

        if (qtd > 9999) {
            return { valido: false, mensagem: 'Máximo 9999' };
        }

        return { valido: true, mensagem: '' };
    }

    // ============================================
    // VALIDAÇÕES DE INTERVALO
    // ============================================

    function validarIntervaloCompleto(intervalo) {
        var resultado = {
            valido: false,
            mensagem: '',
            detalhes: {}
        };

        // Validar início
        var inicioAnoMes = validarCampoAnoMes(intervalo.anoMesInicial);
        var inicioApon = validarCampoApon(intervalo.aponInicial);

        resultado.detalhes.inicio = {
            anoMes: inicioAnoMes,
            apon: inicioApon
        };

        if (!inicioAnoMes.valido || !inicioApon.valido) {
            resultado.mensagem = 'Intervalo inicial inválido';
            return resultado;
        }

        // Validar fim (se informado)
        if (intervalo.anoMesFinal || intervalo.aponFinal) {
            var fimAnoMes = validarCampoAnoMes(intervalo.anoMesFinal || intervalo.anoMesInicial);
            var fimApon = validarCampoApon(intervalo.aponFinal || intervalo.aponInicial);

            resultado.detalhes.fim = {
                anoMes: fimAnoMes,
                apon: fimApon
            };

            if (!fimAnoMes.valido || !fimApon.valido) {
                resultado.mensagem = 'Intervalo final inválido';
                return resultado;
            }

            // Validar ordem cronológica
            var inicioCompleto = parseInt(intervalo.anoMesInicial + intervalo.aponInicial.padStart(6, '0'));
            var fimCompleto = parseInt((intervalo.anoMesFinal || intervalo.anoMesInicial) +
                (intervalo.aponFinal || intervalo.aponInicial).padStart(6, '0'));

            if (fimCompleto < inicioCompleto) {
                resultado.mensagem = 'Intervalo final não pode ser anterior ao inicial';
                return resultado;
            }
        }

        resultado.valido = true;
        resultado.mensagem = 'Intervalo válido';
        return resultado;
    }

    // ============================================
    // VALIDAÇÕES DE OPERAÇÃO
    // ============================================

    function validarParaCalculo(app) {
        console.log('🔍 [VALIDACAO] Iniciando validação para cálculo...');

        // 🔥 DEBUG EXTENSIVO: Verificar o que está chegando
        console.log('🔍 [DEBUG] Estado da validação:');
        console.log('   • app:', !!app);
        console.log('   • app.elementos:', app ? !!app.elementos : false);

        if (app && app.elementos) {
            console.log('   • Keys em app.elementos:', Object.keys(app.elementos));
            console.log('   • anoMesInicial em app.elementos:', 'anoMesInicial' in app.elementos);

            // Verificar cada campo individualmente
            ['dataAto', 'anoMesInicial', 'aponInicial', 'anoMesFinal', 'aponFinal'].forEach(function (id) {
                var elemento = app.elementos[id];
                console.log('   • ' + id + ':', {
                    existe: !!elemento,
                    tipo: elemento ? elemento.constructor.name : 'N/A',
                    valor: elemento ? elemento.value : 'N/A',
                    id: elemento ? elemento.id : 'N/A'
                });
            });
        }

        // 🔥 SOLUÇÃO ROBUSTA: Usar múltiplas fontes para obter valores
        function obterValorComFallback(id) {
            // Fonte 1: app.elementos
            if (app && app.elementos && app.elementos[id]) {
                var valorApp = app.elementos[id].value;
                if (valorApp !== undefined && valorApp !== null && valorApp !== '') {
                    console.log('📋 [' + id + '] Fonte: app.elementos ->', valorApp);
                    return valorApp;
                }
            }

            // Fonte 2: DOM direto
            var elementoDom = document.getElementById(id);
            if (elementoDom) {
                var valorDom = elementoDom.value;
                if (valorDom !== undefined && valorDom !== null && valorDom !== '') {
                    console.log('📋 [' + id + '] Fonte: DOM ->', valorDom);
                    return valorDom;
                }
            }

            // Fonte 3: Usar window.app se disponível
            if (window.app && window.app.elementos && window.app.elementos[id]) {
                var valorWindowApp = window.app.elementos[id].value;
                if (valorWindowApp !== undefined && valorWindowApp !== null && valorWindowApp !== '') {
                    console.log('📋 [' + id + '] Fonte: window.app ->', valorWindowApp);
                    return valorWindowApp;
                }
            }

            console.log('📋 [' + id + '] Fonte: NENHUMA -> ""');
            return '';
        }

        // Obter valores com fallback múltiplo
        var dataAto = obterValorComFallback('dataAto');
        var anoMesInicial = obterValorComFallback('anoMesInicial');
        var aponInicial = obterValorComFallback('aponInicial');
        var anoMesFinal = obterValorComFallback('anoMesFinal');
        var aponFinal = obterValorComFallback('aponFinal');

        // Obter opção selecionada
        var opcao = null;
        if (window.appState) {
            opcao = window.appState.getOpcaoSelecionada();
        } else if (window.app && window.app.state) {
            opcao = window.app.state.opcaoSelecionada;
        }

        console.log('📊 [VALIDACAO] Dados coletados:', {
            dataAto: dataAto,
            opcao: opcao ? { codigo: opcao.codigo, label: opcao.label } : 'Nenhuma',
            anoMesInicial: anoMesInicial,
            aponInicial: aponInicial,
            anoMesFinal: anoMesFinal,
            aponFinal: aponFinal
        });

        // 🔥 VALIDAR DATA
        if (!dataAto || dataAto.trim() === '') {
            console.error('❌ [VALIDACAO] Falha: Data vazia');
            return {
                podeCalcular: false,
                mensagem: 'Selecione uma data'
            };
        }

        // 🔥 VALIDAR OPÇÃO SELECIONADA
        if (!opcao || !opcao.codigo) {
            console.error('❌ [VALIDACAO] Falha: Opção não selecionada');
            return {
                podeCalcular: false,
                mensagem: 'Selecione uma opção'
            };
        }

        // 🔥 VALIDAR ANO/MÊS INICIAL (CRÍTICO)
        console.log('🔍 [VALIDACAO] Verificando anoMesInicial:', {
            valor: anoMesInicial,
            trim: anoMesInicial.trim(),
            vazio: !anoMesInicial || anoMesInicial.trim() === '',
            comprimento: anoMesInicial ? anoMesInicial.length : 0
        });

        if (!anoMesInicial || anoMesInicial.trim() === '') {
            console.error('❌ [VALIDACAO] Falha: anoMesInicial está vazio!');

            // Debug adicional
            var elementoDebug = document.getElementById('anoMesInicial');
            console.log('🔍 [DEBUG] Elemento anoMesInicial no DOM:', {
                existe: !!elementoDebug,
                elemento: elementoDebug,
                valor: elementoDebug ? elementoDebug.value : 'N/A',
                outerHTML: elementoDebug ? elementoDebug.outerHTML.substring(0, 150) : 'N/A'
            });

            return {
                podeCalcular: false,
                mensagem: 'Ano/Mês Inicial não pode estar vazio'
            };
        }

        // 🔥 VALIDAR APONTAMENTO INICIAL
        if (!aponInicial || aponInicial.trim() === '') {
            console.error('❌ [VALIDACAO] Falha: aponInicial vazio');
            return {
                podeCalcular: false,
                mensagem: 'Apontamento Inicial não pode estar vazio'
            };
        }

        // 🔥 VALIDAR ANO/MÊS FINAL
        if (!anoMesFinal || anoMesFinal.trim() === '') {
            console.error('❌ [VALIDACAO] Falha: anoMesFinal vazio');
            return {
                podeCalcular: false,
                mensagem: 'Ano/Mês Final não pode estar vazio'
            };
        }

        // 🔥 VALIDAR APONTAMENTO FINAL
        if (!aponFinal || aponFinal.trim() === '') {
            console.error('❌ [VALIDACAO] Falha: aponFinal vazio');
            return {
                podeCalcular: false,
                mensagem: 'Apontamento Final não pode estar vazio'
            };
        }

        // 🔥 VALIDAR FORMATO ANO/MÊS (6 dígitos)
        if (!/^\d{6}$/.test(anoMesInicial)) {
            console.error('❌ [VALIDACAO] Falha: anoMesInicial formato inválido', anoMesInicial);
            return {
                podeCalcular: false,
                mensagem: 'Ano/Mês Inicial deve ter 6 dígitos (AAAAMM)'
            };
        }

        if (!/^\d{6}$/.test(anoMesFinal)) {
            console.error('❌ [VALIDACAO] Falha: anoMesFinal formato inválido', anoMesFinal);
            return {
                podeCalcular: false,
                mensagem: 'Ano/Mês Final deve ter 6 dígitos (AAAAMM)'
            };
        }

        // 🔥 VALIDAR APONTAMENTOS (aceitar "1121" mas converter para "001121")
        var resultadoInicial = validarCampoApontamento(aponInicial, 'Apontamento Inicial');
        var resultadoFinal = validarCampoApontamento(aponFinal, 'Apontamento Final');

        if (!resultadoInicial.valido) {
            console.error('❌ [VALIDACAO] Falha: aponInicial inválido', aponInicial);
            return {
                podeCalcular: false,
                mensagem: resultadoInicial.mensagem
            };
        }

        if (!resultadoFinal.valido) {
            console.error('❌ [VALIDACAO] Falha: aponFinal inválido', aponFinal);
            return {
                podeCalcular: false,
                mensagem: resultadoFinal.mensagem
            };
        }

        // 🔥 VALIDAR QUE ANO/MÊS FINAL NÃO É ANTERIOR AO INICIAL
        if (anoMesFinal < anoMesInicial) {
            console.error('❌ [VALIDACAO] Falha: anoMesFinal anterior a anoMesInicial');
            return {
                podeCalcular: false,
                mensagem: 'Ano/Mês Final não pode ser anterior ao Ano/Mês Inicial'
            };
        }

        // 🔥 VALIDAR QUE APONTAMENTO FINAL NÃO É ANTERIOR AO INICIAL (se mesmo ano/mês)
        if (anoMesFinal === anoMesInicial) {
            var numInicial = parseInt(resultadoInicial.valorDisplay);
            var numFinal = parseInt(resultadoFinal.valorDisplay);

            if (numFinal < numInicial) {
                console.error('❌ [VALIDACAO] Falha: aponFinal menor que aponInicial');
                return {
                    podeCalcular: false,
                    mensagem: 'Apontamento Final não pode ser menor que o Apontamento Inicial'
                };
            }
        }

        // Formatar data (remover hífens)
        var dataFormatada = dataAto.replace(/-/g, '');

        console.log('✅ [VALIDACAO] Validação APROVADA. Dados para backend:', {
            data: dataFormatada,
            tipoOperacao: opcao.label,
            anoMesInicial: anoMesInicial,
            aponInicial: resultadoInicial.valorBackend, // "001121"
            anoMesFinal: anoMesFinal,
            aponFinal: resultadoFinal.valorBackend      // "001146"
        });

        return {
            podeCalcular: true,
            mensagem: 'Dados válidos para cálculo',
            dados: {
                data: dataFormatada,
                tipoOperacao: opcao.label,
                anoMesInicial: anoMesInicial,
                aponInicial: resultadoInicial.valorBackend, // "001121"
                anoMesFinal: anoMesFinal,
                aponFinal: resultadoFinal.valorBackend      // "001146"
            }
        };
    }

    // ============================================
    // VALIDAÇÃO DE APONTAMENTOS (6 dígitos)
    // ============================================

    function validarCampoApontamento(valor, nomeCampo) {
        console.log('🔍 [APONTAMENTO] Validando ' + nomeCampo + ':', valor);

        if (!valor || valor.trim() === '') {
            console.error('❌ [APONTAMENTO] ' + nomeCampo + ' vazio');
            return {
                valido: false,
                mensagem: nomeCampo + ' não pode estar vazio'
            };
        }

        // Converter para número
        var numero = parseInt(valor);
        if (isNaN(numero)) {
            console.error('❌ [APONTAMENTO] ' + nomeCampo + ' não é número:', valor);
            return {
                valido: false,
                mensagem: nomeCampo + ' deve conter apenas números'
            };
        }

        // Validar se está entre 1 e 999999
        if (numero < 1) {
            console.error('❌ [APONTAMENTO] ' + nomeCampo + ' menor que 1:', numero);
            return {
                valido: false,
                mensagem: nomeCampo + ' deve ser maior ou igual a 1'
            };
        }

        if (numero > 999999) {
            console.error('❌ [APONTAMENTO] ' + nomeCampo + ' maior que 999999:', numero);
            return {
                valido: false,
                mensagem: nomeCampo + ' não pode ser maior que 999999'
            };
        }

        console.log('✅ [APONTAMENTO] ' + nomeCampo + ' válido:', {
            valorDisplay: String(numero), // Para display (ex: "1121")
            valorBackend: String(numero).padStart(6, '0') // Para backend (ex: "001121")
        });

        return {
            valido: true,
            valorDisplay: String(numero), // Para display (ex: "1121")
            valorBackend: String(numero).padStart(6, '0') // Para backend (ex: "001121")
        };
    }

    function validarParaSelagem(app) {
        var resultado = {
            podeSelar: false,
            mensagem: '',
            dados: null
        };

        // 1. Validar para cálculo primeiro
        var validacaoCalculo = validarParaCalculo(app);
        if (!validacaoCalculo.podeCalcular) {
            resultado.mensagem = validacaoCalculo.mensagem;
            return resultado;
        }

        // 2. Validar selos solicitados
        var selosSolicitados = window.appState ? window.appState.getSelosSolicitados() : {};
        var totalSelos = 0;
        var tipos = ['TP1', 'TP3', 'TP4', 'TPD', 'TPI'];

        for (var i = 0; i < tipos.length; i++) {
            totalSelos += parseInt(selosSolicitados[tipos[i]] || 0);
        }

        if (totalSelos === 0) {
            resultado.mensagem = 'Solicite os selos necessários antes de selar';
            return resultado;
        }

        // 3. Validar selos zerados (se houver solicitação para selo zerado)
        var selosZerados = window.appState ? window.appState.getSelosZerados() : [];
        for (var i = 0; i < selosZerados.length; i++) {
            var seloZerado = selosZerados[i];
            if (selosSolicitados[seloZerado.tipo] > 0) {
                resultado.mensagem = 'Não é possível selar com ' + seloZerado.nome + ' zerado!';
                return resultado;
            }
        }

        resultado.podeSelar = true;
        resultado.mensagem = 'Pronto para selagem';
        resultado.dados = validacaoCalculo.dados;
        resultado.dados.selosSolicitados = selosSolicitados;
        resultado.dados.totalSelos = totalSelos;

        return resultado;
    }

    function debugValidacaoCalculo(app) {
        console.log('🔍 DEBUG VALIDAÇÃO CÁLCULO:');

        // Verificar se temos app e elementos
        if (!app) {
            console.log('❌ App não fornecido');
            return;
        }

        if (!app.elementos) {
            console.log('❌ App.elementos não disponível');
            return;
        }

        // Verificar cada campo individualmente
        var campos = [
            { id: 'dataAto', nome: 'Data Ato', elemento: app.elementos.dataAto },
            { id: 'anoMesInicial', nome: 'Ano/Mês Inicial', elemento: app.elementos.anoMesInicial },
            { id: 'aponInicial', nome: 'Apon. Inicial', elemento: app.elementos.aponInicial },
            { id: 'anoMesFinal', nome: 'Ano/Mês Final', elemento: app.elementos.anoMesFinal },
            { id: 'aponFinal', nome: 'Apon. Final', elemento: app.elementos.aponFinal }
        ];

        campos.forEach(function (campo) {
            console.log('📋 ' + campo.nome + ':', {
                elementoExiste: !!campo.elemento,
                elemento: campo.elemento,
                valor: campo.elemento ? campo.elemento.value : 'N/A',
                valorRaw: campo.elemento ? campo.elemento.value : 'N/A',
                tipo: campo.elemento ? campo.elemento.type || 'text' : 'N/A',
                placeholder: campo.elemento ? campo.elemento.placeholder : 'N/A'
            });
        });

        // Verificar também via DOM direto
        console.log('🎯 VERIFICAÇÃO DOM DIRETA:');
        campos.forEach(function (campo) {
            var elementoDom = document.getElementById(campo.id);
            console.log('  • ' + campo.id + ' (DOM):', {
                existe: !!elementoDom,
                valor: elementoDom ? elementoDom.value : 'N/A',
                innerHTML: elementoDom ? elementoDom.outerHTML.substring(0, 100) + '...' : 'N/A'
            });
        });

        // Verificar appState
        if (window.appState) {
            var opcao = window.appState.getOpcaoSelecionada();
            console.log('🎯 Opção selecionada (appState):', opcao);
        }

        return true;
    }

    // ============================================
    // VALIDAÇÕES DE DADOS DA API
    // ============================================

    function validarRespostaApi(resposta) {
        var resultado = {
            valida: false,
            mensagem: '',
            dados: null
        };

        if (!resposta || typeof resposta !== 'object') {
            resultado.mensagem = 'Resposta inválida da API';
            return resultado;
        }

        // Verificar se é sucesso
        if (resposta.success === true || resposta.sucesso === true) {
            resultado.valida = true;
            resultado.mensagem = resposta.message || resposta.mensagem || 'Operação bem-sucedida';
            resultado.dados = resposta.data || resposta.dados || resposta;
        } else {
            resultado.mensagem = resposta.message || resposta.mensagem || 'Erro na operação';
            resultado.dados = resposta;
        }

        return resultado;
    }

    // ============================================
    // FUNÇÕES DE FORMATAÇÃO COM VALIDAÇÃO
    // ============================================

    function formatarEValidarAnoMes(valor, elemento) {
        var formatado = valor.replace(/\D/g, '');
        if (formatado.length > 6) {
            formatado = formatado.substring(0, 6);
        }

        var validacao = validarCampoAnoMes(formatado);

        if (elemento) {
            if (!validacao.valido && formatado.length === 6) {
                elemento.classList.add('campo-invalido');
                elemento.title = validacao.mensagem;
            } else {
                elemento.classList.remove('campo-invalido');
                elemento.title = '';
            }
        }

        return {
            valor: formatado,
            valido: validacao.valido,
            mensagem: validacao.mensagem
        };
    }

    function formatarEValidarApon(valor, elemento) {
        var formatado = valor.replace(/\D/g, '');
        if (formatado.length > 6) {
            formatado = formatado.substring(0, 6);
        }

        var validacao = validarCampoApon(formatado);

        if (elemento) {
            if (!validacao.valido && formatado) {
                elemento.classList.add('campo-invalido');
                elemento.title = validacao.mensagem;
            } else {
                elemento.classList.remove('campo-invalido');
                elemento.title = '';
            }
        }

        return {
            valor: formatado,
            valido: validacao.valido,
            mensagem: validacao.mensagem
        };
    }

    // ============================================
    // VALIDAÇÃO DE APONTAMENTOS (6 dígitos)
    // ============================================

    function validarCampoApontamento(valor, nomeCampo) {
        if (!valor || valor.trim() === '') {
            return {
                valido: false,
                mensagem: nomeCampo + ' não pode estar vazio'
            };
        }

        // Converter para número
        var numero = parseInt(valor);
        if (isNaN(numero)) {
            return {
                valido: false,
                mensagem: nomeCampo + ' deve conter apenas números'
            };
        }

        // Validar se está entre 1 e 999999
        if (numero < 1 || numero > 999999) {
            return {
                valido: false,
                mensagem: nomeCampo + ' deve estar entre 1 e 999999'
            };
        }

        return {
            valido: true,
            valorDisplay: String(numero), // Para display (ex: "1121")
            valorBackend: String(numero).padStart(6, '0') // Para backend (ex: "001121")
        };
    }

    // ============================================
    // API PÚBLICA
    // ============================================

    return {
        // Validações de campos
        validarCampoAnoMes: validarCampoAnoMes,
        validarCampoApon: validarCampoApon,
        validarDataAto: validarDataAto,
        validarSeloQuantidade: validarSeloQuantidade,

        // Validações de intervalo
        validarIntervaloCompleto: validarIntervaloCompleto,

        // Validações de operação
        validarParaCalculo: validarParaCalculo,
        validarParaSelagem: validarParaSelagem,

        // Validações de API
        validarRespostaApi: validarRespostaApi,

        // Formatação com validação
        formatarEValidarAnoMes: formatarEValidarAnoMes,
        formatarEValidarApon: formatarEValidarApon
    };
})();

console.log('✅ validation-controller.js carregado - Validações específicas');