/**
 * LOADER UTILS - Utilitários para animações e loading
 */

window.loaderUtils = (function() {
    'use strict';
    
    /**
     * Criar elemento de loading com três pontos animados
     * @param {string} texto - Texto base (ex: "Solicitando")
     * @returns {HTMLElement} Elemento de loading
     */
    function criarLoadingComPontos(texto) {
        var container = document.createElement('span');
        container.className = 'loading-com-pontos';
        container.style.cssText = 'display: inline-flex; align-items: center; gap: 4px;';
        
        var textoBase = document.createElement('span');
        textoBase.textContent = texto;
        textoBase.style.cssText = 'font-weight: 700; text-transform: uppercase;';
        
        var pontos = document.createElement('span');
        pontos.className = 'pontos-animados';
        pontos.style.cssText = 'font-weight: 700; min-width: 20px;';
        pontos.textContent = '';
        
        // Animação dos pontos
        var pontosStates = ['', '.', '..', '...'];
        var currentPoint = 0;
        
        var interval = setInterval(function() {
            pontos.textContent = pontosStates[currentPoint];
            currentPoint = (currentPoint + 1) % pontosStates.length;
        }, 500);
        
        // Armazenar intervalo para limpar depois
        container._loadingInterval = interval;
        
        container.appendChild(textoBase);
        container.appendChild(pontos);
        
        return container;
    }
    
    /**
     * Limpar animação de pontos
     * @param {HTMLElement} elemento - Elemento com animação
     */
    function limparAnimacaoPontos(elemento) {
        if (elemento && elemento._loadingInterval) {
            clearInterval(elemento._loadingInterval);
            elemento._loadingInterval = null;
        }
    }
    
    /**
     * Substituir conteúdo do botão por loading
     * @param {HTMLElement} botao - Elemento do botão
     * @param {string} texto - Texto para loading
     * @returns {Function} Função para restaurar conteúdo original
     */
    function substituirBotaoPorLoading(botao, texto) {
        if (!botao) return function() {};
        
        // Salvar conteúdo original
        var conteudoOriginal = botao.innerHTML;
        var classesOriginais = Array.from(botao.classList);
        var disabledOriginal = botao.disabled;
        var cursorOriginal = botao.style.cursor;
        
        // Criar elemento de loading
        var loadingEl = criarLoadingComPontos(texto);
        
        // Limpar botão e adicionar loading
        botao.innerHTML = '';
        botao.appendChild(loadingEl);
        
        // Adicionar classes de loading
        botao.classList.add('btn-loading-temporario');
        botao.disabled = true;
        botao.style.cursor = 'wait';
        
        // Função para restaurar
        return function() {
            if (botao._loadingInterval) {
                clearInterval(botao._loadingInterval);
                botao._loadingInterval = null;
            }
            
            botao.innerHTML = conteudoOriginal;
            botao.classList.remove('btn-loading-temporario');
            
            // Restaurar classes
            classesOriginais.forEach(function(cls) {
                if (!botao.classList.contains(cls)) {
                    botao.classList.add(cls);
                }
            });
            
            botao.disabled = disabledOriginal;
            botao.style.cursor = cursorOriginal;
        };
    }
    
    /**
     * Criar overlay de loading para toda a aplicação
     * @param {string} mensagem - Mensagem a exibir
     */
    function mostrarOverlayLoading(mensagem) {
        var overlay = document.createElement('div');
        overlay.className = 'app-loading-overlay';
        overlay.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0, 0, 0, 0.7);
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            z-index: 9999;
            color: white;
            font-size: 16px;
            gap: 20px;
        `;
        
        var spinner = document.createElement('div');
        spinner.className = 'app-loading-spinner';
        spinner.style.cssText = `
            width: 60px;
            height: 60px;
            border: 4px solid rgba(255, 255, 255, 0.3);
            border-radius: 50%;
            border-top-color: #6a11cb;
            animation: spin 1s linear infinite;
        `;
        
        var texto = document.createElement('div');
        texto.className = 'app-loading-text';
        texto.textContent = mensagem;
        texto.style.cssText = 'font-weight: 700; font-size: 18px;';
        
        overlay.appendChild(spinner);
        overlay.appendChild(texto);
        
        // Adicionar CSS para animação se não existir
        if (!document.querySelector('#loading-animation-style')) {
            var style = document.createElement('style');
            style.id = 'loading-animation-style';
            style.textContent = `
                @keyframes spin {
                    0% { transform: rotate(0deg); }
                    100% { transform: rotate(360deg); }
                }
            `;
            document.head.appendChild(style);
        }
        
        document.body.appendChild(overlay);
        
        return overlay;
    }
    
    /**
     * Remover overlay de loading
     * @param {HTMLElement} overlay - Overlay a remover
     */
    function removerOverlayLoading(overlay) {
        if (overlay && overlay.parentNode) {
            overlay.parentNode.removeChild(overlay);
        }
    }
    
    return {
        criarLoadingComPontos: criarLoadingComPontos,
        limparAnimacaoPontos: limparAnimacaoPontos,
        substituirBotaoPorLoading: substituirBotaoPorLoading,
        mostrarOverlayLoading: mostrarOverlayLoading,
        removerOverlayLoading: removerOverlayLoading
    };
})();

console.log('✅ loader-utils.js carregado');