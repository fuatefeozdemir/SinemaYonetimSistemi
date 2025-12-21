package cinema.service;

import cinema.H2.H2MediaRepository;
import cinema.H2.H2SessionRepository;
import cinema.model.Session;
import cinema.model.content.Media;
import cinema.model.content.Film;
import cinema.repository.SessionRepository;

import java.sql.SQLException;
import java.util.List;

public class MediaService {

    public List<Media> getAllFilms() {
        // Repository'den gelen ham veriyi UI'ya iletir
        return H2MediaRepository.getAllFilm();
    }

    public void addMedia(Media media) {
        // İş mantığı kontrolleri burada yapılabilir (Örn: İsim boş mu?)
        H2MediaRepository.saveMedia(media);
    }

    public void updateMedia(Media media) {
        H2MediaRepository.updateMedia(media);
    }

    public void deleteMedia(String name) {
        H2MediaRepository.deleteMedia(name);
    }

    public Media getMediaByName(String name) {
        return H2MediaRepository.getMedia(name);
    }
    public List<Session> getSessionsByFilmId(String name) {
        // Veritabanından (H2) bu filmId'ye ait seansları çeker:
        // "SELECT * FROM sessions WHERE film_id = ?"
        return H2SessionRepository.getSessionsByMediaName(name);
    }
}