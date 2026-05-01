package com.selador.dao;

import com.selador.model.Selado;
import com.selador.exception.DatabaseException;
import java.util.*;
import java.util.logging.Logger;

/**
 * DAO para operações na tabela selados
 */
public class SeladoDAO extends BaseDAO {
    
    private static final Logger logger = Logger.getLogger(SeladoDAO.class.getName());
    
    // Consultas SQL
    private static final String TABLE_NAME = "selados";
    
    private static final String SQL_INSERIR = 
        "INSERT INTO " + TABLE_NAME + " (" +
        "NUMTIPATO, IDAP, CHAVE, SELO, QRCODE, REGISTRO, " +
        "DATAENVIO, DATARETORNO, RETORNO, USUARIO, STATUS, JSON" + // Adicionado JSON
        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SQL_BUSCAR_POR_SELO = 
        "SELECT * FROM " + TABLE_NAME + " WHERE SELO = ?";
    
    private static final String SQL_BUSCAR_POR_REGISTRO = 
        "SELECT * FROM " + TABLE_NAME + " WHERE REGISTRO = ?";
    
    private static final String SQL_BUSCAR_POR_PERIODO = 
        "SELECT * FROM " + TABLE_NAME + " " +
        "WHERE DATAENVIO BETWEEN ? AND ? " +
        "ORDER BY DATAENVIO DESC";
    
    private static final String SQL_ATUALIZAR_RETORNO = 
        "UPDATE " + TABLE_NAME + " SET " +
        "DATARETORNO = ?, " +
        "RETORNO = ?, " +
        "STATUS = ? " +
        "WHERE SELO = ?";
    
    private static final String SQL_BUSCAR_POR_STATUS = 
        "SELECT * FROM " + TABLE_NAME + " WHERE STATUS = ?";
    
    private static final String SQL_BUSCAR_POR_IDAP = 
        "SELECT * FROM " + TABLE_NAME + " WHERE IDAP = ?";
    
    private static final String SQL_ATUALIZAR_QRCODE = 
        "UPDATE " + TABLE_NAME + " SET QRCODE = ? WHERE SELO = ?";
    
    private static final String SQL_CONTAR_POR_STATUS = 
        "SELECT STATUS, COUNT(*) as total " +
        "FROM " + TABLE_NAME + " " +
        "GROUP BY STATUS";
    
    private static final String SQL_BUSCAR_NAO_ENVIADOS = 
        "SELECT * FROM " + TABLE_NAME + " " +
        "WHERE DATARETORNO IS NULL " +
        "AND DATAENVIO < DATE_SUB(NOW(), INTERVAL 1 HOUR)";
    
    /**
     * Insere um novo registro na tabela selados
     */
    public int inserir(Selado selado) throws DatabaseException {
        logger.info("Inserindo registro na tabela selados: " + selado.getSelo());
        
        return executeUpdate(SQL_INSERIR,
            selado.getNumTipAto(),     // NUMTIPATO
            selado.getIdap(),          // IDAP
            selado.getChave(),         // CHAVE
            selado.getSelo(),          // SELO
            selado.getQrcode(),        // QRCODE
            selado.getRegistro(),      // REGISTRO
            selado.getDataEnvio(),     // DATAENVIO
            selado.getDataRetorno(),   // DATARETORNO
            selado.getRetorno(),       // RETORNO
            selado.getUsuario(),       // USUARIO
            selado.getStatus(),        // STATUS
            selado.getJson()           // JSON
        );
    }
    
    /**
     * Busca registro pelo número do selo
     */
    public Selado buscarPorSelo(String selo) throws DatabaseException {
        List<Map<String, Object>> resultados = executeQuery(SQL_BUSCAR_POR_SELO, selo);
        
        if (resultados.isEmpty()) {
            return null;
        }
        
        return mapToSelado(resultados.get(0));
    }
    
    /**
     * Busca registros pelo número de registro
     */
    public List<Selado> buscarPorRegistro(String registro) throws DatabaseException {
        List<Map<String, Object>> resultados = executeQuery(SQL_BUSCAR_POR_REGISTRO, registro);
        return mapToSelados(resultados);
    }
    
    /**
     * Busca registros por período
     */
    public List<Selado> buscarPorPeriodo(String dataInicio, String dataFim) throws DatabaseException {
        List<Map<String, Object>> resultados = executeQuery(SQL_BUSCAR_POR_PERIODO, dataInicio, dataFim);
        return mapToSelados(resultados);
    }
    
    /**
     * Atualiza retorno de um selo
     */
    public int atualizarRetorno(String selo, String dataRetorno, String retorno, String status) 
            throws DatabaseException {
        
        logger.info("Atualizando retorno do selo: " + selo);
        return executeUpdate(SQL_ATUALIZAR_RETORNO, dataRetorno, retorno, status, selo);
    }
    
    /**
     * Busca registros por status
     */
    public List<Selado> buscarPorStatus(String status) throws DatabaseException {
        List<Map<String, Object>> resultados = executeQuery(SQL_BUSCAR_POR_STATUS, status);
        return mapToSelados(resultados);
    }
    
    /**
     * Busca registro pelo IDAP
     */
    public Selado buscarPorIdap(String idap) throws DatabaseException {
        List<Map<String, Object>> resultados = executeQuery(SQL_BUSCAR_POR_IDAP, idap);
        
        if (resultados.isEmpty()) {
            return null;
        }
        
        return mapToSelado(resultados.get(0));
    }
    
    /**
     * Atualiza QRCode de um selo
     */
    public int atualizarQrcode(String selo, String qrcode) throws DatabaseException {
        return executeUpdate(SQL_ATUALIZAR_QRCODE, qrcode, selo);
    }
    
    /**
     * Conta registros por status
     */
    public Map<String, Integer> contarPorStatus() throws DatabaseException {
        List<Map<String, Object>> resultados = executeQuery(SQL_CONTAR_POR_STATUS);
        
        Map<String, Integer> contagem = new HashMap<>();
        for (Map<String, Object> row : resultados) {
            String status = (String) row.get("STATUS");
            Long total = (Long) row.get("total");
            contagem.put(status, total.intValue());
        }
        
        return contagem;
    }
    
    /**
     * Busca selos não enviados (pendentes de retorno)
     */
    public List<Selado> buscarNaoEnviados() throws DatabaseException {
        List<Map<String, Object>> resultados = executeQuery(SQL_BUSCAR_NAO_ENVIADOS);
        return mapToSelados(resultados);
    }
    
    /**
     * Insere múltiplos registros em lote
     */
    public void inserirEmLote(List<Selado> selados) throws DatabaseException {
        List<TransactionOperation> operations = new ArrayList<>();
        
        for (Selado selado : selados) {
            operations.add(new TransactionOperation(
                SQL_INSERIR,
                selado.getNumTipAto(),
                selado.getIdap(),
                selado.getChave(),
                selado.getSelo(),
                selado.getQrcode(),
                selado.getRegistro(),
                selado.getDataEnvio(),
                selado.getDataRetorno(),
                selado.getRetorno(),
                selado.getUsuario(),
                selado.getStatus()
            ));
        }
        
        if (!operations.isEmpty()) {
            executeTransaction(operations);
            logger.info("Inseridos " + operations.size() + " registros na tabela selados");
        }
    }
    
    /**
     * Verifica se selo já foi registrado
     */
    public boolean existe(String selo) throws DatabaseException {
        try {
            Selado s = buscarPorSelo(selo);
            return s != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Busca último registro por tipo de ato
     */
    public Selado buscarUltimoPorTipoAto(String numTipAto) throws DatabaseException {
        String sql = "SELECT * FROM " + TABLE_NAME + " " +
                    "WHERE NUMTIPATO = ? " +
                    "ORDER BY DATAENVIO DESC " +
                    "LIMIT 1";
        
        List<Map<String, Object>> resultados = executeQuery(sql, numTipAto);
        
        if (resultados.isEmpty()) {
            return null;
        }
        
        return mapToSelado(resultados.get(0));
    }
    
    /**
     * Converte lista de Maps para lista de Selados
     */
    private List<Selado> mapToSelados(List<Map<String, Object>> resultados) {
        List<Selado> selados = new ArrayList<>();
        
        for (Map<String, Object> row : resultados) {
            Selado selado = mapToSelado(row);
            if (selado != null) {
                selados.add(selado);
            }
        }
        
        return selados;
    }
    
    /**
     * Converte Map para objeto Selado
     */
    private Selado mapToSelado(Map<String, Object> row) {
        try {
            Selado selado = new Selado();
            
            // ID auto-increment
            selado.setId(getLong(row, "ID"));
            
            // Informações do selo
            selado.setNumTipAto(getString(row, "NUMTIPATO"));
            selado.setIdap(getString(row, "IDAP"));
            selado.setChave(getString(row, "CHAVE"));
            selado.setSelo(getString(row, "SELO"));
            selado.setQrcode(getString(row, "QRCODE"));
            selado.setRegistro(getString(row, "REGISTRO"));
            
            // Datas
            selado.setDataEnvio(getString(row, "DATAENVIO"));
            selado.setDataRetorno(getString(row, "DATARETORNO"));
            
            // Status e retorno
            selado.setRetorno(getString(row, "RETORNO"));
            selado.setUsuario(getString(row, "USUARIO"));
            selado.setStatus(getString(row, "STATUS"));
            
            // Campos adicionais (se existirem)
            selado.setJson(getString(row, "JSON"));
            
            // REMOVER ou ADICIONAR conforme a classe Selado
            // selado.setImg(getString(row, "IMG")); // Método não existe
            
            return selado;
            
        } catch (Exception e) {
            logger.warning("Erro ao converter Map para Selado: " + e.getMessage());
            return null;
        }
    }
    
    // Métodos auxiliares para conversão segura
    private String getString(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value != null ? value.toString().trim() : "";
    }
    
    private Long getLong(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value != null) {
            try {
                return Long.parseLong(value.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}