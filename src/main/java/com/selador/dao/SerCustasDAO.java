package com.selador.dao;

import com.selador.model.SerCustas;
import com.selador.exception.DatabaseException;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;

public class SerCustasDAO extends BaseDAO {
    
    private static final Logger logger = Logger.getLogger(SerCustasDAO.class.getName());
    private static final String TABLE_NAME = "ser_custas";
    
    private static final String SQL_LISTAR_TODOS = "SELECT * FROM " + TABLE_NAME + " ORDER BY CODIGO_CUS";
    private static final String SQL_BUSCAR_POR_CODIGO = "SELECT * FROM " + TABLE_NAME + " WHERE SISTEMA_CUS = ? AND CODIGO_CUS = ?";
    private static final String SQL_LISTAR_POR_MODULO = "SELECT * FROM " + TABLE_NAME + " WHERE MODULO_CUS = ? ORDER BY CODIGO_CUS";
    private static final String SQL_LISTAR_ATOS_PRINCIPAIS = "SELECT * FROM " + TABLE_NAME + " WHERE MODULO_CUS = 'N' AND AGREGADO_CUS != 'S' ORDER BY CODIGO_CUS";
    private static final String SQL_LISTAR_ENCARGOS = "SELECT * FROM " + TABLE_NAME + " WHERE MODULO_CUS = 'C' ORDER BY CODIGO_CUS";
    private static final String SQL_LISTAR_POR_TIPOATO = "SELECT * FROM " + TABLE_NAME + " WHERE TIPOATO_CUS = ? ORDER BY CODIGO_CUS";
    private static final String SQL_BUSCAR_POR_DESCRICAO = "SELECT * FROM " + TABLE_NAME + " WHERE DESCR_CUS LIKE ? ORDER BY CODIGO_CUS";
    private static final String SQL_CONTAR_POR_MODULO = "SELECT MODULO_CUS, COUNT(*) as total FROM " + TABLE_NAME + " GROUP BY MODULO_CUS";
    private static final String SQL_BUSCAR_IMOVEIS = "SELECT * FROM protimoveis WHERE MATRIC_IMOP LIKE ? OR ENDERECO_IMOP LIKE ? OR BAIRRO_IMOP LIKE ? OR CIDADE_IMOP LIKE ? ORDER BY MATRIC_IMOP LIMIT 50";
    private static final String SQL_BUSCAR_PARTES = "SELECT * FROM imovpartes WHERE MATRIC_IMOP = ? ORDER BY QUALIDIC_IMOP, NOME_IMOP";
    
    public List<SerCustas> listarTodos() throws DatabaseException {
        logger.info("Listando todas as custas");
        List<Map<String, Object>> resultados = executeQuery(SQL_LISTAR_TODOS);
        return mapToSerCustasList(resultados);
    }
    
    public SerCustas buscarPorCodigo(String sistemaCus, int codigoCus) throws DatabaseException {
        logger.info("Buscando custa por codigo: " + sistemaCus + "/" + codigoCus);
        List<Map<String, Object>> resultados = executeQuery(SQL_BUSCAR_POR_CODIGO, sistemaCus, codigoCus);
        if (resultados.isEmpty()) return null;
        return mapToSerCustas(resultados.get(0));
    }
    
    public List<SerCustas> listarPorModulo(String moduloCus) throws DatabaseException {
        logger.info("Listando custas por modulo: " + moduloCus);
        List<Map<String, Object>> resultados = executeQuery(SQL_LISTAR_POR_MODULO, moduloCus);
        return mapToSerCustasList(resultados);
    }
    
    public List<SerCustas> listarAtosPrincipais() throws DatabaseException {
        logger.info("Listando atos principais do tabelionato de notas");
        List<Map<String, Object>> resultados = executeQuery(SQL_LISTAR_ATOS_PRINCIPAIS);
        return mapToSerCustasList(resultados);
    }
    
    public List<SerCustas> listarEncargos() throws DatabaseException {
        logger.info("Listando encargos");
        List<Map<String, Object>> resultados = executeQuery(SQL_LISTAR_ENCARGOS);
        return mapToSerCustasList(resultados);
    }
    
    public List<SerCustas> listarPorTipoAto(int tipoatoCus) throws DatabaseException {
        logger.info("Listando custas por tipo de ato: " + tipoatoCus);
        List<Map<String, Object>> resultados = executeQuery(SQL_LISTAR_POR_TIPOATO, tipoatoCus);
        return mapToSerCustasList(resultados);
    }
    
    public List<SerCustas> buscarPorDescricao(String descricao) throws DatabaseException {
        logger.info("Buscando custas por descricao: " + descricao);
        List<Map<String, Object>> resultados = executeQuery(SQL_BUSCAR_POR_DESCRICAO, "%" + descricao + "%");
        return mapToSerCustasList(resultados);
    }
    
    public Map<String, Integer> contarPorModulo() throws DatabaseException {
        List<Map<String, Object>> resultados = executeQuery(SQL_CONTAR_POR_MODULO);
        Map<String, Integer> contagem = new HashMap<>();
        for (Map<String, Object> row : resultados) {
            String modulo = (String) row.get("MODULO_CUS");
            Long total = (Long) row.get("total");
            contagem.put(modulo, total.intValue());
        }
        return contagem;
    }
    
    public SerCustas buscarEncargoPorCodigo(int codigoCus) throws DatabaseException {
        return buscarPorCodigo("N", codigoCus);
    }
    
    private List<SerCustas> mapToSerCustasList(List<Map<String, Object>> resultados) {
        List<SerCustas> lista = new ArrayList<>();
        for (Map<String, Object> row : resultados) {
            SerCustas custa = mapToSerCustas(row);
            if (custa != null) lista.add(custa);
        }
        return lista;
    }
    
    private SerCustas mapToSerCustas(Map<String, Object> row) {
        try {
            SerCustas custa = new SerCustas();
            custa.setSistemaCus(getString(row, "SISTEMA_CUS"));
            custa.setCodigoCus(getInt(row, "CODIGO_CUS"));
            custa.setModuloCus(getString(row, "MODULO_CUS"));
            custa.setTipoatoCus(getInt(row, "TIPOATO_CUS"));
            custa.setClassCus(getString(row, "CLASS_CUS"));
            custa.setDescrCus(getString(row, "DESCR_CUS"));
            custa.setQtdeCus(getInt(row, "QTDE_CUS"));
            custa.setValorCus(getBigDecimal(row, "VALOR_CUS"));
            custa.setInformCus(getString(row, "INFOR_CUS"));
            custa.setQtdevrcCus(getBigDecimal(row, "QTDEVRC_CUS"));
            custa.setAliqCus(getBigDecimal(row, "ALIQ_CUS"));
            custa.setAliqfrjCus(getBigDecimal(row, "ALIQFRJ_CUS"));
            custa.setAvaliadoCus(getString(row, "AVALIADO_CUS"));
            custa.setBaseCus(getString(row, "BASE_CUS"));
            custa.setAgregadoCus(getString(row, "AGREGADO_CUS"));
            custa.setIsentoCus(getString(row, "ISENTO_CUS"));
            custa.setHerculesCus(getString(row, "HERCULES_CUS"));
            custa.setHercqtdeCus(getString(row, "HERCQTDE_CUS"));
            custa.setClassCod(getString(row, "CLASS_COD"));
            custa.setTagCus(getString(row, "TAG_CUS"));
            return custa;
        } catch (Exception e) {
            logger.warning("Erro ao converter Map para SerCustas: " + e.getMessage());
            return null;
        }
    }
    
    private String getString(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value != null ? value.toString().trim() : "";
    }
    
    private int getInt(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value instanceof Number) return ((Number) value).intValue();
        else if (value != null) {
            try { return Integer.parseInt(value.toString()); }
            catch (NumberFormatException e) { return 0; }
        }
        return 0;
    }
    
    private BigDecimal getBigDecimal(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value instanceof BigDecimal) return (BigDecimal) value;
        else if (value instanceof Number) return BigDecimal.valueOf(((Number) value).doubleValue());
        else if (value != null) {
            try { return new BigDecimal(value.toString()); }
            catch (NumberFormatException e) { return BigDecimal.ZERO; }
        }
        return BigDecimal.ZERO;
    }
    
    public List<Map<String, Object>> listarProtocolos() throws DatabaseException {
        logger.info("Listando protocolos");
        String sql = "SELECT codigo_pro, nome_apres_pro FROM protocolo ORDER BY nome_apres_pro";
        List<Map<String, Object>> resultados = executeQuery(sql);
        List<Map<String, Object>> protocolos = new ArrayList<>();
        for (Map<String, Object> row : resultados) {
            Map<String, Object> protocolo = new HashMap<>();
            protocolo.put("codigo_pro", getString(row, "codigo_pro"));
            protocolo.put("nome_apres_pro", getString(row, "nome_apres_pro"));
            protocolos.add(protocolo);
        }
        logger.info("Protocolos listados: " + protocolos.size());
        return protocolos;
    }
    
    public List<Map<String, Object>> listarImoveisPorProtocolo(String protocolo) throws DatabaseException {
        logger.info("Listando imoveis para protocolo: " + protocolo);
        String sql = "SELECT * FROM protimoveis WHERE protocolo_pro = ? ORDER BY MATRIC_IMOP";
        List<Map<String, Object>> resultados = executeQuery(sql, protocolo);
        logger.info("Imoveis listados para protocolo: " + resultados.size());
        return resultados;
    }
    
    public List<Map<String, Object>> buscarImoveis(String termo) throws DatabaseException {
        logger.info("Buscando imoveis com termo: " + termo);
        String termoBusca = termo != null ? "%" + termo + "%" : "%%";
        List<Map<String, Object>> resultados = executeQuery(SQL_BUSCAR_IMOVEIS, termoBusca, termoBusca, termoBusca, termoBusca);
        return resultados;
    }
    
    public List<Map<String, Object>> buscarPartes(String imovelId) throws DatabaseException {
        logger.info("Buscando partes do imovel: " + imovelId);
        List<Map<String, Object>> resultados = executeQuery(SQL_BUSCAR_PARTES, imovelId);
        return resultados;
    }
    
    /**
     * Busca dados completos de um protocolo para cálculo de custas
     * @param codigoProtocolo Código do protocolo
     * @return Mapa com dados do protocolo ou null se não encontrado
     */
    public Map<String, Object> buscarDadosProtocolo(String codigoProtocolo) throws DatabaseException {
        logger.info("Buscando dados do protocolo: " + codigoProtocolo);
        
        // Busca dados do protocolo - tabelas: protocolo, protpartes, fin_recitem
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("  p.CODIGO_PRO, ");
        sql.append("  p.SERVICO_PRO, ");
        sql.append("  p.CODTIPOATO_PRO, ");
        sql.append("  p.LIVRO_PRO, ");
        sql.append("  p.FOLHA_PRO, ");
        sql.append("  p.LETRA_PRO, ");
        sql.append("  p.VLRDECLAR_PRO, ");
        sql.append("  p.VLRAVALIADO_PRO, ");
        sql.append("  p.NUMREC_PRO, ");
        sql.append("  p.EMOLUMENTO_PRO, ");
        sql.append("  p.DATA_PRO, ");
        sql.append("  p.NOME_APRES_PRO ");
        sql.append("FROM protocolo p ");
        sql.append("WHERE p.CODIGO_PRO = ?");
        
        List<Map<String, Object>> resultados = executeQuery(sql.toString(), codigoProtocolo);
        
        if (resultados.isEmpty()) {
            logger.warning("Protocolo não encontrado: " + codigoProtocolo);
            return null;
        }
        
        Map<String, Object> protocolo = resultados.get(0);
        
        // Busca quantidade de partes
        String sqlPartes = "SELECT COUNT(*) as total FROM protpartes WHERE CODIGO_PRO = ?";
        List<Map<String, Object>> partesResult = executeQuery(sqlPartes, codigoProtocolo);
        if (!partesResult.isEmpty()) {
            protocolo.put("quantidade_partes", partesResult.get(0).get("total"));
        }
        
        // Busca imóveis relacionados ao protocolo
        String sqlImoveis = "SELECT COUNT(*) as total FROM protimoveis WHERE protocolo_pro = ?";
        List<Map<String, Object>> imoveisResult = executeQuery(sqlImoveis, codigoProtocolo);
        if (!imoveisResult.isEmpty()) {
            protocolo.put("quantidade_imoveis", imoveisResult.get(0).get("total"));
        }
        
        // Busca dados financeiros do recibo (fin_recitem)
        if (protocolo.get("NUMREC_PRO") != null) {
            String numRec = protocolo.get("NUMREC_PRO").toString();
            
            // FADEP (código 901)
            String sqlFadep = "SELECT SUM(qtde_rec * valor_rec) as valor FROM fin_recitem WHERE num_rec = ? AND codigo_cus = 901";
            List<Map<String, Object>> fadepResult = executeQuery(sqlFadep, numRec);
            if (!fadepResult.isEmpty() && fadepResult.get(0).get("valor") != null) {
                protocolo.put("fadep", fadepResult.get(0).get("valor"));
            }
            
            // FUNARPEN (códigos 903, 908, 909)
            String sqlFunarpen = "SELECT SUM(qtde_rec * valor_rec) as valor FROM fin_recitem WHERE num_rec = ? AND codigo_cus IN (903, 908, 909)";
            List<Map<String, Object>> funarpenResult = executeQuery(sqlFunarpen, numRec);
            if (!funarpenResult.isEmpty() && funarpenResult.get(0).get("valor") != null) {
                protocolo.put("funarpen", funarpenResult.get(0).get("valor"));
            }
            
            // ISS (código 904)
            String sqlIss = "SELECT valor_rec as valor FROM fin_recitem WHERE num_rec = ? AND codigo_cus = 904 LIMIT 1";
            List<Map<String, Object>> issResult = executeQuery(sqlIss, numRec);
            if (!issResult.isEmpty() && issResult.get(0).get("valor") != null) {
                protocolo.put("iss", issResult.get(0).get("valor"));
            }
            
            // DISTRIB (código 905)
            String sqlDistrib = "SELECT valor_rec as valor FROM fin_recitem WHERE num_rec = ? AND codigo_cus = 905 LIMIT 1";
            List<Map<String, Object>> distribResult = executeQuery(sqlDistrib, numRec);
            if (!distribResult.isEmpty() && distribResult.get(0).get("valor") != null) {
                protocolo.put("distrib", distribResult.get(0).get("valor"));
            }
            
            // FUNREJUS (código 907)
            String sqlFunrejus = "SELECT valor_rec as valor FROM fin_recitem WHERE num_rec = ? AND codigo_cus = 907 LIMIT 1";
            List<Map<String, Object>> funrejusResult = executeQuery(sqlFunrejus, numRec);
            if (!funrejusResult.isEmpty() && funrejusResult.get(0).get("valor") != null) {
                protocolo.put("funrejus", funrejusResult.get(0).get("valor"));
            }
            
            // Emolumentos (base)
            String sqlEmol = "SELECT SUM(qtde_rec * valor_rec) as valor FROM fin_recitem " +
                           "INNER JOIN ser_custas ON fin_recitem.codigo_cus = ser_custas.CODIGO_CUS " +
                           "WHERE num_rec = ? AND ser_custas.SISTEMA_CUS = 'N' AND ser_custas.BASE_CUS = 'S'";
            List<Map<String, Object>> emolResult = executeQuery(sqlEmol, numRec);
            if (!emolResult.isEmpty() && emolResult.get(0).get("valor") != null) {
                protocolo.put("emolumentos", emolResult.get(0).get("valor"));
            }
        }
        
        logger.info("Dados do protocolo " + codigoProtocolo + " encontrados: " + protocolo.keySet());
        return protocolo;
    }
}

