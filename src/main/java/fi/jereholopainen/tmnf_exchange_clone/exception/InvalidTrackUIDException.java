package fi.jereholopainen.tmnf_exchange_clone.exception;

public class InvalidTrackUIDException extends BadRequestException {
    public InvalidTrackUIDException(String message) {
        super(message);
    }

}
