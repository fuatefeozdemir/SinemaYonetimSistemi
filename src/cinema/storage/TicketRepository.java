package cinema.storage;

import cinema.model.Ticket;
import cinema.model.Session;
import cinema.model.people.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketRepository {
    private static final String URL = "jdbc:h2:./data/CINEMA_DB;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";

    public static void initialize() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            // Ticket Tablosu
            stmt.execute("CREATE TABLE IF NOT EXISTS tickets (" +
                    "ticket_id VARCHAR(20) PRIMARY KEY, " +
                    "session_id VARCHAR(50), " +
                    "customer_email VARCHAR(255), " + // Müşteriyi e-posta ile bağlıyoruz
                    "seat_code VARCHAR(10), " +
                    "final_price DOUBLE, " +
                    "purchase_time TIMESTAMP, " +
                    "is_refunded BOOLEAN DEFAULT FALSE, " +
                    "FOREIGN KEY (session_id) REFERENCES sessions(session_id), " +
                    "FOREIGN KEY (customer_email) REFERENCES users(email))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 1. Bileti Kaydet
    public static void saveTicket(Ticket ticket) {
        String sql = "INSERT INTO tickets (ticket_id, session_id, customer_email, seat_code, final_price, purchase_time, is_refunded) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ticket.getTicketId());
            pstmt.setString(2, ticket.getSession().getSessionId());
            pstmt.setString(3, ticket.getCustomer().getEmail());
            pstmt.setString(4, ticket.getSeatCode());
            pstmt.setDouble(5, ticket.getFinalPrice());
            pstmt.setTimestamp(6, Timestamp.valueOf(ticket.getPurchaseTime()));
            pstmt.setBoolean(7, ticket.isRefunded());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2. Bilet İade Durumunu Güncelle
    public static void updateRefundStatus(String ticketId, boolean status) {
        String sql = "UPDATE tickets SET is_refunded = ? WHERE ticket_id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, status);
            pstmt.setString(2, ticketId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 3. Bir Müşteriye Ait Biletleri Getir
    public static List<Ticket> getTicketsByCustomer(String email) {
        List<Ticket> customerTickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE customer_email = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Not: Burada Ticket nesnesini tam oluşturmak için
                // Session ve Customer nesnelerini de ilgili repolardan çekmelisin.
                // Şimdilik sadece ID bazlı takip veya basit listeleme için kullanılabilir.
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customerTickets;
    }
}
