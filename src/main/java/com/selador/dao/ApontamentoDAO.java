package com.selador.dao;

import com.selador.model.Apontamento;
import com.selador.util.TipoOperacaoMapeamento;
import com.selador.util.TipoOperacaoMapeamento.MapeamentoTipo;
import com.selador.web.dto.IntervaloApontamentoDTO;
import com.selador.enums.FormatoData;
import com.selador.exception.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

/**
 * DAO para operações na tabela ctp001 (apontamentos)
 * VERSÃO CORRIGIDA - Compatível com BaseDAO atualizada
 */
public class ApontamentoDAO extends BaseDAO {
    
    private static final Logger logger = Logger.getLogger(ApontamentoDAO.class.getName());
    
    // Consultas SQL
    private static final String TABLE_NAME = "ctp001";
    
    /**
     * MÉTODO PRINCIPAL - Versão que trata condições especiais (739 e 706)
     * COM CORREÇÃO: Intimação/Digitalização usa apenas dataapo_001 (sem OR)
     */
    @Override
    public IntervaloApontamentoDTO buscarIntervaloPorTipoEData(String tipoOuCodigo, String descricaoOperacao, String dataBanco) 
            throws DatabaseException {
        
        System.out.println("🔍 [DAO] BUSCANDO INTERVALO - VERSÃO COM CONDIÇÕES ESPECIAIS");
        System.out.println("   Parâmetro recebido: " + tipoOuCodigo);
        System.out.println("   Descrição: " + descricaoOperacao);
        System.out.println("   Data: " + dataBanco);
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionFactory.getConnection();
            
            // ============================================
            // 1. OBTER MAPEAMENTO
            // ============================================
            MapeamentoTipo mapeamento = TipoOperacaoMapeamento.getPorCodigoComDescricao(tipoOuCodigo, descricaoOperacao);
            
            if (mapeamento == null) {
                System.out.println("⚠️ [DAO] Mapeamento não encontrado, usando padrão Apontamento");
                mapeamento = new MapeamentoTipo(
                    descricaoOperacao != null ? descricaoOperacao : "Apontamento",
                    tipoOuCodigo,
                    "dataapo_001",
                    "YYYYMMDD",
                    false
                );
            }
            
            System.out.println("✅ [DAO] MAPEAMENTO DETERMINADO:");
            System.out.println("   Label: " + mapeamento.getLabelFrontend());
            System.out.println("   Código: " + mapeamento.getCodigo());
            System.out.println("   Coluna: " + mapeamento.getColunaData());
            System.out.println("   Sem zero: " + mapeamento.isUsaFormatoDDMMYYYYSemZero());
            
            // ============================================
            // 2. CONVERTER DATA - CORREÇÃO: SE A COLUNA É dataapo_001, SEMPRE YYYYMMDD
            // ============================================
            boolean usaFormatoSemZero = mapeamento.isUsaFormatoDDMMYYYYSemZero();
            
            // 🔥 CORREÇÃO: Se a coluna é dataapo_001, sempre usar YYYYMMDD (com zeros)
            if ("dataapo_001".equals(mapeamento.getColunaData())) {
                usaFormatoSemZero = false; // Força YYYYMMDD
                System.out.println("🔧 [DAO] Coluna dataapo_001 detectada: usando YYYYMMDD (usaFormatoSemZero=false)");
            }
            
            String dataFormatada = FormatoData.converterParaFormatoBanco(dataBanco, usaFormatoSemZero);
            System.out.println("✅ [DAO] Data convertida: " + dataFormatada);
            
            // ============================================
            // 3. MONTAR QUERY SQL COM CONDIÇÕES ESPECIAIS
            // ============================================
            String sql = montarQueryComCondicoesEspeciais(mapeamento, dataFormatada);
            
            System.out.println("📝 [DAO] Query SQL FINAL:");
            System.out.println("   SQL: " + sql);
            
            // ============================================
            // 4. EXECUTAR QUERY
            // ============================================
            stmt = conn.prepareStatement(sql);
            
            // 🔥 CORREÇÃO: TODOS OS TIPOS USAM APENAS 1 PARÂMETRO AGORA
            // Intimação/Digitalização não usa mais OR, então não precisa de 2 parâmetros
            stmt.setString(1, dataFormatada);
            System.out.println("🔧 [DAO] Parâmetro único = '" + dataFormatada + "'");
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                String numApo1Ini = rs.getString("NumApo1Ini");
                String numApo1Fim = rs.getString("NumApo1Fim");
                String inicial = rs.getString("Inicial");
                String finalNum = rs.getString("Final");
                
                if (numApo1Ini != null && inicial != null) {
                    System.out.println("✅ [DAO] INTERVALO ENCONTRADO:");
                    System.out.println("   NumApo1Ini: " + numApo1Ini);
                    System.out.println("   NumApo1Fim: " + (numApo1Fim != null ? numApo1Fim : numApo1Ini));
                    System.out.println("   Inicial: " + inicial);
                    System.out.println("   Final: " + (finalNum != null ? finalNum : inicial));
                    
                    // Formatar numApo2 (remover zeros à esquerda)
                    String numApo2Ini = inicial.replaceAll("^0+", "");
                    String numApo2Fim = finalNum != null ? finalNum.replaceAll("^0+", "") : numApo2Ini;
                    if (numApo2Ini.isEmpty()) numApo2Ini = "1";
                    if (numApo2Fim == null || numApo2Fim.isEmpty()) numApo2Fim = numApo2Ini;
                    
                    return new IntervaloApontamentoDTO(
                        numApo1Ini,
                        numApo2Ini,
                        numApo1Fim != null ? numApo1Fim : numApo1Ini,
                        numApo2Fim
                    );
                }
            }
            
            System.out.println("⚠️ [DAO] Nenhum registro encontrado");
            return null;
            
        } catch (SQLException e) {
            System.err.println("❌ [DAO] Erro SQL: " + e.getMessage());
            e.printStackTrace();
            throw new DatabaseException("Erro ao buscar intervalo", e);
        } catch (Exception e) {
            System.err.println("❌ [DAO] Erro geral: " + e.getMessage());
            e.printStackTrace();
            throw new DatabaseException("Erro inesperado", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("⚠️ Erro ao fechar recursos: " + e.getMessage());
            }
        }
    }

    /**
     * Método alternativo - Intimação/Digitalização usa APENAS dataapo_001 (sem OR)
     */
    private String montarQueryComCondicoesEspeciais(MapeamentoTipo mapeamento, String dataFormatada) {
        String codigo = mapeamento.getCodigo();
        String label = mapeamento.getLabelFrontend();
        String colunaData = mapeamento.getColunaData();
        
        // Query base
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT MIN(numapo1_001) as NumApo1Ini, ");
        sql.append("       MAX(numapo1_001) as NumApo1Fim, ");
        sql.append("       MIN(CAST(numapo2_001 AS UNSIGNED)) as Inicial, ");
        sql.append("       MAX(CAST(numapo2_001 AS UNSIGNED)) as Final ");
        sql.append("FROM ").append(TABLE_NAME).append(" ");
        sql.append("WHERE controle_001 = '01' ");
        
        // 🔥 CORREÇÃO: Intimação/Digitalização usa APENAS a coluna do mapeamento (dataapo_001)
        // SEM condição OR
        sql.append("  AND ").append(colunaData).append(" = ?");
        
        System.out.println("🔧 [DAO] Usando coluna: " + colunaData);
        
        // Adicionar outras condições especiais
        if ("739".equals(codigo) || "Instrumento Diferido".equalsIgnoreCase(label)) {
            sql.append(" AND especie_001 > '089'");
            System.out.println("🔧 [DAO] Adicionando condição especial: especie_001 > '089' (Instrumento Diferido)");
            
        } else if ("706".equals(codigo) || "Protesto com Custas Antecipadas".equalsIgnoreCase(label)) {
            sql.append(" AND especie_001 < '090'");
            System.out.println("🔧 [DAO] Adicionando condição especial: especie_001 < '090' (Protesto com Custas)");
            
        } else if ("729".equals(codigo) || "Devolução por Irregularidade".equalsIgnoreCase(label)) {
            sql.append(" AND situacao_001 = '30'");
            System.out.println("🔧 [DAO] Adicionando condição especial: situacao_001 = '30' (Devolução)");
        }
        
        return sql.toString();
    }
    
    // ============================================
    // MÉTODOS ADICIONAIS CORRIGIDOS
    // ============================================
    
    /**
     * Busca apontamentos por data COM CONDIÇÕES ESPECIAIS
     */
    public List<Apontamento> buscarPorData(String dataFormatada, String tipoFrontend) throws DatabaseException {
        // 1. Obter mapeamento
        MapeamentoTipo mapeamento = TipoOperacaoMapeamento.getPorLabel(tipoFrontend);
        if (mapeamento == null) {
            logger.warning("Tipo não mapeado: " + tipoFrontend + ". Usando padrão.");
            mapeamento = TipoOperacaoMapeamento.getPorLabel("Apontamento");
        }
        
        // 2. Converter data - CORREÇÃO: SE A COLUNA É dataapo_001, SEMPRE YYYYMMDD
        boolean usaFormatoSemZero = mapeamento.isUsaFormatoDDMMYYYYSemZero();
        
        // 🔥 CORREÇÃO: Se a coluna é dataapo_001, sempre usar YYYYMMDD (com zeros)
        if ("dataapo_001".equals(mapeamento.getColunaData())) {
            usaFormatoSemZero = false; // Força YYYYMMDD
            System.out.println("🔧 [BUSCA POR DATA] Coluna dataapo_001 detectada: usando YYYYMMDD");
        }
        
        String dataParaBusca = FormatoData.converterParaFormatoBanco(dataFormatada, usaFormatoSemZero);
        
        // 3. Montar query COM JOIN E CONDIÇÕES ESPECIAIS
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT c.numapo1_001, c.numapo2_001, c.controle_001, c.devedor_001, ");
        sql.append("c.cgccpfsac_001, c.valor_001, CAST(c.numapo2_001 AS UNSIGNED) as protocolo, ");
        sql.append("c.seloaponta_001, c.dataapo_001, c.dtintimacao_001, c.dataret_001, ");
        sql.append("c.datapag_001, c.databai_001, c.idap_001, c.especie_001, ");
        sql.append("p.RVLRDIG_MES, ");
        sql.append("c.dtindevido_001, c.dtproces_001, c.dtsusta_001, c.dtrevog_001, ");
        sql.append("c.dtsuspenso_001, c.dtrevsusp_001, c.datasusp2_001 ");
        sql.append("FROM ").append(TABLE_NAME).append(" c ");
        sql.append("INNER JOIN parametros p ON p.CODIGO_PAR = 1 ");
        sql.append("WHERE c.controle_001 = '01' AND ");
        sql.append(mapeamento.getColunaData()).append(" = ?");
        
        // Adicionar condições especiais
        String codigo = mapeamento.getCodigo();
        if ("739".equals(codigo)) {
            sql.append(" AND c.especie_001 > '089'");
            System.out.println("🔧 [DAO] Busca por data: Adicionando condição especie_001 > '089'");
        } else if ("706".equals(codigo)) {
            sql.append(" AND c.especie_001 < '090'");
            System.out.println("🔧 [DAO] Busca por data: Adicionando condição especie_001 < '090'");
        } else if ("729".equals(codigo)) {
            sql.append(" AND c.situacao_001 = '30'");
            System.out.println("🔧 [DAO] Busca por data: Adicionando condição situacao_001 = '30'");
        }
        
        sql.append(" ORDER BY CAST(c.numapo1_001 AS UNSIGNED), CAST(c.numapo2_001 AS UNSIGNED)");
        
        String sqlFinal = sql.toString();
        List<Map<String, Object>> resultados = executeQuery(sqlFinal, dataParaBusca);
        
        // Converter para objetos Apontamento
        List<Apontamento> apontamentos = new ArrayList<>();
        for (Map<String, Object> row : resultados) {
            Apontamento apontamento = mapToApontamento(row);
            if (apontamento != null) {
                apontamentos.add(apontamento);
            }
        }
        
        return apontamentos;
    }
    
    /**
     * Busca por período
     */
    public List<Apontamento> buscarPorPeriodo(String dataInicio, String dataFim, String tipoFrontend) throws DatabaseException {
        MapeamentoTipo mapeamento = TipoOperacaoMapeamento.getPorLabel(tipoFrontend);
        if (mapeamento == null) {
            logger.warning("Tipo não mapeado: " + tipoFrontend + ". Usando padrão.");
            mapeamento = TipoOperacaoMapeamento.getPorLabel("Apontamento");
        }
        
        // Converter datas
        String dataInicioConvertida = FormatoData.converterParaFormatoBanco(dataInicio, mapeamento.isUsaFormatoDDMMYYYYSemZero());
        String dataFimConvertida = FormatoData.converterParaFormatoBanco(dataFim, mapeamento.isUsaFormatoDDMMYYYYSemZero());
        
        String sql = String.format(
            "SELECT numapo1_001, numapo2_001, controle_001, devedor_001, " +
            "cgccpfsac_001, valor_001, CAST(numapo2_001 AS UNSIGNED) as protocolo, " +
            "seloaponta_001, dataapo_001, dtintimacao_001, dataret_001, " +
            "datapag_001, databai_001, idap_001, especie_001, " +
            "dtindevido_001, dtproces_001, dtsusta_001, dtrevog_001, " +
            "dtsuspenso_001, dtrevsusp_001, datasusp2_001 " +
            "FROM %s " +
            "WHERE controle_001 = '01' AND %s BETWEEN ? AND ? " +
            "ORDER BY CAST(%s AS UNSIGNED), CAST(numapo1_001 AS UNSIGNED), CAST(numapo2_001 AS UNSIGNED)",
            TABLE_NAME, mapeamento.getColunaData(), mapeamento.getColunaData()
        );
        
        List<Map<String, Object>> resultados = executeQuery(sql, dataInicioConvertida, dataFimConvertida);
        
        List<Apontamento> apontamentos = new ArrayList<>();
        for (Map<String, Object> row : resultados) {
            Apontamento apontamento = mapToApontamento(row);
            if (apontamento != null) {
                apontamentos.add(apontamento);
            }
        }
        
        return apontamentos;
    }
    
    /**
     * Atualiza selos em lote
     */
    public void atualizarSelosEmLote(Map<String, String> apontamentosSelos) throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);
            
            String sql = "UPDATE " + TABLE_NAME + " " +
                    "SET seloaponta_001 = ? " +
                    "WHERE numapo1_001 = ? AND numapo2_001 = ?";
            
            stmt = conn.prepareStatement(sql);
            
            int batchCount = 0;
            for (Map.Entry<String, String> entry : apontamentosSelos.entrySet()) {
                String[] chaves = entry.getKey().split("/");
                if (chaves.length == 2) {
                    stmt.setString(1, entry.getValue()); // selo
                    stmt.setString(2, chaves[0]); // numApo1
                    stmt.setString(3, chaves[1]); // numApo2
                    stmt.addBatch();
                    batchCount++;
                    
                    if (batchCount % 100 == 0) {
                        stmt.executeBatch();
                    }
                }
            }
            
            stmt.executeBatch();
            conn.commit();
            
            logger.info("Atualizados " + batchCount + " selos em lote");
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                logger.severe("Erro ao fazer rollback: " + ex.getMessage());
            }
            throw new DatabaseException("Erro ao atualizar selos em lote: " + e.getMessage(), e);
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.severe("Erro ao fechar recursos: " + e.getMessage());
            }
        }
    }
    
    /**
     * Conta registros não selados - VERSÃO CORRIGIDA COM NUMTIPATO
     * ATUALIZADO: Intimação/Digitalização usa APENAS dataapo_001 (sem OR)
     */
    public int contarRegistrosNaoSeladosCompleto(String dataFormatada, String tipoFrontend,
                                                String anoMesInicial, String anoMesFinal, 
                                                String aponInicial, String aponFinal) throws DatabaseException {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionFactory.getConnection();
            
            System.out.println("🔍 [CONTAGEM CORRIGIDA] Método iniciado:");
            System.out.println("   Tipo: " + tipoFrontend);
            System.out.println("   Data: " + dataFormatada);
            System.out.println("   Ano/Mês Inicial: " + anoMesInicial);
            System.out.println("   Ano/Mês Final: " + anoMesFinal);
            System.out.println("   Apontamento Inicial: " + aponInicial);
            System.out.println("   Apontamento Final: " + aponFinal);
            
            // Obter mapeamento para determinar formato de data
            MapeamentoTipo mapeamento = TipoOperacaoMapeamento.getPorLabel(tipoFrontend);
            if (mapeamento == null) {
                logger.warning("Tipo não mapeado: " + tipoFrontend + ". Usando padrão.");
                mapeamento = TipoOperacaoMapeamento.getPorLabel("Apontamento");
            }
            
            // Converter data usando formato do mapeamento
            boolean usaFormatoSemZero = mapeamento.isUsaFormatoDDMMYYYYSemZero();
            
            // 🔥 CORREÇÃO: Se a coluna é dataapo_001, sempre usar YYYYMMDD (com zeros)
            if ("dataapo_001".equals(mapeamento.getColunaData())) {
                usaFormatoSemZero = false; // Força YYYYMMDD
                System.out.println("🔧 [CONTAGEM] Coluna dataapo_001 detectada: usando YYYYMMDD (usaFormatoSemZero=false)");
            }
            
            String dataConvertida = FormatoData.converterParaFormatoBanco(dataFormatada, usaFormatoSemZero);
            
            // Obter código da operação
            String codigoOperacao = obterCodigoOperacaoPorTipo(tipoFrontend);
            String colunaData = mapeamento.getColunaData();
            
            System.out.println("🔧 Configuração:");
            System.out.println("   Código Operação: " + codigoOperacao);
            System.out.println("   Coluna Data: " + colunaData);
            System.out.println("   Usa Formato Sem Zero: " + usaFormatoSemZero);
            System.out.println("   Data Convertida: " + dataConvertida);
            
            // 🔥 CORREÇÃO: Para TODOS os tipos, usar APENAS a coluna do mapeamento
            // Intimação/Digitalização não usa mais OR
            String sql = "SELECT COUNT(*) as total_registros " +
                    "FROM ctp001 c " +
                    "LEFT JOIN selados s ON c.idap_001 = s.IDAP " +
                    "AND s.NUMTIPATO = ? " + // NUMTIPATO específico
                    "WHERE c.controle_001 = '01' " +
                    "AND c.numapo1_001 BETWEEN ? AND ? " +
                    "AND CAST(c.numapo2_001 AS UNSIGNED) BETWEEN ? AND ? " +
                    "AND " + colunaData + " = ? " + // 🔥 APENAS a coluna do mapeamento
                    "AND s.IDAP IS NULL";
            
            System.out.println("📝 SQL Contagem:");
            System.out.println("   " + sql);
            
            // Preparar statement
            stmt = conn.prepareStatement(sql);
            
            // Parâmetros
            stmt.setString(1, codigoOperacao);
            stmt.setString(2, anoMesInicial);
            stmt.setString(3, anoMesFinal);
            stmt.setInt(4, Integer.parseInt(aponInicial));
            stmt.setInt(5, Integer.parseInt(aponFinal));
            stmt.setString(6, dataConvertida);
            
            System.out.println("🔧 Parâmetros:");
            System.out.println("   NUMTIPATO=" + codigoOperacao +
                            ", anoMesInicial=" + anoMesInicial + 
                            ", anoMesFinal=" + anoMesFinal +
                            ", aponInicial=" + aponInicial + 
                            ", aponFinal=" + aponFinal +
                            ", data=" + dataConvertida);
            
            rs = stmt.executeQuery();
            
            int total = 0;
            if (rs.next()) {
                total = rs.getInt("total_registros");
            }
            
            // 🔥 **LÓGICA ESPECIAL PARA INTIMAÇÃO/DIGITALIZAÇÃO (RVLRDIG_MES)**
            // MANTIDO para compatibilidade
            if (isIntimacaoDigitalizacaoPorTipo(tipoFrontend)) {
                // Verificar RVLRDIG_MES
                int RVLRDIGMES = getRVLRDIGMES(conn);
                System.out.println("   RVLRDIG_MES = " + RVLRDIGMES);
                
                if (RVLRDIGMES == 1) {
                    // Contar códigos especiais (725, 735, 736)
                    int especiais = contarCodigosEspeciaisIntimacao(conn, anoMesInicial, anoMesFinal, 
                                                                aponInicial, aponFinal, dataConvertida, codigoOperacao, colunaData);
                    System.out.println("   Códigos especiais (725/735/736): " + especiais);
                    
                    // Fórmula: (total * 2) + especiais
                    int totalAjustado = (total * 2) + especiais;
                    System.out.println("✅ [CONTAGEM AJUSTADA] Total original: " + total + 
                                    ", Total ajustado: " + totalAjustado);
                    total = totalAjustado;
                }
            }
            
            System.out.println("✅ [CONTAGEM] Total final: " + total);
            return total;
            
        } catch (Exception e) {
            System.out.println("❌ [CONTAGEM] Erro: " + e.getMessage());
            e.printStackTrace();
            throw new DatabaseException("Erro ao contar registros não selados", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.warning("Erro ao fechar recursos: " + e.getMessage());
            }
        }
    }

    /**
     * Verifica se é Intimação/Digitalização pelo tipo
     */
    private boolean isIntimacaoDigitalizacaoPorTipo(String tipoFrontend) {
        if (tipoFrontend == null) return false;
        
        String tipoUpper = tipoFrontend.toUpperCase();
        
        return tipoUpper.contains("INTIMA") || 
               tipoUpper.contains("DIGITALIZA") || 
               "INTIMAÇÃO/DIGITALIZAÇÃO".equalsIgnoreCase(tipoFrontend);
    }

    /**
     * Método auxiliar para contar códigos especiais na Intimação/Digitalização
     * ATUALIZADO: Usa apenas a coluna do mapeamento
     */
    private int contarCodigosEspeciaisIntimacao(Connection conn, String anoMesInicial, String anoMesFinal,
                                            String aponInicial, String aponFinal, String dataConvertida,
                                            String codigoOperacao, String colunaData) throws SQLException {
        
        // 🔥 SQL ATUALIZADA: Usa apenas a coluna do mapeamento (sem OR)
        String sql = "SELECT COUNT(*) as especiais " +
                    "FROM ctp001 c " +
                    "LEFT JOIN selados s ON c.idap_001 = s.IDAP " +
                    "AND s.NUMTIPATO IN ('725', '735', '736') " +
                    "WHERE c.controle_001 = '01' " +
                    "AND c.numapo1_001 BETWEEN ? AND ? " +
                    "AND CAST(c.numapo2_001 AS UNSIGNED) BETWEEN ? AND ? " +
                    "AND " + colunaData + " = ? " + // 🔥 APENAS a coluna do mapeamento
                    "AND s.IDAP IS NULL";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, anoMesInicial);
            pstmt.setString(2, anoMesFinal);
            pstmt.setInt(3, Integer.parseInt(aponInicial));
            pstmt.setInt(4, Integer.parseInt(aponFinal));
            pstmt.setString(5, dataConvertida);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt("especiais") : 0;
            }
        }
    }

    /**
     * Obtém valor de RVLRDIG_MES da tabela parametros
     */
    private int getRVLRDIGMES(Connection conn) throws SQLException {
        String sql = "SELECT RVLRDIG_MES FROM parametros WHERE CODIGO_PAR = 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("RVLRDIG_MES");
            }
        }
        return 0; // Default se não encontrar
    }
    
    /**
     * Busca não selados
     */
    public List<Apontamento> buscarNaoSelados(String dataFormatada, String tipoFrontend) throws DatabaseException {
        MapeamentoTipo mapeamento = TipoOperacaoMapeamento.getPorLabel(tipoFrontend);
        if (mapeamento == null) {
            logger.warning("Tipo não mapeado: " + tipoFrontend + ". Usando padrão.");
            mapeamento = TipoOperacaoMapeamento.getPorLabel("Apontamento");
        }
        
        String dataParaBusca = FormatoData.converterParaFormatoBanco(dataFormatada, mapeamento.isUsaFormatoDDMMYYYYSemZero());
        
        String sql = String.format(
            "SELECT numapo1_001, numapo2_001, controle_001, devedor_001, " +
            "cgccpfsac_001, valor_001, CAST(numapo2_001 AS UNSIGNED) as protocolo, " +
            "seloaponta_001, dataapo_001, dtintimacao_001, dataret_001, " +
            "datapag_001, databai_001, idap_001, especie_001, " +
            "dtindevido_001, dtproces_001, dtsusta_001, dtrevog_001, " +
            "dtsuspenso_001, dtrevsusp_001, datasusp2_001 " +
            "FROM %s " +
            "WHERE controle_001 = '01' AND %s = ? " +
            "AND (seloaponta_001 IS NULL OR seloaponta_001 = '') " +
            "ORDER BY CAST(numapo1_001 AS UNSIGNED), CAST(numapo2_001 AS UNSIGNED)",
            TABLE_NAME, mapeamento.getColunaData()
        );
        
        List<Map<String, Object>> resultados = executeQuery(sql, dataParaBusca);
        
        List<Apontamento> apontamentos = new ArrayList<>();
        for (Map<String, Object> row : resultados) {
            Apontamento apontamento = mapToApontamento(row);
            if (apontamento != null && !apontamento.isSelado()) {
                apontamentos.add(apontamento);
            }
        }
        
        return apontamentos;
    }
    
    /**
     * Busca por número
     */
    public Apontamento buscarPorNumero(String numApo1, String numApo2) throws DatabaseException {
        String sql = "SELECT numapo1_001, numapo2_001, controle_001, devedor_001, " +
                "cgccpfsac_001, valor_001, CAST(numapo2_001 AS UNSIGNED) as protocolo, " +
                "seloaponta_001, dataapo_001, dtintimacao_001, dataret_001, " +
                "datapag_001, databai_001, idap_001, especie_001, " +
                "dtindevido_001, dtproces_001, dtsusta_001, dtrevog_001, " +
                "dtsuspenso_001, dtrevsusp_001, datasusp2_001 " +
                "FROM " + TABLE_NAME + " " +
                "WHERE numapo1_001 = ? AND numapo2_001 = ?";
        
        List<Map<String, Object>> resultados = executeQuery(sql, numApo1, numApo2);
        
        if (resultados.isEmpty()) {
            return null;
        }
        
        return mapToApontamento(resultados.get(0));
    }
    
    /**
     * Atualiza selo
     */
    public int atualizarSelo(String numApo1, String numApo2, String selo) throws DatabaseException {
        String sql = "UPDATE " + TABLE_NAME + " " +
                "SET seloaponta_001 = ? " +
                "WHERE numapo1_001 = ? AND numapo2_001 = ?";
        
        return executeUpdate(sql, selo, numApo1, numApo2);
    }
    
    /**
     * Verifica existência
     */
    public boolean existeApontamento(String numApo1, String numApo2) throws DatabaseException {
        String sql = "SELECT COUNT(*) as count FROM " + TABLE_NAME + " " +
                "WHERE numapo1_001 = ? AND numapo2_001 = ?";
        
        List<Map<String, Object>> resultados = executeQuery(sql, numApo1, numApo2);
        
        if (!resultados.isEmpty()) {
            Map<String, Object> row = resultados.get(0);
            Object count = row.get("count");
            return count != null && Integer.parseInt(count.toString()) > 0;
        }
        
        return false;
    }
    
    // ============================================
    // MÉTODOS AUXILIARES CORRIGIDOS
    // ============================================
    
    /**
     * Converte Map para Apontamento (corrigido sem RVLRDIG_MES)
     */
    private Apontamento mapToApontamento(Map<String, Object> row) {
        try {
            Apontamento apontamento = new Apontamento();
            
            // Campos básicos
            apontamento.setNumApo1(getString(row, "numapo1_001"));
            apontamento.setNumApo2(getString(row, "numapo2_001"));
            apontamento.setControle(getString(row, "controle_001"));
            apontamento.setProtocolo(getString(row, "protocolo"));
            apontamento.setDevedor(getString(row, "devedor_001"));
            apontamento.setCpfCnpj(getString(row, "cgccpfsac_001"));
            apontamento.setSelo(getString(row, "seloaponta_001"));
            
            // Valor
            Object valorObj = row.get("valor_001");
            if (valorObj != null) {
                try {
                    apontamento.setValor(new java.math.BigDecimal(valorObj.toString()));
                } catch (Exception e) {
                    apontamento.setValor(java.math.BigDecimal.ZERO);
                }
            }
            
            return apontamento;
            
        } catch (Exception e) {
            logger.warning("Erro ao converter Map para Apontamento: " + e.getMessage());
            return null;
        }
    }
    
    private String getString(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value != null ? value.toString().trim() : "";
    }

    /**
     * Obtém código da operação (NUMTIPATO) por tipo
     */
    private String obterCodigoOperacaoPorTipo(String tipoFrontend) {
        if (tipoFrontend == null) return "701";
        
        tipoFrontend = tipoFrontend.trim().toUpperCase();
        
        // Mapeamento para códigos NUMTIPATO
        if (tipoFrontend.contains("701") || tipoFrontend.contains("APONTAMENTO")) return "701";
        if (tipoFrontend.contains("702") || tipoFrontend.contains("INTIMAÇÃO")) return "702";
        if (tipoFrontend.contains("703") || tipoFrontend.contains("REMESSA")) return "703";
        if (tipoFrontend.contains("704") || tipoFrontend.contains("PAGAMENTO")) return "704";
        if (tipoFrontend.contains("705") || tipoFrontend.contains("RETIRADA")) return "705";
        if (tipoFrontend.contains("706") || tipoFrontend.contains("CUSTAS ANTECIPADAS")) return "706";
        if (tipoFrontend.contains("725") || tipoFrontend.contains("DIGITALIZAÇÃO CUSTAS PAGAS")) return "725";
        if (tipoFrontend.contains("727") || tipoFrontend.contains("SUSTAÇÃO DEFINITIVA")) return "727";
        if (tipoFrontend.contains("729") || tipoFrontend.contains("DEVOLUÇÃO")) return "729";
        if (tipoFrontend.contains("733") || tipoFrontend.contains("SUSPENSÃO")) return "733";
        if (tipoFrontend.contains("734") || tipoFrontend.contains("SUSTAÇÃO")) return "734";
        if (tipoFrontend.contains("735") || tipoFrontend.contains("BAIXA DE SELO INTIMAÇÃO")) return "735";
        if (tipoFrontend.contains("736") || tipoFrontend.contains("BAIXA DE SELO DIGITALIZAÇÃO")) return "736";
        if (tipoFrontend.contains("738") || tipoFrontend.contains("INTIMAÇÃO CUSTAS DIFERIDAS")) return "738";
        if (tipoFrontend.contains("740") || tipoFrontend.contains("DIGITALIZAÇÃO DAS CUSTAS DIFERIDAS")) return "740";
        if (tipoFrontend.contains("739") || tipoFrontend.contains("INSTRUMENTO DIFERIDO")) return "739";
        
        // Mapeamento por descrição
        if (tipoFrontend.contains("INTIMAÇÃO/DIGITALIZAÇÃO") || tipoFrontend.contains("INTIMAÇÃO") || tipoFrontend.contains("DIGITALIZAÇÃO")) {
            if (tipoFrontend.contains("DIFERIDAS")) {
                return "738";
            } else {
                return "702";
            }
        }
        
        if (tipoFrontend.contains("RETIRADA/BAIXA")) return "705";
        if (tipoFrontend.contains("PAGAMENTO/BAIXA")) return "704";
        if (tipoFrontend.contains("PROTESTO COM CUSTAS ANTECIPADAS")) return "706";
        if (tipoFrontend.contains("DEVOLUÇÃO POR IRREGULARIDADE")) return "729";
        
        // Casos especiais para 733 e 734
        if (tipoFrontend.contains("SUSTAÇÃO DO PROTESTO") || 
            tipoFrontend.contains("REVOGAÇÃO DE SUSTAÇÃO") ||
            tipoFrontend.contains("SUSTAÇÃO")) {
            return "734";
        }
        
        if (tipoFrontend.contains("SUSPENSÃO DE PROTESTO") || 
            tipoFrontend.contains("REVOGAÇÃO DE SUSPENSO") ||
            tipoFrontend.contains("SEGUNDA REVOGAÇÃO") ||
            tipoFrontend.contains("SUSPENSÃO")) {
            return "733";
        }
        
        // Default para Apontamento
        return "701";
    }
}