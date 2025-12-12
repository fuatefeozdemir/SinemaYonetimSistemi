package cinema.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {

    private static final String URL = "jdbc:sqlite:cinema.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);

            if (conn != null) {
                initializeDatabase(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private static void initializeDatabase(Connection conn) {
        try (Statement stmt = conn.createStatement()) {

            String sqlCreateUsers = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT NOT NULL UNIQUE, " +
                    "password TEXT NOT NULL, " +
                    "role TEXT NOT NULL)";
            stmt.execute(sqlCreateUsers);

            String sqlSeedAdmin = "INSERT OR IGNORE INTO users (username, password, role) " +
                    "VALUES ('admin', '123', 'admin')";
            stmt.execute(sqlSeedAdmin);

            String sqlSeedCustomer = "INSERT OR IGNORE INTO users (username, password, role) " +
                    "VALUES ('customer', '123', 'customer')";
            stmt.execute(sqlSeedCustomer);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}