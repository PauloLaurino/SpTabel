package com.monitor.sptabel.util;

import com.monitor.funarpen.util.DbUtil;

import java.sql.*;

/**
 * Utilitário para construir JSON de "selados" a partir das tabelas do banco sptabel.
 *
 * Observações:
 * - As queries são genéricas e usam nomes de tabela conforme informado: atos, not_1, imoveis,
 *   imovpartes, fin_reccab, fin_recitem, selados.
 * - Ajuste colunas/joins conforme seu esquema real quando necessário.
 */
public class SeloJsonBuilder {

    public static String buildSeladoJsonByAtoId(long atoId) throws SQLException {
        try (Connection c = DbUtil.getConnection()) {
            return buildSeladoJson(c, atoId);
        }
    }

    public static String buildSeladoJson(Connection c, long atoId) throws SQLException {
        StringBuilder out = new StringBuilder();
        out.append('{');
        appendObject(out, "ato", () -> queryOneAsJson(c, "SELECT * FROM atos WHERE id = ?", atoId));
        out.append(',');
        appendArray(out, "partes", () -> queryAsJsonArray(c, "SELECT * FROM not_1 WHERE ato_id = ?", atoId));
        out.append(',');
        appendArray(out, "imoveis", () -> queryAsJsonArray(c, "SELECT * FROM imoveis WHERE ato_id = ?", atoId));
        out.append(',');
        appendArray(out, "imovpartes", () -> queryAsJsonArray(c, "SELECT * FROM imovpartes WHERE imovel_id IN (SELECT id FROM imoveis WHERE ato_id = ?)", atoId));
        out.append(',');
        appendObject(out, "recibo", () -> queryOneAsJson(c, "SELECT * FROM fin_reccab WHERE ato_id = ?", atoId));
        out.append(',');
        // se houver reccab, puxar items
        Long reccabId = queryLong(c, "SELECT id FROM fin_reccab WHERE ato_id = ? LIMIT 1", atoId);
        if (reccabId != null) {
            appendArray(out, "recitems", () -> queryAsJsonArray(c, "SELECT * FROM fin_recitem WHERE reccab_id = ?", reccabId));
            out.append(',');
        } else {
            appendArray(out, "recitems", () -> "[]");
            out.append(',');
        }
        appendArray(out, "selados", () -> queryAsJsonArray(c, "SELECT * FROM selados WHERE ato_id = ?", atoId));
        out.append('}');
        return out.toString();
    }

    // Helpers
    private static interface JsonSupplier { String get() throws SQLException; }

    private static void appendObject(StringBuilder sb, String name, JsonSupplier supplier) throws SQLException {
        sb.append('"').append(name).append('"').append(':');
        String j = supplier.get();
        sb.append(j == null ? "null" : j);
    }

    private static void appendArray(StringBuilder sb, String name, JsonSupplier supplier) throws SQLException {
        sb.append('"').append(name).append('"').append(':');
        String j = supplier.get();
        sb.append(j == null ? "[]" : j);
    }

    private static Long queryLong(Connection c, String sql, Object param) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }

    private static String queryOneAsJson(Connection c, String sql, Object param) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return "null";
                return rowToJson(rs);
            }
        }
    }

    private static String queryAsJsonArray(Connection c, String sql, Object param) throws SQLException {
        StringBuilder a = new StringBuilder();
        a.append('[');
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) a.append(','); first = false;
                    a.append(rowToJson(rs));
                }
            }
        }
        a.append(']');
        return a.toString();
    }

    private static String rowToJson(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();
        StringBuilder o = new StringBuilder();
        o.append('{');
        for (int i = 1; i <= cols; i++) {
            if (i > 1) o.append(',');
            String col = md.getColumnLabel(i);
            Object val = rs.getObject(i);
            o.append('"').append(escape(col)).append('"').append(':');
            if (val == null) o.append("null");
            else if (val instanceof Number) o.append(val.toString());
            else {
                String s = val.toString();
                o.append('"').append(escape(s)).append('"');
            }
        }
        o.append('}');
        return o.toString();
    }

    private static String escape(String s) {
        if (s == null) return null;
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
