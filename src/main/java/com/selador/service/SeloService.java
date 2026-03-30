package com.selador.service;

import com.selador.dao.ConnectionFactory;
import com.selador.util.TipoOperacaoMapeamento;
import com.selador.util.TipoOperacaoMapeamento.MapeamentoTipo;
import com.selador.enums.FormatoData;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service para cálculo e gerenciamento de selos - VERSÃO CORRIGIDA COM EXPANSÃO
 * DE INTERVALO E LÓGICA DE SELOS INDEPENDENTES
 */
public class SeloService {

    private static final Logger logger = Logger.getLogger(SeloService.class.getName());
    private static SeloService instance;

    private SeloService() {
    }

    public static synchronized SeloService getInstance() {
        if (instance == null) {
            instance = new SeloService();
        }
        return instance;
    }

    /**
     * 🔥 NOVO MÉTODO: Verificar se precisa expandir intervalo de numapo1_001
     */
    private boolean precisaExpandirIntervaloNumApo1(String tipoNormalizado) {
        // 🔥 Apenas para Pagamento/Baixa e Retirada/Baixa
        return "Pagamento/Baixa".equals(tipoNormalizado) ||
                "Retirada/Baixa".equals(tipoNormalizado);
    }

    /**
     * 🔥 NOVO MÉTODO: Determinar intervalo expandido de numapo1_001
     * Busca o mínimo e máximo de numapo1_001 para a data específica
     */
    private Map<String, String> determinarIntervaloExpandidoNumApo1(
            Connection conn, String tipoNormalizado, String dataParaConsulta, String colunaData) throws SQLException {

        logger.info("🔍 [INTERVALO EXPANDIDO] Determinando para " + tipoNormalizado);
        logger.info("   Data: " + dataParaConsulta);
        logger.info("   Coluna: " + colunaData);

        // Buscar o intervalo REAL de numapo1_001 para esta data
        String sql = "SELECT MIN(numapo1_001) as min_ano_mes, " +
                "MAX(numapo1_001) as max_ano_mes " +
                "FROM ctp001 " +
                "WHERE controle_001 = '01' " +
                "AND " + colunaData + " = ? ";

        // Adicionar condições específicas
        if ("Pagamento/Baixa".equals(tipoNormalizado)) {
            sql += "AND situacao_001 = '04' ";
        } else if ("Retirada/Baixa".equals(tipoNormalizado)) {
            sql += "AND situacao_001 = '03' ";
        }

        Map<String, String> intervalo = new HashMap<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dataParaConsulta);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String minAnoMes = rs.getString("min_ano_mes");
                    String maxAnoMes = rs.getString("max_ano_mes");

                    if (minAnoMes != null && maxAnoMes != null) {
                        intervalo.put("anoMesInicial", minAnoMes);
                        intervalo.put("anoMesFinal", maxAnoMes);

                        logger.info("✅ Intervalo encontrado: " + minAnoMes + " a " + maxAnoMes);
                        return intervalo;
                    }
                }
            }
        } catch (SQLException e) {
            logger.warning("⚠️ Erro ao buscar intervalo expandido: " + e.getMessage());
        }

        // Se não encontrou, retornar null para usar o intervalo original
        logger.info("⚠️ Não foi possível determinar intervalo expandido");
        return null;
    }

    /**
     * CALCULA SELOS NECESSÁRIOS - VERSÃO CORRIGIDA COM LÓGICA DE SELOS INDEPENDENTES
     */
    public Map<String, Object> calcularSelosNecessarios(String dataFormatada, String tipoFrontend) {

        String tipoNormalizado = normalizarTipoSimples(tipoFrontend);

        logger.info("═══════════════════════════════════════════════════");
        logger.info("🧮 [CALCULO CORRIGIDO COM SELOS INDEPENDENTES] Iniciando cálculo");
        logger.info("   Tipo recebido: '" + tipoFrontend + "'");
        logger.info("   Tipo normalizado: '" + tipoNormalizado + "'");
        logger.info("   Data: " + dataFormatada);

        // 🔥 Verificar se precisa expandir
        boolean precisaExpandir = precisaExpandirIntervaloNumApo1(tipoNormalizado);
        if (precisaExpandir) {
            logger.info("   ⚠️ Este tipo requer expansão de intervalo numapo1_001");
        }

        logger.info("═══════════════════════════════════════════════════");

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("success", false);
        resultado.put("data", dataFormatada);
        resultado.put("tipo", tipoNormalizado);

        Connection conn = null;

        try {
            conn = ConnectionFactory.getConnection();

            MapeamentoTipo mapeamento = buscarMapeamentoCorrigido(tipoNormalizado);

            if (mapeamento == null) {
                resultado.put("mensagem", "Tipo de operação não reconhecido: " + tipoNormalizado);
                resultado.put("success", false);
                logger.warning("Tipo de operação não reconhecido: " + tipoNormalizado);
                return resultado;
            }

            String codigoOperacao = mapeamento.getCodigo();

            boolean usaFormatoSemZero = mapeamento.isUsaFormatoDDMMYYYYSemZero();

            if ("Intimação/Digitalização".equals(tipoNormalizado)) {
                usaFormatoSemZero = false;
                logger.info("🔧 [CORREÇÃO] Intimação/Digitalização: Forçando YYYYMMDD");
            }

            logger.info("🔧 Configurações do mapeamento:");
            logger.info("   usaFormatoSemZero: " + usaFormatoSemZero);
            logger.info("   Coluna Data: " + mapeamento.getColunaData());
            logger.info("   Formato: " + mapeamento.getFormatoData());

            String dataParaConsulta = FormatoData.converterParaFormatoBanco(dataFormatada, usaFormatoSemZero);
            logger.info("📅 Data convertida: '" + dataParaConsulta + "'");

            Map<String, String> intervalo = buscarIntervaloApontamentosCorrigido(conn, dataParaConsulta, codigoOperacao,
                    tipoNormalizado);

            if (intervalo == null || intervalo.get("anoMesInicial") == null) {
                resultado.put("mensagem", "Nenhum registro encontrado para a data informada");
                resultado.put("total_registros", 0);
                resultado.put("selos", new HashMap<String, Integer>());
                resultado.put("success", true);
                logger.info("Nenhum registro encontrado para a data informada");
                return resultado;
            }

            String anoMesInicial = intervalo.get("anoMesInicial");
            String anoMesFinal = intervalo.get("anoMesFinal");
            String aponInicial = intervalo.get("aponInicial");
            String aponFinal = intervalo.get("aponFinal");

            logger.info("📊 Intervalo encontrado:");
            logger.info("   AnoMes: " + anoMesInicial + " a " + anoMesFinal);
            logger.info("   Apontamentos: " + aponInicial + " a " + aponFinal);

            Map<String, Integer> selos = new HashMap<>();
            int totalRegistros = 0;

            int RVLRDIG_MES = getRVLRDIGMES(conn);
            logger.info("📊 RVLRDIG_MES: " + RVLRDIG_MES);

            // 🔥 LÓGICA CORRIGIDA: Usar cálculo combinado para tipos complexos
            if ("Intimação/Digitalização".equals(tipoNormalizado) || 
                "Retirada/Baixa".equals(tipoNormalizado) || 
                "Pagamento/Baixa".equals(tipoNormalizado)) {
                
                // 🔥 USAR LÓGICA COMBINADA para estes tipos
                Map<String, Integer> selosNecessarios = calcularSelosNecessariosCombinados(
                    conn, anoMesInicial, anoMesFinal, aponInicial, aponFinal, 
                    dataParaConsulta, tipoNormalizado, codigoOperacao, RVLRDIG_MES
                );
                
                if ("Intimação/Digitalização".equals(tipoNormalizado)) {
                    calcularSelosIntimacaoFinal(selos, selosNecessarios);
                } else {
                    calcularSelosPorTipoSimplificado(tipoNormalizado, selos, selosNecessarios);
                }
                
                // Contar total de registros
                totalRegistros = contarTotalRegistrosParaTipo(conn, anoMesInicial, anoMesFinal,
                                                             aponInicial, aponFinal, dataParaConsulta,
                                                             tipoNormalizado, codigoOperacao);

            } else {
                // Para outros tipos (mais simples), usar lógica antiga
                int registrosNaoSelados = contarRegistrosNaoSeladosPorTipoSimples(conn, anoMesInicial, anoMesFinal,
                        aponInicial, aponFinal, dataParaConsulta, codigoOperacao, tipoNormalizado);
                
                calcularSelosPorTipoSimples(tipoNormalizado, selos, registrosNaoSelados, RVLRDIG_MES);
                totalRegistros = registrosNaoSelados;
            }

            resultado.put("success", true);
            resultado.put("total_registros", totalRegistros);
            resultado.put("selos", selos);
            resultado.put("intervalo", intervalo);
            resultado.put("RVLRDIG_MES", RVLRDIG_MES);
            resultado.put("mensagem", "Cálculo concluído: " + totalRegistros + " registros encontrados");

            logger.info("📊 RESULTADO FINAL:");
            logger.info("   Total Registros: " + totalRegistros);
            logger.info("   Selos Necessários: " + selos);

        } catch (Exception e) {
            logger.severe("❌ ERRO no cálculo: " + e.getMessage());
            e.printStackTrace();
            resultado.put("mensagem", "Erro: " + e.getMessage());
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                logger.warning("Erro ao fechar conexão: " + e.getMessage());
            }
        }

        return resultado;
    }

    /**
     * 🔥 MÉTODO COMBINADO CORRIGIDO: Calcular selos necessários considerando combinações
     * Para Retirada/Baixa, Pagamento/Baixa, Intimação/Digitalização
     */
    private Map<String, Integer> calcularSelosNecessariosCombinados(Connection conn, String anoMesInicial, String anoMesFinal,
                                                                String aponInicial, String aponFinal, String data,
                                                                String tipo, String codigoOperacao, int RVLRDIG_MES) throws SQLException {
        
        logger.info("🎯 [CALCULO COMBINADO CORRIGIDO] Tipo: " + tipo);
        logger.info("📊 Parâmetros:");
        logger.info("   anoMesInicial: " + anoMesInicial);
        logger.info("   aponInicial: " + aponInicial + " - " + aponFinal);
        logger.info("   data: " + data);
        logger.info("   RVLRDIG_MES: " + RVLRDIG_MES);
        
        if ("Intimação/Digitalização".equals(tipo)) {
            return calcularSelosIntimacaoCombinado(conn, anoMesInicial, anoMesFinal, aponInicial, aponFinal, 
                                                data, RVLRDIG_MES);
        }
        
        // Para Retirada/Baixa e Pagamento/Baixa
        // 1. Primeiro, obter TODOS os registros que atendem aos critérios
        List<RegistroBasico> registros = new ArrayList<>();
        
        MapeamentoTipo mapeamento = buscarMapeamentoCorrigido(tipo);
        String colunaData = mapeamento != null ? mapeamento.getColunaData() : "dataapo_001";
        String situacao = obterSituacaoPorTipo(tipo, codigoOperacao);
        
        String sqlRegistros = "SELECT idap_001, numapo2_001 FROM ctp001 " +
                            "WHERE controle_001 = '01' " +
                            "AND numapo1_001 = ? " +
                            "AND CAST(numapo2_001 AS UNSIGNED) BETWEEN ? AND ? " +
                            "AND " + colunaData + " = ? ";
        
        if (situacao != null) {
            sqlRegistros += "AND situacao_001 = ? ";
        }
        
        logger.info("🔍 SQL buscar registros:");
        logger.info(sqlRegistros);
        
        try (PreparedStatement pstmt = conn.prepareStatement(sqlRegistros)) {
            pstmt.setString(1, anoMesInicial);
            pstmt.setInt(2, Integer.parseInt(aponInicial));
            pstmt.setInt(3, Integer.parseInt(aponFinal));
            pstmt.setString(4, data);
            
            if (situacao != null) {
                pstmt.setString(5, situacao);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RegistroBasico reg = new RegistroBasico();
                    reg.idap = rs.getString("idap_001");
                    reg.numapo2 = rs.getString("numapo2_001");
                    registros.add(reg);
                }
            }
        }
        
        logger.info("📊 Total registros encontrados: " + registros.size());
        
        // 2. Determinar quais NUMTIPATOs verificar baseado no tipo
        List<String> numtipatosParaVerificar = new ArrayList<>();
        
        if ("Retirada/Baixa".equals(tipo)) {
            numtipatosParaVerificar.add("705");
            numtipatosParaVerificar.add("735");
            if (RVLRDIG_MES == 1) {
                numtipatosParaVerificar.add("736");
            }
        } else if ("Pagamento/Baixa".equals(tipo)) {
            numtipatosParaVerificar.add("704");
            numtipatosParaVerificar.add("735");
            if (RVLRDIG_MES == 1) {
                numtipatosParaVerificar.add("736");
            }
        } else {
            numtipatosParaVerificar.add(codigoOperacao);
        }
        
        logger.info("📋 NUMTIPATOs para verificar: " + numtipatosParaVerificar);
        
        // 🔥 LOG DIAGNÓSTICO DETALHADO
        logger.info("🔍 DIAGNÓSTICO DETALHADO:");
        for (RegistroBasico reg : registros) {
            String idapTruncado = reg.idap.length() > 20 ? reg.idap.substring(0, 20) : reg.idap;
            logger.info("   Registro " + reg.numapo2 + ":");
            logger.info("     IDAP completo: " + reg.idap);
            logger.info("     IDAP truncado (20 chars): " + idapTruncado);
        }
        
        // 3. Para CADA NUMTIPATO, contar quantos registros não o têm
        Map<String, Integer> contagens = new HashMap<>();
        for (String numtipato : numtipatosParaVerificar) {
            int contagem = 0;
            
            for (RegistroBasico reg : registros) {
                if (!temSelo(conn, reg.idap, numtipato)) {
                    contagem++;
                    logger.info("❌ Registro " + reg.numapo2 + " NÃO TEM selo " + numtipato);
                } else {
                    logger.info("✅ Registro " + reg.numapo2 + " JÁ TEM selo " + numtipato);
                }
            }
            
            contagens.put(numtipato, contagem);
            logger.info("📊 " + numtipato + ": " + contagem + " registros PRECISAM");
        }
        
        return contagens;
    }

    /**
     * 🔥 MÉTODO CORRIGIDO: Verificar se um registro tem um selo específico
     * Usando LEFT(IDAP, 20) para comparação
     */
    private boolean temSelo(Connection conn, String idap, String numtipato) throws SQLException {
        if (idap == null || idap.trim().isEmpty()) {
            logger.warning("⚠️ IDAP nulo ou vazio para verificar selo " + numtipato);
            return false;
        }
        
        // Pegar apenas os primeiros 20 caracteres do IDAP
        String idapTruncado = idap;
        if (idap.length() > 20) {
            idapTruncado = idap.substring(0, 20);
        }
        
        String sql = "SELECT COUNT(*) as count FROM selados WHERE LEFT(IDAP, 20) = ? AND NUMTIPATO = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idapTruncado);
            pstmt.setString(2, numtipato);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    boolean tem = count > 0;
                    logger.info("🔍 Verificação selo: LEFT(IDAP,20)='" + idapTruncado + 
                            "', NUMTIPATO=" + numtipato + " -> " + (tem ? "✅ TEM" : "❌ NÃO TEM"));
                    return tem;
                }
            }
        } catch (SQLException e) {
            logger.warning("⚠️ Erro ao verificar selo " + numtipato + ": " + e.getMessage());
            return false;
        }
        
        return false;
    }

    /**
     * 🔥 MÉTODO INTIMAÇÃO CORRIGIDO: Calcular selos necessários para Intimação/Digitalização
     * Considera: especie_001, RVLRDIG_MES e selos já existentes
     * Usando LEFT(..., 20) no JOIN
     */
    private Map<String, Integer> calcularSelosIntimacaoCombinado(Connection conn, String anoMesInicial, String anoMesFinal,
                                                                String aponInicial, String aponFinal, String data,
                                                                int RVLRDIG_MES) throws SQLException {
        
        logger.info("🎯 [INTIMAÇÃO COMBINADO CORRIGIDO] Calculando selos");
        
        // 1. Primeiro, obter TODOS os registros que atendem aos critérios BÁSICOS
        List<IntimacaoRegistro> registros = new ArrayList<>();
        
        String sqlRegistros = "SELECT idap_001, numapo2_001, especie_001 FROM ctp001 " +
                            "WHERE controle_001 = '01' " +
                            "AND numapo1_001 = ? " +
                            "AND CAST(numapo2_001 AS UNSIGNED) BETWEEN ? AND ? " +
                            "AND dataapo_001 = ? " +
                            "AND situacao_001 IN ('05', '15', '16', '18')";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sqlRegistros)) {
            pstmt.setString(1, anoMesInicial);
            pstmt.setInt(2, Integer.parseInt(aponInicial));
            pstmt.setInt(3, Integer.parseInt(aponFinal));
            pstmt.setString(4, data);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    IntimacaoRegistro reg = new IntimacaoRegistro();
                    reg.idap = rs.getString("idap_001");
                    reg.numapo2 = rs.getString("numapo2_001");
                    reg.especie = rs.getString("especie_001");
                    registros.add(reg);
                }
            }
        }
        
        logger.info("📊 Total registros encontrados: " + registros.size());
        
        // 2. Para CADA registro, determinar quais selos são necessários E verificar se já existem
        int contagem702 = 0;
        int contagem703 = 0;
        int contagem725 = 0;
        int contagem738 = 0;
        int contagem740 = 0;
        
        for (IntimacaoRegistro reg : registros) {
            // Parsear espécie
            String especie = reg.especie;
            if (especie != null && especie.length() > 3) {
                especie = especie.substring(especie.length() - 3);
            }
            
            boolean especieMenor090 = false;
            boolean especieMaior089 = false;
            
            try {
                int especieNum = Integer.parseInt(especie);
                especieMenor090 = especieNum < 90;
                especieMaior089 = especieNum > 89;
            } catch (NumberFormatException e) {
                logger.warning("⚠️ Erro ao parsear espécie: " + especie);
            }
            
            logger.info("🔍 Registro " + reg.numapo2 + " - Espécie: " + reg.especie + 
                    " -> <090: " + especieMenor090 + ", >089: " + especieMaior089);
            
            // Determinar quais selos são NECESSÁRIOS para este registro
            List<String> selosNecessarios = new ArrayList<>();
            
            // 703 -> SEMPRE necessário para TODOS
            selosNecessarios.add("703");
            
            if (especieMenor090) {
                selosNecessarios.add("702");
                if (RVLRDIG_MES == 1) {
                    selosNecessarios.add("725");
                }
            }
            
            if (especieMaior089) {
                selosNecessarios.add("738");
                if (RVLRDIG_MES == 1) {
                    selosNecessarios.add("740");
                }
            }
            
            logger.info("   Selos necessários: " + selosNecessarios);
            
            // Verificar quais desses selos já existem (usando LEFT 20)
            for (String numtipato : selosNecessarios) {
                boolean jaExiste = temSelo(conn, reg.idap, numtipato);
                logger.info("   " + numtipato + ": " + (jaExiste ? "JÁ TEM" : "PRECISA"));
                
                if (!jaExiste) {
                    switch (numtipato) {
                        case "702": contagem702++; break;
                        case "703": contagem703++; break;
                        case "725": contagem725++; break;
                        case "738": contagem738++; break;
                        case "740": contagem740++; break;
                    }
                }
            }
        }
        
        // 3. Montar resultado
        Map<String, Integer> resultado = new HashMap<>();
        if (contagem702 > 0) resultado.put("702", contagem702);
        if (contagem703 > 0) resultado.put("703", contagem703);
        if (contagem725 > 0) resultado.put("725", contagem725);
        if (contagem738 > 0) resultado.put("738", contagem738);
        if (contagem740 > 0) resultado.put("740", contagem740);
        
        int tpdTotal = contagem738 + contagem740;
        if (tpdTotal > 0) {
            resultado.put("TPD_TOTAL", tpdTotal);
        }
        
        logger.info("📊 RESUMO Intimação:");
        logger.info("   702 (TP1): " + contagem702);
        logger.info("   703 (TPI): " + contagem703);
        logger.info("   725 (TP3): " + contagem725);
        logger.info("   738 (TPD): " + contagem738);
        logger.info("   740 (TPD): " + contagem740);
        logger.info("   TPD TOTAL: " + tpdTotal);
        
        return resultado;
    }

    /**
     * Método para calcular selos finais de Intimação/Digitalização
     */
    private void calcularSelosIntimacaoFinal(Map<String, Integer> selos, Map<String, Integer> selosNecessarios) {
        
        logger.info("📊 [INTIMAÇÃO FINAL] Convertendo para siglas: " + selosNecessarios);
        
        // Converter números para siglas
        if (selosNecessarios.containsKey("702")) {
            selos.put("TP1", selosNecessarios.get("702"));
        }
        if (selosNecessarios.containsKey("703")) {
            selos.put("TPI", selosNecessarios.get("703"));
        }
        if (selosNecessarios.containsKey("725")) {
            selos.put("TP3", selosNecessarios.get("725"));
        }
        
        // TPD pode vir de 738, 740 ou TPD_TOTAL
        int tpdTotal = 0;
        if (selosNecessarios.containsKey("738")) {
            tpdTotal += selosNecessarios.get("738");
        }
        if (selosNecessarios.containsKey("740")) {
            tpdTotal += selosNecessarios.get("740");
        }
        if (selosNecessarios.containsKey("TPD_TOTAL")) {
            tpdTotal = selosNecessarios.get("TPD_TOTAL");
        }
        
        if (tpdTotal > 0) {
            selos.put("TPD", tpdTotal);
        }
        
        logger.info("✅ Selos finais Intimação: " + selos);
    }

    /**
     * Método simplificado para calcularSelosPorTipo (para tipos combinados)
     */
    private void calcularSelosPorTipoSimplificado(String tipo, Map<String, Integer> selos, 
                                             Map<String, Integer> selosNecessarios) {
        
        logger.info("📊 [SIMPLIFICADO] Calculando selos para: " + tipo);
        logger.info("📊 Selos necessários: " + selosNecessarios);
        
        // Converter números para siglas
        if (selosNecessarios.containsKey("705")) {
            selos.put("TP4", selosNecessarios.get("705"));
        }
        if (selosNecessarios.containsKey("704")) {
            selos.put("TP4", selosNecessarios.get("704"));
        }
        if (selosNecessarios.containsKey("735")) {
            selos.put("TP1", selosNecessarios.get("735"));
        }
        if (selosNecessarios.containsKey("736")) {
            selos.put("TP3", selosNecessarios.get("736"));
        }
        
        logger.info("✅ Selos finais: " + selos);
    }

    /**
     * 🔥 MÉTODO SIMPLES CORRIGIDO: Contar registros não selados (para tipos não combinados)
     * Usando LEFT(..., 20) no JOIN
     */
    private int contarRegistrosNaoSeladosPorTipoSimples(Connection conn, String anoMesInicial, String anoMesFinal,
                                                            String aponInicial, String aponFinal, String data,
                                                            String codigoOperacao, String tipo) throws SQLException {
        
        logger.info("🔍 [CONTAGEM SIMPLES CORRIGIDA] Contando para: " + tipo);
        logger.info("   Parâmetros: anoMes=" + anoMesInicial + ", apon=" + aponInicial + "-" + aponFinal + 
                ", data=" + data + ", codigo=" + codigoOperacao);
        
        MapeamentoTipo mapeamento = buscarMapeamentoCorrigido(tipo);
        if (mapeamento == null) {
            logger.warning("❌ Mapeamento não encontrado para tipo: " + tipo);
            return 0;
        }
        
        String colunaData = mapeamento.getColunaData();
        logger.info("   Coluna data: " + colunaData);
        
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        
        // 🔥 CORREÇÃO: Usar LEFT(..., 20) no JOIN
        sql.append("SELECT COUNT(DISTINCT c.numapo2_001) as total ")
        .append("FROM ctp001 c ")
        .append("LEFT JOIN selados s ON LEFT(c.idap_001, 20) = LEFT(s.IDAP, 20) AND s.NUMTIPATO = ? ")
        .append("WHERE c.controle_001 = '01' ")
        .append("AND c.numapo1_001 = ? ")
        .append("AND CAST(c.numapo2_001 AS UNSIGNED) BETWEEN ? AND ? ")
        .append("AND ").append(colunaData).append(" = ? ")
        .append("AND s.IDAP IS NULL ");
        
        params.add(codigoOperacao);
        params.add(anoMesInicial);
        params.add(Integer.parseInt(aponInicial));
        params.add(Integer.parseInt(aponFinal));
        params.add(data);
        
        // Adicionar situação se necessário
        String situacao = obterSituacaoPorTipo(tipo, codigoOperacao);
        if (situacao != null) {
            sql.append("AND c.situacao_001 = ? ");
            params.add(situacao);
            logger.info("   Situação: " + situacao);
        }
        
        // Condições especiais por tipo
        String label = mapeamento.getLabelFrontend();
        if ("Instrumento Diferido".equals(label)) {
            sql.append("AND c.especie_001 > '089' ");
            logger.info("   Condição: especie_001 > '089'");
        } else if ("Protesto com Custas Antecipadas".equals(label)) {
            sql.append("AND c.especie_001 < '090' ");
            logger.info("   Condição: especie_001 < '090'");
        }
        
        logger.info("📝 SQL corrigido:");
        logger.info(sql.toString());
        logger.info("🔢 Parâmetros: " + params);
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                int total = rs.next() ? rs.getInt("total") : 0;
                logger.info("📊 Resultado: " + total + " registros não selados");
                return total;
            }
        }
    }

    /**
     * Método simples para calcular selos por tipo (para tipos não combinados)
     */
    private void calcularSelosPorTipoSimples(String tipo, Map<String, Integer> selos, int totalRegistros, int RVLRDIG_MES) {
        
        logger.info("📊 [SIMPLES] Calculando para: " + tipo + " (Registros: " + totalRegistros + ")");
        
        String tipoUpper = tipo.toUpperCase();
        
        if (tipoUpper.contains("INSTRUMENTO DIFERIDO") || "739".equals(tipoUpper)) {
            selos.put("TPD", totalRegistros);
        } else if (tipoUpper.contains("PROTESTO COM CUSTAS ANTECIPADAS") || "706".equals(tipoUpper)) {
            selos.put("TP4", totalRegistros);
        } else if (tipoUpper.contains("APONTAMENTO") || "701".equals(tipoUpper)) {
            selos.put("TPI", totalRegistros);
        } else if (tipoUpper.contains("DEVOLU") || "729".equals(tipoUpper)) {
            selos.put("TPI", totalRegistros);
        } else if (tipoUpper.contains("SUSTA") && tipoUpper.contains("DEFINITIVA")) {
            selos.put("TPI", totalRegistros);
        } else if (tipoUpper.contains("REVOGA") && tipoUpper.contains("SUSTA")) {
            selos.put("TPI", totalRegistros);
        } else if (tipoUpper.contains("SUSTA")) {
            selos.put("TPI", totalRegistros);
        } else if (tipoUpper.contains("SEGUNDA") && tipoUpper.contains("REVOGA")) {
            selos.put("TPI", totalRegistros);
        } else if (tipoUpper.contains("REVOGA") && tipoUpper.contains("SUSPENSO")) {
            selos.put("TPI", totalRegistros);
        } else if (tipoUpper.contains("SUSPEN")) {
            selos.put("TPI", totalRegistros);
        } else {
            selos.put("TPI", totalRegistros);
            logger.warning("⚠️ Tipo não mapeado, usando TPI=" + totalRegistros);
        }
    }

    /**
     * 🔥 MÉTODO COMPLETO: Contar todos os registros que atendem aos critérios
     */
    private int contarTotalRegistrosParaTipo(Connection conn, String anoMesInicial, String anoMesFinal,
            String aponInicial, String aponFinal, String data,
            String tipo, String codigoOperacao) throws SQLException {

        logger.info("🔍 [TOTAL REGISTROS] Contando para: " + tipo);

        MapeamentoTipo mapeamento = buscarMapeamentoCorrigido(tipo);
        if (mapeamento == null) return 0;

        String colunaData = mapeamento.getColunaData();

        // Verificar se precisa expandir intervalo
        boolean precisaExpandir = precisaExpandirIntervaloNumApo1(tipo);
        Map<String, String> intervaloExpandido = null;
        if (precisaExpandir) {
            intervaloExpandido = determinarIntervaloExpandidoNumApo1(conn, tipo, data, colunaData);
        }

        if (intervaloExpandido != null) {
            anoMesInicial = intervaloExpandido.get("anoMesInicial");
            anoMesFinal = intervaloExpandido.get("anoMesFinal");
        }

        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT COUNT(DISTINCT numapo2_001) as total ")
                .append("FROM ctp001 ")
                .append("WHERE controle_001 = '01' ");

        if (anoMesInicial.equals(anoMesFinal)) {
            sql.append("AND numapo1_001 = ? ");
            params.add(anoMesInicial);
        } else {
            sql.append("AND numapo1_001 BETWEEN ? AND ? ");
            params.add(anoMesInicial);
            params.add(anoMesFinal);
        }

        sql.append("AND CAST(numapo2_001 AS UNSIGNED) BETWEEN ? AND ? ")
                .append("AND ").append(colunaData).append(" = ? ");

        params.add(Integer.parseInt(aponInicial));
        params.add(Integer.parseInt(aponFinal));
        params.add(data);

        String situacao = obterSituacaoPorTipo(tipo, codigoOperacao);
        if (situacao != null) {
            sql.append("AND situacao_001 = ? ");
            params.add(situacao);
        }

        String label = mapeamento.getLabelFrontend();
        if ("Instrumento Diferido".equals(label)) {
            sql.append("AND especie_001 > '089' ");
        } else if ("Protesto com Custas Antecipadas".equals(label)) {
            sql.append("AND especie_001 < '090' ");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt("total") : 0;
            }
        }
    }

    /**
     * 🔥 BUSCAR INTERVALO CORRIGIDO COMPLETO COM EXPANSÃO DE NUMApo1
     */
    private Map<String, String> buscarIntervaloApontamentosCorrigido(Connection conn, String dataParaConsulta,
            String codigoOperacao, String tipoNormalizado) throws SQLException {

        logger.info("🔍 [INTERVALO CORRIGIDO COMPLETO] Buscando para: " + tipoNormalizado);

        MapeamentoTipo mapeamento = buscarMapeamentoCorrigido(tipoNormalizado);

        if (mapeamento == null) {
            logger.warning("❌ Mapeamento não encontrado!");
            return null;
        }

        String colunaData = mapeamento.getColunaData();

        // 🔥 Verificar se precisa expandir intervalo de numapo1_001
        boolean precisaExpandir = precisaExpandirIntervaloNumApo1(tipoNormalizado);

        Map<String, String> intervaloExpandido = null;
        if (precisaExpandir) {
            intervaloExpandido = determinarIntervaloExpandidoNumApo1(conn, tipoNormalizado, dataParaConsulta,
                    colunaData);
        }

        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT MIN(numapo1_001) as NumApo1Ini, MAX(numapo1_001) as NumApo1Fim, ")
                .append("MIN(CAST(numapo2_001 AS UNSIGNED)) as Inicial, ")
                .append("MAX(CAST(numapo2_001 AS UNSIGNED)) as Final ")
                .append("FROM ctp001 WHERE controle_001 = '01' ");

        sql.append("AND ").append(colunaData).append(" = ?");
        params.add(dataParaConsulta);

        // 🔥 ADICIONAR CONDIÇÃO DE SITUAÇÃO
        String situacao = obterSituacaoPorTipo(tipoNormalizado, codigoOperacao);
        if (situacao != null) {
            sql.append(" AND situacao_001 = ?");
            params.add(situacao);
            logger.info("🔧 Adicionando condição de situação: " + situacao);
        }

        String label = mapeamento.getLabelFrontend();

        if ("739".equals(codigoOperacao) || "Instrumento Diferido".equalsIgnoreCase(label)) {
            sql.append(" AND especie_001 > '089'");
            logger.info("🔧 Adicionando condição especial: especie_001 > '089'");

        } else if ("706".equals(codigoOperacao) || "Protesto com Custas Antecipadas".equalsIgnoreCase(label)) {
            sql.append(" AND especie_001 < '090'");
            logger.info("🔧 Adicionando condição especial: especie_001 < '090'");

        }

        logger.info("📝 SQL Intervalo: " + sql.toString());
        logger.info("🔢 Parâmetros: " + params);

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, String> intervalo = new HashMap<>();

                    // 🔥 Se tem intervalo expandido, usar ele
                    if (intervaloExpandido != null) {
                        intervalo.put("anoMesInicial", intervaloExpandido.get("anoMesInicial"));
                        intervalo.put("anoMesFinal", intervaloExpandido.get("anoMesFinal"));
                        logger.info("🔥 Usando intervalo expandido de numapo1_001: " +
                                intervaloExpandido.get("anoMesInicial") + " a " +
                                intervaloExpandido.get("anoMesFinal"));
                    } else {
                        intervalo.put("anoMesInicial", rs.getString("NumApo1Ini"));
                        intervalo.put("anoMesFinal", rs.getString("NumApo1Fim"));
                    }

                    intervalo.put("aponInicial", rs.getString("Inicial"));
                    intervalo.put("aponFinal", rs.getString("Final"));

                    logger.info("✅ Intervalo encontrado:");
                    logger.info("   AnoMes: " + intervalo.get("anoMesInicial") + " a " + intervalo.get("anoMesFinal"));
                    logger.info(
                            "   Apontamentos: " + intervalo.get("aponInicial") + " a " + intervalo.get("aponFinal"));

                    return intervalo;
                }
            }
        }

        logger.warning("⚠️ Nenhum registro encontrado");
        return null;
    }

    /**
     * 🔥 OBTER SITUAÇÃO ESPECÍFICA POR TIPO
     */
    private String obterSituacaoPorTipo(String tipo, String codigoOperacao) {
        String tipoUpper = tipo.toUpperCase();

        if ("701".equals(codigoOperacao) || tipoUpper.contains("APONTAMENTO")) {
            return "05"; // Apontamento
        } else if (tipoUpper.contains("INTIMA") || tipoUpper.contains("DIGITALIZA")) {
            return null; // Intimação/Digitalização usa múltiplas situações (15,16,18)
        } else if ("704".equals(codigoOperacao) || (tipoUpper.contains("PAGAMENTO") && tipoUpper.contains("BAIXA"))) {
            return "04"; // Pagamento/Baixa
        } else if ("705".equals(codigoOperacao) || (tipoUpper.contains("RETIRADA") && tipoUpper.contains("BAIXA"))) {
            return "03"; // Retirada/Baixa
        } else if ("706".equals(codigoOperacao) || tipoUpper.contains("PROTESTO COM CUSTAS")) {
            return "01"; // Protesto com Custas Antecipadas
        } else if ("739".equals(codigoOperacao) || tipoUpper.contains("INSTRUMENTO DIFERIDO")) {
            return "01"; // Instrumento Diferido
        } else if ("729".equals(codigoOperacao) || tipoUpper.contains("DEVOLU")) {
            return "30"; // Devolução por Irregularidade
        } else if ("727".equals(codigoOperacao) || (tipoUpper.contains("SUSTA") && tipoUpper.contains("DEFINITIVA"))) {
            return "12"; // Sustação Definitiva
        }
        return null;
    }

    /**
     * Buscar o valor do parâmetro RVLRDIG_MES
     */
    private int getRVLRDIGMES(Connection conn) throws SQLException {
        String sql = "SELECT RVLRDIG_MES FROM parametros WHERE CODIGO_PAR = 1";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("RVLRDIG_MES");
            } else {
                return 0;
            }
        }
    }

    /**
     * Método CORRIGIDO para normalizar tipo
     */
    private String normalizarTipoSimples(String tipoRaw) {
        if (tipoRaw == null)
            return "Apontamento";

        String tipoCorrigido = corrigirEncoding(tipoRaw);
        String tipoUpper = tipoCorrigido.toUpperCase();

        if (tipoUpper.contains("INTIMA") || tipoUpper.contains("DIGITALIZA") ||
                tipoUpper.contains("702") || tipoUpper.contains("703") ||
                tipoUpper.contains("725") || tipoUpper.contains("738") ||
                tipoUpper.contains("740")) {
            return "Intimação/Digitalização";
        }

        if ((tipoUpper.contains("PAGAMENTO") && tipoUpper.contains("BAIXA")) ||
                tipoUpper.contains("704") || tipoUpper.contains("735") ||
                tipoUpper.contains("736")) {
            return "Pagamento/Baixa";
        }

        if ((tipoUpper.contains("RETIRADA") && tipoUpper.contains("BAIXA")) ||
                tipoUpper.contains("705") || tipoUpper.contains("735") ||
                tipoUpper.contains("736")) {
            return "Retirada/Baixa";
        }

        if (tipoUpper.contains("INSTRUMENTO") && tipoUpper.contains("DIFERIDO") ||
                tipoUpper.contains("739")) {
            return "Instrumento Diferido";
        }

        if ((tipoUpper.contains("PROTESTO") && tipoUpper.contains("CUSTAS")) ||
                tipoUpper.contains("706")) {
            return "Protesto com Custas Antecipadas";
        }

        if (tipoUpper.contains("DEVOLU") || tipoUpper.contains("729")) {
            return "Devolução por Irregularidade";
        }

        if ((tipoUpper.contains("SUSTA") && tipoUpper.contains("DEFINITIVA")) ||
                tipoUpper.contains("727")) {
            return "Sustação Definitiva";
        }

        if ((tipoUpper.contains("REVOGA") && tipoUpper.contains("SUSTA")) ||
                tipoUpper.contains("734")) {
            return "Revogação de Sustação";
        }

        if (tipoUpper.contains("SUSTA") || tipoUpper.contains("734")) {
            return "Sustação do Protesto";
        }

        if ((tipoUpper.contains("SEGUNDA") && tipoUpper.contains("REVOGA")) ||
                tipoUpper.contains("733")) {
            return "Segunda Revogação";
        }

        if ((tipoUpper.contains("REVOGA") && tipoUpper.contains("SUSPENSO")) ||
                tipoUpper.contains("733")) {
            return "Revogação de Suspenso";
        }

        if (tipoUpper.contains("SUSPEN") || tipoUpper.contains("733")) {
            return "Suspensão de Protesto";
        }

        if (tipoUpper.contains("APONTAMENTO") || tipoUpper.contains("701")) {
            return "Apontamento";
        }

        if (tipoUpper.contains("BAIXA") && !tipoUpper.contains("PAGAMENTO") && !tipoUpper.contains("RETIRADA")) {
            return "Retirada/Baixa";
        }

        return tipoRaw;
    }

    /**
     * Buscar mapeamento
     */
    private MapeamentoTipo buscarMapeamentoCorrigido(String tipoNormalizado) {
        MapeamentoTipo mapeamento = null;

        String tipoCorrigido = corrigirEncoding(tipoNormalizado);
        mapeamento = TipoOperacaoMapeamento.getPorLabel(tipoCorrigido);

        if (mapeamento != null) {
            return mapeamento;
        }

        return TipoOperacaoMapeamento.getPorLabel("Apontamento");
    }

    /**
     * Corrigir encoding corrompido
     */
    private String corrigirEncoding(String tipo) {
        if (tipo == null)
            return tipo;

        String corrigido = tipo
                .replace("Ã£", "ã")
                .replace("Ã§", "ç")
                .replace("Ã£o", "ão")
                .replace("Ã¡", "á")
                .replace("Ã³", "ó")
                .replace("Ã©", "é")
                .replace("Ã", "í");

        if (tipo.contains("DevoluÃ§Ã£o") || tipo.contains("Devolução")) {
            return "Devolução por Irregularidade";
        }
        if (tipo.contains("SustaÃ§Ã£o") || tipo.contains("Sustação")) {
            if (tipo.contains("Definitiva")) {
                return "Sustação Definitiva";
            } else if (tipo.contains("Revogação") || tipo.contains("RevogaÃ§Ã£o")) {
                return "Revogação de Sustação";
            } else {
                return "Sustação do Protesto";
            }
        }
        if (tipo.contains("SuspensÃ£o") || tipo.contains("Suspensão")) {
            if (tipo.contains("Segunda")) {
                return "Segunda Revogação";
            } else if (tipo.contains("Revogação") || tipo.contains("RevogaÃ§Ã£o")) {
                return "Revogação de Suspenso";
            } else {
                return "Suspensão de Protesto";
            }
        }

        return corrigido;
    }

    // ============================================
    // CLASSES AUXILIARES
    // ============================================

    /**
     * Classe auxiliar para registro de intimação
     */
    private class IntimacaoRegistro {
        public String idap;
        public String numapo2;
        public String especie;
    }

    /**
     * Classe auxiliar para registro básico
     */
    private class RegistroBasico {
        public String idap;
        public String numapo2;
    }

    // ============================================
    // MÉTODOS PARA SELOS DISPONÍVEIS (mantidos)
    // ============================================

    public Map<String, Object> contarSelosDisponiveisComSiglas() {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("success", false);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();

            String sql = "SELECT "
                    + "selos.tiposelo_sel, "
                    + "COUNT(*) AS quantidade "
                    + "FROM selos "
                    + "WHERE (selos.tiposelo_sel IN ('0001', '0003', '0004', '0009', '0010')) "
                    + "AND (selos.tipo_sel = '0000') "
                    + "AND (selos.dataselo_sel IS NULL) "
                    + "GROUP BY selos.tiposelo_sel "
                    + "ORDER BY selos.tiposelo_sel";

            logger.info("🔍 [SELOS] Executando SQL de contagem:");
            logger.info(sql);

            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            Map<String, Integer> contagem = new HashMap<>();
            contagem.put("0001", 0);
            contagem.put("0003", 0);
            contagem.put("0004", 0);
            contagem.put("0009", 0);
            contagem.put("0010", 0);

            while (rs.next()) {
                String tipoBanco = rs.getString("tiposelo_sel");
                int quantidade = rs.getInt("quantidade");
                contagem.put(tipoBanco, quantidade);
                logger.info("📊 [SELOS] Tipo " + tipoBanco + ": " + quantidade + " disponíveis");
            }

            Map<String, Integer> contagemFrontend = new HashMap<>();
            contagemFrontend.put("TP1", contagem.get("0001"));
            contagemFrontend.put("TPD", contagem.get("0003"));
            contagemFrontend.put("TPI", contagem.get("0004"));
            contagemFrontend.put("TP3", contagem.get("0009"));
            contagemFrontend.put("TP4", contagem.get("0010"));

            int totalGeral = contagemFrontend.values().stream().mapToInt(Integer::intValue).sum();

            resultado.put("success", true);
            resultado.put("contagem", contagemFrontend);
            resultado.put("totalGeral", totalGeral);
            resultado.put("mensagem", "Contagem obtida com sucesso");

            logger.info("✅ [SELOS] Contagem final: " + contagemFrontend);
            logger.info("✅ [SELOS] Total geral: " + totalGeral);

        } catch (Exception e) {
            logger.severe("❌ [SELOS] Erro ao contar selos: " + e.getMessage());
            resultado.put("mensagem", "Erro: " + e.getMessage());

            Map<String, Integer> contagemZerada = new HashMap<>();
            contagemZerada.put("TP1", 0);
            contagemZerada.put("TPD", 0);
            contagemZerada.put("TPI", 0);
            contagemZerada.put("TP3", 0);
            contagemZerada.put("TP4", 0);

            resultado.put("contagem", contagemZerada);
            resultado.put("totalGeral", 0);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                logger.warning("❌ [SELOS] Erro ao fechar recursos: " + e.getMessage());
            }
        }

        return resultado;
    }

    public Map<String, Object> buscarSelosDisponiveis(String tipoSeloFrontend, int quantidade) {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("success", false);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();

            String tipoBanco = converterTipoFrontendParaBanco(tipoSeloFrontend);

            String sql = "SELECT selo_sel, numpedido, tiposelo_sel " +
                    "FROM selos " +
                    "WHERE tipo_sel = '0000' " +
                    "AND tiposelo_sel = ? " +
                    "AND (chave1_sel IS NULL OR chave1_sel = '' OR chave1_sel = ' ') " +
                    "ORDER BY selo_sel ASC " +
                    "LIMIT ?";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, tipoBanco);
            stmt.setInt(2, quantidade);

            rs = stmt.executeQuery();

            List<String> selos = new ArrayList<>();
            while (rs.next()) {
                selos.add(rs.getString("selo_sel"));
            }

            resultado.put("success", true);
            resultado.put("selos", selos);
            resultado.put("quantidade", selos.size());
            resultado.put("tipo", tipoSeloFrontend);
            resultado.put("tipo_banco", tipoBanco);
            resultado.put("mensagem", selos.size() + " selos disponíveis do tipo " + tipoSeloFrontend);

        } catch (Exception e) {
            resultado.put("mensagem", "Erro ao buscar selos: " + e.getMessage());
            logger.log(Level.SEVERE, "Erro buscarSelosDisponiveis: " + e.getMessage(), e);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Erro ao fechar recursos: " + e.getMessage(), e);
            }
        }

        return resultado;
    }

    public Map<String, Object> reservarSelos(Map<String, Integer> selosRequeridos) {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("success", false);

        if (selosRequeridos == null || selosRequeridos.isEmpty()) {
            resultado.put("mensagem", "Nenhum selo requerido");
            return resultado;
        }

        Connection conn = null;

        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            Map<String, List<String>> selosReservados = new HashMap<>();
            boolean todosReservados = true;
            String erroReserva = null;

            for (Map.Entry<String, Integer> entry : selosRequeridos.entrySet()) {
                String tipoFrontend = entry.getKey();
                int quantidade = entry.getValue();

                if (quantidade <= 0)
                    continue;

                Map<String, Object> disponiveis = buscarSelosDisponiveis(tipoFrontend, quantidade);
                if (!Boolean.TRUE.equals(disponiveis.get("success"))) {
                    todosReservados = false;
                    erroReserva = "Não foi possível buscar selos do tipo " + tipoFrontend;
                    break;
                }

                @SuppressWarnings("unchecked")
                List<String> selosDisponiveis = (List<String>) disponiveis.get("selos");

                if (selosDisponiveis.size() < quantidade) {
                    todosReservados = false;
                    erroReserva = "Selos insuficientes do tipo " + tipoFrontend +
                            ". Requeridos: " + quantidade + ", Disponíveis: " + selosDisponiveis.size();
                    break;
                }

                String tipoBanco = (String) disponiveis.get("tipo_banco");
                if (!reservarSelosNoBanco(conn, selosDisponiveis, tipoBanco)) {
                    todosReservados = false;
                    erroReserva = "Erro ao reservar selos do tipo " + tipoFrontend;
                    break;
                }

                selosReservados.put(tipoFrontend, selosDisponiveis);
            }

            if (todosReservados) {
                conn.commit();
                resultado.put("success", true);
                resultado.put("selos_reservados", selosReservados);
                resultado.put("mensagem", "Selos reservados com sucesso");
            } else {
                conn.rollback();
                resultado.put("mensagem", erroReserva != null ? erroReserva : "Falha na reserva");
            }

        } catch (Exception e) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Erro no rollback: " + ex.getMessage(), ex);
            }
            resultado.put("mensagem", "Erro na reserva: " + e.getMessage());
            logger.log(Level.SEVERE, "Erro na reserva de selos: " + e.getMessage(), e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Erro ao fechar conexão: " + e.getMessage(), e);
            }
        }

        return resultado;
    }

    private boolean reservarSelosNoBanco(Connection conn, List<String> selos, String tipoBanco) throws SQLException {
        if (selos == null || selos.isEmpty())
            return true;

        String sql = "UPDATE selos SET tipo_sel = '0701', " +
                "chave1_sel = 'RESERVADO', " +
                "datahora = ? " +
                "WHERE selo_sel = ? AND tiposelo_sel = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            Timestamp agora = new Timestamp(System.currentTimeMillis());

            for (String selo : selos) {
                pstmt.setTimestamp(1, agora);
                pstmt.setString(2, selo);
                pstmt.setString(3, tipoBanco);
                pstmt.addBatch();
            }

            int[] resultados = pstmt.executeBatch();

            for (int result : resultados) {
                if (result != 1 && result != Statement.SUCCESS_NO_INFO) {
                    return false;
                }
            }
        }

        return true;
    }

    private String converterTipoFrontendParaBanco(String tipoFrontend) {
        Map<String, String> mapeamento = new HashMap<>();
        mapeamento.put("TP1", "0001");
        mapeamento.put("TPD", "0003");
        mapeamento.put("TPI", "0004");
        mapeamento.put("TP3", "0009");
        mapeamento.put("TP4", "0010");

        return mapeamento.getOrDefault(tipoFrontend, "0004");
    }

    private String converterTipoBancoParaFrontend(String tipoBanco) {
        Map<String, String> mapeamento = new HashMap<>();
        mapeamento.put("0001", "TP1");
        mapeamento.put("0003", "TPD");
        mapeamento.put("0004", "TPI");
        mapeamento.put("0009", "TP3");
        mapeamento.put("0010", "TP4");

        return mapeamento.getOrDefault(tipoBanco, "TPI");
    }

    public Map<String, Object> verificarDisponibilidadePorTipo(String tipoSelo) {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("success", false);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();

            String tipoBanco = converterTipoFrontendParaBanco(tipoSelo);

            String sql = "SELECT COUNT(*) as total, " +
                    "MIN(selo_sel) as primeiro, " +
                    "MAX(selo_sel) as ultimo " +
                    "FROM selos " +
                    "WHERE tipo_sel = '0000' " +
                    "AND tiposelo_sel = ? " +
                    "AND (chave1_sel IS NULL OR chave1_sel = '' OR chave1_sel = ' ')";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, tipoBanco);

            rs = stmt.executeQuery();

            if (rs.next()) {
                int total = rs.getInt("total");
                String primeiro = rs.getString("primeiro");
                String ultimo = rs.getString("ultimo");

                resultado.put("success", true);
                resultado.put("tipo", tipoSelo);
                resultado.put("tipo_banco", tipoBanco);
                resultado.put("disponiveis", total);
                resultado.put("primeiro", primeiro);
                resultado.put("ultimo", ultimo);
                resultado.put("mensagem", total + " selos disponíveis do tipo " + tipoSelo);
            }

        } catch (Exception e) {
            resultado.put("mensagem", "Erro na verificação: " + e.getMessage());
            logger.log(Level.SEVERE, "Erro na verificação de disponibilidade: " + e.getMessage(), e);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Erro ao fechar recursos: " + e.getMessage(), e);
            }
        }

        return resultado;
    }

    public Map<String, Object> liberarSelosReservados(Map<String, List<String>> selosReservados) {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("success", false);

        if (selosReservados == null || selosReservados.isEmpty()) {
            resultado.put("mensagem", "Nenhum selo para liberar");
            return resultado;
        }

        Connection conn = null;

        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            boolean todosLiberados = true;

            for (Map.Entry<String, List<String>> entry : selosReservados.entrySet()) {
                String tipoFrontend = entry.getKey();
                List<String> selos = entry.getValue();

                String tipoBanco = converterTipoFrontendParaBanco(tipoFrontend);

                if (!liberarSelosNoBanco(conn, selos, tipoBanco)) {
                    todosLiberados = false;
                    break;
                }
            }

            if (todosLiberados) {
                conn.commit();
                resultado.put("success", true);
                resultado.put("mensagem", "Selos liberados com sucesso");
            } else {
                conn.rollback();
                resultado.put("mensagem", "Falha ao liberar alguns selos");
            }

        } catch (Exception e) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Erro no rollback: " + ex.getMessage(), ex);
            }
            resultado.put("mensagem", "Erro ao liberar selos: " + e.getMessage());
            logger.log(Level.SEVERE, "Erro ao liberar selos: " + e.getMessage(), e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Erro ao fechar conexão: " + e.getMessage(), e);
            }
        }

        return resultado;
    }

    private boolean liberarSelosNoBanco(Connection conn, List<String> selos, String tipoBanco) throws SQLException {
        if (selos == null || selos.isEmpty())
            return true;

        String sql = "UPDATE selos SET tipo_sel = '0000', " +
                "chave1_sel = NULL, " +
                "chave2_sel = NULL, " +
                "dataselo_sel = NULL, " +
                "codtipoato = NULL, " +
                "nome = NULL, " +
                "cpfcnpj = NULL, " +
                "protocolo = NULL, " +
                "datahora = NULL, " +
                "chavedigital = NULL, " +
                "IDAP = NULL " +
                "WHERE selo_sel = ? AND tiposelo_sel = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String selo : selos) {
                pstmt.setString(1, selo);
                pstmt.setString(2, tipoBanco);
                pstmt.addBatch();
            }

            int[] resultados = pstmt.executeBatch();

            for (int result : resultados) {
                if (result != 1 && result != Statement.SUCCESS_NO_INFO) {
                    return false;
                }
            }
        }

        return true;
    }

    public Map<String, Object> getEstatisticasSelos() {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("success", false);

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.createStatement();

            String sqlTotal = "SELECT COUNT(*) as total FROM selos";
            rs = stmt.executeQuery(sqlTotal);
            int totalSelos = 0;
            if (rs.next()) {
                totalSelos = rs.getInt("total");
            }

            String sqlDisponiveis = "SELECT COUNT(*) as disponiveis FROM selos WHERE tipo_sel = '0000'";
            rs = stmt.executeQuery(sqlDisponiveis);
            int disponiveis = 0;
            if (rs.next()) {
                disponiveis = rs.getInt("disponiveis");
            }

            String sqlUtilizados = "SELECT COUNT(*) as utilizados FROM selos WHERE tipo_sel != '0000'";
            rs = stmt.executeQuery(sqlUtilizados);
            int utilizados = 0;
            if (rs.next()) {
                utilizados = rs.getInt("utilizados");
            }

            String sqlTipos = "SELECT tiposelo_sel, COUNT(*) as quantidade " +
                    "FROM selos WHERE tipo_sel = '0000' " +
                    "GROUP BY tiposelo_sel ORDER BY tiposelo_sel";
            rs = stmt.executeQuery(sqlTipos);

            Map<String, Integer> distribuicao = new HashMap<>();
            while (rs.next()) {
                String tipoBanco = rs.getString("tiposelo_sel");
                int quantidade = rs.getInt("quantidade");
                String tipoFrontend = converterTipoBancoParaFrontend(tipoBanco);
                distribuicao.put(tipoFrontend + " (" + tipoBanco + ")", quantidade);
            }

            resultado.put("success", true);
            resultado.put("total_selos", totalSelos);
            resultado.put("disponiveis", disponiveis);
            resultado.put("utilizados", utilizados);
            resultado.put("taxa_utilizacao", totalSelos > 0 ? (double) utilizados / totalSelos * 100 : 0);
            resultado.put("distribuicao", distribuicao);
            resultado.put("mensagem", "Estatísticas obtidas com sucesso");

        } catch (Exception e) {
            resultado.put("mensagem", "Erro ao obter estatísticas: " + e.getMessage());
            logger.log(Level.SEVERE, "Erro ao obter estatísticas: " + e.getMessage(), e);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Erro ao fechar recursos: " + e.getMessage(), e);
            }
        }

        return resultado;
    }
}