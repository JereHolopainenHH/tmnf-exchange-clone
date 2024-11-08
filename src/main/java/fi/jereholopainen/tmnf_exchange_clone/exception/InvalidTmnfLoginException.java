package fi.jereholopainen.tmnf_exchange_clone.exception;

public class InvalidTmnfLoginException extends RuntimeException {

    public InvalidTmnfLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTmnfLoginException(String message) {
        super(message);
    }
}
