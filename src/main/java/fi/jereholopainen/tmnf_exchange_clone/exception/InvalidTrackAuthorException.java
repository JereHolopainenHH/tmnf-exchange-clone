package fi.jereholopainen.tmnf_exchange_clone.exception;

public class InvalidTrackAuthorException extends RuntimeException {

    public InvalidTrackAuthorException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTrackAuthorException(String message) {
        super(message);
    }
}
