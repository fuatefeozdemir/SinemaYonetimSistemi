package cinema.H2;

import cinema.model.people.Cashier;
import cinema.model.people.Customer;
import cinema.model.people.Manager;
import cinema.model.people.User;
import cinema.repository.UserRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.UUID;

public class H2UserRepository implements UserRepository {
    private static final String URL = "jdbc:h2:./data/CINEMA_DB;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";

    private static final String CUSTOMER = "CUSTOMER";
    private static final String MANAGER = "MANAGER";
    private static final String CASHIER = "CASHIER";

    @Override
    public void initialize() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            // 1. Ana Users Tablosu
            // ID String olduğu için PRIMARY KEY VARCHAR(50) yapıldı
            String createUserTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id VARCHAR(50) PRIMARY KEY, " +
                    "first_name VARCHAR(100) NOT NULL, " +
                    "last_name VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(150) UNIQUE NOT NULL, " +
                    "date_of_birth DATE, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "user_type VARCHAR(20)" + // ADMIN veya CUSTOMER olduğunu anlamak için
                    ")";
            stmt.execute(createUserTable);


            String createCustomerTable = "CREATE TABLE IF NOT EXISTS customers (" +
                    "user_id VARCHAR(50) PRIMARY KEY, " +
                    "loyalty_points INT DEFAULT 0, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)";

            stmt.execute(createCustomerTable);

            String createManagerTable = "CREATE TABLE IF NOT EXISTS managers (" +
                    "user_id VARCHAR(50) PRIMARY KEY, " +
                    "staff_id INT NOT NULL, " +
                    "hourly_rate DOUBLE, " +
                    "is_full_time BOOLEAN, " +
                    "hire_date DATE, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ")";
            stmt.execute(createManagerTable);

            String cashierTableSql = "CREATE TABLE IF NOT EXISTS cashiers (" +
                    "user_id VARCHAR(50) PRIMARY KEY, " +
                    "staff_id INT NOT NULL, " +
                    "hourly_rate DOUBLE, " +
                    "is_full_time BOOLEAN, " +
                    "hire_date DATE, " +
                    "daily_count INT DEFAULT 0, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ")";
            stmt.execute(cashierTableSql);


            System.out.println("Veritabanı tabloları başarıyla hazırlandı.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveUser(User user) {
        String userSql = "INSERT INTO users (id, first_name, last_name, email, date_of_birth, password, user_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String newUserId = UUID.randomUUID().toString();
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false); // Transaction başlat (Biri başarısız olursa ikisini de iptal et)
            try {
                // 1. Ana tabloya ekle

                PreparedStatement pstmt = conn.prepareStatement(userSql);
                pstmt.setString(1, newUserId);
                pstmt.setString(2, user.getFirstName());
                pstmt.setString(3, user.getLastName());
                pstmt.setString(4, user.getEmail());
                pstmt.setDate(5, java.sql.Date.valueOf(user.getDateOfBirth()));
                pstmt.setString(6, user.getPassword());

                // 2. Alt tabloya (Customer/Admin) ekle
                if (user instanceof Customer c) {
                    pstmt.setString(7, CUSTOMER);
                    pstmt.executeUpdate();

                    String customerSql = "INSERT INTO customers (user_id, loyalty_points) VALUES (?, ?)";
                    try (PreparedStatement pstmtCust = conn.prepareStatement(customerSql)) {
                        pstmtCust.setString(1, newUserId);
                        pstmtCust.setInt(2, c.getLoyaltyPoints());

                        pstmtCust.executeUpdate();
                    }
                } else if (user instanceof Manager m) {
                    pstmt.setString(7, MANAGER);
                    pstmt.executeUpdate();


                    PreparedStatement ps = conn.prepareStatement("INSERT INTO managers (user_id, staff_id, hourly_rate, is_full_time, hire_date) VALUES (?, ?, ?, ?, ?)");
                    ps.setString(1, newUserId);
                    ps.setInt(2, m.getStaffID());
                    ps.setDouble(3, m.getHourlyRate());
                    ps.setBoolean(4, m.isFullTime());
                    ps.setDate(5, java.sql.Date.valueOf(m.getHireDate()));
                    ps.executeUpdate();
                } else if (user instanceof Cashier c) {
                    pstmt.setString(7, CASHIER);
                    pstmt.executeUpdate();

                    String sql = "INSERT INTO cashiers (user_id, staff_id, hourly_rate, is_full_time, hire_date, daily_count) VALUES (?, ?, ?, ?, ?, ?)";
                    try (var ps = conn.prepareStatement(sql)) {
                        ps.setString(1, newUserId);
                        ps.setInt(2, c.getStaffID());
                        ps.setDouble(3, c.getHourlyRate());
                        ps.setBoolean(4, c.isFullTime());
                        ps.setDate(5, java.sql.Date.valueOf(c.getHireDate()));
                        ps.setInt(6, c.getDailyCount());
                        ps.executeUpdate();
                    }
                }

                conn.commit(); // Her şey yolundaysa kaydet
            } catch (SQLException e) {
                System.out.println("HATA");
                e.printStackTrace();
                conn.rollback(); // Hata varsa geri al
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void updateUser(User user) {

        String userSql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, date_of_birth = ?, password = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false); // İşlemi güvenli hale getir
            try {
                // Önce ana tabloyu güncelle
                PreparedStatement pstmt = conn.prepareStatement(userSql);
                pstmt.setString(1, user.getFirstName());
                pstmt.setString(2, user.getLastName());
                pstmt.setString(3, user.getEmail());
                pstmt.setDate(4, java.sql.Date.valueOf(user.getDateOfBirth()));
                pstmt.setString(5, user.getPassword());
                pstmt.setString(6, user.getId());
                pstmt.executeUpdate();

                // Tipine göre alt tabloyu güncelle
                if (user instanceof Customer c) {
                    String custSql = "UPDATE customers SET loyalty_points = ? WHERE user_id = ?";
                    var pstmtCust = conn.prepareStatement(custSql);
                    pstmtCust.setInt(1, c.getLoyaltyPoints()); // Customer'a özel alan
                    pstmtCust.setString(2, c.getId());
                    pstmtCust.executeUpdate();
                } else if (user instanceof Manager m) {
                    PreparedStatement ps = conn.prepareStatement("UPDATE managers SET hourly_rate = ?, is_full_time = ? WHERE user_id = ?");
                    ps.setDouble(1, m.getHourlyRate());
                    ps.setBoolean(2, m.isFullTime());
                    ps.setString(3, m.getId());
                    ps.executeUpdate();
                } else if (user instanceof Cashier c) {
                    String sql = "UPDATE cashiers SET hourly_rate = ?, is_full_time = ?, daily_count = ? WHERE user_id = ?";
                    try (var ps = conn.prepareStatement(sql)) {
                        ps.setDouble(1, c.getHourlyRate());
                        ps.setBoolean(2, c.isFullTime());
                        ps.setInt(3, c.getDailyCount());
                        ps.setString(4, c.getId());
                        ps.executeUpdate();
                    }
                }
                conn.commit();
                System.out.println("Kullanıcı ve bağlı tüm bilgiler güncellendi: " + user.getFirstName());
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUser(String email) {
        // Önce bağımlı kayıtları (biletleri) silmemiz gerekiyor
        String deleteTicketsSql = "DELETE FROM tickets WHERE customer_email = ?";
        String deleteUserSql = "DELETE FROM users WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(URL)) {
            // İşlemlerin güvenliği için transaction başlatıyoruz
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtTickets = conn.prepareStatement(deleteTicketsSql);
                 PreparedStatement pstmtUser = conn.prepareStatement(deleteUserSql)) {

                // 1. Adım: Kullanıcıya ait biletleri temizle
                pstmtTickets.setString(1, email);
                pstmtTickets.executeUpdate();

                // 2. Adım: Kullanıcıyı sil
                pstmtUser.setString(1, email);
                int affectedRows = pstmtUser.executeUpdate();

                if (affectedRows > 0) {
                    conn.commit(); // Her iki işlem de başarılıysa onayla
                    System.out.println(email + " has been successfully deleted along with their tickets.");
                } else {
                    conn.rollback(); // Kullanıcı bulunamazsa işlemleri geri al
                    System.out.println("User not found.");
                }

            } catch (SQLException e) {
                conn.rollback(); // Herhangi bir hatada tüm değişiklikleri iptal et
                System.err.println("Delete error: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }

    @Override
    public User getUser(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String type = rs.getString("user_type");
                String id = rs.getString("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                LocalDate dob = rs.getDate("date_of_birth").toLocalDate();
                String password = rs.getString("password");

                // BURASI GÜNCELLENDİ: CASHIER eklendi
                return switch (type) {
                    case CUSTOMER -> getCustomerDetails(conn, id, firstName, lastName, email, dob, password);
                    case MANAGER -> getManagerDetails(conn, id, firstName, lastName, email, dob, password);
                    case CASHIER -> getCashierDetails(conn, id, firstName, lastName, email, dob, password);
                    default -> null;
                };
            }
        } catch (SQLException e) {
            System.err.println("Kullanıcı getirme hatası: " + e.getMessage());
        }
        return null;
    }

    // Yardımcı metod: Customer verilerini JOIN ile çeker
    private Customer getCustomerDetails(Connection conn, String id, String fn, String ln, String email, LocalDate dob, String pass) throws SQLException {
        String sql = "SELECT loyalty_points FROM customers WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Customer c = new Customer(fn, ln, email, dob, pass);
                c.setId(id);
                c.setLoyaltyPoints(rs.getInt("loyalty_points"));
                return c;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Manager getManagerDetails(Connection conn, String id, String fn, String ln, String email, LocalDate dob, String pass) throws SQLException {
        String sql = "SELECT staff_id, hourly_rate, is_full_time, hire_date FROM managers WHERE user_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Verileri RS'den çekiyoruz
                int staffID = rs.getInt("staff_id");
                double hourlyRate = rs.getDouble("hourly_rate");
                boolean isFullTime = rs.getBoolean("is_full_time");
                LocalDate hireDate = rs.getDate("hire_date").toLocalDate();

                // Manager nesnesini oluştur (Not: StoreManager gibi somut bir sınıf kullanmalısın)
                Manager manager = new Manager(fn, ln, email, dob, pass, staffID, hourlyRate, isFullTime, hireDate);

                // Ana tablodan gelen ID'yi set ediyoruz
                manager.setId(id);

                return manager;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private Cashier getCashierDetails(Connection conn, String id, String fn, String ln, String email, LocalDate dob, String pass) throws SQLException {
        String sql = "SELECT * FROM cashiers WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Cashier c = new Cashier(fn, ln, email, dob, pass,
                        rs.getInt("staff_id"),
                        rs.getDouble("hourly_rate"),
                        rs.getBoolean("is_full_time"),
                        rs.getDate("hire_date").toLocalDate());
                c.setId(id);
                return c;
            }
        }
        return null;
    }
}
