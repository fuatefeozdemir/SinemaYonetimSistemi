package cinema.H2;

import cinema.model.Session;
import cinema.model.Hall;
import cinema.model.content.Media;
import cinema.repository.SessionRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class H2SessionRepository implements SessionRepository {
    private static final String URL = "jdbc:h2:./data/CINEMA_DB;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";

    public void initialize() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS sessions (" +
                    "session_id VARCHAR(50) PRIMARY KEY, " +
                    "hall_name VARCHAR(100), " +
                    "media_name VARCHAR(255), " +
                    "start_time TIMESTAMP, " +
                    "end_time TIMESTAMP, " +
                    "seat_data TEXT, " +
                    "FOREIGN KEY (hall_name) REFERENCES halls(hall_name), " +
                    "FOREIGN KEY (media_name) REFERENCES media(name))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveSession(Session session) {
        String sql = "INSERT INTO sessions (session_id, hall_name, media_name, start_time, end_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, session.getSessionId());
            pstmt.setString(2, session.getHall().getHallName());
            pstmt.setString(3, session.getFilm().getName());
            pstmt.setTimestamp(4, Timestamp.valueOf(session.getStartTime()));
            pstmt.setTimestamp(5, Timestamp.valueOf(session.getEndTime()));

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Session getSession(String id) {
        String sql = "SELECT * FROM sessions WHERE session_id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Diğer tablolardan Hall ve Media nesneleri getiriliyor
                Hall hall = new H2HallRepository().getHall(rs.getString("hall_name"));
                Media media = new H2MediaRepository().getMedia(rs.getString("media_name"));

                Session session = new Session(
                        rs.getString("session_id"),
                        hall,
                        media,
                        rs.getTimestamp("start_time").toLocalDateTime(),
                        rs.getTimestamp("end_time").toLocalDateTime()
                );

                return session;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Session> getSessionsByMediaName(String mediaName) {
        List<Session> sessions = new ArrayList<>();
        // Seansları erken saatten geç saate sıralayarak getirme
        String sql = "SELECT * FROM sessions WHERE media_name = ? ORDER BY start_time ASC";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, mediaName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Hall hall = new H2HallRepository().getHall(rs.getString("hall_name"));
                Media media = new H2MediaRepository().getMedia(rs.getString("media_name"));

                Session session = new Session(
                        rs.getString("session_id"),
                        hall,
                        media,
                        rs.getTimestamp("start_time").toLocalDateTime(),
                        rs.getTimestamp("end_time").toLocalDateTime()
                );

                sessions.add(session);
            }
        } catch (SQLException e) {
            System.err.println("Seanslar çekilirken hata oluştu (Film: " + mediaName + "): " + e.getMessage());
            e.printStackTrace();
        }
        return sessions;
    }


    public void deleteSession(String film, String hall, String start) {
        String sql = "DELETE FROM sessions WHERE media_name = ? AND hall_name = ? AND start_time = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, film);
            pstmt.setString(2, hall);

            // Tablodaki veriyi parse ediyoruz
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            // Eğer start metni saniye içeriyorsa (Örn: 2025-12-22 12:00:00)
            // sadece ilk 16 karakteri alarak HH:mm formatına uyduruyoruz
            String sanitizedStart = start.substring(0, 16);
            LocalDateTime localDateTime = LocalDateTime.parse(sanitizedStart, formatter);

            pstmt.setTimestamp(3, Timestamp.valueOf(localDateTime));

            // KRİTİK EKSİK: Sorguyu veritabanında çalıştırır
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                System.out.println("Silinecek seans bulunamadı. Kriterler uyuşmuyor olabilir.");
            }

        } catch (Exception e) {
            // Hatayı yukarı fırlatıyoruz ki UI katmanı (ManagerMainFrame) yakalayabilsin [cite: 134-135, 193]
            throw new RuntimeException(e.getMessage());
        }
    }
}