/**
 * Sprite Gráfico de Estoque - SPR
 * Inclui o widget de Selagem de Atos - Tabelionato de Notas
 * 
 * Este arquivo é carregado no AoEntrar do formulário principal
 */

(function () {
    'use strict';

    console.log('📦 [SPR] Carregando SPR - Gráfico de Estoque...');
    console.log('📦 [SPR] URL atual:', window.location.href);
    console.log('📦 [SPR] User Agent:', navigator.userAgent);

    // ============================================
    // 1. CARREGAR WIDGET DE SELAGEM NOTAS
    // ============================================

    console.log('📦 [SPR] Verificando necessidade de carregar widget de selagem...');

    // Verificar se o widget já foi carregado
    if (!window.notasSelosWidgetInicializado) {
        console.log('📦 [SPR] Widget de selagem NÃO estava inicializado, carregando...');

        // Criar elemento de script para o widget
        var scriptWidget = document.createElement('script');
        scriptWidget.src = 'assets/js/notas-selos-widget.js';
        scriptWidget.async = true;
        scriptWidget.onload = function () {
            console.log('✅ [SPR] Script notas-selos-widget.js CARREGADO com sucesso');
        };
        scriptWidget.onerror = function (e) {
            console.error('❌ [SPR] Erro ao carregar notas-selos-widget.js', e);
        };

        // Tentar carregar após um pequeno atraso
        setTimeout(function () {
            document.head.appendChild(scriptWidget);
            console.log('✅ [SPR] Widget de Selagem Notas adicionado ao DOM');
        }, 500);
    } else {
        console.log('ℹ️ [SPR] Widget de Selagem Notas já estava inicializado anteriormente');
    }

    // ============================================
    // 2. CARREGAR GRÁFICO DE ESTOQUE (sidebar)
    // ============================================

    console.log('📦 [SPR] Verificando necessidade de carregar gráfico de estoque...');

    // Verificar se existe elemento para o gráfico
    var sidebar = document.querySelector('.fp-container, .fp-sidebar, #sidebar, .sidebar, .menu');
    console.log('📦 [SPR] Sidebar encontrada:', sidebar ? 'SIM' : 'NÃO');

    if (sidebar) {
        console.log('📦 [SPR] Carregando script do gráfico de estoque via fetch...');

        // Carregar o script do gráfico
        var scriptGrafico = document.createElement('script');
        scriptGrafico.src = 'html/assets/sprGraficoEstoque.js';
        scriptGrafico.async = true;
        scriptGrafico.onload = function () {
            console.log('✅ [SPR] Script html/assets/sprGraficoEstoque.js CARREGADO com sucesso');

            // Verificar se a função foi definida
            if (typeof window.sprGraficoEstoque === 'function') {
                console.log('📦 [SPR] Função window.sprGraficoEstoque encontrada, executando...');
                window.sprGraficoEstoque();
            } else {
                console.warn('⚠️ [SPR] Função window.sprGraficoEstoque NÃO encontrada');
            }
        };
        scriptGrafico.onerror = function (e) {
            console.error('❌ [SPR] Erro ao carregar html/assets/sprGraficoEstoque.js', e);
        };

        setTimeout(function () {
            document.head.appendChild(scriptGrafico);
            console.log('✅ [SPR] Script de gráfico adicionado ao DOM');
        }, 800);
    } else {
        console.log('ℹ️ [SPR] Nenhuma sidebar encontrada, gráfico de estoque não será carregado');
    }

    // ============================================
    // 3. OUTRAS FUNÇÕES DO SPR (se houver)
    // ============================================

    // Função principal do SPR (pode ser expandida)
    window.sprGraficoEstoque = function () {
        console.log('📊 [SPR] Função sprGraficoEstoque executada');

        // Aquí podem ser adicionadas outras inicializações
        // necessárias para o gráfico de estoque
    };

    // Executar função principal
    try {
        window.sprGraficoEstoque();
    } catch (e) {
        console.error('❌ [SPR] Erro ao executar sprGraficoEstoque:', e);
    }

    console.log('✅ [SPR] SPR - Gráfico de Estoque carregado com sucesso!');

})();
