import java.sql.*;

public class ShowJson {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mariadb://100.102.13.23:3306/sptabel";
        try (Connection conn = DriverManager.getConnection(url, "root", "k15720")) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT SELO, JSON FROM selados WHERE SELO IN ('SFTN2.cJ8RN.Mwcwe-ER4fM.1122q', 'SFTN2.cJURN.Mwcwe-URDfM.1122q', 'SFTN1.ZGoAb.mE3mk-FmMID.1196p')")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        System.out.println("SELO: " + rs.getString(1));
                        System.out.println("JSON: " + rs.getString(2));
                        System.out.println("-----------------------------------");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao conectar no banco do .127 ou .23: " + e.getMessage());
            // Tentar no outro IP se falhar
            String url127 = "jdbc:mariadb://100.75.153.127:3306/sptabel";
            try (Connection conn = DriverManager.getConnection(url127, "root", "k15720")) {
                 try (PreparedStatement ps = conn.prepareStatement("SELECT SELO, JSON FROM selados WHERE SELO = 'SFTN1.ZGoAb.mE3mk-FmMID.1196p'")) {
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            System.out.println("SELO: " + rs.getString(1));
                            System.out.println("JSON: " + rs.getString(2));
                            System.out.println("-----------------------------------");
                        }
                    }
                }
            } catch (Exception e2) {
                System.err.println("Erro no .127 também: " + e2.getMessage());
            }
        }
    }
}
