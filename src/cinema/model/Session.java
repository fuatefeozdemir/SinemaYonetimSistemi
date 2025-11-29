package cinema.model;

import cinema.exception.InvalidInputException;
import cinema.model.content.Media;

import java.time.LocalDateTime;

public class Session {

    private final String sessionId; // İsteğe bağlı: seans ID
    private final Hall hall;
    private final Media film; // Film ya da Media nesnesi
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    private final Seat[][] seats; // SEANSA ÖZEL KOLTUK MATRİSİ

    // -----------------------
    // Constructor
    // -----------------------
    public Session(String sessionId, Hall hall, Media film, LocalDateTime startTime, LocalDateTime endTime) {
        if (hall == null || film == null || startTime == null || endTime == null) {
            throw new InvalidInputException("Seans oluşturulurken boş değer girilemez.");
        }

        if (endTime.isBefore(startTime)) {
            throw new InvalidInputException("Seans bitiş saati başlangıç saatinden önce olamaz.");
        }

        this.sessionId = sessionId;
        this.hall = hall;
        this.film = film;
        this.startTime = startTime;
        this.endTime = endTime;

        // Koltuk matrisini oluştur
        seats = new Seat[hall.getRowCount()][hall.getColumnCount()];
        initializeSeats();
    }

    // -----------------------
    // Koltukları oluştur
    // -----------------------
    private void initializeSeats() {
        for (int r = 0; r < hall.getRowCount(); r++) {
            for (int c = 0; c < hall.getColumnCount(); c++) {
                seats[r][c] = new Seat(r, c);
            }
        }
    }

    // -----------------------
    // Koltuk işlemleri
    // -----------------------
    public Seat getSeat(int row, int column) {
        if (row < 0 || row >= hall.getRowCount() || column < 0 || column >= hall.getColumnCount()) {
            throw new InvalidInputException("Koltuk salon sınırlarının dışında!");
        }
        return seats[row][column];
    }

    public Seat getSeat(String seatCode) {
        seatCode = seatCode.toUpperCase().trim();
        char rowLetter = seatCode.charAt(0);
        int row = rowLetter - 'A';
        int column = Integer.parseInt(seatCode.substring(1));
        return getSeat(row, column);
    }

    public void reserveSeat(String seatCode) {
        Seat seat = getSeat(seatCode);
        seat.reserve();
    }

    public void freeSeat(String seatCode) {
        Seat seat = getSeat(seatCode);
        seat.free();
    }

    public int getAvailableSeatsCount() {
        int count = 0;
        for (int r = 0; r < hall.getRowCount(); r++) {
            for (int c = 0; c < hall.getColumnCount(); c++) {
                if (!seats[r][c].isTaken()) count++;
            }
        }
        return count;
    }

    // -----------------------
    // GETTERLAR
    // -----------------------
    public String getSessionId() { return sessionId; }
    public Hall getHall() { return hall; }
    public Media getFilm() { return film; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public Seat[][] getSeats() { return seats; }
}
