package fi.jereholopainen.tmnf_exchange_clone.exception;

public class InvalidTrackAuthorException extends BadRequestException {
    public InvalidTrackAuthorException(String message) {
        super(message);
    }

}
