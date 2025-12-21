package cinema.H2;

import cinema.model.Hall;
import cinema.repository.HallRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class H2HallRepository implements HallRepository {
    private static final String URL = "jdbc:h2:./data/CINEMA_DB;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";

    @Override
    public void initialize() {
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

    @Override
    public void saveHall(Hall hall) {
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

    @Override
    public Hall getHall(String name) {
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

    @Override
    public void updateHall(String oldName, String newName, int rows, int cols) {
        // Aynı bağlantı (Connection) üzerinden iki tabloyu da güncelleyeceğiz
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false); // İşlemi bir bütün (Transaction) olarak başlat

            try {
                // 1. ADIM: Önce bağımlı tablodaki (Sessions) referansları güncelle
                String updateSessionsSql = "UPDATE sessions SET hall_name = ? WHERE hall_name = ?";
                try (PreparedStatement pstmt1 = conn.prepareStatement(updateSessionsSql)) {
                    pstmt1.setString(1, newName);
                    pstmt1.setString(2, oldName);
                    pstmt1.executeUpdate();
                }

                // 2. ADIM: Şimdi ana tablodaki (Halls) ismi ve boyutları güncelle
                String updateHallsSql = "UPDATE halls SET hall_name = ?, row_count = ?, column_count = ? WHERE hall_name = ?";
                try (PreparedStatement pstmt2 = conn.prepareStatement(updateHallsSql)) {
                    pstmt2.setString(1, newName);
                    pstmt2.setInt(2, rows);
                    pstmt2.setInt(3, cols);
                    pstmt2.setString(4, oldName);
                    pstmt2.executeUpdate();
                }

                conn.commit(); // Her şey tamamsa veritabanına işle
                System.out.println("Salon ve bağlı seanslar başarıyla güncellendi.");

            } catch (SQLException e) {
                conn.rollback(); // Bir hata olursa hiçbir şeyi değiştirme, eski haline dön
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Hall> getAllHalls() {
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

    @Override
    public void deleteHall(String name) {
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