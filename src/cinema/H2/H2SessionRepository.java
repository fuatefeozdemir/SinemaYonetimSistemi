package cinema.H2;

import cinema.model.Session;
import cinema.model.Hall;
import cinema.model.content.Media;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class H2SessionRepository {
    private static final String URL = "jdbc:h2:./data/CINEMA_DB;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";

    public static void initialize() {
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

    // --- YARDIMCI METOTLAR: Matris <-> String Dönüşümü ---

    private static String serializeSeats(boolean[][] seats) {
        StringBuilder sb = new StringBuilder();
        for (boolean[] row : seats) {
            for (boolean seat : row) {
                sb.append(seat ? '1' : '0');
            }
        }
        return sb.toString();
    }

    private static void deserializeSeats(String data, boolean[][] seats) {
        int charIndex = 0;
        for (int r = 0; r < seats.length; r++) {
            for (int c = 0; c < seats[r].length; c++) {
                seats[r][c] = (data.charAt(charIndex++) == '1');
            }
        }
    }

    // --- CRUD İŞLEMLERİ ---

    public static void saveSession(Session session) {
        String sql = "INSERT INTO sessions (session_id, hall_name, media_name, start_time, end_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, session.getSessionId());
            pstmt.setString(2, session.getHall().getHallName());
            pstmt.setString(3, session.getFilm().getName());
            pstmt.setTimestamp(4, Timestamp.valueOf(session.getStartTime()));
            pstmt.setTimestamp(5, Timestamp.valueOf(session.getEndTime()));
            //        pstmt.setString(6, "");

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Session getSession(String id) {
        String sql = "SELECT * FROM sessions WHERE session_id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Diğer repolardan Hall ve Media nesnelerini çekmemiz gerekiyor
                Hall hall = new H2HallRepository().getHall(rs.getString("hall_name"));
                Media media = new H2MediaRepository().getMedia(rs.getString("media_name"));

                Session session = new Session(
                        rs.getString("session_id"),
                        hall,
                        media,
                        rs.getTimestamp("start_time").toLocalDateTime(),
                        rs.getTimestamp("end_time").toLocalDateTime()
                );

                // Kayıtlı koltuk verisini matrise işle
//                deserializeSeats(rs.getString("seat_data"), boolean[][]);
                return session;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Session> getSessionsByMediaName(String mediaName) {
        List<Session> sessions = new ArrayList<>();
        // media_name'e göre tüm seansları en erken saatten başlayarak getir
        String sql = "SELECT * FROM sessions WHERE media_name = ? ORDER BY start_time ASC";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, mediaName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Mevcut Hall ve Media repolarını kullanarak nesneleri çekiyoruz
                // Not: Bu kısımlar senin getSession(id) metodunla aynı mantıktadır.
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

//    public static void updateSeats(Session session) {
//        String sql = "UPDATE sessions SET seat_data = ? WHERE session_id = ?";
//        try (Connection conn = DriverManager.getConnection(URL);
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, serializeSeats(session.getSeats()));
//            pstmt.setString(2, session.getSessionId());
//            pstmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
    }
}