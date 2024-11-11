package fi.jereholopainen.tmnf_exchange_clone.exception;

public class TmnfLoginTakenException extends BadRequestException {

    public TmnfLoginTakenException(String message, Throwable cause) {
        super(message, cause);
    }

    public TmnfLoginTakenException(String message) {
        super(message);
    }
}
