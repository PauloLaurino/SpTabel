// ============================================
// DEBOUNCE UTILS - Utilitários de debouncing para FASE 3
// ============================================

window.debounceUtils = (function () {
    'use strict';

    // Cache de timeouts
    var timeouts = {};

    /**
     * Debounce avançado com cancelamento automático
     * @param {Function} fn - Função a ser executada
     * @param {number} delay - Delay em ms
     * @param {string} key - Chave única para cancelamento
     * @returns {Function} Função debounced
     */
    function debounce(fn, delay, key) {
        if (key && timeouts[key]) {
            clearTimeout(timeouts[key]);
            delete timeouts[key];
        }

        var timeoutId;

        return function () {
            var context = this;
            var args = arguments;

            clearTimeout(timeoutId);

            timeoutId = setTimeout(function () {
                fn.apply(context, args);

                // Limpar da cache após execução
                if (key) {
                    delete timeouts[key];
                }
            }, delay);

            // Armazenar para cancelamento posterior
            if (key) {
                timeouts[key] = timeoutId;
            }
        };
    }

    /**
     * Throttle para eventos frequentes
     * @param {Function} fn - Função a ser executada
     * @param {number} limit - Limite em ms
     * @returns {Function} Função throttled
     */
    function throttle(fn, limit) {
        var lastRun;
        var lastFunc;

        return function () {
            var context = this;
            var args = arguments;

            if (!lastRun) {
                fn.apply(context, args);
                lastRun = Date.now();
            } else {
                clearTimeout(lastFunc);

                lastFunc = setTimeout(function () {
                    if ((Date.now() - lastRun) >= limit) {
                        fn.apply(context, args);
                        lastRun = Date.now();
                    }
                }, limit - (Date.now() - lastRun));
            }
        };
    }

    /**
     * Cancelar debounce por chave
     * @param {string} key - Chave do debounce
     */
    function cancelDebounce(key) {
        if (timeouts[key]) {
            clearTimeout(timeouts[key]);
            delete timeouts[key];
        }
    }

    /**
     * Cancelar todos os debounces
     */
    function cancelAll() {
        for (var key in timeouts) {
            if (timeouts.hasOwnProperty(key)) {
                clearTimeout(timeouts[key]);
            }
        }
        timeouts = {};
    }

    /**
     * Debounce para inputs específicos do sistema
     * @param {string} inputId - ID do input
     * @param {Function} callback - Callback
     * @param {number} delay - Delay (opcional)
     */
    function debounceInput(inputId, callback, delay) {
        if (!delay) {
            // Delay padrão baseado no tipo de input
            if (inputId.includes('anoMes') || inputId.includes('apon')) {
                delay = 300; // Campos de intervalo
            } else if (inputId.includes('data')) {
                delay = 500; // Data do ato
            } else if (inputId.includes('inputTP')) {
                delay = 100; // Inputs de selos (mais rápido)
            } else {
                delay = 250; // Padrão
            }
        }

        var debounced = debounce(callback, delay, 'input_' + inputId);

        // Configurar automaticamente no input se existir
        setTimeout(function () {
            var input = document.getElementById(inputId);
            if (input) {
                input.addEventListener('input', debounced);
                input.addEventListener('change', debounced);
            }
        }, 100);

        return debounced;
    }

    return {
        // Funções principais
        debounce: debounce,
        throttle: throttle,

        // Funções específicas do sistema
        debounceInput: debounceInput,
        debounceBusca: function (callback) {
            return debounce(callback, 500, 'busca_intervalo');
        },
        debounceCalculo: function (callback) {
            return debounce(callback, 800, 'calculo_selos');
        },

        // Gerenciamento
        cancelDebounce: cancelDebounce,
        cancelAll: cancelAll,

        // Estatísticas
        getStats: function () {
            return {
                activeTimeouts: Object.keys(timeouts).length,
                timeoutKeys: Object.keys(timeouts)
            };
        }
    };
})();

console.log('✅ debounce-utils.js criado - FASE 3.1');