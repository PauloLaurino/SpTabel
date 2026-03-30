/* DEPRECIADO: usar validation-manager.js em vez deste arquivo
--------------------------------------------------------------

// ============================================
// VALIDATION - Funções de validação
// ============================================

window.validation = (function() {
    
    function validarEmail(email) {
        var re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }
    
    function validarData(data) {
        if (!data) return false;
        var date = new Date(data);
        return !isNaN(date.getTime());
    }
    
    function validarAnoMes(anoMes) {
        if (!anoMes || typeof anoMes !== 'string') return false;
        if (anoMes.length !== 6) return false;
        if (!/^\d{6}$/.test(anoMes)) return false;
        
        var ano = parseInt(anoMes.substring(0, 4));
        var mes = parseInt(anoMes.substring(4, 6));
        
        if (ano < 1900 || ano > 2100) return false;
        if (mes < 1 || mes > 12) return false;
        
        return true;
    }
    
    function validarApon(apon) {
        if (!apon || typeof apon !== 'string') return false;
        if (!/^\d{1,6}$/.test(apon)) return false;
        
        var num = parseInt(apon);
        return num >= 1 && num <= 999999;
    }
    
    function validarSeloQuantidade(quantidade) {
        if (quantidade === '' || quantidade === null || quantidade === undefined) return false;
        var qtd = parseInt(quantidade);
        return !isNaN(qtd) && qtd >= 0 && qtd <= 9999;
    }
    
    function validarIntervalo(intervalo) {
        if (!intervalo) return false;
        
        var temInicio = intervalo.anoMesInicial && intervalo.aponInicial;
        var temFim = intervalo.anoMesFinal && intervalo.aponFinal;
        
        return temInicio || temFim;
    }
    
    return {
        validarEmail: validarEmail,
        validarData: validarData,
        validarAnoMes: validarAnoMes,
        validarApon: validarApon,
        validarSeloQuantidade: validarSeloQuantidade,
        validarIntervalo: validarIntervalo
    };
})();

--------------------------------------------------------------
DEPRECIADO: usar validation-manager.js em vez deste arquivo */