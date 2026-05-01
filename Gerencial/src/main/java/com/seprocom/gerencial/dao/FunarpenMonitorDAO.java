package com.seprocom.gerencial.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@Repository
public class FunarpenMonitorDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Lista selos que não retornaram (pendentes ou erro)
     */
    public List<Map<String, Object>> listarSelosNaoRetornados(String dIni, String dFim, Integer offset, Integer limit, boolean incluirTransmitidos, String statusFiltro) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT sd.ID, sd.SELO, sd.DATAENVIO, sd.DATARETORNO, sd.RETORNO, sd.STATUS, sd.IDAP, sd.NUMTIPATO, sd.JSON, s.tiposelo_sel, s.pedido_sel, s.numpedido, s.dataped_sel ");
        sql.append("FROM selados sd LEFT JOIN selos s ON s.selo_sel = sd.SELO ");
        sql.append("WHERE s.versao = '112' ");

        if (statusFiltro != null && !statusFiltro.isEmpty()) {
            if ("PENDENTE".equalsIgnoreCase(statusFiltro)) {
                sql.append("AND (sd.STATUS IS NULL OR sd.STATUS = '' OR sd.STATUS = 'PENDENTE') ");
            } else {
                sql.append("AND sd.STATUS = '").append(statusFiltro).append("' ");
            }
        } else if (!incluirTransmitidos) {
            sql.append("AND (sd.STATUS <> 'SUCESSO' OR sd.RETORNO IS NULL OR sd.RETORNO = '') ");
        }

        if (dIni != null) sql.append("AND CAST(sd.DATAENVIO AS DATE) >= '").append(dIni).append("' ");
        if (dFim != null) sql.append("AND CAST(sd.DATAENVIO AS DATE) <= '").append(dFim).append("' ");

        sql.append("ORDER BY sd.DATAENVIO DESC ");
        
        if (limit != null) {
            sql.append("LIMIT ").append(limit);
            if (offset != null) sql.append(" OFFSET ").append(offset);
        } else {
            sql.append("LIMIT 1000");
        }

        return jdbcTemplate.queryForList(sql.toString());
    }

    /**
     * Retorna contagem de estoque agrupada por tipo
     */
    public Map<String, Integer> contarEstoquePorTipo() {
        String sql = "SELECT t.sigle AS sig, COUNT(DISTINCT s.selo_sel) AS qtd " +
                     "FROM selos s INNER JOIN selo_tipo t ON t.tipo = s.tiposelo_sel " +
                     "WHERE s.tipo_sel = '0000' AND s.versao = '112' " +
                     "GROUP BY t.sigle ORDER BY t.sigle";
        
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            result.put((String) row.get("sig"), ((Number) row.get("qtd")).intValue());
        }
        return result;
    }

    /**
     * Kpis gerais para os cards do dashboard
     */
    public Map<String, Integer> getKpisDashboard() {
        Map<String, Integer> kpis = new LinkedHashMap<>();
        
        // Total Estoque
        String sqlEstoque = "SELECT COUNT(DISTINCT s.selo_sel) FROM selos s WHERE s.tipo_sel = '0000' AND s.versao = '112'";
        kpis.put("totalEstoque", jdbcTemplate.queryForObject(sqlEstoque, Integer.class));

        // Utilizados Hoje
        String sqlUtilizados = "SELECT COUNT(*) FROM selados WHERE DATE(DATAENVIO) = CURDATE()";
        kpis.put("utilizadosHoje", jdbcTemplate.queryForObject(sqlUtilizados, Integer.class));

        // Pendentes
        String sqlPendentes = "SELECT COUNT(*) FROM selados sd INNER JOIN selos s ON s.selo_sel = sd.SELO " +
                             "WHERE (sd.STATUS IS NULL OR sd.STATUS = '' OR sd.STATUS = 'PENDENTE') " +
                             "AND (sd.RETORNO IS NULL OR sd.RETORNO = '') AND s.versao = '112'";
        kpis.put("pendentes", jdbcTemplate.queryForObject(sqlPendentes, Integer.class));

        // Erros
        String sqlErros = "SELECT COUNT(*) FROM selados sd INNER JOIN selos s ON s.selo_sel = sd.SELO " +
                         "WHERE (sd.STATUS LIKE '%false%' OR sd.STATUS LIKE '%erro%') AND s.versao = '112'";
        kpis.put("erros", jdbcTemplate.queryForObject(sqlErros, Integer.class));

        return kpis;
    }

    /**
     * Retorna os parâmetros de configuração do cartório (PFX, Titular, etc)
     */
    public Map<String, Object> getParametrosCartorio() {
        String sql = "SELECT CAMINHOCERTIFICADO_PAR, SENHACERTIFICADO_PAR, TITULAR_PAR FROM parametros WHERE CODIGO_PAR = 1";
        return jdbcTemplate.queryForMap(sql);
    }
}
