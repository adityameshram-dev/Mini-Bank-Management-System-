import java.sql.*;

public class DBConnection {

    public static Connection getConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/bank";
            String user = "root";
            String pass = "root@2026";

            Connection con = DriverManager.getConnection(url, user, pass);
            return con;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}