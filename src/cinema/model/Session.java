package cinema.model;

import cinema.exception.InvalidInputException;
import cinema.model.content.Media;
import java.time.LocalDateTime;

public class Session {

    // Seansa ait alanlar
    private final String sessionId;
    private final Media film;
    private final Hall hall;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public Session(String sessionId, Hall hall, Media film, LocalDateTime startTime, LocalDateTime endTime) {

        // Tarih kontrolü
        if (endTime.isBefore(startTime)) {
            throw new InvalidInputException("Hata: Seans bitiş saati başlangıçtan önce olamaz.");
        }

        this.sessionId = sessionId;
        this.hall = hall;
        this.film = film;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Media getFilm() {
        return film;
    }

    public Media getMedia() {
        return film;
    }

    public Hall getHall() {
        return hall;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}