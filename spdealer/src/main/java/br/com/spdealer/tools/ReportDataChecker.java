package br.com.spdealer.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReportDataChecker {
    public static void main(String[] args) {
        try {
            String dbUrl = System.getProperty("db.url");
            String dbUser = System.getProperty("db.user");
            String dbPass = System.getProperty("db.pass");
            if (dbUrl == null) dbUrl = System.getenv("DB_URL");
            if (dbUser == null) dbUser = System.getenv("DB_USER");
            if (dbPass == null) dbPass = System.getenv("DB_PASS");
            if (dbUrl != null && dbUrl.startsWith("jdbc:mysql://")) {
                dbUrl = dbUrl.replaceFirst("jdbc:mysql://", "jdbc:mariadb://");
            }
            if (dbUrl == null || dbUser == null) {
                System.err.println("DB connection info not provided (DB_URL/DB_USER required)");
                System.exit(2);
            }
            try (Connection c = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
                String filial = System.getenv().getOrDefault("REPORT_FILIAL", "001");
                String date = "2025-12-15"; // ISO date for SQL

                String sqlRecCount = "SELECT COUNT(*) as cnt, COALESCE(SUM(vlrsal_rec),0) as total FROM receber WHERE (status_rec IS NULL OR status_rec = '') AND vlrsal_rec > 0 AND filial_rec = ? AND dtvenci_rec = ?";
                try (PreparedStatement psRecCount = c.prepareStatement(sqlRecCount)) {
                    psRecCount.setString(1, filial);
                    psRecCount.setString(2, date);
                    try (ResultSet rs = psRecCount.executeQuery()) {
                        if (rs.next()) {
                            System.out.println("receber.count=" + rs.getLong("cnt") + " total=" + rs.getBigDecimal("total"));
                        }
                    }
                }

                String sqlPagCount = "SELECT COUNT(*) as cnt, COALESCE(SUM(vlrsal_pag),0) as total FROM pagar WHERE (status_pag IS NULL OR status_pag = '') AND vlrsal_pag > 0 AND filial_pag = ? AND dtvenci_pag = ?";
                
                // Also check movement date (dtmovi_*) counts for same day
                String sqlRecDtmovi = "SELECT COUNT(*) as cnt FROM receber WHERE dtmovi_rec = ? AND filial_rec = ?";
                try (PreparedStatement psRecDtm = c.prepareStatement(sqlRecDtmovi)) {
                    psRecDtm.setString(1, date);
                    psRecDtm.setString(2, filial);
                    try (ResultSet rs = psRecDtm.executeQuery()) {
                        if (rs.next()) {
                            System.out.println("receber.dtmovi.count=" + rs.getLong("cnt"));
                        }
                    }
                }

                String sqlPagDtmovi = "SELECT COUNT(*) as cnt FROM pagar WHERE dtmovi_pag = ? AND filial_pag = ?";
                try (PreparedStatement psPagDtm = c.prepareStatement(sqlPagDtmovi)) {
                    psPagDtm.setString(1, date);
                    psPagDtm.setString(2, filial);
                    try (ResultSet rs = psPagDtm.executeQuery()) {
                        if (rs.next()) {
                            System.out.println("pagar.dtmovi.count=" + rs.getLong("cnt"));
                        }
                    }
                }
                try (PreparedStatement psPagCount = c.prepareStatement(sqlPagCount)) {
                    psPagCount.setString(1, filial);
                    psPagCount.setString(2, date);
                    try (ResultSet rs = psPagCount.executeQuery()) {
                        if (rs.next()) {
                            System.out.println("pagar.count=" + rs.getLong("cnt") + " total=" + rs.getBigDecimal("total"));
                        }
                    }
                }

                // Simple aging buckets for receber (example ranges)
                String sqlBuckets =
                    "SELECT '0-30' as bucket, COALESCE(SUM(vlrsal_rec),0) as total FROM receber r WHERE (r.status_rec IS NULL OR r.status_rec='') AND r.vlrsal_rec>0 AND r.filial_rec=? AND DATEDIFF(r.dtvenci_rec, ?) BETWEEN 0 AND 30"
                    + " UNION ALL SELECT '31-60', COALESCE(SUM(vlrsal_rec),0) FROM receber r WHERE (r.status_rec IS NULL OR r.status_rec='') AND r.vlrsal_rec>0 AND r.filial_rec=? AND DATEDIFF(r.dtvenci_rec, ?) BETWEEN 31 AND 60"
                    + " UNION ALL SELECT '61-90', COALESCE(SUM(vlrsal_rec),0) FROM receber r WHERE (r.status_rec IS NULL OR r.status_rec='') AND r.vlrsal_rec>0 AND r.filial_rec=? AND DATEDIFF(r.dtvenci_rec, ?) BETWEEN 61 AND 90"
                    + " UNION ALL SELECT '91-120', COALESCE(SUM(vlrsal_rec),0) FROM receber r WHERE (r.status_rec IS NULL OR r.status_rec='') AND r.vlrsal_rec>0 AND r.filial_rec=? AND DATEDIFF(r.dtvenci_rec, ?) BETWEEN 91 AND 120"
                    + " UNION ALL SELECT '>120', COALESCE(SUM(vlrsal_rec),0) FROM receber r WHERE (r.status_rec IS NULL OR r.status_rec='') AND r.vlrsal_rec>0 AND r.filial_rec=? AND DATEDIFF(r.dtvenci_rec, ?) > 120";

                try (PreparedStatement psBuckets = c.prepareStatement(sqlBuckets)) {
                    // set params sequence: filial,date repeated 5 times
                    for (int i=1;i<=10;i+=2) {
                        psBuckets.setString(i, filial);
                        psBuckets.setString(i+1, date);
                    }
                    try (ResultSet rs = psBuckets.executeQuery()) {
                        System.out.println("=== receber buckets ===");
                        while (rs.next()) {
                            System.out.println(rs.getString("bucket") + ": " + rs.getBigDecimal("total"));
                        }
                    }
                }

                // Show columns of receber and pagar for diagnosis
                System.out.println("=== receber columns ===");
                try (PreparedStatement psShowRec = c.prepareStatement("SHOW COLUMNS FROM receber")) {
                    try (ResultSet rs = psShowRec.executeQuery()) {
                        while (rs.next()) {
                            System.out.println(rs.getString(1));
                        }
                    }
                }
                System.out.println("=== pagar columns ===");
                try (PreparedStatement psShowPag = c.prepareStatement("SHOW COLUMNS FROM pagar")) {
                    try (ResultSet rs = psShowPag.executeQuery()) {
                        while (rs.next()) {
                            System.out.println(rs.getString(1));
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
