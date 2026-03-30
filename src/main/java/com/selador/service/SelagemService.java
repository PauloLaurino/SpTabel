package com.selador.service;

import com.selador.dao.ConnectionFactory;
import com.selador.util.FunarpenJsonGenerator;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Service para o processo de selagem - VERSÃO SIMPLIFICADA
 * SEM verificação de selos no ctp001 e SEM log complexo
 */
public class SelagemService {

    private static SelagemService instance;

    private SelagemService() {
        // Construtor privado para singleton
    }

    public static synchronized SelagemService getInstance() {
        if (instance == null) {
            instance = new SelagemService();
        }
        return instance;
    }

    // ============================================
    // MÉTODO COMPLETO SIMPLIFICADO
    // ============================================

    /**
 * Processa selagem completa - VERSÃO CORRIGIDA COM CONTAGEM CERTA
 */
public Map<String, Object> processarSelagemCompleta(
        String dataAto,
        String tipoOperacaoLabel,
        Map<String, String> intervalo,
        String usuario,
        Map<String, Integer> selosSolicitados) throws Exception {

    System.out.println("═══════════════════════════════════════════════════");
    System.out.println("🏁 [SELAGEM CORRIGIDA] INICIANDO");
    System.out.println("   Data Ato: " + dataAto);
    System.out.println("   Tipo Operação: " + tipoOperacaoLabel);
    System.out.println("   Selos Solicitados: " + selosSolicitados);

    Map<String, Object> resultadoFinal = new HashMap<>(); // 🔥 Mudei o nome para evitar conflito
    resultadoFinal.put("sucesso", false);
    resultadoFinal.put("timestamp", new java.util.Date());
    resultadoFinal.put("usuario", usuario);

    Connection conn = null;

    try {
        // ============================================
        // 1. VALIDAÇÃO BÁSICA
        // ============================================
        if (dataAto == null || dataAto.trim().isEmpty()) {
            throw new Exception("Data do ato é obrigatória");
        }

        if (tipoOperacaoLabel == null || tipoOperacaoLabel.trim().isEmpty()) {
            throw new Exception("Tipo de operação é obrigatório");
        }

        if (intervalo == null || intervalo.isEmpty()) {
            throw new Exception("Intervalo de apontamentos é obrigatório");
        }

        // Verificar selos solicitados
        if (selosSolicitados == null || selosSolicitados.isEmpty()) {
            throw new Exception("Nenhum selo foi solicitado");
        }

        boolean temSeloValido = false;
        for (Integer quantidade : selosSolicitados.values()) {
            if (quantidade != null && quantidade > 0) {
                temSeloValido = true;
                break;
            }
        }

        if (!temSeloValido) {
            throw new Exception("Nenhum selo válido foi solicitado (quantidade > 0)");
        }

        // ============================================
        // 2. OBTER CONEXÃO
        // ============================================
        conn = ConnectionFactory.getConnection();
        conn.setAutoCommit(false);

        // ============================================
        // 3. BUSCAR APONTAMENTOS (SIMPLES)
        // ============================================
        List<Map<String, Object>> apontamentos = buscarApontamentosBasico(
                conn, intervalo, tipoOperacaoLabel, dataAto);

        if (apontamentos.isEmpty()) {
            resultadoFinal.put("sucesso", true);
            resultadoFinal.put("mensagem", "Nenhum apontamento encontrado para selagem");
            resultadoFinal.put("total_apontamentos", 0);
            resultadoFinal.put("total_processados", 0);
            resultadoFinal.put("total_erros", 0);
            resultadoFinal.put("apontamentos", new ArrayList<>());
            return resultadoFinal;
        }

        System.out.println("✅ Apontamentos encontrados: " + apontamentos.size());

        // ============================================
        // 4. PROCESSAR CADA APONTAMENTO (VERSÃO CORRIGIDA)
        // ============================================
        List<Map<String, Object>> resultadoDetalhado = new ArrayList<>();
        int apontamentosComNovoSelo = 0;  // 🔥 NOME MAIS CLARO
        int apontamentosQueJaTinhamSelo = 0;
        int erros = 0;
        int totalSelosUtilizados = 0;

        // 🔥 MAPEAMENTO PARA CONTROLE DE SELOS APLICADOS
        Map<String, List<Map<String, Object>>> selosAplicadosPorApontamento = new HashMap<>();

        for (Map<String, Object> apontamento : apontamentos) {
            String numapo1 = (String) apontamento.get("numapo1");
            String numapo2 = (String) apontamento.get("numapo2");
            String protocolo = (String) apontamento.get("protocolo");
            String cpfcnpj = (String) apontamento.get("cpfcnpj");
            String devedor = (String) apontamento.get("devedor");
            String especie = (String) apontamento.get("especie_001");
            String idapOriginal = (String) apontamento.get("idap_001");
            
            String chaveApontamento = numapo1 + "/" + numapo2;
            
            Map<String, Object> detalhe = new HashMap<>();
            detalhe.put("protocolo", protocolo != null ? protocolo : "");
            detalhe.put("numapo1", numapo1 != null ? numapo1 : "");
            detalhe.put("numapo2", numapo2 != null ? numapo2 : "");
            detalhe.put("devedor", devedor != null ? devedor : "");
            detalhe.put("cpfcnpj", cpfcnpj != null ? cpfcnpj : "");
            detalhe.put("especie", especie != null ? especie : "");
            detalhe.put("status", "processando");
            detalhe.put("selos", new ArrayList<>());
            detalhe.put("observacao", "");
            
            System.out.println("\n📝 Processando " + chaveApontamento);
            
            try {
                // 🔥 FLAG PARA SABER SE APLICOU ALGUM NOVO SELO NESTE APONTAMENTO
                boolean aplicouAlgumNovoSelo = false;
                List<Map<String, Object>> selosAplicadosNeste = new ArrayList<>();
                
                // 🔥 PARA CADA TIPO DE SELO SOLICITADO
                for (Map.Entry<String, Integer> entry : selosSolicitados.entrySet()) {
                    String tipoSelo = entry.getKey();
                    int quantidadeSolicitada = entry.getValue();
                    
                    if (quantidadeSolicitada <= 0) continue;
                    
                    // Determinar qual NUMTIPATO corresponde a este tipo de selo para ESTA operação
                    String numtipato = determinarNumtipatoPorTipoSelo(tipoSelo, tipoOperacaoLabel);
                    
                    if (numtipato == null) {
                        System.out.println("   ⚠️ Tipo de selo não reconhecido para esta operação: " + tipoSelo);
                        continue;
                    }
                    
                    System.out.println("   🔍 Verificando selo " + numtipato + " (" + tipoSelo + ")");
                    
                    // 🔥 CORREÇÃO CRÍTICA: Verificar se JÁ TEM este selo específico para ESTA operação
                    if (verificarSeloExistente(conn, idapOriginal, numtipato)) {
                        System.out.println("   ℹ️ Apontamento JÁ TEM selo " + numtipato + " para " + tipoOperacaoLabel);
                        detalhe.put("observacao", "Já possuía selo para esta operação");
                        continue; // Pular para próximo tipo de selo
                    }
                    
                    // Buscar um selo disponível deste tipo
                    List<String> selosDisponiveis = buscarSelosDisponiveis(conn, tipoSelo, 1);
                    if (selosDisponiveis.isEmpty()) {
                        System.out.println("   ⚠️ Não há selos disponíveis do tipo " + tipoSelo);
                        detalhe.put("observacao", "Sem selos disponíveis do tipo " + tipoSelo);
                        continue;
                    }
                    
                    String numeroSelo = selosDisponiveis.get(0);
                    System.out.println("   ✅ Selo disponível encontrado: " + numeroSelo);
                    
                    // Buscar dtdistrib
                    String dtdistrib = buscarDtdistribParaApontamento(conn, numapo1, numapo2);
                    
                    // Gerar IDAP
                    String novoIdap = gerarIDAP(dataAto, numapo2, tipoOperacaoLabel, numtipato, dtdistrib);
                    
                    // Atualizar ctp001 com selo
                    atualizarCtp001ComSelo(conn, numapo1, numapo2, numeroSelo, novoIdap, numtipato);
                    
                    // Atualizar tabela selos
                    atualizarSeloComoUtilizado(conn, numeroSelo, numapo1, numapo2,
                                            dataAto, tipoOperacaoLabel, devedor,
                                            cpfcnpj, protocolo, usuario, novoIdap, numtipato);
                    
                    // Inserir na tabela selados
                    inserirNaTabelaSelados(conn, numtipato, tipoOperacaoLabel, numapo1, numapo2,
                                        numeroSelo, novoIdap, dataAto, protocolo, dtdistrib);
                    
                    // 🔥 MARCAR QUE APLICOU NOVO SELO
                    aplicouAlgumNovoSelo = true;
                    totalSelosUtilizados++;
                    
                    // Registrar selo utilizado
                    Map<String, Object> seloUtilizado = new HashMap<>();
                    seloUtilizado.put("numero", numeroSelo);
                    seloUtilizado.put("tipo", tipoSelo);
                    seloUtilizado.put("numtipato", numtipato);
                    seloUtilizado.put("idap", novoIdap);
                    selosAplicadosNeste.add(seloUtilizado);
                    
                    System.out.println("   ✅ APLICADO NOVO SELO: " + numeroSelo + " (" + tipoSelo + "/" + numtipato + ")");
                }
                
                // 🔥 CORREÇÃO: ATUALIZAR STATUS BASEADO NO QUE REALMENTE ACONTECEU
                if (aplicouAlgumNovoSelo) {
                    // APLICOU PELO MENOS UM NOVO SELO → "sucesso"
                    detalhe.put("status", "sucesso");
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> selosArray = (List<Map<String, Object>>) detalhe.get("selos");
                    selosArray.addAll(selosAplicadosNeste);
                    apontamentosComNovoSelo++;
                    System.out.println("   📊 STATUS: SUCESSO (aplicou novo selo)");
                } else {
                    // NÃO APLICOU NOVO SELO → verificar motivo
                    String observacao = detalhe.get("observacao").toString();
                    if (observacao.contains("Já possuía")) {
                        // JÁ TINHA SELO → "ja_possui"
                        detalhe.put("status", "ja_possui");
                        apontamentosQueJaTinhamSelo++;
                        System.out.println("   📊 STATUS: JÁ POSSUÍA SELO");
                    } else if (observacao.contains("Sem selos")) {
                        // SEM SELOS DISPONÍVEIS → "sem_selo"
                        detalhe.put("status", "sem_selo_disponivel");
                        erros++;
                        System.out.println("   📊 STATUS: ERRO - SEM SELOS DISPONÍVEIS");
                    } else {
                        // OUTRO ERRO → "erro"
                        detalhe.put("status", "erro");
                        erros++;
                        System.out.println("   📊 STATUS: ERRO");
                    }
                }
                
            } catch (Exception e) {
                System.out.println("   ❌ ERRO: " + e.getMessage());
                e.printStackTrace();
                
                detalhe.put("status", "erro");
                detalhe.put("causa_erro", e.getMessage());
                erros++;
            }
            
            resultadoDetalhado.add(detalhe);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> selosDoDetalhe = (List<Map<String, Object>>) detalhe.get("selos");
            selosAplicadosPorApontamento.put(chaveApontamento, selosDoDetalhe);
        }

        // ============================================
        // 5. COMMIT
        // ============================================
        conn.commit();
        System.out.println("💾 Transação commitada");

        // ============================================
        // 6. RESULTADO FINAL CORRETO
        // ============================================
        // 🔥 CALCULAR ESTATÍSTICAS CORRETAS
        int totalApontamentosAnalisados = apontamentos.size();
        int totalSucessoComNovoSelo = apontamentosComNovoSelo;
        int totalJaTinhaSelo = apontamentosQueJaTinhamSelo;
        int totalComErro = erros;

        // Verificar consistência
        int somaStatus = totalSucessoComNovoSelo + totalJaTinhaSelo + totalComErro;
        if (somaStatus != totalApontamentosAnalisados) {
            System.out.println("⚠️ INCONSISTÊNCIA DETECTADA: " + somaStatus + " != " + totalApontamentosAnalisados);
            
            // Corrigir contagem
            totalComErro += (totalApontamentosAnalisados - somaStatus);
            System.out.println("   Corrigido: erros = " + totalComErro);
        }

        // 🔥 CONSTRUIR MENSAGEM INFORMATIVA
        String mensagemResultado;
        if (totalSucessoComNovoSelo > 0) {
            mensagemResultado = "Selagem concluída: " + totalSucessoComNovoSelo + 
                             " apontamento(s) receberam novo selo.";
            
            if (totalJaTinhaSelo > 0) {
                mensagemResultado += " " + totalJaTinhaSelo + " já possuíam selo para esta operação.";
            }
            
            if (totalComErro > 0) {
                mensagemResultado += " " + totalComErro + " tiveram erro.";
            }
        } else if (totalJaTinhaSelo > 0) {
            mensagemResultado = "Nenhum novo selo aplicado. " + totalJaTinhaSelo + 
                             " apontamento(s) já possuíam selo para esta operação.";
            
            if (totalComErro > 0) {
                mensagemResultado += " " + totalComErro + " tiveram erro.";
            }
        } else {
            mensagemResultado = "Selagem não aplicou selos. " + totalComErro + " erro(s).";
        }

        // 🔥 PREPARAR RESULTADO DETALHADO PARA FRONTEND
        List<Map<String, Object>> apontamentosParaResposta = new ArrayList<>();
        for (Map<String, Object> detalhe : resultadoDetalhado) {
            Map<String, Object> apontamentoResposta = new HashMap<>();
            
            // 🔥 MANTER APENAS DADOS ESSENCIAIS PARA FRONTEND
            apontamentoResposta.put("protocolo", detalhe.get("protocolo"));
            apontamentoResposta.put("numapo1", detalhe.get("numapo1"));
            apontamentoResposta.put("numapo2", detalhe.get("numapo2"));
            apontamentoResposta.put("devedor", detalhe.get("devedor"));
            apontamentoResposta.put("cpfcnpj", detalhe.get("cpfcnpj"));
            apontamentoResposta.put("especie", detalhe.get("especie"));
            apontamentoResposta.put("status", detalhe.get("status"));
            
            // 🔥 SELOS APLICADOS (SÓ OS QUE REALMENTE FORAM APLICADOS AGORA)
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> selosDoDetalhe = (List<Map<String, Object>>) detalhe.get("selos");
            apontamentoResposta.put("selos", new ArrayList<>(selosDoDetalhe));
            
            // 🔥 OBSERVAÇÃO (se houver)
            if (detalhe.containsKey("observacao") && 
                !detalhe.get("observacao").toString().isEmpty()) {
                apontamentoResposta.put("observacao", detalhe.get("observacao"));
            }
            
            apontamentosParaResposta.add(apontamentoResposta);
        }

        // 🔥 RESULTADO FINAL CORRETO
        resultadoFinal.put("sucesso", true);
        resultadoFinal.put("mensagem", mensagemResultado);
        resultadoFinal.put("message", mensagemResultado); // Para compatibilidade

        // 🔥 ESTATÍSTICAS CORRETAS
        resultadoFinal.put("total_apontamentos", totalApontamentosAnalisados);
        resultadoFinal.put("total_processados", totalSucessoComNovoSelo); // 🔥 APENAS OS QUE RECEBERAM NOVO SELO
        resultadoFinal.put("total_ja_tinha_selos", totalJaTinhaSelo);     // 🔥 NOVO CAMPO
        resultadoFinal.put("total_erros", totalComErro);
        resultadoFinal.put("total_selos_utilizados", totalSelosUtilizados);

        // 🔥 DETALHES
        resultadoFinal.put("apontamentos", apontamentosParaResposta);
        resultadoFinal.put("selos_solicitados", selosSolicitados);
        resultadoFinal.put("usuario", usuario);
        resultadoFinal.put("timestamp", System.currentTimeMillis());

        System.out.println("\n🎉 CONCLUÍDO - ESTATÍSTICAS CORRETAS:");
        System.out.println("   📊 Total apontamentos analisados: " + totalApontamentosAnalisados);
        System.out.println("   ✅ Receberam NOVO selo: " + totalSucessoComNovoSelo);
        System.out.println("   ℹ️  Já tinham selo: " + totalJaTinhaSelo);
        System.out.println("   ❌ Com erro: " + totalComErro);
        System.out.println("   🏷️  Total selos utilizados: " + totalSelosUtilizados);
        System.out.println("   📝 Mensagem: " + mensagemResultado);

        return resultadoFinal;

    } catch (Exception e) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.out.println("❌ Erro ao fazer rollback: " + rollbackEx.getMessage());
            }
        }
        
        resultadoFinal.put("sucesso", false);
        resultadoFinal.put("mensagem", "Erro na selagem: " + e.getMessage());
        resultadoFinal.put("erro_detalhe", e.toString());
        
        throw e;
        
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException closeEx) {
                System.out.println("⚠️ Erro ao fechar conexão: " + closeEx.getMessage());
            }
        }
    }
}

    /**
     * 🔥 MÉTODO CORRIGIDO: Determina NUMTIPATO baseado no tipo de selo e operação
     * Mapeamento completo dos 15 códigos de selagem para os 5 tipos de selos
     */
    private String determinarNumtipatoPorTipoSelo(String tipoSelo, String tipoOperacaoLabel) {
        String tipoNormalizado = normalizarTipoOperacao(tipoOperacaoLabel);

        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("🔍 [DETERMINAR NUMTIPATO - CORRIGIDO]");
        System.out.println("   Tipo Selo: " + tipoSelo);
        System.out.println("   Tipo Operação: " + tipoNormalizado);
        System.out.println("   Código Original: " + tipoOperacaoLabel);
        System.out.println("═══════════════════════════════════════════════════");

        // 🔥 MAPEAMENTO COMPLETO DOS 15 CÓDIGOS PARA OS 5 TIPOS DE SELOS

        // ============================================
        // 1. SELO TPI (0004) - Isento
        // ============================================
        if ("TPI".equals(tipoSelo)) {
            switch (tipoNormalizado) {
                case "Apontamento":
                    System.out.println("✅ TPI → Apontamento → 701");
                    return "701";

                case "Intimação/Digitalização":
                    System.out.println("✅ TPI → Intimação/Digitalização → 703");
                    return "703";

                case "Retirada/Baixa":
                case "Pagamento/Baixa":
                    // Para baixas, TPI não é usado geralmente
                    System.out.println("⚠️ TPI normalmente não usado para baixas");
                    return null;

                case "Devolução por Irregularidade":
                    System.out.println("✅ TPI → Devolução → 729");
                    return "729";

                case "Sustação do Protesto":
                    System.out.println("✅ TPI → Sustação Protesto → 734");
                    return "734";

                case "Revogação de Sustação":
                    System.out.println("✅ TPI → Revogação Sustação → 734 (mesmo código)");
                    return "734"; // Mesmo código, diferente descrição

                case "Sustação Definitiva":
                    System.out.println("✅ TPI → Sustação Definitiva → 727");
                    return "727";

                case "Suspensão de Protesto":
                    System.out.println("✅ TPI → Suspensão Protesto → 733");
                    return "733";

                case "Revogação de Suspenso":
                    System.out.println("✅ TPI → Revogação Suspenso → 733 (mesmo código)");
                    return "733"; // Mesmo código, diferente descrição

                case "Segunda Revogação":
                    System.out.println("✅ TPI → Segunda Revogação → 733 (mesmo código)");
                    return "733"; // Mesmo código, diferente descrição

                default:
                    System.out.println("⚠️ TPI → Operação não mapeada: " + tipoNormalizado);
                    return "701"; // Fallback: Apontamento
            }
        }

        // ============================================
        // 2. SELO TP1 (0001) - Protesto Comum
        // ============================================
        if ("TP1".equals(tipoSelo)) {
            switch (tipoNormalizado) {
                case "Intimação/Digitalização":
                    System.out.println("✅ TP1 → Intimação → 702");
                    return "702";

                case "Retirada/Baixa":
                case "Pagamento/Baixa":
                    System.out.println("✅ TP1 → Baixa → 735");
                    return "735";

                case "Instrumento Diferido":
                    System.out.println("⚠️ TP1 não usado para Instrumento Diferido");
                    return null;

                case "Protesto com Custas Antecipadas":
                    System.out.println("⚠️ TP1 não usado para Custas Antecipadas");
                    return null;

                default:
                    System.out.println("⚠️ TP1 → Operação não mapeada: " + tipoNormalizado);
                    return null;
            }
        }

        // ============================================
        // 3. SELO TP3 (0009) - Edital
        // ============================================
        if ("TP3".equals(tipoSelo)) {
            switch (tipoNormalizado) {
                case "Intimação/Digitalização":
                    System.out.println("✅ TP3 → Intimação Edital → 725");
                    return "725";

                case "Retirada/Baixa":
                case "Pagamento/Baixa":
                    System.out.println("✅ TP3 → Baixa Edital → 736");
                    return "736";

                default:
                    System.out.println("⚠️ TP3 → Operação não mapeada: " + tipoNormalizado);
                    return null;
            }
        }

        // ============================================
        // 4. SELO TPD (0003) - Distribuição/Diferido
        // ============================================
        if ("TPD".equals(tipoSelo)) {
            switch (tipoNormalizado) {
                case "Intimação/Digitalização":
                    System.out.println("✅ TPD → Intimação Distribuição → 738");
                    return "738";

                case "Instrumento Diferido":
                    System.out.println("✅ TPD → Instrumento Diferido → 739");
                    return "739";

                case "Retirada/Baixa":
                case "Pagamento/Baixa":
                    // Para Intimação com Distrato/TAC (740)
                    System.out.println("✅ TPD → Intimação Distrato → 740");
                    return "740";

                default:
                    System.out.println("⚠️ TPD → Operação não mapeada: " + tipoNormalizado);
                    return null;
            }
        }

        // ============================================
        // 5. SELO TP4 (0010) - Jornal/Título
        // ============================================
        if ("TP4".equals(tipoSelo)) {
            switch (tipoNormalizado) {
                case "Retirada/Baixa":
                    System.out.println("✅ TP4 → Retirada → 705");
                    return "705";

                case "Pagamento/Baixa":
                    System.out.println("✅ TP4 → Pagamento → 704");
                    return "704";

                case "Protesto com Custas Antecipadas":
                    System.out.println("✅ TP4 → Custas Antecipadas → 706");
                    return "706";

                default:
                    System.out.println("⚠️ TP4 → Operação não mapeada: " + tipoNormalizado);
                    return null;
            }
        }

        System.out.println("❌ Nenhum mapeamento encontrado para: " + tipoSelo + " → " + tipoNormalizado);
        return null;
    }

    /**
     * Método compatibilidade - versão sem selos solicitados
     */
    public Map<String, Object> processarSelagemCompleta(
            String dataAto,
            String tipoOperacaoLabel,
            Map<String, String> intervalo,
            String usuario) throws Exception {
        return processarSelagemCompleta(dataAto, tipoOperacaoLabel, intervalo, usuario, null);
    }

    // ============================================
    // MÉTODOS AUXILIARES SIMPLIFICADOS
    // ============================================

    /**
     * Busca apontamentos NÃO SELADOS - VERSÃO FINAL SIMPLES
     */
    private List<Map<String, Object>> buscarApontamentosBasico(
            Connection conn, Map<String, String> intervalo,
            String tipoOperacaoLabel, String dataAto) throws SQLException {

        String anoMesInicial = intervalo.get("anoMesInicial");
        String anoMesFinal = intervalo.get("anoMesFinal") != null ? intervalo.get("anoMesFinal") : anoMesInicial;
        String aponInicial = intervalo.get("aponInicial");
        String aponFinal = intervalo.get("aponFinal");

        System.out.println("\n🔍 [BUSCA SIMPLES]");
        System.out.println("   Tipo: " + tipoOperacaoLabel);
        System.out.println("   Data: " + dataAto);
        System.out.println("   Intervalo: " + anoMesInicial + "/" + aponInicial +
                " até " + anoMesFinal + "/" + aponFinal);

        // Obter coluna data e situação
        String colunaData = determinarColunaDataPorTipo(tipoOperacaoLabel);
        String situacao = obterSituacaoPorTipo(tipoOperacaoLabel);

        // 🔥 SIMPLES: Buscar TODOS os apontamentos que atendem aos critérios básicos
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT ")
                .append("c.numapo1_001, ")
                .append("c.numapo2_001, ")
                .append("c.devedor_001, ")
                .append("c.class_001, ")
                .append("c.numer_001, ")
                .append("c.digito_001, ")
                .append("c.especie_001, ")
                .append("c.situacao_001, ")
                .append("c.dtdistrib_001, ")
                .append("c.idap_001 ")
                .append("FROM ctp001 c ")
                .append("WHERE c.controle_001 = '01' ");

        // Condição numapo1
        if (anoMesInicial.equals(anoMesFinal)) {
            sql.append("AND c.numapo1_001 = ? ");
        } else {
            sql.append("AND c.numapo1_001 BETWEEN ? AND ? ");
        }

        // Condição numapo2
        sql.append("AND CAST(c.numapo2_001 AS UNSIGNED) BETWEEN ? AND ? ");

        // Condição data
        String dataParaConsulta = converterDataParaFormatoBancoPorTipoSelagem(dataAto, tipoOperacaoLabel);
        sql.append("AND ").append(colunaData).append(" = ? ");

        // Condição de situação
        if (situacao != null) {
            sql.append("AND c.situacao_001 = ? ");
        }

        // 🔥 NÃO ADICIONAR NENHUM JOIN OU NOT EXISTS AQUI!
        // A verificação de selos existentes será feita DEPOIS, para CADA selo
        // específico

        sql.append("ORDER BY CAST(c.numapo1_001 AS UNSIGNED), CAST(c.numapo2_001 AS UNSIGNED)");

        System.out.println("📝 SQL SIMPLES (sem verificação de selos):");
        System.out.println(sql.toString().replace("\n", " "));

        // Preparar parâmetros
        List<Object> params = new ArrayList<>();

        if (anoMesInicial.equals(anoMesFinal)) {
            params.add(anoMesInicial);
        } else {
            params.add(anoMesInicial);
            params.add(anoMesFinal);
        }

        params.add(Integer.parseInt(aponInicial));
        params.add(Integer.parseInt(aponFinal));
        params.add(dataParaConsulta);

        if (situacao != null) {
            params.add(situacao);
        }

        System.out.println("🔢 Parâmetros: " + params);

        // Executar query
        List<Map<String, Object>> apontamentos = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) param);
                } else {
                    pstmt.setString(i + 1, param.toString());
                }
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> apontamento = new HashMap<>();

                    String numapo1 = rs.getString("numapo1_001");
                    String numapo2 = rs.getString("numapo2_001");

                    apontamento.put("numapo1", numapo1);
                    apontamento.put("numapo2", numapo2);
                    apontamento.put("devedor", rs.getString("devedor_001"));
                    apontamento.put("class_001", rs.getString("class_001"));
                    apontamento.put("numer_001", rs.getString("numer_001"));
                    apontamento.put("digito_001", rs.getString("digito_001"));
                    apontamento.put("especie_001", rs.getString("especie_001"));
                    apontamento.put("situacao_001", rs.getString("situacao_001"));
                    apontamento.put("dtdistrib_001", rs.getString("dtdistrib_001"));
                    apontamento.put("idap_001", rs.getString("idap_001"));

                    // Protocolo = numapo2
                    try {
                        apontamento.put("protocolo", String.valueOf(Integer.parseInt(numapo2)));
                    } catch (NumberFormatException e) {
                        apontamento.put("protocolo", numapo2);
                    }

                    // CPF/CNPJ
                    apontamento.put("cpfcnpj", calcularCpfCnpj(rs));

                    apontamentos.add(apontamento);
                }
            }
        }

        System.out.println("✅ Apontamentos encontrados: " + apontamentos.size());

        return apontamentos;
    }

    /**
     * Normalizar tipo de operação - VERSÃO SIMPLIFICADA
     */
    private String normalizarTipoOperacao(String tipoOperacaoLabel) {
        if (tipoOperacaoLabel == null)
            return "Apontamento";

        String tipoUpper = tipoOperacaoLabel.toUpperCase();

        if (tipoUpper.contains("INTIMA") || tipoUpper.contains("DIGITALIZA")) {
            return "Intimação/Digitalização";
        }
        if (tipoUpper.contains("PAGAMENTO")) {
            return "Pagamento/Baixa";
        }
        if (tipoUpper.contains("RETIRADA")) {
            return "Retirada/Baixa";
        }
        if (tipoUpper.contains("INSTRUMENTO") && tipoUpper.contains("DIFERIDO")) {
            return "Instrumento Diferido";
        }
        if (tipoUpper.contains("PROTESTO") && tipoUpper.contains("CUSTAS")) {
            return "Protesto com Custas Antecipadas";
        }
        if (tipoUpper.contains("DEVOLU")) {
            return "Devolução por Irregularidade";
        }
        if (tipoUpper.contains("SUSTA") && tipoUpper.contains("DEFINITIVA")) {
            return "Sustação Definitiva";
        }
        if (tipoUpper.contains("REVOGA") && tipoUpper.contains("SUSTA")) {
            return "Revogação de Sustação";
        }
        if (tipoUpper.contains("SUSTA")) {
            return "Sustação do Protesto";
        }
        if (tipoUpper.contains("SEGUNDA") && tipoUpper.contains("REVOGA")) {
            return "Segunda Revogação";
        }
        if (tipoUpper.contains("REVOGA") && tipoUpper.contains("SUSPENSO")) {
            return "Revogação de Suspenso";
        }
        if (tipoUpper.contains("SUSPEN")) {
            return "Suspensão de Protesto";
        }
        if (tipoUpper.contains("APONTAMENTO")) {
            return "Apontamento";
        }
        if (tipoUpper.contains("BAIXA")) {
            return "Retirada/Baixa";
        }

        return "Apontamento";
    }

    /**
     * Determinar coluna data por tipo
     */
    private String determinarColunaDataPorTipo(String tipoOperacaoLabel) {
        String tipoNormalizado = normalizarTipoOperacao(tipoOperacaoLabel);

        switch (tipoNormalizado) {
            case "Apontamento":
            case "Intimação/Digitalização":
                return "dataapo_001";
            case "Retirada/Baixa":
                return "dataret_001";
            case "Pagamento/Baixa":
                return "datapag_001";
            case "Instrumento Diferido":
            case "Protesto com Custas Antecipadas":
                return "databai_001";
            case "Devolução por Irregularidade":
                return "dtindevido_001";
            case "Sustação do Protesto":
                return "dtsusta_001";
            case "Revogação de Sustação":
                return "dtrevog_001";
            case "Sustação Definitiva":
                return "dtproces_001";
            case "Suspensão de Protesto":
                return "dtsuspenso_001";
            case "Revogação de Suspenso":
                return "dtrevsusp_001";
            case "Segunda Revogação":
                return "datasusp2_001";
            default:
                return "dataapo_001";
        }
    }

    /**
     * Converter data para formato do banco - VERSÃO FINAL CORRIGIDA
     */
    private String converterDataParaFormatoBancoPorTipoSelagem(String dataFormatada, String tipoFrontend) {
        try {
            String tipoNormalizado = normalizarTipoOperacao(tipoFrontend);

            System.out.println("🔄 [CONVERTER DATA] Tipo: " + tipoNormalizado);
            System.out.println("   Data recebida: " + dataFormatada);

            // Tipos que usam DDMMYYYY COM zero no mês (como dtintimacao, dtsusta,
            // dtindevido)
            String[] tiposDDMMYYYYComZero = {
                    "Intimação/Digitalização",
                    "Devolução por Irregularidade",
                    "Sustação do Protesto",
                    "Revogação de Sustação",
                    "Sustação Definitiva",
                    "Suspensão de Protesto",
                    "Revogação de Suspenso",
                    "Segunda Revogação"
            };

            boolean usaDDMMYYYYComZero = false;
            for (String tipo : tiposDDMMYYYYComZero) {
                if (tipo.equals(tipoNormalizado)) {
                    usaDDMMYYYYComZero = true;
                    break;
                }
            }

            // Extrair partes da data
            String dia, mes, ano;

            if (dataFormatada.matches("\\d{4}[-/]\\d{2}[-/]\\d{2}")) {
                // Formato: 2026-01-26
                String[] partes = dataFormatada.split("[-/]");
                ano = partes[0];
                mes = partes[1];
                dia = partes[2];
            } else if (dataFormatada.matches("\\d{8}")) {
                // Formato: 20260126 (YYYYMMDD)
                ano = dataFormatada.substring(0, 4);
                mes = dataFormatada.substring(4, 6);
                dia = dataFormatada.substring(6, 8);
            } else {
                // Fallback: parse com SimpleDateFormat
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date date = sdf.parse(dataFormatada.split("T")[0]); // Remove hora se houver

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                ano = String.valueOf(cal.get(Calendar.YEAR));
                mes = String.format("%02d", cal.get(Calendar.MONTH) + 1);
                dia = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
            }

            // Remover zeros à esquerda do dia, mas manter no mês
            int diaInt = Integer.parseInt(dia);

            if (usaDDMMYYYYComZero) {
                // Formato: DDMMYYYY COM zero no mês
                // Exemplo: 26/01/2026 → "26012026"
                String resultado = diaInt + mes + ano;
                System.out.println("   ✅ DDMMYYYY com zero no mês: " + resultado);
                return resultado;
            } else {
                // Formato: YYYYMMDD
                // Exemplo: 26/01/2026 → "20260126"
                String resultado = ano + mes + String.format("%02d", diaInt);
                System.out.println("   ✅ YYYYMMDD: " + resultado);
                return resultado;
            }

        } catch (Exception e) {
            System.out.println("❌ Erro na conversão: " + e.getMessage());

            // Fallback com data atual
            Calendar cal = Calendar.getInstance();
            int dia = cal.get(Calendar.DAY_OF_MONTH);
            int mes = cal.get(Calendar.MONTH) + 1;
            int ano = cal.get(Calendar.YEAR);

            if (tipoFrontend.contains("Intima") || tipoFrontend.contains("Devol") ||
                    tipoFrontend.contains("Susta") || tipoFrontend.contains("Suspens")) {
                // DDMMYYYY com zero no mês
                return dia + String.format("%02d", mes) + ano;
            } else {
                // YYYYMMDD
                return String.format("%04d%02d%02d", ano, mes, dia);
            }
        }
    }

    /**
     * Calcular CPF/CNPJ
     */
    private String calcularCpfCnpj(ResultSet rs) throws SQLException {
        try {
            String numer001 = rs.getString("numer_001");
            String digito001 = rs.getString("digito_001");

            if (numer001 == null || numer001.trim().isEmpty()) {
                return "";
            }

            // Implementação básica
            return numer001 + (digito001 != null ? digito001 : "");

        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Busca selos disponíveis com controle de quantidade
     */
    private List<String> buscarSelosDisponiveis(Connection conn, String tipoFrontend, int quantidadeNecessaria)
            throws SQLException {
        String tipoBanco = getTipoBancoPorFrontend(tipoFrontend);

        if (tipoBanco == null) {
            System.out.println("⚠️ Tipo de selo não mapeado: " + tipoFrontend);
            return new ArrayList<>();
        }

        String sql = "SELECT selo_sel " +
                "FROM selos " +
                "WHERE tipo_sel = '0000' " + // Disponíveis (não utilizados)
                "AND tiposelo_sel = ? " +
                "AND (chave1_sel IS NULL OR chave1_sel = '' OR chave1_sel = ' ') " +
                "ORDER BY selo_sel ASC " +
                "LIMIT ?";

        List<String> selos = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tipoBanco);
            pstmt.setInt(2, quantidadeNecessaria);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    selos.add(rs.getString("selo_sel"));
                }
            }
        }

        if (selos.size() < quantidadeNecessaria) {
            System.out.println("⚠️ Disponível apenas " + selos.size() + " de " +
                    quantidadeNecessaria + " selos do tipo " + tipoFrontend);
        } else {
            System.out.println("✅ Encontrados " + selos.size() + " selos disponíveis do tipo " + tipoFrontend);
        }

        return selos;
    }

    /**
     * 🔥 CORREÇÃO: Verificar se um apontamento já tem selo ESPECÍFICO
     * Usando IDAP completo e numtipato específico
     */
    private boolean verificarSeloExistente(Connection conn, String idap, String numtipato) throws SQLException {
        if (idap == null || idap.trim().isEmpty()) {
            System.out.println("⚠️ IDAP nulo ou vazio para verificar selo " + numtipato);
            return false;
        }

        // 🔥 DUAS ESTRATÉGIAS DE VERIFICAÇÃO

        // 1. Verificar na tabela selados (mais confiável)
        String sqlSelados = "SELECT COUNT(*) as count FROM selados WHERE IDAP = ? AND NUMTIPATO = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelados)) {
            pstmt.setString(1, idap);
            pstmt.setString(2, numtipato);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    if (count > 0) {
                        System.out.println("   🔍 Encontrado na tabela selados: IDAP='" + idap +
                                "', NUMTIPATO=" + numtipato);
                        return true;
                    }
                }
            }
        }

        // 2. Verificar com LEFT(IDAP, 20) como fallback (para compatibilidade)
        if (idap.length() > 20) {
            String idapTruncado = idap.substring(0, 20);
            String sqlLeft = "SELECT COUNT(*) as count FROM selados WHERE LEFT(IDAP, 20) = ? AND NUMTIPATO = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sqlLeft)) {
                pstmt.setString(1, idapTruncado);
                pstmt.setString(2, numtipato);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int count = rs.getInt("count");
                        boolean tem = count > 0;
                        System.out.println("   🔍 Verificação LEFT(IDAP,20)='" + idapTruncado +
                                "', NUMTIPATO=" + numtipato + " -> " +
                                (tem ? "✅ TEM" : "❌ NÃO TEM"));
                        return tem;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Converte tipo frontend para tipo banco
     */
    private String getTipoBancoPorFrontend(String tipoFrontend) {
        Map<String, String> mapeamento = new HashMap<>();
        mapeamento.put("TP1", "0001");
        mapeamento.put("TPD", "0003");
        mapeamento.put("TPI", "0004");
        mapeamento.put("TP3", "0009");
        mapeamento.put("TP4", "0010");

        return mapeamento.getOrDefault(tipoFrontend, "0004");
    }

    /**
     * Buscar dtdistrib_001
     */
    private String buscarDtdistribParaApontamento(Connection conn, String numapo1, String numapo2)
            throws SQLException {

        String sql = "SELECT dtdistrib_001 FROM ctp001 " +
                "WHERE controle_001 = '01' " +
                "AND numapo1_001 = ? " +
                "AND numapo2_001 = ? " +
                "LIMIT 1";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, numapo1);
            pstmt.setString(2, numapo2);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("dtdistrib_001");
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * Gera IDAP - VERSÃO CORRIGIDA COM YYYYMMDD
     */
    private String gerarIDAP(String dataAto, String numapo2, String tipoOperacaoLabel,
            String numtipato, String dtdistrib) {
        try {
            // 🔥 CORREÇÃO: Usar dtdistrib formatado como YYYYMMDD
            String dataFormatada = converterDtdistribParaYYYYMMDD(dtdistrib);

            System.out.println("🔄 [GERAR IDAP] Data formatada para IDAP: " + dataFormatada +
                    " (dtdistrib original: " + dtdistrib + ")");

            // Verificar se a data está no formato YYYYMMDD
            if (!dataFormatada.matches("^\\d{4}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])$")) {
                System.out.println("⚠️ Data formatada inválida para IDAP. Usando data atual.");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                dataFormatada = sdf.format(new java.util.Date());
            }

            // numapo2 formatado para 10 dígitos
            String numapo2Formatado = formatarNumapo2(numapo2);

            // Parte fixa do IDAP: YYYYMMDD + "TP" + numapo2 (10 dígitos)
            String parteFixa = dataFormatada + "TP" + numapo2Formatado;

            // Código do ato (3 dígitos)
            String codigoAto = formatarNumtipato(numtipato);

            // IDAP final
            String idap = parteFixa + codigoAto;

            // Garantir 40 caracteres
            return garantir40Caracteres(idap);

        } catch (Exception e) {
            System.out.println("❌ [GERAR IDAP] Erro na geração: " + e.getMessage());

            // Fallback seguro
            return gerarIDAPFallback(numapo2, numtipato);
        }
    }

    /**
     * Formata numapo2 para 10 dígitos
     */
    private String formatarNumapo2(String numapo2) {
        if (numapo2 == null || numapo2.trim().isEmpty()) {
            return "0000000000";
        }

        try {
            long numApo2Long = Long.parseLong(numapo2.trim());
            return String.format("%010d", numApo2Long);
        } catch (NumberFormatException e) {
            // Se não for número, usar hash
            int hash = Math.abs(numapo2.hashCode()) % 1000000000;
            return String.format("%010d", hash);
        }
    }

    /**
     * Formata numtipato para 3 dígitos
     */
    private String formatarNumtipato(String numtipato) {
        if (numtipato == null || numtipato.trim().isEmpty()) {
            return "701";
        }

        String limpo = numtipato.trim();
        if (limpo.length() < 3) {
            return String.format("%03d", Integer.parseInt(limpo));
        } else if (limpo.length() > 3) {
            return limpo.substring(0, 3);
        } else {
            return limpo;
        }
    }

    /**
     * Garante que o IDAP tenha 40 caracteres
     */
    private String garantir40Caracteres(String idap) {
        if (idap.length() > 40) {
            return idap.substring(0, 40);
        }

        int zeros = 40 - idap.length();
        return idap + String.format("%0" + zeros + "d", 0);
    }

    /**
     * Fallback para geração de IDAP
     */
    private String gerarIDAPFallback(String numapo2, String numtipato) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dataAtual = sdf.format(new java.util.Date());

        String numapo2Formatado = formatarNumapo2(numapo2);
        String codigoAto = formatarNumtipato(numtipato);

        String idap = dataAtual + "TP" + numapo2Formatado + codigoAto;
        return garantir40Caracteres(idap);
    }

    /**
     * Converte dtdistrib para YYYYMMDD - ESPECÍFICO PARA IDAP
     */
    private String converterDtdistribParaYYYYMMDD(String dtdistrib) {
        if (dtdistrib == null || dtdistrib.trim().isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            return sdf.format(new java.util.Date());
        }

        String dtdistribLimpo = dtdistrib.trim();

        try {
            // Se já está no formato YYYYMMDD (8 dígitos)
            if (dtdistribLimpo.matches("^\\d{8}$")) {
                // Verificar se está no formato correto (YYYYMMDD)
                String ano = dtdistribLimpo.substring(0, 4);
                String mes = dtdistribLimpo.substring(4, 6);
                String dia = dtdistribLimpo.substring(6, 8);

                int anoInt = Integer.parseInt(ano);
                int mesInt = Integer.parseInt(mes); // Agora está sendo usado
                int diaInt = Integer.parseInt(dia);

                // Validar se é uma data válida
                if (anoInt >= 1900 && anoInt <= 2100 &&
                        mesInt >= 1 && mesInt <= 12 &&
                        diaInt >= 1 && diaInt <= 31) {
                    // Já está em YYYYMMDD válido
                    return dtdistribLimpo;
                }

                // Se parece estar invertido (DDMMYYYY)
                if (anoInt <= 31 && anoInt >= 1 && diaInt >= 1900) {
                    // Está no formato DDMMYYYY, converter para YYYYMMDD
                    return dia + mes + ano;
                }

                // Se chegou aqui, usar data atual
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                return sdf.format(new java.util.Date());
            }

            // Converter formatos com separadores
            if (dtdistribLimpo.contains("/") || dtdistribLimpo.contains("-")) {
                // Normalizar separadores
                String normalizado = dtdistribLimpo.replace('-', '/');
                String[] partes = normalizado.split("/");

                if (partes.length == 3) {
                    String parte1 = partes[0];
                    String parte2 = partes[1];
                    String parte3 = partes[2];

                    // Determinar qual parte é o ano (4 dígitos)
                    if (parte1.length() == 4) {
                        // Formato: YYYY/MM/DD
                        return parte1 +
                                String.format("%02d", Integer.parseInt(parte2)) +
                                String.format("%02d", Integer.parseInt(parte3));
                    } else if (parte3.length() == 4) {
                        // Formato: DD/MM/YYYY
                        return parte3 +
                                String.format("%02d", Integer.parseInt(parte2)) +
                                String.format("%02d", Integer.parseInt(parte1));
                    } else if (parte3.length() == 2) {
                        // Formato: DD/MM/YY
                        int ano = Integer.parseInt(parte3);
                        if (ano < 50) {
                            ano += 2000;
                        } else {
                            ano += 1900;
                        }
                        return String.format("%04d", ano) +
                                String.format("%02d", Integer.parseInt(parte2)) +
                                String.format("%02d", Integer.parseInt(parte1));
                    }
                }
            }

            // Tentar vários formatos de parse
            String[] formatos = {
                    "dd/MM/yyyy", "dd-MM-yyyy", "dd/MM/yy", "dd-MM-yy",
                    "yyyy/MM/dd", "yyyy-MM-dd", "ddMMyyyy", "yyyyMMdd"
            };

            for (String formato : formatos) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(formato);
                    sdf.setLenient(false);
                    java.util.Date date = sdf.parse(dtdistribLimpo);

                    SimpleDateFormat output = new SimpleDateFormat("yyyyMMdd");
                    return output.format(date);
                } catch (Exception e) {
                    // Continuar tentando outros formatos
                }
            }

        } catch (Exception e) {
            System.out.println("⚠️ Erro ao converter dtdistrib: " + dtdistrib + " - " + e.getMessage());
        }

        // Fallback: usar data atual
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new java.util.Date());
    }

    /**
     * Atualiza ctp001 com selo
     */
    private void atualizarCtp001ComSelo(
            Connection conn, String numapo1, String numapo2,
            String numeroSelo, String idap, String numtipato) throws SQLException {

        String campoParaSelo = determinarCampoSeloPorNumtipato(numtipato);

        String sql = "UPDATE ctp001 SET " + campoParaSelo + " = ?, idap_001 = ? " +
                "WHERE numapo1_001 = ? AND numapo2_001 = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, numeroSelo);
            pstmt.setString(2, idap);
            pstmt.setString(3, numapo1);
            pstmt.setString(4, numapo2);
            pstmt.executeUpdate();
        }
    }

    /**
     * Determina campo de selo por numtipato
     */
    private String determinarCampoSeloPorNumtipato(String numtipato) {
        if (numtipato == null)
            return "seloaponta_001";

        switch (numtipato) {
            case "701":
                return "seloaponta_001";
            case "705":
            case "706":
            case "739":
            case "704":
            case "729":
                return "selotit_001";
            case "738":
            case "735":
            case "702":
                return "seloint_001";
            case "740":
            case "725":
            case "736":
                return "digital_001";
            case "703":
                return "seloremessa_001";
            case "727":
            case "733":
            case "734":
            default:
                return "seloisento_001";
        }
    }

    /**
     * Atualiza selo como utilizado
     */
    private void atualizarSeloComoUtilizado(
            Connection conn, String numeroSelo, String numapo1, String numapo2,
            String dataAto, String tipoOperacao, String devedor, String cpfcnpj,
            String protocolo, String usuario, String idap, String numtipato) throws SQLException {

        String dataSelo = converterDataParaYYYYMMDD(dataAto);
        String tipoSelCompleto = String.format("0%03d", Integer.parseInt(numtipato));

        String sql = "UPDATE selos SET " +
                "chave1_sel = ?, " +
                "chave2_sel = ?, " +
                "dataselo_sel = ?, " +
                "tipo_sel = ?, " +
                "numreq_sel = '0000000000', " +
                "codtipoato = ?, " +
                "nome = ?, " +
                "cpfcnpj = ?, " +
                "protocolo = ?, " +
                "datahora = ?, " +
                "chavedigital = ?, " +
                "IDAP = ? " +
                "WHERE selo_sel = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, numapo1);
            pstmt.setString(2, numapo2);
            pstmt.setString(3, dataSelo);
            pstmt.setString(4, tipoSelCompleto);
            pstmt.setInt(5, Integer.parseInt(numtipato));
            pstmt.setString(6, devedor != null ? devedor : "");
            pstmt.setString(7, cpfcnpj != null ? cpfcnpj : "");
            pstmt.setString(8, protocolo != null ? protocolo : "");
            pstmt.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(10, numeroSelo);
            pstmt.setString(11, idap);
            pstmt.setString(12, numeroSelo);

            pstmt.executeUpdate();
        }
    }

    /**
     * Converte data para YYYYMMDD
     */
    private String converterDataParaYYYYMMDD(String data) {
        try {
            if (data == null || data.trim().isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                return sdf.format(new java.util.Date());
            }

            if (data.contains("T")) {
                data = data.split("T")[0];
            }

            String dataLimpa = data.replace("-", "").replace("/", "");

            if (dataLimpa.matches("\\d{8}")) {
                return dataLimpa;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            return sdf.format(new java.util.Date());

        } catch (Exception e) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            return sdf.format(new java.util.Date());
        }
    }

    /**
     * Insere na tabela selados - VERSÃO CORRIGIDA
     */
    private void inserirNaTabelaSelados(
            Connection conn, String numtipato, String tipoOperacao, String numapo1, String numapo2,
            String numeroSelo, String idap, String dataAto, String protocolo, String dtdistrib) {

        try {
            String qrcodeUrl = "https://selo.funarpen.com.br/consulta/" + numeroSelo;
            String dataEnvio = formatarDataEnvio(dataAto);

            // Gerar JSON V2
            String jsonFunarpen = "";
            try {
                Map<String, Object> dadosSelo = buscarDadosSelo(conn, numeroSelo);
                jsonFunarpen = FunarpenJsonGenerator.gerarJsonParaAto(
                        conn, Integer.parseInt(numtipato), tipoOperacao, numapo1, numapo2,
                        numeroSelo, idap, dadosSelo);
            } catch (Exception e) {
                System.out.println("⚠️ Não foi possível gerar JSON V2: " + e.getMessage());
            }

            // 🔥 VERIFICAR SE JÁ EXISTE (USANDO LEFT 20)
            String idapTruncado = idap.length() > 20 ? idap.substring(0, 20) : idap;
            String sqlVerificar = "SELECT COUNT(*) as count FROM selados WHERE LEFT(IDAP, 20) = ? AND NUMTIPATO = ?";

            try (PreparedStatement pstmtVerif = conn.prepareStatement(sqlVerificar)) {
                pstmtVerif.setString(1, idapTruncado);
                pstmtVerif.setString(2, numtipato);

                try (ResultSet rs = pstmtVerif.executeQuery()) {
                    if (rs.next() && rs.getInt("count") > 0) {
                        System.out.println("⚠️ Já existe registro em selados para IDAP=" + idapTruncado +
                                " e NUMTIPATO=" + numtipato);
                        return;
                    }
                }
            }

            String sql = "INSERT INTO selados (" +
                    "NUMTIPATO, CHAVE, SELO, QRCODE, REGISTRO, DATAENVIO, IDAP, JSON" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, numtipato);
                pstmt.setString(2, numeroSelo);
                pstmt.setString(3, numeroSelo);
                pstmt.setString(4, qrcodeUrl);
                pstmt.setString(5, protocolo != null ? protocolo : "");
                pstmt.setString(6, dataEnvio);
                pstmt.setString(7, idap);
                pstmt.setString(8, jsonFunarpen);

                pstmt.executeUpdate();
                System.out.println("✅ Inserido na tabela selados: " + idapTruncado + " - " + numtipato);
            }

        } catch (Exception e) {
            System.out.println("⚠️ Não foi possível inserir na tabela selados: " + e.getMessage());
        }
    }

    /**
     * Busca dados do selo
     */
    private Map<String, Object> buscarDadosSelo(Connection conn, String numeroSelo) throws SQLException {
        Map<String, Object> dados = new HashMap<>();

        String sql = "SELECT selo_sel, numpedido, tipo_sel, tiposelo_sel, " +
                "chave1_sel, chave2_sel, dataselo_sel, codtipoato, " +
                "nome, cpfcnpj, protocolo, datahora, chavedigital, IDAP " +
                "FROM selos WHERE selo_sel = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, numeroSelo);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object value = rs.getObject(i);
                        dados.put(columnName, value);
                    }
                }
            }
        }

        return dados;
    }

    /**
     * Formata DATAENVIO
     */
    private String formatarDataEnvio(String dataAto) {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

            if (dataAto != null && !dataAto.trim().isEmpty()) {
                String dataAtoFormatada = extrairDataAto(dataAto);
                SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss.SSS");
                String horaAtual = horaFormat.format(new java.util.Date());
                return dataAtoFormatada + "T" + horaAtual;
            } else {
                return isoFormat.format(new java.util.Date());
            }

        } catch (Exception e) {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            return isoFormat.format(new java.util.Date());
        }
    }

    /**
     * Extrai data
     */
    private String extrairDataAto(String dataAtoParam) {
        if (dataAtoParam == null || dataAtoParam.trim().isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(new java.util.Date());
        }

        try {
            if (dataAtoParam.contains("T")) {
                return dataAtoParam.substring(0, 10);
            } else if (dataAtoParam.contains("-")) {
                return dataAtoParam.substring(0, 10);
            } else if (dataAtoParam.matches("\\d{8}")) {
                return dataAtoParam.substring(0, 4) + "-" +
                        dataAtoParam.substring(4, 6) + "-" +
                        dataAtoParam.substring(6, 8);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.format(new java.util.Date());
            }
        } catch (Exception e) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(new java.util.Date());
        }
    }

    // ============================================
    // MÉTODOS LEGADOS (mantidos para compatibilidade)
    // ============================================

    public Map<String, Object> processarSelagemIndividual(
            String numApo1, String numApo2, String tipoSelo, String usuario) throws Exception {

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("sucesso", false);
        resultado.put("timestamp", new java.util.Date());
        resultado.put("usuario", usuario);

        try {
            Map<String, String> intervalo = new HashMap<>();
            intervalo.put("anoMesInicial", numApo1);
            intervalo.put("aponInicial", numApo2);
            intervalo.put("anoMesFinal", numApo1);
            intervalo.put("aponFinal", numApo2);

            Map<String, Object> resultadoCompleto = processarSelagemCompleta(
                    new java.util.Date().toString(), "Apontamento", intervalo, usuario, null);

            if (Boolean.TRUE.equals(resultadoCompleto.get("sucesso"))) {
                resultado.put("sucesso", true);
                resultado.put("mensagem", "Selagem realizada com sucesso");
            } else {
                resultado.put("mensagem", resultadoCompleto.get("mensagem"));
            }

        } catch (Exception e) {
            resultado.put("mensagem", "Erro na selagem: " + e.getMessage());
        }

        return resultado;
    }

    public Map<String, Object> processarSelagemEmLote(
            List<Map<String, Object>> apontamentos, String tipoSelo, String usuario) throws Exception {

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("sucesso", false);
        resultado.put("timestamp", new java.util.Date());
        resultado.put("usuario", usuario);

        resultado.put("mensagem", "Use o método processarSelagemCompleta");

        return resultado;
    }

    /**
     * Obtém situação específica por tipo de operação
     */
    private String obterSituacaoPorTipo(String tipoOperacaoLabel) {
        String tipoNormalizado = normalizarTipoOperacao(tipoOperacaoLabel);

        switch (tipoNormalizado) {
            case "Apontamento":
                return "05";
            case "Intimação/Digitalização":
                return null; // Usa múltiplas situações (15, 16, 18)
            case "Retirada/Baixa":
                return "03";
            case "Pagamento/Baixa":
                return "04";
            case "Instrumento Diferido":
            case "Protesto com Custas Antecipadas":
                return "01";
            case "Devolução por Irregularidade":
                return "30";
            case "Sustação Definitiva":
                return "12";
            case "Sustação do Protesto":
            case "Revogação de Sustação":
            case "Suspensão de Protesto":
            case "Revogação de Suspenso":
            case "Segunda Revogação":
                return null; // Situação não específica
            default:
                return null;
        }
    }
}