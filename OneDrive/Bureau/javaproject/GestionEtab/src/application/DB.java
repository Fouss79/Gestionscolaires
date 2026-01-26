package application;

import java.sql.Connection;
import java.sql.DriverManager;

public class DB {

    private static final String URL =
        "jdbc:mysql://localhost:3306/gestionecole?useSSL=false&serverTimezone=UTC";

    private static final String USER = "root";   // ⚠️ PAS root@localhost
    private static final String PASSWORD = "";   // mets ton mot de passe si tu en as

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
