package cinema.service;

import cinema.H2.H2HallRepository;
import cinema.H2.H2MediaRepository;
import cinema.H2.H2SessionRepository;
import cinema.model.Session;
import cinema.model.Hall;
import cinema.model.content.Media;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessionService {
    private static final String URL = "jdbc:h2:./data/CINEMA_DB;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";

    public static Session getSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return null;
        }
        return H2SessionRepository.getSession(sessionId);
    }

    public static List<Session> getSessionsByMediaName(String mediaName) {
        List<Session> sessions = new ArrayList<>();

        String sql = "SELECT * FROM sessions WHERE media_name = ?";

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
            System.err.println("Seans çekme hatası: " + e.getMessage());
        }
        return sessions;
    }
}