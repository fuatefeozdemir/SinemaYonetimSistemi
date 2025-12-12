package cinema.exception;

// Giriş başarısız olduğunda fırlatılır

public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }
}