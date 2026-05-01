package com.selador.dao;

import com.selador.model.Selo;
import com.selador.exception.DatabaseException;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;
import java.sql.*;

/**
 * DAO para operações na tabela selos
 */
public class SeloDAO extends BaseDAO {
    
    private static final Logger logger = Logger.getLogger(SeloDAO.class.getName());
    
    // Consultas SQL
    private static final String TABLE_NAME = "selos";
    
    private static final String SQL_BUSCAR_DISPONIVEL = 
        "SELECT selo_sel, tiposelo_sel, valorselo_sel " +
        "FROM " + TABLE_NAME + " " +
        "WHERE tipo_sel = '0000' " +
        "AND (dataselo_sel IS NULL) " +
        "AND tiposelo_sel = ? " +
        "AND (cancelado IS NULL OR cancelado != 'S') " +
        "ORDER BY selo_sel ASC " +
        "LIMIT 1 " +
        "FOR UPDATE";  // Lock para evitar concorrência
    
    private static final String SQL_CONTAR_DISPONIVEIS = 
        "SELECT COALESCE(st.sigle, s.tiposelo_sel) as tiposelo_label, COUNT(*) as total " +
        "FROM selos s " +
        "LEFT JOIN selo_tipo st ON s.tiposelo_sel = st.tipo " +
        "WHERE s.tipo_sel = '0000' " +
        "AND (s.dataselo_sel IS NULL) " +
        "AND (s.cancelado IS NULL OR s.cancelado != 'S') " +
        "GROUP BY s.tiposelo_sel, st.sigle " +
        "ORDER BY s.tiposelo_sel";
    
    private static final String SQL_ATUALIZAR_SELO_USADO = 
        "UPDATE " + TABLE_NAME + " SET " +
        "chave1_sel = ?, " +
        "chave2_sel = ?, " +
        "dataselo_sel = CURDATE(), " +
        "tipo_sel = ?, " +
        "numreq_sel = ?, " +
        "codtipoato = ?, " +
        "nome = ?, " +
        "cpfcnpj = ?, " +
        "protocolo = ?, " +
        "datahora = NOW(), " +
        "chavedigital = ?, " +
        "IDAP = ? " +
        "WHERE selo_sel = ?";
    
    private static final String SQL_BUSCAR_POR_SELO = 
        "SELECT * FROM " + TABLE_NAME + " WHERE selo_sel = ?";
    
    private static final String SQL_BUSCAR_POR_CHAVE = 
        "SELECT * FROM " + TABLE_NAME + " WHERE chave1_sel = ? AND chave2_sel = ?";
    
    private static final String SQL_BUSCAR_POR_PERIODO = 
        "SELECT * FROM " + TABLE_NAME + " " +
        "WHERE dataselo_sel BETWEEN ? AND ? " +
        "ORDER BY dataselo_sel DESC, selo_sel";
    
    private static final String SQL_VERIFICAR_UTILIZADO = 
        "SELECT COUNT(*) as total FROM " + TABLE_NAME + " " +
        "WHERE selo_sel = ? " +
        "AND (chave1_sel IS NOT NULL AND chave1_sel != '')";
    
    private static final String SQL_ATUALIZAR_VALOR = 
        "UPDATE " + TABLE_NAME + " SET valorselo_sel = ? WHERE selo_sel = ?";
    
    /**
     * Busca um selo disponível do tipo especificado
     */
    public Selo buscarDisponivel(String tipoSelo) throws DatabaseException {
        logger.info("Buscando selo disponível do tipo: " + tipoSelo);
        
        List<Map<String, Object>> resultados = executeQuery(SQL_BUSCAR_DISPONIVEL, tipoSelo);
        
        if (resultados.isEmpty()) {
            logger.warning("Nenhum selo disponível encontrado para tipo: " + tipoSelo);
            return null;
        }
        
        Map<String, Object> row = resultados.get(0);
        return mapToSelo(row);
    }
    

    /**
     * Marca um selo como utilizado
     */
    public int atualizarSeloUtilizado(Selo selo, Map<String, Object> dados) throws DatabaseException {
        logger.info("Atualizando selo como utilizado: " + selo.getNumeroSelo());
        
        String codTipoAto = (String) dados.getOrDefault("codTipoAto", "701");
        String tipoSel = (String) dados.getOrDefault("tipoSel", "0701");
        String numreqSel = (String) dados.getOrDefault("numreqSel", "0000000000");
        
        return executeUpdate(SQL_ATUALIZAR_SELO_USADO,
            dados.get("chave1"),       // numapo1_001
            dados.get("chave2"),       // numapo2_001
            tipoSel,                   // tipo_sel
            numreqSel,                 // numreq_sel
            Integer.parseInt(codTipoAto), // codtipoato
            dados.get("nome"),         // nome
            dados.get("cpfcnpj"),      // cpfcnpj
            dados.get("protocolo"),    // protocolo
            selo.getNumeroSelo(),      // chavedigital (usa o próprio número do selo)
            dados.get("idap"),         // IDAP
            selo.getNumeroSelo()       // WHERE selo_sel = ?
        );
    }
    
    /**
     * Busca selo pelo número
     */
    public Selo buscarPorNumero(String numeroSelo) throws DatabaseException {
        List<Map<String, Object>> resultados = executeQuery(SQL_BUSCAR_POR_SELO, numeroSelo);
        
        if (resultados.isEmpty()) {
            return null;
        }
        
        return mapToSelo(resultados.get(0));
    }
    
    /**
     * Busca selos por chave (numapo1/numapo2)
     */
    public List<Selo> buscarPorChave(String chave1, String chave2) throws DatabaseException {
        List<Map<String, Object>> resultados = executeQuery(SQL_BUSCAR_POR_CHAVE, chave1, chave2);
        return mapToSelos(resultados);
    }
    
    /**
     * Busca selos utilizados em um período
     */
    public List<Selo> buscarPorPeriodo(java.util.Date dataInicio, java.util.Date dataFim) 
            throws DatabaseException {
        
        java.sql.Date sqlDataInicio = new java.sql.Date(dataInicio.getTime());
        java.sql.Date sqlDataFim = new java.sql.Date(dataFim.getTime());
        
        List<Map<String, Object>> resultados = executeQuery(SQL_BUSCAR_POR_PERIODO, 
            sqlDataInicio, sqlDataFim);
        return mapToSelos(resultados);
    }
    
    /**
     * Verifica se um selo já foi utilizado
     */
    public boolean foiUtilizado(String numeroSelo) throws DatabaseException {
        List<Map<String, Object>> resultados = executeQuery(SQL_VERIFICAR_UTILIZADO, numeroSelo);
        
        if (!resultados.isEmpty()) {
            Object totalObj = resultados.get(0).get("total");
            if (totalObj instanceof Number) {
                return ((Number) totalObj).longValue() > 0;
            }
        }
        
        return false;
    }
    
    /**
     * Atualiza o valor de um selo
     */
    public int atualizarValor(String numeroSelo, double novoValor) throws DatabaseException {
        return executeUpdate(SQL_ATUALIZAR_VALOR, novoValor, numeroSelo);
    }
    
    /**
     * Reserva múltiplos selos em uma transação
     */
    public List<Selo> reservarSelos(String tipoSelo, int quantidade) throws DatabaseException {
        List<Selo> selosReservados = new ArrayList<>();
        List<TransactionOperation> operations = new ArrayList<>();
        
        for (int i = 0; i < quantidade; i++) {
            operations.add(new TransactionOperation(SQL_BUSCAR_DISPONIVEL, tipoSelo));
        }
        
        executeTransaction(operations);
        
        for (TransactionOperation op : operations) {
            List<Map<String, Object>> resultados = executeQuery(op.getSql(), op.getParams());
            if (!resultados.isEmpty()) {
                selosReservados.add(mapToSelo(resultados.get(0)));
            }
        }
        
        logger.info("Reservados " + selosReservados.size() + " selos do tipo " + tipoSelo);
        return selosReservados;
    }
    
    /**
     * Atualiza múltiplos selos em lote
     */
    public void atualizarSelosEmLote(List<Map<String, Object>> atualizacoes) throws DatabaseException {
        List<TransactionOperation> operations = new ArrayList<>();
        
        for (Map<String, Object> atualizacao : atualizacoes) {
            String codTipoAto = (String) atualizacao.getOrDefault("codTipoAto", "701");
            
            operations.add(new TransactionOperation(
                SQL_ATUALIZAR_SELO_USADO,
                atualizacao.get("chave1"),
                atualizacao.get("chave2"),
                atualizacao.get("tipoSel"),
                atualizacao.get("numreqSel"),
                Integer.parseInt(codTipoAto),
                atualizacao.get("nome"),
                atualizacao.get("cpfcnpj"),
                atualizacao.get("protocolo"),
                atualizacao.get("chavedigital"),
                atualizacao.get("idap"),
                atualizacao.get("selo")
            ));
        }
        
        if (!operations.isEmpty()) {
            executeTransaction(operations);
            logger.info("Atualizados " + operations.size() + " selos em lote");
        }
    }
    
    /**
     * Converte lista de Maps para lista de Selos
     */
    private List<Selo> mapToSelos(List<Map<String, Object>> resultados) {
        List<Selo> selos = new ArrayList<>();
        
        for (Map<String, Object> row : resultados) {
            Selo selo = mapToSelo(row);
            if (selo != null) {
                selos.add(selo);
            }
        }
        
        return selos;
    }
    
    /**
     * Converte Map para objeto Selo (versão limpa)
     */
    private Selo mapToSelo(Map<String, Object> row) {
        try {
            Selo selo = new Selo();
            
            selo.setNumeroSelo(getString(row, "selo_sel"));
            selo.setTipo(getString(row, "tipo_sel"));
            
            Double valorDouble = getDouble(row, "valorselo_sel");
            if (valorDouble != null) {
                selo.setValor(BigDecimal.valueOf(valorDouble));
            }
            
            selo.setChave1(getString(row, "chave1_sel"));
            selo.setChave2(getString(row, "chave2_sel"));
            
            selo.setDataSelo(convertToUtilDate(row, "dataselo_sel"));
            
            selo.setNome(getString(row, "nome"));
            selo.setCpfCnpj(getString(row, "cpfcnpj"));
            selo.setProtocolo(getString(row, "protocolo"));
            
            selo.setIdap(getString(row, "IDAP"));
            
            return selo;
            
        } catch (Exception e) {
            logger.warning("Erro ao converter Map para Selo: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Método auxiliar para converter para java.util.Date
     */
    private java.util.Date convertToUtilDate(Map<String, Object> row, String key) {
        Object value = row.get(key);
        
        if (value == null) {
            return null;
        }
        
        if (value instanceof java.util.Date) {
            return (java.util.Date) value;
        } else if (value instanceof java.sql.Date) {
            return new java.util.Date(((java.sql.Date) value).getTime());
        } else if (value instanceof java.sql.Timestamp) {
            return new java.util.Date(((java.sql.Timestamp) value).getTime());
        } else if (value instanceof String) {
            try {
                return java.sql.Date.valueOf((String) value);
            } catch (Exception e) {
                return null;
            }
        }
        
        return null;
    }
    
    private String getString(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value != null ? value.toString().trim() : "";
    }
    
    private Double getDouble(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).doubleValue();
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return null;
        }
    }
    
    public Map<String, Integer> contarDisponiveis() throws com.selador.exception.DatabaseException {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        System.out.println("SeloDAO: Executando contarDisponiveis...");
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_CONTAR_DISPONIVEIS);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String label = rs.getString("tiposelo_label");
                int total = rs.getInt("total");
                resultado.put(label, total);
                System.out.println("SeloDAO: Found " + label + " = " + total);
            }
            if (resultado.isEmpty()) {
                System.out.println("SeloDAO: Nenhum registro encontrado para a query de estoque.");
            }
        } catch (SQLException e) {
            System.err.println("SeloDAO: Erro ao contar disponíveis: " + e.getMessage());
            e.printStackTrace();
        }
        return resultado;
    }
}