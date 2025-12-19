package cinema.storage;


import cinema.model.content.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.UUID;

public class MediaRepository {
    static final String URL = "jdbc:h2:./data/CINEMA_DB;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";

    private static final String FILM = "FILM";
    private static final String TRAILER = "TRAILER";
    private static final String ADVERTISEMENT = "ADVERTISEMENT";

    private static final String STANDARD = "Standard";
    private static final String PREMIUM = "Premium";
    private static final String ANIMATION = "Animation";

    public static void initialize() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            // 1. Ana Tablo: Media
            stmt.execute("CREATE TABLE IF NOT EXISTS media (" +
                    "id VARCHAR(50) PRIMARY KEY, " +
                    "name VARCHAR(255) UNIQUE, " +
                    "duration_minutes INT, " +
                    "is_visible BOOLEAN, " +
                    "media_type VARCHAR(20))");

            // 2. Alt Tablo: Film
            String filmTableSql = "CREATE TABLE IF NOT EXISTS films (" +
                    "media_id VARCHAR(50) PRIMARY KEY, " +
                    "release_date DATE, " +
                    "director VARCHAR(150), " +
                    "age_restriction VARCHAR(20), " +
                    "genre VARCHAR(100), " +
                    "language VARCHAR(50), " +
                    "imdb_rating FLOAT, " +
                    "type VARCHAR(20), " +
                    "FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE)";
            stmt.execute(filmTableSql);

            // 3. Alt Tablo: Advertisement
            stmt.execute("CREATE TABLE IF NOT EXISTS advertisements (" +
                    "media_id VARCHAR(50) PRIMARY KEY, " +
                    "company VARCHAR(150), " +
                    "FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE)");

            // 4. Alt Tablo: Trailer
            String trailerTableSql = "CREATE TABLE IF NOT EXISTS trailers (" +
                    "media_id VARCHAR(50) PRIMARY KEY, " +
                    "film_name VARCHAR(255), " + // Fragmanın ait olduğu filmin ismi
                    "FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (film_name) REFERENCES media(name) ON DELETE SET NULL" +
                    ")";
            stmt.execute(trailerTableSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveMedia(Media media) {
        String mediaSql = "INSERT INTO media (id, name, duration_minutes, is_visible, media_type) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);
            try {
                var pstmt = conn.prepareStatement(mediaSql);
                String mediaId = UUID.randomUUID().toString(); // ID burada üretilebilir
                pstmt.setString(1, mediaId);
                pstmt.setString(2, media.getName());
                pstmt.setInt(3, media.getDurationMinutes());
                pstmt.setBoolean(4, media.isVisible());

                if (media instanceof Film f) {
                    pstmt.setString(5, FILM);
                    pstmt.executeUpdate();

                    var ps = conn.prepareStatement("INSERT INTO films VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                    ps.setString(1, mediaId);
                    ps.setDate(2, java.sql.Date.valueOf(f.getReleaseDate()));
                    ps.setString(3, f.getDirector());
                    ps.setString(4, f.getAgeRestriction());
                    ps.setString(5, f.getGenre());
                    ps.setString(6, f.getLanguage());
                    ps.setFloat(7, f.getImdbRating());
                    ps.setString(8, f.getType()); // Film tipini buraya ekliyoruz
                    ps.executeUpdate();
                } else if (media instanceof Advertisement a) {
                    pstmt.setString(5, ADVERTISEMENT);
                    pstmt.executeUpdate();
                    var ps = conn.prepareStatement("INSERT INTO advertisements VALUES (?, ?)");
                    ps.setString(1, mediaId);
                    ps.setString(2, a.getCompany());
                    ps.executeUpdate();
                } else if (media instanceof Trailer t) {
                    pstmt.setString(5, TRAILER);
                    pstmt.executeUpdate();

                    // t.getFilmName() değerini trailers tablosuna kaydediyoruz
                    var ps = conn.prepareStatement("INSERT INTO trailers (media_id, film_name) VALUES (?, ?)");
                    ps.setString(1, mediaId);
                    ps.setString(2, t.getFilmName());
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateMedia(Media media) {
        // 1. Ana tabloyu güncelleme sorgusu
        String mediaUpdateSql = "UPDATE media SET duration_minutes = ?, is_visible = ? WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false); // İşlem güvenliği için transaction başlatıyoruz

            try {
                // --- ANA TABLO GÜNCELLEME ---
                var pstmt = conn.prepareStatement(mediaUpdateSql);
                pstmt.setInt(1, media.getDurationMinutes());
                pstmt.setBoolean(2, media.isVisible());
                pstmt.setString(3, media.getName());
                int affectedRows = pstmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Güncellenecek medya bulunamadı: " + media.getName());
                }

                // --- ALT TABLO GÜNCELLEME (if-else yapısı) ---
                if (media instanceof Film f) {
                    String filmUpdateSql = "UPDATE films SET release_date = ?, director = ?, " +
                            "age_restriction = ?, genre = ?, language = ?, " +
                            "imdb_rating = ?, type = ? " +
                            "WHERE media_id = (SELECT id FROM media WHERE name = ?)";

                    try (var ps = conn.prepareStatement(filmUpdateSql)) {
                        ps.setDate(1, java.sql.Date.valueOf(f.getReleaseDate()));
                        ps.setString(2, f.getDirector());
                        ps.setString(3, f.getAgeRestriction());
                        ps.setString(4, f.getGenre());
                        ps.setString(5, f.getLanguage());
                        ps.setFloat(6, f.getImdbRating());
                        ps.setString(7, f.getType());
                        ps.setString(8, f.getName()); // Media ID'yi bulmak için ismi kullanıyoruz
                        ps.executeUpdate();
                    }
                } else if (media instanceof Advertisement a) {
                    String adUpdateSql = "UPDATE advertisements SET company = ? " +
                            "WHERE media_id = (SELECT id FROM media WHERE name = ?)";

                    try (var ps = conn.prepareStatement(adUpdateSql)) {
                        ps.setString(1, a.getCompany());
                        ps.setString(2, a.getName());
                        ps.executeUpdate();
                    }
                } else if (media instanceof Trailer t) {
                    String trailerUpdateSql = "UPDATE trailers SET film_name = ? WHERE media_id = (SELECT id FROM media WHERE name = ?)";
                    try (var ps = conn.prepareStatement(trailerUpdateSql)) {
                        ps.setString(1, t.getFilmName());
                        ps.setString(2, t.getName());
                        ps.executeUpdate();
                    }
                }

                conn.commit(); // Tüm güncellemeler başarılıysa onayla
                System.out.println(media.getName() + " ve bağlı özellikleri başarıyla güncellendi.");

            } catch (SQLException e) {
                conn.rollback(); // Herhangi bir hata durumunda her şeyi geri al
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMedia(String name) {
        String sql = "DELETE FROM media WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static Media getMedia(String name) {
        // Önce ana tablodan temel bilgileri alıyoruz
        String sql = "SELECT * FROM media WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String id = rs.getString("id");
                int duration = rs.getInt("duration_minutes");
                boolean visible = rs.getBoolean("is_visible");
                String mediaType = rs.getString("media_type");

                // Medya tipine göre ilgili detay metoduna yönlendiriyoruz
                return switch (mediaType) {
                    case "FILM" -> getFilmDetails(conn, id, name, duration, visible);
                    case "AD" -> getAdvertisementDetails(conn, id, name, duration, visible);
                    case "TRAILER" -> getTrailerDetails(conn, id, name, duration, visible);
                    default -> null;
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Medya bulunamadıysa
    }

    // FILM Detaylarını Çeken Metod
    private static Film getFilmDetails(Connection conn, String id, String name, int duration, boolean visible) {
        String sql = "SELECT * FROM films WHERE media_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String filmType = rs.getString("type");
                switch (filmType) {
                    case PREMIUM:
                        return new Premium3D(name,
                                duration,
                                visible,
                                rs.getDate("release_date").toLocalDate(),
                                rs.getString("director"),
                                rs.getString("age_restriction"),
                                rs.getString("genre"),
                                rs.getString("language"),
                                rs.getFloat("imdb_rating"));
                    case STANDARD:
                        return new Standard2D(name,
                                duration,
                                visible,
                                rs.getDate("release_date").toLocalDate(),
                                rs.getString("director"),
                                rs.getString("age_restriction"),
                                rs.getString("genre"),
                                rs.getString("language"),
                                rs.getFloat("imdb_rating"));
                    case ANIMATION:
                        return new Animation(name,
                                duration,
                                visible,
                                rs.getDate("release_date").toLocalDate(),
                                rs.getString("director"),
                                rs.getString("age_restriction"),
                                rs.getString("genre"),
                                rs.getString("language"),
                                rs.getFloat("imdb_rating"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ADVERTISEMENT Detaylarını Çeken Metod
    private static Advertisement getAdvertisementDetails(Connection conn, String id, String name, int duration, boolean visible) {
        String sql = "SELECT company FROM advertisements WHERE media_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Advertisement(name, duration, visible, rs.getString("company"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // TRAILER Detaylarını Çeken Metod
    private static Trailer getTrailerDetails(Connection conn, String id, String name, int duration, boolean visible){
        String sql = "SELECT film_name FROM trailers WHERE media_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Fragman nesnesini film ismiyle beraber oluşturuyoruz
                return new Trailer(name, duration, visible, rs.getString("film_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
