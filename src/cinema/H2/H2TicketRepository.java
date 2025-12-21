package cinema.H2;

import cinema.model.Session;
import cinema.model.Ticket;
import cinema.repository.TicketRepository;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class H2TicketRepository implements TicketRepository {
    private static final String URL = "jdbc:h2:./data/CINEMA_DB;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";
    // Tarihleri DB'den okurken veya yazarken kullanılacak format
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void initialize() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS tickets (" +
                    "ticket_id VARCHAR(255) PRIMARY KEY, " +
                    "session_id VARCHAR(50), " +
                    "customer_email VARCHAR(255), " +
                    "seat_code VARCHAR(10), " +
                    "final_price DOUBLE, " +
                    "purchase_time TIMESTAMP, " +
                    "FOREIGN KEY (session_id) REFERENCES sessions(session_id), " +
                    "FOREIGN KEY (customer_email) REFERENCES users(email))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveTicket(Ticket ticket) {
        String sql = "INSERT INTO tickets (ticket_id, session_id, customer_email, seat_code, final_price, purchase_time, is_refunded) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ticket.getTicketId());
            pstmt.setString(2, ticket.getSession());
            pstmt.setString(3, ticket.getCustomer());
            pstmt.setString(4, ticket.getSeatCode());
            pstmt.setDouble(5, ticket.getFinalPrice());
            pstmt.setTimestamp(6, Timestamp.valueOf(ticket.getPurchaseTime()));
            pstmt.setBoolean(7, false);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateRefundStatus(String ticketId, boolean status) {

    }

    @Override
    public List<Ticket> getTicketsByCustomer(String email) {
        return List.of();
    }

    @Override
    public List<Session> getSessionsForMovie(String movieTitle) {
        List<Session> sessions = new ArrayList<>();
        // Tüm kolonları çekiyoruz (session_id, hall_id, start_time, end_time vb.)
        String query = "SELECT * FROM sessions WHERE media_name = ? ORDER BY start_time ASC";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, movieTitle);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String sessionId = rs.getString("session_id");
                Timestamp startTs = rs.getTimestamp("start_time");
                Timestamp endTs = rs.getTimestamp("end_time");

                // LocalDateTime dönüşümleri
                LocalDateTime startTime = startTs != null ? startTs.toLocalDateTime() : null;
                LocalDateTime endTime = endTs != null ? endTs.toLocalDateTime() : null;

                // Session nesnesini oluşturma
                // Not: Hall ve Media için ilgili repository'lerden tam nesne çekebilir
                // veya sadece ID'lerini içeren boş nesneler atayabilirsin.
                Session session = new Session(
                        sessionId,
                        null, // Hall nesnesi (Gerekirse hallRepository.getById(rs.getString("hall_id")))
                        null, // Media nesnesi (Film bilgisi)
                        startTime,
                        endTime
                );

                sessions.add(session);
            }
        } catch (SQLException e) {
            System.err.println("Seans nesneleri çekilemedi: " + e.getMessage());
        }
        return sessions;
    }

    @Override
    public List<String> getOccupiedSeats(String sessionId) {
        List<String> occupied = new ArrayList<>();

        // Doğrudan session_id üzerinden filtreleme yapıyoruz.
        // Join'e gerek kalmadı çünkü bilet tablosunda zaten session_id var.
        // Sadece iade edilmemiş (is_refunded = FALSE) biletleri alıyoruz.
        String query = "SELECT seat_code FROM tickets WHERE session_id = ? AND is_refunded = FALSE";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, sessionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                occupied.add(rs.getString("seat_code"));
            }
        } catch (SQLException e) {
            System.err.println("Koltuklar çekilemedi (SessionID: " + sessionId + "): " + e.getMessage());
        }

        return occupied;
    }

    public static List<Ticket> getTicketsByCustomerEmail(String email) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE customer_email = ? ORDER BY purchase_time DESC";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Ticket ticket = new Ticket(
                        rs.getString("session_id"),
                        rs.getString("customer_email"),
                        rs.getString("seat_code"),
                        rs.getDouble("final_price")
                );

                // KRİTİK DOKUNUŞ: Veritabanındaki gerçek ID'yi Java nesnesine zorla yazıyoruz
                ticket.setTicketId(rs.getString("ticket_id"));

                tickets.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public static boolean deleteTicket(String ticketId) {
        String sql = "DELETE FROM tickets WHERE ticket_id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ticketId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}