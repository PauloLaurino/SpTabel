// ============================================
// HELPERS - Funções utilitárias compartilhadas
// ============================================

window.helpers = (function() {
    
    function formatarAnoMes(elemento) {
        var valor = elemento.value.replace(/\D/g, '');
        if (valor.length > 6) {
            valor = valor.substring(0, 6);
        }
        elemento.value = valor;
    }
    
    // **FUNÇÃO CRÍTICA: Usada pelo api-client.js**
    function converterDataParaYYYYMMDD(dataISO) {
        try {
            return dataISO.replace(/-/g, '');
        } catch (e) {
            console.warn('⚠️ Erro ao converter data:', e);
            return null;
        }
    }
    
    function obterNomeSelo(tipo) {
        var nomes = {
            'TP1': 'Pago TP1',
            'TP3': 'Pago TP3',
            'TP4': 'Pago TP4',
            'TPD': 'Difer. TPD',
            'TPI': 'Isento TPI'
        };
        return nomes[tipo] || tipo;
    }
    
    function formatarNumero(numero) {
        return numero.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".");
    }
    
    function formatarCPF(cpf) {
        cpf = cpf.replace(/\D/g, '');
        return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
    }
    
    function calcularIdade(dataNascimento) {
        var hoje = new Date();
        var nascimento = new Date(dataNascimento);
        var idade = hoje.getFullYear() - nascimento.getFullYear();
        var mes = hoje.getMonth() - nascimento.getMonth();
        
        if (mes < 0 || (mes === 0 && hoje.getDate() < nascimento.getDate())) {
            idade--;
        }
        
        return idade;
    }
    
    // Função para extrair dados de resposta da API
    function extrairDadosResposta(response) {
        if (response && response.success === true) {
            return response.data || response.dados || response;
        } else if (response && response.sucesso === true) {
            return response.dados || response.data || response;
        }
        return response;
    }
    
    return {
        formatarAnoMes: formatarAnoMes,
        converterDataParaYYYYMMDD: converterDataParaYYYYMMDD,
        obterNomeSelo: obterNomeSelo,
        formatarNumero: formatarNumero,
        formatarCPF: formatarCPF,
        calcularIdade: calcularIdade,
        extrairDadosResposta: extrairDadosResposta
    };
})();