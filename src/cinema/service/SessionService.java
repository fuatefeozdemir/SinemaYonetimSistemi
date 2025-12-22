package cinema.service;

import cinema.H2.H2HallRepository;
import cinema.H2.H2MediaRepository;
import cinema.H2.H2SessionRepository;
import cinema.model.Session;
import cinema.model.Hall;
import cinema.model.content.Media;
import cinema.model.content.Film;
import cinema.repository.HallRepository;
import cinema.repository.MediaRepository;
import cinema.repository.SessionRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class SessionService {

    private final SessionRepository sessionRepository;
    private final HallRepository hallRepository;
    private final MediaRepository mediaRepository;
    // Veritabanı bağlantı adresi ve tarih formatı standardı
    private static final String URL = "jdbc:h2:./data/CINEMA_DB;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public SessionService(SessionRepository sessionRepository, HallRepository hallRepository, MediaRepository mediaRepository) {
        this.sessionRepository = sessionRepository;
        this.hallRepository = hallRepository;
        this.mediaRepository = mediaRepository;
    }

    // Yeni seans ekleme
    public void addSession(String mediaName, String hallName, String dateTimeStr) throws Exception {
        Media media = mediaRepository.getMedia(mediaName);
        Hall hall = hallRepository.getHall(hallName);

        if (media == null || hall == null) {
            throw new Exception("Film veya Salon bulunamadı!");
        }

        LocalDateTime startTime = LocalDateTime.parse(dateTimeStr, FORMATTER);

        int duration = 120; // Varsayılan süre
        if (media instanceof Film) {
            duration = ((Film) media).getDurationMinutes();
        }
        LocalDateTime endTime = startTime.plusMinutes(duration);

        // Rastgele seans idsi oluşturuluyor
        String sessionId = UUID.randomUUID().toString().substring(0, 8);

        Session session = new Session(sessionId, hall, media, startTime, endTime);
        sessionRepository.saveSession(session);
    }

    // IDsi verilen seansı veritabanından getirir
    public Session getSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return null;
        }
        return sessionRepository.getSession(sessionId);
    }

    // Bir filme ait tüm seansları liste olarak döndürür
    public List<Session> getSessionsByMediaName(String mediaName) {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT * FROM sessions WHERE media_name = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, mediaName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Hall hall = hallRepository.getHall(rs.getString("hall_name"));
                Media media = mediaRepository.getMedia(rs.getString("media_name"));

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
            System.err.println("Seans listesi yüklenirken hata oluştu: " + e.getMessage());
        }
        return sessions;
    }

    // Seansı siler
    public void deleteSession(String film, String hall, String start) {
        sessionRepository.deleteSession(film, hall, start);
    }
}