package cinema.exception;

// Dolu olan koltuk doldurulmaya çalışıldığında fırlatılır

public class SeatOccupiedException extends RuntimeException {

    public SeatOccupiedException(String message) {
        super(message);
    }
}
