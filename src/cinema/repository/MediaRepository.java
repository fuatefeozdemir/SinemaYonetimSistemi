package cinema.repository;

import cinema.model.content.Media;
import java.sql.SQLException;
import java.util.List;


// Çalışmıyor olabilir

public interface MediaRepository {
    void initialize();
    void saveMedia(Media media);
    void updateMedia(Media media);
    void deleteMedia(String name);
    Media getMedia(String name);
    List<Media> getAllFilms() throws SQLException;
}