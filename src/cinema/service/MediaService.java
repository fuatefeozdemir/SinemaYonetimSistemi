package cinema.service;

import cinema.H2.H2MediaRepository;
import cinema.H2.H2SessionRepository;
import cinema.model.Session;
import cinema.model.content.Media;
import java.util.List;

public class MediaService {

    // Veritabanındaki tüm medyaları liste olarak getirir
    public List<Media> getAllFilms() {
        return H2MediaRepository.getAllFilm();
    }

    // Yeni bir medyayı kaydeder
    public void addMedia(Media media) {
        H2MediaRepository.saveMedia(media);
    }

    // Medya bilgilerini günceller
    public void updateMedia(Media media) {
        H2MediaRepository.updateMedia(media);
    }

    // Medyayı siler
    public void deleteMedia(String name) {
        H2MediaRepository.deleteMedia(name);
    }

    // Medyayı getirir
    public Media getMediaByName(String name) {
        return H2MediaRepository.getMedia(name);
    }
}