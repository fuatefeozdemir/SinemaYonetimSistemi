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

//    private boolean[][] seats;

    public Session(String sessionId, Hall hall, Media film, LocalDateTime startTime, LocalDateTime endTime) {
//        if (hall == null || film == null || startTime == null || endTime == null) {
//            throw new InvalidInputException("Seans oluşturulurken boş değer girilemez.");
//        }

        if (endTime.isBefore(startTime)) {
            throw new InvalidInputException("Seans bitiş saati başlangıç saatinden önce olamaz.");
        }

        this.sessionId = sessionId;
        this.hall = hall;
        this.film = film;
        this.startTime = startTime;
        this.endTime = endTime;

        // Salon kapasitesine göre matrisi başlat (Default hepsi false yani boş)
//        this.seats = new boolean[hall.getRowCount()][hall.getColumnCount()];
    }

    // --- KOLTUK YÖNETİM METODLARI ---

//    public void reserveSeat(String seatCode) {
//        int[] coords = decodeSeatCode(seatCode);
//        int r = coords[0];
//        int c = coords[1];
//
//        if (seats[r][c]) {
//            throw new SeatOccupiedException(seatCode + " koltuğu zaten dolu!");
//        }
//        seats[r][c] = true;
//    }

//    public void freeSeat(String seatCode) {
//        int[] coords = decodeSeatCode(seatCode);
//        seats[coords[0]][coords[1]] = false;
//    }

//    public boolean isSeatTaken(String seatCode) {
//        int[] coords = decodeSeatCode(seatCode);
//        return seats[coords[0]][coords[1]];
//    }

    // "A1" gibi kodları matris indislerine (0,0) çevirir
    private int[] decodeSeatCode(String seatCode) {
        try {
            seatCode = seatCode.toUpperCase().trim();
            int row = seatCode.charAt(0) - 'A';
            int col = Integer.parseInt(seatCode.substring(1)) - 1;

            if (row < 0 || row >= hall.getRowCount() || col < 0 || col >= hall.getColumnCount()) {
                throw new InvalidInputException("Koltuk salon sınırlarının dışında: " + seatCode);
            }
            return new int[]{row, col};
        } catch (Exception e) {
            throw new InvalidInputException("Geçersiz koltuk kodu formatı: " + seatCode);
        }
    }

//    public int getAvailableSeatsCount() {
//        int count = 0;
//        for (boolean[] row : seats) {
//            for (boolean isTaken : row) {
//                if (!isTaken) count++;
//            }
//        }
//        return count;
//    }

    // --- GETTERLAR ---

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
//
//    public boolean[][] getSeats() {
//        return seats;
//    }
}