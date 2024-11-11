package fi.jereholopainen.tmnf_exchange_clone.exception;

public class UsernameTakenException extends RuntimeException {

    public UsernameTakenException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsernameTakenException(String message) {
        super(message);
    }
}