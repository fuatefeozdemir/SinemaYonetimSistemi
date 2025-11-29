package cinema.exception;

public class SeatOccupiedException extends RuntimeException {
    public SeatOccupiedException(String message) {
        super(message);
    }
}
