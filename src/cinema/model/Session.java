package cinema.model;

import cinema.exception.InvalidInputException;
import cinema.exception.SeatOccupiedException;
import cinema.model.content.Media;

import java.time.LocalDateTime;

public class Session {

    private final String sessionId; // Seans ID'si
    private final Hall hall; // Seans hangi salonda
    private final Media film; // Seanstaki film
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;


    public Session(String sessionId, Hall hall, Media film, LocalDateTime startTime, LocalDateTime endTime) {

        if (endTime.isBefore(startTime)) {
            throw new InvalidInputException("Seans bitiş saati başlangıç saatinden önce olamaz.");
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

    public Hall getHall() {
        return hall;
    }

    public Media getFilm() {
        return film;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}