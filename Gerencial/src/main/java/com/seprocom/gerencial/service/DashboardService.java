package com.seprocom.gerencial.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Lista todos os atos únicos disponíveis para o filtro.
     */
    public List<String> getAtosDisponiveis() {
        return jdbcTemplate.queryForList(
            "SELECT DISTINCT DESCATO_REC FROM spprot.recibo WHERE DESCATO_REC IS NOT NULL AND DESCATO_REC <> '' ORDER BY DESCATO_REC",
            String.class
        );
    }

    /**
     * @param dataIni Data inicial (formato YYYYMMDD)
     * @param dataFim Data final (formato YYYYMMDD)
     * @param tipo Tipo de agrupamento: "mensal" ou "anual"
     * @return Lista de resultados
     */
    public List<Map<String, Object>> getEstatisticas(String dataIni, String dataFim, List<String> atosSelecionados, String tipo) {
        log.info("Buscando estatísticas de {} até {} (Tipo: {}) com {} atos selecionados", dataIni, dataFim, tipo, atosSelecionados.size());
        
        String format = "anual".equalsIgnoreCase(tipo) ? "%Y" : "%Y-%m";
        
        // Verifica se a base sptabel existe no host
        boolean existeNotas = checkTableExists("sptabel", "fin_reccab");
        
        StringBuilder sql = new StringBuilder();
        
        // Parte 1: Títulos de Protesto
        sql.append("SELECT 'Tabelionato de Protesto' as Serventia, '' as MesAno, '' as Apont, '' as Prot, '' as Canc, '' as Pagos, '' as Ret, '' as Susp, '' as Atos, '' as Arrecadacao, '' as Custeio, '' as FUNDEP, '' as ISS, '' as FUNREJUS_25, '' as FUNREJUS_02, '' as FUNSEG, '' as Selo, '' as Repasses ");
        sql.append("UNION ALL ");
        sql.append("SELECT 'Tabelionato de Protesto', '" + ("anual".equalsIgnoreCase(tipo) ? "Ano" : "Mes/Ano") + "', 'Apont', 'Prot', 'Canc', 'Pagos', 'Ret', 'Susp', 'Atos', 'Arrecadacao', 'Custeio', 'FUNDEP', 'ISS', 'FUNREJUS_25', 'FUNREJUS_02', 'FUNSEG', 'Selo', 'Repasses' ");
        sql.append("UNION ALL ");

        // Montagem dinâmica do CASE para os Atos
        String caseAtos = "0";
        if (atosSelecionados != null && !atosSelecionados.isEmpty()) {
            caseAtos = atosSelecionados.stream()
                .map(ato -> String.format("WHEN r.DESCATO_REC = '%s' THEN 1", ato.replace("'", "''")))
                .collect(Collectors.joining(" ", "CASE ", " ELSE 0 END"));
        }

        sql.append("SELECT ");
        sql.append("    'Tabelionato de Protesto', ");
        sql.append("    DATE_FORMAT(r.dtmovi_rec, '" + format + "') AS MesAno, ");
        sql.append("    (SELECT COUNT(*) FROM ctp001 m WHERE m.controle_001 = 1 AND m.dataapo_001 BETWEEN ? AND ? AND DATE_FORMAT(m.dataapo_001,'" + format + "') = DATE_FORMAT(r.dtmovi_rec,'" + format + "')) as Apont, ");
        sql.append("    (SELECT COUNT(*) FROM ctp001 m WHERE m.controle_001 = 1 AND m.databai_001 BETWEEN ? AND ? AND DATE_FORMAT(m.databai_001,'" + format + "') = DATE_FORMAT(r.dtmovi_rec,'" + format + "')) as Prot, ");
        sql.append("    (SELECT COUNT(*) FROM ctp001 m WHERE m.controle_001 = 1 AND m.datacan_001 BETWEEN ? AND ? AND DATE_FORMAT(m.datacan_001,'" + format + "') = DATE_FORMAT(r.dtmovi_rec,'" + format + "')) as Canc, ");
        sql.append("    (SELECT COUNT(*) FROM ctp001 m WHERE m.controle_001 = 1 AND m.datapag_001 BETWEEN ? AND ? AND DATE_FORMAT(m.datapag_001,'" + format + "') = DATE_FORMAT(r.dtmovi_rec,'" + format + "')) as Pagos, ");
        sql.append("    (SELECT COUNT(*) FROM ctp001 m WHERE m.controle_001 = 1 AND m.dataret_001 BETWEEN ? AND ? AND DATE_FORMAT(m.dataret_001,'" + format + "') = DATE_FORMAT(r.dtmovi_rec,'" + format + "')) as Ret, ");
        sql.append("    (SELECT COUNT(*) FROM ctp001 m WHERE m.controle_001 = 1 AND DATE_FORMAT(STR_TO_DATE(LPAD(m.dtsusta_001,8,'0'),'%d%m%Y'),'" + format + "') = DATE_FORMAT(r.dtmovi_rec,'" + format + "')) as Susp, ");
        sql.append("    SUM(" + caseAtos + ") as SumAtos, ");
        sql.append("    FORMAT(SUM(IFNULL(r.vlrpro_rec, 0) + IFNULL(r.vlrint_rec, 0) + IFNULL(r.vlrcor_rec, 0) + IFNULL(r.vlrdig_rec, 0) + IFNULL(r.vlredi_rec, 0) + IFNULL(r.certidao_rec, 0) + IFNULL(r.positiva_rec, 0) + IFNULL(r.vlrdigit_rec, 0) + IFNULL(r.cancela_rec, 0)), 2, 'pt_BR') as Arred, ");
        sql.append("    FORMAT(IFNULL((SELECT SUM(IFNULL(CAST(CONCAT(SUBSTRING(p.vlrpag_pag,1,12),'.',SUBSTRING(p.vlrpag_pag,-2)) AS DECIMAL(14,2)),0)) FROM pagar p WHERE DATE_FORMAT(CAST(p.dtpagi_pag AS DATE), '" + format + "') = DATE_FORMAT(r.dtmovi_rec, '" + format + "') AND CAST(p.dtpagi_pag AS DATE) BETWEEN ? AND ?),0),2,'pt_BR') as Cust, ");
        sql.append("    FORMAT(SUM(IFNULL(r.vlrass_rec,0) + IFNULL(r.fadep_rec,0)),2,'pt_BR'), ");
        sql.append("    FORMAT(SUM(IFNULL(r.vlriss_rec,0)),2,'pt_BR'), ");
        sql.append("    FORMAT(SUM(IFNULL(r.frj25_rec,0)),2,'pt_BR'), ");
        sql.append("    FORMAT(SUM(IFNULL(r.vlrfrj_rec,0)),2,'pt_BR'), ");
        sql.append("    FORMAT(SUM(IFNULL(r.vlrdist_rec,0)),2,'pt_BR'), ");
        sql.append("    FORMAT(SUM(IFNULL(r.vlrselo_rec,0) + IFNULL(r.vlrselocercan_rec,0)),2,'pt_BR'), ");
        sql.append("    FORMAT(SUM(IFNULL(r.vlrass_rec,0) + IFNULL(r.fadep_rec,0) + IFNULL(r.vlriss_rec,0) + IFNULL(r.frj25_rec,0) + IFNULL(r.vlrfrj_rec,0) + IFNULL(r.vlrdist_rec,0) + IFNULL(r.vlrselo_rec,0) + IFNULL(r.vlrselocercan_rec,0)), 2, 'pt_BR') as Rep ");
        sql.append("FROM spprot.recibo r ");
        sql.append("WHERE r.dtmovi_rec BETWEEN ? AND ? AND (SUBSTRING(r.descr8_rec,1,8) <> 'ANTECIPA' OR r.OBSERVA_REC <> 'CANCELADO') ");
        sql.append("GROUP BY DATE_FORMAT(r.dtmovi_rec, '" + format + "') ");

        if (existeNotas) {
            sql.append("UNION ALL ");
            sql.append("SELECT 'Tabelionato de Notas', '', '', '', '', '', '', '', '', '', '', '', '', 'QtAtos', '', '', '', '' ");
            sql.append("UNION ALL ");
            sql.append("SELECT ");
            sql.append("    'Tabelionato de Notas', ");
            sql.append("    DATE_FORMAT(n.dtrec_rec,'" + format + "'), ");
            sql.append("    '', '', '', '', '', '', ");
            sql.append("    SUM(IF(SUBSTRING(n.obs_rec,1,9)='CANCELADO',0, IF(sc.BASE_CUS='S',ri.qtde_rec,0))), ");
            sql.append("    FORMAT(SUM(IF(SUBSTRING(n.obs_rec,1,9)='CANCELADO',0, IF(sc.BASE_CUS='S',(ri.valor_rec*ri.qtde_rec),0))),2,'pt_BR'), ");
            sql.append("    FORMAT(IFNULL((SELECT SUM(IFNULL(pp.PAG_PAGO,0)) FROM sptabel.par_pagar pp WHERE DATE_FORMAT(pp.PAG_DTPAG, '" + format + "') = DATE_FORMAT(n.dtrec_rec, '" + format + "') AND pp.PAG_DTPAG BETWEEN ? AND ?),0),2,'pt_BR'), ");
            sql.append("    FORMAT(SUM(IF(SUBSTRING(n.obs_rec,1,9)='CANCELADO',0, IF(IFNULL(sc.BASE_CUS,'N')='N',(ri.valor_rec*ri.qtde_rec),0))),2,'pt_BR'), ");
            sql.append("    '', '', '', '', '', '' ");
            sql.append("FROM sptabel.fin_recitem ri ");
            sql.append("JOIN sptabel.ser_custas sc ON ri.codigo_cus = sc.CODIGO_CUS ");
            sql.append("JOIN sptabel.fin_reccab n ON ri.num_rec = n.num_rec ");
            sql.append("WHERE n.dtrec_rec BETWEEN ? AND ? AND sc.HERCULES_CUS='S' AND IFNULL(sc.CLASS_COD,'') <> '9' ");
            sql.append("GROUP BY DATE_FORMAT(n.dtrec_rec,'" + format + "') ");
        }

        // Parâmetros (ajustados conforme a ordem no SQL)
        List<Object> params = new ArrayList<>();
        // Protesto
        params.add(dataIni); params.add(dataFim); // Apont
        params.add(dataIni); params.add(dataFim); // Prot
        params.add(dataIni); params.add(dataFim); // Canc
        params.add(dataIni); params.add(dataFim); // Pagos
        params.add(dataIni); params.add(dataFim); // Ret
        params.add(dataIni); params.add(dataFim); // Cust
        params.add(dataIni); params.add(dataFim); // Where Protesto
        
        if (existeNotas) {
           params.add(dataIni); params.add(dataFim); // Cust Notas
           params.add(dataIni); params.add(dataFim); // Where Notas
        }

        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    private boolean checkTableExists(String base, String tabela) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?", 
                Integer.class, base, tabela);
            return count != null && count > 0;
        } catch (Exception e) {
            log.warn("Base ou tabela {}.{} não encontrada ou sem acesso.", base, tabela);
            return false;
        }
    }
}
