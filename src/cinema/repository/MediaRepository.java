package cinema.repository;

import cinema.model.content.Media;
import cinema.model.content.Trailer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


// yapılacak

public interface MediaRepository {
    void initialize();
    void saveMedia(Media media);
    void updateMedia(Media media);
    void deleteMedia(String name);
    Media getMedia(String name);
    List<Media> getAllFilm();
}