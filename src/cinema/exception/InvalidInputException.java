package cinema.exception;

// Geçersiz veri formatı veya aralık dışında değer atanmaya çalışıldığında fırlatılır

public class InvalidInputException extends RuntimeException {

    public InvalidInputException(String message) {
        super(message);
    }
}