package cinema.H2;

import cinema.model.Hall;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class H2HallRepository {
    private static final String URL = "jdbc:h2:./data/CINEMA_DB;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";

    public static void initialize() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            // Hall tablosu
            stmt.execute("CREATE TABLE IF NOT EXISTS halls (" +
                    "hall_name VARCHAR(100) PRIMARY KEY, " +
                    "row_count INT NOT NULL, " +
                    "column_count INT NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveHall(Hall hall) {
        String sql = "INSERT INTO halls (hall_name, row_count, column_count) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hall.getHallName());
            pstmt.setInt(2, hall.getRowCount());
            pstmt.setInt(3, hall.getColumnCount());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Hall getHall(String name) {
        String sql = "SELECT * FROM halls WHERE hall_name = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Hall(
                        rs.getString("hall_name"),
                        rs.getInt("row_count"),
                        rs.getInt("column_count")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Hall> getAllHalls() {
        List<Hall> halls = new ArrayList<>();
        String sql = "SELECT * FROM halls";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                halls.add(new Hall(
                        rs.getString("hall_name"),
                        rs.getInt("row_count"),
                        rs.getInt("column_count")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return halls;
    }

    public static void deleteHall(String name) {
        String sql = "DELETE FROM halls WHERE hall_name = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}