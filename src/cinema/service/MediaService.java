package cinema.service;

import cinema.model.content.Media;
import cinema.repository.MediaRepository;

import java.util.List;

public class MediaService {

    private final MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    // Veritabanındaki tüm medyaları liste olarak getirir
    public List<Media> getAllFilms() {
        return mediaRepository.getAllFilm();
    }

    // Yeni bir medyayı kaydeder
    public void addMedia(Media media) {
        mediaRepository.saveMedia(media);
    }

    // Medya bilgilerini günceller
    public void updateMedia(Media media) {
        mediaRepository.updateMedia(media);
    }

    // Medyayı siler
    public void deleteMedia(String name) {
        mediaRepository.deleteMedia(name);
    }

    // Medyayı getirir
    public Media getMediaByName(String name) {
        return mediaRepository.getMedia(name);
    }
}